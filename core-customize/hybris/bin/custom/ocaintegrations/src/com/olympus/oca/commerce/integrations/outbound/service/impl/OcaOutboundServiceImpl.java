package com.olympus.oca.commerce.integrations.outbound.service.impl;

import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import com.olympus.oca.commerce.integrations.outbound.service.OcaOutboundService;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

public class OcaOutboundServiceImpl implements OcaOutboundService {

    private OutboundServiceFacade outboundServiceFacade;

    private static final String OUTBOUND_CONTRACT_PRICE_OBJECT = "OutboundContractPriceRequest";
    private static final String OUTBOUND_CONTRACT_PRICE_DESTINATION = "contractOBPriceRequest";
    private static final String OUTBOUND_FREIGHT_PRICE_OBJECT = "OutboundFreightCost";
    private static final String OUTBOUND_FREIGHT_PRICE_DESTINATION = "freightPriceConsumedDestination";

    @Override
    public Observable<ResponseEntity<Map>> getContractPrice(BTPOutboundContractPriceRequestModel contractPriceModel) {
        return getOutboundServiceFacade().send(contractPriceModel, OUTBOUND_CONTRACT_PRICE_OBJECT, OUTBOUND_CONTRACT_PRICE_DESTINATION);
    }

    @Override
    public Observable<ResponseEntity<Map>> getFreightCost(BTPOutboundFreightPriceRequestModel freightPriceModel) {
        return getOutboundServiceFacade().send(freightPriceModel,OUTBOUND_FREIGHT_PRICE_OBJECT,OUTBOUND_FREIGHT_PRICE_DESTINATION);
    }

    protected OutboundServiceFacade getOutboundServiceFacade() {
        return outboundServiceFacade;
    }

    @Required
    public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
        this.outboundServiceFacade = outboundServiceFacade;
    }

}
