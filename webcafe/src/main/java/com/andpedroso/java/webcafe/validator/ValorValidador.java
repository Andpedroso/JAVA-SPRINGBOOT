package com.andpedroso.java.webcafe.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ValorValidador implements ConstraintValidator<Valor, BigDecimal> {
    private Valor annotation;
    @Override
    public void initialize(Valor annotation) {
        this.annotation = annotation;
    }
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        var min = BigDecimal.valueOf(annotation.min());
        if (value == null) return false;
        else return value.compareTo(min) >= 0;
    }
}
