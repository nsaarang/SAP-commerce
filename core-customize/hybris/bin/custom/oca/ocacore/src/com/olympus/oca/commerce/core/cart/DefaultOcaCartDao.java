/**
 *
 */
package com.olympus.oca.commerce.core.cart;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.olympus.oca.commerce.core.cart.dao.OcaCartDao;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;


/**
 * The Class DefaultOcaCartDao.
 */
public class DefaultOcaCartDao implements OcaCartDao
{

	/** The Constant SHIPPING_CARRIER_QUERY. */
	protected static final String SHIPPING_CARRIER_QUERY = "SELECT {" + ShippingCarrierModel.PK + "} FROM {"
			+ ShippingCarrierModel._TYPECODE + "} WHERE {" + ShippingCarrierModel.CODE + "}  != ?shipByOlympusCode";

	/** The Constant SHIPPING_CARRIER_CODE_QUERY. */
	protected static final String SHIPPING_CARRIER_CODE_QUERY = "SELECT {" + ShippingCarrierModel.PK + "} FROM {"
			+ ShippingCarrierModel._TYPECODE + "} WHERE {" + ShippingCarrierModel.CODE + "} = ?carrierCode";

	/** The Constant SHIP_BY_OLYMPUS_FALLBACK_CODE. */
	protected static final String SHIP_BY_OLYMPUS_FALLBACK_CODE = "shipByOlympus";

	/** The Constant SHIP_BY_OLYMPUS_QUERY_PARAM. */
	protected static final String SHIP_BY_OLYMPUS_QUERY_PARAM = "shipByOlympusCode";

	/** The Constant CARRIER_CODE_QUERY_PARAM. */
	protected static final String CARRIER_CODE_QUERY_PARAM = "carrierCode";

	/** The configuration service. */
	private ConfigurationService configurationService;

	/** The flexible search service. */
	private FlexibleSearchService flexibleSearchService;
	
	private static final String DELIVERY_OPTIONS = "SELECT {pk} from {DeliveryOption AS deliveryOption JOIN ShippingCarrier AS shippingCarrier ON {deliveryOption.shippingCarrier}={shippingCarrier.pk}} where {shippingCarrier.code}=?shippingCarrierCode";

	/**
	 * Gets the shipping carrier list for third party.
	 *
	 * @return the shipping carrier list for third party
	 */
	@Override
	public List<ShippingCarrierModel> getShippingCarrierListForThirdParty()
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final String shipByOlympusCode = getConfigurationService().getConfiguration()
				.getString(OcaCoreConstants.SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE);
		final StringBuffer query = new StringBuffer(SHIPPING_CARRIER_QUERY);
		params.put(SHIP_BY_OLYMPUS_QUERY_PARAM, shipByOlympusCode);
		final SearchResult<ShippingCarrierModel> res = getFlexibleSearchService().search(query.toString(), params);
		return res.getResult() == null ? Collections.EMPTY_LIST : res.getResult();
	}

	/**
	 * Gets the shipping carrier for code.
	 *
	 * @param carrierCode
	 *                       the carrier code
	 * @return the shipping carrier for code
	 */
	@Override
	public ShippingCarrierModel getShippingCarrierForCode(final String carrierCode)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuffer query = new StringBuffer(SHIPPING_CARRIER_CODE_QUERY);
		params.put(CARRIER_CODE_QUERY_PARAM, carrierCode);
		final SearchResult<ShippingCarrierModel> res = getFlexibleSearchService().search(query.toString(), params);
		return res.getResult() == null ? null : res.getResult().get(0);
	}
	
	
	@Override
	public List<DeliveryOptionModel> getDeliveryOptions(final String shippingCarrierCode)
	{
		final StringBuilder queryString = new StringBuilder(DELIVERY_OPTIONS);
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("shippingCarrierCode", shippingCarrierCode);
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(queryString);
		flexibleSearchQuery.addQueryParameters(parameters);
		final SearchResult<DeliveryOptionModel> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);
		if (null != searchResult && CollectionUtils.isNotEmpty(searchResult.getResult()))
		{
			return searchResult.getResult();
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the configuration service.
	 *
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets the configuration service.
	 *
	 * @param configurationService
	 *                                the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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

}
