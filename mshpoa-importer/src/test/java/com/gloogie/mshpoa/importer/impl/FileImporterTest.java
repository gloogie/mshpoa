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

        final List<WeatherStation> stations = fileImporter.consume(fileTKO);

        Assert.assertNotNull(stations);
        Assert.assertEquals(4, stations.size());

        // Check station Mont Aigoual
        final WeatherStation station = stations.get(0);
        Assert.assertNotNull(station);
        Assert.assertEquals("Mont Aigoual", station.getName());
        final List<Measure> measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(5, measures.size());
        final List<FailedMeasure> failedMeasures = station.getFailedMeasures();
        Assert.assertNotNull(failedMeasures);
        Assert.assertEquals(1, failedMeasures.size());

        // Check measures of station Mont Aigoual
        Measure measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Pressure);
        final Pressure pressure = (Pressure) measure;
        Assert.assertEquals("BAR", pressure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), pressure.getDate());
        Assert.assertEquals(new Double(1014), pressure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Humidity);
        final Humidity humidity = (Humidity) measure;
        Assert.assertEquals(new Double(25), humidity.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        final Temperature temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(21), temperature.getValue());

        final FailedMeasure failedmeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedmeasure);
        Assert.assertEquals("T,C,", failedmeasure.getValue());
    }

    @Test
    public void testConsumeFileWrongNumberOfPressureFields() throws Exception {

        final List<WeatherStation> stations = fileImporter.consume(filePKO);

        Assert.assertNotNull(stations);
        Assert.assertEquals(4, stations.size());

        // Check station Mont Aigoual
        final WeatherStation station = stations.get(0);
        Assert.assertNotNull(station);
        Assert.assertEquals("Mont Aigoual", station.getName());
        final List<Measure> measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(5, measures.size());
        final List<FailedMeasure> failedMeasures = station.getFailedMeasures();
        Assert.assertNotNull(failedMeasures);
        Assert.assertEquals(1, failedMeasures.size());

        // Check measures of station Mont Aigoual
        Measure measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        Temperature temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(20.5), temperature.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Humidity);
        final Humidity humidity = (Humidity) measure;
        Assert.assertEquals(new Double(25), humidity.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(21), temperature.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertEquals("P,BAR,1014", failedMeasure.getValue());
    }

    @Test
    public void testConsumeFileWrongNumberOfHumidityFields() throws Exception {

        final List<WeatherStation> stations = fileImporter.consume(fileHKO);

        Assert.assertNotNull(stations);
        Assert.assertEquals(4, stations.size());

        // Check station Mont Aigoual
        final WeatherStation station = stations.get(0);
        Assert.assertNotNull(station);
        Assert.assertEquals("Mont Aigoual", station.getName());
        final List<Measure> measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(5, measures.size());
        final List<FailedMeasure> failedMeasures = station.getFailedMeasures();
        Assert.assertNotNull(failedMeasures);
        Assert.assertEquals(1, failedMeasures.size());

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
        final Pressure pressure = (Pressure) measure;
        Assert.assertEquals("BAR", pressure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), pressure.getDate());
        Assert.assertEquals(new Double(1014), pressure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(21), temperature.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertEquals("H,25,test", failedMeasure.getValue());
    }

    @Test
    public void testConsumeFileUnknownMeasureType() throws Exception {

        final List<WeatherStation> stations = fileImporter.consume(fileUnknownType);

        Assert.assertNotNull(stations);
        Assert.assertEquals(4, stations.size());

        // Check station Mont Aigoual
        final WeatherStation station = stations.get(0);
        Assert.assertNotNull(station);
        Assert.assertEquals("Mont Aigoual", station.getName());
        final List<Measure> measures = station.getMeasures();
        Assert.assertNotNull(measures);
        Assert.assertEquals(5, measures.size());
        final List<FailedMeasure> failedMeasures = station.getFailedMeasures();
        Assert.assertNotNull(failedMeasures);
        Assert.assertEquals(1, failedMeasures.size());

        // Check measures of station Mont Aigoual
        Measure measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Pressure);
        final Pressure pressure = (Pressure) measure;
        Assert.assertEquals("BAR", pressure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), pressure.getDate());
        Assert.assertEquals(new Double(1014), pressure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Humidity);
        final Humidity humidity = (Humidity) measure;
        Assert.assertEquals(new Double(25), humidity.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue(measure instanceof Temperature);
        final Temperature temperature = (Temperature) measure;
        Assert.assertEquals("C", temperature.getUnit());
        Assert.assertEquals(new Double(21), temperature.getValue());

        final FailedMeasure failedmeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedmeasure);
        Assert.assertEquals("Z,C,20.5", failedmeasure.getValue());
    }

    @Test
    public void testConsumeFileWrongNumberOfLines() throws Exception {

        try {
            fileImporter.consume(fileNbLinesKO);
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals(
                "The line [P,BAR,2014-11-01,1001] is not a valid line for the station. Expected number of fields was 2, actual number is 4",
                e.getMessage());
        }
    }
}