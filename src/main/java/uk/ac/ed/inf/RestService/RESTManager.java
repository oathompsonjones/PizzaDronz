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
    public RESTManager(String baseUrl) throws IOException {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        try {
            GET(Endpoints.IS_ALIVE);
        } catch (Exception e) {
            throw new IOException("No running server was found at " + baseUrl + ".");
        }
    }

    /**
     * Makes a request to the `restaurants` endpoint to get the list of restaurants.
     *
     * @return The list of restaurants.
     */
    public Restaurant[] getRestaurants() {
        try {
            return GET(Endpoints.RESTAURANTS);
        } catch (Exception ignored) {
            // This error should never be thrown because we test the isAlive endpoint at the start.
            return null;
        }
    }

    /**
     * Makes a request to the `orders` endpoint to get the list of orders for a given date.
     *
     * @param date The date to get the orders for.
     *
     * @return The list of orders for the given date.
     */
    public Order[] getOrders(LocalDate date) {
        try {
            return date == null ? GET(Endpoints.ORDERS) : GET(Endpoints.ORDERS, "/" + date);
        } catch (Exception ignored) {
            // This error should never be thrown because we test the isAlive endpoint at the start.
            return null;
        }
    }

    /**
     * Makes a request to the `centralArea` endpoint to get the central area.
     *
     * @return The central area.
     */
    public NamedRegion getCentralArea() {
        try {
            return GET(Endpoints.CENTRAL_AREA);
        } catch (Exception ignored) {
            // This error should never be thrown because we test the isAlive endpoint at the start.
            return null;
        }
    }

    /**
     * Makes a request to the `noFlyZones` endpoint to get the list of no-fly zones.
     *
     * @return The list of no-fly zones.
     */
    public NamedRegion[] getNoFlyZones() {
        try {
            return GET(Endpoints.NO_FLY_ZONES);
        } catch (Exception ignored) {
            // This error should never be thrown because we test the isAlive endpoint at the start.
            return null;
        }
    }

    /**
     * Performs a GET request to the server.
     *
     * @param endpoint The endpoint to call.
     *
     * @return The response from the server.
     */
    private <T> T GET(Endpoint<T> endpoint) throws IOException {
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
    private <T> T GET(Endpoint<T> endpoint, String args) throws IOException {
        return objectMapper.readValue(new URL(baseUrl + endpoint.url() + args), endpoint.clazz());
    }
}
