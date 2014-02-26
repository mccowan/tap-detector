package com.github.mccowan.timeseries;

/**
 * TODO$(user): Class description
 *
 * @author mccowan
 */
public interface TimeSeriesObservation<ENTITY_TYPE extends TimedEntity> {
    TimeSeriesView<ENTITY_TYPE> context();
    ENTITY_TYPE point();
}
