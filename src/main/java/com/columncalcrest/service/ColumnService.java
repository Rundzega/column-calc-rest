package com.columncalcrest.service;

import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.MaxIterationsExceededException;
import com.columncalcrest.exception.RebarFailedException;
import com.columncalcrest.model.*;
import com.columncalcrest.dto.ColumnResults;
import com.columncalcrest.model.*;
import com.columncalcrest.util.FitLinearPolynomialFunction;
import com.columncalcrest.validation.CrossSectionValidation;
import com.columncalcrest.wrapper.ColumnWrapper;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.ArrayList;

public class ColumnService {


    private final ColumnResults columnResults;

    public ColumnService(ColumnWrapper columnWrapper) {

        Concrete concrete = columnWrapper.getConcrete();
        Steel steel = columnWrapper.getSteel();
        ArrayList<ConcreteRectangle> rectanglesList = columnWrapper.getRectangleList();

        ArrayList<Rebar> rebarsList = columnWrapper.getRebarList();

        CrossSectionValidation crossSectionValidation = new CrossSectionValidation();
        crossSectionValidation.validateRectanglesPosition(rectanglesList);
        crossSectionValidation.validateRebarsPosition(rebarsList, rectanglesList);

        Criteria criteria = columnWrapper.getCriteria();
        CrossSection crossSection = new CrossSection(rectanglesList, rebarsList, concrete,
                steel, criteria.getxDiscretizationsNumber(), criteria.getyDiscretizationsNumber());

        double columnLength = columnWrapper.getLength() / 100;
        BottomNodalLoads bottomNodalLoads = columnWrapper.getBottomNodalLoads();
        TopNodalLoads topNodalLoads = columnWrapper.getTopNodalLoads();
        NodalRestrictions bottomRestrictions = columnWrapper.getBottomNodalRestrictions();
        NodalRestrictions topRestrictions = columnWrapper.getTopNodalRestrictions();

        ArrayList<NodeFiniteElement> nodesList = this.createNodeFiniteElementsList(criteria, columnLength,
                bottomNodalLoads, topNodalLoads, bottomRestrictions, topRestrictions);

        ArrayList<BarFiniteElement> barsList = this.createFEMBarsList(criteria, columnLength,
                bottomNodalLoads, topNodalLoads, crossSection, nodesList);

        Column column = new Column(nodesList, barsList);


        column.nonLinearSolve(criteria.getForcesTolerance(), criteria.getDisplacementsTolerance(),
                criteria.getLoadIncrementsNumber(), criteria.getMaxIterationsPerIncrement());

        column.generateForcesDisplacementsResults(crossSection);

        this.columnResults = new ColumnResults(column, crossSection, criteria);
    }

