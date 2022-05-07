package com.columncalcrest.model;

import javax.validation.constraints.NotNull;

public class TopNodalLoads {

    @NotNull
    private double fz;
    @NotNull
    private double hx;
    @NotNull
    private double hy;
    @NotNull
    private double px;
    @NotNull
    private double py;
    @NotNull
    private double mx;
    @NotNull
    private double my;

    public TopNodalLoads(double fz, double hx, double hy, double px, double py, double mx, double my) {
        this.fz = fz;
        this.hx = hx;
        this.hy = hy;
        this.px = px;
        this.py = py;
        this.mx = mx;
        this.my = my;
    }

    public double getFz() {
        return fz;
    }

    public double getHx() {
        return hx;
    }

    public double getHy() {
        return hy;
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
