package com.github.mccowan.timeseries;

/**
 * Describes an entity associated with a time index.
 *
 * @author mccowan
 */
public interface TimedEntity {
    /** Returns the time index associated with this entity. */
    long getTime();
}
