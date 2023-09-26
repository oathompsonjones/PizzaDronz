package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Path2D;

public class LngLatHandler implements LngLatHandling {
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        return Math.sqrt(Math.pow(endPosition.lng() - startPosition.lng(), 2) + Math.pow(endPosition.lat() - startPosition.lat(), 2));
    }

    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        Path2D path = new Path2D.Double();
        path.moveTo(vertices[0].lng(), vertices[0].lat());
        for (int i = 1; i < vertices.length; i++)
            path.lineTo(vertices[i].lng(), vertices[i].lat());
        path.closePath();
        return path.contains(position.lng(), position.lat());
    }

    public LngLat nextPosition(LngLat startPosition, double angle) {
        double lngMultiplier = Math.cos(Math.toRadians(angle));
        double newLng = startPosition.lng() + lngMultiplier * SystemConstants.DRONE_MOVE_DISTANCE;
        double latMultiplier = Math.sin(Math.toRadians(angle));
        double newLat = startPosition.lat() + latMultiplier * SystemConstants.DRONE_MOVE_DISTANCE;
        return new LngLat(newLng, newLat);
    }
}
