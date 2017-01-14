package com.gloogie.mshpoa.report;

import com.gloogie.mshpoa.model.Measure;
import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.model.WeatherStation;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Class to compute some stats on sensors measures
 */
public class StatsComputer
{
    private List<MeasureType> measureTypes = new ArrayList<>();
    private List<WeatherStation> stations = new ArrayList<>();
    private final Comparator<Measure> measureComparator = (m1, m2) -> Double.compare(m1.getValue(), m2.getValue());

    /**
     * Constructor for StatsComputer
     *
     * @param measureTypes list of distincts measure types
     * @param stations     list of stations on which the results will be computed
     */
    public StatsComputer(final List<MeasureType> measureTypes, final List<WeatherStation> stations) {
        Validate.notNull(measureTypes, "List of measure types cannot be null");
        Validate.notNull(stations, "List of stations cannot be null");
        this.measureTypes = measureTypes;
        this.stations = stations;
    }

    /**
     * @return the list of measure types
     */
    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    /**
     * Compute the number of weather stations
     *
     * @return the number of weather stations
     */
    public long getNumberOfWeatherStations() {
        return stations.size();
    }

    /**
     * Compute the number of failed measures
     *
     * @return the number of failed measures
     */
    public long getNumberOfSensorsInError() {
        long result = 0;
        for (final WeatherStation station : stations) {
            result += station.getFailedMeasures().size();
        }
        return result;
    }

    /**
     * Compute the minimum value for measures of the specified type
     *
     * @param type measure type
     * @return the minimum value
     */
    public double getMinValue(final String type) {

        Validate.notBlank(type, "type cannot be blank");

        double result = Double.MAX_VALUE;
        for (final WeatherStation station : stations) {
            final Optional<Measure> min = station.getMeasures()
                                                 .stream()
                                                 .filter(measure -> measure.getType() != null && type.equals(
                                                     measure.getType().getCode()))
                                                 .min(measureComparator);
            if (min.isPresent() && min.get().getValue() < result) {
                result = min.get().getValue();
            }
        }
        return result;
    }

    /**
     * Compute the maximum value for measures of the specified type
     *
     * @param type measure type
     * @return the maximum value
     */
    public double getMaxValue(final String type) {

        Validate.notBlank(type, "type cannot be blank");

        double result = Double.MIN_VALUE;
        for (final WeatherStation station : stations) {
            final Optional<Measure> max = station.getMeasures()
                                                 .stream()
                                                 .filter(measure -> measure.getType() != null && type.equals(
                                                     measure.getType().getCode()))
                                                 .max(measureComparator);
            if (max.isPresent() && max.get().getValue() > result) {
                result = max.get().getValue();
            }
        }
        return result;
    }

    /**
     * Compute the mean value for measures of the specified type
     *
     * @param type measure type
     * @return the mean value
     */
    public double getMeanValue(final String type) {

        Validate.notBlank(type, "type cannot be blank");

        double sum = 0;
        int count = 0;
        for (final WeatherStation station : stations) {
            count += station.getMeasures()
                            .stream()
                            .filter(measure -> measure.getType() != null && type.equals(measure.getType().getCode()))
                            .count();
            sum += station.getMeasures()
                          .stream()
                          .filter(measure -> measure.getType() != null && type.equals(measure.getType().getCode()))
                          .mapToDouble(Measure::getValue).sum();
        }
        if (count > 0) {
            return sum / count;
        } else {
            return 0;
        }
    }
}
