package com.olympus.oca.commerce.core.category;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;

public class OcaDefaultCategoryModelUrlResolver extends AbstractUrlResolver<CategoryModel> {

    public static final String FORWARD_SLASH = "/";
    public static final String CATEGORIES = "categories";
    public static final String PRODUCTS = "products";

    @Override
    protected String resolveInternal(CategoryModel source) {
        {
            final StringBuilder url = new StringBuilder();
            url.append(FORWARD_SLASH);
            url.append(CATEGORIES);
            url.append(FORWARD_SLASH);
            url.append(source.getCode());
            url.append(FORWARD_SLASH);
            url.append(PRODUCTS);
            return url.toString();
        }
    }
}
