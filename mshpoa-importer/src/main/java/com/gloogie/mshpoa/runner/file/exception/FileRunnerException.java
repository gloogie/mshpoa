package com.gloogie.mshpoa.runner.file.exception;

/**
 * Exception for FileRunner
 */
public class FileRunnerException extends Exception
{
    public FileRunnerException(final String message, final Exception cause) {
        super(message, cause);
    }

    public FileRunnerException(final String message) {
        super(message);
    }
}
