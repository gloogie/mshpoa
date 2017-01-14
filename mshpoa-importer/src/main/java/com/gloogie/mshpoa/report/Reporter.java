package com.gloogie.mshpoa.report;

import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.writer.Writer;
import org.apache.commons.lang3.Validate;

/**
 * Class to writer reports on computed stats
 */
public class Reporter
{
    private final StatsComputer statsComputer;
    private final Writer writer;

    /**
     * Constructor for Reporter
     *
     * @param statsComputer the stats computer which give the stats to add in the report
     * @param writer        the writer to use to write the report
     */
    public Reporter(final StatsComputer statsComputer, final Writer writer) {
        Validate.notNull(writer, "Writer cannot be null");
        Validate.notNull(statsComputer, "Stats computer cannot be null");

        this.statsComputer = statsComputer;
        this.writer = writer;
    }

    public void writeReport(final String sourceName) {
        writer.write("=====================================================");
        writer.write(String.format("REPORT OF SOURCE %s", sourceName));
        writer.write("=====================================================");
        writer.write(String.format("Number of weather stations: %d", statsComputer.getNumberOfWeatherStations()));
        writer.write(String.format("Number of failed measures: %d", statsComputer.getNumberOfSensorsInError()));

        for (final MeasureType type : statsComputer.getMeasureTypes()) {
            writer.write("=====================================================");
            writer.write(String.format("Measures of type %s:", type.getName()));
            writer.write(String.format("Min value: %s", statsComputer.getMinValue(type.getCode())));
            writer.write(String.format("Max value: %s", statsComputer.getMaxValue(type.getCode())));
            writer.write(String.format("Mean value: %s", statsComputer.getMeanValue(type.getCode())));
        }

        writer.write("=====================================================");
        writer.write(String.format("END REPORT OF SOURCE %s", sourceName));
        writer.write("=====================================================");
        writer.write("");
    }
}
