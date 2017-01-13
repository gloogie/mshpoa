package com.gloogie.mshpoa.model;

/**
 * Model for measures of sensors
 */
public class FailedMeasure
{
    private Exception exception;
    private String value;

    public Exception getException() {
        return exception;
    }

    public void setException(final Exception exception) {
        this.exception = exception;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
