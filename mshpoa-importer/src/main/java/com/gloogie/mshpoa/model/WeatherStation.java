package com.gloogie.mshpoa.model;

import java.util.List;

/**
 * Model for weather stations
 */
public class WeatherStation
{
    private String name;
    private List<Measure> measures;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(final List<Measure> measures) {
        this.measures = measures;
    }
}
