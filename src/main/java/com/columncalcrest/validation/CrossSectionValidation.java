package com.columncalcrest.validation;

import com.columncalcrest.exception.InvalidColumnInput;
import com.columncalcrest.model.ConcreteRectangle;
import com.columncalcrest.model.Rebar;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrossSectionValidation {

    public void validateRectanglesPosition(ArrayList<ConcreteRectangle> rectangleArrayList) {

        rectangleArrayList.forEach(primaryRectangle -> {
            double primaryRectLeftVertice = primaryRectangle.getxCenterCoord() - primaryRectangle.getWidth() / 2;
            double primaryRectRightVertice = primaryRectangle.getxCenterCoord() + primaryRectangle.getWidth() / 2;
            double primaryRectTopVertice = primaryRectangle.getyCenterCoord() + primaryRectangle.getHeight() / 2;
            double primaryRectBottomVertice = primaryRectangle.getyCenterCoord() - primaryRectangle.getHeight() / 2;

            AtomicBoolean isCrossSectionContinuous = new AtomicBoolean(false);

            for(ConcreteRectangle secondaryRectangle:rectangleArrayList) {
                if (primaryRectangle != secondaryRectangle) {
                    double secondaryRectLeftVertice = secondaryRectangle.getxCenterCoord() - secondaryRectangle.getWidth() / 2;
                    double secondaryRectRightVertice = secondaryRectangle.getxCenterCoord() + secondaryRectangle.getWidth() / 2;
                    double secondaryRectTopVertice = secondaryRectangle.getyCenterCoord() + secondaryRectangle.getHeight() / 2;
                    double secondaryRectBottomVertice = secondaryRectangle.getyCenterCoord() - secondaryRectangle.getHeight() / 2;

                    if (primaryRectLeftVertice < secondaryRectRightVertice &&
                            primaryRectRightVertice > secondaryRectLeftVertice &&
                            primaryRectBottomVertice < secondaryRectTopVertice &&
                            primaryRectTopVertice > secondaryRectBottomVertice) {

                        throw new InvalidColumnInput("Invalid cross-section input, rectangles overlap");
                    }

                    if (primaryRectangle.getHeight() < secondaryRectangle.getHeight()) {
                        if (primaryRectLeftVertice == secondaryRectRightVertice ||
                            primaryRectRightVertice == secondaryRectLeftVertice) {

                            if ((secondaryRectBottomVertice <= primaryRectBottomVertice &&
                                    primaryRectBottomVertice <= secondaryRectTopVertice) ||
                               (secondaryRectBottomVertice <= primaryRectTopVertice &&
                                    primaryRectTopVertice <= secondaryRectTopVertice)) {

                                isCrossSectionContinuous.set(true);
                                continue;
                            }
                        }
                    } else {

                        if (primaryRectLeftVertice == secondaryRectRightVertice ||
                                primaryRectRightVertice == secondaryRectLeftVertice) {

                            if((primaryRectBottomVertice <= secondaryRectBottomVertice &&
                                    secondaryRectBottomVertice <= primaryRectTopVertice) ||
                                    (primaryRectBottomVertice <= secondaryRectTopVertice &&
                                            secondaryRectTopVertice <= primaryRectTopVertice)) {

                                isCrossSectionContinuous.set(true);
                                continue;
                            }
                        }
                    }

                    if (primaryRectangle.getWidth() < secondaryRectangle.getWidth()) {

                        if (primaryRectBottomVertice == secondaryRectTopVertice ||
                                primaryRectTopVertice == secondaryRectBottomVertice) {

                            if ((secondaryRectLeftVertice <= primaryRectLeftVertice &&
                                    primaryRectLeftVertice <= secondaryRectRightVertice) ||
                                    (secondaryRectLeftVertice <= primaryRectRightVertice &&
                                            primaryRectRightVertice <= secondaryRectRightVertice)) {

                                isCrossSectionContinuous.set(true);
                                continue;
                            }
                        }
                    } else {

                        if (primaryRectBottomVertice == secondaryRectTopVertice ||
                                primaryRectTopVertice == secondaryRectBottomVertice) {

                            if ((primaryRectLeftVertice <= secondaryRectLeftVertice &&
                                    secondaryRectLeftVertice <= primaryRectRightVertice) ||
                                    (primaryRectLeftVertice <= secondaryRectRightVertice &&
                                            secondaryRectRightVertice <= primaryRectRightVertice)) {

                                isCrossSectionContinuous.set(true);
                                continue;
                            }
                        }
                    }
                }
            }
            if (!isCrossSectionContinuous.get() && rectangleArrayList.size() > 1) {
                throw new InvalidColumnInput("Cross Section must be continuous");
            }
        });

    }

    public void validateRebarsPosition(ArrayList<Rebar> rebarArrayList, ArrayList<ConcreteRectangle> rectangleArrayList) {

        rebarArrayList.forEach(primaryRebar -> {
            double primaryRebarLeftExtreme = primaryRebar.getxCoord() - primaryRebar.getDiameter() / 20;
            double primaryRebarRightExtreme = primaryRebar.getxCoord() + primaryRebar.getDiameter() / 20;
            double primaryRebarTopExtreme = primaryRebar.getyCoord() + primaryRebar.getDiameter() / 20;
            double primaryRebarBottomExtreme = primaryRebar.getyCoord() - primaryRebar.getDiameter() / 20;

            AtomicBoolean isInsideCrossSection = new AtomicBoolean(false);

            for (ConcreteRectangle rectangle:rectangleArrayList) {
                double rectLeftVertice = rectangle.getxCenterCoord() - rectangle.getWidth() / 2;
                double rectRightVertice = rectangle.getxCenterCoord() + rectangle.getWidth() / 2;
                double rectTopVertice = rectangle.getyCenterCoord() + rectangle.getHeight() / 2;
                double rectBottomVertice = rectangle.getyCenterCoord() - rectangle.getHeight() / 2;

                if (rectLeftVertice <= primaryRebarLeftExtreme &&
                        rectRightVertice >= primaryRebarRightExtreme &&
                        rectBottomVertice <= primaryRebarBottomExtreme &&
                        rectTopVertice >= primaryRebarTopExtreme) {

                    isInsideCrossSection.set(true);
                }
            }
            if (!isInsideCrossSection.get()) {
                throw new InvalidColumnInput("Rebars must be inside the cross section");
            }

            for (Rebar secondaryRebar:rebarArrayList) {

                if (primaryRebar != secondaryRebar) {
                    double rebarsDistanceSquared = Math.pow(primaryRebar.getxCoord() - secondaryRebar.getxCoord(), 2) +
                                                   Math.pow(primaryRebar.getyCoord() - secondaryRebar.getyCoord(), 2);

                    double radiusSumSquared = Math.pow(primaryRebar.getDiameter() /20 + secondaryRebar.getDiameter() / 20, 2);

                    if (rebarsDistanceSquared < radiusSumSquared) {
                        throw new InvalidColumnInput("Rebars cannot be overlaped");
                    }
                }
            }
        });
    }

}
