package com.columncalcrest.model;

import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.MaxIterationsExceededException;
import com.columncalcrest.exception.RebarFailedException;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.optim.MaxIter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class Column {
    // Column formed by n 1D Finite Elements


    // List containing all nodes of the column
    private final ArrayList<NodeFiniteElement> nodesList;

    // List containing all bars of the column
    private final ArrayList<BarFiniteElement> barsList;

    // Vector containing column's boundary conditions
    private RealVector boundaryConditionsVector;

    // Vector containing column's external forces
    private RealVector externalForcesVector;

    // Inverse global stiffness matrix
    private RealMatrix inverseStiffnessMatrix;

    // Load increment vector of each iteration
    private RealVector loadIncrement;

    // Load Vector of current iteration
    private RealVector currentIterationForcesVector;

    // Represents the displacements vector of previous iteration;
    private RealVector previousDisplacementsVector;

    // Represents the displacements vector of current iteration;
    private RealVector currentDisplacementsVector;

    // Represents the difference between the external forces vector and internal forces vector
    private RealVector imbalanceForcesVector;

    // Aux Vectors used to keep track of displacement vectors
    private RealVector firstAuxVector;

    private RealVector secondAuxVector;

    private RealVector thirdAuxVector;

    private RealVector fourthAuxVector;

    private ArrayList<Double> uyDisplacements;

    private ArrayList<Double> uxDisplacements;

    private ArrayList<Double> uzDisplacements;

    private ArrayList<Double> ndForces;

    private ArrayList<Double> myForces;

    private ArrayList<Double> mxForces;

    private ArrayList<Double> lengthPoints;


    public Column(ArrayList<NodeFiniteElement> nodesList, ArrayList<BarFiniteElement> barsList) {
        this.nodesList = nodesList;
        this.barsList = barsList;
        this.currentDisplacementsVector = new ArrayRealVector(5 * nodesList.size());
        this.imbalanceForcesVector = new ArrayRealVector(5 * nodesList.size());

    }

    public void firstLinearSolve(double numberOfLoadIncrements) {
        // Obtains the inverse Stiffnes Matrix to determine displacements due to
        // external forces vector, initializes the column, transforms linear loads into
        // equivalent nodal loads, applies boundary conditions and solves the linear system once
        // for the first load increment


        this.calcBoundaryConditionsAndExternalForces();
        int totalDegreesOfFreedom = this.nodesList.size() * 5;
        RealMatrix globalStiffnessMatrix = calcGlobalStiffnessMatrix(totalDegreesOfFreedom);
        this.calcInverselStiffnessMatrix(totalDegreesOfFreedom, globalStiffnessMatrix);


        this.externalForcesVector = this.externalForcesVector.ebeMultiply(this.boundaryConditionsVector);
        this.loadIncrement = this.externalForcesVector.mapDivide(numberOfLoadIncrements);
        this.currentIterationForcesVector = this.loadIncrement.copy();
        this.calcualteDisplacements(this.currentIterationForcesVector);
    }


    public void calcualteDisplacements(RealVector forceVectorNoBoundaryConditions) {
        this.previousDisplacementsVector = this.currentDisplacementsVector.copy();

        RealVector forcesVectorWithBoundaryConditions = forceVectorNoBoundaryConditions.ebeMultiply(this.boundaryConditionsVector);

        this.currentDisplacementsVector = this.currentDisplacementsVector
                .add(this.inverseStiffnessMatrix.preMultiply(forcesVectorWithBoundaryConditions));
        nodesList.forEach((node) -> {
            node.updateNodalDisplacements(this.currentDisplacementsVector);
        });
    }

    public void extrapolateDisplacements(int currentIncrement) {

        switch (currentIncrement) {
            case 1:
                this.firstAuxVector = this.currentDisplacementsVector.copy();
                break;
            case 2:
                this.secondAuxVector = this.firstAuxVector.copy();
                this.firstAuxVector = this.currentDisplacementsVector.copy();
                this.currentDisplacementsVector = this.firstAuxVector.mapMultiply(2.0).add(this.secondAuxVector);
                break;
            case 3:
                this.thirdAuxVector = this.secondAuxVector.copy();
                this.secondAuxVector = this.firstAuxVector.copy();
                this.firstAuxVector = this.currentDisplacementsVector.copy();
                this.currentDisplacementsVector = this.firstAuxVector.
                        mapMultiply(3.0).
                        subtract(this.secondAuxVector.mapMultiply(3.0)).
                        add(thirdAuxVector);
                break;
            default:
                this.fourthAuxVector = this.thirdAuxVector.copy();
                this.thirdAuxVector = this.secondAuxVector.copy();
                this.secondAuxVector = this.firstAuxVector.copy();
                this.firstAuxVector = this.currentDisplacementsVector.copy();
                this.currentDisplacementsVector = this.firstAuxVector.mapMultiply(4).
                        subtract(this.secondAuxVector.mapMultiply(6.0)).
                        add(this.thirdAuxVector.mapMultiply(4.0)).
                        subtract(this.fourthAuxVector);
        }
    }

    public boolean checkConvergence(double forcesTolerance, double displacementTolerance, int currentIteration) {
        double imbalanceForcesNorm = this.imbalanceForcesVector.getNorm();
        double externalForcesNorm = this.currentIterationForcesVector.getNorm();
        double displacementIncrementNorm = this.currentDisplacementsVector.
                subtract(this.previousDisplacementsVector).
                getNorm();
        double currentDisplacementsNorm = this.currentDisplacementsVector.getNorm();

        double forcesConvergence = imbalanceForcesNorm/externalForcesNorm;
        double displacementsConvergence = displacementIncrementNorm / currentDisplacementsNorm;






        return forcesConvergence <= forcesTolerance && displacementsConvergence <= displacementTolerance && currentIteration > 0;
    }

    public boolean nonLinearSolve(double forcesTolerance, double displacementsTolerance,
                                  int numberOfLoadIncrements, int maxNumberOfIterations) {

        firstLinearSolve(numberOfLoadIncrements);
        int currentLoadIncrement = 1;
        int currentLoadIteration = 0;
        int totalCounter = 0;

        while (currentLoadIteration <= maxNumberOfIterations) {
            RealVector globalNonLinearLoadVector = new ArrayRealVector(5 * this.nodesList.size());
            barsList.forEach((bar) -> {
                bar.calcNonLinearForces();
                int initialIndex = 5 * bar.getInitialNode().getId();
                int finalIndex = 5 * bar.getFinalNode().getId();
                for (int i = 0; i < 5; i++) {

                    globalNonLinearLoadVector.addToEntry(initialIndex + i,
                            bar.getNonLinearNodalForces().getEntry(i));
                    globalNonLinearLoadVector.addToEntry(finalIndex + i,
                            bar.getNonLinearNodalForces().getEntry(5 + i));
                }
            });
            RealVector globalNonLinearLoadVectorBoundaryConditions = this.boundaryConditionsVector.ebeMultiply(globalNonLinearLoadVector);

            this.imbalanceForcesVector = this.currentIterationForcesVector.subtract(globalNonLinearLoadVectorBoundaryConditions);


            if (this.checkConvergence(forcesTolerance, displacementsTolerance, currentLoadIteration)) {
                if (currentLoadIncrement == numberOfLoadIncrements) {
                    return true;
                }
                this.currentIterationForcesVector = this.currentIterationForcesVector.add(this.loadIncrement);
                this.imbalanceForcesVector = this.currentIterationForcesVector.subtract(globalNonLinearLoadVectorBoundaryConditions);

                //TODO: TURN ON OR OFF
                //extrapolateDisplacements(currentLoadIncrement);

                currentLoadIncrement++;
                currentLoadIteration = 0;
            }

            currentLoadIteration++;
            this.calcualteDisplacements(this.imbalanceForcesVector);
            totalCounter++;
        }
        throw new MaxIterationsExceededException("Max number of iterations exceeded without convergence");
    }


    private void calcBoundaryConditionsAndExternalForces() {

        ArrayList<Double> AuxBoundaryConditionsList = new ArrayList<>();
        ArrayList<Double> AuxExternalForcesList = new ArrayList<>();

        nodesList.forEach((node) -> {
            double[] auxNodeRestrictions = node.getNodalRestrictions().toArray();
            double[] auxNodalLoads = node.getNodalLoads().toArray();

            ArrayList<Double> auxNodeRestrictionsArr = new ArrayList<>();
            ArrayList<Double> auxNodeLoadsArr = new ArrayList<>();

            for(double condition : auxNodeRestrictions) auxNodeRestrictionsArr.add(condition);
            for(double force : auxNodalLoads) auxNodeLoadsArr.add(force);

            AuxBoundaryConditionsList.addAll(auxNodeRestrictionsArr);
            AuxExternalForcesList.addAll(auxNodeLoadsArr);
        });

        double[] auxBoundArray = AuxBoundaryConditionsList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] auxExternalArray = AuxExternalForcesList.stream().mapToDouble(Double::doubleValue).toArray();
        this.boundaryConditionsVector = new ArrayRealVector(auxBoundArray);
        this.externalForcesVector = new ArrayRealVector(auxExternalArray);

    }

    private RealMatrix calcGlobalStiffnessMatrix(int totalDegreesOfFreedom) {
        RealMatrix globalStiffnessMatrix = MatrixUtils.createRealMatrix(totalDegreesOfFreedom, totalDegreesOfFreedom);

        barsList.forEach((bar) -> {
            int iNodeFirstIndex = 5 * bar.getInitialNode().getId();
            int fNodeFirstIndex = 5 * bar.getFinalNode().getId();
            int iNodeLastIndex = iNodeFirstIndex + 5;
            int fNodeLastIndex = fNodeFirstIndex + 5;


            bar.setStiffnessMatrix();
            RealMatrix firstSubMatrix = bar.getStiffnessMatrix().getSubMatrix(0, 4, 0, 4);
            RealMatrix secondSubMatrix = bar.getStiffnessMatrix().getSubMatrix(0, 4, 5, 9);
            RealMatrix thirdSubMatrix = bar.getStiffnessMatrix().getSubMatrix(5, 9, 0, 4);
            RealMatrix fourthSubMatrix = bar.getStiffnessMatrix().getSubMatrix(5, 9, 5, 9);


            double[][] sumFirstSubAndGlobal = firstSubMatrix.add(
                    globalStiffnessMatrix.getSubMatrix(iNodeFirstIndex, iNodeLastIndex-1, iNodeFirstIndex, iNodeLastIndex-1)).getData();
            double[][] sumSecondSubAndGlobal = secondSubMatrix.add(
                    globalStiffnessMatrix.getSubMatrix(iNodeFirstIndex, iNodeLastIndex-1, fNodeFirstIndex, fNodeLastIndex-1)).getData();
            double[][] sumThirdSubAndGlobal = thirdSubMatrix.add(
                    globalStiffnessMatrix.getSubMatrix(fNodeFirstIndex, fNodeLastIndex-1, iNodeFirstIndex, iNodeLastIndex-1)).getData();
            double[][] sumFourthSubAndGlobal = fourthSubMatrix.add(
                    globalStiffnessMatrix.getSubMatrix(fNodeFirstIndex, fNodeLastIndex-1, fNodeFirstIndex, fNodeLastIndex-1)).getData();

            globalStiffnessMatrix.setSubMatrix(sumFirstSubAndGlobal, iNodeFirstIndex, iNodeFirstIndex);
            globalStiffnessMatrix.setSubMatrix(sumSecondSubAndGlobal, iNodeFirstIndex, fNodeFirstIndex);
            globalStiffnessMatrix.setSubMatrix(sumThirdSubAndGlobal, fNodeFirstIndex, iNodeFirstIndex);
            globalStiffnessMatrix.setSubMatrix(sumFourthSubAndGlobal, fNodeFirstIndex, fNodeFirstIndex);

            bar.calculateEquivalentNodalLoads();
            RealVector iForcesSubVector = this.externalForcesVector.getSubVector(iNodeFirstIndex, 5).
                    add(bar.getEquivalentNodalLoads().getSubVector(0, 5));
            RealVector fForcesSubVector = this.externalForcesVector.getSubVector(fNodeFirstIndex, 5).
                    add(bar.getEquivalentNodalLoads().getSubVector(5, 5));

            this.externalForcesVector.setSubVector(iNodeFirstIndex, iForcesSubVector);
            this.externalForcesVector.setSubVector(fNodeFirstIndex, fForcesSubVector);
        });
        return globalStiffnessMatrix;
    }

    private void calcInverselStiffnessMatrix(int totalDegreesOfFreedom, RealMatrix stiffnessMatrix) throws SingularMatrixException {
        for (int row = 0; row < totalDegreesOfFreedom; row++) {
            for (int column = 0; column < totalDegreesOfFreedom; column++) {
                if (this.boundaryConditionsVector.getEntry(row) == 0) {
                    if (row == column) {
                        stiffnessMatrix.setEntry(row, column, 1.0);
                    } else {
                        stiffnessMatrix.setEntry(row, column, 0.0);
                        stiffnessMatrix.setEntry(column, row, 0.0);
                    }
                }
            }
        }


        LUDecomposition matrixSolver = new LUDecomposition(stiffnessMatrix);

        this.inverseStiffnessMatrix = matrixSolver.getSolver().getInverse();
    }
    public void generateForcesDisplacementsResults(CrossSection crossSection) {

        int arraysLength = this.nodesList.size() + this.barsList.size();

        this.uyDisplacements = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.uyDisplacements, 0.0);
        this.uxDisplacements = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.uxDisplacements, 0.0);
        this.uzDisplacements = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.uzDisplacements, 0.0);
        this.ndForces = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.ndForces, 0.0);
        this.myForces = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.myForces, 0.0);
        this.mxForces = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.mxForces, 0.0);
        this.lengthPoints = new ArrayList<>(Arrays.asList(new Double[arraysLength]));
        Collections.fill(this.lengthPoints, 0.0);


        this.barsList.forEach(bar -> {
            int nodeIndex = bar.getInitialNode().getId() * 2;
            ArrayList<Double> globalZpoints = new ArrayList<>(Arrays.asList(
                    bar.getInitialNode().getzPosition(),
                    bar.getInitialNode().getzPosition() + bar.getLength() / 2,
                    bar.getFinalNode().getzPosition()
            ));

            ArrayList<Double> localZpoints = new ArrayList<>(Arrays.asList(
                    0.0, 0.5 * bar.getLength(), bar.getLength()
            ));

            AtomicInteger localPointIndex = new AtomicInteger();

            localZpoints.forEach((point) -> {
                double[] displacements = bar.calcAndGetDisplacements(point);
                double[] strains = bar.calcAndGetStrainAndCurvatures(point);
                double[] internalForces = crossSection.calcInternalForces(strains[0],
                        strains[1], strains[2], true);

                int arrayIndex = nodeIndex + localPointIndex.get();
                double uzDisplacement = displacements[0];
                double uyDisplacement = displacements[1];
                double uxDisplacement = displacements[2];

                double ndForce = - internalForces[0];
                double mxForce = internalForces[1];
                double myForce = - internalForces[2];

                if ((nodeIndex == 0 && localPointIndex.get() == 2) ||
                        (((nodeIndex == ((this.nodesList.size() - 2) * 2))) && (localPointIndex.get() == 0)) ||
                        (nodeIndex != 0 && nodeIndex != ((this.nodesList.size() - 2) * 2) && localPointIndex.get() % 2 == 0)) {

                    this.uzDisplacements.set(arrayIndex, this.uzDisplacements.get(arrayIndex) + uzDisplacement / 2);
                    this.uyDisplacements.set(arrayIndex, this.uyDisplacements.get(arrayIndex) + uyDisplacement / 2);
                    this.uxDisplacements.set(arrayIndex, this.uxDisplacements.get(arrayIndex) + uxDisplacement / 2);
                    this.ndForces.set(arrayIndex, this.ndForces.get(arrayIndex) + ndForce / 2);
                    this.mxForces.set(arrayIndex, this.mxForces.get(arrayIndex) + mxForce / 2);
                    this.myForces.set(arrayIndex, this.myForces.get(arrayIndex) + myForce / 2);
                    this.lengthPoints.set(arrayIndex, this.lengthPoints.get(arrayIndex) + globalZpoints.get(localPointIndex.get()) / 2);

                } else {

                    this.uzDisplacements.set(arrayIndex, this.uzDisplacements.get(arrayIndex) + uzDisplacement);
                    this.uyDisplacements.set(arrayIndex, this.uyDisplacements.get(arrayIndex) + uyDisplacement);
                    this.uxDisplacements.set(arrayIndex, this.uxDisplacements.get(arrayIndex) + uxDisplacement);
                    this.ndForces.set(arrayIndex, this.ndForces.get(arrayIndex) + ndForce);
                    this.mxForces.set(arrayIndex, this.mxForces.get(arrayIndex) + mxForce);
                    this.myForces.set(arrayIndex, this.myForces.get(arrayIndex) + myForce);
                    this.lengthPoints.set(arrayIndex, this.lengthPoints.get(arrayIndex) + globalZpoints.get(localPointIndex.get()));
                }

                localPointIndex.incrementAndGet();
            });
        });
    }

    public boolean checkCrossSectionIsFailed() throws ConcreteFailedException, RebarFailedException {

        this.barsList.forEach(bar -> {
            boolean crossSectionIsFailed = bar.checkCrossSectionIsFailed();
        });
        return false;
    }

    public ArrayList<double[]> getResistanceDiagramPoints(CrossSection crossSection, Criteria criteria, int index,
                                                          ArrayList<Double> ndForces, ArrayList<Double> mxForces,
                                                          ArrayList<Double> myForces) {

        double neutralAxisDepthTolerance = criteria.getNeutralAxisDepthTolerance() > 10 ? criteria.getNeutralAxisDepthTolerance() : 0.001;
        int numberOfDiagramPoints = criteria.getDiagramPointsNumber();

        int stepAngle = 360/criteria.getDiagramPointsNumber();

        crossSection.setInternalMomentsDiagram(ndForces.get(index),
                neutralAxisDepthTolerance, stepAngle);

        return new ArrayList<>(crossSection.getInternalMomentsDiagram());
    }

    public ArrayList<Double> getAnglesResistanceDiagram(CrossSection crossSection) {

        return new ArrayList<>(crossSection.getGraphAngles());
    }

    public ArrayList<Double> getSolicitingForcesPoints(ArrayList<Double> mxForces, ArrayList<Double> myForces,
                                                       int index) {

        ArrayList<Double> solicitingForces = new ArrayList<>();
        solicitingForces.add(mxForces.get(index));
        solicitingForces.add(myForces.get(index));

        return solicitingForces;
    }

    public RealVector getCurrentDisplacementsVector() {
        return currentDisplacementsVector;
    }

    public ArrayList<Double> getUyDisplacements() {
        return uyDisplacements;
    }

    public ArrayList<Double> getLengthPoints() {
        return lengthPoints;
    }

    public ArrayList<Double> getUxDisplacements() {
        return uxDisplacements;
    }

    public ArrayList<Double> getUzDisplacements() {
        return uzDisplacements;
    }

    public ArrayList<Double> getNdForces() {
        return ndForces;
    }

    public ArrayList<Double> getMyForces() {
        return myForces;
    }

    public ArrayList<Double> getMxForces() {
        return mxForces;
    }


}
