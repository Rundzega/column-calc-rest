package com.columncalcrest.model;

import com.columncalcrest.exception.ConcreteFailedException;
import com.columncalcrest.exception.InvalidColumnInput;
import com.columncalcrest.exception.RebarFailedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class CrossSection {

    // ArrayList containing all of the rectangles that form the cross-section
    private final ArrayList<ConcreteRectangle> rectanglesArrayList;

    // ArrayList containing all of the rebars in the cross-section
    private final ArrayList<Rebar> rebarArrayList;

    // ArrayList contining all of the discretized concrete elements that form the cross-section
    private final ArrayList<DiscretizedConcElement> discretizedElementsList;

    // Represents the area of the cross-section
    private double totalArea;

    // Inertial moment of the cross-section relative to the X Axis
    private double xInertialMoment;

    // Inertial moment of the cross-section relative to the Y Axis
    private double yInertialMoment;

    // Concrete class of the cross-section
    private final Concrete concreteClass;

    // Steel class of the cross-section
    private final Steel steelClass;

    // X coordinate of the cross-secion's center of gravity
    private double xCenterOfGravity;

    // Y coordinate of the cross-section's center of gravity
    private double yCenterOfGravity;

    // Cross-section parameter need to calculate internal forces and moments
    private double hParam;

    // Cross-section parameter need to calculate internal forces and moments
    private double yMaxParam;

    // Cross-section parameter need to calculate internal forces and moments
    private double yMinParam;

    // Cross-section parameter need to calculate internal forces and moments
    private double dParam;

    // Represents the sum of all normal forces acting on the cross-section
    private double normalForcesSum;

    // Depth of the neutral axis in which the cross-section reaches internal forces equilibrium
    private double EquilibriumNeutralAxisDepth;

    // Internal bending moment relative to the X axis that combined with a Y moment brings the CS to
    // failure for a given load at a certain angle
    private double failXBendingMoment;

    // Internal bending moment relative to the Y axis that combined with a X moment brings the CS to
    // failure for a given load at a certain angle
    private double failYBendingMoment;

    private ArrayList<Double> graphAngles;

    // List containing all pairs of bending moments that draw the internal moments diagram
    private ArrayList<double[]> internalMomentsDiagram;

    public CrossSection(ArrayList<ConcreteRectangle> rectanglesArrayList, ArrayList<Rebar> rebarsList,
                        Concrete concreteClass, Steel steelClass, int numberOfDiscretizedX,
                        int numberOfDiscretizedY) {

        if (rectanglesArrayList.size() == 0 || rebarsList.size() == 0 || concreteClass == null || steelClass == null) {
            throw new InvalidColumnInput("Dados de entrada da seção transversal inválidos");
        }

        this.rectanglesArrayList = rectanglesArrayList;
        this.rebarArrayList = rebarsList;
        this.concreteClass = concreteClass;
        this.steelClass = steelClass;
        this.discretizedElementsList = new ArrayList<>();
        this.setInertialMoments(numberOfDiscretizedX, numberOfDiscretizedY);

    }

    public void DiscretizeCrossSection(ConcreteRectangle rectangle, int numberOfDiscretizedX,
                                       int numberOfDiscretizedY) {
        // Discretizes the rectangles that form the cross-section into
        // numberOfDiscretizedX and numberOfDiscretizedY smaller rectangles

        double elementWidth = rectangle.getWidth() / numberOfDiscretizedX;
        double elementHeight = rectangle.getHeight() / numberOfDiscretizedY;
        double elementHalfWidth = elementWidth / 2;
        double elementHalfHeight = elementHeight / 2;

        for (int i = 0; i < numberOfDiscretizedX; i++) {
            for (int j = 0; j < numberOfDiscretizedY; j++) {
                double xCenterCoord = rectangle.getVerticesArray()[0][0] + i * elementWidth + elementHalfWidth;
                double yCenterCoord = rectangle.getVerticesArray()[0][1] + j * elementHeight + elementHalfHeight;
                DiscretizedConcElement concElement = new DiscretizedConcElement(concreteClass, steelClass, elementWidth,
                        elementHeight, xCenterCoord, yCenterCoord);
                this.discretizedElementsList.add(concElement);
            }
        }
    }

    public void setElementsCenterOfGravity() {
        this.discretizedElementsList.forEach((element) -> {
            element.setCoordRelativeToCSCenter(this);
        });

        this.rebarArrayList.forEach(((rebar) -> {
            rebar.setCoordRelativeToCSCenter(this);
        }));
    }

    public void setInertialMoments(int numberOfDiscretizedX, int numberOfDiscretizedY) {
        // Calculates the center of gravity of the cross-section composed of rectangles

        AtomicReference<Double> totalArea = new AtomicReference<>((double) 0);
        AtomicReference<Double> XStaticalMoment = new AtomicReference<>((double) 0);
        AtomicReference<Double> YStaticalMoment = new AtomicReference<>((double) 0);

        this.rectanglesArrayList.forEach((rectangle) -> {
            totalArea.updateAndGet(v -> (double) (v + rectangle.getArea()));
            XStaticalMoment.updateAndGet(v -> (double) (v + rectangle.getXStaticalMoment()));
            YStaticalMoment.updateAndGet(v -> (double) (v + rectangle.getYStaticalMoment()));
            this.DiscretizeCrossSection(rectangle, numberOfDiscretizedX, numberOfDiscretizedY);
        });

        this.xCenterOfGravity = YStaticalMoment.get() / totalArea.get();
        this.yCenterOfGravity = XStaticalMoment.get() / totalArea.get();
        this.setElementsCenterOfGravity();

        AtomicReference<Double> accXInertialMoment = new AtomicReference<>((double) 0);
        AtomicReference<Double> accYInertialMoment = new AtomicReference<>((double) 0);

        this.rectanglesArrayList.forEach((rectangle) -> {
            double yDistance = this.yCenterOfGravity - rectangle.getyCenterCoord();
            double xDistance = this.xCenterOfGravity - rectangle.getxCenterCoord();
            accXInertialMoment.updateAndGet(v -> (double) (v + rectangle.getXInertialMoment() + rectangle.getArea() * Math.pow(yDistance, 2)));
            accYInertialMoment.updateAndGet(v -> (double) (v + rectangle.getYInertialMoment() + rectangle.getArea() * Math.pow(xDistance, 2)));
        });

        this.xInertialMoment = accXInertialMoment.get();
        this.yInertialMoment = accYInertialMoment.get();
        this.totalArea = totalArea.get();
    }

    public double[] rotateCoordinates(double[] coordinates, double angle) {
        // Rotates a pair of coordinates [x, y] relative to an angle(degree)

        double angleRadians = Math.toRadians(angle);
        double[] newCoordinates = {coordinates[0] * Math.cos(angleRadians) + coordinates[1] * Math.sin(angleRadians),
                                    -coordinates[0] * Math.sin(angleRadians) + coordinates[1] * Math.cos(angleRadians)};
        return newCoordinates;
    }

    public void setCrossSectionCalcParameters(double angle) {
        // Obtains parameters needed in order to calculate internal
        // forces and moments in a cross-section rotated at an angle relative to the initial X Axis

        ArrayList<Double> auxVerificationD = new ArrayList<>();
        ArrayList<double[]> auxVertices = new ArrayList<>();
        this.rectanglesArrayList.forEach((rectangle) -> {
            for (int i = 0; i < 4; i++) {
                double[] coordinates = {rectangle.getVerticesArray()[i][0] - this.xCenterOfGravity,
                                        rectangle.getVerticesArray()[i][1] - this.yCenterOfGravity};
                double[] rotatedVertice = rotateCoordinates(coordinates, angle);
                auxVertices.add(rotatedVertice);
            }
        });

        this.yMinParam = 0;
        this.yMaxParam = 0;

        auxVertices.forEach((vertice) -> {
            this.yMaxParam = Math.max(vertice[1], this.yMaxParam);
            this.yMinParam = Math.min(vertice[1], this.yMinParam);
        });

        this.hParam = this.yMaxParam - this.yMinParam;

        this.rebarArrayList.forEach((rebar) -> {
            double[] coordinates = {rebar.getxCoord() - this.xCenterOfGravity,
                                    rebar.getyCoord() - this.yCenterOfGravity};
            auxVerificationD.add(rotateCoordinates(coordinates, angle)[1]);
        });

        this.dParam = this.yMaxParam - Collections.min(auxVerificationD);
    }

    public void setNormalForcesSum(double load, double neutralAxisDepthAttempt, double angle) {
        // Obtains the sum of all normal forces acting on the cross-section
        // including the Nd load for a given neutralAxisAttempt position and angle

        this.normalForcesSum = 0;


        double ultConcStrain = this.concreteClass.getUltConcStrain();
        double yieldSteelStrain = this.steelClass.getYieldStrain();
        int strainDomain;

        if (neutralAxisDepthAttempt <= ((ultConcStrain / (yieldSteelStrain + ultConcStrain))*this.dParam)) {
            strainDomain = 2;
        } else if (neutralAxisDepthAttempt <= this.hParam) {
            strainDomain = 3;
        } else {
            strainDomain = 5;
        }

        AtomicReference<Double> normalForcesSumReference = new AtomicReference<>((double) 0);

        this.discretizedElementsList.forEach((element) -> {
            double[] elementPosition = {element.getxCoord() - this.xCenterOfGravity,
                                                element.getyCoord() - this.yCenterOfGravity};
            double[] elementRotatedPosition = rotateCoordinates(elementPosition, angle);
            element.calculateStrain(elementRotatedPosition[1], neutralAxisDepthAttempt, this.yMaxParam,
                    this.hParam, this.dParam, strainDomain);
            element.calculateConcStress(false);
            normalForcesSumReference.updateAndGet(sum -> (sum - element.getConcStress() * element.getArea()));
        });

        this.rebarArrayList.forEach((rebar) -> {
            double[] rebarPosition = {rebar.getxCoord() - this.xCenterOfGravity,
                                        rebar.getyCoord() - this.yCenterOfGravity};
            double[] rebarRotatedPosition = rotateCoordinates(rebarPosition, angle);
            rebar.calculateStrain(rebarRotatedPosition[1], neutralAxisDepthAttempt, this.yMaxParam,
                    this.hParam, this.dParam, strainDomain);
            rebar.calculateSteelStress();
            rebar.calculateConcStress(false);
            normalForcesSumReference.updateAndGet(sum -> (sum - (rebar.getSteelStress() - rebar.getConcStress()) * rebar.getArea()));
        });

        this.normalForcesSum = normalForcesSumReference.get();
        this.normalForcesSum -= load;
    }


    public void setEquilibriumNeutralAxisDepth(double load, double tolerance, double angle) {
        // Utilizes the bissection method to find the position of the neutralAxis that
        // correctly balances the internal forces of the cross-section

        this.setCrossSectionCalcParameters(angle);
        double minNeutralAxisDepthAtt = -this.dParam;
        double maxNeutralAxisDepthAtt = 2 * this.dParam;
        double averageNeutralAxisDepthAtt = (minNeutralAxisDepthAtt + maxNeutralAxisDepthAtt) / 2;


        this.setNormalForcesSum(load, minNeutralAxisDepthAtt, angle);
        double minAttemptInternalForces = getNormalForcesSum();

        this.setNormalForcesSum(load, maxNeutralAxisDepthAtt, angle);
        double maxAttemptInternalForces = getNormalForcesSum();

        this.setNormalForcesSum(load, averageNeutralAxisDepthAtt, angle);
        double avgAttemptInternalForces = getNormalForcesSum();


        int counter = 1;

        while(minAttemptInternalForces * maxAttemptInternalForces > 0) {
            minNeutralAxisDepthAtt = maxNeutralAxisDepthAtt;
            maxAttemptInternalForces = maxNeutralAxisDepthAtt * 10;
            counter++;
            if (counter > 10) {
                // TODO: ERRORS
                throw new RuntimeException();
            }
        }

        while((maxNeutralAxisDepthAtt - minNeutralAxisDepthAtt) / 2 > tolerance) {
            if (minAttemptInternalForces * avgAttemptInternalForces < 0) {
                maxNeutralAxisDepthAtt = averageNeutralAxisDepthAtt;
                averageNeutralAxisDepthAtt = (minNeutralAxisDepthAtt + maxNeutralAxisDepthAtt) / 2;

            } else {
                minNeutralAxisDepthAtt = averageNeutralAxisDepthAtt;
                averageNeutralAxisDepthAtt = (minNeutralAxisDepthAtt + maxNeutralAxisDepthAtt) / 2;
                minAttemptInternalForces = avgAttemptInternalForces;

            }
            this.setNormalForcesSum(load, averageNeutralAxisDepthAtt, angle);
            avgAttemptInternalForces = this.getNormalForcesSum();
        }

        this.EquilibriumNeutralAxisDepth = averageNeutralAxisDepthAtt;
    }

    public void setInternalBendingMoments() {
        // Calculates a pair of bending moments relative to the x and y axis that brings the
        // cross-section to failure for a given normal load and a certain depth and angle of
        // the neutral axis

        this.failXBendingMoment = 0;
        this.failYBendingMoment = 0;

        this.discretizedElementsList.forEach((element) -> {
            this.failXBendingMoment -= element.getConcStress() * element.getArea() *
                    (element.getxCoord() - this.xCenterOfGravity); //kNcm
            this.failYBendingMoment += element.getConcStress() * element.getArea() *
                    (element.getyCoord() - this.yCenterOfGravity); //kNcm
        });

        this.rebarArrayList.forEach((rebar) -> {
            this.failXBendingMoment -= (rebar.getSteelStress() - rebar.getConcStress()) *
                    rebar.getArea() * (rebar.getxCoord() - this.xCenterOfGravity); //kNcm
            this.failYBendingMoment += (rebar.getSteelStress() - rebar.getConcStress()) *
                    rebar.getArea() * (rebar.getyCoord() - this.yCenterOfGravity); //kNcm
        });
    }

    public void setInternalMomentsDiagram(double load, double tolerance, double stepAngle) {
        // Varies the neutral axis angle in 360 degrees in N steps in order to obtain a diagram
        // containing pairs of bending moments that bring the cross-section to failure given a load

        double graphAngle;

        ArrayList<double[]> auxInternalMomentsDiagram = new ArrayList<>();
        ArrayList<Double> auxGraphAngles = new ArrayList<>();

        for (double angle = 0; angle <= 360; angle += stepAngle) {
            this.setEquilibriumNeutralAxisDepth(load, tolerance, angle);
            this.setInternalBendingMoments();
            double auxAngle = Math.toDegrees(Math.atan(
                    Math.abs(this.failXBendingMoment) / Math.abs(this.failYBendingMoment)));

            if (this.failYBendingMoment == 0) {
                // No neutral axis case

                if (this.failXBendingMoment > 0) {
                    graphAngle = 90;
                    auxGraphAngles.add(graphAngle);
                } else if (this.failXBendingMoment < 0){
                    graphAngle = 270;
                    auxGraphAngles.add(graphAngle);
                } else {
                    graphAngle = 0;
                    auxGraphAngles.add(graphAngle);
                }
            } else if (this.failYBendingMoment > 0 && this.failXBendingMoment > 0){
                // First quadrant case

                graphAngle = auxAngle;
                auxGraphAngles.add(graphAngle);
            } else if (this.failYBendingMoment < 0 && this.failXBendingMoment >= 0) {
                //Second quadrant case

                graphAngle = 180 - auxAngle;
                auxGraphAngles.add(graphAngle);

            } else if (this.failYBendingMoment < 0 && this.failXBendingMoment < 0) {
                // Third quadrant case

                graphAngle = 180 + auxAngle;
                auxGraphAngles.add(graphAngle);
            } else {
                // Fourth quadrant case

                graphAngle = 360 - auxAngle;
                auxGraphAngles.add(graphAngle);
            }
            double[] internalMomentsComponent = {
                    this.failYBendingMoment/100,
                    this.failXBendingMoment/100,
            };

            auxInternalMomentsDiagram.add(internalMomentsComponent);


            this.internalMomentsDiagram = auxInternalMomentsDiagram;
            this.graphAngles = auxGraphAngles;
        }
    }

    public double[] calcInternalForces(double axialStrain, double yCurvature,
                                        double xCurvature, boolean creep) {
        // Returns an array containing normal force Nd and Bending Moments My and Mx acting on
        // the cross-section for given values of axial strain, yCurvature and xCurvature

        AtomicReference<Double> ndNormalForce = new AtomicReference<>((double) 0);
        AtomicReference<Double> myBendingMoment = new AtomicReference<>((double) 0);
        AtomicReference<Double> mxBendingMoment = new AtomicReference<>((double) 0);

        int counter = 0;

        this.discretizedElementsList.forEach((element) -> {
            double xRelativeCoord = element.getxCoordRelativeToCSCenter();
            double yRelativeCoord = element.getyCoordRelativeToCSCenter();

            element.setStrain(axialStrain +
                    yRelativeCoord * yCurvature/100 +
                    xRelativeCoord * xCurvature/100
            );
            element.calculateConcStress(creep);
            double concStress = element.getConcStress();
            double area = element.getArea();
            ndNormalForce.updateAndGet(v -> v + concStress * area);
            myBendingMoment.updateAndGet(v -> v + concStress * area * yRelativeCoord);
            mxBendingMoment.updateAndGet(v -> v + concStress * area * xRelativeCoord);
        });



        this.rebarArrayList.forEach((rebar) -> {
            double xRelativeCoord = rebar.getxCoordRelativeToCSCenter();
            double yRelativeCoord = rebar.getyCoordRelativeToCSCenter();

            rebar.setStrain(axialStrain +
                    rebar.getyCoordRelativeToCSCenter() * yCurvature/100 +
                    rebar.getxCoordRelativeToCSCenter() * xCurvature/100);
            rebar.calculateSteelStress();
            rebar.calculateConcStress(creep);
            double concStress = rebar.getConcStress();
            double steelStress = rebar.getSteelStress();
            double area = rebar.getArea();

            ndNormalForce.updateAndGet(v -> v + (steelStress - concStress) * area);
            myBendingMoment.updateAndGet(v -> v + (steelStress - concStress) * area * yRelativeCoord);
            mxBendingMoment.updateAndGet(v -> v + (steelStress - concStress) * area * xRelativeCoord);
        });


        return new double[]{
                ndNormalForce.get(),
                myBendingMoment.get()/100,
                mxBendingMoment.get()/100
        };
    }

    public boolean checkIsFailed() throws ConcreteFailedException, RebarFailedException {

        // Checks wether the cross-section failed due to concrete crushing
        // or excessive steel alongation

        discretizedElementsList.forEach((element) -> {
            if (element.isConcFailed()) {
                throw new ConcreteFailedException("A seção transversal falhou: Concreto atingiu a deformação máxima permitida");
            }
        });

        rebarArrayList.forEach((rebar) -> {
            if (rebar.isRebarFailed()) {
                throw new RebarFailedException("A seção transversal falhou: Aço atingiu a deformação máxima permitida");
            }
        });

        return false;
    }

    public double getxCenterOfGravity() {
        return xCenterOfGravity;
    }

    public double getyCenterOfGravity() {
        return yCenterOfGravity;
    }

    public Concrete getConcreteClass() {
        return concreteClass;
    }

    public double getXInertialMoment() {
        return xInertialMoment;
    }

    public double getYInertialMoment() {
        return yInertialMoment;
    }

    public double getTotalArea() {
        return totalArea;
    }

    public double getEquilibriumNeutralAxisDepth() {
        return EquilibriumNeutralAxisDepth;
    }

    public double getNormalForcesSum() {
        return normalForcesSum;
    }

    public ArrayList<double[]> getInternalMomentsDiagram() {
        return internalMomentsDiagram;
    }

    public ArrayList<Double> getGraphAngles() {
        return graphAngles;
    }
}

