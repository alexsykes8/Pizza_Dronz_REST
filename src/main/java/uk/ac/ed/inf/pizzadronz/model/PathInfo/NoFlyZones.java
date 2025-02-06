package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.pizzadronz.constant.SystemConstants;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.CENTRAL_URL;
import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.DRONE_MOVE_DISTANCE;
import static uk.ac.ed.inf.pizzadronz.model.PathInfo.AStar.doSegmentsIntersect;

/**
 * Represents no fly zones and central area on the map.
 * <p>Provides methods for retrieving the no fly zones and central from server.</p>
 *
 */

public class NoFlyZones {
    private NamedRegion[] noFlyZones;
    private NamedRegion central = setCentralRegion();


    public NoFlyZones() {
        setNoFlyZones();
    }



    public NamedRegion[] getNoFlyZones() {
        return noFlyZones;
    }


    /**
     *     Fetch no fly zones from the server and set the noFlyZones field.
      */
    private void setNoFlyZones() {
        String url = SystemConstants.NO_FLY_ZONES_URL;

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Fetch and map the JSON to a list of NamedRegion objects
        ResponseEntity<NamedRegion[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<NamedRegion[]>() {
                }
        );

        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            this.noFlyZones = response.getBody();
        }

    }


    /**
     * for testing purposes, this method allows no fly zones to be set manually
     * @param noFlyZones the no fly zones to be set
      */
    public void addNoFlyZones(NamedRegion[] noFlyZones) {
        NamedRegion[] newNoFlyZones = new NamedRegion[this.noFlyZones.length + noFlyZones.length];
        for (int i = 0; i < this.noFlyZones.length; i++) {
            newNoFlyZones[i] = this.noFlyZones[i];
        }
        for (int i = 0; i < noFlyZones.length; i++) {
            newNoFlyZones[this.noFlyZones.length + i] = noFlyZones[i];
        }

        this.noFlyZones = newNoFlyZones;
    }


    /**
     *     get the central region from the server
     *     @return the central region, allowing it to be set as a class parameter
      */
    public NamedRegion setCentralRegion() {
        String url = CENTRAL_URL;

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Fetch and map the JSON to a list of NamedRegion objects
        ResponseEntity<NamedRegion> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<NamedRegion>() {
                }
        );

        NamedRegion central = null;
        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            central = response.getBody();
        }

        return central;
    }

    public NamedRegion getCentral() {
        return central;
    }


}
