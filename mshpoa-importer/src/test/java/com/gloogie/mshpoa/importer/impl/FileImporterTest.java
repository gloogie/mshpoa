package com.gloogie.mshpoa.importer.impl;

import com.gloogie.mshpoa.importer.exception.ImporterException;
import com.gloogie.mshpoa.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Test class for FileImporter
 */
public class FileImporterTest
{
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final File fileOK = new File(FileImporter.class.getResource("/test_ok.txt").getPath());
    private final File fileEmpty = new File(FileImporter.class.getResource("/test_empty.txt").getPath());
    private final File fileTKO = new File(FileImporter.class.getResource("/test_T_ko.txt").getPath());
    private final File filePKO = new File(FileImporter.class.getResource("/test_P_ko.txt").getPath());
    private final File fileHKO = new File(FileImporter.class.getResource("/test_H_ko.txt").getPath());
    private final File fileUnknownType = new File(FileImporter.class.getResource("/test_unknown_type.txt").getPath());
    private final File fileNbLinesKO = new File(FileImporter.class.getResource("/test_nb_lines_ko.txt").getPath());

    private final FileImporter fileImporter = new FileImporter();

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testConsumeFileOK() throws Exception {
        final List<WeatherStation> stations = fileImporter.consume(fileOK);

        Assert.assertNotNull(stations);
        Assert.assertEquals(4, stations.size());

        // Check station Mont Aigoual
        WeatherStation station = stations.get(0);
        Assert.assertNotNull(station);
        Assert.assertEquals("Mont Aigoual", station.getName());
        List<Measure> measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(6, measures.size());

        // Check measures of station Mont Aigoual
        Measure measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        Temperature temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(20.5), temperature.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Pressure);
        Pressure pressure = (Pressure) measure;
        Assert.assertEquals("BAR", pressure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), pressure.getDate());
        Assert.assertEquals(new Double(1014), pressure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Humidity);
        final Humidity humidity = (Humidity) measure;
        Assert.assertEquals(new Double(25), humidity.getValue());

        measure = measures.get(3);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(21), temperature.getValue());

        /** To be continued... **/

        // Check station Clapiers
        station = stations.get(1);
        Assert.assertNotNull(station);
        Assert.assertEquals("Clapiers", station.getName());
        measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(9, measures.size());

        // Check measures of station Clapiers
        measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Pressure);
        pressure = (Pressure) measure;
        Assert.assertEquals("BAR", pressure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-02"), pressure.getDate());
        Assert.assertEquals(new Double(1012), pressure.getValue());

        /** To be continued... **/
    }

    @Test
    public void testConsumeFileNull() throws Exception {
        try {
            fileImporter.consume(null);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals("File cannot be null", e.getMessage());
        }
    }

    @Test
    public void testConsumeFileEmpty() throws Exception {
        final List<WeatherStation> stations = fileImporter.consume(fileEmpty);

        Assert.assertNotNull(stations);
        Assert.assertEquals(0, stations.size());
    }

    @Test
    public void testConsumeFileWrongNumberOfTemperatureFields() throws Exception {

        try {
            fileImporter.consume(fileTKO);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals(
                "The line [T,C,] is not a valid line for the temperature measure. Expected number of fields was 3, actual number is 2",
                e.getMessage());
        }
    }

    @Test
    public void testConsumeFileWrongNumberOfPressureFields() throws Exception {

        try {
            fileImporter.consume(filePKO);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals(
                "The line [P,BAR,1014] is not a valid line for pressure measure. Expected number of fields was 4, actual number is 3",
                e.getMessage());
        }
    }

    @Test
    public void testConsumeFileWrongNumberOfHumidityFields() throws Exception {

        try {
            fileImporter.consume(fileHKO);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals(
                "The line [H,25,test] is not a valid line for humidity measure. Expected number of fields was 2, actual number is 3",
                e.getMessage());
        }
    }

    @Test
    public void testConsumeFileUnknownMeasureType() throws Exception {

        try {
            fileImporter.consume(fileUnknownType);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals("Unsupported measure type: Z", e.getMessage());
        }
    }

    @Test
    public void testConsumeFileWrongNumberOfLines() throws Exception {

        try {
            fileImporter.consume(fileNbLinesKO);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals("Unsupported measure type: Clapiers", e.getMessage());
        }
    }
}