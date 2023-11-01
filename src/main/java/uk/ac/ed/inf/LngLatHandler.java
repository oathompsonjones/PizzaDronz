package uk.ac.ed.inf;

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
     * @param startPosition the position to calculate the distance from
     * @param endPosition   the position to calculate the distance to
     * @return the distance between the two points
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        // Uses the Pythagorean theorem to calculate the distance between two points
        return Math.sqrt(Math.pow(endPosition.lng() - startPosition.lng(), 2) + Math.pow(endPosition.lat() - startPosition.lat(), 2));
    }

    /**
     * Checks if two points are close to each other
     *
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
     *
     * @param position the position to check
     * @param region   the region to check
     * @return true if the position is in the region, false otherwise
     */
    public boolean isInRegion(LngLat position, NamedRegion region) {
        // Creates a polygon from the region's vertices
        LngLat[] vertices = region.vertices();
        Path2D   path     = new Path2D.Double();
        path.moveTo(vertices[0].lng(), vertices[0].lat());
        for (int i = 1; i < vertices.length; i++) {
            path.lineTo(vertices[i].lng(), vertices[i].lat());
            // Determines if the position lies on the edge of the polygon, as the path.contains method does not include all the edges
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
     * Checks if two lines intersect
     *
     * @param pos1 the first point of the first line
     * @param pos2 the second point of the first line
     * @param pos3 the first point of the second line
     * @param pos4 the second point of the second line
     * @return true if the lines intersect, false otherwise
     */
    public boolean linesIntersect(LngLat pos1, LngLat pos2, LngLat pos3, LngLat pos4) {
        Line2D line1 = new Line2D.Double(pos1.lng(), pos1.lat(), pos2.lng(), pos2.lat());
        Line2D line2 = new Line2D.Double(pos3.lng(), pos3.lat(), pos4.lng(), pos4.lat());
        return line1.intersectsLine(line2);
    }

    /**
     * Checks if a line crosses a region
     *
     * @param pos1   the first point of the line
     * @param pos2   the second point of the line
     * @param region the region to check
     * @return true if the line crosses the region, false otherwise
     */
    public boolean lineCrossesRegion(LngLat pos1, LngLat pos2, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        for (int i = 0; i < vertices.length; i++) {
            LngLat vertex1 = vertices[i];
            LngLat vertex2 = vertices[(i + 1) % vertices.length];
            if (linesIntersect(pos1, pos2, vertex1, vertex2))
                return true;
        }
        return false;
    }

    /**
     * Calculates the next position of the drone
     *
     * @param startPosition the position to calculate the next position from
     * @param angle         the angle to calculate the next position at
     * @return the next position of the drone
     */
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // If the angle is 999 the drone is hovering
        if (angle == 999)
            return startPosition;
        // Uses trigonometry to calculate the new position
        double newLng = startPosition.lng() + Math.cos(Math.toRadians(angle)) * SystemConstants.DRONE_MOVE_DISTANCE;
        double newLat = startPosition.lat() + Math.sin(Math.toRadians(angle)) * SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(newLng, newLat);
    }

    /**
     * Calculates the next position of the drone in each of the 16 cardinal directions
     *
     * @param position the position to calculate the neighbours of
     * @return an array of the next positions of the drone in each of the 16 cardinal directions
     */
    public LngLat[] getNeighbours(LngLat position, int count) {
        LngLat[] neighbours = new LngLat[count];
        double   angle      = 360.0 / count;
        for (int i = 0; i < count; i++)
            neighbours[i] = nextPosition(position, i * angle);
        return neighbours;
    }
}
