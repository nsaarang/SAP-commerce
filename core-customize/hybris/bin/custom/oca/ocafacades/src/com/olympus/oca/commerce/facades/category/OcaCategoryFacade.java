package com.olympus.oca.commerce.facades.category;

import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.List;

public interface OcaCategoryFacade {

    /**
     * Returns filtered category hierarchy till 3 levels
     * Returns:
     * the List of CategoryHierarchyList till 3 levels
     **/
    List<CategoryHierarchyData> getFilteredCategory();

}