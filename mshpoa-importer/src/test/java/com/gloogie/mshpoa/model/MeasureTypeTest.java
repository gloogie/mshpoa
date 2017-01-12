package com.gloogie.mshpoa.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for MeasureType
 */
public class MeasureTypeTest
{
    @Test
    public void testKnownSensorTypes() throws Exception {
        MeasureType type = MeasureType.valueOf("T");
        Assert.assertEquals(MeasureType.T, type);
        Assert.assertEquals("Temperature", type.getName());
        type = MeasureType.valueOf("P");
        Assert.assertEquals(MeasureType.P, type);
        Assert.assertEquals("Pressure", type.getName());
        type = MeasureType.valueOf("H");
        Assert.assertEquals(MeasureType.H, type);
        Assert.assertEquals("Humidity", type.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownSensorType() throws Exception {
        MeasureType.valueOf("ZZZ");
    }

}