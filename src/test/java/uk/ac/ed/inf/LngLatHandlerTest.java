package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandlerTest extends TestCase {
    LngLatHandler handler = new LngLatHandler();
    public void testDistanceTo() {
        LngLat startPos = new LngLat(0, 4);
        LngLat endPos = new LngLat(3, 0);
        assertEquals(5.0, handler.distanceTo(startPos, endPos));
    }

    public void testIsCloseTo() {
        LngLat pos1 = new LngLat(0, 0);
        LngLat pos2 = new LngLat(0, SystemConstants.DRONE_IS_CLOSE_DISTANCE);
        LngLat pos3 = new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE, SystemConstants.DRONE_IS_CLOSE_DISTANCE);
        assertTrue(handler.isCloseTo(pos1, pos2));
        assertFalse(handler.isCloseTo(pos1, pos3));
    }

    public void testIsInRegion() {
        LngLat[] vertices = new LngLat[] {
                new LngLat(0, 0),
                new LngLat(0, 4),
                new LngLat(4, 2)
        };
        NamedRegion region = new NamedRegion("Test Region", vertices);
        assertTrue(handler.isInRegion(new LngLat(2, 2), region));
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
