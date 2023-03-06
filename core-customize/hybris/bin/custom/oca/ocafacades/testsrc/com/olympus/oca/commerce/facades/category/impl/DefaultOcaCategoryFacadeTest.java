//package com.olympus.oca.commerce.facades.category.impl;
//
//import de.hybris.platform.cmsfacades.data.CatalogData;
//import de.hybris.platform.cmsfacades.data.CatalogVersionData;
//import de.hybris.platform.commercefacades.catalog.CatalogFacade;
//import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static de.hybris.platform.testframework.Assert.assertEquals;
//
//public class DefaultOcaCategoryFacadeTest {
//    @InjectMocks
//private DefaultOcaCategoryFacade defaultOcaCategoryFacade;
//
//    @Mock
//    private CatalogFacade catalogFacade;
//
//    @Before
//    public void setUp() {
//        List<CatalogData> catalogDataList = new ArrayList<>();
//        List<CatalogVersionData> catalogVersionDataList = new ArrayList<>();
//        List<CategoryHierarchyData> categoryHierarchyDataList = new ArrayList<>();
//
//        CategoryHierarchyData categoryHierarchyDataLevelOne = new CategoryHierarchyData();
//        categoryHierarchyDataLevelOne.setLevel(1);
//        categoryHierarchyDataLevelOne.setSubcategories(Collections.EMPTY_LIST);
//
//        CategoryHierarchyData categoryHierarchyDataLevelTwo = new CategoryHierarchyData();
//        categoryHierarchyDataLevelTwo.setLevel(2);
//        List<CategoryHierarchyData> categoryHierarchyDataListLevelTwo = new ArrayList<>();
//        categoryHierarchyDataListLevelTwo.add(categoryHierarchyDataLevelTwo);
//        categoryHierarchyDataLevelOne.setSubcategories(categoryHierarchyDataListLevelTwo);
//
//        CategoryHierarchyData categoryHierarchyDataLevelThree = new CategoryHierarchyData();
//        categoryHierarchyDataLevelThree.setLevel(3);
//        categoryHierarchyDataLevelThree.setSubcategories(Collections.EMPTY_LIST);
//
//        categoryHierarchyDataListLevelTwo.add(categoryHierarchyDataLevelThree);
//        categoryHierarchyDataList.add(categoryHierarchyDataLevelOne);
//
//        CatalogVersionData catalogVersionData = new CatalogVersionData();
//        catalogVersionData.setId("Online");
//        catalogVersionData.setCategoriesHierarchyData(categoryHierarchyDataList);
//
//        catalogVersionDataList.add(catalogVersionData);
//
//        CatalogData catalogData = new CatalogData();
//        catalogData.setCatalogVersions(catalogVersionDataList);
//        catalogDataList.add(catalogData);
//
//        Mockito.when(catalogFacade.getAllProductCatalogsForCurrentSite(Mockito.anySet())).thenReturn(catalogDataList);
//    }
//
//    @Test
//    public void getFilteredCategoryTest() {
//        List<CategoryHierarchyData> result = defaultOcaCategoryFacade.getFilteredCategory();
//        assertEquals(1, result.size());
//        assertEquals(1, result.get(0).getLevel().intValue());
//        assertEquals(1, result.get(0).getSubcategories().size());
//        assertEquals(2, result.get(0).getSubcategories().get(0).getLevel().intValue());
//        assertEquals(1, result.get(0).getSubcategories().get(0).getSubcategories().size());
//        assertEquals(3, result.get(0).getSubcategories().get(0).getSubcategories().get(0).getLevel().intValue());
//    }
//}
