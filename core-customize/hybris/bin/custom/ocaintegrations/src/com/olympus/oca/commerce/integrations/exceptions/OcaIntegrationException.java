package com.olympus.oca.commerce.integrations.exceptions;

public abstract class OcaIntegrationException extends RuntimeException {
    public OcaIntegrationException(final String message) {
        super(message);
    }
}
