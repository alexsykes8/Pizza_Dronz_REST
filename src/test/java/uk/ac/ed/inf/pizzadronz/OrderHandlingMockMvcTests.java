package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.OrderValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc

/**
 * Tests the OrderHandlingController using MockMvc to check response codes
 */

class OrderHandlingMockMvcTests {

    private JsonNode[] testOrders;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fetches test orders from the server
     */
    @BeforeEach
    void setUp() {
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

        this.testOrders = jsonNodes;
    }

    @Test
    void testNoError() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "01/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("NO_ERROR", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("VALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when the card number is incorrect because it contains a letter
     */
    @Test
    void testCardNoInvalidNonDigit() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "A485959141852684");
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CARD_NUMBER_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");

    }

    /**
     * test for when the card number is incorrect because it is not 16 digits
     */
    @Test
    void testCardNoInvalidWrongLength() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "141852684");
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CARD_NUMBER_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");

    }

    /**
     * test for when there is an expiry date problem
     */
    @Test
    void testInvalidExpiry() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2025-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "2738461839480184");
        creditCardInformation.put("creditCardExpiry", "12/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("EXPIRY_DATE_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");

    }


    /**
     * test for when there is an expiry date problem because the month is not possible
     */
    @Test
    void testInvalidExpiryBadMonth() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2025-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "2738461839480184");
        creditCardInformation.put("creditCardExpiry", "13/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("EXPIRY_DATE_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");

    }

    /**
     * test for when CVC is wrong because of a nondigit character
     */
    @Test
    void testInvalidCVVletter() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "8i6");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CVV_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when CVC is wrong because of the length
     */
    @Test
    void testInvalidCVVLength() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "8738");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CVV_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when order total is incorrect
     */
    @Test
    void testTotalIncorrect() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2300);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("TOTAL_INCORRECT", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when a pizza in the order is undefined
     */
    @Test
    void testPizzaUndefined() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Sweet dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("PIZZA_NOT_DEFINED", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when too many pizzas ordered
     */
    @Test
    void testInvalidPizzaCount() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 5100);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("MAX_PIZZA_COUNT_EXCEEDED", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when pizzas were ordered from multiple restaurants
     */
    @Test
    void testMultipleRestaurants() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);
        pizzasInOrder.addObject().put("name", "R7: Hot, hotter, the hottest").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("PIZZA_FROM_MULTIPLE_RESTAURANTS", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when the restaurant is closed on the order day
     */
    @Test
    void testRestaurantClosed() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2025-01-16");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/26");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("RESTAURANT_CLOSED", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when the price for a pizza is invalid because the price has a letter
     */
    @Test
    void testPizzaPriceIncorrectPrice() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 1100);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 100);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("PRICE_FOR_PIZZA_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test for when the price for a pizza is invalid because the price is missing
     */
    @Test
    void testPizzaPriceEmptyField() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 1100);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight");
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("PRICE_FOR_PIZZA_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }


    /**
     * test for when the order contains no pizzas
     */
    @Test
    void testEmptyOrder() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("EMPTY_ORDER", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }


    @Test
    void testEmptyFieldCardNumber() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CARD_NUMBER_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }


    @Test
    void testEmptyFieldExpiryDate() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("EXPIRY_DATE_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }


    @Test
    void testEmptyFieldCVV() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("CVV_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }



    @Test
    void testEmptyFieldOrderTotal() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight").put("priceInPence", 1400);
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("TOTAL_INCORRECT", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }


    @Test
    void testEmptyFieldPrice() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("orderNo", "092884D0");
        inputJson.put("orderDate", "2024-01-28");
        inputJson.put("orderStatus", "UNDEFINED");
        inputJson.put("orderValidationCode", "UNDEFINED");
        inputJson.put("priceTotalInPence", 2400);

        ArrayNode pizzasInOrder = inputJson.putArray("pizzasInOrder");
        pizzasInOrder.addObject().put("name", "R6: Sucuk delight");
        pizzasInOrder.addObject().put("name", "R6: Dreams of Syria").put("priceInPence", 900);

        ObjectNode creditCardInformation = inputJson.putObject("creditCardInformation");
        creditCardInformation.put("creditCardNumber", "4485959141852684");
        creditCardInformation.put("creditCardExpiry", "1/24");
        creditCardInformation.put("cvv", "816");
        MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
        assertEquals("PRICE_FOR_PIZZA_INVALID", orderValidationResult.getOrderValidationCode().toString(),
                "The orderValidationCode in the response does not match the expected value.");
        assertEquals("INVALID", orderValidationResult.getOrderStatus().toString(),
                "The orderStatus in the response does not match the expected value.");
    }

    /**
     * test the POST method validateOrder. Note that this test takes a while to complete as it tests all the test orders.
     * for faster testing of the testValidateOrder, use the UnitTests instead, and only use this as an occasional service test
     */
    @Test
    void testValidateOrder() throws Exception {
        for (JsonNode testOrder : testOrders) {
            String jsonRequest = testOrder.toPrettyString();
            String expectedOrderValidationCode = testOrder.get("orderValidationCode").asText();
            String expectedOrderStatus = testOrder.get("orderStatus").asText();
            MvcResult result = (MvcResult) mockMvc.perform(post("/validateOrder")
                            .contentType("application/json")
                            .content(jsonRequest))
                    .andExpect(status().isOk())
                    .andReturn();
            String responseContent = result.getResponse().getContentAsString();
            OrderValidationResult orderValidationResult = objectMapper.readValue(responseContent, OrderValidationResult.class);
            assertEquals(expectedOrderValidationCode, orderValidationResult.getOrderValidationCode().toString(),
                    "The orderValidationCode in the response does not match the expected value.");
            assertEquals(expectedOrderStatus, orderValidationResult.getOrderStatus().toString(),
                    "The orderStatus in the response does not match the expected value.");
        }
    }
}



