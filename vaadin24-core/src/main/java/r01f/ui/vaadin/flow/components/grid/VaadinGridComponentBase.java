package r01f.ui.vaadin.flow.components.grid;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

import lombok.extern.slf4j.Slf4j;
import r01f.locale.I18NKey;
import r01f.patterns.Factory;
import r01f.ui.i18n.UII18NService;
import r01f.ui.presenter.UIPresenterSubscriber;
import r01f.ui.vaadin.flow.components.form.VaadinDetailEditForm;
import r01f.ui.vaadin.flow.components.menu.VaadinMenus;
import r01f.ui.vaadin.flow.databinding.VaadinListDataProviders;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;
import r01f.ui.viewobject.UIViewObject;
import r01f.util.types.collections.CollectionUtils;

/**
 * Creates a grid component like:
 * <pre>
 * 		+=======================================+
 * 		| Object title  [ADD]				    |
 *	    |									    |
 *	    | +-----------------------------------+ |
 *	    | |		      |		     |			  | |
 *	    | +-----------|----------|------------+ |
 *	    | |		      |		     |		      | |
 *	    | |		      |		     |			  | |
 *	    | |		      |		     |			  | |
 *	    | +-----------------------------------+ |
 *	    +=======================================+
 * </pre>
 * @param <V>
 * @param <W>
 */
