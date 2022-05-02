package com.columncalcrest.model;

public class TopNodalLoads {

    private double fz;

    private double hx;

    private double hy;

    private double px;

    private double py;

    private double mx;

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
