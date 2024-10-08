package uk.ac.ed.inf.FlightPaths;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;

import java.util.*;

/**
 * Generates the flight paths for the given orders.
 */
public class FlightPathGenerator {
    /**
     * Creates an instance of the {@link LngLatHandler} class.
     */
    private final LngLatHandler                     lngLatHandler = new LngLatHandler();
    /**
     * Stores the cache of flight paths.
     */
    private final Map<String, List<FlightPathNode>> cache         = new HashMap<>();
    /**
     * Stores the central area, which the drone cannot leave once it has entered.
     */
    private final NamedRegion                       centralArea;
    /**
     * Stores the no-fly zones, which the drone cannot enter.
     */
    private final NamedRegion[]                     noFlyZones;
    /**
     * Stores all the restaurants.
     */
    private final Restaurant[]                      restaurants;
    /**
     * Stores the time limit for the A* algorithm.
     */
    private final int                               timeLimit;

    /**
     * Constructs a new {@link FlightPathGenerator} object.
     *
     * @param centralArea the central area, which the drone cannot leave once it has entered
     * @param noFlyZones  the no-fly zones, which the drone cannot enter
     * @param restaurants all the restaurants
     */
    public FlightPathGenerator(NamedRegion centralArea, NamedRegion[] noFlyZones, Restaurant[] restaurants) {
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
        this.restaurants = restaurants;
        this.timeLimit = 20_000 / restaurants.length;
    }

    /**
     * Generates the full flight path for the given orders.
     *
     * @param orders the orders
     *
     * @return the full flight path for the given orders
     */
    public FlightPathNode[] generateFullPath(Order[] orders) {
        var fullPath = new LinkedList<FlightPathNode>();

        for (int i = 0; i < orders.length; i++) {
            Order                order      = orders[i];
            List<FlightPathNode> toAppleton = generate(order);
            if (toAppleton.isEmpty()) continue;
            List<FlightPathNode> toRestaurant = reversePath(toAppleton);

            // Go from Appleton to restaurant.
            fullPath.addAll(toRestaurant);

            // Hover at restaurant.
            fullPath.add(new FlightPathNode(order.getOrderNo(),
                                            toAppleton.get(0).fromCoordinate(),
                                            999,
                                            toAppleton.get(0).toCoordinate()
            ));

            // Go from restaurant to Appleton.
            fullPath.addAll(toAppleton);

            // Hover at Appleton.
            fullPath.add(new FlightPathNode(order.getOrderNo(),
                                            toRestaurant.get(0).fromCoordinate(),
                                            999,
                                            toRestaurant.get(0).toCoordinate()
            ));

            System.out.print("\rGenerated flight path for " + (i + 1) + " orders.");
        }
        System.out.println();

        return fullPath.toArray(FlightPathNode[]::new);
    }

    /**
     * Generates the flight path for the given order.
     *
     * @param order the order
     *
     * @return the flight path for the given order
     */
    private List<FlightPathNode> generate(Order order) {
        Restaurant restaurant = getOrderRestaurant(order, restaurants);
        assert restaurant != null;

        // Set the start position to the restaurant, and the goal position to Appleton Tower.
        LngLat start = restaurant.location();
        var    goal  = new LngLat(-3.186874, 55.944494);

        // Fetch the flight from the cache, or compute it if it is not in the cache.
        List<FlightPathNode> path = cache.computeIfAbsent(restaurant.name(), __ -> aStar(start, goal, 16));
        // Build the path out of new FlightPathNodes, and set the order number.
        path = path.stream().map(node -> new FlightPathNode(order.getOrderNo(), node)).toList();

        order.setOrderStatus(path.isEmpty() ? OrderStatus.VALID_BUT_NOT_DELIVERED : OrderStatus.DELIVERED);
        return path;
    }

