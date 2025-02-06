package uk.ac.ed.inf.pizzadronz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.exception.InvalidBodyException;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.Order;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.Restaurant;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.Restaurants;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.*;


import java.io.IOException;
import java.util.List;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.*;
import static uk.ac.ed.inf.pizzadronz.controller.OrderHandling.extractOrder;
import static uk.ac.ed.inf.pizzadronz.model.GeoJson.GeoJsonStrings.*;
import static uk.ac.ed.inf.pizzadronz.model.PathInfo.AStar.*;

@RestController

/**
 * Controller for finding the path between a restaurant and appleton tower.
 *
 * <p>This class provides the methods to find a path for the drone to take to AT, given an order.</p>

 */

public class PathHandling {
    private final OrderHandling orderHandling = new OrderHandling();
    private final NoFlyZones noFlyZones = new NoFlyZones();

    public PathHandling() {
    }

    /**
     * Given an order, this method calculates the path from the restaurant to AT using A*.
     * @param JsonOrder the order to calculate the path for
     * @return LngLat[] the path from the restaurant to AT in the form of the points making up the path
     * @throws JsonProcessingException
     */
    @PostMapping("/calcDeliveryPath")
    public LngLat[] calcDeliveryPath(@RequestBody JsonNode JsonOrder) throws JsonProcessingException {
        // Extract the order from the JsonNode input
        Order order = extractOrder(JsonOrder);

        //check that the input order is valid
        if (orderHandling.validateOrder(JsonOrder).getOrderStatus().equals(OrderStatus.INVALID)) {
            throw new InvalidBodyException("Invalid order");
        }

        //establish the restaurant that the order is from
        Restaurant orderRestaurant = orderHandling.findOrderRestaurant(order.getPizzasInOrder()[0]);
        LngLat start = orderRestaurant.getLocation();

        //get the path from the restaurant to AT
        LngLat[] totalPath = getPath(start);
        return totalPath;
    }

    /**
     * Given an order, this method calculates the path from the restaurant to AT using A* and returns it as a GeoJson string.
     * @param JsonOrder the order to calculate the path for
     * @return String the path from the restaurant to AT in the form of a GeoJson string
     * @throws IOException
     */
    @PostMapping("/calcDeliveryPathAsGeoJson")
    public String calcDeliveryPathAsGeoJson(@RequestBody JsonNode JsonOrder) throws IOException {
        // Extract the order from the JsonNode input
        Order order = extractOrder(JsonOrder);

        //check that the input order is valid
        if (orderHandling.validateOrder(JsonOrder).getOrderStatus().equals(OrderStatus.INVALID)) {
            throw new InvalidBodyException("Invalid order");
        }

        //establish the restaurant that the order is from
        Restaurant orderRestaurant = orderHandling.findOrderRestaurant(order.getPizzasInOrder()[0]);
        LngLat start = orderRestaurant.getLocation();

        //get the path from the restaurant to AT and convert it to GeoJson
        LngLat[] totalPath = getPath(start);
        return GeoJSONPath(totalPath);
    }


