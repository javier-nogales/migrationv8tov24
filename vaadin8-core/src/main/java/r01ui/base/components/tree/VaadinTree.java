package r01ui.base.components.tree;

import java.util.EventListener;
import java.util.List;

import com.vaadin.data.TreeData;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;
import com.vaadin.ui.dnd.DragSourceExtension;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import r01f.util.types.collections.CollectionUtils;

@Slf4j
@Accessors(prefix="_")
public abstract class VaadinTree<T>
	 		  extends TreeGrid<T>
		   implements VaadinChangingTree {

	private static final long serialVersionUID = 7968729693741406787L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	// The dragged items: set at [GridDragStart] unset at [GridDragEnd]
	@Getter protected TreeData<T> _draggedSubTree;

			protected VaadinTreeChangedEventListener _changedEventListener;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	public VaadinTree() {
		// default no-args constructor
		super();

		////////// DRAG SOURCE
		// set the tree draggable
		final TreeGridDragSource<T> treeDragSource = new TreeGridDragSource<>(this);

		// set allowed effects
		treeDragSource.setEffectAllowed(EffectAllowed.MOVE);

		// add drag start listener
		treeDragSource.addGridDragStartListener(event -> { 	// Keep reference to the dragged sub-tree
													T draggedItem = event.getDraggedItems().get(0);
													_draggedSubTree = _treeData().getSubTreeOf(draggedItem);
													log.debug("...drag subtree:\n{}",
															  _treeDebugInfo(_draggedSubTree));
											 	 });
		// add drag end listener
		treeDragSource.addGridDragEndListener(event -> {	// If drop was successful, remove dragged items from source Grid
													if (event.getDropEffect() == DropEffect.MOVE) {
														this.getDataProvider()
															.refreshAll();
														// Remove reference to dragged items
														_draggedSubTree = null;
													}
										   	   });

		////////// DROP TARGET
		final TreeGridDropTarget<T> treeDropTarget = new TreeGridDropTarget<>(this,
				  															  DropMode.ON_TOP_OR_BETWEEN);
		treeDropTarget.setDropEffect(DropEffect.MOVE);

		// add drop target listener
		treeDropTarget.addTreeGridDropListener(event -> {
				// accepting dragged items from another grid in the same UI
				event.getDragSourceExtension()
					 .ifPresent(source -> {
					 				////////// [0] - guess [dragged sub-tree] and [drop target item]
				 					// set the drop target item
				 					T dropTargetItem = event.getDropTargetRow()
														    .orElse(null);			// maybe the grid is empty (and the dragged item comes from OUTSIDE the grid)
				 					// it the dragged items comes from OUTSIDE the grid (it's NOT an internal item movement)
				 					// the _draggedSubTree = null (if it was an internal movement _draggedSubTree should have been set at [addGridDragStartListener] below)
				 					boolean externalDraggedItem = _draggedSubTree == null;

				 					// if the item comes from OUTSIDE the grid, set the dragged sub-tree
				 					// (remember that if the movement is internal, the dragged sub-tree should have been set at [addGridDragStartListener] below)
				 					if (externalDraggedItem) _draggedSubTree = _draggedSubTreeFrom(source);

				 					// get the dragged item (maybe it has a sub-tree below)
				 					T draggedItem = this.getDraggedItem();

				 					// get parent before move to use after move -> in point [4]
					  				T draggedItemParentItem = null;
					  				if (_treeData().contains(draggedItem)) {
					  					draggedItemParentItem = _treeData().getParent(draggedItem);
					  				}

				 					// do not drag the item in the same item
				 					if (dropTargetItem != null && dropTargetItem.equals(draggedItem)) return;

				 					// special validation
				 					if (!_customIsValid(dropTargetItem,draggedItem,event.getDropLocation())) return;

				 					////////// [1] - If an INTERNAL movement, remove the moved sub-tree
				 					//				 ... it was stored at _draggedSubTree
									// 				 it'll be later added at the correct position
				 					if (!externalDraggedItem) _treeData().removeSubTree(_draggedSubTree);

									////////// [2] - Drop
					  				if (event.getDropLocation() == DropLocation.EMPTY) {
										log.debug("...dropping item={} on an EMPTY tree",
												  _itemCaption(draggedItem));
						  				_treeData().addItems(null,			// root item
						  								     draggedItem);
						  			}
						  			// ... dropping ON TOP: as child of the [drop target item]
					  				else if (event.getDropLocation() == DropLocation.ON_TOP) {
										log.debug("...dropping item={} {} item={}",
												  _itemCaption(draggedItem),
												  event.getDropLocation(),
												  dropTargetItem != null ? _itemCaption(draggedItem) : "null");
					  					// add the [dragged item] as CHILD of the [drop target item]
						  				_treeData().addItems(dropTargetItem,
						  								   draggedItem);
						  			}
						  			// ... dropping ABOVE or BELOW
						  			else {
										// find the parent of the drop target
										T parentItem = _treeData().getParent(dropTargetItem);

										// find the position where the [dragged item] is dropped
										final List<T> itemList = this.getDataCommunicator()
																	 .fetchItemsWithRange(0,this.getDataCommunicator()
																			   					.getDataProviderSize());
										int index = itemList.indexOf(dropTargetItem);
										log.debug("...dropping item={} {} item at {} (drop target item={})",
												  _itemCaption(draggedItem),
												  event.getDropLocation(),
												  index,dropTargetItem != null ? _itemCaption(draggedItem) : "null");
										// add the item as child of the parent (the parent can be null = root item)
										_treeData().addItems(parentItem,
														   draggedItem);
										// move the item depending on the drop location: ABOVE or BELOW
										if (event.getDropLocation() == DropLocation.ABOVE) {
											if (index - 1 > 0) {
												if (itemList.get(index-1).equals(parentItem)) {
													// dropping as the FIRST sibling
													_treeData().moveAfterSibling(draggedItem,
														   					     null);	// first item among siblings
												} else {
													// dropping as NON FIRST sibling
													if (_treeData().getParent(draggedItem) == _treeData().getParent(itemList.get(index-1))) {
														// In case of the same parent for dragged item and target location, move dragged after
														_treeData().moveAfterSibling(draggedItem,
																					 itemList.get(index-1));
													} else {
														// In case of different parents for dragged and target, it means they are not siblings and the target location is in a node/list above.
														// The dragged item will be sent to the end of said upper node/list
														_treeData().removeItem(draggedItem);
														_treeData().addItem(_treeData().getParent(itemList.get(index-1)), draggedItem);
													}
												}
											} else {
												// the first ROOT item of the tree
												_treeData().moveAfterSibling(draggedItem,
																		     null);		// first item among siblings
											}
										} else if (event.getDropLocation() == DropLocation.BELOW) {
											_treeData().moveAfterSibling(draggedItem,
																	     dropTargetItem);
										}
						  			}

					  				////////// [3] - If the [dragged item] is a sub-structure, it has to be moved
					  				_treeData().addSubTreeBelow(draggedItem, 	// the parent of the sub-tree is the original parent!
					  									 	    draggedItem,_draggedSubTree);	// BEWARE!!! the [dragged item] has already been moved
					  																			//			 only the sub-tree below the [dragged item]
					  																			// 			 has to be moved!

					  				////////// [4] - tigger custom action on involved items
					  				_triggerCustomActionsOnInvolvedItemsAtDragAndDrop(draggedItem,
					  													 			  draggedItemParentItem,
					  													 			  dropTargetItem);

					  				////////// [5] - Refresh the tree
					  				this.getDataProvider()
					  					.refreshAll();
					  				if (log.isDebugEnabled()) log.debug("\n[Structure tree]\n{}",
					  													_treeDebugInfo(_treeData()));

					  				////////// [6] - last things...
					  				_draggedSubTree = null;			// BEWARE!!! Remove reference to dragged items

					  				////////// [99] - tell the outside world
					  				_treeDataUpdated();
			 				});
		  });
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM ACTION AT DRAG & DROP
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract void _triggerCustomActionsOnInvolvedItemsAtDragAndDrop(T draggedItem,
																			  T parentItem,
																			  T dropTargetItem);
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void setTreeData(final TreeData<T> treeData) {
		super.setTreeData(treeData);
	}
	@Override
	public TreeData<T> getTreeData() {
		return super.getTreeData();
	}
	@SuppressWarnings("unchecked")
	public <D extends TreeData<T>> D getTreeDataAs(final Class<D> type) {
		return (D)this.getTreeData();
	}
	@SuppressWarnings("unchecked")
	protected VaadinTreeData<T> _treeData() {
		return this.getTreeDataAs(VaadinTreeData.class);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * It the dragged item comes from the OUTSIDE of the grid (another component), this
	 * function returns the dragged item and it's sub-tree
	 * @param source
	 * @return
	 */
	protected TreeData<T> _draggedSubTreeFrom(final DragSourceExtension<? extends AbstractComponent> source) {
		throw new UnsupportedOperationException("drag & drop not supported; extend this type!!");
	}
	/**
	 * Gets the parent of the sub-tree being dragged (the item being dragged)
	 * @return
	 */
	public T getDraggedItem() {
		T draggedItem = null;
		if (CollectionUtils.hasData(_draggedSubTree.getRootItems())
		 && _draggedSubTree.getRootItems().size() == 1) {
			draggedItem = _draggedSubTree.getRootItems().get(0);
		} else {
			throw new UnsupportedOperationException("Cannot drag & drop multiple items!");
		}
		return draggedItem;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	protected abstract String _itemCaption(final T item);
	protected StringBuilder _treeDebugInfo(final TreeData<T> treeData) {
		return new StringBuilder("");
	}
	protected boolean _customIsValid(final T dropTargetItem,final T draggedItem, final DropLocation dropLocation) {
		return true;
	}
	/**
	 * Called when the tree data structure is updated (ie: by drag & drop a tree item)
	 * @param treeData
	 */
	protected void _treeDataUpdated() {
		if (_changedEventListener != null) {
			VaadinTreeChangedEvent event = new VaadinTreeChangedEvent(this);
			_changedEventListener.onTreeChanged(event);
		}
		this.fireEvent(new TreeChangeEvent(this));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	TREE UPDATED EVENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @deprecated
	 * Use addTreeChangeListener instead.
	 * @param listener
	 */
	@Deprecated
	public void setTreeChangedEventListener(final VaadinTreeChangedEventListener listener) {
		_changedEventListener = listener;
	}
	@Accessors(prefix="_")
	public static class VaadinTreeChangedEvent
		 		extends Component.Event {
		private static final long serialVersionUID = -3843906493580325214L;

		public VaadinTreeChangedEvent(final Component srcComponent) {
			super(srcComponent);
		}
	}
	@FunctionalInterface
	public interface VaadinTreeChangedEventListener
	         extends EventListener {

        public abstract void onTreeChanged(final VaadinTreeChangedEvent event);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LISTENERS -> improve over single "_changedEventListener"
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Registration addTreeChangeListener(final TreeChangeListener listener) {
		return this.addListener(TreeChangeEvent.class,
								listener,
								TreeChangeListener.TREE_CHANGE_METHOD);
	}
}
