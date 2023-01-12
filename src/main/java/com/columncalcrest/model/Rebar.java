package com.columncalcrest.model;

import com.columncalcrest.exception.InvalidColumnInput;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Rebar extends CrossSectionElement{

    @Positive
    @NotNull
    private final double diameter;
    // Cross-Section area of the rebar in cm2

    private final double area;

    // Indicates wheter rebar has reached limit strain
    private boolean rebarFailed;

    // Represents the compressive or tensile stress of the rebar
    private double steelStress;

    @NotNull
    @Valid
    private final Concrete concreteClass;

    @NotNull
    @Valid
    private final Steel steelClass;

    @NotNull
    private final double xCoord;

    @NotNull
    private final double yCoord;

    //diameter in mm
//    @JsonCreator
    public Rebar(Concrete concreteClass,
                 Steel steelClass,
                 double diameter,
                 double xCoord,
                 double yCoord) {

        super(concreteClass, steelClass, xCoord, yCoord);

        this.concreteClass = concreteClass;
        this.steelClass = steelClass;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.diameter = diameter;
        this.area = (Math.PI*Math.pow(diameter, 2))/400; //cm2
        this.rebarFailed = false;
    }

    public void calculateSteelStress() {
        // Calculates the stress in a rebar subject to strain in kN/cm2

        if (this.getStrain() <= this.getSteelClass().getYieldStrain()) {
            this.steelStress = this.getSteelClass().getYoungModulus() * this.getStrain();

            if(this.steelStress >= 0) {
                this.steelStress = Math.min(this.steelStress, this.getSteelClass().getFyd());
            }
            if(this.steelStress < 0) {
                this.steelStress = Math.max(this.steelStress, -this.getSteelClass().getFyd());
            }
        }
        else {
            this.rebarFailed = true;
            this.steelStress = 0;
        }
    }

    public double getDiameter() {
        return diameter;
    }

    @Override
    public Concrete getConcreteClass() {
        return concreteClass;
    }

    @Override
    public Steel getSteelClass() {
        return steelClass;
    }

    @Override
    public double getxCoord() {
        return xCoord;
    }

    @Override
    public double getyCoord() {
        return yCoord;
    }

    public double getArea() {
        return area;
    }

    public double getSteelStress() {
        return steelStress;
    }

    public boolean isRebarFailed() {
        return rebarFailed;
    }

}
