package com.columncalcrest.model;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;

public class NodeFiniteElement {
    // This class represents the nodes of the Finite Element Method

    // Represents the unique element id
    private final int id;

    // Represents the node position along the column element
    private final double zPosition;

    // Represents the displacements restrictions (0=fix, 1=free) on the node on 5 degrees of freedom
    private RealVector nodalRestrictions;

    // Represents the loads in kN and kNcm applied directly to the node on 5 degrees of freedom
    private RealVector nodalLoads;

    // Represents the displacement in cm of the node on 5 degrees of freedom
    private RealVector nodalDisplacements;

    public NodeFiniteElement(int id, double zPosition) {
        this.id = id;
        this.zPosition = zPosition;
        this.nodalRestrictions = new ArrayRealVector(new double[]{1, 1, 1, 1, 1});
        this.nodalLoads = new ArrayRealVector(5);
        this.nodalDisplacements = new ArrayRealVector(5);
    }

    public void updateNodalDisplacements(RealVector globalDisplacementVector) {

        this.nodalDisplacements = globalDisplacementVector.getSubVector(5 * this.id, 5).copy();
    }

    public RealVector getNodalDisplacements() {
        return nodalDisplacements;
    }

    public RealVector getNodalRestrictions() {
        return nodalRestrictions;
    }

    public RealVector getNodalLoads() {
        return nodalLoads;
    }

    public int getId() {
        return id;
    }

    public double getzPosition() {
        return zPosition;
    }

    public void setNodalRestrictions(RealVector nodalRestrictions) {
        this.nodalRestrictions = nodalRestrictions;
    }

    public void setNodalLoads(RealVector nodalLoads) {
        this.nodalLoads = nodalLoads;
    }
}
