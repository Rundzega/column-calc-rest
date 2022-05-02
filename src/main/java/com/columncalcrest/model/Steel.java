package com.columncalcrest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Steel {

    // Design tensile strength for steel in MPa
    private final double fyd;

    // Young's Modulus for steel in MPa
    private final double youngModulus;

    // Ultimate strain for steel
    private final double yieldStrain;

    @JsonCreator
    public Steel(@JsonProperty("fyk") double fyk,
                 @JsonProperty("gammaSteel")double gammaSteel,
                 @JsonProperty("youngModulus")double youngModulus,
                 @JsonProperty("yieldStrain")double yieldStrain) {

        this.fyd = fyk/(10*gammaSteel); // in kN/cm2
        this.youngModulus = youngModulus/10; // in kn/cm2
        this.yieldStrain = yieldStrain;

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
