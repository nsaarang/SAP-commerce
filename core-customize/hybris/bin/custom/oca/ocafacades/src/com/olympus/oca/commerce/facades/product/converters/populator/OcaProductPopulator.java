package com.olympus.oca.commerce.facades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantProductModel;

public class OcaProductPopulator implements Populator<ProductModel, ProductData> {

    @Override
    public void populate(ProductModel productModel, ProductData productData) throws ConversionException {

        productData.setSearchEnabled(productModel.isSearchEnabled());
        productData.setPurchaseEnabled(productModel.isPurchaseEnabled());
        if (null != productModel.getDescription()) {
            productData.setDescription(productModel.getDescription());
        }
        if (productModel instanceof VariantProductModel) {
            final VariantProductModel variantProduct = (VariantProductModel) productModel;
            if (null != variantProduct.getBaseProduct() && null != variantProduct.getBaseProduct().getName()) {
                productData.setBaseProductName(variantProduct.getBaseProduct().getName());
            }
        }
    }}
