/**
 *
 */
package com.olympus.oca.commerce.core.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.olympus.oca.commerce.core.order.OcaOrderDao;


/**
 * The Class DefaultOcaOrderDao.
 */
public class DefaultOcaOrderDao implements OcaOrderDao
{

	/** The Constant RECENTLY_ORDERED_PRODUCTS_QUERY. */
	protected static final String RECENTLY_ORDERED_PRODUCTS_QUERY = "SELECT {entry:" + OrderEntryModel.PRODUCT + "} FROM {"
			+ OrderModel._TYPECODE + " as o join " + OrderEntryModel._TYPECODE + " as entry on {entry:" + OrderEntryModel.ORDER
			+ "}={o:" + OrderModel.PK + "}} where {o:" + OrderModel.USER + "} = ?customer and " + "{o:" + OrderModel.UNIT
			+ "} = ?unit order by {entry:" + OrderEntryModel.CREATIONTIME + " } desc";


	/** The Constant CUSTOMER. */
	protected static final String CUSTOMER = "customer";

	/** The Constant UNIT. */
	protected static final String UNIT = "unit";

	/** The flexible search service. */
	private FlexibleSearchService flexibleSearchService;

	private static final String SELECT_QUERY = "SELECT {entry.pk} from {orderEntry as entry JOIN Order as order on {entry.order}={order.pk}";
	private static final String ADDRESS_QUERY = " JOIN Address as address ON {order.deliveryAddress}={address.pk}";
	private static final String PRODUCT_QUERY = " JOIN Product as products ON {products.pk} = {entry.product}";

