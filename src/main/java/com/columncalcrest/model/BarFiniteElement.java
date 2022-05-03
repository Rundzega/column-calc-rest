package com.columncalcrest.model;

import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.RebarFailedException;
import com.columncalcrest.util.FitLinearPolynomialFunction;
import com.columncalcrest.util.GaussLegendre;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;


public class BarFiniteElement {
    // This class represents 1D Elements of the Finite Element Method

    // Represents the initial node of the 1D element of the structure
    private NodeFiniteElement initialNode;

    // Represents the final node of the 1D element of the structure
    private NodeFiniteElement finalNode;

    // Represents the length of the 1D element of the structure
    private double length;

    // Represents the unique id of the 1D element of the structure
    private int id;

    // Represents the cross section of the 1D element of the structure
    private CrossSection crossSection;

    // Represents the stiffness matrix of the 1D element of the structure
    private RealMatrix stiffnessMatrix;

    // Represents the loads applied on the 1D elements of the scruture
    private RealVector uniformLoads;

    // Uniform loads after transformation become equivalent nodal loads
    private RealVector equivalentNodalLoads;

    // Represents the non linear nodal forces
    private RealVector nonLinearNodalForces;


    // TODO: VARIABLES MISSING

    public BarFiniteElement(NodeFiniteElement initialNode, NodeFiniteElement finalNode,
                            double length, int id, CrossSection crossSection) {

        this.initialNode = initialNode;
        this.finalNode = finalNode;
        this.crossSection = crossSection;
        this.length = length;
        this.id = id;
        this.uniformLoads = new ArrayRealVector(5);
        this.nonLinearNodalForces = new ArrayRealVector(10);
    }

    public void setStiffnessMatrix() {
        double youngModulus = this.crossSection.getConcreteClass().getYoungModulus() * 10000;
        double xInertialMoment =  this.crossSection.getXInertialMoment() / Math.pow(100, 4);
        double yInertialMoment = this.crossSection.getYInertialMoment() / Math.pow(100, 4);
        double area = this.crossSection.getTotalArea() / Math.pow(100, 2);

        double c1 = youngModulus * area / this.length;
        double c2 = 12 * youngModulus * xInertialMoment / Math.pow(this.length, 3);
        double c3 = c2 * this.length / 2;
        double c4 = c2 * Math.pow(this.length, 2) / 3;
        double c5 = c4 / 2;
        double c6 = 12 * youngModulus * yInertialMoment / Math.pow(this.length, 3);
        double c7 = c6 * this.length / 2;
        double c8 = c6 * Math.pow(this.length, 2) / 3;
        double c9 = c8 / 2;

        this.stiffnessMatrix = MatrixUtils.createRealMatrix(new double[][]{
                {c1, 0, 0, 0, 0, -c1, 0, 0, 0, 0},
                {0, c2, c3, 0, 0, 0, -c2, c3, 0, 0},
                {0, c3, c4, 0, 0, 0, -c3, c5, 0, 0},
                {0, 0, 0, c6, c7, 0, 0, 0, -c6, c7},
                {0, 0, 0, c7, c8, 0, 0, 0, -c7, c9},
                {-c1, 0, 0, 0, 0, c1, 0, 0, 0, 0},
                {0, -c2, -c3, 0, 0, 0, c2, -c3, 0, 0},
                {0, c3, c5, 0, 0, 0, -c3, c4, 0, 0},
                {0, 0, 0, -c6, -c7, 0, 0, 0, c6, -c7},
                {0, 0, 0, c7, c9, 0, 0, 0, -c7, c8}
        });
    }

