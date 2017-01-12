package com.gloogie.mshpoa.model;

import java.util.Date;

/**
 * Model for measures of pressure sensors
 */
public class Pressure extends Measure
{
    private Date date;
    private String unit;

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }
}
