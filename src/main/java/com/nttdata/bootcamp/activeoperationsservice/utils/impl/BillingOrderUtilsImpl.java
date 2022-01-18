package com.nttdata.bootcamp.activeoperationsservice.utils.impl;

import com.nttdata.bootcamp.activeoperationsservice.utils.BillingOrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingOrderUtilsImpl implements BillingOrderUtils {
    @Override
    public Double roundDouble(Double numberToRound, int decimalPlaces) {
        numberToRound = numberToRound * Math.pow(10, decimalPlaces);
        numberToRound = (double) (Math.round(numberToRound));
        return numberToRound / Math.pow(10, decimalPlaces);
    }

    @Override
    public Double applyInterests(Double amount, Double interestPercentage) {
        return (100 + interestPercentage)/100 * amount;
    }
}
