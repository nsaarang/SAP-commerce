package com.olympus.oca.commerce.facades.order.converters.populator;

import com.olympus.oca.commerce.core.enums.LoadingGroup;
import com.olympus.oca.commerce.core.enums.MaterialGroup;
import com.olympus.oca.commerce.core.model.HeavyOrderQuestionsModel;
import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.HeavyOrderQuestionsCartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Optional.ofNullable;

/**
 * OcaCartPopulator
 * Adds the shipping notifications to cart model based on the weight and attributes of the product.
 */
public class OcaCartPopulator extends CartPopulator {

    /**
     * Threshold weight limit to decide whether products will be shipped by ground.
     */
    private static final String THRESHOLD_WEIGHT = "150";
    private ModelService modelService;

    private PriceDataFactory priceDataFactory;

    @Override
    public void populate(CartModel source, CartData target) {

        addShippingNotifications(source, target);
        addTotals(source, target);
        target.setDeliveryCost(source.getDeliveryAddress() != null ? createPrice(source, source.getDeliveryCost()) : null);

    }

    /**
     * This method is to add the boolean values to determine if the order is going to ship by ground and also the gross weight will be in LB.
     *
     * @param source - The cart model object.
     * @param target - The response cart data object.
     */
    public void addShippingNotifications(final CartModel source, final CartData target) {
        AtomicReference<Double> totalShipmentWeight = new AtomicReference<>((double) 0);
        source.getEntries().forEach(abstractOrderEntryModel -> {
            ProductModel product = abstractOrderEntryModel.getProduct();
            Optional<LoadingGroup> loadingGroup = ofNullable(product.getLoadingGroup());
            Optional<MaterialGroup> materialGroup = ofNullable(product.getMaterialGroup());
            if (!target.isShipByGround()) {
                target.setShipByGround((loadingGroup.isPresent() ?
                        product.getLoadingGroup().getCode().equalsIgnoreCase(LoadingGroup.LG0001.getCode())
                        : Boolean.FALSE) ||
                        (materialGroup.isPresent() ?
                                product.getMaterialGroup().getCode().equalsIgnoreCase(MaterialGroup.MG9004.getCode())
                                : Boolean.FALSE));
            }
            totalShipmentWeight.updateAndGet(totalWeight -> totalWeight
                    + (abstractOrderEntryModel.getQuantity() * (ofNullable(product.getGrossWeight()).orElse((double) 0))));
        });
        if (totalShipmentWeight.get() > (Double.parseDouble(THRESHOLD_WEIGHT))) {
            target.setHeavyOrder(true);
            if (Objects.nonNull(source.getHeavyOrderQuestions())) {
                HeavyOrderQuestionsCartData heavyOrderQuestionsCartData = new HeavyOrderQuestionsCartData();
                HeavyOrderQuestionsModel heavyOrderQuestionsModel = source.getHeavyOrderQuestions();
                heavyOrderQuestionsCartData.setEmail(heavyOrderQuestionsModel.getEmail());
                heavyOrderQuestionsCartData.setName(heavyOrderQuestionsModel.getName());
                heavyOrderQuestionsCartData.setPhoneNumber(heavyOrderQuestionsModel.getPhoneNumber());
                heavyOrderQuestionsCartData.setTruckSize(heavyOrderQuestionsModel.getTruckSize());
                heavyOrderQuestionsCartData.setLargeTruckEntry(heavyOrderQuestionsModel.isLargeTruckEntry());
                heavyOrderQuestionsCartData.setLiftAvailable(heavyOrderQuestionsModel.isLiftAvailable());
                heavyOrderQuestionsCartData.setLoadingDock(heavyOrderQuestionsModel.isLoadingDock());
                target.setHeavyOrderQuestions(heavyOrderQuestionsCartData);
            }
        } else {
            target.setHeavyOrder(false);
            source.setHeavyOrderQuestions(null);
            getModelService().save(source);
        }
    }

    @Override
    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Override
    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    @Override
    public void addTotals(final AbstractOrderModel source, final AbstractOrderData target) {
        double contractPriceTotal = 0.00;
        double subTotal = 0.00;

        for (AbstractOrderEntryModel entry : source.getEntries()) {
            if (Objects.nonNull(entry)) {
                contractPriceTotal += entry.getContractPrice();
                subTotal += entry.getTotalPrice();
            }
        }

        double discount = contractPriceTotal - subTotal;
        double totalPrice = subTotal + discount;

        target.setSubTotal(createPrice(source, subTotal));
        target.setTotalPrice(createPrice(source, totalPrice));
        target.setTotalDiscounts(createPrice(source, discount));
    }
}
