package com.gloogie.mshpoa.model;

/**
 * Type of the sensor
 */
public enum MeasureType
{
    T("Temperature"),
    P("Pressure"),
    H("Humidity");

    private final String name;

    MeasureType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
