package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Path2D;

public class LngLatHandler implements LngLatHandling {
    /**
     * Calculates the distance between two points
     * @param startPosition the position to calculate the distance from
     * @param endPosition the position to calculate the distance to
     * @return the distance between the two points
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        // Uses the Pythagorean theorem to calculate the distance between two points
        return Math.sqrt(Math.pow(endPosition.lng() - startPosition.lng(), 2) + Math.pow(endPosition.lat() - startPosition.lat(), 2));
    }

    /**
     * Checks if two points are close to each other
     * @param startPosition the position to check the distance from
     * @param otherPosition the position to check the distance to
     * @return true if the distance between the two points is less than the close distance, false otherwise
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        // Checks if the distance between two points is less than the close distance
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Checks if a position is in a region
     * @param position the position to check
     * @param region the region to check
     * @return true if the position is in the region, false otherwise
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {
        // Creates a polygon from the region's vertices
        LngLat[] vertices = region.vertices();
        Path2D path = new Path2D.Double();
        path.moveTo(vertices[0].lng(), vertices[0].lat());
        for (int i = 1; i < vertices.length; i++)
            path.lineTo(vertices[i].lng(), vertices[i].lat());
        path.closePath();
        // Determines if the position lies on the edge of the polygon, as the path.contains method does not include all the edges
        for (int i = 0; i < vertices.length; i++) {
            LngLat vertex1 = vertices[i];
            LngLat vertex2 = vertices[(i + 1) % vertices.length];
            if (distanceTo(position, vertex1) + distanceTo(position, vertex2) == distanceTo(vertex1, vertex2))
                return true;
        }
        // Determines if the position lies within the polygon
        return path.contains(position.lng(), position.lat());
    }

    /**
     * Calculates the next position of the drone
     * @param startPosition the position to calculate the next position from
     * @param angle the angle to calculate the next position at
     * @return the next position of the drone
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // If the angle is 999 the drone is hovering
        if (angle == 999)
            return startPosition;
        // Uses trigonometry to calculate the new position
        double lngMultiplier = Math.cos(Math.toRadians(angle));
        double newLng = startPosition.lng() + lngMultiplier * SystemConstants.DRONE_MOVE_DISTANCE;
        double latMultiplier = Math.sin(Math.toRadians(angle));
        double newLat = startPosition.lat() + latMultiplier * SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(newLng, newLat);
    }
}
