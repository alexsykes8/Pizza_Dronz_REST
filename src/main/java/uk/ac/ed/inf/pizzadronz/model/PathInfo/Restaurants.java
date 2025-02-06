package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;

import java.util.Arrays;


/**
 * Represents restaurants on the map.
 * <p>Provides methods for retrieving the restaurants from server.</p>
 *
 */

public class Restaurants {
    private Restaurant[] restaurants;


    public Restaurants() {
        setRestaurants();
    }


    public Restaurant[] getRestaurants() {
        return restaurants;
    }

    /**
     *    Fetch restaurants from the server and set the restaurants field.
     */
    private void setRestaurants() {


        String url = SystemConstants.RESTAURANT_URL;

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Fetch and map the JSON to a list of Restaurant objects
        ResponseEntity<Restaurant[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Restaurant[]>() {
                }
        );

        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            this.restaurants = response.getBody();
        }

    }

    @Override
    public String toString() {
        return "Restaurants{" +
                "restaurants=" + Arrays.toString(restaurants) +
                '}';
    }

    /**
     * for testing purposes, this method allows restaurants to be set manually
     * @param restaurantArray the restaurants to be set
     */
    public void overrideRestaurants(Restaurant[] restaurantArray) {
        restaurants = restaurantArray;
    }
}
