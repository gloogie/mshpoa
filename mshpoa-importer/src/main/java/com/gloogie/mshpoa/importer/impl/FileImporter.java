package com.gloogie.mshpoa.importer.impl;

import com.gloogie.mshpoa.importer.Importer;
import com.gloogie.mshpoa.importer.exception.ImporterException;
import com.gloogie.mshpoa.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation class of Importer for files
 */
public class FileImporter extends Importer<File>
{
    public static final int NUMBER_FIELDS_OF_LINE_FOR_WEATHER_STATION = 2;

    private final String datePattern;
    private final DateFormat dateFormat;

    private final Map<String, MeasureType> measureTypesPerCode;
    private final String prefixComment;
    private final String fieldsSeparator;
    private final Map<String, List<MeasureField>> fieldsPerType;

    /**
     * Constructor for FileImporter
     *
     * @param measureTypes    the list of possible measure types
     * @param prefixComment   the prefix for comments (lines starting with this prefix are ignored in the file)
     * @param datePattern     date pattern uased to parse dates in measures lines
     * @param fieldsSeparator separator used between each fields in measures lines
     * @param fieldsPerType   list of measure fields per measure type
     */
    public FileImporter(final List<MeasureType> measureTypes, final String prefixComment, final String datePattern,
                        final String fieldsSeparator, final Map<String, List<MeasureField>> fieldsPerType) {

        Validate.notNull(measureTypes, "List of measure types cannot be null");
        Validate.notBlank(prefixComment, "Prefix comment cannot be blank");
        Validate.notBlank(datePattern, "Date pattern cannot be blank");
        Validate.notBlank(fieldsSeparator, "Fields separator cannot be blank");
        Validate.notNull(fieldsPerType, "Fields per type cannot be null");

        setMeasureTypes(measureTypes);
        this.measureTypesPerCode = new LinkedHashMap<>();

        // Check that fieldsPerType contains all provided measure types and build the map of measure types indexed by code
        for (final MeasureType type : measureTypes) {
            if (!fieldsPerType.containsKey(type.getCode())) {
                throw new IllegalArgumentException("fieldsPerType does not contain the key [" + type.getCode() + "]");
            }
            this.measureTypesPerCode.put(type.getCode(), type);
        }

        this.prefixComment = prefixComment;
        this.datePattern = datePattern;
        this.dateFormat = new SimpleDateFormat(datePattern);
        this.fieldsSeparator = fieldsSeparator;
        this.fieldsPerType = fieldsPerType;
    }

    @Override
    public List<WeatherStation> consume(final File file) throws ImporterException {

        if (file == null) {
            throw new ImporterException("File cannot be null");
        }

        if (!file.exists()) {
            throw new ImporterException(String.format("File [%s] is not found", file.getPath()));
        }

        final List<WeatherStation> stations = new ArrayList<>();
        final List<String> lines;
        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {

            lines = stream.filter(line -> StringUtils.isNotBlank(line) && !line.startsWith(prefixComment))
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
        final String[] fields = lines.get(index).split(fieldsSeparator);
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
        final String[] fieldsValues = line.split(fieldsSeparator);

        final String typeField = fieldsValues[0].trim();
        final MeasureType type = measureTypesPerCode.get(typeField);

        if (type == null) {
            throw new ImporterException(String.format("Unsupported measure type: %s", typeField));
        }

        final List<MeasureField> fields = fieldsPerType.get(type.getCode());

        final Measure measure = new Measure();
        if (fieldsValues.length != fields.size() + 1) {
            final String message = String.format(
                "The line [%s] is not a valid line for the measure of type %s. Expected number of fields was %d, actual number is %d",
                line, type.getName(), fields.size() + 1, fieldsValues.length);
            throw new ImporterException(message);
        }
        measure.setType(type);

        // Fill each configured field on the measure
        int i = 1;
        for (final MeasureField field : fields) {
            switch (field) {
                case VALUE: {
                    measure.setValue(parseValue(fieldsValues[i], line));
                    break;
                }
                case UNIT: {
                    measure.setUnit(fieldsValues[i]);
                    break;
                }
                case DATE: {
                    measure.setDate(parseDate(fieldsValues[i], line));
                    break;
                }
            }
            i++;
        }

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
                line, datePattern);
            throw new ImporterException(message, e);
        }
    }
}
