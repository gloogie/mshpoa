package com.gloogie.mshpoa.model;

import java.util.Date;

/**
 * Model for measures of sensors
 */
public class Measure
{
    private MeasureType type;
    private Date date;
    private Double value;
    private String unit;

    public MeasureType getType() {
        return type;
    }

    public void setType(final MeasureType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }
}
