package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

/**
 * A class to manage the REST API calls to the server, and to parse the JSON responses into Java objects.
 */
public class RESTManager {
    /**
     * The object mapper used to parse JSON responses into Java objects.
     */
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    /**
     * The base URL of the server.
     */
    private       String       baseUrl;

    /**
     * Creates a new RESTManager object.
     *
     * @param baseUrl The base URL of the server.
     */
    public RESTManager(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!this.baseUrl.endsWith("/"))
            this.baseUrl += "/";
    }

    /**
     * Performs a GET request to the server.
     *
     * @param endpoint The endpoint to call.
     * @return The response from the server.
     */
    private Object GET(Endpoints endpoint) {
        return GET(endpoint, "");
    }

    /**
     * Performs a GET request to the server.
     *
     * @param endpoint The endpoint to call.
     * @param args     The arguments to pass to the endpoint.
     * @return The response from the server.
     */
    private Object GET(Endpoints endpoint, String args) {
        try {
            return mapper.readValue(new URL(baseUrl + endpoint.getUrl() + args), endpoint.getReturnType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Makes a request to the `isAlive` endpoint to check if the server is alive.
     *
     * @return True if the server is alive, false otherwise.
     */
    public boolean isAlive() {
        return (boolean) GET(Endpoints.IS_ALIVE);
    }

    /**
     * Makes a request to the `restaurants` endpoint to get the list of restaurants.
     *
     * @return The list of restaurants.
     */
    public Restaurant[] getRestaurants() {
        return (Restaurant[]) GET(Endpoints.RESTAURANTS);
    }

    /**
     * Makes a request to the `orders` endpoint to get the list of orders.
     *
     * @return The list of orders.
     */
    public Order[] getOrders() {
        return (Order[]) GET(Endpoints.ORDERS);
    }

    /**
     * Makes a request to the `orders` endpoint to get the list of orders for a given date.
     *
     * @param date The date to get the orders for.
     * @return The list of orders for the given date.
     */
    public Order[] getOrders(LocalDate date) {
        return (Order[]) GET(Endpoints.ORDERS, "/" + date);
    }

    /**
     * Makes a request to the `centralArea` endpoint to get the central area.
     *
     * @return The central area.
     */
    public NamedRegion getCentralArea() {
        return (NamedRegion) GET(Endpoints.CENTRAL_AREA);
    }

    /**
     * Makes a request to the `noFlyZones` endpoint to get the list of no-fly zones.
     *
     * @return The list of no-fly zones.
     */
    public NamedRegion[] getNoFlyZones() {
        return (NamedRegion[]) GET(Endpoints.NO_FLY_ZONES);
    }

    /**
     * An enum to represent the endpoints of the server.
     */
    private enum Endpoints {
        /**
         * The endpoint to check if the server is alive.
         */
        IS_ALIVE("isAlive", boolean.class),
        /**
         * The endpoint to get the list of restaurants.
         */
        RESTAURANTS("restaurants", Restaurant[].class),
        /**
         * The endpoint to get the list of orders.
         */
        ORDERS("orders", Order[].class),
        /**
         * The endpoint to get the central area.
         */
        CENTRAL_AREA("centralArea", NamedRegion.class),
        /**
         * The endpoint to get the list of no-fly zones.
         */
        NO_FLY_ZONES("noFlyZones", NamedRegion[].class);

        /**
         * The URL of the endpoint.
         */
        private final String   url;
        /**
         * The return type of the endpoint.
         */
        private final Class<?> returnType;

        /**
         * Creates a new Endpoints object.
         *
         * @param url        The URL of the endpoint.
         * @param returnType The return type of the endpoint.
         */
        Endpoints(String url, Class<?> returnType) {
            this.url = url;
            this.returnType = returnType;
        }

        /**
         * Gets the URL of the endpoint.
         *
         * @return The URL of the endpoint.
         */
        public String getUrl() {
            return url;
        }

        /**
         * Gets the return type of the endpoint.
         *
         * @return The return type of the endpoint.
         */
        public Class<?> getReturnType() {
            return returnType;
        }
    }
}
