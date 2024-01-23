package uk.ac.ed.inf.UnitTests.FlightPaths;

import junit.framework.TestCase;
import uk.ac.ed.inf.FlightPaths.FlightPathGenerator;
import uk.ac.ed.inf.FlightPaths.LngLatHandler;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.util.Arrays;

public class FlightPathGeneratorTest extends TestCase {
    public void testPathContainsAllOrders() {
        var centralRegion = generateCentralRegion();
        var noFlyZones    = generateNoFlyZones();
        var restaurants   = generateRestaurants();
        var orders        = generateOrders();
        var generator     = new FlightPathGenerator(centralRegion, noFlyZones, restaurants);
        var fullPath      = generator.generateFullPath(orders);

        var pathLengths = new int[orders.length];
        for (int i = 0; i < orders.length; i++)
            pathLengths[i] = generator.generateFullPath(new Order[] { orders[i] }).length;
        assertEquals(Arrays.stream(pathLengths).sum(), fullPath.length);
    }

    public void testRuntime() {
        long startTime = System.currentTimeMillis();
        var  centralRegion = generateCentralRegion();
        var  noFlyZones = generateNoFlyZones();
        var  restaurants = generateRestaurants();
        var  orders = generateOrders();
        var  generator = new FlightPathGenerator(centralRegion, noFlyZones, restaurants);

        generator.generateFullPath(orders);
        assertTrue(System.currentTimeMillis() - startTime < 60_000);
    }

    public void testPathDoesNotEnterNoFlyZones() {
        var centralRegion = generateCentralRegion();
        var noFlyZones    = generateNoFlyZones();
        var restaurants   = generateRestaurants();
        var orders        = generateOrders();
        var generator     = new FlightPathGenerator(centralRegion, noFlyZones, restaurants);
        var fullPath      = generator.generateFullPath(orders);
        var lngLatHandler = new LngLatHandler();

        for (var flightPathNode : fullPath) {
            for (var noFlyZone : noFlyZones) {
                if (lngLatHandler.lineCrossesRegion(flightPathNode.fromCoordinate(),
                                                    flightPathNode.toCoordinate(),
                                                    noFlyZone
                                                   )) fail("Flight path crosses no fly zone");
            }
        }
    }

    private NamedRegion generateCentralRegion() {
        return new NamedRegion("central", new LngLat[] {
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233)
        });
    }

    private NamedRegion[] generateNoFlyZones() {
        return new NamedRegion[] {
                new NamedRegion("1", new LngLat[] {
                        new LngLat(-3.190578818321228, 55.94402412577528),
                        new LngLat(-3.1899887323379517, 55.94284650540911),
                        new LngLat(-3.187097311019897, 55.94328811724263),
                        new LngLat(-3.187682032585144, 55.944477740393744),
                        new LngLat(-3.190578818321228, 55.94402412577528)
                }),

                new NamedRegion("2", new LngLat[] {
                        new LngLat(-3.1907182931900024, 55.94519570234043),
                        new LngLat(-3.1906163692474365, 55.94498241796357),
                        new LngLat(-3.1900262832641597, 55.94507554227258),
                        new LngLat(-3.190133571624756, 55.94529783810495),
                        new LngLat(-3.1907182931900024, 55.94519570234043)
                }),

                new NamedRegion("3", new LngLat[] {
                        new LngLat(-3.189543485641479, 55.94552313663306),
                        new LngLat(-3.189382553100586, 55.94553214854692),
                        new LngLat(-3.189259171485901, 55.94544803726933),
                        new LngLat(-3.1892001628875732, 55.94533688994374),
                        new LngLat(-3.189194798469543, 55.94519570234043),
                        new LngLat(-3.189135789871216, 55.94511759833873),
                        new LngLat(-3.188138008117676, 55.9452738061846),
                        new LngLat(-3.1885510683059692, 55.946105902745614),
                        new LngLat(-3.1895381212234497, 55.94555918427592),
                        new LngLat(-3.189543485641479, 55.94552313663306)
                }),

                new NamedRegion("4", new LngLat[] {
                        new LngLat(-3.1876927614212036, 55.94520696732767),
                        new LngLat(-3.187555968761444, 55.9449621408666),
                        new LngLat(-3.186981976032257, 55.94505676722831),
                        new LngLat(-3.1872327625751495, 55.94536993377657),
                        new LngLat(-3.1874459981918335, 55.9453361389472),
                        new LngLat(-3.1873735785484314, 55.94519344934259),
                        new LngLat(-3.1875935196876526, 55.94515665035927),
                        new LngLat(-3.187624365091324, 55.94521973430925),
                        new LngLat(-3.1876927614212036, 55.94520696732767)
                })
        };
    }

    private Restaurant[] generateRestaurants() {
        return new Restaurant[] {
                new Restaurant("1",
                               new LngLat(-3.1912869215011597, 55.945535152517735),
                               new DayOfWeek[] {},
                               new Pizza[] {
                                       new Pizza("1", 1), new Pizza("2", 1)
                               }
                ),

                new Restaurant("2",
                               new LngLat(-3.202541470527649, 55.943284737579376),
                               new DayOfWeek[] {},
                               new Pizza[] {
                                       new Pizza("3", 1), new Pizza("4", 1)
                               }
                ),

                new Restaurant("3",
                               new LngLat(-3.1838572025299072, 55.94449876875712),
                               new DayOfWeek[] {},
                               new Pizza[] {
                                       new Pizza("5", 1), new Pizza("6", 1)
                               }
                ),

                new Restaurant("4",
                               new LngLat(-3.1940174102783203, 55.94390696616939),
                               new DayOfWeek[] {},
                               new Pizza[] {
                                       new Pizza("7", 1), new Pizza("8", 1)
                               }
                ),

                new Restaurant("5",
                               new LngLat(-3.1810810679852035, 55.938910643735845),
                               new DayOfWeek[] {},
                               new Pizza[] {
                                       new Pizza("9", 1), new Pizza("10", 1)
                               }
                ),

                new Restaurant("6", new LngLat(-3.185428203143916, 55.945846113595), new DayOfWeek[] {}, new Pizza[] {
                        new Pizza("11", 1), new Pizza("12", 1)
                }),

                new Restaurant("7", new LngLat(-3.179798972064253, 55.939884084483), new DayOfWeek[] {}, new Pizza[] {
                        new Pizza("13", 1), new Pizza("14", 1)
                })
        };
    }

    private Order[] generateOrders() {
        var restaurants = generateRestaurants();
        var orders      = new Order[restaurants.length * 5];
        for (int i = 0; i < orders.length; i++) {
            orders[i] = new Order();
            var restaurant = restaurants[i / 5];
            orders[i].setPizzasInOrder(new Pizza[] { restaurant.menu()[i % 2] });
        }
        return orders;
    }
}
