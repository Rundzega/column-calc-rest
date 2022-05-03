package com.columncalcrest.dto;

import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.InvalidColumnInput;
import com.columncalcrest.exception.MaxIterationsExceededException;
import com.columncalcrest.exception.RebarFailedException;
import com.columncalcrest.model.Criteria;
import com.columncalcrest.model.Column;
import com.columncalcrest.model.CrossSection;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.ArrayList;
import java.util.Collections;

public class ColumnResults {

    @JsonProperty
    private boolean isResultsAvailable;
    @JsonProperty
    private ArrayList<Double> uyDisplacements;
    @JsonProperty
    private ArrayList<Double> uxDisplacements;
    @JsonProperty
    private ArrayList<Double> uzDisplacements;
    @JsonProperty
    private ArrayList<Double> ndForces;
    @JsonProperty
    private ArrayList<Double> mxForces;
    @JsonProperty
    private ArrayList<Double> myForces;
    @JsonProperty
    private ArrayList<Double> lengthPoints;
    @JsonProperty
    private ArrayList<double[]> ndMinResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<double[]> ndMaxResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<double[]> mxMinResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<double[]> mxMaxResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<double[]> myMinResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<double[]> myMaxResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<Double> anglesResistanceDiagramPoints;
    @JsonProperty
    private ArrayList<Double> ndMinSolicitingForces;
    @JsonProperty
    private ArrayList<Double> ndMaxSolicitingForces;
    @JsonProperty
    private ArrayList<Double> mxMinSolicitingForces;
    @JsonProperty
    private ArrayList<Double> mxMaxSolicitingForces;
    @JsonProperty
    private ArrayList<Double> myMinSolicitingForces;
    @JsonProperty
    private ArrayList<Double> myMaxSolicitingForces;

    public ColumnResults(Column column,
                         CrossSection crossSection,
                         Criteria criteria) {

        this.isResultsAvailable = true;
        this.uzDisplacements = column.getUzDisplacements();
        this.uxDisplacements = column.getUxDisplacements();
        this.uyDisplacements = column.getUyDisplacements();
        this.mxForces = column.getMxForces();
        this.myForces = column.getMyForces();
        this.ndForces = column.getNdForces();
        this.mxForces = column.getMxForces();
        this.lengthPoints = column.getLengthPoints();


        int ndMinIndex = this.ndForces.indexOf(Collections.min(this.ndForces));
        int mxMinIndex = this.mxForces.indexOf(Collections.min(this.mxForces));
        int myMinIndex = this.myForces.indexOf(Collections.min(this.myForces));
        int ndMaxIndex = this.ndForces.indexOf(Collections.max(this.ndForces));
        int mxMaxIndex = this.mxForces.indexOf(Collections.max(this.mxForces));
        int myMaxIndex = this.myForces.indexOf(Collections.max(this.myForces));

        this.ndMinResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                ndMinIndex, this.ndForces, this.mxForces, this.myForces);

