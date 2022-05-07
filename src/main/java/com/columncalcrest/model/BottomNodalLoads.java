package com.columncalcrest.model;

import javax.validation.constraints.NotNull;

public class BottomNodalLoads {

    @NotNull
    private double px;

    @NotNull
    private double py;

    @NotNull
    private double mx;

    @NotNull
    private double my;

    public BottomNodalLoads(double px, double py, double mx, double my) {
        this.px = px;
        this.py = py;
        this.mx = mx;
        this.my = my;
    }

    public double getPx() {
        return px;
    }

    public double getPy() {
        return py;
    }

    public double getMx() {
        return mx;
    }

    public double getMy() {
        return my;
    }
}
