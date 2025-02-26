package r01f.ui.vaadin.flow.databinding;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.HasListDataView;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.function.ValueProvider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.facets.HasOID;
import r01f.guids.OID;
import r01f.util.types.collections.CollectionUtils;

/**
 * Utils about vaadin {@link Grid}
 * see: https://vaadin.com/docs/-/part/framework/datamodel/datamodel-providers.html#datamodel.dataproviders
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinListDataProviders {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Provides access to a {@link Collection}-backed {@link DataProvider}
	 * <pre class='brush:java'>
	 * 		Collection<T> items = VaadinListDataProviders.collectionBackedOf(grid)
	 * 												 	 .getUnderlyingItemsCollection();
	 * </pre>
	 * @param <T>
	 * @param hasListDataView
	 * @return
	 */
	public static <T> VaadinHasDataListProviderAccessor<T> collectionBackedOf(final HasListDataView<T,?> hasListDataView) {
		return new VaadinListDataProviders() { /* nothing */ }
					.new VaadinHasDataListProviderAccessor<>(hasListDataView);
	}
	/**
	 * Provides access to a {@link DataProvider}
	 * @param <T>
	 * @param dataProvider
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> VaadinListDataProviderAccessor<T> ofListDataProvider(final DataProvider<T,?> dataProvider) {
		return VaadinListDataProviders.ofListDataProvider((ListDataProvider<T>)dataProvider);
	}
	/**
	 * Provides access to a {@link ListDataProvider}
	 * @param <T>
	 * @param dataProvider
	 * @return
	 */
	public static <T> VaadinListDataProviderAccessor<T> ofListDataProvider(final ListDataProvider<T> dataProvider) {
		return new VaadinListDataProviders() { /* nothing */ }
					.new VaadinListDataProviderAccessor<>(dataProvider);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	ACCESSORS
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class VaadinHasDataListProviderAccessor<T>
		 extends VaadinDataProviderAccessorBase<T,
	 											VaadinHasDataListProviderAccessor<T>> {
		
		private final HasListDataView<T,?> _hasListDataView;

		@Override @SuppressWarnings("unchecked")
		public ListDataProvider<T> getDataProvider() {
			return (ListDataProvider<T>)_hasListDataView.getListDataView();
		}
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public class VaadinListDataProviderAccessor<T>
		 extends VaadinDataProviderAccessorBase<T,
		 										VaadinListDataProviderAccessor<T>> {
		
		private final ListDataProvider<T> _dataProvider;

		@Override
		public ListDataProvider<T> getDataProvider() {
			return _dataProvider;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	BASE
/////////////////////////////////////////////////////////////////////////////////////////
	private interface VaadinDataProviderAccessor<T,SELF_TYPE extends VaadinDataProviderAccessor<T,SELF_TYPE>> {
		public ListDataProvider<T> getDataProvider();
		public Collection<T> getUnderlyingItemsCollection();
		public List<T> getUnderlyingItemsCollectionAsList();
		public int getUnderlyingItemsCollectionSize();
		public <O extends OID> T getItemWithOid(final O oid);
		public Collection<T> getItemsMatching(final Predicate<T> pred);
		public SELF_TYPE refreshItem(final T item);
		public <O extends OID> SELF_TYPE refreshItemWithOid(final O oid);
		public SELF_TYPE refreshAll();
		public SELF_TYPE setItems(final Collection<T> newItems);
		public SELF_TYPE addNewItem(final T item);
		public SELF_TYPE addNewItemAt(final T item,final int index);
		public SELF_TYPE addNewItems(final Collection<T> newItems);
		public SELF_TYPE removeItem(final T item);
		public SELF_TYPE removeItemIf(final Predicate<T> pred);
		public SELF_TYPE removeAll();
		public SELF_TYPE replaceItem(final T replacedItem,final T replacingItem);
		public <O extends OID> SELF_TYPE replaceItemWithOid(final O oid,
															final T replacingItem);
		public <V extends Comparable<? super V>> SELF_TYPE setSortOrder(final ValueProvider<T,V> valueProvider,
															   	        final SortDirection sortDirection);
		public int getItemIndex(final T item);
		public SELF_TYPE moveItem(final T item,final int newIndex);
		public SELF_TYPE moveItemUp(final T item);
		public SELF_TYPE moveItemDown(final T item);
		public boolean canMoveItemUp(final T item);
		public boolean canMoveItemDown(final T item);
		public boolean containsItem(final T item);
	}
	private abstract class VaadinDataProviderAccessorBase<T,
														  SELF_TYPE extends VaadinDataProviderAccessorBase<T,SELF_TYPE>> 
	   			implements VaadinDataProviderAccessor<T,SELF_TYPE> {
		@Override
		public Collection<T> getUnderlyingItemsCollection() {
			return this.getDataProvider()
					   .getItems();
		}
		@Override
		public List<T> getUnderlyingItemsCollectionAsList() {
			return (List<T>)this.getUnderlyingItemsCollection();
		}
		@Override
		public int getUnderlyingItemsCollectionSize() {
			Collection<T> col = this.getUnderlyingItemsCollection();
			return col != null ? col.size() : 0;
		}
		@Override @SuppressWarnings("unchecked")
		public <O extends OID> T getItemWithOid(final O oid) {
			// find the item with the given oid
			Collection<T> itemsWithOid = this.getItemsMatching(colItem -> {
																	if (!(colItem instanceof HasOID)) throw new IllegalStateException("Cannot find by oid if the underlying collection items DO NOT implement " + HasOID.class);
																	HasOID<O> hasOid = (HasOID<O>)colItem;
																	return hasOid.getOid().is(oid);
																});
			if (CollectionUtils.hasData(itemsWithOid) && itemsWithOid.size() > 1) throw new IllegalStateException("There exists more than a single item with oid=" + oid + " at the collection!");
			return CollectionUtils.hasData(itemsWithOid) ? CollectionUtils.pickOneAndOnlyElement(itemsWithOid)
														 : null;
		}
		@Override
		public Collection<T> getItemsMatching(final Predicate<T> pred) {
			return this.getUnderlyingItemsCollection()
					   .stream()
					   .filter(pred)
					   .collect(Collectors.toList());
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE refreshItem(final T item) {
			this.getDataProvider()
				.refreshItem(item);
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public <O extends OID> SELF_TYPE refreshItemWithOid(final O oid) {
			// find the item with the given oid
			T item = this.getItemWithOid(oid);
			// refresh the item
			if (item != null) this.refreshItem(item);
			
			return (SELF_TYPE)this; 
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE refreshAll() {
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override
		public SELF_TYPE setItems(final Collection<T> newItems) {
			// add a new item to the underlying collection
			this.getUnderlyingItemsCollection()
				.clear();
			return this.addNewItems(newItems);
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE addNewItem(final T item) {
			// add a new item to the underlying collection
			this.getUnderlyingItemsCollection()
				.add(item);
			// refresh
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE addNewItemAt(final T item,final int index) {
			// add a new item to the underlying collection
			List<T> itemList = this.getUnderlyingItemsCollectionAsList();
			itemList.add(index,item);
			// refresh
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE addNewItems(final Collection<T> newItems) {
			// add a new item to the underlying collection
			this.getUnderlyingItemsCollection()
				.addAll(newItems);
			// refresh
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE removeItem(final T item) {
			this.getUnderlyingItemsCollection()
				.remove(item);
			// refresh 
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE removeItemIf(final Predicate<T> pred) {
			this.getUnderlyingItemsCollection()
			 	.removeIf(pred);
			// refresh
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE removeAll() {
			this.getUnderlyingItemsCollection()
				.clear();
			// refresh 
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE replaceItem(final T replacedItem,final T replacingItem) {
			this.getUnderlyingItemsCollection()
				.remove(replacedItem);
			this.getUnderlyingItemsCollection()
				.add(replacingItem);
			// refresh
			this.getDataProvider()
				.refreshAll();
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public <O extends OID> SELF_TYPE replaceItemWithOid(final O oid,
															final T replacingItem) {
			// find the item with the given oid
			T item = this.getItemWithOid(oid);
			if (item == null) throw new IllegalStateException("item with oid=" + oid + " does NOT exist!");
			// replace
			this.replaceItem(item,replacingItem);
			return (SELF_TYPE)this;
		}
		@Override @SuppressWarnings("unchecked")
		public <V extends Comparable<? super V>> SELF_TYPE setSortOrder(final ValueProvider<T,V> valueProvider,
															   	        final SortDirection sortDirection) {
			this.getDataProvider()
				.setSortOrder(valueProvider,sortDirection);
			return (SELF_TYPE)this;
		}
		@Override 
		public int getItemIndex(final T item) {
			if (item == null) return -1;
			List<T> itemList = this.getUnderlyingItemsCollectionAsList();
			return CollectionUtils.hasData(itemList) ? itemList.indexOf(item) : -1;
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE moveItem(final T item,final int newIndex) {
			int currIndex = this.getItemIndex(item);
			if (currIndex < 0) return (SELF_TYPE)this;		// the item does NOT exists
			return this.removeItem(item)
					   .addNewItemAt(item,newIndex);
		}
		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE moveItemUp(final T item) {
			int currIndex = this.getItemIndex(item);
			if (currIndex < 0 || currIndex == 0) return (SELF_TYPE)this;
			return this.moveItem(item,
								 currIndex - 1);
		}

		@Override @SuppressWarnings("unchecked")
		public SELF_TYPE moveItemDown(final T item) {
			int currIndex = this.getItemIndex(item);
			if (currIndex < 0 || currIndex == this.getUnderlyingItemsCollectionSize() - 1) return (SELF_TYPE)this;
			return this.moveItem(item,
								 currIndex + 1);
		}
		@Override
		public boolean canMoveItemUp(final T item) {
			int itemIndex = this.getItemIndex(item);
			return itemIndex > 0;
		}
		@Override
		public boolean canMoveItemDown(final T item) {
			int itemIndex = this.getItemIndex(item);
			int itemCount = this.getUnderlyingItemsCollectionSize();
			return itemIndex < (itemCount - 1);
		}
		@Override
		public boolean containsItem(final T item) {
			return this.getUnderlyingItemsCollection()
					   .contains(item);
		}
	}
}
