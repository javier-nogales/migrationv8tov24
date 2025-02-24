package r01f.ui.vaadin.flow.databinding;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataCommunicator;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class VaadinDataComunicators {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings({ "rawtypes","unchecked" })
	public static <T> Collection<T> allItemsOf(final Grid<T> grid) {
		// fetch all
		if (grid.getDataCommunicator() instanceof HierarchicalDataCommunicator<?>) {
			HierarchicalDataCommunicator<T> hCommunicator = (HierarchicalDataCommunicator<T>)grid.getDataCommunicator();
			hCommunicator.fetchFromProvider(0,hCommunicator.getDataProviderSize())
						 .forEach(item -> {
										if (hCommunicator.hasChildren(item)) {
											// expand
											hCommunicator.expand(item);
											// recurse!
											_expandRecursively(grid,
															   _childsOf(grid,
																	   	 item),		// parent
															   Integer.MAX_VALUE);
										}
								  });
		}
		Query qry = grid.getDataCommunicator()
					    .buildQuery(0,grid.getDataCommunicator().getDataProviderSize());
		
		return (Collection<T>)grid.getDataProvider()
								  .fetch(qry)
								  .collect(Collectors.toList());
	}
	protected static <T> void _expandRecursively(final Grid<T> grid,
												 final Stream<T> items,
								  	  			 final int depth) {
		if (depth < 0) return;
		HierarchicalDataCommunicator<T> dataCommunicator = (HierarchicalDataCommunicator<T>)grid.getDataCommunicator();
		items.forEach(item -> {
							if (dataCommunicator.hasChildren(item)) {
								// expand
								dataCommunicator.expand(item);
								// recurse!
								_expandRecursively(grid,
												   _childsOf(grid,
															 item),		// parent
												   depth - 1);
							}
					  });
	}
	private static <T> Stream<T> _childsOf(final Grid<T> grid,
										   final T item) {
		HierarchicalDataProvider<T,?> hierarchicalDataProvider = (HierarchicalDataProvider<T,?>)grid.getDataProvider();
		return hierarchicalDataProvider.fetchChildren(new HierarchicalQuery<>(null,		// filter
																			  item));	// parent
	}
}
