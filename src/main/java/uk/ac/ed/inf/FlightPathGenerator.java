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
    private final Map<String, List<LngLat>> cache = new HashMap<>();

    public FlightPathGenerator(NamedRegion centralArea, NamedRegion[] noFlyZones, Restaurant[] restaurants) {
        this.centralArea = centralArea;
        this.noFlyZones = noFlyZones;
        this.restaurants = restaurants;
    }

    public List<LngLat> generate(Order order) {
        LngLat start    = PizzaDronz.getOrderRestaurant(order, restaurants).location();
        LngLat goal     = PizzaDronz.appletonTower;
        String cacheKey = start + "," + goal;
        return cache.computeIfAbsent(cacheKey, k -> aStar(start, goal));
    }

    private List<LngLat> aStar(LngLat start, LngLat goal) {
        Map<LngLat, LngLat> cameFrom = new HashMap<>();

        Map<LngLat, Double> gScore = new HashMap<>();
        gScore.put(start, 0.0);

        Map<LngLat, Double> fScore = new HashMap<>();
        fScore.put(start, heuristic(start, goal));

        Queue<LngLat> openSet = new PriorityQueue<>(Comparator.comparingDouble(d -> fScore.getOrDefault(d, Double.MAX_VALUE)));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            LngLat current = openSet.remove();
            if (PizzaDronz.lngLatHandler.isCloseTo(current, goal))
                return reconstructPath(cameFrom, current);

            boolean currentInCentralArea = PizzaDronz.lngLatHandler.isInCentralArea(current, centralArea);
            for (LngLat neighbour : PizzaDronz.lngLatHandler.getNeighbours(current)) {
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

        return null;
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
