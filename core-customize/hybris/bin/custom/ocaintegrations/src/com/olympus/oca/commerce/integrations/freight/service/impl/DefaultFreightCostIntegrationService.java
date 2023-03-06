package com.olympus.oca.commerce.integrations.freight.service.impl;

import com.olympus.oca.commerce.integrations.exceptions.OcaIntegrationException;
import com.olympus.oca.commerce.integrations.freight.service.FreightCostIntegrationService;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceItemRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import rx.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultFreightCostIntegrationService implements FreightCostIntegrationService {
    private static final Logger LOG = Logger.getLogger(DefaultFreightCostIntegrationService.class);
    private static final String PROCESS_TYPE = "ZAOR";
    private static final String FREIGHT_TERM = "A45";
    private static final String ITEM_TYPE = "U960";

    private OcaOutboundService ocaOutboundService;

    public DefaultFreightCostIntegrationService(OcaOutboundService ocaOutboundService) {
        this.ocaOutboundService = ocaOutboundService;
    }

    @Override
    public AbstractOrderModel fetchFreightCostForCart(AbstractOrderModel candidate) throws OcaIntegrationException {
        Assert.notNull(candidate, "cart is required to calculate the freight cost");
        if (candidate.getDeliveryAddress() != null) {
            fetchFreightPriceFromCrm(candidate).subscribe(
                    // onNext
                    responseEntityMap -> {

                        if (isSentSuccessfully(responseEntityMap)) {
                            if (getPropertyValue(responseEntityMap, "ITEM") instanceof HashMap) {
                                Map resultMap = (HashMap) getPropertyValue(responseEntityMap, "ITEM");
                                fetchFreightCostFromResponse(resultMap, candidate);
                            } else {
                                List<Map> valueMapList = (ArrayList) getPropertyValue(responseEntityMap, "ITEM");
                                valueMapList.forEach(resultMap -> {
                                    fetchFreightCostFromResponse(resultMap, candidate);
                                });
                            }
                        }
                    }
                    // onError
                    , error -> LOG.error(
                            String.format("The freight price for OrderNumber [%s] has not been received from BTP! %n%s", candidate.getCode(),
                                          error.getMessage()), error));
        }
        return candidate;
    }

    protected Observable<ResponseEntity<Map>> fetchFreightPriceFromCrm(AbstractOrderModel cart) {
        BTPOutboundFreightPriceRequestModel freightPriceModel = new BTPOutboundFreightPriceRequestModel();
        convertOrderToFreightRequest(cart, freightPriceModel);
        return ocaOutboundService.getFreightCost(freightPriceModel);
    }

    protected void fetchFreightCostFromResponse(Map<String, ?> resultMap, AbstractOrderModel order) {
        if (resultMap.containsKey("LINE_ITEM_ID") && resultMap.containsKey("FREIGHT")) {
            String lineItem = resultMap.get("LINE_ITEM_ID").toString();
            String freightPrice = resultMap.get("FREIGHT").toString();
            for (final AbstractOrderEntryModel e : order.getEntries()) {
                if (lineItem.equalsIgnoreCase(e.getEntryNumber().toString())) {
                    e.setFreightPrice(Double.valueOf(freightPrice));
                }
            }
        }
    }

    protected void convertOrderToFreightRequest(AbstractOrderModel cart, BTPOutboundFreightPriceRequestModel freightPriceModel) {
        if (cart instanceof CartModel) {
            List<BTPOutboundFreightPriceItemRequestModel> freightItemSets = new ArrayList<>();
            freightPriceModel.setProcess_type(PROCESS_TYPE);
            freightPriceModel.setQuoteId(cart.getCode());
            freightPriceModel.setSoldTo(cart.getUnit().getUid());
            for (AbstractOrderEntryModel cartEntry : cart.getEntries()) {
                BTPOutboundFreightPriceItemRequestModel freightItem = new BTPOutboundFreightPriceItemRequestModel();
                freightItem.setProduct_id(cartEntry.getProduct().getCode());
                freightItem.setLine_item_id(String.valueOf(cartEntry.getEntryNumber()));
                freightItem.setFreight_term(FREIGHT_TERM);
                freightItem.setUom(cartEntry.getUnit().getCode());
                freightItem.setQuantity(cartEntry.getQuantity());
                freightItem.setItem_type(ITEM_TYPE);
                freightItem.setFreight("");
                freightItem.setContract_id("");
                freightItemSets.add(freightItem);
            }
            freightPriceModel.setBtpOutboundFreightPriceItems(freightItemSets);
        }
    }

    //        @Override
    protected boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap) {
        return HttpStatus.OK.equals(responseEntityMap.getStatusCode());
    }

    //    @Override
    protected Object getPropertyValue(ResponseEntity<Map> responseEntityMap, String property) {
        if (responseEntityMap.getBody() == null) {
            return null;
        }
        Object next = responseEntityMap.getBody().keySet().iterator().next();
        checkArgument(next != null, String.format("Freight Price response entity key set cannot be null for property [%s]!", property));

        String responseKey = next.toString();
        checkArgument(responseKey != null && !responseKey.isEmpty(),
                      String.format("Freight Price response property can neither be null nor empty for property [%s]!", property));

        Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
        checkArgument(propertyValue != null, String.format("Freight Price response property [%s] value cannot be null!", property));

        return propertyValue;
    }
}
