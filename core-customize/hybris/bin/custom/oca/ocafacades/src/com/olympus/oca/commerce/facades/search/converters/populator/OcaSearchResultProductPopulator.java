package com.olympus.oca.commerce.facades.search.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * The Class OcaSearchResultProductPopulator.
 */
public class OcaSearchResultProductPopulator implements Populator<SearchResultValueData, ProductData>
{

	/** The search result product converter. */
	private Converter<SearchResultValueData, ProductData> searchResultProductConverter;

	/**
	 * Populate.
	 *
	 * @param source
	 *                  the source
	 * @param target
	 *                  the target
	 */
	@Override
	public void populate(final SearchResultValueData source, final ProductData target)
	{
		target.setPurchaseEnabled(
				null != source.getValues().get("purchaseEnabled") ? ((Boolean) source.getValues().get("purchaseEnabled")) : false);
		target.setOtherVariants(searchResultProductConverter.convertAll(source.getVariants()));
		target.setBaseProductName((String) source.getValues().get("baseProductName"));
		target.setNonPurchasableDisplayStatus((String)source.getValues().get("nonPurchasableDisplayStatus"));	}

	/**
	 * Gets the search result product converter.
	 *
	 * @return the searchResultProductConverter
	 */
	public Converter<SearchResultValueData, ProductData> getSearchResultProductConverter()
	{
		return searchResultProductConverter;
	}

	/**
	 * Sets the search result product converter.
	 *
	 * @param searchResultProductConverter
	 *                                        the searchResultProductConverter to set
	 */
	public void setSearchResultProductConverter(final Converter<SearchResultValueData, ProductData> searchResultProductConverter)
	{
		this.searchResultProductConverter = searchResultProductConverter;
	}
}
