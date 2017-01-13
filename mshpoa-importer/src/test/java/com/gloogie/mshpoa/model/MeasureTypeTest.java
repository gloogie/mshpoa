package com.gloogie.mshpoa.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for MeasureType
 */
public class MeasureTypeTest
{
    @Test
    public void testOK() throws Exception {
        final MeasureType type = new MeasureType();
        type.setCode("T");
        type.setName("temperature");
        Assert.assertEquals("T", type.getCode());
        Assert.assertEquals("temperature", type.getName());
    }

}