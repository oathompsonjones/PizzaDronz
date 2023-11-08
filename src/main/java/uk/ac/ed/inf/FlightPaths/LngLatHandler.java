package uk.ac.ed.inf.FlightPaths;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * Handles longitude and latitude data
 */
public class LngLatHandler implements LngLatHandling {
    /**
     * Calculates the distance between two points
     *
     * @param start the position to calculate the distance from
     * @param end   the position to calculate the distance to
     *
     * @return the distance between the two points
     */
    public double distanceTo(LngLat start, LngLat end) {
        // Uses the Pythagorean theorem to calculate the distance between two points
        return Math.sqrt(Math.pow(end.lng() - start.lng(), 2) + Math.pow(end.lat() - start.lat(), 2));
    }

    /**
     * Checks if two points are close to each other
     *
     * @param start the position to check the distance from
     * @param other the position to check the distance to
     *
     * @return true if the distance between the two points is less than the close distance, false otherwise
     */
    public boolean isCloseTo(LngLat start, LngLat other) {
        // Checks if the distance between two points is less than the close distance
        return distanceTo(start, other) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Checks if a position is in a region
     *
     * @param position the position to check
     * @param region   the region to check
     *
     * @return true if the position is in the region, false otherwise
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {
        // Creates a polygon from the region's vertices
        LngLat[] vertices = region.vertices();
        var      path     = new Path2D.Double();
        path.moveTo(vertices[0].lng(), vertices[0].lat());
        for (int i = 1; i < vertices.length; i++) {
            path.lineTo(vertices[i].lng(), vertices[i].lat());
            // Determines if position lies on the edge of polygon, as path.contains does not include all edges
            LngLat vertex1 = vertices[i];
            LngLat vertex2 = vertices[(i + 1) % vertices.length];
            if (distanceTo(position, vertex1) + distanceTo(position, vertex2) == distanceTo(vertex1, vertex2))
                return true;
        }
        path.closePath();
        // Determines if the position lies within the polygon
        return path.contains(position.lng(), position.lat());
    }

    /**
     * Calculates the next position of the drone
     *
     * @param startPosition the position to calculate the next position from
     * @param angle         the angle to calculate the next position at
     *
     * @return the next position of the drone
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double newLng = startPosition.lng() + Math.cos(Math.toRadians(angle)) * SystemConstants.DRONE_MOVE_DISTANCE;
        double newLat = startPosition.lat() + Math.sin(Math.toRadians(angle)) * SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(newLng, newLat);
    }

    /**
     * Checks if a line crosses a region
     *
     * @param pos1   the first point of the line
     * @param pos2   the second point of the line
     * @param region the region to check
     *
     * @return true if the line crosses the region, false otherwise
     */
    public boolean lineCrossesRegion(LngLat pos1, LngLat pos2, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        for (int i = 0; i < vertices.length; i++) {
            LngLat vertex1 = vertices[i];
            LngLat vertex2 = vertices[(i + 1) % vertices.length];
            var    line1   = new Line2D.Double(pos1.lng(), pos1.lat(), pos2.lng(), pos2.lat());
            var    line2   = new Line2D.Double(vertex1.lng(), vertex1.lat(), vertex2.lng(), vertex2.lat());
            if (line1.intersectsLine(line2)) return true;
        }
        return false;
    }
}
