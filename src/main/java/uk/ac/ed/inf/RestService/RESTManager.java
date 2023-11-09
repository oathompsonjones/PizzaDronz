package uk.ac.ed.inf.RestService;

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
     * The object mapper used to parse between JSON and Java Objects.
     */
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    /**
     * The base URL of the server.
     */
    private final String       baseUrl;

    /**
     * Creates a new RESTManager object.
     *
     * @param baseUrl The base URL of the server.
     */
    public RESTManager(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    /**
     * Makes a request to the `restaurants` endpoint to get the list of restaurants.
     *
     * @return The list of restaurants.
     */
    public Restaurant[] getRestaurants() {
        return GET(Endpoints.RESTAURANTS);
    }

    /**
     * Makes a request to the `orders` endpoint to get the list of orders for a given date.
     *
     * @param date The date to get the orders for.
     *
     * @return The list of orders for the given date.
     */
    public Order[] getOrders(LocalDate date) {
        return GET(Endpoints.ORDERS, "/" + date);
    }

    /**
     * Makes a request to the `centralArea` endpoint to get the central area.
     *
     * @return The central area.
     */
    public NamedRegion getCentralArea() {
        return GET(Endpoints.CENTRAL_AREA);
    }

    /**
     * Makes a request to the `noFlyZones` endpoint to get the list of no-fly zones.
     *
     * @return The list of no-fly zones.
     */
    public NamedRegion[] getNoFlyZones() {
        return GET(Endpoints.NO_FLY_ZONES);
    }

    /**
     * Performs a GET request to the server.
     *
     * @param endpoint The endpoint to call.
     *
     * @return The response from the server.
     */
    private <T> T GET(Endpoint<T> endpoint) {
        return GET(endpoint, "");
    }

    /**
     * Performs a GET request to the server.
     *
     * @param endpoint The endpoint to call.
     * @param args     The arguments to pass to the endpoint.
     *
     * @return The response from the server.
     */
    private <T> T GET(Endpoint<T> endpoint, String args) {
        try {
            return objectMapper.readValue(new URL(baseUrl + endpoint.url() + args), endpoint.clazz());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
}
