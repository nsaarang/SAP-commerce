package com.olympus.oca.commerce.facades.cart.impl;

import static org.mockito.ArgumentMatchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.data.DeliveryOptionData;
import de.hybris.platform.commercefacades.product.data.PurchaseOrderData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierData;
import de.hybris.platform.ocafacades.shipping.data.ShippingCarrierListData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.olympus.oca.commerce.core.cart.OcaCartService;
import com.olympus.oca.commerce.core.model.DeliveryOptionModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierAccountModel;
import com.olympus.oca.commerce.core.model.ShippingCarrierModel;


/**
 * @author nsaumya
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaCartFacadeTest
{
	public static final String SHAUN_MURPHY = "Shaun Murphy";
	@InjectMocks
	private DefaultOcaCartFacade defaultOcaCartFacade;
	@Mock
	private CartService cartService;
	@Mock
	private CartModel cartModel;
	@Mock
	private UserModel userModel;
	@Mock
	private DeliveryOptionModel deliveryOptionModel;
	@Mock
	private DeliveryOptionModel deliveryOptionModel1;
	@Mock
	private DeliveryOptionData deliveryOptionData;
	@Mock
	private DeliveryOptionData deliveryOptionData1;
	@Mock
	private Converter<DeliveryOptionModel, DeliveryOptionData> deliveryOptionConverter;
	@Mock
	private OcaCartService ocaCartService;
	@Mock
	private Converter<CartModel, CartData> cartConverter;
	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private Converter<ShippingCarrierModel, ShippingCarrierData> shippingCarrierConverter;
	@Mock
	private ShippingCarrierAccountModel shippingCarrierAccountModel;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ShippingCarrierModel shippingCarrierModel;
	@Mock
	ShippingCarrierData shippingCarrier;
	@Mock
	ShippingCarrierData carrierData;
	@Mock
	private ShippingCarrierModel shippingCarrierMode;
	@Mock
	private ShippingCarrierModel carrierModel;
	@Mock
	private Configuration configuration;
	private static final String SHIP_BY_OLYMPUS_FALLBACK_CODE = "shipByOlympus";
	private static final String SHIP_BY_OLYMPUS_CODE = "ship.by.olympus.code";
	private static final String SHIP_BY_OLYMPUS = "shipByOlympus";

	public static final String MINICART_MAX_DISPLAY_VALUE="minicart.max.display.value";

	public static final String PLUS_SYMBOL="+";

	private static Integer maxValue=999;
	CartData miniCartData=new CartData();
	@Mock
	DefaultCartFacade defaultCartFacade;
	@Mock
	private Converter<CartModel, CartData> miniCartConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultOcaCartFacade.setCartService(cartService);
		defaultOcaCartFacade.setDeliveryOptionConverter(deliveryOptionConverter);
		defaultOcaCartFacade.setOcaCartService(ocaCartService);
		defaultOcaCartFacade.setB2bUnitService(b2bUnitService);
		defaultOcaCartFacade.setShippingCarrierConverter(shippingCarrierConverter);
		defaultOcaCartFacade.setConfigurationService(configurationService);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		//defaultCartFacade.setMiniCartConverter(miniCartConverter);

	}

	@Test
	public void testAutoGeneratePONumber()
	{
		Mockito.when(defaultOcaCartFacade.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(cartModel.getUser()).thenReturn(userModel);
		Mockito.when(userModel.getName()).thenReturn(SHAUN_MURPHY);
		final String date = new SimpleDateFormat("yyMMddHHmm").format(new Date());
		final String poNumber = "1010512SM" + date;
		final PurchaseOrderData purchaseOrderData = defaultOcaCartFacade.autoGeneratePONumber("1010512");
		Assert.assertEquals(poNumber, purchaseOrderData.getPurchaseOrderNumber());
	}

	@Test
	public void testAutoGeneratePONumberWithOnlyFirstName()
	{
		Mockito.when(defaultOcaCartFacade.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(cartModel.getUser()).thenReturn(userModel);
		Mockito.when(userModel.getName()).thenReturn("Shaun");
		final String date = new SimpleDateFormat("yyMMddHHmm").format(new Date());
		final String poNumber = "1010512S" + date;
		final PurchaseOrderData purchaseOrderData = defaultOcaCartFacade.autoGeneratePONumber("1010512");
		Assert.assertEquals(poNumber, purchaseOrderData.getPurchaseOrderNumber());
	}

	@Test
	public void testAutoGeneratePONumberNoCart()
	{
		Mockito.when(defaultOcaCartFacade.hasSessionCart()).thenReturn(false);
		final PurchaseOrderData poNumber = defaultOcaCartFacade.autoGeneratePONumber("1010512");
		Assert.assertNull(poNumber);
	}

	@Test
	public void testNoName()
	{
		Mockito.when(defaultOcaCartFacade.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(cartModel.getUser()).thenReturn(userModel);
		Mockito.when(userModel.getName()).thenReturn(null);
		final String date = new SimpleDateFormat("yyMMddHHmm").format(new Date());
		final String poNumber = "1010512" + date;
		final PurchaseOrderData purchaseOrderData = defaultOcaCartFacade.autoGeneratePONumber("1010512");
		Assert.assertEquals(poNumber, purchaseOrderData.getPurchaseOrderNumber());
	}

	@Test
	public void testAutoGeneratePONumber3Name()
	{
		Mockito.when(defaultOcaCartFacade.hasSessionCart()).thenReturn(true);
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(cartModel.getUser()).thenReturn(userModel);
		Mockito.when(userModel.getName()).thenReturn("Shaun Murphy mur");
		final String date = new SimpleDateFormat("yyMMddHHmm").format(new Date());
		final String poNumber = "1010512Sm" + date;
		final PurchaseOrderData purchaseOrder = defaultOcaCartFacade.autoGeneratePONumber("1010512");
		Assert.assertEquals(poNumber, purchaseOrder.getPurchaseOrderNumber());
	}

	@Test
	public void testDeliveryOptions()
	{
		final List<DeliveryOptionModel> deliveryOptionModelList = new ArrayList<>();
		deliveryOptionModel.setName("Standard(2-3days)");
		deliveryOptionModel1.setName("By Ground");
		deliveryOptionModelList.add(deliveryOptionModel);
		deliveryOptionModelList.add(deliveryOptionModel1);
		final List<DeliveryOptionData> deliveryOptionDataList = new ArrayList<>();
		deliveryOptionData.setName("Standard(2-3days)");
		deliveryOptionData1.setName("By Ground");
		deliveryOptionDataList.add(deliveryOptionData);
		deliveryOptionDataList.add(deliveryOptionData1);
		Mockito.when(ocaCartService.getDeliveryOptions("fedex")).thenReturn(deliveryOptionModelList);
		Mockito.when(deliveryOptionConverter.convertAll(deliveryOptionModelList)).thenReturn(deliveryOptionDataList);
		final List<DeliveryOptionData> deliveryOptions = defaultOcaCartFacade.getDeliveryOptions("fedex");
		Assert.assertEquals(2, deliveryOptions.size());
	}

	@Test
	public void testDeliveryOptionsWithNullList()
	{
		Mockito.when(ocaCartService.getDeliveryOptions("fedex")).thenReturn(null);
		final List<DeliveryOptionData> deliveryOptions = defaultOcaCartFacade.getDeliveryOptions("fedex");
		Assert.assertEquals(0, deliveryOptions.size());
	}

	@Test
	public void testDeliveryOptionsWithEmptyList()
	{
		final List<DeliveryOptionModel> deliveryOptionModelList = new ArrayList<>();
		Mockito.when(ocaCartService.getDeliveryOptions("fedex")).thenReturn(deliveryOptionModelList);
		final List<DeliveryOptionData> deliveryOptions = defaultOcaCartFacade.getDeliveryOptions("fedex");
		Assert.assertEquals(0, deliveryOptions.size());
	}

	@Test
	public void testUpdateCart()
	{
		final CartModel cart = new CartModel();
		final CartData data = new CartData();
		Mockito.when(ocaCartService.updateSessionCart("1010512", false)).thenReturn(cart);
		Mockito.when(cartConverter.convert(cart)).thenReturn(data);
		final CartData updatedData = defaultOcaCartFacade.updateSessionCart("1010512", false);
		Assert.assertEquals(updatedData, data);

	}

	@Test
	public void testB2BUnitShippingCarrier()
	{
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		final CartModel ocaCartModel = new CartModel();
		Mockito.when(cartService.getSessionCart()).thenReturn(ocaCartModel);
		Mockito.when(b2bUnitService.getUnitForUid(any())).thenReturn(b2bUnit);
		Mockito.when(configuration.getString(SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE)).thenReturn(SHIP_BY_OLYMPUS);
		ocaCartModel.setUnit(b2bUnit);
		b2bUnit.setShippingCarrierAccountReference(shippingCarrierAccountModel);
		Mockito.when(shippingCarrierConverter.convert(shippingCarrierAccountModel.getShippingCarrier()))
				.thenReturn(shippingCarrier);
		final ShippingCarrierListData listData = defaultOcaCartFacade.selectShippingCarrier();
		Assert.assertEquals(1, listData.getShippingCarriers().size());
	}

	@Test
	public void testCartShippingCarrier()
	{
		final CartModel ocaCart = new CartModel();
		final B2BUnitModel ocaB2BUnit = new B2BUnitModel();
		Mockito.when(configuration.getString(SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE)).thenReturn(SHIP_BY_OLYMPUS);
		Mockito.when(cartService.getSessionCart()).thenReturn(ocaCart);
		Mockito.when(b2bUnitService.getUnitForUid(any())).thenReturn(ocaB2BUnit);
		ocaCart.setUnit(ocaB2BUnit);
		ocaCart.setShippingCarrier("shipCarrier");
		Mockito.when(ocaCartService.getShippingCarrierForCode(SHIP_BY_OLYMPUS)).thenReturn(shippingCarrierMode);
		Mockito.when(shippingCarrierConverter.convert(shippingCarrierMode)).thenReturn(carrierData);
		final ShippingCarrierListData listData = defaultOcaCartFacade.selectShippingCarrier();
		Assert.assertEquals(2, listData.getShippingCarriers().size());
	}

	@Test
	public void testShipByOlympusShippingCarrier()
	{
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		final CartModel cartModel1 = new CartModel();
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel1);
		Mockito.when(b2bUnitService.getUnitForUid(any())).thenReturn(b2bUnit);
		Mockito.when(configuration.getString(SHIP_BY_OLYMPUS_CODE, SHIP_BY_OLYMPUS_FALLBACK_CODE)).thenReturn(SHIP_BY_OLYMPUS);
		cartModel1.setUnit(b2bUnit);
		b2bUnit.setShippingCarrierAccountReference(null);
		cartModel1.setShippingCarrier(null);
		Mockito.when(ocaCartService.getShippingCarrierForCode(SHIP_BY_OLYMPUS)).thenReturn(carrierModel);
		Mockito.when(shippingCarrierConverter.convert(shippingCarrierMode)).thenReturn(carrierData);
		final ShippingCarrierListData listData = defaultOcaCartFacade.selectShippingCarrier();
		Assert.assertEquals(1, listData.getShippingCarriers().size());
	}

	@Test
	public void testMiniCartSummaryExceedingMaxValue() {
		Mockito.when(defaultOcaCartFacade.getMiniCart()).thenReturn(miniCartData);
		Mockito.when(configuration.getInteger(MINICART_MAX_DISPLAY_VALUE,999)).thenReturn(maxValue);
		miniCartData.setTotalUnitCount(1001);
		Assert.assertEquals("999+", defaultOcaCartFacade.getMiniCartSummary().getDisplayTotalUnitCount());
	}
	@Test
	public void testMiniCartSummaryBelowMaxValue() {
		Mockito.when(defaultOcaCartFacade.getMiniCart()).thenReturn(miniCartData);
		Mockito.when(configuration.getInteger(MINICART_MAX_DISPLAY_VALUE,999)).thenReturn(maxValue);
		miniCartData.setTotalUnitCount(999);
		Assert.assertEquals("999", defaultOcaCartFacade.getMiniCartSummary().getDisplayTotalUnitCount());
	}
}
