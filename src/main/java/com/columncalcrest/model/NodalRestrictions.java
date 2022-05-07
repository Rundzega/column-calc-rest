package com.columncalcrest.model;

import com.columncalcrest.exception.InvalidColumnInput;

import javax.validation.constraints.NotNull;

public class NodalRestrictions {

    @NotNull
    private boolean ux;

    @NotNull
    private boolean uy;

    @NotNull
    private boolean uz;

    @NotNull
    private boolean rx;

    @NotNull
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
