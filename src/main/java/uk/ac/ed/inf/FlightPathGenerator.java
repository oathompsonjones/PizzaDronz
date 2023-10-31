package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.*;

public class FlightPathGenerator {
    private final NamedRegion               centralArea;
    private final NamedRegion[]             noFlyZones;
    private final Restaurant[]              restaurants;
    private final int                       timeLimit;
    private final Map<String, List<LngLat>> cache = new HashMap<>();

    public FlightPathGenerator(NamedRegion centralArea, NamedRegion[] noFlyZones, Restaurant[] restaurants) {
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
        this.restaurants = restaurants;
        this.timeLimit = 20_000 / restaurants.length;
    }

    public List<List<LngLat>> generate(Order[] orders) {
        return Arrays.stream(orders).map(this::generate).toList();
    }

    private List<LngLat> generate(Order order) {
        System.out.println(PizzaDronz.getOrderRestaurant(order, restaurants).name());
        Restaurant restaurant = PizzaDronz.getOrderRestaurant(order, restaurants);
        LngLat     start      = restaurant.location();
        LngLat     goal       = PizzaDronz.appletonTower;
        return cache.computeIfAbsent(restaurant.name(), k -> aStar(start, goal, 16));
    }

    private List<LngLat> aStar(LngLat start, LngLat goal, int maxNeighbours) {
        System.out.println("A* with " + maxNeighbours + " neighbours");
        long startTime = System.currentTimeMillis();

        Map<LngLat, LngLat> cameFrom = new HashMap<>();

        Map<LngLat, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        Map<LngLat, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, goal));

        Queue<LngLat> openSet = new PriorityQueue<>(Comparator.comparingDouble(d -> fScore.getOrDefault(d, Double.MAX_VALUE)));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            if (System.currentTimeMillis() - startTime > timeLimit)
                return maxNeighbours > 4 ? aStar(start, goal, maxNeighbours / 2) : new LinkedList<>();

            LngLat current = openSet.remove();
            if (PizzaDronz.lngLatHandler.isCloseTo(current, goal))
                return reconstructPath(cameFrom, current);

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

        return new LinkedList<>();
    }

    private double heuristic(LngLat node, LngLat goal) {
        return PizzaDronz.lngLatHandler.distanceTo(node, goal);
    }

    private List<LngLat> reconstructPath(Map<LngLat, LngLat> cameFrom, LngLat current) {
        List<LngLat> totalPath = new LinkedList<>();
        while (current != null) {
            totalPath.add(0, current);
            current = cameFrom.get(current);
        }
        return totalPath;
    }
}
