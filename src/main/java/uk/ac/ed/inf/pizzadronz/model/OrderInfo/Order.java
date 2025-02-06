package uk.ac.ed.inf.pizzadronz.model.OrderInfo;

import uk.ac.ed.inf.pizzadronz.constant.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constant.OrderValidationCode;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.Restaurant;


import java.util.Objects;
import java.util.Arrays;

/**
 * Represents order info.
 */
public class Order {

    private String orderNo;
    private String orderDate;
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;
    private Integer priceTotalInPence;
    private Pizza[] pizzasInOrder;
    private CreditCardInformation creditCardInformation;
    private Restaurant restaurant;

    public Order() {
    }

    public Order(String orderNo, String orderDate, OrderStatus orderStatus, OrderValidationCode orderValidationCode, Integer priceTotalInPence, Pizza[] pizzasInOrder, CreditCardInformation creditCardInformation) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderValidationCode = orderValidationCode;
        this.priceTotalInPence = priceTotalInPence;
        this.pizzasInOrder = pizzasInOrder;
        this.creditCardInformation = creditCardInformation;
    }

    public CreditCardInformation getCreditCardInformation() {
        return creditCardInformation;
    }


    public Pizza[] getPizzasInOrder() {
        return pizzasInOrder;
    }


    public Integer getPriceTotalInPence() {
        return priceTotalInPence;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }


    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;

        return Objects.equals(priceTotalInPence, order.priceTotalInPence) &&
                Objects.equals(orderNo, order.orderNo) &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(orderStatus, order.orderStatus) &&
                Objects.equals(orderValidationCode, order.orderValidationCode) &&
                Arrays.equals(pizzasInOrder, order.pizzasInOrder) &&
                Objects.equals(creditCardInformation, order.creditCardInformation);
    }



    @Override
    public String toString() {
        return "Order{" +
                "orderNo='" + orderNo + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderValidationCode=" + orderValidationCode +
                ", priceTotalInPence=" + priceTotalInPence +
                ", pizzasInOrder=" + Arrays.toString(pizzasInOrder) +
                ", creditCardInformation=" + creditCardInformation +
                '}';
    }


}
