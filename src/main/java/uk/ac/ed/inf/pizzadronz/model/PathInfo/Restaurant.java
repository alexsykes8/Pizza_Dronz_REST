package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import uk.ac.ed.inf.pizzadronz.model.OrderInfo.Pizza;

import java.util.Arrays;

/**
 * Represents a restaurant.
 */
public class Restaurant {
    String name;
    LngLat location;
    String[] openingDays;
    Pizza[] menu;

    public Restaurant() {
    }

    public Restaurant(String name, LngLat location, String[] openingDays, Pizza[] menu) {
        this.name = name;
        this.location = location;
        this.openingDays = openingDays;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLat getLocation() {
        return location;
    }

    public void setLocation(LngLat location) {
        this.location = location;
    }

    public String[] getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(String[] openingDays) {
        this.openingDays = openingDays;
    }

    public Pizza[] getMenu() {
        return menu;
    }

    public void setMenu(Pizza[] menu) {
        this.menu = menu;
    }

    /**
     *     Returns true if the restaurant is open on the given day
     *     @param day the day to check
     *                @return true if the restaurant is open on the given day
      */
    public boolean isOpen(String day) {
        for (String openingDay : openingDays) {
            if (openingDay.equals(day)) {
                return true;
            }
        }
        return false;
    }

    // Returns the price of the pizza with the given name
    public int getPizzaPrice(String pizzaName) {
        for (Pizza pizza : menu) {
            if (pizza.getName().equals(pizzaName)) {
                return pizza.getPriceInPence();
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Restaurant{name='" + name + "', location=" + location +
                ", openingDays=" + Arrays.toString(openingDays) + ", menu=" + Arrays.toString(menu) + '}';
    }
}