    public void calculateEquivalentNodalLoads() {
        // Transforms linear loads distributed along the 1D element in equivalent
        // nodal loads

        double zLoad = this.uniformLoads.getEntry(0);

        double[] yCoefficients = FitLinearPolynomialFunction.fit(0, this.length,
                this.uniformLoads.getEntry(1), this.uniformLoads.getEntry(2));

        double[] xCoefficients = FitLinearPolynomialFunction.fit(0, this.length,
                this.uniformLoads.getEntry(3), this.uniformLoads.getEntry(4));

        double halfLength = this.length/2;

        ArrayList<Double> zPositionsArray = new ArrayList<>();
        GaussLegendre.VALUES.forEach((legendreValue) -> {
            double zPosition = halfLength * legendreValue + halfLength;
            zPositionsArray.add(zPosition);
        });

        this.equivalentNodalLoads = new ArrayRealVector(new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        for (int degreeOfFreedomIndex = 0; degreeOfFreedomIndex < 10; degreeOfFreedomIndex++) {
            switch (degreeOfFreedomIndex) {
                case 0:
                case 5:
                    this.axialNodalLoadsAuxFunction(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            zLoad, degreeOfFreedomIndex, zPositionsArray);
                    break;
                case 1:
                case 2:
                case 6:
                case 7:
                    this.transversalNodalLoadsAuxFunction(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            yCoefficients, degreeOfFreedomIndex, zPositionsArray);
                    break;
                case 3:
                case 4:
                case 8:
                case 9:
                    this.transversalNodalLoadsAuxFunction(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            xCoefficients, degreeOfFreedomIndex, zPositionsArray);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }

    public double[] calcAndGetDisplacements(double zPosition) {
        // Calculates and returns axial and transversal displacements on zy and zx
        // planes at a given zPosition along the 1D element

        ArrayList<Double> phiAuxParameters = new ArrayList<>();
        ArrayList<ArrayList<Double>> phiAuxParamLists = new ArrayList<>();
        phiAuxParamLists.add(phiAuxParameters);

        this.PhiParamsCalcAuxFunction(zPosition, phiAuxParamLists, this::useShapeFunctions);

        double axialDisplacement = phiAuxParameters.get(0) + phiAuxParameters.get(5);
        double yTransversalDisplacement = phiAuxParameters.get(1) + phiAuxParameters.get(2) +
                phiAuxParameters.get(6) + phiAuxParameters.get(7);
        double xTransversalDisplacements = phiAuxParameters.get(3) + phiAuxParameters.get(4) +
                phiAuxParameters.get(8) + phiAuxParameters.get(9);

        return new double[]{axialDisplacement, yTransversalDisplacement, xTransversalDisplacements};
    }

    public double[] calcAndGetGeometricNLParameters(double zPosition) {
        // Calculates and returns parameters need for Geometric non linearity calculations

        ArrayList<Double> phiDerivAuxParameters = new ArrayList<>();
        ArrayList<ArrayList<Double>> phiAuxParamLists = new ArrayList<>();
        phiAuxParamLists.add(phiDerivAuxParameters);

        this.PhiParamsCalcAuxFunction(zPosition, phiAuxParamLists, this::useDerivativeShapeFunction);

        double dwyParam = phiDerivAuxParameters.get(1) + phiDerivAuxParameters.get(2) +
                phiDerivAuxParameters.get(6) + phiDerivAuxParameters.get(7);
        double dwxParam = phiDerivAuxParameters.get(3) + phiDerivAuxParameters.get(4) +
                phiDerivAuxParameters.get(8) + phiDerivAuxParameters.get(9);

        return new double[]{dwyParam, dwxParam};
    }

    public double[] calcAndGetStrainAndCurvatures(double zPosition) {
        // Calculates and returns axial strain and curvatures on zy and zx
        // planes at a given zPosition along the 1D element

        ArrayList<Double> phiDerivAuxParameters = new ArrayList<>();
        ArrayList<Double> phiSecondDerivAuxParameters = new ArrayList<>();
        ArrayList<ArrayList<Double>> phiAuxParamLists = new ArrayList<>();
        phiAuxParamLists.add(phiDerivAuxParameters);
        phiAuxParamLists.add(phiSecondDerivAuxParameters);

        this.PhiParamsCalcAuxFunction(zPosition, phiAuxParamLists, this::useDerivativeShapeFunction, this::useSecondDerivativeShapeFunction);

        double duoAuxParam = phiDerivAuxParameters.get(0) + phiDerivAuxParameters.get(5);
        double dwyAuxParam = phiDerivAuxParameters.get(1) + phiDerivAuxParameters.get(2) +
                phiDerivAuxParameters.get(6) + phiDerivAuxParameters.get(7);
        double dwxAuxParam = phiDerivAuxParameters.get(3) + phiDerivAuxParameters.get(4) +
                phiDerivAuxParameters.get(8) + phiDerivAuxParameters.get(9);
        double dwyDerivAuxParam = phiSecondDerivAuxParameters.get(1) + phiSecondDerivAuxParameters.get(2) +
                phiSecondDerivAuxParameters.get(6) + phiSecondDerivAuxParameters.get(7);
        double dwxDerivAuxParam = phiSecondDerivAuxParameters.get(3) + phiSecondDerivAuxParameters.get(4) +
                phiSecondDerivAuxParameters.get(8) + phiSecondDerivAuxParameters.get(9);

        double axialStrain = duoAuxParam + 0.5 * (Math.pow(dwyAuxParam, 2) + Math.pow(dwxAuxParam, 2));
        double yCurvature = -dwyDerivAuxParam;
        double xCurvature = -dwxDerivAuxParam;

        return new double[]{axialStrain, yCurvature, xCurvature};
    }

    public void PhiParamsCalcAuxFunction(double zPosition, ArrayList<ArrayList<Double>> phiAuxParamLists,
                                         BiFunction<Integer, Double, Double>... shapeFunctions) {
        // Aux function that fills the phiAuxParamsList

        if (phiAuxParamLists.size() != shapeFunctions.length || phiAuxParamLists.size() > 2) {
            throw new RuntimeException("Parameters list and shapeFunctions list should be the same size (max 2)");
        }

        double[] auxInitialDisplacements = this.initialNode.getNodalDisplacements().toArray();
        double[] auxFinalDisplacements = this.finalNode.getNodalDisplacements().toArray();

        ArrayList<Double> initialNodeDisplacements = new ArrayList<>();
        ArrayList<Double> finalNodeDisplacements = new ArrayList<>();

        for(double displ : auxInitialDisplacements) initialNodeDisplacements.add(displ);
        for(double displ : auxFinalDisplacements) finalNodeDisplacements.add(displ);

        AtomicInteger degreeOfFreedomCounter = new AtomicInteger();
        initialNodeDisplacements.forEach((displacement) -> {
            for (int i = 0; i < phiAuxParamLists.size(); i++) {
                phiAuxParamLists.get(i).add(shapeFunctions[i].apply(degreeOfFreedomCounter.get(), zPosition)
                        * displacement);
            }
            degreeOfFreedomCounter.getAndIncrement();
        });
        finalNodeDisplacements.forEach((displacement) -> {
            for (int i = 0; i < phiAuxParamLists.size(); i++) {

                phiAuxParamLists.get(i).add(shapeFunctions[i].apply(degreeOfFreedomCounter.get(), zPosition)
                        * displacement);
            }
            degreeOfFreedomCounter.getAndIncrement();
        });
    }

    private void axialNodalLoadsAuxFunction(ArrayList<Double> legendreValues, ArrayList<Double> legendeWeights,
                                                   double zLoad, int degreeOfFreedomIndex, ArrayList<Double> zPositionsArray) {
        // Aux Function responsible for converting linear loads into equivalent nodal loads

        RealVector auxVector = new ArrayRealVector(this.equivalentNodalLoads.getDimension());

        for (int legendrePoint = 0; legendrePoint < legendreValues.size(); legendrePoint++) {
            double legendreIteration = legendeWeights.get(legendrePoint) *
                    (zLoad * useShapeFunctions(degreeOfFreedomIndex, zPositionsArray.get(legendrePoint)));
            auxVector.addToEntry(degreeOfFreedomIndex, legendreIteration);
        }
        this.equivalentNodalLoads.setEntry(degreeOfFreedomIndex,
                auxVector.getEntry(degreeOfFreedomIndex) * this.length/2);

    }

    private void transversalNodalLoadsAuxFunction(ArrayList<Double> legendreValues, ArrayList<Double> legendeWeights,
                                                  double[] axisCoefficients, int degreeOfFreedomIndex, ArrayList<Double> zPositionsArray) {
        // Aux Function responsible for converting linear loads into equivalent nodal loads

        RealVector auxVector = new ArrayRealVector(this.equivalentNodalLoads.getDimension());

        for (int legendrePoint = 0; legendrePoint < legendreValues.size(); legendrePoint++) {
            double legendreIteration = legendeWeights.get(legendrePoint) *
                    (linearPolynomialCalc(zPositionsArray.get(legendrePoint), axisCoefficients[0], axisCoefficients[1])) *
                    (useShapeFunctions(degreeOfFreedomIndex, zPositionsArray.get(legendrePoint)));
            auxVector.addToEntry(degreeOfFreedomIndex, legendreIteration);
        }
        this.equivalentNodalLoads.setEntry(degreeOfFreedomIndex,
                auxVector.getEntry(degreeOfFreedomIndex) * this.length/2);
    }

    public double linearPolynomialCalc(double z, double a, double b) {
        return (a * z + b);
    }

    public double useShapeFunctions(int degreeOfFreedomIndex, double zPosition) {
        // Returns the result of applying the shape functions to interpolate
        // displacements along the length of the 1D element based on nodal displacements
        // Degrees of freedom are numbered 0 through 9:
        // 0: axial displacement (z) of initial node
        // 1: transversal displacement in zy plane of initial node
        // 2: initial node rotation on zy plane
        // 3: transversal displacement in zx plane of initial node
        // 4: initial node rotation on zx plane
        // 5: axial displacement (z) of final node
        // 6: transversal displacement in zy plane of final node
        // 7: final node rotation on zy plane
        // 8: transversal displacement in zx plane of final node
        // 9: final node rotation on zx plane

        double relPosition = zPosition/this.length;
        double result;

        switch(degreeOfFreedomIndex) {
            case 0:
                result = 1 - relPosition;
                break;
            case 1:
            case 3:
                result = 2 * Math.pow(relPosition, 3) - 3 * Math.pow(relPosition, 2) + 1;
                break;
            case 2:
            case 4:
                result = this.length *
                        (Math.pow(relPosition, 3) - 2 * Math.pow(relPosition, 2) + relPosition);
                break;
            case 5:
                result = relPosition;
                break;
            case 6:
            case 8:
                result = - 2 * Math.pow(relPosition, 3) + 3 * Math.pow(relPosition, 2);
                break;
            case 7:
            case 9:
                result = this.length * (Math.pow(relPosition, 3) - Math.pow(relPosition, 2));
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public double useDerivativeShapeFunction(int degreeOfFreedomIndex, double zPosition) {
        // Returns the result of applying the first derivative of the shape functions
        // degrees of freedom are numbered 0 through 9

        double result;

        switch(degreeOfFreedomIndex) {
            case 0:
                result = - (1 / this.length);
                break;
            case 1:
            case 3:
                result = (6 * zPosition * (zPosition - this.length)) / Math.pow(this.length, 3);
                break;
            case 2:
            case 4:
                result = (Math.pow(this.length, 2) - 4 * this.length * zPosition + 3 * Math.pow(zPosition, 2)) /
                Math.pow(this.length, 2);
                break;
            case 5:
                result = 1 / this.length;
                break;
            case 6:
            case 8:
                result = (6 * zPosition * (this.length - zPosition)) / Math.pow(this.length, 3);
                break;
            case 7:
            case 9:
                result = (zPosition * (3 * zPosition - 2 * this.length)) / Math.pow(this.length, 2);
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public double useSecondDerivativeShapeFunction(int degreeOfFreedomIndex, double zPosition) {
        // Returns the result of applying the second derivative of the shape functions
        // degrees of freedom are numbered 0 through 9

        double result;

        switch(degreeOfFreedomIndex) {
            case 0:
            case 5:
                result = 0;
                break;
            case 1:
            case 3:
                result  = - (6 * (this.length - 2 * zPosition)) / Math.pow(this.length, 3);
                break;
            case 2:
            case 4:
                result = (6 * zPosition - 4 * this.length) / Math.pow(this.length, 2);
                break;
            case 6:
            case 8:
                result = (6 * (this.length - 2 * zPosition)) / Math.pow(this.length, 3);
                break;
            case 7:
            case 9:
                result = - (2 * (this.length - 3 * zPosition)) / Math.pow(this.length, 2);
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }

    public void calcNonLinearForces() {
        // Calculates the non linear forces acting on the 1D element nodes

        double halfLength = this.length/2;


        ArrayList<Double> zPositionsArray = new ArrayList<>();
        GaussLegendre.VALUES.forEach((legendreValue) -> {
            double zPosition = halfLength * legendreValue + halfLength;
            zPositionsArray.add(zPosition);
        });


        ArrayList<Double> auxVectorA = new ArrayList<>();
        ArrayList<Double> auxVectorB = new ArrayList<>();

        double kAuxParam = Math.sqrt(3.0/5.0);
        double firstEvalPoint = -kAuxParam * this.length + halfLength;
        double secondAvalPoint = halfLength;
        double thirdEvalPoint = kAuxParam * this.length + halfLength;

        ArrayList<Double> evaluationPoints = new ArrayList<>(Arrays.asList(firstEvalPoint, secondAvalPoint, thirdEvalPoint));
        ArrayList<Double> gaussPointsNormalForces = new ArrayList<>();
        ArrayList<Double> gaussPointsYBendingMoments = new ArrayList<>();
        ArrayList<Double> gaussPointsXBendingMoments = new ArrayList<>();


        evaluationPoints.forEach((evalPoint) -> {
            double[] strainAndCurvatures = calcAndGetStrainAndCurvatures(evalPoint);
            double[] internalForces = this.crossSection.calcInternalForces(strainAndCurvatures[0],
                                                                            strainAndCurvatures[1],
                                                                            strainAndCurvatures[2],
                                                                            true);

            gaussPointsNormalForces.add(internalForces[0]);
            gaussPointsYBendingMoments.add(internalForces[1]);
            gaussPointsXBendingMoments.add(internalForces[2]);
        });

        zPositionsArray.forEach((zPosition) -> {
            double[] geometricNLParams = calcAndGetGeometricNLParameters(zPosition);
            auxVectorA.add(geometricNLParams[0]);
            auxVectorB.add(geometricNLParams[1]);
        });

        for (int degreeOfFreedomIndex = 0; degreeOfFreedomIndex < 10; degreeOfFreedomIndex++) {
            switch (degreeOfFreedomIndex) {
                case 0:
                case 5:
                    this.axialNonLinearAuxFunction(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            gaussPointsNormalForces, degreeOfFreedomIndex, zPositionsArray);
                    break;
                case 1:
                case 2:
                case 6:
                case 7:
                    this.transversalNonLinearAuxFunctions(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            gaussPointsNormalForces, degreeOfFreedomIndex, zPositionsArray,
                            gaussPointsYBendingMoments, auxVectorA);
                    break;
                case 3:
                case 4:
                case 8:
                case 9:
                    this.transversalNonLinearAuxFunctions(GaussLegendre.VALUES, GaussLegendre.WEIGHTS,
                            gaussPointsNormalForces, degreeOfFreedomIndex, zPositionsArray,
                            gaussPointsXBendingMoments, auxVectorB);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }
    private void axialNonLinearAuxFunction(ArrayList<Double> legendreValues, ArrayList<Double> legendeWeights,
                                           ArrayList<Double> gaussPointsNormalForces, int degreeOfFreedomIndex,
                                           ArrayList<Double> zPositionsArray) {
        // Aux Function responsible for calculating non linear forces on nodes

        RealVector sumVector = new ArrayRealVector(this.nonLinearNodalForces.getDimension());

        for (int legendrePoint = 0; legendrePoint < legendreValues.size(); legendrePoint++) {
            double legendreIteration = legendeWeights.get(legendrePoint) *
                    (gaussPointsNormalForces.get(legendrePoint) *
                            useDerivativeShapeFunction(degreeOfFreedomIndex, zPositionsArray.get(legendrePoint)));
            sumVector.addToEntry(degreeOfFreedomIndex, legendreIteration);
        }
        this.nonLinearNodalForces.setEntry(degreeOfFreedomIndex,
                sumVector.getEntry(degreeOfFreedomIndex) * this.length/2);
    }

    private void transversalNonLinearAuxFunctions(ArrayList<Double> legendreValues, ArrayList<Double> legendeWeights,
                                                  ArrayList<Double> gaussPointsNormalForces, int degreeOfFreedomIndex,
                                                  ArrayList<Double> zPositionsArray, ArrayList<Double> gaussPointsAxisMoments,
                                                  ArrayList<Double> auxVector) {
        // Aux Function responsible for calculating non linear forces on nodes

        RealVector sumVector = new ArrayRealVector(this.nonLinearNodalForces.getDimension());

        for (int legendrePoint = 0; legendrePoint < legendreValues.size(); legendrePoint++) {

            double firstExpressionIntegration = legendeWeights.get(legendrePoint) *
                    (- gaussPointsAxisMoments.get(legendrePoint) *
                            useSecondDerivativeShapeFunction(degreeOfFreedomIndex, zPositionsArray.get(legendrePoint)));
            double secondExpressionIntegration = legendeWeights.get(legendrePoint) *
                    (gaussPointsNormalForces.get(legendrePoint) * auxVector.get(legendrePoint) *
                            useDerivativeShapeFunction(degreeOfFreedomIndex, zPositionsArray.get(legendrePoint)));

            sumVector.addToEntry(degreeOfFreedomIndex,
                    (firstExpressionIntegration + secondExpressionIntegration));
        }
        this.nonLinearNodalForces.setEntry(degreeOfFreedomIndex,
                sumVector.getEntry(degreeOfFreedomIndex) * this.length/2);
    }

    public boolean checkCrossSectionIsFailed() throws ConcreteFailedException, RebarFailedException {
        return this.crossSection.checkIsFailed();
    }

    public NodeFiniteElement getInitialNode() {
        return initialNode;
    }

    public NodeFiniteElement getFinalNode() {
        return finalNode;
    }

    public RealMatrix getStiffnessMatrix() {
        return stiffnessMatrix;
    }

    public RealVector getEquivalentNodalLoads() {
        return equivalentNodalLoads;
    }

    public RealVector getNonLinearNodalForces() {
        return nonLinearNodalForces;
    }

    public double getLength() {
        return length;
    }

    public void setUniformLoads(RealVector uniformLoads) {
        this.uniformLoads = uniformLoads;
    }
}
