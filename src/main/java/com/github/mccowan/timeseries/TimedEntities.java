package com.github.mccowan.timeseries;

import java.util.Comparator;

/**
 * @author mccowan
 */
public class TimedEntities {
    final static Comparator<TimedEntity> ASCENDING_COMPARATOR = new Comparator<TimedEntity>() {
        @Override
        public int compare(final TimedEntity o1, final TimedEntity o2) {
            return Long.compare(o1.getTime(), o2.getTime());
        }
    };
}
