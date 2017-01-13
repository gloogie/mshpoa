package com.gloogie.mshpoa.importer.impl;

import com.gloogie.mshpoa.importer.Importer;
import com.gloogie.mshpoa.importer.exception.ImporterException;
import com.gloogie.mshpoa.model.FailedMeasure;
import com.gloogie.mshpoa.model.Measure;
import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.model.WeatherStation;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation class of Importer for files
 */
public class FileImporter implements Importer<File>
{
    public static final String PREFIX_COMMENT = "#";
    public static final int NUMBER_FIELDS_OF_LINE_FOR_WEATHER_STATION = 2;
    public static final int NUMBER_FIELDS_OF_LINE_FOR_TEMPERATURE_MEASURE = 3;
    public static final int NUMBER_FIELDS_OF_LINE_FOR_PRESSURE_MEASURE = 4;
    public static final int NUMBER_FIELDS_OF_LINE_FOR_HUMIDITY_MEASURE = 2;
    public static final String FIELDS_SEPARATOR = ",";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private final DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public List<WeatherStation> consume(final File file) throws ImporterException {

        if (file == null) {
            throw new ImporterException("File cannot be null");
        }

        final List<WeatherStation> stations = new ArrayList<>();
        final List<String> lines;
        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {

            lines = stream.filter(line -> StringUtils.isNotBlank(line) && !line.startsWith(PREFIX_COMMENT))
                          .collect(Collectors.toList());

        } catch (final IOException e) {
            final String message = String.format("An error occurred while reading the file [%s]", file.getPath());
            throw new ImporterException(message, e);
        }
        for (int i = 0; i < lines.size(); i++) {
            final WeatherStation station = parseWeatherStation(lines, i);
            stations.add(station);
            // Skip all lines of measures of the current station to parse the next station
            i += station.getMeasures().size() + station.getFailedMeasures().size();
        }
        return stations;
    }

    private WeatherStation parseWeatherStation(final List<String> lines, final int index) throws ImporterException {
        final WeatherStation station = new WeatherStation();
        final String[] fields = lines.get(index).split(FIELDS_SEPARATOR);
        if (fields.length != NUMBER_FIELDS_OF_LINE_FOR_WEATHER_STATION) {
            final String message = String.format(
                "The line [%s] is not a valid line for the station. Expected number of fields was %d, actual number is %d",
                lines.get(index), NUMBER_FIELDS_OF_LINE_FOR_WEATHER_STATION, fields.length);
            throw new ImporterException(message);
        }
        station.setName(fields[0]);
        final int nbMeasures;
        try {
            nbMeasures = parseNumberOfMeasures(fields[1], station.getName());
        } catch (final NumberFormatException e) {
            final String message = String.format("Number of measures [%s] is not valid for station [%s]", fields[1],
                                                 fields[0]);
            throw new ImporterException(message, e);
        }
        final int start = index + 1;
        final int end = Math.min(lines.size(), index + nbMeasures + 1);
        final List<String> measuresLines = lines.subList(start, end);
        parseAndAddMeasuresOfStation(station, measuresLines);
        return station;
    }

    private void parseAndAddMeasuresOfStation(final WeatherStation station, final List<String> lines)
        throws ImporterException {
        final List<Measure> measures = new ArrayList<>();
        final List<FailedMeasure> failedMeasures = new ArrayList<>();
        for (final String line : lines) {
            try {
                measures.add(parseMeasure(line));
            } catch (final ImporterException e) {
                final FailedMeasure failedMeasure = new FailedMeasure();
                failedMeasure.setException(e);
                failedMeasure.setValue(line);
                failedMeasures.add(failedMeasure);
            }
        }
        station.setMeasures(measures);
        station.setFailedMeasures(failedMeasures);
    }

    private Measure parseMeasure(final String line) throws ImporterException {
        final String[] fields = line.split(FIELDS_SEPARATOR);

        final String typeField = fields[0].trim();
        final MeasureType type;
        try {
            type = MeasureType.valueOf(typeField);
        } catch (final IllegalArgumentException e) {
            throw new ImporterException(String.format("Unsupported measure type: %s", typeField));
        }
        switch (type) {
            case T: {
                return parseTemperatureMeasure(line, fields);
            }
            case P: {
                return parsePressureMeasure(line, fields);
            }
            case H: {
                return parseHumidityMeasure(line, fields);
            }
            default: {
                throw new ImporterException(String.format("Unsupported measure type: %s", type.getName()));
            }
        }
    }

    private Measure parseTemperatureMeasure(final String line, final String[] fields) throws ImporterException {
        final Measure measure = new Measure();
        if (fields.length != NUMBER_FIELDS_OF_LINE_FOR_TEMPERATURE_MEASURE) {
            final String message = String.format(
                "The line [%s] is not a valid line for the temperature measure. Expected number of fields was %d, actual number is %d",
                line, NUMBER_FIELDS_OF_LINE_FOR_TEMPERATURE_MEASURE, fields.length);
            throw new ImporterException(message);
        }
        measure.setType(MeasureType.T);
        measure.setUnit(fields[1]);
        measure.setValue(parseValue(fields[2], line));

        return measure;
    }

    private Measure parsePressureMeasure(final String line, final String[] fields) throws ImporterException {
        final Measure measure = new Measure();
        if (fields.length != NUMBER_FIELDS_OF_LINE_FOR_PRESSURE_MEASURE) {
            final String message = String.format(
                "The line [%s] is not a valid line for pressure measure. Expected number of fields was %d, actual number is %d",
                line, NUMBER_FIELDS_OF_LINE_FOR_PRESSURE_MEASURE, fields.length);
            throw new ImporterException(message);
        }
        measure.setType(MeasureType.P);
        measure.setUnit(fields[1]);
        measure.setDate(parseDate(fields[2], line));
        measure.setValue(parseValue(fields[3], line));

        return measure;
    }

    private Measure parseHumidityMeasure(final String line, final String[] fields) throws ImporterException {
        final Measure measure = new Measure();
        if (fields.length != NUMBER_FIELDS_OF_LINE_FOR_HUMIDITY_MEASURE) {
            final String message = String.format(
                "The line [%s] is not a valid line for humidity measure. Expected number of fields was %d, actual number is %d",
                line, NUMBER_FIELDS_OF_LINE_FOR_HUMIDITY_MEASURE, fields.length);
            throw new ImporterException(message);
        }
        measure.setType(MeasureType.H);
        measure.setValue(parseValue(fields[1], line));

        return measure;
    }

    private int parseNumberOfMeasures(final String field, final String stationName) throws ImporterException {
        try {
            return Integer.parseInt(field.trim());
        } catch (final NumberFormatException e) {
            final String message = String.format("Number of measures [%s] is not valid for station [%s]", field,
                                                 stationName);
            throw new ImporterException(message, e);
        }
    }

    private double parseValue(final String field, final String line) throws ImporterException {
        try {
            return Double.parseDouble(field.trim());
        } catch (final NumberFormatException e) {
            final String message = String.format(
                "Value of measure [%s] in the line [%s] is not a valid double", field, line);
            throw new ImporterException(message, e);
        }
    }

    private Date parseDate(final String field, final String line) throws ImporterException {
        try {
            return dateFormat.parse(field.trim());
        } catch (final ParseException e) {
            final String message = String.format(
                "Date of measure [%s] in the line [%s] is not a valid date (Expected format is %s)", field,
                line, DATE_PATTERN);
            throw new ImporterException(message, e);
        }
    }
}
