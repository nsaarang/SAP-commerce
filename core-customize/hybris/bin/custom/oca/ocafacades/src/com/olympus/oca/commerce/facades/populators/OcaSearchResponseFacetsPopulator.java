/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.populators.SearchResponseFacetsPopulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OcaSearchResponseFacetsPopulator extends SearchResponseFacetsPopulator
{

	@Override
	protected SolrSearchQueryData refineQueryRemoveFacet(final SolrSearchQueryData searchQueryData, final String facet,
			final String facetValue)
	{
		List<SolrSearchQueryTermData> newTerms = new ArrayList<>(searchQueryData.getFilterTerms());
		// Remove the term for the specified facet
		final Iterator<SolrSearchQueryTermData> iterator = newTerms.iterator();
		while (iterator.hasNext())
		{
			final SolrSearchQueryTermData term = iterator.next();
			if (facet.equals(term.getKey()) && facetValue.equals(term.getValue()))
			{
				iterator.remove();
			}
		}

		if ("levelTwoCategories".equalsIgnoreCase(facet) || "allCategories".equalsIgnoreCase(facet))
		{
			final SolrSearchQueryTermData newTerm = createSearchQueryTermData();
			newTerm.setKey(facet);
			newTerm.setValue(facetValue);
			newTerms = new ArrayList<>();
			newTerms.add(newTerm);
		}

		// Build the new query data
		final SolrSearchQueryData result = cloneSearchQueryData(searchQueryData);
		result.setFilterTerms(newTerms);
		return result;
	}

	@Override
	protected SolrSearchQueryData refineQueryAddFacet(final SolrSearchQueryData searchQueryData, final String facet,
			final String facetValue)
	{
		final SolrSearchQueryTermData newTerm = createSearchQueryTermData();
		newTerm.setKey(facet);
		newTerm.setValue(facetValue);

		List<SolrSearchQueryTermData> newTerms = new ArrayList<>(searchQueryData.getFilterTerms());
		if ("levelTwoCategories".equalsIgnoreCase(facet) || "allCategories".equalsIgnoreCase(facet))
		{
			newTerms = new ArrayList<>();
		}
		newTerms.add(newTerm);

		// Build the new query data
		final SolrSearchQueryData result = cloneSearchQueryData(searchQueryData);
		result.setFilterTerms(newTerms);
		return result;
	}

}
