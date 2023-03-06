/**
 *
 */
package com.olympus.oca.commerce.facades.order.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductData;
import de.hybris.platform.ocafacades.order.data.RecentlyOrderedProductListData;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.localization.Localization;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.olympus.oca.commerce.core.order.OcaOrderService;
import com.olympus.oca.commerce.facades.order.OcaOrderFacade;


/**
 * The Class DefaultOcaOrderFacade.
 */
public class DefaultOcaOrderFacade extends DefaultOrderFacade implements OcaOrderFacade
{

	/** The order service. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaOrderFacade.class);

	private OcaOrderService orderService;

	private EnumerationService enumerationService;

	@Resource
	private ConfigurationService configurationService;

	/** The b 2 b unit service. */
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	private Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter;

	private static final String STATUS_MSG_1 = "ocafacades.statusMessage1";
	private static final String STATUS_MSG_2 = "ocafacades.statusMessage2";

	/** The recently ordered product converter. */
	private Converter<ProductModel, RecentlyOrderedProductData> recentlyOrderedProductConverter;
	private Converter<OrderEntryModel, OrderHistoryData> ocaOrderHistoryConverter;

	/**
	 * Gets the recently ordered products.
	 *
	 * @param orgUnitId
	 *                     the org unit id
	 * @return the recently ordered products
	 */
	@Override
	public RecentlyOrderedProductListData getRecentlyOrderedProducts(final String orgUnitId)
	{
		final RecentlyOrderedProductListData recentlyOrderedProductListData = new RecentlyOrderedProductListData();
		recentlyOrderedProductListData.setRecentlyOrderedProducts(
				Converters.convertAll(getOrderService().getRecentlyOrderedProducts((CustomerModel) getUserService().getCurrentUser(),
						getB2bUnitService().getUnitForUid(orgUnitId)), getRecentlyOrderedProductConverter()));
		return recentlyOrderedProductListData;
	}

