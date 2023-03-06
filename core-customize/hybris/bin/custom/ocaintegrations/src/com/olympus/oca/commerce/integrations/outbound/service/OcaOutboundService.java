package com.olympus.oca.commerce.integrations.outbound.service;

import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

public interface OcaOutboundService {

    Observable<ResponseEntity<Map>> getContractPrice(BTPOutboundContractPriceRequestModel contractPriceModel);

    Observable<ResponseEntity<Map>> getFreightCost(BTPOutboundFreightPriceRequestModel freightPriceModel);
}
