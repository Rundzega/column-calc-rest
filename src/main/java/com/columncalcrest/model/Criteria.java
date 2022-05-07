package com.columncalcrest.model;

import com.columncalcrest.exception.InvalidColumnInput;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Criteria {

    @Positive
    @NotNull
    private int finiteElementsNumber;

    @Positive
    @NotNull
    private int xDiscretizationsNumber;

    @Positive
    @NotNull
    private int yDiscretizationsNumber;

    @Min(45)
    @NotNull
    private int diagramPointsNumber;

    @Positive
    @NotNull
    private int loadIncrementsNumber;

    @Positive
    @NotNull
    private int maxIterationsPerIncrement;

    @Positive
    @NotNull
    @DecimalMax("0.01")
    private double displacementsTolerance;

    @Positive
    @NotNull
    @DecimalMax("0.01")
    private double forcesTolerance;

    @Positive
    @NotNull
    @DecimalMax("0.01")
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

//        if (finiteElementsNumber <= 0) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: Número de elementos finitos deve ser maior do que 0");
//        }
//        if (xDiscretizationsNumber <= 0 || yDiscretizationsNumber <= 0) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: Número de elementos discretizados nas direções" +
//                    "X e Y da Seção Transversal deve ser maior do que 0");
//        }
//        if (diagramPointsNumber < 45) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: Número de pontos do diagrama N-Mx-My deve ser no mínimo 45");
//        }
//        if (loadIncrementsNumber <= 0) {
//            throw new InvalidColumnInput("Critérios de cálculo invállidos: Número de incrementos de cargas deve ser maior do que 0");
//        }
//        if (maxIterationsPerIncrement <= 0) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: Número máximo de iterações por incremento de carga deve ser maior do que 0");
//        }
//        if (displacementsTolerance <= 0 || displacementsTolerance > 0.01) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: A tolerância de cálculo dos deslocamentos do pilar deve estar entre 0 e 1%");
//        }
//        if (forcesTolerance <= 0 || forcesTolerance > 0.01) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: A tolerância de cálculo das forças no pilar deve estar entre 0 e 1%");
//        }
//        if (neutralAxisDepthTolerance <= 0 || neutralAxisDepthTolerance > 0.01) {
//            throw new InvalidColumnInput("Critérios de cálculo inválidos: A tolerância de cálculo para obtenção da profundidade da linha neutra deve estar entre 0 e 1%");
//        }

        this.finiteElementsNumber = finiteElementsNumber;
        System.out.println(finiteElementsNumber);
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
