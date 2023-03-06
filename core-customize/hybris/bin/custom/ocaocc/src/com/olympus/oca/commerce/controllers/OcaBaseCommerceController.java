package com.olympus.oca.commerce.controllers;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class OcaBaseCommerceController extends OcaBaseController{

        private static final Logger LOG = LoggerFactory.getLogger(OcaBaseCommerceController.class);

        @Resource(name = "commerceWebServicesCartFacade2")
        private CartFacade cartFacade;

        @Resource(name = "userFacade")
        private UserFacade userFacade;


        protected CartData getSessionCart()
        {
            return cartFacade.getSessionCart();
        }

        protected CartFacade getCartFacade()
        {
            return cartFacade;
        }

        protected void setCartFacade(final CartFacade cartFacade)
        {
            this.cartFacade = cartFacade;
        }

        protected UserFacade getUserFacade()
        {
            return userFacade;
        }

        protected void setUserFacade(final UserFacade userFacade)
        {
            this.userFacade = userFacade;
        }

}
