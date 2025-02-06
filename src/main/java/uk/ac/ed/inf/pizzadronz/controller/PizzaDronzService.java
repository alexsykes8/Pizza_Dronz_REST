package uk.ac.ed.inf.pizzadronz.controller;

import org.springframework.web.bind.annotation.*;

@RestController

/**
 * This class deals with general endpoints.
 */

public class PizzaDronzService {

    /**
     * This method returns my UUID
     * @return String, my UUID
     */
    @GetMapping("/uuid")
    public String getUUID() {
        return "s2337768";
    }

    /**
     * This method checks if the service is alive.
     * @return boolean true if the service is alive
     */
    @GetMapping("/isAlive")
    public boolean isAlive() {
        return true;
    }

}
