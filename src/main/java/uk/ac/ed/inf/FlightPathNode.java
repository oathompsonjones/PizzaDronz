package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Stores a node in a flight path.
 */
public record FlightPathNode(
        String orderNo,
        LngLat fromCoordinate,
        double angle,
        LngLat toCoordinate,
        int ticksSinceStartOfCalculation
) {
    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param orderNo The order number.
     * @param node    The {@link FlightPathNode} to copy.
     */
    public FlightPathNode(String orderNo, FlightPathNode node) {
        this(orderNo,
             node.fromCoordinate,
             node.angle,
             node.toCoordinate,
             (int) (System.nanoTime() - PizzaDronz.startTime)
            );
    }

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param angle The angle between the two coordinates.
     * @param node  The {@link FlightPathNode} to copy.
     */
    public FlightPathNode(double angle, FlightPathNode node) {
        this(
                node.orderNo,
                node.fromCoordinate,
                angle,
                node.toCoordinate,
                (int) (System.nanoTime() - PizzaDronz.startTime)
            );
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
