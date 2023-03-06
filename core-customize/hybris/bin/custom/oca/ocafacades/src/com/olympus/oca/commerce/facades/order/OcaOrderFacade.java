package com.olympus.oca.commerce.facades.order;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductListData;


// TODO: Auto-generated Javadoc
/**
 * The Interface OcaOrderFacade.
 */
public interface OcaOrderFacade extends OrderFacade
{

	/**
	 * Gets the recently ordered products.
	 *
	 * @param orgUnitId
	 *                     the org unit id
	 * @return the recently ordered products
	 */
	RecentlyOrderedProductListData getRecentlyOrderedProducts(String orgUnitId);
	
	/**
	 * Gets the paged order history for statuses.
	 *
	 * @param pageableData            the pageable data
	 * @param orderHistoryFiltersData the order history filters data
	 * @param freeQueryText
	 * @return the paged order history for statuses
	 */
	SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
																	 final OrderHistoryFiltersData orderHistoryFiltersData, String query);
}
