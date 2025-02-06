package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.controller.OrderHandling;
import uk.ac.ed.inf.pizzadronz.controller.PathHandling;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.*;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.*;
import static uk.ac.ed.inf.pizzadronz.controller.PathHandling.isInRegion;
import static uk.ac.ed.inf.pizzadronz.model.GeoJson.GeoJsonStrings.*;
import static uk.ac.ed.inf.pizzadronz.model.PathInfo.AStar.doesPathIntersectWithAnyRegion;
import static uk.ac.ed.inf.pizzadronz.model.PathInfo.AStar.euclideanDistance;


/**
 * Unit tests for the PathHandling class
 */

public class PathHandlingUnitTests {
    private static final PathHandling pathHandling = new PathHandling();
    private JsonNode[] testOrders;
    private final OrderHandling orderHandling = pathHandling.getOrderHandling();
    // for repeated tests, collect paths created for many restaurants
    private static LngLat[][] repeatedPaths20 = new LngLat[20][];
    private static LngLat[][] repeatedPaths50 = new LngLat[50][];
    private static double totalLength = 0;



    public PathHandlingUnitTests() throws IOException {
    }




    /**
     *Test that the path generated is a valid path for a valid order using restaurants from the server
     */
    @Test
    void testRestaurantLocation1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");

