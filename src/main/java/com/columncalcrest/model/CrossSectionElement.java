package com.columncalcrest.model;

public class CrossSectionElement {

    // X coordinate of the center of the element in cm
    private double xCoord;

    // Y coordinate of the center of the element in cm
    private double yCoord;

    // Represents the strain of element
    private double strain;

    // Concrete class of the cross-section
    private Concrete concreteClass;

    // Steel class of the cross-section
    private Steel steelClass;

    // Represents the compressive or tensile stress of concrete element
    private double concStress;

    // Represents wheter material has failed
    private boolean concFailed;

    // Represents the x coordinate of the element relative to the cross-section geometrical center
    private double xCoordRelativeToCSCenter;

    // Represents the y coordinate of the element relative to the cross-section geometrical center
    private double yCoordRelativeToCSCenter;

    public CrossSectionElement(Concrete concreteClass, Steel steelClass, double xCoord, double yCoord) {
        this.concreteClass = concreteClass;
        this.steelClass = steelClass;

        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    protected void calculateConcStress(boolean creep) {
        // Calculates the compressive stress in kN/cm2 of an element subject to strain

        double concAltStrainModif = creep ? concreteClass.getAltConcStrainCreepModif() : concreteClass.getAltConcStrainModif();
        double concAltStrain = creep ? concreteClass.getAltConcStrainCreep() : concreteClass.getAltConcStrain();

        if (this.strain < 0) {
            if (Math.abs(this.strain) < concAltStrainModif) {
                this.concStress = -(this.concreteClass.getAlpha() *
                        this.concreteClass.getFcd() *
                        (1 - Math.pow(1 - Math.abs(this.strain) / concAltStrain,
                                this.concreteClass.getnParam())));
            } else if (Math.abs(this.strain) <= this.concreteClass.getUltConcStrainCreep()) {
                this.concStress = -(this.concreteClass.getBeta() * this.concreteClass.getFcd());
            } else {
                this.concStress = 0;
            }
        } else {
            this.concStress = 0;
            this.concFailed = !creep;
        }
    }

    protected double getStrain() {
        return strain;
    }

    protected void calculateStrain(double yLine, double neutralAxisCoord, double yMaxParam,
                                   double hParam, double dParam, int strainDomain) {

        //  Obtains strain at a point situated at "yLine" distance from the
        //  neutral axis at a given strain domain

        double diParam = yMaxParam - yLine;

        switch(strainDomain) {
            case 2:
                this.strain = - (this.steelClass.getYieldStrain() *
                        ((neutralAxisCoord - diParam) / (dParam - neutralAxisCoord)));
                break;
            case 3:
            case 4:
                this.strain = - (this.concreteClass.getUltConcStrain() *
                        ((neutralAxisCoord - diParam) / neutralAxisCoord));
                break;
            default:
                double concAltStrain = this.concreteClass.getAltConcStrain();
                double concUltStrain = this.concreteClass.getUltConcStrain();

                this.strain = - (concAltStrain * ((neutralAxisCoord - diParam) /
                        (neutralAxisCoord - (1 - concAltStrain / concUltStrain) * hParam)));
                break;
        }
    }

    protected void setStrain(double strain) {
        this.strain = strain;
    }

    protected void setCoordRelativeToCSCenter(CrossSection crossSection) {
        this.xCoordRelativeToCSCenter = this.xCoord - crossSection.getxCenterOfGravity();
        this.yCoordRelativeToCSCenter = this.yCoord - crossSection.getyCenterOfGravity();
    }

    protected Concrete getConcreteClass() {
        return concreteClass;
    }

    protected Steel getSteelClass() {
        return steelClass;
    }

    protected double getConcStress() {
        return concStress;
    }

    protected boolean isConcFailed() {
        return concFailed;
    }

    protected double getxCoord() {
        return xCoord;
    }

    protected double getyCoord() {
        return yCoord;
    }

    public double getxCoordRelativeToCSCenter() {
        return xCoordRelativeToCSCenter;
    }

    public double getyCoordRelativeToCSCenter() {
        return yCoordRelativeToCSCenter;
    }
}
