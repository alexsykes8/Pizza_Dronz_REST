package uk.ac.ed.inf.pizzadronz.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.model.OrderInfo.*;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.Restaurant;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.Restaurants;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.MAX_PIZZAS_PER_ORDER;
import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.ORDER_CHARGE_IN_PENCE;

@RestController

/**
 * Deals with the order.
 *
 * <p>This class provides methods to validate the order.</p>

 */
public class OrderHandling {
    Restaurants restaurants = new Restaurants();

    /**
     * validate the order
     * @param JsonOrder the order to be validated
     * @return OrderValidationResult of the validation code and status
     */
    @PostMapping("/validateOrder")
    public OrderValidationResult validateOrder(@RequestBody JsonNode JsonOrder) {
        // Convert to order object from json input
        Order order = extractOrder(JsonOrder);

        // Get the validation code for the order by inspecting contents
        OrderValidationCode orderValidationCode = getOrderValidationCode(order, restaurants);
        order.setOrderValidationCode(orderValidationCode);

        //Choose the appropriate status based on the validation code
        OrderStatus orderStatus = getOrderStatus(order);

        return new OrderValidationResult(orderStatus, orderValidationCode);
    }

    /**
     * extract the order from the json input
     * @param JsonOrder the order to be extracted
     * @return the input as an order object
     */
    public static Order extractOrder(JsonNode JsonOrder) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(JsonOrder, Order.class);
    }

    /**
     * get the order status based on the validation code
     * @param order the order to be checked
     * @return the status of the order
     */
    public OrderStatus getOrderStatus(Order order) {
        if (order.getOrderValidationCode() == OrderValidationCode.NO_ERROR) {
            return OrderStatus.VALID;
        }
        if (order.getOrderValidationCode() == OrderValidationCode.UNDEFINED) {
            return OrderStatus.UNDEFINED;
        }
        else {
            return OrderStatus.INVALID;
        }
    }

    /**
     * get the restaurants
     * @return the restaurants
     */
    public Restaurants getRestaurants() {
        return restaurants;
    }

    /**
     *  get the validation code for the order
     * @param order the order to be validated
     * @param restaurants the restaurants the order will be checked against
     * @return the validation code for the order
     */
    public OrderValidationCode getOrderValidationCode(Order order, Restaurants restaurants) {
        // check that the order is not empty
        if (order.getPizzasInOrder().length == 0) {
            return OrderValidationCode.EMPTY_ORDER;
        }
        // check that the credit card number is valid
        if (order.getCreditCardInformation().getCreditCardNumber() == null || !(order.getCreditCardInformation().getCreditCardNumber().matches("\\d{16}")) || order.getCreditCardInformation().getCreditCardNumber().length() != 16) {
            return OrderValidationCode.CARD_NUMBER_INVALID;
        }

        // check that the expiry date is valid
        String expiryDate = order.getCreditCardInformation().getCreditCardExpiry();
        if (expiryDate == null || !(expiryDate.matches("\\d{2}/\\d{2}")|| expiryDate.matches("\\d{1}/\\d{2}") || expiryDate.matches("\\d{2}/\\d{1}"))) {
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }

        //Convert the order date string to a LocalDate object
        String[] orderDateParts = order.getOrderDate().split("-");
        int orderYear = Integer.parseInt(orderDateParts[0]);
        int orderMonth = Integer.parseInt(orderDateParts[1]);
        int orderDay = Integer.parseInt(orderDateParts[2]);
        LocalDate orderDate = LocalDate.of(orderYear, orderMonth, orderDay);

        //Convert the expiry date string to a LocalDate object and check if it is valid
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        //if date is not possible because it is not a valid month or year
        if (month < 1 || month > 12 || year < 0 || year > 99){
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }

        LocalDate expiryDateLocal;
        // the year is in the 21st century
        expiryDateLocal = LocalDate.of(year + 2000, month, 1);


        expiryDateLocal = expiryDateLocal.with(TemporalAdjusters.lastDayOfMonth());
        // if the card expired before the order was placed then it is invalid
        if (expiryDateLocal.isBefore(orderDate)) {
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }


        // check that the CVV is valid
        if (order.getCreditCardInformation().getCvv() == null || !(order.getCreditCardInformation().getCvv().matches("\\d{3}")) || order.getCreditCardInformation().getCvv().length() != 3) {
            return OrderValidationCode.CVV_INVALID;
        }

        boolean PizzaDefined;

        //check that pizzas are defined
        for (Pizza pizza : order.getPizzasInOrder()) {
            PizzaDefined = false;
            // Search through the restaurants to find the pizza
            for (Restaurant restaurant : restaurants.getRestaurants()) {
                // Search through the menu of the restaurant to see if the pizza exists
                for (Pizza pizza1 : restaurant.getMenu()) {
                    if (pizza1.getName().equals(pizza.getName())) {
                        PizzaDefined = true;
                        break;
                    }
                }
            }
            if (!PizzaDefined) {
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }
        }


        // check that the number of pizzas is not too high
        if (order.getPizzasInOrder().length > MAX_PIZZAS_PER_ORDER) {
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }

        // check for pizzas being ordered from different restaurants
        String[] restaurantName = {};

        //for each pizza, check where the pizza came from and compare to past pizzas
        for (Pizza pizza : order.getPizzasInOrder()) {
            for (Restaurant restaurant : restaurants.getRestaurants()) {
                for (Pizza pizza1 : restaurant.getMenu()) {
                    if (pizza1.getName().equals(pizza.getName())) {
                        // Create a new array with one additional element
                        String[] newArray = new String[restaurantName.length + 1];
                        System.arraycopy(restaurantName, 0, newArray, 0, restaurantName.length);
                        newArray[newArray.length - 1] = restaurant.getName();
                        restaurantName = newArray;
                        break;
                    }
                }
            }
        }
        //compares each restaurant to see if they are all the same
        if (!allRestaurantsSame(restaurantName)) {
            return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
        }

        // check for the restaurant being closed
        //check what day of the week the order was placed on
        DayOfWeek orderDayOfWeek = orderDate.getDayOfWeek();
        //get the days of the week that the restaurant is open
        int restaurantIndex = 0;
        for (int i = 0; i < restaurants.getRestaurants().length; i++) {
            if (restaurants.getRestaurants()[i].getName().equals(restaurantName[0])) {
                restaurantIndex = i; // Return the index if the name matches
            }
        }
        // check that the restaurant is open
        if (orderDayOfWeek == DayOfWeek.MONDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("MONDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.TUESDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("TUESDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.WEDNESDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("WEDNESDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.THURSDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("THURSDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.FRIDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("FRIDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.SATURDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("SATURDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (orderDayOfWeek == DayOfWeek.SUNDAY && !restaurants.getRestaurants()[restaurantIndex].isOpen("SUNDAY"))
        {
            return OrderValidationCode.RESTAURANT_CLOSED;
        }

        // check that the total price is correct
        // add cost of each pizza and add order charge
        int correctTotal = 0;
        for (Pizza pizza : order.getPizzasInOrder()) {
            if (pizza.getPriceInPence() == null) {
                return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
            }
            correctTotal = correctTotal + pizza.getPriceInPence();
        }
        correctTotal = correctTotal + ORDER_CHARGE_IN_PENCE;

        // check that the order total is correct
        if (order.getPriceTotalInPence() == null || correctTotal != order.getPriceTotalInPence()) {
            return OrderValidationCode.TOTAL_INCORRECT;
        }


        // check that the price for each pizza is correct
        for (Pizza pizza : order.getPizzasInOrder()) {
            int actualCost = restaurants.getRestaurants()[restaurantIndex].getPizzaPrice(pizza.getName());
            if (actualCost != pizza.getPriceInPence()) {
                return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
            }
        }


        return OrderValidationCode.NO_ERROR;
    }


    /**
     * checks all restaurant names in an array are the same
     * @param array the array of restaurants to be checked
     * @return true if all the restaurants are the same, false otherwise
     */
    private static boolean allRestaurantsSame(String[] array) {
        if (array == null || array.length == 0) {
            return true;
        }

        String firstElement = array[0];

        for (String str : array) {
            if (!str.equals(firstElement)) {
                return false;
            }
        }

        return true;
    }

    /**
     * find the restaurant that the order is from
     * @param pizza the pizza to be checked
     * @return the restaurant that the pizza is from
     */
    public Restaurant findOrderRestaurant(Pizza pizza){
            for (Restaurant restaurant : restaurants.getRestaurants()) {
                for (Pizza pizza1 : restaurant.getMenu()) {
                    if (pizza1.getName().equals(pizza.getName())) {
                        // set the restaurant for the pizza. If it passes the check for multiple restaurants then it can be used later to identify the restaurant of the delivery
                        return restaurant;
                    }
                }
            }
        return null;
    }

    /**
     * override the restaurants with a new set of restaurants for the sake of testing
     * @param restaurantArray the new restaurants to be used
     */
    public void overrideRestaurants(Restaurant[] restaurantArray) {
        restaurants.overrideRestaurants(restaurantArray);
    }
}
