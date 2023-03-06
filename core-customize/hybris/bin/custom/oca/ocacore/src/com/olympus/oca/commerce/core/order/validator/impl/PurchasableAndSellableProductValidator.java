package com.olympus.oca.commerce.core.order.validator.impl;

import com.olympus.oca.commerce.core.exception.ProductNotSellableException;
import de.hybris.platform.commerceservices.order.validator.AddToCartValidator;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.ErrorConstants.PRODUCT_NOT_SELLABLE;

public class PurchasableAndSellableProductValidator implements AddToCartValidator {

    @Override
    public boolean supports(CommerceCartParameter parameter) {
        // Assuming all the products are supported.
        return true;
    }

    @Override
    public void validate(CommerceCartParameter parameter) {
        ProductModel product = parameter.getProduct();
        if (!product.isPurchaseEnabled()) {
            throw new ProductNotSellableException(PRODUCT_NOT_SELLABLE);
        }
    }
}