    /**
     * get the path from the restaurant to AT by combining the lower resolution path to a point close to AT and the high resolution path from that point to AT
     * the change between larger drone movement and smaller drone movement is for the sake of computational efficiency
     * @param start the starting point of the path
     * @return the path from the restaurant to AT in the form of the points making up the path
     * @throws JsonProcessingException
     */
    public LngLat[] getPath(LngLat start) throws JsonProcessingException {


        //get the path from the restaurant to AT, starting with large drone steps until close to AT
        List<LngLat> pathStart = astar(start, APPLETON_TOWER, noFlyZones, DRONE_MOVE_DISTANCE_LARGE, DRONE_IS_CLOSE_DISTANCE_LARGE);
        //Convert to an array
        LngLat[] pathLngLatStart = new LngLat[pathStart.size()];
        for (int i = 0; i < pathStart.size(); i++) {
            pathLngLatStart[i] = pathStart.get(i);
        }


        //get the path from the last large movement to AT, with medium drone steps until close to AT
        LngLat middle = new LngLat(pathLngLatStart[pathLngLatStart.length - 1].getLng(), pathLngLatStart[pathLngLatStart.length - 1].getLat());
        List<LngLat> pathMiddle = astar(middle, APPLETON_TOWER,  noFlyZones, DRONE_MOVE_DISTANCE_MEDIUM,DRONE_IS_CLOSE_DISTANCE_MEDIUM);
        // convert pathMiddle to an array
        LngLat[] pathLngLatMiddle = new LngLat[pathMiddle.size()];
        for (int i = 0; i < pathMiddle.size(); i++) {
            pathLngLatMiddle[i] = pathMiddle.get(i);
        }


        //get the path from the last large movement to AT, with medium drone steps until close to AT
        LngLat end = new LngLat(pathLngLatMiddle[pathLngLatMiddle.length - 1].getLng(), pathLngLatMiddle[pathLngLatMiddle.length - 1].getLat());
        List<LngLat> pathEnd = astar(end, APPLETON_TOWER,  noFlyZones, DRONE_MOVE_DISTANCE_SMALL,DRONE_IS_CLOSE_DISTANCE_SMALL);
        // convert pathEnd to an array
        LngLat[] pathLngLatEnd = new LngLat[pathEnd.size()];
        for (int i = 0; i < pathEnd.size(); i++) {
            pathLngLatEnd[i] = pathEnd.get(i);
        }



        //combine the start of the path and middle of the path to AT

        LngLat[] endMiddlePath = new LngLat[pathLngLatStart.length + pathLngLatMiddle.length];
        System.arraycopy(pathLngLatStart, 0, endMiddlePath, 0, pathLngLatStart.length);
        System.arraycopy(pathLngLatMiddle, 0, endMiddlePath, pathLngLatStart.length, pathLngLatMiddle.length);

        //combine the middle and end of the path to AT
        LngLat[] totalPath = new LngLat[endMiddlePath.length + pathLngLatEnd.length];
        System.arraycopy(endMiddlePath, 0, totalPath, 0, endMiddlePath.length);
        System.arraycopy(pathLngLatEnd, 0, totalPath, endMiddlePath.length, pathLngLatEnd.length);


        // split the path up into smaller segments to match the drones movement and return
        return formatPath(totalPath);
    }


    /**
     * Get the no fly zones
     * @return the no fly zones
     */
    public NoFlyZones getNoFlyZones() {
        return noFlyZones;
    }


    /**
     * check if a point is in any no fly zone
     * @param location the point to check
     * @return true if the point is in any no fly zone, false otherwise
     * @throws IOException
     */
    public boolean isInAnyRegion(LngLat location) throws IOException {

        //check for each noFLyZone
        for (NamedRegion noFlyZone : noFlyZones.getNoFlyZones()) {
            //For speed, check plausibility by checking if point is within the bounds of the region
            if (location.getLng() >= noFlyZone.getMinLng() &&
                    location.getLng() <= noFlyZone.getMaxLng() &&
                    location.getLat() >= noFlyZone.getMinLat() &&
                    location.getLat() <= noFlyZone.getMaxLat()) {

                //check if the point is in the region
                boolean inRegion = isInRegion(location, noFlyZone);

                if (inRegion) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * check if a point is in a given region
     * @param location the point to check
     * @param region the region to check for the point in
     * @return true if the point is in the region, false otherwise
     * @throws IOException
     */
    public static boolean isInRegion(LngLat location, NamedRegion region) throws IOException {
        IsInRegionRequest request = new IsInRegionRequest(location, region);

        LngLatHandling lngLatHandling = new LngLatHandling();

        //check if the point is in the region using existing method
        return lngLatHandling.isInRegion(request.toJson());
    }



    /**
     * for testing purposes, override restaurant array
     * @param restaurantArray the new restaurants to use
     */
    public void overrideRestaurants(Restaurant[] restaurantArray) {
        orderHandling.overrideRestaurants(restaurantArray);
    }

    /**
     * for testing purposes, return the restaurants
     * @return the restaurants
     */
    public Restaurants getRestaurants() {
        return orderHandling.getRestaurants();
    }

    /**
     * for testing purposes, return the order handling
     * @return the order handling
     */
    public OrderHandling getOrderHandling() {
        return orderHandling;
    }




}