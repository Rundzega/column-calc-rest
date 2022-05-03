package com.columncalcrest.model;

import com.columncalcrest.exception.InvalidColumnInput;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConcreteRectangle {

    // This class represents the Rectangles used to create the Cross-Section of the Column

    // Rectangle widht
    private final double width;

    // Rectangle height
    private final double height;

    // X Coordinate of the center of the rectangle in cm
    private final double xCenterCoord;

    // Y Coordinate of the center of the rectangle in cm
    private final double yCenterCoord;

    // Area of the concrete rectangle in cm2
    private final double area;

    // Array containing the x and y coordinates of each vertice of the rectangle
    private final double[][] verticesArray;

    // Statical moment of the rectangle relative to the x Axis in cm3
    private final double XStaticalMoment;

    // Statical moment of the rectangle relative to the y Axis in cm3
    private final double YStaticalMoment;

    // Inertical moment of the rectangle relative to the x Axis in cm4
    private final double XInertialMoment;

    // Inertial moment of the rectangle relative to the y Axis in cm4
    private final double YInertialMoment;


    // width in cm, height in cm, xCenterCoord in cm, yCenterCoord in cm
    @JsonCreator
    public ConcreteRectangle(@JsonProperty("width") double width,
                             @JsonProperty("height")double height,
                             @JsonProperty("xCenterCoord")double xCenterCoord,
                             @JsonProperty("yCenterCoord")double yCenterCoord ) {

        if (width <= 0 || height <= 0) {
            throw new InvalidColumnInput("Invalid rectangle input data");
        }

        this.width = width;
        this.height = height;
        this.area = width * height;
        this.xCenterCoord = xCenterCoord;
        this.yCenterCoord = yCenterCoord;
        this.verticesArray = new double[][] {
                {xCenterCoord - width/2, yCenterCoord - height/2},
                {xCenterCoord + width/2, yCenterCoord - height/2},
                {xCenterCoord + width/2, yCenterCoord + height/2},
                {xCenterCoord - width/2, yCenterCoord + height/2}
        };
        this.XStaticalMoment = this.area * yCenterCoord;
        this.YStaticalMoment = this.area * xCenterCoord;
        this.XInertialMoment = (width * Math.pow(height, 3)) / 12;
        this.YInertialMoment = (height * Math.pow(width, 3)) / 12;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getxCenterCoord() {
        return xCenterCoord;
    }

    public double getyCenterCoord() {
        return yCenterCoord;
    }

    public double getArea() {
        return area;
    }

    public double[][] getVerticesArray() {
        return verticesArray;
    }

    public double getXStaticalMoment() {
        return XStaticalMoment;
    }

    public double getYStaticalMoment() {
        return YStaticalMoment;
    }

    public double getXInertialMoment() {
        return XInertialMoment;
    }

    public double getYInertialMoment() {
        return YInertialMoment;
    }
}
