package com.gloogie.mshpoa.importer;

import com.gloogie.mshpoa.importer.exception.ImporterException;
import com.gloogie.mshpoa.model.MeasureType;
import com.gloogie.mshpoa.model.WeatherStation;

import java.util.List;

/**
 * Interface for importers
 */
public abstract class Importer<SourceType>
{
    List<MeasureType> measureTypes;

    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    protected void setMeasureTypes(final List<MeasureType> measureTypes) {
        this.measureTypes = measureTypes;
    }

    /**
     * Parse the specified source which contains measures of weather stations
     *
     * @param source source which contains the measures
     * @return the list of weather stations parsed
     * @throws ImporterException if an error occurred
     */
    public abstract List<WeatherStation> consume(SourceType source) throws ImporterException;
}
