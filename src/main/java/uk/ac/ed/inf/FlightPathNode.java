package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Stores a node in a flight path.
 */
public record FlightPathNode(String orderNo, LngLat fromCoordinate, double angle, LngLat toCoordinate,
                             int ticksSinceStartOfCalculation) {
    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param orderNo        The order number.
     * @param flightPathNode The {@link FlightPathNode} to copy.
     */
    public FlightPathNode(String orderNo, FlightPathNode flightPathNode) {
        this(orderNo, flightPathNode.fromCoordinate, flightPathNode.angle, flightPathNode.toCoordinate, (int) (System.nanoTime() - PizzaDronz.startTime));
    }

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param angle          The angle between the two coordinates.
     * @param flightPathNode The {@link FlightPathNode} to copy.
     */
    public FlightPathNode(double angle, FlightPathNode flightPathNode) {
        this(flightPathNode.orderNo, flightPathNode.fromCoordinate, angle, flightPathNode.toCoordinate, (int) (System.nanoTime() - PizzaDronz.startTime));
    }

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param fromCoordinate The starting coordinate.
     * @param angle          The angle between the two coordinates.
     * @param toCoordinate   The ending coordinate.
     */
    public FlightPathNode(LngLat fromCoordinate, double angle, LngLat toCoordinate) {
        this(null, fromCoordinate, angle, toCoordinate, (int) (System.nanoTime() - PizzaDronz.startTime));
    }

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param orderNo        The order number.
     * @param fromCoordinate The starting coordinate.
     * @param angle          The angle between the two coordinates.
     * @param toCoordinate   The ending coordinate.
     */
    public FlightPathNode(String orderNo, LngLat fromCoordinate, double angle, LngLat toCoordinate) {
        this(orderNo, fromCoordinate, angle, toCoordinate, (int) (System.nanoTime() - PizzaDronz.startTime));
    }
}
