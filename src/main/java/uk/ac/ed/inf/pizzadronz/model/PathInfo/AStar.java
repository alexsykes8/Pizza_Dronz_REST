package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.pizzadronz.controller.LngLatHandling;

import java.util.*;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.DRONE_MOVE_DISTANCE;
import static uk.ac.ed.inf.pizzadronz.controller.LngLatHandling.IsInRegionHelper;


/**
 * Represents the A* algorithm for pathfinding
 * <p> Provides methods for pathfinding and obstacle avoidance </p>
 */
public class AStar {

    private static double[][] directions;

    public AStar() {

    }

    /** Changes the size of the segments of the path generated. This is for efficiency,
     * the drone only becomes more accurate in its path as it gets closer to the destination
     * @param size the size of the path segments
     */
    public static void setSegmentSize(double size) {
        directions = new double[][]{
                {0, size},      // N
                {Math.toRadians(22.5), size},  // NNE
                {Math.toRadians(45), size},    // NE
                {Math.toRadians(67.5), size},  // ENE
                {Math.toRadians(90), size},    // E
                {Math.toRadians(112.5), size}, // ESE
                {Math.toRadians(135), size},   // SE
                {Math.toRadians(157.5), size}, // SSE
                {Math.toRadians(180), size},   // S
                {Math.toRadians(202.5), size}, // SSW
                {Math.toRadians(225), size},   // SW
                {Math.toRadians(247.5), size}, // WSW
                {Math.toRadians(270), size},   // W
                {Math.toRadians(292.5), size}, // WNW
                {Math.toRadians(315), size},   // NW
                {Math.toRadians(337.5), size}
        };
    }

    /**
     * Euclidean distance estimates the cost of the cheapest path to the goal, aka heuristic
     * @param a the first location
     * @param b the second location
     * @return the euclidean distance between the two locations
      */
    public static double euclideanDistance(LngLat a, LngLat b) {
        return Math.sqrt(Math.pow(a.getLng() - b.getLng(), 2) + Math.pow(a.getLat() - b.getLat(), 2));
    }

    /**
     * Checks if a path is obstructed by any no-fly zones
     * @param current the start of the path segment
     * @param neighbour the node that the algorithm is considering moving to
     * @param noFlyZones the no-fly zones
     * @param pathSize  the size of the path segment
     * @return true if the path is obstructed, false otherwise
     * @throws JsonProcessingException
     */
    public static boolean isObstacle(LngLat current, LngLat neighbour, NoFlyZones noFlyZones, double pathSize) throws JsonProcessingException {
        // Creates a segment between the two locations and checks if it intersects with any no-fly zone
        LngLat[] pathSegment = {current, neighbour};
        if (doesPathIntersectWithAnyRegion(pathSegment, noFlyZones.getNoFlyZones(), pathSize)){
            return true;
        }

        // check if the path is leaving central
        if (isPathLeavingCentralRegion(pathSegment, noFlyZones.getCentral(), pathSize)) {
            return false;
        }


        return false;
    }

    /**
     * Check if the path will leave central if it goes to this neighbour
     * @param pathSegment the segment of the path to check
     * @param centralRegion the central region
     * @param pathSize      the size of the path segment
     * @return true if the path is leaving central, false otherwise
     * @throws JsonProcessingException
     */
    private static boolean isPathLeavingCentralRegion(LngLat[] pathSegment, NamedRegion centralRegion, double pathSize) throws JsonProcessingException {

        if (!pointCloseToRegion(pathSegment[0], centralRegion, pathSize)) {
            return false; // Start point is not close enough to the central region for it to be possible for  the start point to be in central
        }

        else{
            IsInRegionRequest request = new IsInRegionRequest(pathSegment[0], centralRegion);
            boolean inRegion = IsInRegionHelper(request);

            if (!inRegion) { // if the start point is not in the central region, there is no risk that the drone can move out of central
                return false;
            }

            // check if end of the path is outside central
            request = new IsInRegionRequest(pathSegment[1], centralRegion);
            inRegion = IsInRegionHelper(request);
            if (inRegion) { // the end point is in central, so the drone is not leaving central
                return false;
            }
        }

        return true;
    }

