package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.controller.OrderHandling;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.Order;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ed.inf.pizzadronz.PathHandlingUnitTests.validatePath;
import static uk.ac.ed.inf.pizzadronz.controller.OrderHandling.extractOrder;


@SpringBootTest
@AutoConfigureMockMvc

/**
 * Tests the PathHandling controller using MockMvc to check response codes
 */

public class PathHandlingMockMvcTests {

    private static JsonNode[] testOrders;
    private static OrderHandling orderHandling = new OrderHandling();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * set up the test orders
     */

    @BeforeAll
    static void setUp() {
        System.out.println("Fetching test orders");
        RestTemplate restTemplate = new RestTemplate();
        String url = SystemConstants.ORDER_URL;
        // map array of JsonNode objects
        ResponseEntity<JsonNode[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<JsonNode[]>() {
                }
        );
        JsonNode[] jsonNodes = response.getBody();

        testOrders = jsonNodes;
    }

    /**
     * Test the path handling controller with a random selection of invalid orders to ensure they return the correct status code of 400
     * @throws Exception
     */

    @RepeatedTest(5)
    void testPathHandlingInvalidOrders() throws Exception {
        OrderStatus orderStatus = OrderStatus.VALID;

        // selects a random invalid order from the test orders
        int randomIndex = 0;
        while (orderStatus != OrderStatus.INVALID) {
            randomIndex = (int) (Math.random() * testOrders.length);
            orderStatus = orderHandling.validateOrder(testOrders[randomIndex]).getOrderStatus();
        }

        // checks that the return is 400
        JsonNode testOrder = testOrders[randomIndex];
        MvcResult result = (MvcResult) mockMvc.perform(post("/calcDeliveryPath")
                        .contentType("application/json")
                        .content(testOrder.toPrettyString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test the path handling controller with a random selection of valid orders to ensure they return the correct status code of 200. Also validatest the returned LngLat array
     * @throws Exception
     */
    @RepeatedTest(5)
    void testPathHandlingValidOrders() throws Exception {
        // selects a random valid order from the test orders
        OrderStatus orderStatus = OrderStatus.INVALID;
        int randomIndex = 0;
        while (orderStatus == OrderStatus.INVALID) {
            randomIndex = (int) (Math.random() * testOrders.length);
            orderStatus = orderHandling.validateOrder(testOrders[randomIndex]).getOrderStatus();
        }
        JsonNode testOrder = testOrders[randomIndex];

        // checks that the return is 200
        MvcResult result = (MvcResult) mockMvc.perform(post("/calcDeliveryPath")
                        .contentType("application/json")
                        .content(testOrder.toPrettyString()))
                .andExpect(status().isOk())
                .andReturn();

        // validatest the returned LngLat array
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        LngLat[] path = objectMapper.readValue(jsonResponse, LngLat[].class);

        assertTrue(validatePath(path));
    }

    /**
     * Test the path handling controller with a random selection of invalid orders to ensure they return the correct status code of 400
     * @throws Exception
     */

    @RepeatedTest(5)
    void testPathHandlingGeoJSONInvalidOrders() throws Exception {
        OrderStatus orderStatus = OrderStatus.VALID;

        // selects a random invalid order from the test orders
        int randomIndex = 0;
        while (orderStatus != OrderStatus.INVALID) {
            randomIndex = (int) (Math.random() * testOrders.length);
            orderStatus = orderHandling.validateOrder(testOrders[randomIndex]).getOrderStatus();
        }

        // checks that the return is 400
        JsonNode testOrder = testOrders[randomIndex];
        MvcResult result = (MvcResult) mockMvc.perform(post("/calcDeliveryPathAsGeoJson")
                        .contentType("application/json")
                        .content(testOrder.toPrettyString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * Test the path handling controller with a random selection of valid orders to ensure they return the correct status code of 200. Also validates the returned LngLat array
     * @throws Exception
     */
    @RepeatedTest(5)
    void testPathHandlingGeoJSONValidOrders() throws Exception {
        OrderStatus orderStatus = OrderStatus.INVALID;
        // selects a random valid order from the test orders
        int randomIndex = 0;
        while (orderStatus == OrderStatus.INVALID) {
            randomIndex = (int) (Math.random() * testOrders.length);
            orderStatus = orderHandling.validateOrder(testOrders[randomIndex]).getOrderStatus();
        }
        JsonNode testOrder = testOrders[randomIndex];
        MvcResult result = (MvcResult) mockMvc.perform(post("/calcDeliveryPathAsGeoJson")
                        .contentType("application/json")
                        .content(testOrder.toPrettyString()))
                .andExpect(status().isOk())
                .andReturn();

        // Get the JSON response as a string
        String jsonResponse = result.getResponse().getContentAsString();

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonResponse);

        // Navigate to features[0].geometry.coordinates
        JsonNode coordinatesNode = root.path("features").get(0).path("geometry").path("coordinates");

        // Map the coordinates to an array of LngLat objects
        // Convert the coordinates array to LngLat[]
        LngLat[] coords = new LngLat[coordinatesNode.size()];
        for (int i = 0; i < coordinatesNode.size(); i++) {
            JsonNode point = coordinatesNode.get(i);
            double lng = point.get(0).asDouble();
            double lat = point.get(1).asDouble();
            coords[i] = new LngLat(lng, lat);
        }

        // Validate the path
        assertTrue(validatePath(coords));

        // return the geoJSON string to check manually
        Order order = extractOrder(testOrder);
        System.out.println("Travelling from restaurant: " + orderHandling.findOrderRestaurant(order.getPizzasInOrder()[0]).toString());
        System.out.println(result.getResponse().getContentAsString());
    }



}
