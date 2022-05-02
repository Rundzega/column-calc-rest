package com.columncalcrest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rebar extends CrossSectionElement{

    // Cross-Section area of the rebar in cm2
    private final double area;

    // Indicates wheter rebar has reached limit strain
    private boolean rebarFailed;

    // Represents the compressive or tensile stress of the rebar
    private double steelStress;


    //diameter in mm
    @JsonCreator
    public Rebar(@JsonProperty("concreteClass")Concrete concreteClass,
                 @JsonProperty("steelClass") Steel steelClass,
                 @JsonProperty("diameter") double diameter,
                 @JsonProperty("xCoord") double xCoord,
                 @JsonProperty("yCoord") double yCoord) {

        super(concreteClass, steelClass, xCoord, yCoord);
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
