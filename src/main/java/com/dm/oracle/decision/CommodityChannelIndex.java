package com.dm.oracle.decision;

import com.dm.oracle.config.Constants;
import com.dm.oracle.request.model.Currency;

import java.util.List;

/**
 * Created by khobsyzl28 on 11/9/2017.
 */

    /*
CCI = (Typical Price  -  20-period SMA of TP) / (.015 x Mean Deviation)

Typical Price (TP) = (High + Low + Close)/3

Constant = .015

There are four steps to calculating the Mean Deviation:
First, subtract the most recent 20-period average of the typical price from each period's typical price.
Second, take the absolute values of these numbers.
Third, sum the absolute values.
Fourth, divide by the total number of periods (20).
    */

//Typical Price = (high + low + last)/3
//20 days SMA of Typical Price = (total of last 20 days typical Prices)/20
//20 days Mean Deviation = (total(20days Typical Price - Typical Prices)) / 20
//20day CCI = (TP - 20 day SMA of TP)/(0.015 * 20 days mean deviation)


    /*
    Lambert set the constant at .015 to ensure that approximately 70 to 80 percent of CCI values would fall between -100 and +100.
    This percentage also depends on the look-back period. A shorter CCI (10 periods) will be more volatile with a smaller percentage of values between +100 and -100.
     Conversely, a longer CCI (40 periods) will have a higher percentage of values between +100 and -100.
     */
public class CommodityChannelIndex {
    private final static CommodityChannelIndex _INSTANCE = new CommodityChannelIndex();
    private List<Currency> currencyPeriodicValues;
    private float[] typicalPrices;
    private float mean;
    private float meanDeviation;
    public float cciPoint;

    private CommodityChannelIndex() {

    }

    public static CommodityChannelIndex getInstance() {
        return _INSTANCE;
    }

    public void calculate(List<Currency> values) {
        currencyPeriodicValues = values;
        calculateTypicalPricesAndMean();
        calculateMeanDeviation();
        calculateCCIRate();
    }

    private void calculateTypicalPricesAndMean() {
        typicalPrices = new float[currencyPeriodicValues.size()];
        mean = 0f;
        for (int i = 0; i < typicalPrices.length; i++) {
            typicalPrices[i] = (currencyPeriodicValues.get(i).last + currencyPeriodicValues.get(i).high + currencyPeriodicValues.get(i).low) / 3;
            mean += (currencyPeriodicValues.get(i).low + currencyPeriodicValues.get(i).high) / 2;
        }
        mean /= typicalPrices.length;
    }

    private void calculateMeanDeviation() {
        meanDeviation = 0f;
        for (int i = 0; i < typicalPrices.length; i++) {
            meanDeviation += Math.abs(mean - typicalPrices[i]);
        }
        meanDeviation /= typicalPrices.length;
    }

    private void calculateCCIRate() {
        cciPoint = (typicalPrices[typicalPrices.length-1] - mean)/(Constants.CCI_CALCULATE_CONSTANT * meanDeviation);
    }

}
