package com.columncalcrest.wrapper;

import com.columncalcrest.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;

public class ColumnWrapper {

    @JsonProperty("Length")
    @Positive
    private double length;

    @JsonProperty("Concrete")
    @NotNull
    @Valid
    private Concrete concrete;

    @JsonProperty("Steel")
    @NotNull
    @Valid
    private Steel steel;

    @JsonProperty("BottomRestrictions")
    @NotNull
    @Valid
    private NodalRestrictions bottomRestrictions;

    @JsonProperty("TopRestrictions")
    @NotNull
    @Valid
    private NodalRestrictions topRestrictions;

    @JsonProperty("BottomLoads")
    @NotNull
    @Valid
    private BottomNodalLoads bottomLoads;

    @JsonProperty("TopLoads")
    @NotNull
    @Valid
    private TopNodalLoads topLoads;

    @JsonProperty("Criteria")
    @NotNull
    @Valid
    private Criteria criteria;

    @JsonProperty("RebarList")
    @NotEmpty
    @Valid
    private ArrayList<Rebar> rebarList;

    @JsonProperty("RectangleList")
    @NotEmpty
    @Valid
    private ArrayList<ConcreteRectangle> rectangleList;

    public ColumnWrapper(double length,
                         @Valid Concrete concrete,
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
