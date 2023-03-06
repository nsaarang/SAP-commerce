/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.controllers;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryFiltersWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.RecentlyOrderedProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.olympus.oca.commerce.constants.OcaoccConstants;
import com.olympus.oca.commerce.facades.order.OcaOrderFacade;
import com.olympus.oca.commerce.helper.OcaOrdersHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;




/**
 * The Class OcaOrdersController.
 */
@Controller
@ApiVersion("v2")
@Api(tags = "OCA Orders Controller")
public class OcaOrdersController extends OcaBaseController
{


	/** The order facade. */
	@Resource(name = "orderFacade")
	private OcaOrderFacade orderFacade;

	@Resource(name = "ocaOrdersHelper")
	private OcaOrdersHelper ordersHelper;

	/**
	 * Gets the recently ordered products.
	 *
	 * @param orgUnitId
	 *           the org unit id
	 * @param fields
	 *           the fields
	 * @return the recently ordered products
	 */
	@ApiOperation(nickname = "recentlyOrderProducts", value = "Gets the recently ordered products of a b2bUnit", notes = "Gets the recently ordered products of a b2bUnit")
	@RequestMapping(value = "/{baseSiteId}/users/{userId}/orgUnit/{orgUnitId}/orders/products/recent", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	public RecentlyOrderedProductListWsDTO getRecentlyOrderedProducts(@ApiParam(value = "Organizational unit id.", required = true)
	@PathVariable
	final String orgUnitId, @ApiFieldsParam
	@RequestParam(required = true, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL)
	final String fields)
	{
		return getDataMapper().map(orderFacade.getRecentlyOrderedProducts(orgUnitId), RecentlyOrderedProductListWsDTO.class,
				fields);
	}

	/**
	 * Gets the user order history.
	 * Due to complexity the method is defined as POST
	 * @param currentPage the current page
	 * @param pageSize the page size
	 * @param filters the filters
	 * @param sort the sort
	 * @param fields the fields
	 * @param response the response
	 * @param query the query
	 * @return the user order history
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@RequestMappingOverride(priorityProperty = "ocaocc.OcaOrdersController.orders.priority")
	@RequestMapping(value = OcaoccConstants.OCC_OVERLAPPING_BASE_SITE_USER_PATH + "/orders", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(nickname = "userOrderHistory", value = "Returns order history data for all orders placed by a specified user.", notes = "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the results across multiple pages, if required.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderHistoryListWsDTO getUserOrderHistory(@ApiParam(value = "The current result page requested.")
	@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE)
	final int currentPage, @ApiParam(value = "The number of results returned per page.")
	@RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
	final int pageSize,
			@ApiParam(value = "Request body parameter that contains details such as the order statuses, account number, addressId, sort parameter", required = true)
			@RequestBody
			final OrderHistoryFiltersWsDTO filters, @ApiParam(value = "Sorting method applied to the return results.")
			@RequestParam(required = false)
			final String sort, @ApiFieldsParam
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, final HttpServletResponse response, @RequestParam(required = false)
			final String query)


	{
		final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(filters, currentPage, pageSize, sort,
				addPaginationField(fields), query);
		setTotalCountHeader(response, orderHistoryList.getPagination());
		return orderHistoryList;
	}

	/**
	 * Adds pagination field to the 'fields' parameter
	 *
	 * @param fields
	 * @return fields with pagination
	 */
	protected String addPaginationField(final String fields)
	{
		String fieldsWithPagination = fields;

		if (StringUtils.isNotBlank(fieldsWithPagination))
		{
			fieldsWithPagination += ",";
		}
		fieldsWithPagination += "pagination";

		return fieldsWithPagination;
	}

	protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto)
	{
		if (paginationDto != null && paginationDto.getTotalResults() != null)
		{
			response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
		}
	}


	/**
	 * Gets the order facade.
	 *
	 * @return the orderFacade
	 */
	public OcaOrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	/**
	 * Sets the order facade.
	 *
	 * @param orderFacade
	 *           the orderFacade to set
	 */
	public void setOrderFacade(final OcaOrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}

}
