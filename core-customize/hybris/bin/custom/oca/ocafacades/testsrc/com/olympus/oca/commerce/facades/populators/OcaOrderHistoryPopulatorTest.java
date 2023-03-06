package com.olympus.oca.commerce.facades.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OcaOrderHistoryPopulatorTest {
    @InjectMocks
    private OcaOrderHistoryPopulator populator;

    @Mock
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Mock
    private OrderEntryModel source;


    private OrderHistoryData target= new OrderHistoryData();

    @Mock
    private OrderEntryData entry;

    @Mock
    private OrderModel orderModel= new OrderModel();


    private static final Logger LOGGER = Logger.getLogger(OcaOrderHistoryPopulator.class);
    @Before
    public void setUp() {
        when(source.getOrder()).thenReturn(orderModel);
        when(orderModel.getCode()).thenReturn("123");
        when(source.getOrder().getPurchaseOrderNumber()).thenReturn("PONUMBER");
        when(source.getOrder().getErpOrderNumber()).thenReturn("ERPNUMBER");
        when(orderEntryConverter.convert(source)).thenReturn(entry);
    }

    @Test
    public void populateWithEntries() {

        populator.populate(source, target);
        assertEquals("123", target.getCode());
        assertEquals("PONUMBER", target.getPurchaseOrderNumber());
        assertEquals("ERPNUMBER", target.getErpOrderNumber());
        assertEquals(1, target.getEntries().size());
        assertEquals(entry, target.getEntries().get(0));

    }

}