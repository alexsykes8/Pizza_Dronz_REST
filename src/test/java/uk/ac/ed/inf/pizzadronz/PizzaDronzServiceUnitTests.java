package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.pizzadronz.controller.PizzaDronzService;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the PizzaDronzService class, which contains the general endpoints.
 */
public class PizzaDronzServiceUnitTests {
    private final PizzaDronzService pizzaDronzService = new PizzaDronzService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetUUID() {
        String uuid = pizzaDronzService.getUUID();
        assertEquals("s2337768", uuid, "The uuid should be s2337768");
    }

    @Test
    void testGetisAlive() {
        Boolean life = pizzaDronzService.isAlive();
        assertEquals(true, life, "The service is dead");
    }


}
