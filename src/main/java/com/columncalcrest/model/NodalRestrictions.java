package com.columncalcrest.model;

public class NodalRestrictions {

    private boolean ux;

    private boolean uy;

    private boolean uz;

    private boolean rx;

    private boolean ry;

    public NodalRestrictions(boolean ux, boolean uy, boolean uz, boolean rx, boolean ry) {
        this.ux = ux;
        this.uy = uy;
        this.uz = uz;
        this.rx = rx;
        this.ry = ry;
    }

    public boolean isUx() {
        return ux;
    }

    public boolean isUy() {
        return uy;
    }

    public boolean isUz() {
        return uz;
    }

    public boolean isRx() {
        return rx;
    }

    public boolean isRy() {
        return ry;
    }
}