@Slf4j
public abstract class VaadinGridComponentBase<V extends UIViewObject,
											  W extends VaadinDetailEditForm<V>>
			  extends VerticalLayout
		   implements VaadinI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = 1186941723663834774L;
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	protected final W _winDetailEdit;

	protected final Span _lblCaption;
	protected final Button _btnNew = new Button(new Icon(VaadinIcon.PLUS));
	protected final Grid<V> _grid;
	protected final Span _noResultsSpan;
	protected final Binder<V> _gridBinder;
	protected boolean _changed = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinGridComponentBase(final UII18NService i18n,
								   final W winDetailEdit,
								   final Factory<V> viewObjFactory,
								   final I18NKey captionkey) {
		////////// Edit window
		_winDetailEdit = winDetailEdit;

		////////// Title & add button
		_lblCaption = new Span(i18n.getMessage(captionkey).toUpperCase());

		HorizontalLayout hlyoutTitle = new HorizontalLayout(_lblCaption,
															_btnNew);

		////////// No results Span
		_noResultsSpan = new Span(i18n.getMessage("notification.noresults"));

		/////////// Grid
		_grid = new Grid<>();
		// binder
		_gridBinder = _grid.getEditor()
						   .getBinder();
		// common cols
		_configureGrid(i18n);

		// more cols
		_configureGridColumns(i18n);

		// data
		_grid.setItems(Lists.newArrayList());

		////////// add components
		this.add(hlyoutTitle,
				 _grid,
				 _noResultsSpan);
		
		////////// behavior
		_setBehavior(viewObjFactory);
	}
	protected void _configureGrid(final UII18NService i18n) {
		////////// style
		_grid.setSizeFull();

		////////// grid columns
		// add menu column
		_grid.addComponentColumn(viewObj -> new R01UIGridMenu(i18n,
															  viewObj))
				.setFlexGrow(0)		// exactly as wide as its contents requires
				.setResizable(false);
	}
	/**
	 * Adds the contact mean specific columns
	 * @param i18n
	 */
	protected abstract void _configureGridColumns(final UII18NService i18n);
	
	private void _setBehavior(final Factory<V> viewObjFactory) {
		_btnNew.addClickListener(clickEvent -> {
									// open the [edit] window in ADDITION MODE
									UIPresenterSubscriber<V> subscriber = _afterAddSubscriber(_winDetailEdit);
									_winDetailEdit.forCreating(subscriber);		// what to do after save
									Dialog dialog = (Dialog)_winDetailEdit;
									dialog.open();
							  	});
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ENTRY POINT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the list items
	 * @param items
	 */
	public void setItems(final Collection<V> items) {
		Collection<V> theItems = items != null ? items
											   : Lists.newArrayList();
		_noResultsSpan.setVisible(CollectionUtils.isNullOrEmpty(theItems));
		_grid.setVisible(CollectionUtils.hasData(theItems));
		VaadinListDataProviders.collectionBackedOf(_grid)
							   .setItems(theItems);
	}
	/**
	 * @return the item list
	 */
	public Collection<V> getItems() {
		return VaadinListDataProviders.collectionBackedOf(_grid)
									  .getUnderlyingItemsCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	CAPTION
/////////////////////////////////////////////////////////////////////////////////////////
	public void setCaption(final String caption) {
		_lblCaption.setText(caption);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	MENU
/////////////////////////////////////////////////////////////////////////////////////////
	protected class R01UIGridMenu
			extends MenuBar {

		private static final long serialVersionUID = -8624200824370173655L;

		protected R01UIGridMenu(final UII18NService i18n,
								final V viewObj) {
			MenuItem mnuItemRoot = VaadinMenus.createMenuItemWithIcon(this,
																	  new Icon(VaadinIcon.ELLIPSIS_DOTS_V),
																	  null,		// no label
																	  null);	// no command
			SubMenu subMenuRoot = mnuItemRoot.getSubMenu();
			
			// Edit
			MenuItem mnuItemEdit = VaadinMenus.createMenuChildItemWithIcon(subMenuRoot, 
																		   new Icon(VaadinIcon.EDIT),
																		   i18n.getMessage("edit"),
																		   selectedItem -> {
																				   	// open the detail window in EDIT mode
																					UIPresenterSubscriber<V> saveSubscriber = _afterEditSubscriber(_winDetailEdit);
																					UIPresenterSubscriber<V> deleteSubscriber = _afterDeleteSubscriber(_winDetailEdit);
																					_winDetailEdit.forEditing(viewObj,
																											  saveSubscriber,deleteSubscriber);		// what to do after save or delete
																					Dialog dialog = (Dialog)_winDetailEdit;
																					dialog.open();
																		   });
			subMenuRoot.addItem(mnuItemEdit);
			
			// Delete
			@SuppressWarnings("unused")
			MenuItem mnuItemDelete = VaadinMenus.createMenuChildItemWithIcon(subMenuRoot, 
																		   	 new Icon(VaadinIcon.TRASH),
																		   	 i18n.getMessage("delete"),
																		   	 selectedItem -> {
																				VaadinListDataProviders.collectionBackedOf(_grid)
																									   .removeItem(viewObj);
																				_changed = true;
																				_showOrHideNoResultsMessage();
																		   	 });
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	SUBSCRIBERS
/////////////////////////////////////////////////////////////////////////////////////////
	private UIPresenterSubscriber<V> _afterEditSubscriber(final W win) {
		return UIPresenterSubscriber.from(
					// on success
					viewObj -> {
						// Refresh the edited item
						VaadinListDataProviders.collectionBackedOf(_grid)
											   .refreshItem(viewObj);
						_afterAddOrEdit(viewObj);
						_changed = true;
						// close the window
						win.close();
					},
					// on error
					th -> {
						// TODO do something with the error
						log.error("Error: {}",
								  th.getMessage(),th);
					});
	}
	private UIPresenterSubscriber<V> _afterAddSubscriber(final W window) {
		return UIPresenterSubscriber.from(
					// on success
					viewObj -> {
						// Add the new item to the list
						VaadinListDataProviders.collectionBackedOf(_grid)
											   .addNewItem(viewObj);

						_afterAddOrEdit(viewObj);
						_changed = true;
						_showOrHideNoResultsMessage();
						// close the delete confirm view
						window.close();
					},
					// on error
					th -> {
						// TODO do something with the error
						log.error("Error: {}",
								  th.getMessage(),th);
					});
	}
	private UIPresenterSubscriber<V> _afterDeleteSubscriber(final W window) {
		return UIPresenterSubscriber.from(
					// on success
					obj -> {
						// Refresh the edited item
						VaadinListDataProviders.collectionBackedOf(_grid)
											   .removeItem(obj);
						_showOrHideNoResultsMessage();
						_changed = true;
						// close the window
						window.close();
					},
					// on error
					th -> {
						// TODO do something with the error
						log.error("Error: {}",
								  th.getMessage(),th);
					});
	}
	private void _showOrHideNoResultsMessage() {
		if (VaadinListDataProviders.collectionBackedOf(_grid)
								   .getUnderlyingItemsCollectionSize() > 0) {
			_noResultsSpan.setVisible(false);
			_grid.setVisible(true);
		} else {
			_noResultsSpan.setVisible(true);
			_grid.setVisible(false);
		}
	}
	/**
	 * Called just after an item is added to the grid or edited
	 * This hook can be used to process all visible grid items
	 * and change something
	 * @param viewObj
	 */
	protected abstract void _afterAddOrEdit(final V viewObj);
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		// TODO Auto-generated method stub
	}
}