    private ArrayList<NodeFiniteElement> createNodeFiniteElementsList (Criteria criteria,
                                               double columnLength,
                                               BottomNodalLoads bottomLoads,
                                               TopNodalLoads topLoads,
                                               NodalRestrictions bottomRestrictions,
                                               NodalRestrictions topRestrictions) {

        double finitieElementLength = columnLength / criteria.getFiniteElementsNumber();

        int numberOfFEM = criteria.getFiniteElementsNumber();
        NodeFiniteElement newNode;

        ArrayList<NodeFiniteElement> nodeFiniteElements = new ArrayList<>();

        for (int i = 0; i < numberOfFEM + 7; i++) {

            if (i < 4) {
                newNode = new NodeFiniteElement(i, i*finitieElementLength/4);
                if (i == 0) {
                    double bottomMxLoad = bottomLoads.getMx();
                    double bottomMyLoad = bottomLoads.getMy();

                    RealVector bottomLoadVector = new ArrayRealVector(new double[] {0, 0, -bottomMxLoad, 0, bottomMyLoad});
                    newNode.setNodalLoads(bottomLoadVector);

                    int ux = bottomRestrictions.isUx() ? 0 : 1;
                    int uy = bottomRestrictions.isUy() ? 0 : 1;
                    int uz = bottomRestrictions.isUz() ? 0 : 1;
                    int rx = bottomRestrictions.isRx() ? 0 : 1;
                    int ry = bottomRestrictions.isRy() ? 0 : 1;


                    RealVector newBottomRestrictions = new ArrayRealVector(new double[] {uz, uy, ux, rx, ry});
                    newNode.setNodalRestrictions(newBottomRestrictions);
                }
            } else if (i > numberOfFEM + 2) {
                newNode = new NodeFiniteElement(i,
                        (numberOfFEM - 2) * finitieElementLength + (i-numberOfFEM+2) * finitieElementLength/4);
                if (i == numberOfFEM + 6) {

                    double fz = topLoads.getFz();
                    double hx = topLoads.getHx();
                    double hy = topLoads.getHy();
                    double mx = topLoads.getMx();
                    double my = topLoads.getMy();

                    RealVector topLoadVector = new ArrayRealVector(new double[] {-fz, hy, -mx, hx, my});
                    newNode.setNodalLoads(topLoadVector);

                    int ux = topRestrictions.isUx() ? 0 : 1;
                    int uy = topRestrictions.isUy() ? 0 : 1;
                    int uz = topRestrictions.isUz() ? 0 : 1;
                    int rx = topRestrictions.isRx() ? 0 : 1;
                    int ry = topRestrictions.isRy() ? 0 : 1;


                    RealVector newTopRestrictions = new ArrayRealVector(new double[] {uz, uy, ux, rx, ry});
                    newNode.setNodalRestrictions(newTopRestrictions);
                }
            } else {
                newNode = new NodeFiniteElement(i, (i-3) * finitieElementLength);
            }


            nodeFiniteElements.add(newNode);
        }
        return nodeFiniteElements;
    }

    private ArrayList<BarFiniteElement> createFEMBarsList (Criteria criteria,
                                                  double columnLength,
                                                  BottomNodalLoads bottomLoads,
                                                  TopNodalLoads topLoads,
                                                  CrossSection crossSection,
                                                  ArrayList<NodeFiniteElement> nodesList) {

        double pxBottomLoad = bottomLoads.getPx();
        double pyBottomLoad = bottomLoads.getPy();
        double pxTopLoad = topLoads.getPx();
        double pyTopLoad = topLoads.getPy();

        double[] yCoefficients = FitLinearPolynomialFunction.fit(0, columnLength,
                pyBottomLoad, pyTopLoad);

        double[] xCoefficients = FitLinearPolynomialFunction.fit(0, columnLength,
                pxBottomLoad, pxTopLoad);

        int numberOfFEM = criteria.getFiniteElementsNumber();

        //TODO: MAKE THIS AN OPTION
        double columnWeight = - (crossSection.getTotalArea() * 0.000025) * 140;

        ArrayList<BarFiniteElement> barsList = new ArrayList<>();

        for (int i = 0; i < numberOfFEM + 6; i++) {
            double finalNodePosition = nodesList.get(i + 1).getzPosition();
            double initialNodePosition = nodesList.get(i).getzPosition();
            double barLength = finalNodePosition - initialNodePosition;

            BarFiniteElement newBar = new BarFiniteElement(nodesList.get(i),
                    nodesList.get(i + 1), barLength, i, crossSection);

            double pyBottomBar = yCoefficients[0] * initialNodePosition + yCoefficients[1];
            double pyTopBar = yCoefficients[0] * finalNodePosition + yCoefficients[1];
            double pxBottomBar = xCoefficients[0] * initialNodePosition + xCoefficients[1];
            double pxTopBar = xCoefficients[0] * finalNodePosition + xCoefficients[1];

            RealVector uniformLoads = new ArrayRealVector(new double[] {columnWeight,
                    pyBottomBar,
                    pyTopBar,
                    pxBottomBar,
                    pxTopBar});
            newBar.setUniformLoads(uniformLoads);

            barsList.add(newBar);
        }
        return barsList;
    }

    public ColumnResults getColumnResults() {
        return columnResults;
    }
}
