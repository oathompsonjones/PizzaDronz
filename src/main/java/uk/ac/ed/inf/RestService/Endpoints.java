package uk.ac.ed.inf.RestService;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

public interface Endpoints {
    /**
     * The endpoint to get the list of restaurants.
     */
    Endpoint<Restaurant[]> RESTAURANTS = new Endpoint<>("restaurants", Restaurant[].class);
    /**
     * The endpoint to get the list of orders.
     */
    Endpoint<Order[]>      ORDERS      = new Endpoint<>("orders", Order[].class);
    /**
     * The endpoint to get the central area.
     */
    Endpoint<NamedRegion> CENTRAL_AREA = new Endpoint<>("centralArea", NamedRegion.class);
    /**
     * The endpoint to get the list of no-fly zones.
     */
    Endpoint<NamedRegion[]> NO_FLY_ZONES = new Endpoint<>("noFlyZones", NamedRegion[].class);
}
