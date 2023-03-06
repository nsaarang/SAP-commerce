package com.olympus.oca.commerce.validators;
import com.olympus.oca.commerce.dto.order.HeavyOrderQuestionsCartWsDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

public class OcaQuestionsValidator implements Validator {
    @Override
    public boolean supports(final Class<?> arg0)
    {
        return String.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(Object target, Errors errors) {

        final HeavyOrderQuestionsCartWsDTO questionnaire = (HeavyOrderQuestionsCartWsDTO) target;

        if(Objects.isNull(questionnaire.getName())) {
            errors.rejectValue("name","questionnaire.nameIsEmpty","name should not be empty");
        }
        if(Objects.isNull(questionnaire.getEmail())) {
            errors.rejectValue("email","questionnaire.emailIsEmpty","email should not be empty");
        }
        if(Objects.isNull(questionnaire.getPhoneNumber())) {
            errors.rejectValue("phoneNumber","questionnaire.phoneNumberIsEmpty","phoneNumber should not be empty");
        }
        if (Objects.isNull(questionnaire.getLargeTruckEntry())) {
            errors.rejectValue("largeTruckEntry","questionnaire.largeTruckEntry","large Truck Entry possible :  Select yes or no");
        }
        if (Objects.isNull(questionnaire.getLiftAvailable())) {
            errors.rejectValue("liftAvailable","questionnaire.liftAvailable","lift Available: Select yes or no");
        }
        if (Objects.isNull(questionnaire.getOrderDeliveredInside())) {
            errors.rejectValue("orderDeliveredInside","questionnaire.orderDeliveredInside","Order delivered:  Select yes or no");
        }
        if (Objects.isNull(questionnaire.getLoadingDock())) {
            errors.rejectValue("loadingDock","questionnaire.loadingDock","loading dock available :  Select yes or no");
        }
        if(Objects.nonNull(questionnaire.getLargeTruckEntry()) && questionnaire.getLargeTruckEntry().booleanValue()== false && Objects.isNull(questionnaire.getTruckSize()))
        {
            errors.rejectValue("truckSize","questionnaire.truckSize","please enter the truck size");
        }




    }
}
