package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.*;

/**
 * Generates the flight paths for the given orders.
 */
public class FlightPathGenerator {
    /**
     * Stores the central area, which the drone cannot leave once it has entered.
     */
    private final NamedRegion               centralArea;
    /**
     * Stores the no-fly zones, which the drone cannot enter.
     */
    private final NamedRegion[]             noFlyZones;
    /**
     * Stores all the restaurants.
     */
    private final Restaurant[]              restaurants;
    /**
     * Stores the time limit for the A* algorithm.
     */
    private final int                       timeLimit;
    /**
     * Stores the cache of flight paths.
     */
    private final Map<String, List<LngLat>> cache = new HashMap<>();

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
     * Generates the flight paths for the given orders.
     *
     * @param orders the orders
     * @return the flight paths for the given orders
     */
    public List<List<LngLat>> generate(Order[] orders) {
        return Arrays.stream(orders).map(this::generate).toList();
    }

    /**
     * Generates the flight path for the given order.
     *
     * @param order the order
     * @return the flight path for the given order
     */
    private List<LngLat> generate(Order order) {
        Restaurant restaurant = PizzaDronz.getOrderRestaurant(order, restaurants);
        LngLat     start      = restaurant.location();
        LngLat     goal       = PizzaDronz.appletonTower;
        return cache.computeIfAbsent(restaurant.name(), k -> aStar(start, goal, 16));
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
    private List<LngLat> aStar(LngLat start, LngLat goal, int maxNeighbours) {
        long startTime = System.currentTimeMillis();

        // The set of nodes already evaluated.
        Map<LngLat, LngLat> cameFrom = new HashMap<>();

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
                return maxNeighbours > 4 ? aStar(start, goal, maxNeighbours / 2) : new LinkedList<>();

            // Get the next node to evaluate, and if it is the goal, return the path to it.
            LngLat current = openSet.remove();
            if (PizzaDronz.lngLatHandler.isCloseTo(current, goal))
                return reconstructPath(cameFrom, current);

            // For each neighbour of the current node, check if it is in a no-fly zone, or if the line between the
            // current node and the neighbour crosses a no-fly zone, or if the neighbour leaves the central area
            // after entering it. If so, skip the neighbour. Otherwise, update the neighbour's g-score and f-score,
            // and add it to the open set.
            boolean currentInCentralArea = PizzaDronz.lngLatHandler.isInCentralArea(current, centralArea);
            for (LngLat neighbour : PizzaDronz.lngLatHandler.getNeighbours(current, maxNeighbours)) {
                boolean neighbourInNoFlyZoneOrLineCrossesNoFlyZone = Arrays.stream(noFlyZones).anyMatch(noFlyZone -> PizzaDronz.lngLatHandler.isInRegion(neighbour, noFlyZone) || PizzaDronz.lngLatHandler.lineCrossesRegion(current, neighbour, noFlyZone));
                boolean leavesCentralAreaAfterEntering             = currentInCentralArea && !PizzaDronz.lngLatHandler.isInCentralArea(neighbour, centralArea);
                if (neighbourInNoFlyZoneOrLineCrossesNoFlyZone || leavesCentralAreaAfterEntering)
                    continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + heuristic(current, neighbour);
                if (tentativeGScore < gScore.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
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
    private List<LngLat> reconstructPath(Map<LngLat, LngLat> cameFrom, LngLat current) {
        List<LngLat> totalPath = new LinkedList<>();
        while (current != null) {
            totalPath.add(0, current);
            current = cameFrom.get(current);
        }
        return totalPath;
    }
}
