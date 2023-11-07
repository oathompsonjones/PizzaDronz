package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Stores a node in a flight path.
 */
public record LngLatAngle(LngLat lngLat, double angle, int ticks) {
    /**
     * Creates an instance of the {@link LngLatAngle} class.
     *
     * @param lngLat The {@link LngLat} to copy.
     * @param angle  The angle between the two coordinates.
     */
    public LngLatAngle(LngLat lngLat, double angle) {
        this(lngLat, angle, 0);
    }
}
