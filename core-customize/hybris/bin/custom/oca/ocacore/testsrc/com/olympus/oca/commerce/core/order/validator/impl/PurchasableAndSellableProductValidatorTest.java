package com.olympus.oca.commerce.core.order.validator.impl;

import com.olympus.oca.commerce.core.enums.DistributionChainStatus;
import com.olympus.oca.commerce.core.enums.MaterialGroup4;
import com.olympus.oca.commerce.core.exception.ProductNotSellableException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Objects;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.ErrorConstants.PRODUCT_NOT_SELLABLE;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


@UnitTest
public class PurchasableAndSellableProductValidatorTest {

    private final PurchasableAndSellableProductValidator purchasableAndSellableProductValidator= new PurchasableAndSellableProductValidator();

    @Mock
    CommerceCartParameter commerceCartParameter = new CommerceCartParameter();

    @Mock
    ProductModel product = new ProductModel();

    @Test
    public void testValidateForNonSellableProducts() {
        commerceCartParameter.setProduct(product);
        product.setPurchaseEnabled(Boolean.FALSE);
        ProductNotSellableException exception = assertThrows(ProductNotSellableException.class,
                () -> purchasableAndSellableProductValidator.validate(commerceCartParameter));
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains(PRODUCT_NOT_SELLABLE));
    }

}
