/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.facades.populators;

import com.olympus.oca.commerce.facades.util.OcaCommerceUtils;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


public class OcaOrderHistoryPopulator implements Populator<OrderEntryModel, OrderHistoryData>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private static final Logger LOGGER = Logger.getLogger(OcaOrderHistoryPopulator.class);

	@Override
	public void populate(final OrderEntryModel source, final OrderHistoryData target) throws ConversionException
	{
		LOGGER.debug("Entering into populator");
		target.setCode(source.getOrder().getCode());
		target.setPurchaseOrderNumber(source.getOrder().getPurchaseOrderNumber());
		target.setErpOrderNumber(source.getOrder().getErpOrderNumber());
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd',' yyyy");
		target.setCreationTime(formatter.format(source.getCreationtime()));
		target.setTotalEntries(source.getOrder().getEntries().size());
		String formattedPrice = OcaCommerceUtils.getFormattedPrice(source.getOrder().getTotalPrice(), source.getOrder().getCurrency());
		target.setTotalPrice(formattedPrice);
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

}