    /**
     * Gets the restaurant from the given order.
     *
     * @param order       The order to get the restaurant from.
     * @param restaurants The list of restaurants to search through.
     *
     * @return The restaurant from the given order.
     */
    private Restaurant getOrderRestaurant(Order order, Restaurant[] restaurants) {
        // As this method is only called on valid orders, it is safe to just find the first restaurant that sells the
        // first pizza in the order.
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.menu()) {
                if (pizza.name().equals(order.getPizzasInOrder()[0].name())) return restaurant;
            }
        }
        return null;
    }

    /**
     * Reverses the given path.
     *
     * @param path the path
     *
     * @return the reversed path
     */
    private List<FlightPathNode> reversePath(List<FlightPathNode> path) {
        var reversedPath = new LinkedList<FlightPathNode>();
        for (int i = path.size() - 1; i >= 0; i--) {
            FlightPathNode node = path.get(i);
            // Create a new instance of FlightPathNode, but with the angle reversed.
            reversedPath.add(new FlightPathNode((node.angle() + 180) % 360, node));
        }
        return reversedPath;
    }

    /**
     * Performs the <a href=https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode>A* algorithm</a> to find the
     * shortest path from the start to the goal.
     *
     * @param start         the start
     * @param goal          the goal
     * @param maxNeighbours the maximum number of neighbours to consider
     *
     * @return the shortest path from the start to the goal
     */
    private List<FlightPathNode> aStar(LngLat start, LngLat goal, int maxNeighbours) {
        long startTime = System.currentTimeMillis();

        // The set of nodes already evaluated.
        var cameFrom = new HashMap<LngLat, FlightPathNode>();

        // The cost of going from the start to each node.
        var gScore = new HashMap<LngLat, Double>();
        gScore.put(start, 0.0);

        // The cost of going from each node to the goal.
        var fScore = new HashMap<LngLat, Double>();
        fScore.put(start, heuristic(start, goal));

        // The set of currently discovered nodes that are not evaluated yet.
        var openSet = new PriorityQueue<LngLat>(Comparator.comparingDouble(value -> fScore.getOrDefault(value,
                                                                                                        Double.MAX_VALUE
                                                                                                       )));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // If the algorithm has been running for too long, try again with a smaller number of neighbours, until
            // the number of neighbours is 4, in which case return an empty list.
            if (System.currentTimeMillis() - startTime > timeLimit)
                return maxNeighbours > 4 ? aStar(start, goal, maxNeighbours / 2) : new LinkedList<>();

            // Get the next node to evaluate, and if it is the goal, return the path to it.
            LngLat current = openSet.remove();
            if (lngLatHandler.isCloseTo(current, goal)) return reconstructPath(cameFrom, current);

            // For each neighbour of the current node, check if it is in a no-fly zone, or if the line between the
            // current node and the neighbour crosses a no-fly zone, or if the neighbour leaves the central area
            // after entering it. If so, skip the neighbour. Otherwise, update the neighbour's g-score and f-score,
            // and add it to the open set.
            boolean inCentralArea = lngLatHandler.isInCentralArea(current, centralArea);
            for (int i = 0; i < maxNeighbours; i++) {
                double angle     = i * 360.0 / maxNeighbours;
                LngLat neighbour = lngLatHandler.nextPosition(current, angle);

                // If the neighbour is not in a legal position, skip it.
                boolean crossesNoFlyZone = Arrays
                        .stream(noFlyZones)
                        .anyMatch(region -> lngLatHandler.lineCrossesRegion(current, neighbour, region));
                boolean leavesCentralArea = inCentralArea && !lngLatHandler.isInCentralArea(neighbour, centralArea);
                if (crossesNoFlyZone || leavesCentralArea) continue;

                // Update the neighbour's g-score and f-score, and add it to the open set.
                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + heuristic(current, neighbour);
                if (tentativeGScore < gScore.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    cameFrom.put(neighbour, new FlightPathNode(current, angle, neighbour));
                    gScore.put(neighbour, tentativeGScore);
                    fScore.put(neighbour, tentativeGScore + heuristic(neighbour, goal));
                    openSet.add(neighbour);
                }
            }
        }

        // If no path has been found yet, there is no path.
        return new LinkedList<>();
    }

    /**
     * Calculates the heuristic value for the given node. In this case, the heuristic value is the Euclidean distance
     * between the node and the goal.
     *
     * @param node the node
     * @param goal the goal
     *
     * @return the heuristic value for the given node
     */
    private double heuristic(LngLat node, LngLat goal) {
        return lngLatHandler.distanceTo(node, goal);
    }

    /**
     * Reconstructs the path from the start to the given node.
     *
     * @param cameFrom the map of nodes to their previous nodes
     * @param current  the current node
     *
     * @return the path from the start to the given node
     */
    private List<FlightPathNode> reconstructPath(Map<LngLat, FlightPathNode> cameFrom, LngLat current) {
        var totalPath = new LinkedList<FlightPathNode>();
        // Follow the path backwards, and add each node to the total path.
        while (current != null) {
            FlightPathNode cameFromCurrent = cameFrom.get(current);
            if (cameFromCurrent != null) {
                totalPath.add(0, cameFromCurrent);
                current = cameFromCurrent.fromCoordinate();
            } else current = null;
        }
        return totalPath;
    }
}
