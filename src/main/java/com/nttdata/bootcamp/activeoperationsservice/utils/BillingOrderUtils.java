package com.nttdata.bootcamp.activeoperationsservice.utils;

public interface BillingOrderUtils {
    Double roundDouble(Double numberToRound, int decimalPlaces);
    Double applyInterests(Double number, Double interestPercentage);
}
