package com.gloogie.mshpoa.importer.exception;

/**
 * Exception for Importer
 */
public class ImporterException extends Exception
{
    public ImporterException(final String message, final Exception cause) {
        super(message, cause);
    }

    public ImporterException(final String message) {
        super(message);
    }
}
