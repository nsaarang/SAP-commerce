package com.olympus.oca.commerce.facades.category.impl;

import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.olympus.oca.commerce.facades.category.OcaCategoryFacade;


public class DefaultOcaCategoryFacade implements OcaCategoryFacade {

    private static final Set<CatalogOption> OPTIONS = EnumSet.of(CatalogOption.CATEGORIES, CatalogOption.SUBCATEGORIES);

    private static final String CATALOG_VERSION = "Online";
    private static final Integer CATEGORY_HIERARCHY_DATA_LEVEL_ONE = 1;
    private static final Integer CATEGORY_HIERARCHY_DATA_LEVEL_TWO = 2;
    private static final Integer CATEGORY_HIERARCHY_DATA_LEVEL_THREE = 3;


    private CatalogFacade catalogFacade;

    /**
     * Filters the categories till sub-level 3
     **/
    @Override
    public List<CategoryHierarchyData> getFilteredCategory() {
        final List<CategoryHierarchyData> categoryHierarchyList = new ArrayList<CategoryHierarchyData>();
        for (final CatalogData data : catalogFacade.getAllProductCatalogsForCurrentSite(OPTIONS)) {
            for (final CatalogVersionData catalogVersionData : data.getCatalogVersions()) {
                if (CATALOG_VERSION.equalsIgnoreCase(catalogVersionData.getId())) {
                    catalogVersionData.getCategoriesHierarchyData().forEach(categoryHierarchyDataLevelOne -> {
                        categoryHierarchyDataLevelOne.setLevel(CATEGORY_HIERARCHY_DATA_LEVEL_ONE);
                        categoryHierarchyDataLevelOne.getSubcategories().forEach(categoryHierarchyDataLevelTwo -> {
                            categoryHierarchyDataLevelTwo.setLevel(CATEGORY_HIERARCHY_DATA_LEVEL_TWO);
                            categoryHierarchyDataLevelTwo.getSubcategories().forEach(categoryHierarchyDataLevelThree -> {
                                categoryHierarchyDataLevelThree.setLevel(CATEGORY_HIERARCHY_DATA_LEVEL_THREE);
                                categoryHierarchyDataLevelThree.setSubcategories(Collections.EMPTY_LIST);
                            });
                        });
                    });

                    categoryHierarchyList.addAll(catalogVersionData.getCategoriesHierarchyData());
                }
            }
        }
        return categoryHierarchyList;
    }


    /**
     * @return the catalogFacade
     */
    public CatalogFacade getCatalogFacade() {
        return catalogFacade;
    }

    /**
     * @param catalogFacade the catalogFacade to set
     */
    public void setCatalogFacade(final CatalogFacade catalogFacade) {
        this.catalogFacade = catalogFacade;
    }
}


