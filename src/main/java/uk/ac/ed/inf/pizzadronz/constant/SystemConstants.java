package uk.ac.ed.inf.pizzadronz.constant;


import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;

/**
 *
 */
public final class SystemConstants {

    /**
     * the charge for any order
     */
    public static final int ORDER_CHARGE_IN_PENCE = 100;

    /**
     * the maximum number of pizzas in one order
     */
    public static final int MAX_PIZZAS_PER_ORDER = 4;

    /**
     * the distance a drone can move in 1 iteration when very far from AT
     */
    public static final double DRONE_MOVE_DISTANCE_LARGE = 0.00390;

    /**
     * the distance which is considered "close" when starting very far from AT
     */
    public static final double DRONE_IS_CLOSE_DISTANCE_LARGE = 0.02055;

    /**
     * the distance a drone can move in 1 iteration when far from AT
     */
    public static final double DRONE_MOVE_DISTANCE_MEDIUM = 0.0009;

    /**
     * the distance which is considered "close" when far from AT
     */
    public static final double DRONE_IS_CLOSE_DISTANCE_MEDIUM = 0.0021;


    /**
     * the distance a drone can move in 1 iteration when close to AT
     */
    public static final double DRONE_MOVE_DISTANCE_SMALL = 0.00015;

    /**
     * the distance which is considered "close" when starting close to AT
     */
    public static final double DRONE_IS_CLOSE_DISTANCE_SMALL = 0.00015;

    /**
     * the true distance a drone can move in 1 iteration, to be used for the final path output
     */

    public static final double DRONE_MOVE_DISTANCE = 0.00015;


    /**
     * order data URL
     */
    public static final String ORDER_URL = "https://ilp-rest-2024.azurewebsites.net/orders";

    /**
     * Restaurant data URL
     */
    public static final String RESTAURANT_URL = "https://ilp-rest-2024.azurewebsites.net/restaurants";

    /**
     * no fly zones URL
     */
    public static final String NO_FLY_ZONES_URL = "https://ilp-rest-2024.azurewebsites.net/noFlyZones";

    /**
     * central URL
     */
    public static final String CENTRAL_URL = "https://ilp-rest-2024.azurewebsites.net/centralArea";

    /**
     * the central region name
     */
    public static final String CENTRAL_REGION_NAME = "central";

    /**
     * the location of appleton tower
     */
    public static final LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);



}
