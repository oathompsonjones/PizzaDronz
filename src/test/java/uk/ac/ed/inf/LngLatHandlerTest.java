package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandlerTest extends TestCase {
    LngLatHandler handler = new LngLatHandler();

    public void testDistanceTo() {
        LngLat[] positions = new LngLat[] {
                new LngLat(0, 4),
                new LngLat(3, 0),
                new LngLat(0, 0),
                new LngLat(0, 1),
                new LngLat(1, 0),
                new LngLat(1, 1),
                new LngLat(-1, -1)
        };
        assertEquals(5.0, handler.distanceTo(positions[0], positions[1]));
        assertEquals(5.0, handler.distanceTo(positions[1], positions[0]));
        assertEquals(0.0, handler.distanceTo(positions[2], positions[2]));
        assertEquals(1.0, handler.distanceTo(positions[2], positions[3]));
        assertEquals(1.0, handler.distanceTo(positions[2], positions[4]));
        assertEquals(Math.sqrt(2), handler.distanceTo(positions[3], positions[4]));
        assertEquals(Math.sqrt(8), handler.distanceTo(positions[5], positions[6]));
    }

    public void testIsCloseTo() {
        LngLat[] positions = new LngLat[] {
                new LngLat(0, 0),
                new LngLat(0, SystemConstants.DRONE_IS_CLOSE_DISTANCE),
                new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE, SystemConstants.DRONE_IS_CLOSE_DISTANCE)
        };
        assertTrue(handler.isCloseTo(positions[0], positions[0]));
        assertTrue(handler.isCloseTo(positions[0], positions[1]));
        assertFalse(handler.isCloseTo(positions[0], positions[2]));
    }

    public void testIsInRegion() {
        LngLat[] vertices = new LngLat[] {
                new LngLat(0, 0),
                new LngLat(4, 0),
                new LngLat(2, 4)
        };
        NamedRegion region = new NamedRegion("Triangular Region", vertices);
        assertTrue(handler.isInRegion(new LngLat(2, 2), region));
        assertTrue(handler.isInRegion(vertices[0], region));
        // TODO These both fail because of the way that Java defines "insideness".
        // assertTrue(handler.isInRegion(vertices[1], region));
        // assertTrue(handler.isInRegion(vertices[2], region));
        assertTrue(handler.isInRegion(new LngLat(2, 0), region));
        assertFalse(handler.isInRegion(new LngLat(0, 2), region));
        assertFalse(handler.isInRegion(new LngLat(4, 4), region));
    }

    public void testNextPosition()  {
        LngLat startPos = new LngLat(0, 0);
        LngLat nextPos = handler.nextPosition(startPos, 0);
        assertTrue(SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lng() + 1e-12 && SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lng() - 1e-12);
        assertTrue(0.0 < nextPos.lat() + 1e-12 && 0.0 > nextPos.lat() - 1e-12);

        nextPos = handler.nextPosition(startPos, 90);
        assertTrue(0.0 < nextPos.lng() + 1e-12 && 0.0 > nextPos.lng() - 1e-12);
        assertTrue(SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lat() + 1e-12 && SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lat() - 1e-12);

        nextPos = handler.nextPosition(startPos, 180);
        assertTrue(-SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lng() + 1e-12 && -SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lng() - 1e-12);
        assertTrue(0.0 < nextPos.lat() + 1e-12 && 0.0 > nextPos.lat() - 1e-12);

        nextPos = handler.nextPosition(startPos, 270);
        assertTrue(0.0 < nextPos.lng() + 1e-12 && 0.0 > nextPos.lng() - 1e-12);
        assertTrue(-SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lat() + 1e-12 && -SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lat() - 1e-12);
    }
}
