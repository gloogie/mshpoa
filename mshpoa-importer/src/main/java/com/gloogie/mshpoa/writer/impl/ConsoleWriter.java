package com.gloogie.mshpoa.writer.impl;

import com.gloogie.mshpoa.writer.Writer;

/**
 * Writer in stdout
 */
public class ConsoleWriter implements Writer
{
    @Override
    public void write(final String message) {
        System.out.println(message);
    }
}
