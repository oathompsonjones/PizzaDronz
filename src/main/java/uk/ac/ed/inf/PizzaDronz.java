package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Main class for the PizzaDronz application.
 */
public class PizzaDronz {
    /**
     * Creates an instance of the {@link OrderValidator} class.
     */
    public static final OrderValidator      orderValidator = new OrderValidator();
    /**
     * Creates an instance of the {@link LngLatHandler} class.
     */
    public static final LngLatHandler       lngLatHandler  = new LngLatHandler();
    /**
     * Stores the location of the Appleton Tower.
     */
    public static final LngLat              appletonTower  = new LngLat(-3.186874, 55.944494);
    /**
     * Stores the instance of the {@link FlightPathGenerator} class.
     */
    public final        FlightPathGenerator flightPathGenerator;
    /**
     * Stores the instance of the {@link RESTManager} class.
     */
    private final       RESTManager         restManager;

    /**
     * Creates an instance of the {@link PizzaDronz} class.
     *
     * @param apiUrl The URL of the API.
     * @param date   The date to generate the flight paths for.
     */
    public PizzaDronz(String apiUrl, LocalDate date) {
        restManager = new RESTManager(apiUrl);
        flightPathGenerator = new FlightPathGenerator(restManager.getCentralArea(), restManager.getNoFlyZones(), restManager.getRestaurants());
        Order[]            validOrders = getValidOrders(date);
        List<List<LngLat>> flightPaths = flightPathGenerator.generate(validOrders);
    }

    /**
     * Gets the restaurant from the given order.
     *
     * @param order       The order to get the restaurant from.
     * @param restaurants The list of restaurants to search through.
     * @return The restaurant from the given order.
     */
    public static Restaurant getOrderRestaurant(Order order, Restaurant[] restaurants) {
        return Arrays.stream(restaurants).filter(r -> Arrays.stream(r.menu()).anyMatch(p -> Objects.equals(p.name(), order.getPizzasInOrder()[0].name()))).toArray(Restaurant[]::new)[0];
    }

    /**
     * Fetches and validates all orders for the given date.
     *
     * @param date The date to fetch the orders for.
     * @return The valid orders.
     */
    private Order[] getValidOrders(LocalDate date) {
        Restaurant[] restaurants     = restManager.getRestaurants();
        Order[]      orders          = restManager.getOrders(date);
        Order[]      validatedOrders = Arrays.stream(orders).map(order -> orderValidator.validateOrder(order, restaurants)).toArray(Order[]::new);
        Order[]      validOrders     = Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED).toArray(Order[]::new);
        Order[]      invalidOrders   = Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.INVALID).toArray(Order[]::new);
        return validOrders;
    }

    /**
     * Generates the deliveries JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateDeliveryJSON(LocalDate date) {
        String fileName = "deliveries-" + date.toString() + ".json";
        writeFile(fileName, "");
    }

    /**
     * Generates the flight path JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateFlightPathJSON(LocalDate date) {
        String fileName = "flightpath-" + date.toString() + ".json";
        writeFile(fileName, "");
    }

    /**
     * Generates the flight path GeoJSON file for the given date.
     *
     * @param date The date to generate the GeoJSON files for.
     */
    private void generateFlightPathGeoJSON(LocalDate date) {
        String fileName = "drone-" + date.toString() + ".geojson";
        writeFile(fileName, "");
    }

    /**
     * Writes the given data to the given file.
     *
     * @param name The name of the file to write to.
     * @param data The data to write to the file.
     */
    private void writeFile(String name, String data) {
        try {
            FileWriter fileWriter = new FileWriter(name);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing to file " + name);
        }
    }
}
