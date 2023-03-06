/**
 *
 */
package com.olympus.oca.commerce.core.order;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Set;


/**
 * The Interface OcaOrderService.
 */
public interface OcaOrderService
{

	/**
	 * Gets the recently ordered products.
	 *
	 * @param customer
	 *                    the customer
	 * @param unit
	 *                    the unit
	 * @return the recently ordered products
	 */
	public Set<ProductModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit);
	
	/**
	 * Gets the order list.
	 *
	 * @param customerModel           the customer model
	 * @param store                   the store
	 * @param orderHistoryFiltersData the order history filters data
	 * @param pageableData            the pageable data
	 * @param purchaseOrderNumber
	 * @return the order list
	 */
	SearchPageData<OrderEntryModel> getOrderList(CustomerModel customerModel, BaseStoreModel store,
												 OrderHistoryFiltersData orderHistoryFiltersData, PageableData pageableData, String query);
}
