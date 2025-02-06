package uk.ac.ed.inf.pizzadronz.model.OrderInfo;

import java.util.Objects;

/**
 * Represents pizza items.
 */
public class Pizza {
    private String name;
    private Integer priceInPence;

    public Pizza() {
    }

    public Pizza(String name, Integer priceInPence) {
        this.name = name;
        this.priceInPence = priceInPence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriceInPence() {
        return priceInPence;
    }

    public void setPriceInPence(Integer priceInPence) {
        this.priceInPence = priceInPence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pizza pizza = (Pizza) o;
        return Objects.equals(priceInPence, pizza.priceInPence) &&
                Objects.equals(name, pizza.name);
    }



    @Override
    public String toString() {
        return "Pizza{" +
                "name='" + name + '\'' +
                ", priceInPence=" + priceInPence +
                '}';
    }

}
