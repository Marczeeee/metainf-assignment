package hu.metainf.jiracsvuploader.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data holder class for raw values used to produce statistical data.
 *
 */
public final class StatData {
    /** Ctor. */
    private StatData() {}

    /** {@link Map} for storing runtime raw stat data values. */
    private static final Map<String, Long> DATA_VALUE_MAP = new ConcurrentHashMap<String, Long>();

    /**
     * Adds a value to a property, updating any previous value set.
     *
     * @param propertyId
     *            property identifier value
     * @param value
     *            new property value
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long addValue(final String propertyId, final Long value) {
        return DATA_VALUE_MAP.put(propertyId, value);
    }

    /**
     * Sets an average value to a property. When setting a new value, the average of the old value
     * and the new value is calculated and set as value for the property.
     *
     * @param propertyId
     *            property identifier value
     * @param value
     *            new value to be averaged
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long setAverageValue(final String propertyId, final Long value) {
        if (!DATA_VALUE_MAP.containsKey(propertyId)) {
            StatData.addValue(propertyId, 0L);
        }
        final Long currentVal = DATA_VALUE_MAP.get(propertyId);
        final Long newVal = (currentVal + value) / 2;
        return StatData.addValue(propertyId, newVal);
    }

    /**
     * Sets a minimal value to a property. When setting a new value, the old value and the new value
     * is compared and the lower value is set as value for the property.
     *
     * @param propertyId
     *            property identifier value
     * @param value
     *            new value to be calculates an minimum
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long setMinValue(final String propertyId, final Long value) {
        if (!DATA_VALUE_MAP.containsKey(propertyId)) {
            StatData.addValue(propertyId, Long.MAX_VALUE);
        }
        final Long currentVal = DATA_VALUE_MAP.get(propertyId);
        if (value < currentVal) {
            return StatData.addValue(propertyId, value);
        }
        return currentVal;
    }

    /**
     * Sets a maximal value to a property. When setting a new value, the old value and the new value
     * is compared and the higher value is set as value for the property.
     *
     * @param propertyId
     *            property identifier value
     * @param value
     *            new value to be calculates an maximum
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long setMaxValue(final String propertyId, final Long value) {
        if (!DATA_VALUE_MAP.containsKey(propertyId)) {
            StatData.addValue(propertyId, Long.MIN_VALUE);
        }
        final Long currentVal = DATA_VALUE_MAP.get(propertyId);
        if (value > currentVal) {
            return StatData.addValue(propertyId, value);
        }
        return currentVal;
    }

    /**
     * Increments the value of a property by 1.
     *
     * @param propertyId
     *            property identifier value
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long addIncrementedValue(final String propertyId) {
        return StatData.addSumValue(propertyId, 1L);
    }

    /**
     * Increments the value of a property by the given increment.
     *
     * @param propertyId
     *            property identifier value
     * @param increment
     *            new value to be added to the summarized value
     * @return The previous value set, or <code>null</code> if no previous value was set to the
     *         property id.
     */
    public static Long addSumValue(final String propertyId, final Long increment) {
        if (!DATA_VALUE_MAP.containsKey(propertyId)) {
            StatData.addValue(propertyId, 0L);
        }
        final Long currentVal = DATA_VALUE_MAP.get(propertyId);
        return StatData.addValue(propertyId, currentVal + increment);
    }

    /**
     * Returns the value of a property.
     *
     * @param propertyId
     *            property identifier value
     * @return The value stored for the property, or 0 if no value is set for the property.
     */
    public static Long getValue(final String propertyId) {
        Long value = DATA_VALUE_MAP.get(propertyId);
        if (value == null) {
            value = 0L;
        }
        return value;
    }
}
