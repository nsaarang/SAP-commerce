package com.olympus.oca.commerce.helper;


import com.olympus.oca.commerce.facades.order.OcaOrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryFiltersWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OcaOrdersHelper {
        @Resource(name = "ocaOrderFacade")
        private OcaOrderFacade ocaOrderFacade;

        @Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'DTO',#filters,#currentPage,#pageSize,#sort,#fields)")
        public OrderHistoryListWsDTO searchOrderHistory(final OrderHistoryFiltersWsDTO filters, final int currentPage, final int pageSize,
                                                        final String sort, final String fields, final String query)
        {
            final OrderHistoriesData orderHistoriesData = searchOrderHistory(filters, currentPage, pageSize, sort,query);
            return getDataMapper().map(orderHistoriesData, OrderHistoryListWsDTO.class, fields);
        }

        @Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'Data',#filters,#currentPage,#pageSize,#sort)")
        public OrderHistoriesData searchOrderHistory(final OrderHistoryFiltersWsDTO filters, final int currentPage, final int pageSize,
                                                     final String sort,final String query)
        {
            final PageableData pageableData = createPageableData(currentPage, pageSize, sort);

            OrderHistoryFiltersData orderHistoryFiltersData = getDataMapper().map(filters, OrderHistoryFiltersData.class);
            final OrderHistoriesData orderHistoriesData;
                orderHistoriesData = createOrderHistoriesData(
                        ocaOrderFacade.getPagedOrderHistoryForStatuses(pageableData, orderHistoryFiltersData,query));
            return orderHistoriesData;
        }

        protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result)
        {
            final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

            orderHistoriesData.setOrders(result.getResults());
            orderHistoriesData.setSorts(result.getSorts());
            orderHistoriesData.setPagination(result.getPagination());

            return orderHistoriesData;
        }

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    protected PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
    {
        final PageableData pageable = new PageableData();
        pageable.setCurrentPage(currentPage);
        pageable.setPageSize(pageSize);
        pageable.setSort(sort);
        return pageable;
    }

    protected DataMapper getDataMapper()
    {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }
}


