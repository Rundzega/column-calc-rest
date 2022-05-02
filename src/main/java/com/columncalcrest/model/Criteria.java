package com.columncalcrest.model;

public class Criteria {

    private int finiteElementsNumber;

    private int xDiscretizationsNumber;

    private int yDiscretizationsNumber;

    private int diagramPointsNumber;

    private int loadIncrementsNumber;

    private int maxIterationsPerIncrement;

    private double displacementsTolerance;

    private double forcesTolerance;

    private double neutralAxisDepthTolerance;

    public Criteria(int finiteElementsNumber,
                    int xDiscretizationsNumber,
                    int yDiscretizationsNumber,
                    int diagramPointsNumber,
                    int loadIncrementsNumber,
                    int maxIterationsPerIncrement,
                    double displacementsTolerance,
                    double forcesTolerance,
                    double neutralAxisDepthTolerance) {

        this.finiteElementsNumber = finiteElementsNumber;
        this.xDiscretizationsNumber = xDiscretizationsNumber;
        this.yDiscretizationsNumber = yDiscretizationsNumber;
        this.diagramPointsNumber = diagramPointsNumber;
        this.loadIncrementsNumber = loadIncrementsNumber;
        this.maxIterationsPerIncrement = maxIterationsPerIncrement;
        this.displacementsTolerance = displacementsTolerance;
        this.forcesTolerance = forcesTolerance;
        this.neutralAxisDepthTolerance = neutralAxisDepthTolerance;


    }

    public int getFiniteElementsNumber() {
        return finiteElementsNumber;
    }

    public int getxDiscretizationsNumber() {
        return xDiscretizationsNumber;
    }

    public int getyDiscretizationsNumber() {
        return yDiscretizationsNumber;
    }

    public int getDiagramPointsNumber() {
        return diagramPointsNumber;
    }

    public int getLoadIncrementsNumber() {
        return loadIncrementsNumber;
    }

    public int getMaxIterationsPerIncrement() {
        return maxIterationsPerIncrement;
    }

    public double getDisplacementsTolerance() {
        return displacementsTolerance;
    }

    public double getForcesTolerance() {
        return forcesTolerance;
    }

    public double getNeutralAxisDepthTolerance() {
        return neutralAxisDepthTolerance;
    }
}
