package com.olympus.oca.commerce.controllers;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.olympus.oca.commerce.dto.user.CategoryHierarchyDataList;
import com.olympus.oca.commerce.dto.user.CategoryHierarchyListWsDTO;
import com.olympus.oca.commerce.facades.category.impl.DefaultOcaCategoryFacade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(tags = "OCA Products")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/ocaProducts")
public class OcaProductsController {
    
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource(name = "ocaCategoryFacade")
    private DefaultOcaCategoryFacade defaultOcaCategoryFacade;

    @RequestMapping(value = "/categoryHierarchy", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(nickname = "getCategoryHierarchy", value = "Gets the category hierarchy structure of categories", notes = "Returns information about the  category hierarchy that exists in a catalog version available for the current base store.")
    @ApiBaseSiteIdParam
    public CategoryHierarchyListWsDTO getCategoryHierarchy(
            @ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields) {
        final CategoryHierarchyDataList categoryHierarchyDataList = new CategoryHierarchyDataList();
        categoryHierarchyDataList.setCategoryHierarchy(defaultOcaCategoryFacade.getFilteredCategory());
        return dataMapper.map(categoryHierarchyDataList, CategoryHierarchyListWsDTO.class, fields);
    }

}