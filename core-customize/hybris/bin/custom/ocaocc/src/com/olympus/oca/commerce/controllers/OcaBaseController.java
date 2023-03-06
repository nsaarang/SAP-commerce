package com.olympus.oca.commerce.controllers;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class OcaBaseController
{

	protected static final String DEFAULT_PAGE_SIZE = "20";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
	protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";

	private static final Logger LOG = Logger.getLogger(OcaBaseController.class);

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "ocaB2BUnitValidator")
	private Validator ocaB2BUnitValidator;

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	public void validateB2bUnitId(final String b2bUnitId)
	{
		final Errors errors = new BeanPropertyBindingResult(b2bUnitId, "B2B Unit Id");
		ocaB2BUnitValidator.validate(b2bUnitId, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

}
