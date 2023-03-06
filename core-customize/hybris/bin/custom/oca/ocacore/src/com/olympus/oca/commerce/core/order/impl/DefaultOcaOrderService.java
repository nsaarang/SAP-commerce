/**
 *
 */
package com.olympus.oca.commerce.core.order.impl;

import static com.olympus.oca.commerce.core.constants.OcaCoreConstants.ErrorConstants.B2B_UNIT_NOT_FOUND;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.order.data.OrderHistoryFiltersData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.olympus.oca.commerce.core.b2bUnit.OcaB2BUnitDao;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import com.olympus.oca.commerce.core.order.OcaOrderDao;
import com.olympus.oca.commerce.core.order.OcaOrderService;


/**
 * The Class DefaultOcaOrderService.
 */
public class DefaultOcaOrderService extends DefaultOrderService implements OcaOrderService
{

	/** The oca order dao. */
	private OcaOrderDao ocaOrderDao;
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private UserService userService;
	private OcaB2BUnitDao ocaB2BUnitDao;

	/** The configuration service. */
	private ConfigurationService configurationService;

	/**
	 * Gets the recently ordered products.
	 *
	 * @param customer
	 *                    the customer
	 * @param unit
	 *                    the unit
	 * @return the recently ordered products
	 */
	@Override
	public Set<ProductModel> getRecentlyOrderedProducts(final CustomerModel customer, final B2BUnitModel unit)
	{
		final int productCount = getConfigurationService().getConfiguration()
				.getInt(OcaCoreConstants.RECENTLY_ORDERED_PRODUCT_COUNT, 4);
		final Set<ProductModel> products = new LinkedHashSet();
		getOcaOrderDao().getRecentlyOrderedProducts(customer, unit).forEach((product) -> {
			while (products.size() < productCount)
			{
				products.add(product);
				break;
			}
		});
		return products;
	}

	/**
	 * Gets the order list.
	 *
	 * @param customerModel
	 *                                   the customer model
	 * @param store
	 *                                   the store
	 * @param orderHistoryFiltersData
	 *                                   the order history filters data
	 * @param pageableData
	 *                                   the pageable data
	 * @return the order list
	 */
	public SearchPageData<OrderEntryModel> getOrderList(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderHistoryFiltersData orderHistoryFiltersData, final PageableData pageableData, final String query)
	{
		validateParameterNotNull(customerModel, "Customer model cannot be null");
		validateParameterNotNull(store, "Store must not be null");
		validateParameterNotNull(pageableData, "PageableData must not be null");
		List<B2BUnitModel> b2bModelList = null;
		final boolean isAccountNumberPresent = true;

		b2bModelList = validateB2BUnitforCurrentUser(orderHistoryFiltersData, b2bModelList);
		return getOcaOrderDao().getOrderList(customerModel, store, orderHistoryFiltersData, b2bModelList, pageableData, query);
	}


	/**
	 * @param orderHistoryFiltersData
	 * @param b2bUnits
	 * @return
	 */
	private List<B2BUnitModel> validateB2BUnitforCurrentUser(final OrderHistoryFiltersData orderHistoryFiltersData,
			List<B2BUnitModel> b2bModelList)
	{
		final List<String> b2bUnits = new ArrayList<>();
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) getUserService().getCurrentUser();
		final Set<PrincipalGroupModel> currentCustomerGroups = new HashSet<PrincipalGroupModel>(currentCustomer.getGroups());

		if (null != orderHistoryFiltersData.getAccountNumber())
		{
			final ArrayList<String> filterDataAccountNumber = new ArrayList<String>(
					Arrays.asList(orderHistoryFiltersData.getAccountNumber().split(",")));
			final List<String> customerUidList = currentCustomerGroups.stream().map(PrincipalGroupModel::getUid)
					.collect(Collectors.toList());
			for (final String filterDataUnit : filterDataAccountNumber)
			{
				if (customerUidList.contains(filterDataUnit))
				{
					b2bUnits.add(filterDataUnit);
				}
				else
				{
					throw new ModelNotFoundException(B2B_UNIT_NOT_FOUND);
				}
			}
			if (null != b2bUnits)
			{
				b2bModelList = getOcaB2BUnitDao().getB2BUnitModelFromCode(b2bUnits);
			}
		}
		return b2bModelList;

	}


	/**
	 * Gets the oca order dao.
	 *
	 * @return the ocaOrderDao
	 */
	public OcaOrderDao getOcaOrderDao()
	{
		return ocaOrderDao;
	}

	/**
	 * Sets the oca order dao.
	 *
	 * @param ocaOrderDao
	 *                       the ocaOrderDao to set
	 */
	public void setOcaOrderDao(final OcaOrderDao ocaOrderDao)
	{
		this.ocaOrderDao = ocaOrderDao;
	}

	/**
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *                          the b2bUnitService to set
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *                                the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *                       the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the ocaB2BUnitDao
	 */
	public OcaB2BUnitDao getOcaB2BUnitDao()
	{
		return ocaB2BUnitDao;
	}

	/**
	 * @param ocaB2BUnitDao
	 *                         the ocaB2BUnitDao to set
	 */
	public void setOcaB2BUnitDao(final OcaB2BUnitDao ocaB2BUnitDao)
	{
		this.ocaB2BUnitDao = ocaB2BUnitDao;
	}


}
