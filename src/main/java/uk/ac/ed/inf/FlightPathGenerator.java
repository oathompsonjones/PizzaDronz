package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;

import java.util.*;

/**
 * Generates the flight paths for the given orders.
 */
public class FlightPathGenerator {
    /**
     * Stores the central area, which the drone cannot leave once it has entered.
     */
    private final NamedRegion                   centralArea;
    /**
     * Stores the no-fly zones, which the drone cannot enter.
     */
    private final NamedRegion[]                 noFlyZones;
    /**
     * Stores all the restaurants.
     */
    private final Restaurant[]                  restaurants;
    /**
     * Stores the time limit for the A* algorithm.
     */
    private final int                           timeLimit;
    /**
     * Stores the cache of flight paths.
     */
    private final Map<String, FlightPathNode[]> cache = new HashMap<>();

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
     * @return the full flight path for the given orders
     */
    public FlightPathNode[] generateFullPath(Order[] orders) {
        List<FlightPathNode> fullPath = new LinkedList<>();
        for (Order order : orders) {
            FlightPathNode[] restaurantToAppleton = generate(order);
            FlightPathNode[] appletonToRestaurant = reversePath(restaurantToAppleton);
            // Go from Appleton to restaurant.
            fullPath.addAll(Arrays.asList(appletonToRestaurant));
            // Hover at restaurant.
            fullPath.add(new FlightPathNode(order.getOrderNo(), restaurantToAppleton[0].fromCoordinate(), 999, restaurantToAppleton[0].toCoordinate()));
            // Go from restaurant to Appleton.
            fullPath.addAll(Arrays.asList(restaurantToAppleton));
            // Hover at Appleton.
            fullPath.add(new FlightPathNode(order.getOrderNo(), appletonToRestaurant[0].fromCoordinate(), 999, appletonToRestaurant[0].toCoordinate()));
        }
        return fullPath.toArray(FlightPathNode[]::new);
    }

    /**
     * Generates the flight path for the given order.
     *
     * @param order the order
     * @return the flight path for the given order
     */
    private FlightPathNode[] generate(Order order) {
        Restaurant restaurant = getOrderRestaurant(order, restaurants);
        assert restaurant != null;
        LngLat           start = restaurant.location();
        LngLat           goal  = PizzaDronz.appletonTower;
        FlightPathNode[] path  = Arrays.stream(cache.computeIfAbsent(restaurant.name(), k -> aStar(start, goal, 16))).map(FlightPathNode::new).toArray(FlightPathNode[]::new);
        for (FlightPathNode node : path)
            node.setOrderNo(order.getOrderNo());
        order.setOrderStatus(path.length == 0 ? OrderStatus.VALID_BUT_NOT_DELIVERED : OrderStatus.DELIVERED);
        return path;
    }

