package com.olympus.oca.commerce.integrations.contract.service.impl;

import com.olympus.oca.commerce.integrations.contract.service.ContractPriceIntegrationService;
import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.PricingRequestLineItemsModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import rx.Observable;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import static com.google.common.base.Preconditions.checkArgument;
public class DefaultContractPriceIntegrationService implements ContractPriceIntegrationService {

    private static final Logger LOG = Logger.getLogger(DefaultContractPriceIntegrationService.class);
    private OcaOutboundService ocaOutboundService;
    private static final String ITEM_TYPE = "NEW";

    @Override
    public AbstractOrderEntryModel fetchContractPricetForCart(final AbstractOrderEntryModel entry)
    {
        Assert.notNull(entry, "cart is required to calculate the contract price");
        fetchContractPriceFromCrm(entry).subscribe(
                responseEntityMap -> {
                        if (isSentSuccessfully(responseEntityMap)) {
                            HashMap resultMap = (HashMap) getPropertyValue(responseEntityMap,"OUTPUT");
                            if(null != resultMap) {
                                fetchContractPriceFromResponse(resultMap, entry);
                            }
                        }
                    }
                    , error -> LOG.error(String.format("The contract price for OrderNumber [%s] has not been received from BTP! %n%s",
                            entry.getOrder().getCode(), error.getMessage()), error)
                );
        return entry;
    }

    protected Observable<ResponseEntity<Map>> fetchContractPriceFromCrm(AbstractOrderEntryModel entryModel) {
        BTPOutboundContractPriceRequestModel contractPriceModel = new BTPOutboundContractPriceRequestModel();
        convertOrderToContractRequest(entryModel,contractPriceModel);
      return ocaOutboundService.getContractPrice(contractPriceModel);
    }

    protected void convertOrderToContractRequest(AbstractOrderEntryModel entryModel,BTPOutboundContractPriceRequestModel contractPriceModel) {
        PricingRequestLineItemsModel priceReqModel = new PricingRequestLineItemsModel();
        priceReqModel.setSoldTo(entryModel.getOrder().getUnit().getUid());
        priceReqModel.setProduct_id(entryModel.getProduct().getCode());
        priceReqModel.setItem_type(ITEM_TYPE);
        priceReqModel.setQuote_id(entryModel.getOrder().getCode());
        priceReqModel.setLine_item_id(String.valueOf(entryModel.getEntryNumber()));
        contractPriceModel.setPricingRequestLineItems(priceReqModel);
    }

    private void fetchContractPriceFromResponse(HashMap resultMap, AbstractOrderEntryModel entry) {
        String lineItem = resultMap.get("LINE_ITEM_ID").toString();
        String contractPrice = resultMap.get("CONTRACT_PRICE").toString();
        if(entry instanceof CartEntryModel) {
            if (lineItem.equalsIgnoreCase(entry.getEntryNumber().toString())) {
                entry.setContractPrice(Double.valueOf(contractPrice));
            }

        }
    }

    protected boolean isSentSuccessfully(ResponseEntity<Map> responseEntityMap)
    {
        return HttpStatus.OK.equals(responseEntityMap.getStatusCode());
    }

    protected Object getPropertyValue(ResponseEntity<Map> responseEntityMap, String property)
    {
        if (responseEntityMap.getBody() != null)
        {
            Object next = responseEntityMap.getBody().keySet().iterator().next();
            checkArgument(next != null,
                    String.format("Contract Price response entity key set cannot be null for property [%s]!", property));

            String responseKey = next.toString();
            checkArgument(responseKey != null && !responseKey.isEmpty(),
                    String.format("Contract Price response property can neither be null nor empty for property [%s]!", property));

            Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
            checkArgument(propertyValue != null, String.format("Contract Price response property [%s] value cannot be null!", property));

            return propertyValue;
        }
        else
        {
            return null;
        }
    }

    public DefaultContractPriceIntegrationService(OcaOutboundService ocaOutboundService) {
        this.ocaOutboundService = ocaOutboundService;
    }

}
