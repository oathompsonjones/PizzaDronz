package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.ac.ed.inf.FlightPaths.FlightPathGenerator;
import uk.ac.ed.inf.FlightPaths.FlightPathNode;
import uk.ac.ed.inf.RestService.OrderValidator;
import uk.ac.ed.inf.RestService.RESTManager;
import uk.ac.ed.inf.Serializers.FlightPathNodeGeoJSONSerializer;
import uk.ac.ed.inf.Serializers.FlightPathNodeJSONSerializer;
import uk.ac.ed.inf.Serializers.OrderJSONSerializer;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
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
     * Stores the instance of the {@link RESTManager} class.
     */
    private final RESTManager      restManager;
    /**
     * Creates an instance of the {@link OrderValidator} class.
     */
    private final OrderValidator   orderValidator = new OrderValidator();
    /**
     * Stores the flight paths.
     */
    private       FlightPathNode[] flightPath     = null;
    /**
     * Stores the orders.
     */
    private       Order[]          orders         = null;

    /**
     * Creates an instance of the {@link PizzaDronz} class.
     *
     * @param apiUrl The URL of the API.
     * @param date   The date to generate the flight paths for.
     */
    private PizzaDronz(String apiUrl, LocalDate date) {
        // Set up the RESTManager and the FlightPathGenerator.
        restManager = new RESTManager(apiUrl);
        var flightPathGenerator = new FlightPathGenerator(restManager.getCentralArea(),
                                                          restManager.getNoFlyZones(),
                                                          restManager.getRestaurants()
        );

        // Fetch and validate the orders.
        Order[] validOrders = fetchValidOrders(date);
        System.out.println("Fetched " + orders.length + " orders. " + validOrders.length + " are valid.");

        // Generate the flight path.
        flightPath = flightPathGenerator.generateFullPath(validOrders);
        System.out.println("Generated flight path.");

        // Generate the JSON files.
        generateFlightPathJSON(date);
        generateFlightPathGeoJSON(date);
        generateDeliveryJSON(date);
    }

    /**
     * Entry point of the application.
     *
     * @param args The command line arguments. The first argument is the date and the second argument is the path to the
     *             file containing the orders.
     */
    public static void main(String[] args) {
        // Check if the correct number of arguments were provided.
        if (args.length != 2) {
            System.err.println("Please provide arguments in the following format: <date> <rest-url>");
            return;
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Starting PizzaDronz.");
        new PizzaDronz(args[1], LocalDate.parse(args[0]));
        System.out.println("Exiting PizzaDronz. Calculations took " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Fetches and validates all orders for the given date.
     *
     * @param date The date to fetch the orders for.
     *
     * @return The valid orders.
     */
    private Order[] fetchValidOrders(LocalDate date) {
        Restaurant[] restaurants = restManager.getRestaurants();
        orders = restManager.getOrders(date);
        // Each order in this.orders will be validated as a side effect, before filtering out any invalid orders.
        return Arrays
                .stream(orders)
                .filter(order -> orderValidator.validateOrder(order, restaurants).getOrderStatus()
                                 == OrderStatus.VALID_BUT_NOT_DELIVERED)
                .toArray(Order[]::new);
    }

    /**
     * Generates the deliveries JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateDeliveryJSON(LocalDate date) {
        writeFile("deliveries-" + date + ".json", Order.class, new OrderJSONSerializer(), orders);
    }

    /**
     * Generates the flight path JSON file for the given date.
     *
     * @param date The date to generate the JSON files for.
     */
    private void generateFlightPathJSON(LocalDate date) {
        writeFile("flightpath-" + date + ".json", FlightPathNode.class, new FlightPathNodeJSONSerializer(), flightPath);
    }

    /**
     * Generates the flight path GeoJSON file for the given date.
     *
     * @param date The date to generate the GeoJSON files for.
     */
    private void generateFlightPathGeoJSON(LocalDate date) {
        writeFile("drone-" + date + ".geojson",
                  FlightPathNode[].class,
                  new FlightPathNodeGeoJSONSerializer(),
                  flightPath
                 );
    }

    /**
     * Writes the given content to the given file.
     *
     * @param fileName   The name of the file to write to.
     * @param dataType   The class of the data type.
     * @param serializer The serializer to use.
     * @param content    The content to write.
     * @param <T>        The type of the data.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private <T> void writeFile(String fileName, Class<T> dataType, StdSerializer<T> serializer, Object content) {
        try {
            var module = new SimpleModule().addSerializer(dataType, serializer);
            var file   = new File("resultfiles/" + fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();
            new ObjectMapper()
                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .registerModule(module)
                    .writeValue(file, content);
            System.out.println("Wrote data to resultfiles/" + fileName + ".");
        } catch (Exception err) {
            System.err.println(err.getMessage());
        }
    }
}
