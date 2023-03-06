/**
 *
 */
package com.olympus.oca.commerce.core.b2bUnit.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.olympus.oca.commerce.core.b2bUnit.OcaB2BUnitDao;

/**
 * The Class DefaultOcaB2BUnitDao.
 */
public class DefaultOcaB2BUnitDao implements OcaB2BUnitDao
{
	/** The flexible search service. */
	private FlexibleSearchService flexibleSearchService;
	private static final String B2BUNITMODEL_QUERY = "SELECT {pk} from {B2BUNIT} WHERE {UID} IN (?unit)";


	
	/**
	 * Gets the list of B2BUnitModel from b2bUnits.
	 *
	 * @param b2bUnits the b 2 b units
	 * @return the list of B2BUnitModel from b2bUnits
	 */
	@Override
	public List<B2BUnitModel> getB2BUnitModelFromCode(final List<String> b2bUnits)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		final StringBuffer query = new StringBuffer(B2BUNITMODEL_QUERY);
		params.put("unit", b2bUnits);
		final FlexibleSearchQuery flexiQuery = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<B2BUnitModel> res = getFlexibleSearchService().search(flexiQuery);
		return res.getResult() == null ? Collections.EMPTY_LIST : res.getResult();
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *                                 the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
