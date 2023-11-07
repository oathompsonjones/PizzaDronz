package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import uk.ac.ed.inf.Serializers.FlightPathNodeGeoJSONSerializer;
import uk.ac.ed.inf.Serializers.FlightPathNodeJSONSerializer;
import uk.ac.ed.inf.Serializers.OrderJSONSerializer;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

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
     * Stores the start time of the application.
     */
    public static final long                startTime      = System.nanoTime();
    /**
     * Stores the instance of the {@link FlightPathGenerator} class.
     */
    public final        FlightPathGenerator flightPathGenerator;
    /**
     * Stores the instance of the {@link RESTManager} class.
     */
    private final       RESTManager         restManager;
    /**
     * Stores the flight paths.
     */
    private             FlightPathNode[]    flightPath     = null;
    /**
     * Stores the orders.
     */
    private             Order[]             orders         = null;

    /**
     * Creates an instance of the {@link PizzaDronz} class.
     *
     * @param apiUrl The URL of the API.
     * @param date   The date to generate the flight paths for.
     */
    public PizzaDronz(String apiUrl, LocalDate date) {
        restManager = new RESTManager(apiUrl);
        flightPathGenerator = new FlightPathGenerator(restManager.getCentralArea(), restManager.getNoFlyZones(), restManager.getRestaurants());
        System.out.println("Setup after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        Order[] validOrders = fetchValidOrders(date);
        System.out.println("Fetched " + validOrders.length + " valid orders after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        flightPath = flightPathGenerator.generateFullPath(validOrders);
        System.out.println("Generated flight path after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        generateFlightPathJSON(date);
        generateFlightPathGeoJSON(date);
        generateDeliveryJSON(date);
        System.out.println("Finished after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
    }

    /**
     * Entry point of the application.
     *
     * @param args The command line arguments. The first argument is the date and the second argument is the path to the file containing the orders.
     */
    public static void main(String[] args) {
        new PizzaDronz(args[1], LocalDate.parse(args[0]));
    }

    /**
     * Fetches and validates all orders.
     *
     * @return The valid orders.
     */
    private Order[] fetchValidOrders() {
        Restaurant[] restaurants = restManager.getRestaurants();
        orders = restManager.getOrders();
        Order[] validatedOrders = Arrays.stream(orders).map(order -> orderValidator.validateOrder(order, restaurants)).toArray(Order[]::new);
        return Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED).toArray(Order[]::new);
    }

    /**
     * Fetches and validates all orders for the given date.
     *
     * @param date The date to fetch the orders for.
     * @return The valid orders.
     */
    private Order[] fetchValidOrders(LocalDate date) {
        Restaurant[] restaurants = restManager.getRestaurants();
        orders = restManager.getOrders(date);
        Order[] validatedOrders = Arrays.stream(orders).map(order -> orderValidator.validateOrder(order, restaurants)).toArray(Order[]::new);
        return Arrays.stream(validatedOrders).filter(order -> order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED).toArray(Order[]::new);
    }

    /**
     * Generates the deliveries JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateDeliveryJSON(LocalDate date) {
        String fileName = "deliveries-" + date.toString() + ".json";
        try {
            SimpleModule serializationModule = new SimpleModule().addSerializer(Order.class, new OrderJSONSerializer());
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(serializationModule).writeValue(new File(fileName), orders);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Wrote delivery data in " + fileName + " after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        }
    }

    /**
     * Generates the flight path JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateFlightPathJSON(LocalDate date) {
        String fileName = "flightpath-" + date.toString() + ".json";
        try {
            SimpleModule serializationModule = new SimpleModule().addSerializer(FlightPathNode.class, new FlightPathNodeJSONSerializer());
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(serializationModule).writeValue(new File(fileName), flightPath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Wrote flight path data in " + fileName + " after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        }
    }

    /**
     * Generates the flight path GeoJSON file for the given date.
     *
     * @param date The date to generate the GeoJSON files for.
     */
    private void generateFlightPathGeoJSON(LocalDate date) {
        String fileName = "drone-" + date.toString() + ".geojson";
        try {
            SimpleModule serializationModule = new SimpleModule().addSerializer(FlightPathNode[].class, new FlightPathNodeGeoJSONSerializer());
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(serializationModule).writeValue(new File(fileName), flightPath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Wrote flight path GeoJSON data in " + fileName + " after " + ((System.nanoTime() - startTime) / 1_000_000_000.0) + "s");
        }
    }
}
