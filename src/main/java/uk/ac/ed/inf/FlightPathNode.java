package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Stores a node in a flight path.
 */
public class FlightPathNode {
    /**
     * Stores the starting coordinate.
     */
    private final LngLat fromCoordinate;
    /**
     * Stores the ending coordinate.
     */
    private final LngLat toCoordinate;
    /**
     * Stores the angle between the two coordinates.
     */
    private       double angle;
    /**
     * Stores the order number.
     */
    private       String orderNo;
    /**
     * Stores the number of ticks since the start of the calculation.
     */
    private       int    ticksSinceStartOfCalculation;

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param flightPathNode The {@link FlightPathNode} to copy.
     */
    public FlightPathNode(FlightPathNode flightPathNode) {
        this.orderNo = flightPathNode.orderNo;
        this.fromCoordinate = flightPathNode.fromCoordinate;
        this.angle = flightPathNode.angle;
        this.toCoordinate = flightPathNode.toCoordinate;
        this.ticksSinceStartOfCalculation = flightPathNode.ticksSinceStartOfCalculation;
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
        this.orderNo = orderNo;
        this.fromCoordinate = fromCoordinate;
        this.angle = angle;
        this.toCoordinate = toCoordinate;
        this.ticksSinceStartOfCalculation = (int) (System.nanoTime() - PizzaDronz.startTime);
    }

    /**
     * Creates an instance of the {@link FlightPathNode} class.
     *
     * @param fromCoordinate The starting coordinate.
     * @param angle          The angle between the two coordinates.
     * @param toCoordinate   The ending coordinate.
     */
    public FlightPathNode(LngLat fromCoordinate, double angle, LngLat toCoordinate) {
        this.fromCoordinate = fromCoordinate;
        this.angle = angle;
        this.toCoordinate = toCoordinate;
        this.ticksSinceStartOfCalculation = (int) (System.nanoTime() - PizzaDronz.startTime);
    }

    /**
     * Gets the order number.
     */
    public String orderNo() {
        return orderNo;
    }

    /**
     * Sets the order number.
     *
     * @param orderNo The order number.
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * Gets the starting coordinate.
     */
    public LngLat fromCoordinate() {
        return fromCoordinate;
    }

    /**
     * Gets the angle between the two coordinates.
     */
    public double angle() {
        return angle;
    }

    /**
     * Sets the angle between the two coordinates.
     *
     * @param angle The angle between the two coordinates.
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Gets the ending coordinate.
     */
    public LngLat toCoordinate() {
        return toCoordinate;
    }

    /**
     * Gets the number of ticks since the start of the calculation.
     */
    public int ticksSinceStartOfCalculation() {
        return ticksSinceStartOfCalculation;
    }

    /**
     * Sets the number of ticks since the start of the calculation.
     *
     * @param ticksSinceStartOfCalculation The number of ticks since the start of the calculation.
     */
    public void setTicksSinceStartOfCalculation(int ticksSinceStartOfCalculation) {
        this.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation;
    }
}