    /**
     * Gets the restaurant from the given order.
     *
     * @param order       The order to get the restaurant from.
     * @param restaurants The list of restaurants to search through.
     * @return The restaurant from the given order.
     */
    private Restaurant getOrderRestaurant(Order order, Restaurant[] restaurants) {
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.menu()) {
                if (Objects.equals(pizza.name(), order.getPizzasInOrder()[0].name()))
                    return restaurant;
            }
        }
        return null;
    }

    /**
     * Reverses the given path.
     *
     * @param path the path
     * @return the reversed path
     */
    private FlightPathNode[] reversePath(FlightPathNode[] path) {
        FlightPathNode[] reversedPath = new FlightPathNode[path.length];
        for (int i = 0; i < reversedPath.length; i++) {
            reversedPath[i] = new FlightPathNode(path[path.length - i - 1]);
            reversedPath[i].setAngle((reversedPath[i].angle() + 180) % 360);
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
     * @return the shortest path from the start to the goal
     */
    private FlightPathNode[] aStar(LngLat start, LngLat goal, int maxNeighbours) {
        long startTime = System.currentTimeMillis();

        // The set of nodes already evaluated.
        Map<LngLat, LngLatAngle> cameFrom = new HashMap<>();

        // The cost of going from the start to each node.
        Map<LngLat, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        // The cost of going from each node to the goal.
        Map<LngLat, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, goal));

        // The set of currently discovered nodes that are not evaluated yet.
        Queue<LngLat> openSet = new PriorityQueue<>(Comparator.comparingDouble(d -> fScore.getOrDefault(d, Double.MAX_VALUE)));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // If the algorithm has been running for too long, try again with a smaller number of neighbours, until
            // the number of neighbours is 4, in which case return an empty list.
            if (System.currentTimeMillis() - startTime > timeLimit)
                return maxNeighbours > 4 ? aStar(start, goal, maxNeighbours / 2) : new FlightPathNode[0];

            // Get the next node to evaluate, and if it is the goal, return the path to it.
            LngLat current = openSet.remove();
            if (PizzaDronz.lngLatHandler.isCloseTo(current, goal))
                return reconstructPath(cameFrom, current);

            // For each neighbour of the current node, check if it is in a no-fly zone, or if the line between the
            // current node and the neighbour crosses a no-fly zone, or if the neighbour leaves the central area
            // after entering it. If so, skip the neighbour. Otherwise, update the neighbour's g-score and f-score,
            // and add it to the open set.
            boolean currentInCentralArea = PizzaDronz.lngLatHandler.isInCentralArea(current, centralArea);
            for (LngLatAngle neighbour : PizzaDronz.lngLatHandler.getNeighbours(current, maxNeighbours)) {
                boolean neighbourInNoFlyZoneOrLineCrossesNoFlyZone = Arrays.stream(noFlyZones).anyMatch(noFlyZone -> PizzaDronz.lngLatHandler.lineCrossesRegion(current, neighbour.lngLat(), noFlyZone));
                boolean leavesCentralAreaAfterEntering             = currentInCentralArea && !PizzaDronz.lngLatHandler.isInCentralArea(neighbour.lngLat(), centralArea);
                if (neighbourInNoFlyZoneOrLineCrossesNoFlyZone || leavesCentralAreaAfterEntering)
                    continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + heuristic(current, neighbour.lngLat());
                if (tentativeGScore < gScore.getOrDefault(neighbour.lngLat(), Double.MAX_VALUE)) {
                    cameFrom.put(neighbour.lngLat(), new LngLatAngle(current, neighbour.angle(), (int) (System.nanoTime() - PizzaDronz.startTime)));
                    gScore.put(neighbour.lngLat(), tentativeGScore);
                    fScore.put(neighbour.lngLat(), tentativeGScore + heuristic(neighbour.lngLat(), goal));
                    openSet.add(neighbour.lngLat());
                }
            }
        }

        // If no path has been found yet, there is no path.
        return new FlightPathNode[0];
    }

    /**
     * Calculates the heuristic value for the given node.
     * In this case, the heuristic value is the Euclidean distance between the node and the goal.
     *
     * @param node the node
     * @param goal the goal
     * @return the heuristic value for the given node
     */
    private double heuristic(LngLat node, LngLat goal) {
        return PizzaDronz.lngLatHandler.distanceTo(node, goal);
    }

    /**
     * Reconstructs the path from the start to the given node.
     *
     * @param cameFrom the map of nodes to their previous nodes
     * @param current  the current node
     * @return the path from the start to the given node
     */
    private FlightPathNode[] reconstructPath(Map<LngLat, LngLatAngle> cameFrom, LngLat current) {
        List<FlightPathNode> totalPath = new LinkedList<>();
        while (current != null) {
            LngLatAngle cameFromCurrent = cameFrom.get(current);
            if (cameFromCurrent != null) {
                totalPath.add(0, new FlightPathNode(cameFromCurrent.lngLat(), cameFromCurrent.angle(), current));
                current = cameFrom.get(current) == null ? null : cameFrom.get(current).lngLat();
            } else
                current = null;
        }
        return totalPath.toArray(FlightPathNode[]::new);
    }
}
