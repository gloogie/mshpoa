package com.gloogie.mshpoa.report;

import com.gloogie.mshpoa.model.FailedMeasure;
import com.gloogie.mshpoa.model.Measure;
import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.model.WeatherStation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for StatsComputer
 */
public class StatsComputerTest
{
    private List<MeasureType> types;
    private List<WeatherStation> stations;
    private StatsComputer statsComputer;

    @Before
    public void setUp() throws Exception {
        stations = new ArrayList<>();

        final MeasureType typeT = new MeasureType();
        typeT.setCode("T");
        typeT.setName("temperature");

        final MeasureType typeP = new MeasureType();
        typeP.setCode("P");
        typeP.setName("pressure");

        final MeasureType typeH = new MeasureType();
        typeH.setCode("H");
        typeH.setName("humidity");

        types = new ArrayList<>();
        types.add(typeT);
        types.add(typeP);
        types.add(typeH);

        final WeatherStation station1 = new WeatherStation();
        station1.setName("station1");
        List<Measure> measures = new ArrayList<>();
        Measure measure = new Measure();
        measure.setType(typeT);
        measure.setUnit("C");
        measure.setValue(20.5);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeP);
        measure.setUnit("BAR");
        measure.setValue(1014.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeP);
        measure.setUnit("BAR");
        measure.setValue(1015.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeH);
        measure.setValue(50.0);
        measures.add(measure);

        station1.setMeasures(measures);
        station1.setFailedMeasures(new ArrayList<>());

        final WeatherStation station2 = new WeatherStation();
        station2.setName("station2");
        measures = new ArrayList<>();
        measure = new Measure();
        measure.setType(typeT);
        measure.setUnit("C");
        measure.setValue(30.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeT);
        measure.setUnit("C");
        measure.setValue(35.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeP);
        measure.setUnit("BAR");
        measure.setValue(1010.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeH);
        measure.setValue(30.0);
        measures.add(measure);
        measure = new Measure();
        measure.setType(typeH);
        measure.setValue(70.0);
        measures.add(measure);

        final FailedMeasure failedMeasure = new FailedMeasure();
        failedMeasure.setValue("wrong line");
        final List<FailedMeasure> failedMeasures = new ArrayList<>();
        failedMeasures.add(failedMeasure);

        station2.setMeasures(measures);
        station2.setFailedMeasures(failedMeasures);

        stations.add(station1);
        stations.add(station2);

        statsComputer = new StatsComputer(types, stations);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMeasureTypesNull() throws Exception {
        try {
            new StatsComputer(null, stations);
            Assert.fail("Expected exception was not thrown");
        } catch (final NullPointerException e) {
            Assert.assertEquals("List of measure types cannot be null", e.getMessage());
        }
    }

    @Test
    public void testStationsNull() throws Exception {
        try {
            new StatsComputer(types, null);
            Assert.fail("Expected exception was not thrown");
        } catch (final NullPointerException e) {
            Assert.assertEquals("List of stations cannot be null", e.getMessage());
        }
    }

    @Test
    public void testGetNumberOfWeatherStationsEmpty() throws Exception {
        Assert.assertEquals(0, new StatsComputer(types, new ArrayList<>()).getNumberOfWeatherStations());
    }

    @Test
    public void testGetNumberOfWeatherStations() throws Exception {
        Assert.assertEquals(2, statsComputer.getNumberOfWeatherStations());
    }

    @Test
    public void testGetNumberOfSensorsInError() throws Exception {
        Assert.assertEquals(1, statsComputer.getNumberOfSensorsInError());
    }

    @Test
    public void testGetMinValue() throws Exception {
        Assert.assertEquals(20.5, statsComputer.getMinValue("T"), 0.001);
        Assert.assertEquals(1010.0, statsComputer.getMinValue("P"), 0.001);
        Assert.assertEquals(30.0, statsComputer.getMinValue("H"), 0.001);
    }

    @Test
    public void testGetMaxValue() throws Exception {
        Assert.assertEquals(35.0, statsComputer.getMaxValue("T"), 0.001);
        Assert.assertEquals(1015.0, statsComputer.getMaxValue("P"), 0.001);
        Assert.assertEquals(70.0, statsComputer.getMaxValue("H"), 0.001);
    }

    @Test
    public void testGetMeanValue() throws Exception {
        Assert.assertEquals(28.5, statsComputer.getMeanValue("T"), 0.001);
        Assert.assertEquals(1013.0, statsComputer.getMeanValue("P"), 0.001);
        Assert.assertEquals(50.0, statsComputer.getMeanValue("H"), 0.001);
    }
}