    /**
     * A* algorithm
     * @param start the start location
     * @param goal the goal location
     * @param noFlyZones the no-fly zones
     * @param movement_size the size of the path segments
     * @param close_to_AT the distance to the goal at which the path is considered close enough for the path to be finished
     * @return the path from start to goal
      */
    public static List<LngLat> astar(LngLat start, LngLat goal, NoFlyZones noFlyZones, double movement_size, double close_to_AT) throws JsonProcessingException {
        // sets the size of each segment of the path
        setSegmentSize(movement_size);

        // Priority queue for open list to select nodes for exploration
        PriorityQueue<AStarNode> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));

        // for closed list to keep track of explored nodes
        Set<String> closedList = new HashSet<>();

        // Add initial node to the open list
        openList.add(new AStarNode(start, 0, euclideanDistance(start, goal)));

        // Map to reconstruct the path
        Map<String, LngLat> cameFrom = new HashMap<>();
        Map<String, Double> gCost = new HashMap<>();
        gCost.put(start.toString(), 0.0);

        while (!openList.isEmpty()) {
            // pops the node with the lowest f
            AStarNode currentNode = openList.poll();
            LngLat current = currentNode.position;

            // If path gets close to AT, reconstruct the path
            if (euclideanDistance(current, goal) <= close_to_AT) {
                List<LngLat> path = new ArrayList<>();
                while (cameFrom.containsKey(current.toString())) {
                    path.add(current);
                    current = cameFrom.get(current.toString());
                }
                path.add(start);
                Collections.reverse(path);
                return path;
            }

            closedList.add(current.toString());

            // Explore neighbors
            for (double[] direction : directions) {
                double angle = direction[0];
                double stepSize = direction[1];

                // Calculate the neighbor coords
                double dx = stepSize * Math.cos(angle);
                double dy = stepSize * Math.sin(angle);
                LngLat neighbour = new LngLat(current.getLng() + dx, current.getLat() + dy );

                // Skip neighbors that are obstacles or already in closed list
                if (isObstacle(current, neighbour, noFlyZones, movement_size) || closedList.contains(neighbour.toString())) {
                    continue;
                }

                // update g, the cost of the cheapest path from start to the neighbour, by finding a tentative cost to the neighbour through the current node
                double tentativeG = gCost.get(current.toString()) + stepSize;

                // If tentative g is lower than g was for the node, update g and add it to the open list
                if (!gCost.containsKey(neighbour.toString()) || tentativeG < gCost.get(neighbour.toString())) {
                    gCost.put(neighbour.toString(), tentativeG);
                    double f = tentativeG + euclideanDistance(neighbour, goal);
                    openList.add(new AStarNode(neighbour, tentativeG, f));
                    // update the path to the neighbour
                    cameFrom.put(neighbour.toString(), current);
                }
            }
        }

        // No path found
        return null;
    }


    /**
     * Line Segment Intersection Algorithm using the Orientation Method
     * Function to check if two line segments p1p2 and q1q2 intersect
     * @param p1 the start of the first line segment
     * @param p2 the end of the first line segment
     * @param q1 the start of the second line segment
     * @param q2 the end of the second line segment
     * @return true if the segments intersect, false otherwise
     */
    public static boolean doSegmentsIntersect(LngLat p1, LngLat p2, LngLat q1, LngLat q2) {
        // Calculate the orientations
        int o1 = orientation(p1, p2, q1);
        int o2 = orientation(p1, p2, q2);
        int o3 = orientation(q1, q2, p1);
        int o4 = orientation(q1, q2, p2);

        // General case, the orientations are different, segments intersect
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Special case of the points being collinear, checks if the points are also on the segments
        if (o1 == 0 && onSegment(p1, q1, p2)) return true;
        if (o2 == 0 && onSegment(p1, q2, p2)) return true;
        if (o3 == 0 && onSegment(q1, p1, q2)) return true;
        if (o4 == 0 && onSegment(q1, p2, q2)) return true;

        return false; // No intersection
    }

    /**
     * Function to compute orientation of the triplet (p, q, r)
     * @param p the first point
     * @param q the second point
     * @param r the third point
     * @return 0 if the points are collinear, 1 if they are clockwise, 2 if they are counterclockwise
     */
    public static int orientation(LngLat p, LngLat q, LngLat r) {
        double val = (q.getLat() - p.getLat()) * (r.getLng() - q.getLng()) -
                (q.getLng() - p.getLng()) * (r.getLat() - q.getLat());
        if (val == 0) {
            // p, q and r are collinear
            return 0;
        }
        // 1 indicates clockwise, 2 indicates counterclockwise
        return (val > 0) ? 1 : 2;
    }

    /**
     * Given three collinear points, checks if point q lies on the segment pr
     * @param p the start of the segment
     * @param q the point to check
     * @param r the end of the segment
     * @return true if q lies on the segment pr, false otherwise
     */
    public static boolean onSegment(LngLat p, LngLat q, LngLat r) {
        return (q.getLng() <= Math.max(p.getLng(), r.getLng()) && q.getLng() >= Math.min(p.getLng(), r.getLng()) &&
                q.getLat() <= Math.max(p.getLat(), r.getLat()) && q.getLat() >= Math.min(p.getLat(), r.getLat()));
    }

    /**
     * Function to check if the path intersects with any region
     * @param path the path to check
     * @param regions the regions to check against
     * @param pathSize the size of the path segment
     * @return true if the path intersects with any region, false otherwise
     */
    public static boolean doesPathIntersectWithAnyRegion(LngLat[] path, NamedRegion[] regions, double pathSize) {
        for (NamedRegion region : regions) {
            // for each region, check for an intersection
            if (doesPathIntersectWithRegion(path, region, pathSize)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function to check if the path intersects with a specific polygon
     * @param path the path to check
     * @param region the region to check against
     * @param pathSize the size of the path segment
     * @return true if the path intersects with the region, false otherwise
     */
    public static boolean doesPathIntersectWithRegion(LngLat[] path, NamedRegion region, double pathSize) {
        if (!pointCloseToRegion(path[0], region, pathSize)) {
            return false; // Start or end point is not close enough to the region for an intersection to be possible
        }
        // Iterate through each segment of the path
        for (int i = 0; i < path.length - 1; i++) {

            LngLat pathStart = path[i];
            LngLat pathEnd = path[i + 1];

            // Iterate through each edge of the polygon
            for (int j = 0; j < region.getVertices().length; j++) {
                LngLat polyStart = region.getVertices()[j];
                LngLat polyEnd = region.getVertices()[(j + 1) % region.getVertices().length]; // Wrap around to first vertex

                if (doSegmentsIntersect(pathStart, pathEnd, polyStart, polyEnd)) {
                    return true; // Found an intersection
                }
            }
        }
        return false; // No intersection found
    }

    /**
     * If the end point of the path is not close to the region, there is no possibility of an intersection
     * @param point the point to check
     * @param region the region to check against
     * @param pathSize the size of the path segment
     * @return true if the point is close to the region, false otherwise
     */
    public static boolean pointCloseToRegion(LngLat point, NamedRegion region, double pathSize) {
        if (point.getLat() > (region.getMaxLat() + pathSize) || point.getLat() < region.getMinLat() - pathSize
                || point.getLng() > region.getMaxLng() + pathSize || point.getLng() < region.getMinLng() - pathSize) {
            return false; // Point is too far away for an intersection to be possible
        }
        return true;
    }

    /**
     * Break the path up into segments of a certain size
     * @param path the path to format
     * @return the path broken up into smaller segments, which are each the size of a drones movement
     */
    public static LngLat[] formatPath(LngLat[] path) {
        List<LngLat> formattedPath = new ArrayList<>();
        // for each segment of the path, break up into smaller segments
        for (int i = 0; i < path.length - 1; i++) {
            LngLat start = path[i];
            LngLat end = path[i + 1];
            // find the size of the path segment
            double distance = euclideanDistance(start, end);
            // find the number of smaller segments needed to break up the segment. It should fit perfectly, however the round is used to account for rounding errors
            int segments = (int) Math.round(distance / DRONE_MOVE_DISTANCE);
            // find the movement vectors
            double dlng = (end.getLng() - start.getLng()) / segments;
            double dlat = (end.getLat() - start.getLat()) / segments;
            // add the smaller segments to the path
            for (int j = 0; j < segments; j++) {
                formattedPath.add(new LngLat(start.getLng() + j * dlng, start.getLat() + j * dlat));
            }
        }
        // add the last point
        formattedPath.add(path[path.length - 1]);
        // return the formatted path as an array
        return formattedPath.toArray(new LngLat[0]);

    }


}

