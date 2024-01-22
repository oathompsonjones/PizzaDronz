package uk.ac.ed.inf.UnitTests.RestService;

import junit.framework.TestCase;
import uk.ac.ed.inf.RestService.RESTManager;
import uk.ac.ed.inf.ilp.data.Order;

import java.time.LocalDate;

public class RESTManagerTest extends TestCase {
    RESTManager manager;

    public RESTManagerTest(String testName) {
        super(testName);
        try {
            manager = new RESTManager("https://ilp-rest.azurewebsites.net");
        } catch (Exception e) {
            fail("Exception thrown when creating RESTManager");
        }
    }

    public void testGetRestaurants() {
        try {
            manager.getRestaurants();
        } catch (Exception err) {
            fail("Exception thrown");
        }
    }

    public void testGetOrders() {
        Order[] allOrders  = null;
        Order[] dateOrders = null;
        try {
            allOrders = manager.getOrders(null);
            dateOrders = manager.getOrders(LocalDate.parse("2023-11-15"));
        } catch (Exception err) {
            fail("Exception thrown");
        }
        assertTrue(allOrders.length > dateOrders.length);
    }

    public void testGetCentralArea() {
        try {
            manager.getCentralArea();
        } catch (Exception err) {
            fail("Exception thrown");
        }
    }

    public void testGetNoFlyZones() {
        try {
            manager.getNoFlyZones();
        } catch (Exception err) {
            fail("Exception thrown");
        }
    }
}
