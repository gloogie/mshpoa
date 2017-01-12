package com.gloogie.mshpoa.importer;

import com.gloogie.mshpoa.importer.exception.ImporterException;
import com.gloogie.mshpoa.model.WeatherStation;

import java.util.List;

/**
 * Interface for importers
 */
public interface Importer<SourceType>
{
    List<WeatherStation> consume(SourceType source) throws ImporterException;
}
