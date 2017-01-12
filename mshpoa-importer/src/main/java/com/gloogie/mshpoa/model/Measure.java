package com.gloogie.mshpoa.model;

/**
 * Model for measures of sensors
 */
public abstract class Measure
{
    private Double value;

    public Double getValue() {
        return value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }
}
