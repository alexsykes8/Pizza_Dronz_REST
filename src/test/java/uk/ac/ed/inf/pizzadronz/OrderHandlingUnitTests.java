package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;
import uk.ac.ed.inf.pizzadronz.controller.OrderHandling;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the OrderHandling class
 */
public class OrderHandlingUnitTests {
    private final OrderHandling orderHandling = new OrderHandling();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Order[] testOrders;

    /**
     * Fetches test orders from the server
      */
    @BeforeEach
    void setup() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String url = SystemConstants.ORDER_URL;
        // map array of JsonNode objects
        ResponseEntity<Order[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Order[]>() {
                }
        );
        Order[] jsonNodes = response.getBody();

        this.testOrders = jsonNodes;
    }

    void setTestOrders(Order[] testOrders) {
        this.testOrders = testOrders;
    }

    /**
     * test correctly converts JSON to Order object
     */
    @Test
    void testExtractOrder() {
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
        creditCardInformation.put("creditCardExpiry", "10/25");
        creditCardInformation.put("cvv", "816");

        Pizza expectedPizza1 = new Pizza("R6: Sucuk delight", 1400);
        Pizza expectedPizza2 = new Pizza("R6: Dreams of Syria", 900);
        Pizza[] expectedPizzas = {expectedPizza1, expectedPizza2};
        Order expectedOrder = new Order("092884D0", "2024-01-28", OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, 2400, expectedPizzas, new CreditCardInformation("4485959141852684", "10/25", "816"));

        Boolean result = orderHandling.extractOrder(inputJson).equals(expectedOrder);

        System.out.println("Expected Order: " + expectedOrder);
        System.out.println("Actual Order: " + orderHandling.extractOrder(inputJson));

        assertEquals(true, result);
    }

    /**
     * test price for pizza validation code
     */
    @Test
    void testValidateOrderPriceForPizzaInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.PRICE_FOR_PIZZA_INVALID)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                else {
                    System.out.println("Failed test with expected: " + expectedOrderValidationCode + " and actual: " + actualOrderValidationCode);
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test price for invalid cost validation code
     */
    @Test
    void testValidateOrderTotalCostInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.TOTAL_INCORRECT)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                else {
                    System.out.println("Failed test with expected: " + expectedOrderValidationCode + " and actual: " + actualOrderValidationCode);
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test invalid CVV validation code
     */
    @Test
    void testValidateOrderCVVInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.CVV_INVALID)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder,orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                else {
                    System.out.println("Failed test with expected: " + expectedOrderValidationCode + " and actual: " + actualOrderValidationCode);
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test invalid card number validation code
     */
    @Test
    void testValidateOrderCardNumberInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.CARD_NUMBER_INVALID)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test invalid expiry date validation code
     */
    @Test
    void testValidateOrderExpiryDateInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.EXPIRY_DATE_INVALID)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }


    /**
     * test pizza not defined validation code
     */
    @Test
    void testValidateOrderPizzaNotDefined() {
        boolean result = true;
        for (Order testOrder : testOrders) {

            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.PIZZA_NOT_DEFINED)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test different restaurant validation code
     */
    @Test
    void testValidateOrderDifferentRestaurants() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test empty order validation code
     */
    @Test
    void testValidateOrderEmptyOrder() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.EMPTY_ORDER)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test restaurant closed validation code
     */
    @Test
    void testValidateOrderRestaurantClosed() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderValidationCode().equals(OrderValidationCode.RESTAURANT_CLOSED)) {
                System.out.println("Testing: " + testOrder);
                OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
                OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
                if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test all validation codes
     */
    @Test
    void testValidateOrder() {
        boolean result = true;

        for (Order testOrder : testOrders) {

            System.out.println("Testing: " + testOrder);
            OrderValidationCode expectedOrderValidationCode = testOrder.getOrderValidationCode();
            OrderValidationCode actualOrderValidationCode = orderHandling.getOrderValidationCode(testOrder, orderHandling.getRestaurants());
            if (expectedOrderValidationCode.equals(actualOrderValidationCode)) {
                System.out.println("Passed test");
            }
            result = result && expectedOrderValidationCode.equals(actualOrderValidationCode);
        }
        assertTrue(result);
    }

    /**
     * test valid status code
     */
    @Test
    void testGetStatusCodeValid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderStatus().equals(OrderStatus.VALID)) {
                System.out.println("Testing: " + testOrder);
                OrderStatus expectedStatusCode = testOrder.getOrderStatus();
                OrderStatus actualStatusCode = orderHandling.getOrderStatus(testOrder);
                if (expectedStatusCode.equals(actualStatusCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedStatusCode.equals(actualStatusCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test invalid status code
     */
    @Test
    void testGetStatusCodeInvalid() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderStatus().equals(OrderStatus.INVALID)) {
                System.out.println("Testing: " + testOrder);
                OrderStatus expectedStatusCode = testOrder.getOrderStatus();
                OrderStatus actualStatusCode = orderHandling.getOrderStatus(testOrder);
                if (expectedStatusCode.equals(actualStatusCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedStatusCode.equals(actualStatusCode);

            }
        }
        assertTrue(result);
    }

    /**
     *  test undefined status code
     */
    @Test
    void testGetStatusCodeUndefined() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            if (testOrder.getOrderStatus().equals(OrderStatus.UNDEFINED)) {
                System.out.println("Testing: " + testOrder);
                OrderStatus expectedStatusCode = testOrder.getOrderStatus();
                OrderStatus actualStatusCode = orderHandling.getOrderStatus(testOrder);
                if (expectedStatusCode.equals(actualStatusCode)) {
                    System.out.println("Passed test");
                }
                result = result && expectedStatusCode.equals(actualStatusCode);

            }
        }
        assertTrue(result);
    }

    /**
     * test all status codes
     */
    @Test
    void testGetStatusCode() {
        boolean result = true;
        for (Order testOrder : testOrders) {
            System.out.println("Testing: " + testOrder);
            OrderStatus expectedStatusCode = testOrder.getOrderStatus();
            OrderStatus actualStatusCode = orderHandling.getOrderStatus(testOrder);
            if (expectedStatusCode.equals(actualStatusCode)) {
                System.out.println("Passed test");
            }
            result = result && expectedStatusCode.equals(actualStatusCode);
        }
        assertTrue(result);
    }

    /**
     * test primary validation method for both status and validation code
     */
    @Test
    void validateOrderTest() {
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
        for (JsonNode testOrder : jsonNodes) {
            String expectedOrderValidationCode = testOrder.get("orderValidationCode").asText();
            String expectedOrderStatus = testOrder.get("orderStatus").asText();
            OrderValidationCode expectedOrderValidationCodeEnum = OrderValidationCode.valueOf(expectedOrderValidationCode);
            OrderStatus expectedOrderStatusEnum = OrderStatus.valueOf(expectedOrderStatus);
            OrderValidationResult orderValidationResult = orderHandling.validateOrder(testOrder);
            assertEquals(expectedOrderValidationCodeEnum, orderValidationResult.getOrderValidationCode(),
                    "The orderValidationCode in the response does not match the expected value.");
            assertEquals(expectedOrderStatusEnum, orderValidationResult.getOrderStatus(),
                    "The orderStatus in the response does not match the expected value.");
        }
    }
}
