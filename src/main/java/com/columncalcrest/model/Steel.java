package com.columncalcrest.model;

import com.columncalcrest.exception.InvalidColumnInput;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Steel {

    @Positive
    @NotNull
    private final double fyk;

    @Positive
    @NotNull
    private final double gammaSteel;;

    // Design tensile strength for steel in MPa
    private final double fyd;

    @Positive
    @NotNull
    // Young's Modulus for steel in MPa
    private final double youngModulus;

    @Positive
    @NotNull
    // Ultimate strain for steel
    private final double yieldStrain;

    @JsonCreator
    public Steel(double fyk,
                 double gammaSteel,
                 double youngModulus,
                 double yieldStrain) {


        this.fyk = fyk;
        this.gammaSteel = gammaSteel;
        this.fyd = fyk/(10*gammaSteel); // in kN/cm2
        this.youngModulus = youngModulus/10; // in kn/cm2
        this.yieldStrain = yieldStrain;

    }

    public double getFyk() {
        return fyk;
    }

    public double getGammaSteel() {
        return gammaSteel;
    }

    public double getFyd() {
        return fyd;
    }

    public double getYoungModulus() {
        return youngModulus;
    }

    public double getYieldStrain() {
        return yieldStrain;
    }
}
