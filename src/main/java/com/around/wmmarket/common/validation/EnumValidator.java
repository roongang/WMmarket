package com.around.wmmarket.common.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum,String> {
    private Enum annotation;

    @Override
    public void initialize(Enum constraintAnnotation){
        this.annotation=constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Object[] enumValues=this.annotation.enumClass().getEnumConstants();
        if(enumValues==null) return false;
        for(Object enumValue:enumValues){
            if(value.equals(enumValue.toString())
                    || (this.annotation.ignoreCase() && value.equalsIgnoreCase(enumValue.toString()))) return true;
        }
        return false;
    }

    private void addConstraintViolation(ConstraintValidatorContext context,String msg){
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(msg)
                .addConstraintViolation();
    }
}
