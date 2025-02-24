package r01f.ui.vaadin.flow.components.paging;

import java.io.Serializable;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.Tooltip;

import r01f.model.search.SearchResults;
import r01f.types.pager.Paging;
import r01f.ui.i18n.UII18NService;
import r01f.ui.vaadin.flow.i18n.VaadinI18NMessagesCanBeUpdated;

/**
 * Creates a paging component like:
 * <pre>
 *       ---------------------------------     ----------------------------------------------------- 
 *       |  show [    \/] results of 243 |     |    |  [ |< ] [ << ]     10 / 20    [ >> ] [ >| ]  | 
 *       ------------------------------- -     ------------------------------------------------ ----
 * </pre>
 */
public class VaadinPagingComponent 
	 extends HorizontalLayout
  implements VaadinI18NMessagesCanBeUpdated {

	private static final long serialVersionUID = -9193405814847745953L;
/////////////////////////////////////////////////////////////////////////////////////////
//  UI FIELDS
/////////////////////////////////////////////////////////////////////////////////////////		
	//  [|<]  [<<]  10 / 20  [>>]  [>|]
	private final Button _btnFirst;
	private final Button _btnPrevious;
	private final Label _lblFrom = new Label("0");
	private final Label _lblTo = new Label("0");
	private final Button _btnNext;
	private final Button _btnLast;
	
	// Show: [      \/] results of 234
	private final Label _lblShowText = new Label("Show:");
	private final Select<Integer> _cmbPageSize;
	private final Label _lblResultsText = new Label("results");
	private final Label _lblTotalItemCount = new Label("0");

	private VaadinPagingListener _pagingListener;
/////////////////////////////////////////////////////////////////////////////////////////
//	STATUS (avoid as much as possible)
/////////////////////////////////////////////////////////////////////////////////////////
	private Paging _paging;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public VaadinPagingComponent() {						
		//    ------------------------------------------------
		//    |  [ |< ] [ << ]     10 / 20    [ >> ] [ >| ]  |
		//    ------------------------------------------------
		_btnFirst = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
		_btnFirst.getTooltip()
				 .withText("Start")
				 .withPosition(Tooltip.TooltipPosition.TOP_START);
		_btnFirst.addClickListener(clickEvent -> {
										 _paging.goToFirstPage();
										 _updateUIStatusFromPaging();
										 _pagingListener.goTo(_paging.getCurrentPageFirstItem(),// goto page first item	
															  _paging.getPageItems());			// and get x items
								   });
		
		_btnPrevious = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
		_btnPrevious.getTooltip()
				 	.withText("Previous")
				 	.withPosition(Tooltip.TooltipPosition.TOP_START);
		_btnPrevious.addClickListener(clickEvent -> {
											_paging.goToPrevPage();
											_updateUIStatusFromPaging();
											_pagingListener.goTo(_paging.getCurrentPageFirstItem(),	// goto page first item
															     _paging.getPageItems());			// and get x items
									  });
		
		_btnNext = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
		_btnNext.getTooltip()
				.withText("Next")
				.withPosition(Tooltip.TooltipPosition.TOP_START);
		_btnNext.addClickListener(clickEvent -> {
										_paging.goToNextPage();
										_updateUIStatusFromPaging();
										_pagingListener.goTo(_paging.getCurrentPageFirstItem(),	// goto page first item
															 _paging.getPageItems());			// and get x items
								  });
		
		_btnLast = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
		_btnLast.getTooltip()
				.withText("Last")
				.withPosition(Tooltip.TooltipPosition.TOP_START);
		_btnLast.addClickListener(clickEvent -> {
										_paging.goToLastPage();
										_updateUIStatusFromPaging();
										_pagingListener.goTo(_paging.getCurrentPageFirstItem(),	// goto page first item
															 _paging.getPageItems());			// and get x items
								 });
		
		// create layout
		HorizontalLayout hlyControls = new HorizontalLayout(_btnFirst,_btnPrevious,				// [0] first  [1] previous
											        		_lblFrom,new Label(" / "),_lblTo,	// [2] from [3] / [4] to
											        		_btnNext,_btnLast);					// [5] next  [6] last

		// style
		_styleComponents(hlyControls.getChildren());
		
		//  --------------------------------------
		//  |  show [    \/] results of 243      |
		//  --------------------------------------
		_cmbPageSize = new Select<>();
		_cmbPageSize.setEmptySelectionAllowed(false);
		_cmbPageSize.setItems(Lists.newArrayList(10,20,30));
		_cmbPageSize.setValue(10);
		_cmbPageSize.addValueChangeListener(valChangeEvent -> {
												// [1] - Rebuild the paging with the new page size
												int newPageSize = _cmbPageSize.getValue();
												_paging = new Paging(newPageSize,_paging.getNavBarWindowItems(),	// 10 items / page, 5 pages / window
																	 _paging.getItemCount(),						// total items
																	 1);											// current page
												// [2] - Update the ui
												_updateUIStatusFromPaging();
												// [3] - Signal the listener
												_pagingListener.goTo(_paging.getCurrentPage(),	// goto page first item
																 	 _cmbPageSize.getValue());	// and get x items
											});
		
		HorizontalLayout hlyPageSize = new HorizontalLayout(_lblShowText,_cmbPageSize,				// show [   \/]
															_lblResultsText,_lblTotalItemCount);	// results of XX
		// style
		_styleComponents(hlyPageSize.getChildren());
		
		
		//     ---------------------------------     ----------------------------------------------------- 
		//     |  show [    \/] results of 243 |     |    |  [ |< ] [ << ]     10 / 20    [ >> ] [ >| ]  | 
		//     ------------------------------- -     ------------------------------------------------ ----
		this.add(hlyPageSize,hlyControls);
		this.setSizeFull();
	}
	private static void _styleComponents(final Stream<Component> comps) {
		comps.forEach(comp -> {
							if (comp instanceof Label) {	
								Label lbl = (Label)comp;
							} else if (comp instanceof Button) {
								Button btn = (Button)comp;
								btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
							}
					  });
	}
	private void _updateUIStatusFromPaging() {
		_setPageNavigationButtonsStatus();
		_lblFrom.setText(Integer.toString(_paging.getCurrentPage()));
		_lblTo.setText(Integer.toString(_paging.getPageCount()));		
	}
	private void _setPageNavigationButtonsStatus() {
		// enable / disable buttons
		_btnFirst.setEnabled(_paging.hasPrevPage());
		_btnPrevious.setEnabled(_paging.hasPrevPage());
		
		_btnLast.setEnabled(_paging.hasNextPage());
		_btnNext.setEnabled(_paging.hasNextPage());
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  INIT
/////////////////////////////////////////////////////////////////////////////////////////
	public void init(final int newPageSize,
    			  	 final int currPage,
    			  	 final int numItems) {	
		// Paging		
		Paging paging = new Paging(newPageSize,SearchResults.DEFAULT_NAVBAR_WINDOW_SIZE,	// page size & pages window size
							 	   numItems, 	// total items count
							 	   1); 			// start with the first page
		paging.goToPage(currPage);
		this.init(paging);
	}
	public void init(final Paging paging) {
		_paging = paging;
		
		// Set the number of items / page
		_lblFrom.setText(Integer.toString(_paging.getCurrentPage()));
		_lblTo.setText(Integer.toString(_paging.getPageCount()));		
		_lblTotalItemCount.setText(Integer.toString(_paging.getItemCount()));
				
		// enable / disable buttons
		_setPageNavigationButtonsStatus();

	}
/////////////////////////////////////////////////////////////////////////////////////////
//	LISTENER                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	public void addPagingListener(final VaadinPagingListener listener) {
		_pagingListener = listener;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	                                                                          
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public void updateI18NMessages(final UII18NService i18n) {
		_btnFirst.getTooltip().setText(i18n.getMessage("common.paging.first"));
		_btnPrevious.getTooltip().setText(i18n.getMessage("common.paging.prev"));
		_btnNext.getTooltip().setText(i18n.getMessage("common.paging.next"));
		_btnLast.getTooltip().setText(i18n.getMessage("common.paging.last"));
		
		_lblShowText.setText(i18n.getMessage("common.paging.show"));
		_lblResultsText.setText(i18n.getMessage("common.paging.items"));		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////
// 	LISTENER  
/////////////////////////////////////////////////////////////////////////////////////////   	
	public interface VaadinPagingListener 
	     	 extends Serializable {	
		void goTo(int firstItemNum,
				  int numItems);    
	}	
}