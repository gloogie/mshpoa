package com.gloogie.mshpoa.report.exception;

/**
 * Exception for Reporter
 */
public class ReportException extends Exception
{
    public ReportException(final String message, final Exception cause) {
        super(message, cause);
    }

    public ReportException(final String message) {
        super(message);
    }
}
