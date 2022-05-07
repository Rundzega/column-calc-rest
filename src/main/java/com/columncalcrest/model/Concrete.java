package com.columncalcrest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Concrete {

    @Positive
    @NotNull
    private final double fck;

    @Positive
    @NotNull
    private final double gammaConc;

    @Positive
    @NotNull
    private final double creepCoefficient;
    // Design concrete compressive strenght in MPa

    private final double fcd;

    // Concrete creep coefficient
    private final double effectiveCreepCoefficient;

    // Ultimate strain for concrete in stress-strain diagram
    private final double ultConcStrain;

    // Alternative Strain limit for concrete in stress-strain diagram
    private final double altConcStrain;

    // Ultimate strain for concrete in stress-strain diagram with creep effects
    private final double ultConcStrainCreep;

    // Alternative Strain limit for concrete in stress-strain diagram with creep effects
    private final double altConcStrainCreep;

    // Alpha paramater utilized in concrete stress-strain diagram

    @Positive
    @NotNull
    private final double alpha;

    // Beta paramater utilized in concrete stress-strain diagram
    @Positive
    @NotNull
    private final double beta;

    // n Parameter utilized in concrete stress-strain diagram
    private final double nParam;

    // Concrete Young Modulus
    private final double youngModulus;

    // Modified Ultimate strain for concrete in stress-strain diagram
    private final double altConcStrainModif;

    // Modified Alternative Strain limit for concrete in stress-strain diagram with creep effects
    private final double  altConcStrainCreepModif;


    public Concrete(double fck,
                    double gammaConc,
                    double creepCoefficient,
                    double alpha,
                    double beta) {



//        if (fck <= 0 || gammaConc <= 0 || creepCoefficient <= 0 || alpha <= 0 || beta <= 0) {
//            throw new InvalidColumnInput("Invalid concrete input data");
//        }

        this.fck = fck;
        this.gammaConc = gammaConc;
        this.creepCoefficient = creepCoefficient;
        this.fcd = fck/(10 * gammaConc); // in kN/cm2
        this.effectiveCreepCoefficient = 0.57 * creepCoefficient;
        this.alpha = alpha;
        this.beta = beta;
        this.ultConcStrain = fck <= 50 ? 0.0035 : 0.0026 + 0.035 * Math.pow((90.0 - (fck) / 100.0), 4);
        this.altConcStrain = fck <= 50 ? 0.002 : 0.002 + 0.000085  * Math.pow((fck - 50), 0.53);
        this.ultConcStrainCreep = this.ultConcStrain * (1 + this.effectiveCreepCoefficient);
        this.altConcStrainCreep = this.altConcStrain * (1 + this.effectiveCreepCoefficient);

        // Stress strain diagram parameters
        this.youngModulus = (21500 * Math.pow(fck/10, 1.0/3.0)) / 10; // in kN/cm2
        double ecd = this.youngModulus / 0.12;
        double auxNParam = (gammaConc * ecd * this.altConcStrain) / (alpha*fck);
        this.nParam = auxNParam <= 2 ? auxNParam : 2;
        double auxStrainParam = (1 - (1 - Math.pow((this.beta / this.alpha), (1 / this.nParam))));
        this.altConcStrainModif = this.altConcStrain * auxStrainParam;
        this.altConcStrainCreepModif = this.altConcStrainCreep * auxStrainParam;



    }

    public double getFck() {
        return fck;
    }

    public double getGammaConc() {
        return gammaConc;
    }

    public double getCreepCoefficient() {
        return creepCoefficient;
    }

    public double getFcd() {
        return fcd;
    }

    public double getUltConcStrain() {
        return ultConcStrain;
    }

    public double getAltConcStrain() {
        return altConcStrain;
    }

    public double getUltConcStrainCreep() {
        return ultConcStrainCreep;
    }

    public double getAltConcStrainCreep() {
        return altConcStrainCreep;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getnParam() {
        return nParam;
    }

    public double getYoungModulus() {
        return youngModulus;
    }

    public double getAltConcStrainModif() {
        return altConcStrainModif;
    }

    public double getAltConcStrainCreepModif() {
        return altConcStrainCreepModif;
    }
}

