package com.columncalcrest.model;

public class DiscretizedConcElement extends CrossSectionElement{

    // Area of the discretized concrete element of the cross section
    private double area;

    // Array containing the x and y coordinates of each vertice of the discretized element
    private double[][] verticesArray;

    public DiscretizedConcElement(Concrete concreteClass, Steel steelClass, double width,
                                  double height, double xCenterCoord, double yCenterCoord) {

        super(concreteClass, steelClass, xCenterCoord, yCenterCoord );
        this.area = width * height;
        this.verticesArray = new double[][] {
                {xCenterCoord - width/2, yCenterCoord - height/2},
                {xCenterCoord + width/2, yCenterCoord - height/2},
                {xCenterCoord + width/2, yCenterCoord + height/2},
                {xCenterCoord - width/2, yCenterCoord + height/2}
        };
    }

    public double getArea() {
        return area;
    }
}
