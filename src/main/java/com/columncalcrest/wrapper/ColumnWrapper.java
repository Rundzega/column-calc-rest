package com.columncalcrest.wrapper;

import com.columncalcrest.model.*;
import com.columncalcrest.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ColumnWrapper {

    @JsonProperty("Length")
    private double length;

    @JsonProperty("Concrete")
    private Concrete concrete;

    @JsonProperty("Steel")
    private Steel steel;

    @JsonProperty("BottomRestrictions")
    private NodalRestrictions bottomRestrictions;

    @JsonProperty("TopRestrictions")
    private NodalRestrictions topRestrictions;

    @JsonProperty("BottomLoads")
    private BottomNodalLoads bottomLoads;

    @JsonProperty("TopLoads")
    private TopNodalLoads topLoads;

    @JsonProperty("Criteria")
    private Criteria criteria;

    @JsonProperty("RebarList")
    private ArrayList<Rebar> rebarList;

    @JsonProperty("RectangleList")
    private ArrayList<ConcreteRectangle> rectangleList;

    public ColumnWrapper(double length,
                         Concrete concrete,
                         Steel steel,
                         NodalRestrictions bottomRestrictions,
                         NodalRestrictions topRestrictions,
                         BottomNodalLoads bottomLoads,
                         TopNodalLoads topLoads,
                         Criteria criteria,
                         ArrayList<Rebar> rebarList,
                         ArrayList<ConcreteRectangle> rectangleList) {

        this.length = length;
        this.concrete = concrete;
        this.steel = steel;
        this.bottomRestrictions = bottomRestrictions;
        this.topRestrictions = topRestrictions;
        this.bottomLoads = bottomLoads;
        this.topLoads = topLoads;
        this.criteria = criteria;
        this.rebarList = rebarList;
        this.rectangleList = rectangleList;
    }

    public Concrete getConcrete() {
        return concrete;
    }

    public Steel getSteel() {
        return steel;
    }

    public double getLength() {
        return length;
    }

    public NodalRestrictions getBottomNodalRestrictions() {
        return bottomRestrictions;
    }

    public NodalRestrictions getTopNodalRestrictions() {
        return topRestrictions;
    }

    public BottomNodalLoads getBottomNodalLoads() {
        return bottomLoads;
    }

    public TopNodalLoads getTopNodalLoads() {
        return topLoads;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public ArrayList<Rebar> getRebarList() {
        return rebarList;
    }

    public ArrayList<ConcreteRectangle> getRectangleList() {
        return rectangleList;
    }
}
