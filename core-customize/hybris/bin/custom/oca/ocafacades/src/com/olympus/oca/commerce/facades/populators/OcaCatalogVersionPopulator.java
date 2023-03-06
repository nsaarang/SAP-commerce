/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CatalogVersionPopulator;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantCategoryModel;

import java.util.ArrayList;
import java.util.Collection;
/**
 * Populates {@link CatalogVersionData} from {@link CatalogVersionModel} using specific {@link CatalogOption}s
 */
public class OcaCatalogVersionPopulator extends CatalogVersionPopulator
{
	@Override
	public void populate(final CatalogVersionModel source, final CatalogVersionData target,
			final Collection<CatalogOption> options) throws ConversionException
	{
		target.setId(source.getVersion());
		target.setLastModified(source.getModifiedtime());
		target.setName(source.getCategorySystemName());
		target.setCategoriesHierarchyData(new ArrayList<CategoryHierarchyData>());
		if (options.contains(CatalogOption.CATEGORIES))
		{
			final Collection<CategoryModel> rootCategories = getCategoryService().getRootCategoriesForCatalogVersion(source);
			for (final CategoryModel category : rootCategories)
			{
				if (!(category instanceof VariantCategoryModel))
				{
					final String catUrl = target.getUrl() + getCategoriesUrl();
					final CategoryHierarchyData categoryData = new CategoryHierarchyData();
					categoryData.setUrl(catUrl);
					getCategoryHierarchyPopulator().populate(category, categoryData, options, PageOption.createWithoutLimits());
					target.getCategoriesHierarchyData().add(categoryData);
				}
			}
		}
	}

}