        this.ndMaxResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                ndMaxIndex, this.ndForces, this.mxForces, this.myForces);

        this.mxMinResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                mxMinIndex, this.ndForces, this.mxForces, this.myForces);

        this.mxMaxResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                mxMaxIndex, this.ndForces, this.mxForces, this.myForces);

        this.myMinResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                myMinIndex, this.ndForces, this.mxForces, this.myForces);

        this.myMaxResistanceDiagramPoints = column.getResistanceDiagramPoints(crossSection, criteria,
                myMaxIndex, this.ndForces, this.mxForces, this.myForces);

        this.anglesResistanceDiagramPoints = column.getAnglesResistanceDiagram(crossSection);

        this.ndMinSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, ndMinIndex);

        this.ndMaxSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, ndMaxIndex);

        this.mxMinSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, mxMinIndex);

        this.mxMaxSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, mxMaxIndex);

        this.myMinSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, myMinIndex);

        this.myMaxSolicitingForces = column.getSolicitingForcesPoints(this.mxForces, this.myForces, myMaxIndex);
    }

    public ColumnResults(SingularMatrixException ex) {
        this.isResultsAvailable = false;
    }

    public ColumnResults(MaxIterationsExceededException ex) {
        this.isResultsAvailable = false;
    }

    public ColumnResults(ConcreteFailedException ex) {
        this.isResultsAvailable = false;
    }

    public ColumnResults(RebarFailedException ex) {
        this.isResultsAvailable = false;
    }

    public ColumnResults(InvalidColumnInput ex) {
        this.isResultsAvailable = false;
    }

    public ArrayList<Double> getUyDisplacements() {
        return uyDisplacements;
    }

    public void setUyDisplacements(ArrayList<Double> uyDisplacements) {
        this.uyDisplacements = uyDisplacements;
    }

    public ArrayList<Double> getUxDisplacements() {
        return uxDisplacements;
    }

    public void setUxDisplacements(ArrayList<Double> uxDisplacements) {
        this.uxDisplacements = uxDisplacements;
    }

    public ArrayList<Double> getUzDisplacements() {
        return uzDisplacements;
    }

    public void setUzDisplacements(ArrayList<Double> uzDisplacements) {
        this.uzDisplacements = uzDisplacements;
    }

    public ArrayList<Double> getNdForces() {
        return ndForces;
    }

    public void setNdForces(ArrayList<Double> ndForces) {
        this.ndForces = ndForces;
    }

    public ArrayList<Double> getMxForces() {
        return mxForces;
    }

    public void setMxForces(ArrayList<Double> mxForces) {
        this.mxForces = mxForces;
    }

    public ArrayList<Double> getMyForces() {
        return myForces;
    }

    public void setMyForces(ArrayList<Double> myForces) {
        this.myForces = myForces;
    }

    public ArrayList<Double> getLengthPoints() {
        return lengthPoints;
    }

    public void setLengthPoints(ArrayList<Double> lengthPoints) {
        this.lengthPoints = lengthPoints;
    }

    public ArrayList<double[]> getNdMinResistanceDiagramPoints() {
        return ndMinResistanceDiagramPoints;
    }

    public void setNdMinResistanceDiagramPoints(ArrayList<double[]> ndMinResistanceDiagramPoints) {
        this.ndMinResistanceDiagramPoints = ndMinResistanceDiagramPoints;
    }

    public ArrayList<double[]> getNdMaxResistanceDiagramPoints() {
        return ndMaxResistanceDiagramPoints;
    }

    public void setNdMaxResistanceDiagramPoints(ArrayList<double[]> ndMaxResistanceDiagramPoints) {
        this.ndMaxResistanceDiagramPoints = ndMaxResistanceDiagramPoints;
    }

    public ArrayList<double[]> getMxMinResistanceDiagramPoints() {
        return mxMinResistanceDiagramPoints;
    }

    public void setMxMinResistanceDiagramPoints(ArrayList<double[]> mxMinResistanceDiagramPoints) {
        this.mxMinResistanceDiagramPoints = mxMinResistanceDiagramPoints;
    }

    public ArrayList<double[]> getMxMaxResistanceDiagramPoints() {
        return mxMaxResistanceDiagramPoints;
    }

    public void setMxMaxResistanceDiagramPoints(ArrayList<double[]> mxMaxResistanceDiagramPoints) {
        this.mxMaxResistanceDiagramPoints = mxMaxResistanceDiagramPoints;
    }

    public ArrayList<double[]> getMyMinResistanceDiagramPoints() {
        return myMinResistanceDiagramPoints;
    }

    public void setMyMinResistanceDiagramPoints(ArrayList<double[]> myMinResistanceDiagramPoints) {
        this.myMinResistanceDiagramPoints = myMinResistanceDiagramPoints;
    }

    public ArrayList<double[]> getMyMaxResistanceDiagramPoints() {
        return myMaxResistanceDiagramPoints;
    }

    public void setMyMaxResistanceDiagramPoints(ArrayList<double[]> myMaxResistanceDiagramPoints) {
        this.myMaxResistanceDiagramPoints = myMaxResistanceDiagramPoints;
    }

    public ArrayList<Double> getAnglesResistanceDiagramPoints() {
        return anglesResistanceDiagramPoints;
    }

    public void setAnglesResistanceDiagramPoints(ArrayList<Double> anglesResistanceDiagramPoints) {
        this.anglesResistanceDiagramPoints = anglesResistanceDiagramPoints;
    }

    public ArrayList<Double> getNdMinSolicitingForces() {
        return ndMinSolicitingForces;
    }

    public void setNdMinSolicitingForces(ArrayList<Double> ndMinSolicitingForces) {
        this.ndMinSolicitingForces = ndMinSolicitingForces;
    }

    public ArrayList<Double> getNdMaxSolicitingForces() {
        return ndMaxSolicitingForces;
    }

    public void setNdMaxSolicitingForces(ArrayList<Double> ndMaxSolicitingForces) {
        this.ndMaxSolicitingForces = ndMaxSolicitingForces;
    }

    public ArrayList<Double> getMxMinSolicitingForces() {
        return mxMinSolicitingForces;
    }

    public void setMxMinSolicitingForces(ArrayList<Double> mxMinSolicitingForces) {
        this.mxMinSolicitingForces = mxMinSolicitingForces;
    }

    public ArrayList<Double> getMxMaxSolicitingForces() {
        return mxMaxSolicitingForces;
    }

    public void setMxMaxSolicitingForces(ArrayList<Double> mxMaxSolicitingForces) {
        this.mxMaxSolicitingForces = mxMaxSolicitingForces;
    }

    public ArrayList<Double> getMyMinSolicitingForces() {
        return myMinSolicitingForces;
    }

    public void setMyMinSolicitingForces(ArrayList<Double> myMinSolicitingForces) {
        this.myMinSolicitingForces = myMinSolicitingForces;
    }

    public ArrayList<Double> getMyMaxSolicitingForces() {
        return myMaxSolicitingForces;
    }

    public void setMyMaxSolicitingForces(ArrayList<Double> myMaxSolicitingForces) {
        this.myMaxSolicitingForces = myMaxSolicitingForces;
    }
}
