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

import java.util.List;


/**
 * The Interface OcaOrderDao.
 */
public interface OcaOrderDao
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
	List<ProductModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit);
	
	/**
     * Gets the order list.
     *
     * @param customerModel           the customer model
     * @param store                   the store
     * @param orderHistoryFiltersData the order history filters data
     * @param unit                    the unit
     * @param pageableData            the pageable data
     * @param query
     * @return the order list
     */
	SearchPageData<OrderEntryModel> getOrderList(CustomerModel customerModel, BaseStoreModel store,
                                                 OrderHistoryFiltersData orderHistoryFiltersData, List<B2BUnitModel> unit, PageableData pageableData, String query);
}