	private static final String B2B_UNIT_QUERY = " JOIN B2BUnit as b2bunit ON {order.unit}={b2bunit.pk}";
	private static final String B2B_WHERE_CLAUSE = " AND {b2bunit.pk} IN (?unit)";
	private static final String GENRIC_WHERE_CLAUSE = " AND ({products.code} like ?query OR {products.name} like ?query OR {order.purchaseOrderNumber} like ?query OR {order.erpOrderNumber} like ?query)";
	private static final String STORE_WHERE_CLAUSE = "Where {order.store} = ?store";
	private static final String ADDRESS_WHERE_CLAUSE = " {address.streetName} IN (?addressId)";
	private static final String ORDER_STATUS = " JOIN OrderStatus as orderStatus ON {order.status}={orderStatus.pk}";
	private static final String CREATIONTIME_WHERE_CLAUSE ="AND {order.CREATIONTIME} >= dateadd(MONTH,-18,GETDATE())" ;
	private static final String PARENTHESIS_END = "}";
	private static final String WHERE_BASED_ON_ORDER_STATUS = " {orderStatus.code} IN (?status)";
	private static final String SORY_BY_ORDER_CREATION_DATE_DESC = " ORDER BY {order:" + OrderModel.CREATIONTIME + "} DESC,{entry:"
			+ OrderEntryModel.CREATIONTIME + "} ASC";
	private static final String SORT_BY_ORDER_CREATION_DATE_ASC = " ORDER BY {order:" + OrderModel.CREATIONTIME + "} ASC,{entry:"
			+ OrderEntryModel.CREATIONTIME + "} ASC";
	private static final Object AND = " AND";
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaOrderDao.class);
	private PagedFlexibleSearchService pagedFlexibleSearchService;
	private ConfigurationService configurationService;

	/**
	 * , Gets the recently ordered products.
	 *
	 * @param customer
	 *                    the customer
	 * @param unit
	 *                    the unit
	 * @return the recently ordered products
	 */
	@Override
	public List<ProductModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel units)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuffer query = new StringBuffer(RECENTLY_ORDERED_PRODUCTS_QUERY);
		params.put(CUSTOMER, customer);
		params.put(UNIT, units);
		final FlexibleSearchQuery flexiQuery = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<ProductModel> res = getFlexibleSearchService().search(flexiQuery);
		return res.getResult() == null ? Collections.EMPTY_LIST : res.getResult();
	}


	/**
	 * Gets the order list.
	 *
	 * @param customerModel
	 *                                   the customer model
	 * @param store
	 *                                   the store
	 * @param orderHistoryFiltersData
	 *                                   the order history filters data
	 * @param unit
	 *                                   the unit
	 * @param pageableData
	 *                                   the pageable data
	 * @param query
	 * @return the order list
	 */
	@Override
	public SearchPageData<OrderEntryModel> getOrderList(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderHistoryFiltersData orderHistoryFiltersData, final List<B2BUnitModel> units, final PageableData pageableData,
			final String query)
	{
		validateParameterNotNull(store, "Store must not be null");
		validateParameterNotNull(units, "Unit must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("store", store);

		final StringBuilder selectQuery = new StringBuilder();
		final StringBuilder whereClause = new StringBuilder();
		List<SortQueryData> queries = null;
		selectQuery.append(SELECT_QUERY).append(B2B_UNIT_QUERY);

		if (null != orderHistoryFiltersData.getAddressId())
		{
			queryParams.put("addressId", new ArrayList<String>(Arrays.asList(orderHistoryFiltersData.getAddressId().split(","))));
			selectQuery.append(ADDRESS_QUERY);
			whereClause.append(AND).append(ADDRESS_WHERE_CLAUSE);
		}
		//check status
		if (null != orderHistoryFiltersData.getStatus())
		{
			queryParams.put("status", new ArrayList<String>(Arrays.asList(orderHistoryFiltersData.getStatus().split(","))));
			selectQuery.append(ORDER_STATUS);
			whereClause.append(AND).append(WHERE_BASED_ON_ORDER_STATUS);
		}
		//check unit
		if (CollectionUtils.isNotEmpty(units))
		{
			queryParams.put("unit", units);
		}


		if (null != query)
		{
			queryParams.put("query", "%" + query + "%");
			selectQuery.append(PRODUCT_QUERY);
			whereClause.append(GENRIC_WHERE_CLAUSE);
		}
		selectQuery.append(PARENTHESIS_END);
		selectQuery.append(STORE_WHERE_CLAUSE).append(B2B_WHERE_CLAUSE).append(CREATIONTIME_WHERE_CLAUSE).append(whereClause.toString());
		if (null == units && null == orderHistoryFiltersData.getStatus())
		{
			throw new ModelNotFoundException("Unable to find order history data with given input parameters");
		}
		if (selectQuery.length() != 0)
		{
			queries = getSortQueries(selectQuery.toString());
		}


		return getPagedFlexibleSearchService().search(queries, "recentOrders", queryParams, pageableData);
	}

	/**
	 * @param string
	 * @return
	 */
	private List<SortQueryData> getSortQueries(final String query)
	{
		return Arrays.asList(
				createSortQueryData("recentOrders",
						createQuery(query + getFindOrdersAdditionalFilter(), SORY_BY_ORDER_CREATION_DATE_DESC)),
				createSortQueryData("orderByDate",
						createQuery(query + getFindOrdersAdditionalFilter(), SORT_BY_ORDER_CREATION_DATE_ASC)));
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	protected String createQuery(final String... queryClauses)
	{
		final StringBuilder queryBuilder = new StringBuilder();

		for (final String queryClause : queryClauses)
		{
			queryBuilder.append(queryClause);
		}
		return queryBuilder.toString();
	}

	protected String getFindOrdersAdditionalFilter()
	{
		try
		{
			if (configurationService.getConfiguration().getBoolean(CommerceServicesConstants.FIND_ORDERS_ADDITIONAL_FILTER_ENABLED,
					false))
			{
				return CommerceServicesConstants.FIND_ORDERS_ADDITIONAL_FILTER;
			}
		}
		catch (final ConversionException conversionException)
		{

			throw conversionException;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets the flexible search service.
	 *
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * Sets the flexible search service.
	 *
	 * @param flexibleSearchService
	 *                                 the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the pagedFlexibleSearchService
	 */
	public PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	/**
	 * @param pagedFlexibleSearchService
	 *                                      the pagedFlexibleSearchService to set
	 */
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *                                the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


}
