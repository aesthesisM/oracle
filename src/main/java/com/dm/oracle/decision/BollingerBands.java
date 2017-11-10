package com.dm.oracle.decision;

import com.dm.oracle.config.JacksonConfig;
import com.dm.oracle.request.model.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khobsyzl28 on 11/8/2017.
 */
public class BollingerBands implements Serializable {

    private static final BollingerBands _INSTANCE = new BollingerBands();
    private List<Currency> currencyPeriodicValues;
    public float currencyMean;
    public float currencyStandartDeviation;

    private BollingerBands() {

    }

    public static BollingerBands getInstance() {
        return _INSTANCE;
    }


    public void calculate(List<Currency> values) {
        currencyPeriodicValues = values;
        currencyMean = 0f;
        currencyStandartDeviation = 0f;
        calculateMean();
        calculateStandartDeviation();
    }


    private void calculateMean() {
        for (int i = 0; i < currencyPeriodicValues.size(); i++) {
            currencyMean += (currencyPeriodicValues.get(i).low + currencyPeriodicValues.get(i).high)/2;
        }
        currencyMean /= currencyPeriodicValues.size();
    }

    private void calculateStandartDeviation() {
        float total = 0;
        for (int i = 0; i < currencyPeriodicValues.size(); i++) {
            total += (float) Math.pow(currencyMean - currencyPeriodicValues.get(i).last, 2);
        }
        currencyStandartDeviation = (float) Math.sqrt(total / currencyPeriodicValues.size());
    }

}
