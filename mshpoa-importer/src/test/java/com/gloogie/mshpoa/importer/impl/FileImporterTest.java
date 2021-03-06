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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for FileImporter
 */
public class FileImporterTest
{
    private static final String PREFIX_COMMENT = "#";
    public static final String FIELDS_SEPARATOR = ",";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private final DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    private final File fileOK = new File(FileImporter.class.getResource("/test_ok.txt").getPath());
    private final File fileEmpty = new File(FileImporter.class.getResource("/test_empty.txt").getPath());
    private final File fileTKO = new File(FileImporter.class.getResource("/test_T_ko.txt").getPath());
    private final File filePKO = new File(FileImporter.class.getResource("/test_P_ko.txt").getPath());
    private final File fileHKO = new File(FileImporter.class.getResource("/test_H_ko.txt").getPath());
    private final File fileUnknownType = new File(FileImporter.class.getResource("/test_unknown_type.txt").getPath());
    private final File fileNbLinesKO = new File(FileImporter.class.getResource("/test_nb_lines_ko.txt").getPath());

    private FileImporter fileImporter;

    @Before
    public void setUp() throws Exception {
        final List<MeasureType> measureTypes = new ArrayList<>();
        MeasureType measureType = new MeasureType();
        measureType.setCode("T");
        measureType.setName("temperature");
        measureTypes.add(measureType);
        measureType = new MeasureType();
        measureType.setCode("P");
        measureType.setName("pressure");
        measureTypes.add(measureType);
        measureType = new MeasureType();
        measureType.setCode("H");
        measureType.setName("humidity");
        measureTypes.add(measureType);
        final Map<String, List<MeasureField>> fieldsPerType = new LinkedHashMap<>();
        List<MeasureField> measureFields = new ArrayList<>();
        measureFields.add(MeasureField.UNIT);
        measureFields.add(MeasureField.VALUE);
        fieldsPerType.put("T", measureFields);
        measureFields = new ArrayList<>();
        measureFields.add(MeasureField.UNIT);
        measureFields.add(MeasureField.DATE);
        measureFields.add(MeasureField.VALUE);
        fieldsPerType.put("P", measureFields);
        measureFields = new ArrayList<>();
        measureFields.add(MeasureField.VALUE);
        fieldsPerType.put("H", measureFields);
        fileImporter = new FileImporter(measureTypes, PREFIX_COMMENT, DATE_PATTERN, FIELDS_SEPARATOR, fieldsPerType);
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
        final List<FailedMeasure> failedMeasures = station.getFailedMeasures();
        Assert.assertNotNull(failedMeasures);
        Assert.assertEquals(0, failedMeasures.size());

        // Check measures of station Mont Aigoual
        Measure measure = measures.get(0);
        Assert.assertNotNull(measure);
        Assert.assertNotNull(measure.getType());
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(20.5), measure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue("P".equals(measure.getType().getCode()));
        Assert.assertTrue("pressure".equals(measure.getType().getName()));
        Assert.assertEquals("BAR", measure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), measure.getDate());
        Assert.assertEquals(new Double(1014), measure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue("H".equals(measure.getType().getCode()));
        Assert.assertTrue("humidity".equals(measure.getType().getName()));
        Assert.assertEquals(new Double(25), measure.getValue());

        measure = measures.get(3);
        Assert.assertNotNull(measure);
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(21), measure.getValue());

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
        Assert.assertTrue("P".equals(measure.getType().getCode()));
        Assert.assertTrue("pressure".equals(measure.getType().getName()));
        Assert.assertEquals("BAR", measure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-02"), measure.getDate());
        Assert.assertEquals(new Double(1012), measure.getValue());

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
    public void testConsumeFileNotNound() throws Exception {
        try {
            fileImporter.consume(new File("notExistPath"));
            Assert.fail("Expected exception was not thrown");
        } catch (final ImporterException e) {
            Assert.assertEquals("File [notExistPath] is not found", e.getMessage());
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
        Assert.assertTrue("P".equals(measure.getType().getCode()));
        Assert.assertTrue("pressure".equals(measure.getType().getName()));
        Assert.assertEquals("BAR", measure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), measure.getDate());
        Assert.assertEquals(new Double(1014), measure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue("H".equals(measure.getType().getCode()));
        Assert.assertTrue("humidity".equals(measure.getType().getName()));
        Assert.assertEquals(new Double(25), measure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(21), measure.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertNotNull(failedMeasure.getException());
        Assert.assertEquals(
            "The line [T,C,] is not a valid line for the measure of type temperature. Expected number of fields was 3, actual number is 2",
            failedMeasure.getException().getMessage());
        Assert.assertEquals("T,C,", failedMeasure.getValue());
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
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(20.5), measure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue("H".equals(measure.getType().getCode()));
        Assert.assertTrue("humidity".equals(measure.getType().getName()));
        Assert.assertEquals(new Double(25), measure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(21), measure.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertNotNull(failedMeasure.getException());
        Assert.assertEquals(
            "The line [P,BAR,1014] is not a valid line for the measure of type pressure. Expected number of fields was 4, actual number is 3",
            failedMeasure.getException().getMessage());
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
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(20.5), measure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue("P".equals(measure.getType().getCode()));
        Assert.assertTrue("pressure".equals(measure.getType().getName()));
        Assert.assertEquals("BAR", measure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), measure.getDate());
        Assert.assertEquals(new Double(1014), measure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(21), measure.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertNotNull(failedMeasure);
        Assert.assertNotNull(failedMeasure.getException());
        Assert.assertEquals(
            "The line [H,25,test] is not a valid line for the measure of type humidity. Expected number of fields was 2, actual number is 3",
            failedMeasure.getException().getMessage());
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
        Assert.assertTrue("P".equals(measure.getType().getCode()));
        Assert.assertTrue("pressure".equals(measure.getType().getName()));
        Assert.assertEquals("BAR", measure.getUnit());
        Assert.assertEquals(dateFormat.parse("2014-11-01"), measure.getDate());
        Assert.assertEquals(new Double(1014), measure.getValue());

        measure = measures.get(1);
        Assert.assertNotNull(measure);
        Assert.assertTrue("H".equals(measure.getType().getCode()));
        Assert.assertTrue("humidity".equals(measure.getType().getName()));
        Assert.assertEquals(new Double(25), measure.getValue());

        measure = measures.get(2);
        Assert.assertNotNull(measure);
        Assert.assertTrue("T".equals(measure.getType().getCode()));
        Assert.assertTrue("temperature".equals(measure.getType().getName()));
        Assert.assertEquals("C", measure.getUnit());
        Assert.assertEquals(new Double(21), measure.getValue());

        final FailedMeasure failedMeasure = failedMeasures.get(0);
        Assert.assertNotNull(failedMeasure);
        Assert.assertNotNull(failedMeasure.getException());
        Assert.assertEquals("Unsupported measure type: Z", failedMeasure.getException().getMessage());
        Assert.assertEquals("Z,C,20.5", failedMeasure.getValue());
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