package com.columncalcrest.model;

public class BottomNodalLoads {

    private double px;

    private double py;

    private double mx;

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