	@Override
	public SearchPageData<OrderHistoryData> getPagedOrderHistoryForStatuses(final PageableData pageableData,
																			final OrderHistoryFiltersData orderHistoryFiltersData, final String query)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderEntryModel> orderEntryList = getOrderService().getOrderList(currentCustomer, currentBaseStore,
				orderHistoryFiltersData, pageableData, query);
		return convertPageData(orderEntryList, getOcaOrderHistoryConverter());
	}


	@Override
	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());

		final Map<OrderModel, List<OrderEntryModel>> orderEntryMap = new LinkedHashMap<>();
		final List<T> orderHistoryList = new ArrayList();
		for (final S orderEntry : source.getResults())
		{
			final OrderModel order = ((OrderEntryModel) orderEntry).getOrder();
			if (!orderEntryMap.containsKey(order))
			{
				final List<OrderEntryModel> orderEntryList = new ArrayList<>();
				orderEntryList.add((OrderEntryModel) orderEntry);
				orderEntryMap.put(order, orderEntryList);
			}
			else
			{
				orderEntryMap.get(order).add((OrderEntryModel) orderEntry);
			}
		}
		for (final Entry<OrderModel, List<OrderEntryModel>> mapEntry : orderEntryMap.entrySet())
		{
			final OrderHistoryData orderHistoryData = ocaOrderHistoryConverter.convert(mapEntry.getValue().get(0));
			populateOrderHistoryData(mapEntry.getKey(), orderHistoryData, mapEntry.getValue());
			orderHistoryList.add((T) orderHistoryData);
		}
		result.setResults(orderHistoryList);
		return result;
	}

	/**
	 * @param key
	 * @param orderHistoryData
	 * @param value
	 */
	private void populateOrderHistoryData(final OrderModel source, final OrderHistoryData target,
										  final List<OrderEntryModel> entries)
	{
		final List<ConsignmentData> consignments = new ArrayList<>();
		for (final ConsignmentModel consignmentModel : source.getConsignments())
		{
			populateConsignments(consignmentModel, entries, consignments);
		}
		target.setConsignments(consignments);
		populateUnconsignedEntries(source, entries, target);
		groupConsignmentEntries(target);
	}

	/**
	 * @param target
	 */
	private void groupConsignmentEntries(final OrderHistoryData target)
	{
		final Map<ConsignmentData, Map<String, ConsignmentEntryData>> groupedConsignmentsMap = new LinkedHashMap<>();
		for (final ConsignmentData consignment : target.getConsignments())
		{
			final List<ConsignmentEntryData> originalConsignmentEntries = consignment.getEntries();
			if (CollectionUtils.isNotEmpty(originalConsignmentEntries) && originalConsignmentEntries.size() > 1)
			{
				Collections.sort(originalConsignmentEntries, (consignmentEntry1, consignmentEntry2) -> (consignmentEntry1
						.getOrderEntry().getProduct().getName().compareTo(consignmentEntry2.getOrderEntry().getProduct().getName())));
			}
			for (final ConsignmentEntryData consignmentEntry : originalConsignmentEntries)
			{
				final String baseProduct = consignmentEntry.getOrderEntry().getProduct().getBaseProduct();
				if (groupedConsignmentsMap.keySet().contains(consignment))
				{
					final Map<String, ConsignmentEntryData> consignmentEntryMap = groupedConsignmentsMap.get(consignment);
					if (consignmentEntryMap.keySet().contains(baseProduct))
					{
						final ConsignmentEntryData existingConsignmentEntry = consignmentEntryMap.get(baseProduct);
						if (CollectionUtils.isEmpty(existingConsignmentEntry.getOtherVariants()))
						{
							final List<ConsignmentEntryData> consignmentEntries = new ArrayList<>();
							existingConsignmentEntry.setOtherVariants(consignmentEntries);
						}
						existingConsignmentEntry.getOtherVariants().add(consignmentEntry);
					}
					else
					{
						consignmentEntryMap.put(baseProduct, consignmentEntry);
						groupedConsignmentsMap.put(consignment, consignmentEntryMap);
					}
				}
				else
				{
					final Map<String, ConsignmentEntryData> newConsignmentEntryMap = new LinkedHashMap<>();
					newConsignmentEntryMap.put(baseProduct, consignmentEntry);
					groupedConsignmentsMap.put(consignment, newConsignmentEntryMap);
				}
			}
			consignment.setEntries(new ArrayList(groupedConsignmentsMap.get(consignment).values()));
		}

	}

	protected void populateUnconsignedEntries(final OrderModel source, final List<OrderEntryModel> entries,
											  final OrderHistoryData target)
	{
		for (final ConsignmentModel consignmentModel : source.getConsignments())
		{
			if (CollectionUtils.isNotEmpty(consignmentModel.getConsignmentEntries()))
			{
				for (final ConsignmentEntryModel consignmentEntryModel : consignmentModel.getConsignmentEntries())
				{
					entries.remove(consignmentEntryModel.getOrderEntry());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(entries))
		{
			target.setEntries(Converters.convertAll(entries, getOrderEntryConverter()));
			groupUnconsignedEntries(target);
		}
	}

	void groupUnconsignedEntries(final OrderHistoryData target)
	{
		final List<OrderEntryData> entries = target.getEntries();
		final Map<String, OrderEntryData> groupedOrderEntriesMap = new LinkedHashMap<>();
		Collections.sort(entries, (entry1, entry2) -> (entry1.getProduct().getName().compareTo(entry2.getProduct().getName())));
		entries.stream().forEach(entry -> {
			if (groupedOrderEntriesMap.keySet().contains(entry.getProduct().getBaseProduct()))
			{
				final OrderEntryData existingEntry = groupedOrderEntriesMap.get(entry.getProduct().getBaseProduct());
				if (CollectionUtils.isEmpty(entry.getOtherVariants()))
				{
					final List<OrderEntryData> otherVariants = new ArrayList();
					existingEntry.setOtherVariants(otherVariants);
				}
				existingEntry.getOtherVariants().add(entry);
			}
			else
			{
				groupedOrderEntriesMap.put(entry.getProduct().getBaseProduct(), entry);
			}
		});
		target.setEntries(new ArrayList(groupedOrderEntriesMap.values()));
	}

	public void populateConsignments(final ConsignmentModel source, final List<OrderEntryModel> entries,
			final List<ConsignmentData> consignments)
	{
		final List<ConsignmentEntryModel> consignmentEntryList = source.getConsignmentEntries().stream()
				.filter(consEntry -> entries.stream().anyMatch(entry -> entry.equals(consEntry.getOrderEntry())))
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(consignmentEntryList))
		{
			final ConsignmentData target = new ConsignmentData();
			target.setCode(source.getCode());
			if(ConsignmentStatus.SHIPPED.equals(source.getStatus())) {
				target.setTrackingID(source.getTrackingID());
			}
			target.setStatus(source.getStatus());
			target.setConsignmentStatus( getEnumerationService().getEnumerationName(source.getStatus()));
			target.setEntries(Converters.convertAll(consignmentEntryList, getConsignmentEntryConverter()));
			if (ConsignmentStatus.SHIPPED.equals(source.getStatus())
					|| ConsignmentStatus.READY_FOR_PICKUP.equals(source.getStatus()))
			{
				target.setStatusDate(source.getShippingDate());
			}
			if(!ConsignmentStatus.INVOICED.equals(source.getStatus())
					&& !ConsignmentStatus.CANCELLED.equals(source.getStatus()) &&  !ConsignmentStatus.SHIPPED.equals(source.getStatus()))
			setStatusMessageAndDisclaimer(source,target);
			consignments.add(target);
		}
	}
	private void setStatusMessageAndDisclaimer(ConsignmentModel source, ConsignmentData target) {
		String statusMessage = "";
		if (Objects.nonNull(source.getShippingDate())) {
			long noOfDays = getDateDifference(source.getShippingDate());
			boolean deliveryIsOnHold = StringUtils.isNotEmpty(source.getDeliveryHold());
			if((Objects.isNull(source.getShippingDate())) && (noOfDays == 0 && deliveryIsOnHold) || (noOfDays > 14 && !deliveryIsOnHold)
					|| (noOfDays <= 14 && deliveryIsOnHold) || (noOfDays > 14 && deliveryIsOnHold)) {
				statusMessage = STATUS_MSG_1;
			} else if((noOfDays <= 14 && !deliveryIsOnHold)) {
				statusMessage = STATUS_MSG_2;
			}
			target.setStatusMessage(getConfigurationMessage(statusMessage));
			try {
				target.setShippingDate(getShippedOnDate(source.getShippingDate()));
			} catch (ParseException e) {
				LOG.error("Could not parse date", e.getMessage());
			}
		}else if(Objects.isNull(source.getShippingDate())){
			statusMessage = STATUS_MSG_1;
			target.setStatusMessage(getConfigurationMessage(statusMessage));
		}
	}
	private String getConfigurationMessage(String messageKey) {
		return Localization.getLocalizedString(messageKey);
	}
	private long getDateDifference(Date shippingDate) {
		Calendar calender = Calendar.getInstance();
		Date date1 = removeTime(shippingDate);
		Date date2 = removeTime(calender.getTime());
		long diff = date1.getTime()-date2.getTime();
		return TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
	}

	public Date removeTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	private String getShippedOnDate(Date shippingDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String strDate = dateFormat.format(shippingDate);
		return strDate;
	}

	/**
	 * Gets the recently ordered product converter.
	 *
	 * @return the recentlyOrderedProductConverter
	 */
	public Converter<ProductModel, RecentlyOrderedProductData> getRecentlyOrderedProductConverter()
	{
		return recentlyOrderedProductConverter;
	}


	/**
	 * Sets the recently ordered product converter.
	 *
	 * @param recentlyOrderedProductConverter
	 *                                           the recentlyOrderedProductConverter to set
	 */
	public void setRecentlyOrderedProductConverter(
			final Converter<ProductModel, RecentlyOrderedProductData> recentlyOrderedProductConverter)
	{
		this.recentlyOrderedProductConverter = recentlyOrderedProductConverter;
	}


	/**
	 * Gets the order service.
	 *
	 * @return the orderService
	 */
	public OcaOrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * Sets the order service.
	 *
	 * @param orderService
	 *                        the orderService to set
	 */
	public void setOrderService(final OcaOrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * Gets the b 2 b unit service.
	 *
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}


	/**
	 * Sets the B 2 b unit service.
	 *
	 * @param b2bUnitService
	 *                          the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the ocaOrderHistoryConverter
	 */
	public Converter<OrderEntryModel, OrderHistoryData> getOcaOrderHistoryConverter()
	{
		return ocaOrderHistoryConverter;
	}

	/**
	 * @param ocaOrderHistoryConverter
	 *                                    the ocaOrderHistoryConverter to set
	 */
	public void setOcaOrderHistoryConverter(final Converter<OrderEntryModel, OrderHistoryData> ocaOrderHistoryConverter)
	{
		this.ocaOrderHistoryConverter = ocaOrderHistoryConverter;
	}

	/**
	 * @return the orderEntryConverter
	 */
	public Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	/**
	 * @param orderEntryConverter
	 *                               the orderEntryConverter to set
	 */
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	/**
	 * @return the consignmentEntryConverter
	 */
	public Converter<ConsignmentEntryModel, ConsignmentEntryData> getConsignmentEntryConverter()
	{
		return consignmentEntryConverter;
	}

	/**
	 * @param consignmentEntryConverter
	 *                                     the consignmentEntryConverter to set
	 */
	public void setConsignmentEntryConverter(
			final Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter)
	{
		this.consignmentEntryConverter = consignmentEntryConverter;
	}

	public EnumerationService getEnumerationService() {
		return enumerationService;
	}

	public void setEnumerationService(EnumerationService enumerationService) {
		this.enumerationService = enumerationService;
	}

}
