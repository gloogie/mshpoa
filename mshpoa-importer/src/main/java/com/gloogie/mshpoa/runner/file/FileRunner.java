package com.gloogie.mshpoa.runner.file;

import com.gloogie.mshpoa.importer.impl.FileImporter;
import com.gloogie.mshpoa.model.MeasureField;
import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.model.WeatherStation;
import com.gloogie.mshpoa.report.Reporter;
import com.gloogie.mshpoa.report.StatsComputer;
import com.gloogie.mshpoa.runner.file.exception.FileRunnerException;
import com.gloogie.mshpoa.writer.Writer;
import com.gloogie.mshpoa.writer.impl.ConsoleWriter;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Class to run the program
 */
public class FileRunner
{
    public static final String PROPERTIES_FILE_NAME = "mshpoa-importer.properties";
    public static final String MEASURE_TYPE_PREFIX = "measure.types.";
    public static final String FILE_FIELDS_PREFIX = "file.fields.";
    public static final String FILE_FIELDS_PREFIXCOMMENT = FILE_FIELDS_PREFIX + "prefixcomment";
    public static final String FILE_FIELDS_DATEPATTERN = FILE_FIELDS_PREFIX + "datepattern";
    public static final String FILE_FIELDS_SEPARATOR = FILE_FIELDS_PREFIX + "separator";
    public static final String FIELDS_SEPARATOR = "\\|";

    public static void main(final String[] args) {

        boolean success = true;

        try {
            final FileImporter fileImporter = buildFileImporter();
            final Writer writer = new ConsoleWriter();

            Validate.isTrue(args.length >= 1, "A not empty list of file paths should be passed in the arguments");

            for (final String arg : args) {
                success = processFile(fileImporter, writer, arg) && success;
            }

        } catch (final Exception e) {
            e.printStackTrace();
            success = false;
        }

        System.exit(success ? 0 : 1);
    }

    private static boolean processFile(final FileImporter fileImporter, final Writer writer, final String filePath) {
        try {
            final File file = new File(filePath);
            final List<WeatherStation> stations = fileImporter.consume(file);
            final StatsComputer statsComputer = new StatsComputer(fileImporter.getMeasureTypes(), stations);
            final Reporter reporter = new Reporter(statsComputer, writer);
            reporter.writeReport(file.getName());
            return true;
        } catch (final Exception e) {
            System.out.printf("An error occurred while processing the file %s%n", filePath);
            e.printStackTrace();
            return false;
        }
    }

    private static FileImporter buildFileImporter() throws FileRunnerException {

        final Properties properties = new Properties();
        final InputStream inputStream = FileRunner.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

        if (inputStream == null) {
            throw new FileRunnerException("File [" + PROPERTIES_FILE_NAME + "] is not found in the classpath");
        }

        // Load the properties file
        try {
            properties.load(inputStream);
        } catch (final IOException e) {
            throw new FileRunnerException("An error occurred while reading the file [" + PROPERTIES_FILE_NAME + "]", e);
        }

        // Build the list of measure types
        final List<MeasureType> measureTypes = new ArrayList<>();
        for (final Object key : properties.keySet()) {
            final String propertyName = (String) key;
            if (propertyName.startsWith(MEASURE_TYPE_PREFIX)) {
                final MeasureType measureType = new MeasureType();
                measureType.setCode(propertyName.replace(MEASURE_TYPE_PREFIX, ""));
                measureType.setName(properties.getProperty(propertyName));
                measureTypes.add(measureType);
            }
        }

        // Build the fields for each measure type
        final Map<String, List<MeasureField>> fieldsPerType = new LinkedHashMap<>();
        for (final MeasureType type : measureTypes) {
            final List<MeasureField> measureFields = new ArrayList<>();
            final String property = properties.getProperty(FILE_FIELDS_PREFIX + type.getCode());
            final String[] fields = property.split(FIELDS_SEPARATOR);
            for (final String field : fields) {
                try {
                    measureFields.add(MeasureField.valueOf(field));
                } catch (final IllegalArgumentException e) {
                    throw new FileRunnerException(
                        "The field [" + field + "] is not valid. Valid values are: VALUE, UNIT and DATE", e);
                }
            }
            fieldsPerType.put(type.getCode(), measureFields);
        }

        final String prefixComment = properties.getProperty(FILE_FIELDS_PREFIXCOMMENT);
        if (prefixComment == null) {
            throw new FileRunnerException("Missing property: " + FILE_FIELDS_PREFIXCOMMENT);
        }

        final String dateFormat = properties.getProperty(FILE_FIELDS_DATEPATTERN);
        if (dateFormat == null) {
            throw new FileRunnerException("Missing property: " + FILE_FIELDS_DATEPATTERN);
        }

        final String fieldsSeparator = properties.getProperty(FILE_FIELDS_SEPARATOR);
        if (fieldsSeparator == null) {
            throw new FileRunnerException("Missing property: " + FILE_FIELDS_SEPARATOR);
        }

        return new FileImporter(measureTypes, prefixComment, dateFormat, fieldsSeparator, fieldsPerType);
    }
}
