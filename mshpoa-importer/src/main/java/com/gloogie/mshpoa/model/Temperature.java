package com.gloogie.mshpoa.model;

/**
 * Model for measures of temperature sensors
 */
public class Temperature extends Measure
{
    private String unit;

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }
}
