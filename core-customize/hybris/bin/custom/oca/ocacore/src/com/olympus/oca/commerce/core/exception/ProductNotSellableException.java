package com.olympus.oca.commerce.core.exception;

import org.springframework.ws.WebServiceException;

public class ProductNotSellableException extends WebServiceException {
    public ProductNotSellableException(String message) {
        super(message);
    }

    public ProductNotSellableException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