        System.out.println("Checking that it produces a valid path");
        assertTrue(validatePath(pathHandling.calcDeliveryPath(inputJson)));
        System.out.println("Checking that it produces a valid GeoJson string for just the path");
        System.out.println(pathHandling.calcDeliveryPathAsGeoJson(inputJson));
        System.out.println("Providing geoJSON with additional features");
        System.out.println(GeoJSONPathsPointsRegionsToString(new LngLat[][]{pathHandling.calcDeliveryPath(inputJson)}, restaurantCoords(), pathHandling.getNoFlyZones().getNoFlyZones()));
    }

    /**
     * Test that the path generated is a valid path for a valid order using restaurants from the server
     */
    @Test
    void testRestaurantLocation2() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-25");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R7: Hot, hotter, the hottest").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R7: All you ever wanted").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");

        System.out.println("Checking that it produces a valid path");
        assertTrue(validatePath(pathHandling.calcDeliveryPath(inputJson)));
        System.out.println("Checking that it produces a valid GeoJson string for just the path");
        System.out.println(pathHandling.calcDeliveryPathAsGeoJson(inputJson));
        System.out.println("Providing geoJSON with additional features");
        System.out.println(GeoJSONPathsPointsRegionsToString(new LngLat[][]{pathHandling.calcDeliveryPath(inputJson)}, restaurantCoords(), pathHandling.getNoFlyZones().getNoFlyZones()));
    }


    /**
     * Test that the path generated is a valid path for a valid order using a generated restaurant, ie. not using restaurants from the server
     */
    @Test
    void testRestaurantLocation3() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/restaurants20.json");
        Restaurant[] loadedRestaurants = objectMapper.readValue(file, Restaurant[].class);
        // overwrite the restaurants from the server with ones hardcoded in a file
        pathHandling.overrideRestaurants(loadedRestaurants);



        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        //22nd is Monday, 25th is Thursday
        inputJson.put("orderDate", "2024-01-25");
        inputJson.put("priceTotalInPence", 3000);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "Pizza 1 from Restaurant 17").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "Pizza 2 from Restaurant 17").put("priceInPence", 1500);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");


        System.out.println("Checking that it produces a valid GeoJson string for just the path");
        System.out.println(pathHandling.calcDeliveryPathAsGeoJson(inputJson));
        System.out.println("Providing geoJSON with additional features");
        System.out.println(GeoJSONPathsPointsRegionsToString(new LngLat[][]{pathHandling.calcDeliveryPath(inputJson)}, restaurantCoords(), pathHandling.getNoFlyZones().getNoFlyZones()));
        System.out.println("Checking that it produces a valid path");
        assertTrue(validatePath(pathHandling.calcDeliveryPath(inputJson)));
    }


    /**
     * Tests the path finding algorithm for the valid orders from the server
     */
    @Test
    void testTestOrders() throws IOException {
        setupTestOrders();
        List<LngLat[]> paths = new ArrayList<>();
        // filters out valid orders
        for (JsonNode testOrder : testOrders) {
            OrderValidationResult orderValidationResult = orderHandling.validateOrder(testOrder);
            if (orderValidationResult.getOrderStatus() == OrderStatus.VALID) {
                paths.add(pathHandling.calcDeliveryPath(testOrder));

            }
        }
        LngLat[][] pathArray = paths.toArray(new LngLat[0][0]);

        // returns the geojson for all the paths and the restaurants to allow visual inspection
        System.out.println(GeoJSONPathsPointsRegionsToString(pathArray, restaurantCoords(), pathHandling.getNoFlyZones().getNoFlyZones()));

        for (LngLat[] path : pathArray) {
            assertTrue(validatePath(path));
        }

    }

    /**
     * Tests the path finding algorithm for the valid orders from the server with random regions added
     */
    @Test
    void testTestOrdersWithRandomRegions() throws IOException {
        testRandomRegions();
        setupTestOrders();
        List<LngLat[]> paths = new ArrayList<>();
        // filters out valid orders
        for (JsonNode testOrder : testOrders) {
            OrderValidationResult orderValidationResult = orderHandling.validateOrder(testOrder);
            if (orderValidationResult.getOrderStatus() == OrderStatus.VALID) {
                 paths.add(pathHandling.calcDeliveryPath(testOrder));

            }
        }
        LngLat[][] pathArray = paths.toArray(new LngLat[0][0]);

        // returns the geojson for all the paths and the restaurants to allow visual inspection
        System.out.println(GeoJSONPathsPointsRegionsToString(pathArray, restaurantCoords(), pathHandling.getNoFlyZones().getNoFlyZones()));

        for (LngLat[] path : pathArray) {
            assertTrue(validatePath(path));
        }

    }

    /**
     * Test that the path generated is a valid path for a valid order using 20 generated restaurant, ie. not using restaurants from the server
     * This will save all the paths generated to geoJSON.txt
     */
    @RepeatedTest(20)
    void testRestaurantLocation4(RepetitionInfo repetitionInfo) throws IOException {
        testRandomRegions();

        int currentRepetition = repetitionInfo.getCurrentRepetition();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/restaurants20.json");
        Restaurant[] loadedRestaurants = objectMapper.readValue(file, Restaurant[].class);
        // overwrite the restaurants from the server with ones hardcoded in a file
        pathHandling.overrideRestaurants(loadedRestaurants);

        ObjectNode inputJson = generateRandomOrder(currentRepetition - 1);


        LngLat[] path = pathHandling.calcDeliveryPath(inputJson);
        repeatedPaths20[currentRepetition - 1] = path;

        if (currentRepetition == 20) {
            NamedRegion[] regions = pathHandling.getNoFlyZones().getNoFlyZones();
            NamedRegion[] regionsAndCentral = new NamedRegion[regions.length + 1];
            System.arraycopy(regions, 0, regionsAndCentral, 0, regions.length);
            regionsAndCentral[regions.length] = pathHandling.getNoFlyZones().getCentral();
            String content = GeoJSONPathsPointsRegionsToString(repeatedPaths20, restaurantCoords(), regionsAndCentral);
            Path filePath = Paths.get("src/test/resources/geoJSON.txt");

            try {
                // Write the content to the file
                Files.write(filePath, content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Total length of all paths: " + totalLength);
        }

        totalLength += path.length;
        assertTrue(validatePath(path));
    }

    /**
     * Test that the path generated is a valid path for a valid order using 50 generated restaurants, ie. not using restaurants from the server
     * This will save all the paths generated to geoJSON.txt
     */
    @DirtiesContext
    @RepeatedTest(50)
    void testRestaurantLocation5(RepetitionInfo repetitionInfo) throws IOException {
        testRandomRegions();

        int currentRepetition = repetitionInfo.getCurrentRepetition();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/restaurants50.json");
        Restaurant[] loadedRestaurants = objectMapper.readValue(file, Restaurant[].class);
        // overwrite the restaurants from the server with ones hardcoded in a file
        pathHandling.overrideRestaurants(loadedRestaurants);


        ObjectNode inputJson = generateRandomOrder(currentRepetition - 1);

        LngLat[] path = pathHandling.calcDeliveryPath(inputJson);
        repeatedPaths50[currentRepetition - 1] = path;

        if (currentRepetition == 50) {
            NamedRegion[] regions = pathHandling.getNoFlyZones().getNoFlyZones();
            NamedRegion[] regionsAndCentral = new NamedRegion[regions.length + 1];
            System.arraycopy(regions, 0, regionsAndCentral, 0, regions.length);
            regionsAndCentral[regions.length] = pathHandling.getNoFlyZones().getCentral();
            String content = GeoJSONPathsPointsRegionsToString(repeatedPaths50, restaurantCoords(), regionsAndCentral);
            Path filePath = Paths.get("src/test/resources/geoJSON.txt");

            try {
                // Write the content to the file
                Files.write(filePath, content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Total length of all paths: " + totalLength);
        }

        totalLength += path.length;
        assertTrue(validatePath(path));
    }

    /**
     * Fetches the orders from the server
     */
    void setupTestOrders() throws IOException {
        System.out.println("Fetching test orders");
        RestTemplate restTemplate = new RestTemplate();
        String url = ORDER_URL;
        // map array of JsonNode objects
        ResponseEntity<JsonNode[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<JsonNode[]>() {
                }
        );
        testOrders = response.getBody();
    }


    /**
     * Helper function to get the restaurant coordinates  to use for creating a geoJSON string to show the location of all the restaurants
     */
    public LngLat[] restaurantCoords() {
        LngLat[] restaurantCoords = new LngLat[orderHandling.getRestaurants().getRestaurants().length];
        for (int i = 0; i < orderHandling.getRestaurants().getRestaurants().length; i++) {
            restaurantCoords[i] = orderHandling.getRestaurants().getRestaurants()[i].getLocation();
        }
        return restaurantCoords;
    }

    /**
     * tests that the path generated is a valid path, for use in tests
     * @param path the path to validate
     *             @return true if the path is valid
     */
    public static boolean validatePath(LngLat[] path) throws IOException {
        //checks that the path finishes close to AT
        if (!(euclideanDistance(path[path.length-1], APPLETON_TOWER) < DRONE_IS_CLOSE_DISTANCE_SMALL)){
            System.out.println("Not close to AT");
            return false; // path does not finish close to AT
        }
        for (int i = 0; i < path.length - 1; i++) {
            // checks that each segment of the path is 0.00015 long
            if (roundToSixDecimalPlaces(euclideanDistance(path[i], path[i + 1])) != 0.00015) {
                System.out.println("Segment " + i + " of length: " + roundToSixDecimalPlaces(euclideanDistance(path[i], path[i + 1])));
                return false; // segment is not the correct length
            }
            // checks that the angle of each segment is on the 16 point compass
            if (!(roundToSixDecimalPlaces(calculateAngleFromNorth(path[i], path[i + 1])) % 22.5 == 0)) {
                System.out.println("Angle: " + calculateAngleFromNorth(path[i], path[i + 1]));
                return false; // angle is not on the 16 point compass
            }

            // checks that once the path has entered the central region it does not leave
            if (isInRegion(path[i], pathHandling.getNoFlyZones().getCentral())) {
                if (!isInRegion(path[i + 1], pathHandling.getNoFlyZones().getCentral())) {
                    System.out.println("Path has left the central region after entering");
                    return false; // path has left the central region after entering
                }
            }

            // checks that none of the points are in a no fly zone
            if (pathHandling.isInAnyRegion(path[i])) {
                System.out.println("Point " + i + " is in a no fly zone");
                return false; // point is in a no fly zone
            }

            // checks that the path does not intersect with a no fly zone
            LngLat[] segment = new LngLat[]{path[i], path[i + 1]};
            if (doesPathIntersectWithAnyRegion(segment, pathHandling.getNoFlyZones().getNoFlyZones(), DRONE_MOVE_DISTANCE)) {
                System.out.println("Path intersects with a no fly zone");
                return false; // path intersects with a no fly zone
            }

        }

        return true; // no issues with the path
    }

    /**
     * helper function that calculates the angle of a path segment
     */
    public static double calculateAngleFromNorth(LngLat start, LngLat end) {
        // Find difference vector
        double dlng = end.getLng() - start.getLng();
        double dlat = end.getLat() - start.getLat();

        // Calculate the angle using atan2
        double angleRad = Math.atan2(dlng, dlat);

        // Convert the angle from radians to degrees
        double angleDeg = Math.toDegrees(angleRad);

        // Normalize the angle to the range [0, 360)
        if (angleDeg < 0) {
            angleDeg += 360;
        }

        return angleDeg;
    }

    /**
     * helper function that rounds to 6dp to mimic the autograder
     */
    public static double roundToSixDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(6, RoundingMode.HALF_UP); // Rounds to 6 decimal places
        return bd.doubleValue();
    }


    /**
     * Helper function that creates extra regions for testing and adds them to the no fly zones
     */
    public void testRandomRegions() {
        NamedRegion[] regions = new NamedRegion[6];
        LngLat[] R1 = new LngLat[]{
                new LngLat(-3.198858165002491,
                        55.947602048149975),
                new LngLat(-3.2002589079641837,
                        55.94709452605042),
                new LngLat(-3.199791993643629,
                        55.946079461895835),
                new LngLat(-3.1987483028094346,
                        55.94604870196062),
                new LngLat(-3.198665906164649,
                        55.94669465546883),
                new LngLat(-3.1968257144306165,
                        55.946986868992354),
                new LngLat(-3.1968531799788877,
                        55.94798653016059),
                new LngLat(-3.198858165002491,55.947602048149975)
        };
        NamedRegion region1 = new NamedRegion("Region1", R1);


        LngLat[] R2 = new LngLat[]{
                new LngLat(-3.1939419666905167,
                        55.94795462671394),
                new LngLat(-3.1942166421064826,
                        55.94710100882912),
                new LngLat(-3.1937222263576643,
                        55.94639349145575),
                new LngLat(-3.194326512272937,
                        55.94571672361121),
                new LngLat(-3.194971999500524,
                        55.945555220445755),
                new LngLat(-3.1947522591677284,
                        55.94511685131579),
                new LngLat(-3.193063005359221,
                        55.94505532542678),
                new LngLat(-3.1919917712367294,
                        55.945524457861694),
                new LngLat(-3.1917857646747336,
                        55.94657806245158),
                new LngLat(-3.1910166735098926,
                        55.94721636369698),
                new LngLat(-3.192348849277579,
                        55.94786234460656),
                new LngLat(-3.1934612847124697,
                        55.94803921845252),
                new LngLat(-3.1939419666905167,
                        55.94795462671394)
        };

        NamedRegion region2 = new NamedRegion("Region2", R2);



        LngLat[] R3 = new LngLat[]{
                new LngLat(-3.1860871750399156, 55.9405013889772),
                new LngLat(-3.1842865077794897,55.94025176687947),
                new LngLat(-3.182771094738598,
                        55.94043149495201),
                new LngLat(-3.1827354379611847,55.94121030696266),
                new LngLat(-3.184215194224663,
                        55.9416695990127),
                new LngLat(-3.1886009778486084,
                        55.94202904116554),
                new LngLat(-3.1910612954915507,55.94229862059129),
                new LngLat(-3.1904907875740207,
                        55.939672637402566),
                new LngLat(-3.1881196118960418,55.93946295043432),
                new LngLat(-3.1860871750399156,55.9405013889772)
        };

        NamedRegion region3 = new NamedRegion("Region3", R3);

        LngLat[] R4 = new LngLat[]{
                new LngLat(-3.2420242956260665, 55.944219230870175),
                new LngLat(-3.2480106712980614, 55.9423567589167),
                new LngLat(-3.2464143044525144, 55.94012167434403),
                new LngLat(-3.2453500598885796, 55.94012167434403),
                new LngLat(-3.2409600510621033, 55.94049419739662),
                new LngLat(-3.2376342867996186, 55.942505759969094),
                new LngLat(-3.234308522537134, 55.944889698847874),
                new LngLat(-3.2388315619341483, 55.94570914394643),
                new LngLat(-3.2420242956260665, 55.944219230870175)
        };

        NamedRegion region4 = new NamedRegion("Region4", R4);

        LngLat[] R5 = new LngLat[]{
                new LngLat(-3.2240651686092576, 55.926708458173664),
                new LngLat(-3.2139548452512656, 55.92529234772272),
                new LngLat(-3.2120924172645005, 55.92655939635273),
                new LngLat(-3.2150190898152005, 55.93028576987993),
                new LngLat(-3.2234000157563116, 55.9304348173678),
                new LngLat(-3.2240651686092576, 55.926708458173664)
        };

        NamedRegion region5 = new NamedRegion("Region5", R5);

        LngLat[] R6 = new LngLat[]{
                new LngLat(-3.2166074797073634, 55.96073983977723),
                new LngLat(-3.209157767759649, 55.96215465470206),
                new LngLat(-3.2088917066184592, 55.96401617449081),
                new LngLat(-3.2126165625927, 55.9659520600874),
                new LngLat(-3.219401121688236, 55.96476075733514),
                new LngLat(-3.2221947636683694, 55.96334603768193),
                new LngLat(-3.222061733097746, 55.9617823399995),
                new LngLat(-3.2166074797073634, 55.96073983977723)
        };

        NamedRegion region6 = new NamedRegion("Region6", R6);


        region1.setMinMaxLngLat();
        region2.setMinMaxLngLat();
        region3.setMinMaxLngLat();
        region4.setMinMaxLngLat();
        region5.setMinMaxLngLat();
        region6.setMinMaxLngLat();


        regions[0] = region1;

        regions[1] = region2;

        regions[2] = region3;

        regions[3] = region4;

        regions[4] = region5;

        regions[5] = region6;

        pathHandling.getNoFlyZones().addNoFlyZones(regions);

    }

    /**
     * Helper function to generate random test restaurant locations in a format that can then be copied into the test
     */
    public static void GenerateTestStarts(PathHandling pathHandling, int numberStarts) throws IOException {
        LngLat[] starts = new LngLat[numberStarts];
        double minLat = 55.8754;
        double maxLat = 55.9949;
        double minLng = -3.3400;
        double maxLng = -3.0137;

        for (int i = 0; i < numberStarts; i++) {
            // Generate a random double in the range
            double randomLng = minLng + (Math.random() * (maxLng - minLng));
            double randomLat = minLat + (Math.random() * (maxLat - minLat));
            LngLat randomLngLat = new LngLat(randomLng, randomLat);
            while (pathHandling.isInAnyRegion(randomLngLat)) {
                randomLng = minLng + (Math.random() * (maxLng - minLng));
                randomLat = minLat + (Math.random() * (maxLat - minLat));
                randomLngLat = new LngLat(randomLng, randomLat);
            }
            starts[i] = randomLngLat;
            System.out.println("starts[" + i + "] = new LngLat(" + starts[i].getLng() + ", " + starts[i].getLat() + ");");
        }

    }


    /**
     * Helper function to generate a random restaurant location in edinburgh for testing
     */
    @Test
    @Disabled("This test does not need to run every time the test suite is run")
    void testGenerateTestStarts() throws IOException {
        GenerateTestStarts(pathHandling, 20);
    }

    /**
    * Helper function to add the generated restaurant starts as LngLat objects
     */
    public static LngLat[] FixedTestStarts(){
        LngLat[] starts = new LngLat[20];
        starts[0] = new LngLat(-3.1633199200112467, 55.93278448846059);
        starts[1] = new LngLat(-3.297009288581975, 55.93945916620977);
        starts[2] = new LngLat(-3.1229521214828546, 55.945268177508545);
        starts[3] = new LngLat(-3.324891280342566, 55.8819156548887);
        starts[4] = new LngLat(-3.0585910273190136, 55.948953156203814);
        starts[5] = new LngLat(-3.300935311680428, 55.95277901764011);
        starts[6] = new LngLat(-3.1373746410887096, 55.952554062521784);
        starts[7] = new LngLat(-3.3198386873387506, 55.985445019980055);
        starts[8] = new LngLat(-3.234663700138486, 55.99284954314888);
        starts[9] = new LngLat(-3.095993089184527, 55.9171258825124);
        starts[10] = new LngLat(-3.2638586984741225, 55.89119334159464);
        starts[11] = new LngLat(-3.2429788592960085, 55.98421334628602);
        starts[12] = new LngLat(-3.1666577962455973, 55.881486909307945);
        starts[13] = new LngLat(-3.066006492400517, 55.951223783076195);
        starts[14] = new LngLat(-3.338946886005619, 55.92926740930884);
        starts[15] = new LngLat(-3.1740044195653123, 55.93569361268947);
        starts[16] = new LngLat(-3.197935559925937, 55.89449013006027);
        starts[17] = new LngLat(-3.052000574555976, 55.91224849028449);
        starts[18] = new LngLat(-3.0395524014448045, 55.97674408807226);
        starts[19] = new LngLat(-3.3274219649498646, 55.95990894807062);
        return starts;
    }


    /**
     * resets the hardcoded restaurants
     */
    public void testRestaurants() throws IOException {
        LngLat[] restaurantLocations = FixedTestStarts();
        ObjectNode[] restaurants = new ObjectNode[restaurantLocations.length];
        for (int i = 0; i < restaurantLocations.length; i++) {
            restaurants[i] = createRestaurant(restaurantLocations[i], i);
            System.out.println(restaurants[i].toPrettyString());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Restaurant[] restaurantArray = new Restaurant[restaurantLocations.length];
        for (int i = 0; i < restaurantLocations.length; i++) {
            restaurantArray[i] = objectMapper.convertValue(restaurants[i], Restaurant.class);
        }

        // Save to file
        File file = new File("restaurants20.json");
        objectMapper.writeValue(file, restaurantArray);
        System.out.println("Restaurants saved to: " + file.getAbsolutePath());

        pathHandling.overrideRestaurants(restaurantArray);
    }

    /**
     * creates a restaurant for testing
     */
    public static ObjectNode createRestaurant(LngLat coords, int index) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode inputJson = objectMapper.createObjectNode();

        // Add restaurant name
        inputJson.put("name", "Restaurant" + index);

        // Add location coords
        ObjectNode locationInfo = inputJson.putObject("location");
        locationInfo.put("lng", coords.getLng());
        locationInfo.put("lat", coords.getLat());

        // Add opening days
        ArrayNode daysArray = objectMapper.createArrayNode(); // Create an ArrayNode directly
        String[] daysOfWeek = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        // Chooses a random of days a week to be open
        int numberOfDays = 3 + (int) (Math.random() * 4); // Random number between 3 and 6

        List<String> daysList = Arrays.asList(daysOfWeek);
        Collections.shuffle(daysList);

        for (int i = 0; i < numberOfDays; i++) {
            daysArray.add(daysList.get(i));
        }
        inputJson.set("openingDays", daysArray); // Add directly to the main JSON object

        // Add menu
        ArrayNode menuArray = objectMapper.createArrayNode();
        int numberOfPizzas = 2 + (int) (Math.random() * 5); // Random number between 2 and 6, this is the number of pizzas to add to the menu

        for (int i = 1; i <= numberOfPizzas; i++) {
            ObjectNode pizza = objectMapper.createObjectNode();
            pizza.put("name", "Pizza " + i + " from Restaurant " + index);
            pizza.put("priceInPence", 700 + (int) (Math.random() * 14) * 100); // Random price between 700 and 2000
            menuArray.add(pizza);
        }
        inputJson.set("menu", menuArray); // Add menu array to the main JSON object


        return inputJson;
    }

    /**
     * given a restaurant, this will create an order for it to use in testing
     */
    public ObjectNode generateRandomOrder(int index) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");

        //Convert the order date string to a LocalDate object
        String[] orderDateParts = "1976-01-22".split("-");
        int orderYear = Integer.parseInt(orderDateParts[0]);
        int orderMonth = Integer.parseInt(orderDateParts[1]);
        int orderDay = Integer.parseInt(orderDateParts[2]);
        LocalDate orderDate = LocalDate.of(orderYear, orderMonth, orderDay);
        //check what day of the week the order was placed on
        DayOfWeek orderDayOfWeek = orderDate.getDayOfWeek();
        //get the days of the week that the restaurant is open
        // check that the restaurant is open
        while (!restaurantIsOpen(orderHandling.getRestaurants().getRestaurants()[index], orderDate.getDayOfWeek())){
            orderDate = orderDate.plusDays(1);
        }

        inputJson.put("orderDate", orderDate.toString());

        Pizza[] pizzas = orderHandling.getRestaurants().getRestaurants()[index].getMenu();

        Random random = new Random();

        // Pick the first random pizza
        int index1 = random.nextInt(pizzas.length);

        // Pick the second random index, ensuring it's distinct from index1
        int index2;
        do {
            index2 = random.nextInt(pizzas.length);
        } while (index1 == index2);

        Pizza pizza1 = pizzas[index1];
        Pizza pizza2 = pizzas[index2];

        // put several hardcoded values that should be guaranteed to be correct
        inputJson.put("priceTotalInPence", pizza1.getPriceInPence() + pizza2.getPriceInPence() + ORDER_CHARGE_IN_PENCE);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", pizza1.getName()).put("priceInPence", pizza1.getPriceInPence());
        pizzasInOrder.addObject().put("name", pizza2.getName()).put("priceInPence", pizza2.getPriceInPence());

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "10/78");
        creditCardInformation.put("cvv", "816");

        return inputJson;
    }

    /**
     * checks if the restaurant is open on the given day of the week
     */
    public static boolean restaurantIsOpen(Restaurant restaurant, DayOfWeek orderDayOfWeek) {
        if (orderDayOfWeek == DayOfWeek.MONDAY && !restaurant.isOpen("MONDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.TUESDAY && !restaurant.isOpen("TUESDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.WEDNESDAY && !restaurant.isOpen("WEDNESDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.THURSDAY && !restaurant.isOpen("THURSDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.FRIDAY && !restaurant.isOpen("FRIDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.SATURDAY && !restaurant.isOpen("SATURDAY")){
            return false;
        }
        if (orderDayOfWeek == DayOfWeek.SUNDAY && !restaurant.isOpen("SUNDAY")){
            return false;
        }
        return true;
    }
}
