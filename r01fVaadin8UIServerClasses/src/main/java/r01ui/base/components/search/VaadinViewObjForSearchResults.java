package r01ui.base.components.search;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.model.SummarizedModelObject;
import r01f.model.search.SearchFilter;
import r01f.model.search.SearchResultItem;
import r01f.model.search.SearchResults;
import r01f.ui.viewobject.UIViewObject;
import r01f.ui.viewobject.UIViewObjectWrapped;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class VaadinViewObjForSearchResults<VS extends UIViewObjectWrapped<? extends SummarizedModelObject<?>>>  
  implements UIViewObject {
	
	private static final long serialVersionUID = -858898616165953495L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Total number of results 
	 */
	@Getter private final int _totalItemsCount;
	/**
	 * This results page initial row number
	 */
	@Getter private final int _startPosition;
	/**
	 * This results page final row number
	 */
	@Getter private final int _endPosition;
	/**
	 * The number of requested items (usually the page size)
	 */
	@Getter private final int _requestedNumberOfItems;
	/**
	 * The results
	 */
	@Getter private final Collection<VS> _results;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static <F extends SearchFilter,I extends SearchResultItem,
				   VS extends UIViewObjectWrapped<? extends SummarizedModelObject<?>>> VaadinViewObjForSearchResultsTransformStepBuilder<F,I,VS> from(final SearchResults<F,I> results) {
		return new VaadinViewObjForSearchResultsTransformStepBuilder<>(results);
	}
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class VaadinViewObjForSearchResultsTransformStepBuilder<F extends SearchFilter,I extends SearchResultItem,
																		  VS extends UIViewObjectWrapped<? extends SummarizedModelObject<?>>> {
		private final SearchResults<F,I> _results;
		
		public VaadinViewObjForSearchResults<VS> transformingResultItemsWith(final Function<I,VS> transformer) {
			// get an stream of [view summaries]
			Collection<VS> viewSummaries = _results.getPageItems()
									  	   		   .stream()
									  	   		   .map(transformer)
												   .collect(Collectors.toList());
			// return 
			return new VaadinViewObjForSearchResults<>(_results.getTotalItemsCount(),
													   _results.getStartPosition(),_results.getEndPosition(),
													   _results.getRequestedNumberOfItems(),
													   viewSummaries);
		}
	}
}
