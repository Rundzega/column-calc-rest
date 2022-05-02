package com.columncalcrest.util;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class FitLinearPolynomialFunction {

    public static double[] fit(double firstAbscissa, double secondAbcissa,
                               double firstOrdinate, double secondOrdinate) {

        WeightedObservedPoints yCoefficientPoints = new WeightedObservedPoints();
        yCoefficientPoints.add(firstAbscissa, firstOrdinate);
        yCoefficientPoints.add(secondAbcissa, secondOrdinate);
        PolynomialCurveFitter yFitter = PolynomialCurveFitter.create(1);
        return yFitter.fit(yCoefficientPoints.toList());
    }
}
