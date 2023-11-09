package uk.ac.ed.inf.FlightPaths;

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
        // Test distance with the hypotenuse of a pythagorean triple
        assertEquals(5.0, handler.distanceTo(positions[0], positions[1]));
        // Test that the result is the same when the parameters are swapped
        assertEquals(5.0, handler.distanceTo(positions[1], positions[0]));
        // Test that the result is 0 when the parameters are the same
        assertEquals(0.0, handler.distanceTo(positions[2], positions[2]));
        // Test distance with a vertical line
        assertEquals(1.0, handler.distanceTo(positions[2], positions[3]));
        // Test distance with a horizontal line
        assertEquals(1.0, handler.distanceTo(positions[2], positions[4]));
        // Test distance with a diagonal line
        assertEquals(Math.sqrt(2), handler.distanceTo(positions[3], positions[4]));
        // Test distance with a longer diagonal line
        assertEquals(Math.sqrt(8), handler.distanceTo(positions[5], positions[6]));
    }

    public void testIsCloseTo() {
        LngLat[] positions = new LngLat[] {
                new LngLat(0, 0),
                new LngLat(0, SystemConstants.DRONE_IS_CLOSE_DISTANCE),
                new LngLat(SystemConstants.DRONE_IS_CLOSE_DISTANCE, SystemConstants.DRONE_IS_CLOSE_DISTANCE)
        };
        // Test that the result is true when the parameters are the same
        assertTrue(handler.isCloseTo(positions[0], positions[0]));
        // Test that the result is true when the parameters are close
        assertTrue(handler.isCloseTo(positions[0], positions[1]));
        // Test that the result is false when the parameters are not close
        assertFalse(handler.isCloseTo(positions[0], positions[2]));
    }

    public void testIsInRegion() {
        // Test a triangular region
        LngLat[]    vertices = new LngLat[] { new LngLat(0, 0), new LngLat(4, 0), new LngLat(2, 4) };
        NamedRegion region   = new NamedRegion("Triangular Region", vertices);
        // Test a point in the middle(ish)
        assertTrue(handler.isInRegion(new LngLat(2, 2), region));
        // Test the corners themselves
        for (LngLat vertex : vertices)
            assertTrue(handler.isInRegion(vertex, region));
        // Test a point on the edge
        assertTrue(handler.isInRegion(new LngLat(2, 0), region));
        // Test points outside the region
        assertFalse(handler.isInRegion(new LngLat(0, 2), region));
        assertFalse(handler.isInRegion(new LngLat(4, 4), region));

        // Test a square region
        vertices = new LngLat[] { new LngLat(0, 0), new LngLat(4, 0), new LngLat(4, 4), new LngLat(0, 4) };
        region = new NamedRegion("Square Region", vertices);
        // Test a point in the middle
        assertTrue(handler.isInRegion(new LngLat(2, 2), region));
        // Test the corners themselves
        for (LngLat vertex : vertices)
            assertTrue(handler.isInRegion(vertex, region));
        // Test a point on the edge
        assertTrue(handler.isInRegion(new LngLat(2, 0), region));
        // Test a point outside the region
        assertFalse(handler.isInRegion(new LngLat(5, 5), region));
    }

    public void testNextPosition() {
        LngLat startPos = new LngLat(0, 0);
        // Test that an angle of 0 results in a position to the right
        LngLat nextPos = handler.nextPosition(startPos, 0);
        assertTrue(SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lng() + 1e-12
                   && SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lng() - 1e-12);
        assertTrue(0.0 < nextPos.lat() + 1e-12 && 0.0 > nextPos.lat() - 1e-12);

        // Test that an angle of 90 results in a position above
        nextPos = handler.nextPosition(startPos, 90);
        assertTrue(0.0 < nextPos.lng() + 1e-12 && 0.0 > nextPos.lng() - 1e-12);
        assertTrue(SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lat() + 1e-12
                   && SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lat() - 1e-12);

        // Test that an angle of 180 results in a position to the left
        nextPos = handler.nextPosition(startPos, 180);
        assertTrue(-SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lng() + 1e-12
                   && -SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lng() - 1e-12);
        assertTrue(0.0 < nextPos.lat() + 1e-12 && 0.0 > nextPos.lat() - 1e-12);

        // Test that an angle of 270 results in a position below
        nextPos = handler.nextPosition(startPos, 270);
        assertTrue(0.0 < nextPos.lng() + 1e-12 && 0.0 > nextPos.lng() - 1e-12);
        assertTrue(-SystemConstants.DRONE_MOVE_DISTANCE < nextPos.lat() + 1e-12
                   && -SystemConstants.DRONE_MOVE_DISTANCE > nextPos.lat() - 1e-12);
    }

    public void testLineCrossesRegion() {
        // Test a triangular region
        LngLat[] vertices = new LngLat[] {
                new LngLat(0, 0), new LngLat(4, 0), new LngLat(2, 4)
        };
        NamedRegion region = new NamedRegion("Triangular Region", vertices);
        // Test the edges of the region
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 0), new LngLat(4, 0), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(4, 0), new LngLat(2, 4), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(2, 4), new LngLat(0, 0), region));
        // Test lines which stop at the vertices
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 0), new LngLat(0, 4), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 4), new LngLat(4, 0), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(4, 0), new LngLat(4, 2), region));
        // Test lines which cross the region
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 0), new LngLat(4, 4), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 4), new LngLat(4, 2), region));
        // Test lines which do not cross the region
        assertFalse(handler.lineCrossesRegion(new LngLat(0, 1), new LngLat(0, 2), region));
        assertFalse(handler.lineCrossesRegion(new LngLat(1, 6), new LngLat(2, 8), region));
        // Test lines entirely within the region
        assertTrue(handler.lineCrossesRegion(new LngLat(1, 1), new LngLat(2, 2), region));
        // Test lines which are really just points
        assertTrue(handler.lineCrossesRegion(new LngLat(0, 0), new LngLat(0, 0), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(1, 2), new LngLat(1, 2), region));
        assertTrue(handler.lineCrossesRegion(new LngLat(2, 2), new LngLat(2, 2), region));
    }
}
