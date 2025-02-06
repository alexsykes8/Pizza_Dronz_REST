package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.pizzadronz.controller.LngLatHandling;
import uk.ac.ed.inf.pizzadronz.exception.InvalidBodyException;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A class that contains the unit tests for the LngLatHandling class.
  */

public class LngLatHandlingUnitTests {
    private final LngLatHandling lngLatHandling = new LngLatHandling();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test the getDistanceTo method with valid data.
     */
    @Test
    void testGetDistanceTo() {
        ObjectNode inputJson = objectMapper.createObjectNode();
        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        double distance = lngLatHandling.getDistanceTo(inputJson);

        assertEquals(18.50883760856019, distance, 0.01, "The distance should be 5.0");
    }

    /**
     * Test the getDistanceTo method with invalid data.
      */
    @Test
    void testGetDistanceTo_2(){
        ObjectNode inputJson = objectMapper.createObjectNode();
        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", "-3.192473");
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("lng and lat must be numbers", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method pos1 missing
     */
    @Test
    void testGetDistanceTo_3(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Missing lng or lat", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method pos2 missing
     */
    @Test
    void testGetDistanceTo_4(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position3 = inputJson.putObject("position3");
        position3.put("lng", -8.733829);
        position3.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Missing lng or lat", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method with pos1 lng out of range.
     */
    @Test
    void testGetDistanceTo_5(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -1113.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Longitudinal position rejected", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method with pos2 lng out of range.
     */
    @Test
    void testGetDistanceTo_6(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", 3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -180.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Longitudinal position rejected", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method with pos2 lng out of range.
     */
    @Test
    void testGetDistanceTo_7(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -189);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Longitudinal position rejected", exception.getMessage());
    }

    /**
     * Test the getDistanceTo method with pos1 lng out of range.
     */
    @Test
    void testGetDistanceTo_8(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -200);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Longitudinal position rejected", exception.getMessage());
    }


    /**
     * Test the getDistanceTo method with valid data.
     */
    @Test
    void testGetDistanceTo_9(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -90.192473);
        position1.put("lat", -35.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -87.733829);
        position2.put("lat", 38.286379);

        double distance = lngLatHandling.getDistanceTo(inputJson);

        assertEquals(74.27331697629559, distance, 0.01, "The distance should be 5.0");

    }

    /**
     * Test the getDistanceTo method with valid data.
     */
    @Test
    void testGetDistanceTo_10(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", -38.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.733829);
        position2.put("lat", -38.2863479);

        double distance = lngLatHandling.getDistanceTo(inputJson);

        assertEquals(0.8536738738985139, distance, 0.01, "The distance should be 5.0");

    }


    /**
     * Test the getDistanceTo method with pos2 lat out of range.
     */
    @Test
    void testGetDistanceTo_11(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -4.9387);
        position2.put("lat", 100);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("latitudinal position rejected", exception.getMessage());

    }

    /**
     * Test the getDistanceTo method with pos1 lat out of range.
     */
    @Test
    void testGetDistanceTo_12(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -28.29786);
        position1.put("lat", 112);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("latitudinal position rejected", exception.getMessage());

    }

    /**
     * Test the getDistanceTo method with pos2 lat not double.
     */
    @Test
    void testGetDistanceTo_13(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -28.29786);
        position1.put("lat", 70.2786);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", "2ar8");

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("lng and lat must be numbers", exception.getMessage());

    }


    /**
     * Test the isCloseTo method with valid data where the points are not close.
     */
    @Test
    void testCloseTo_1(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        boolean close = lngLatHandling.isCloseTo(inputJson);

        assertFalse(close, "The points are not close");
    }

    // test valid true
    /**
     * Test the isCloseTo method with valid data where the points are close.
     * */
    @Test
    void testCloseTo_2(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 39.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        boolean close = lngLatHandling.isCloseTo(inputJson);

        assertTrue(close, "The points are close");
    }


    /**
     * Test the isCloseTo method with pos1 missing.
     */
    @Test
    void testCloseTo_3(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Missing lng or lat", exception.getMessage());
    }


    /**
     * Test the isCloseTo method with pos2 missing.
     */
    @Test
    void testCloseTo_4(){
        ObjectNode inputJson = objectMapper.createObjectNode();


        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.getDistanceTo(inputJson));
        assertEquals("Missing lng or lat", exception.getMessage());
    }


    /**
     * Test the nextPosition method with valid data.
     */
    @Test
    void testNextPosition_1(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);
        
        inputJson.put("angle", 45);

        LngLat expectedLngLat = new LngLat(-3.192366933982822, 55.946339066017174);

        LngLat actual = lngLatHandling.nextPosition(inputJson);

        boolean samePoint = expectedLngLat.getLat() == actual.getLat() && expectedLngLat.getLng() == actual.getLng();

        assertTrue(samePoint, "The next position is incorrect");
    }


    /**
     * Test the nextPosition method with start lat not double.
     */
    @Test
    void testNextPosition_2(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", "55.946233");

        inputJson.put("angle", 45);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.nextPosition(inputJson));
        assertEquals("lng and lat must be numbers", exception.getMessage());

    }


    /**
     * Test the nextPosition method with angle not double.
     */
    @Test
    void testNextPosition_3(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        inputJson.put("angle", "45");

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.nextPosition(inputJson));
        assertEquals("angle must be numbers", exception.getMessage());

    }

    /**
     * Test the nextPosition method with valid data.
     */
    @Test
    void testNextPosition_4(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        inputJson.put("angle", 17.4);

        LngLat expectedLngLat = new LngLat(-3.1924281438811617, 55.946376136049274);

        LngLat actual = lngLatHandling.nextPosition(inputJson);

        boolean samePoint = expectedLngLat.getLat() == actual.getLat() && expectedLngLat.getLng() == actual.getLng();

        assertTrue(samePoint, "The next position is incorrect");
    }

    /**
     * Test the nextPosition method with angle missing.
     */
    @Test
    void testNextPosition_5(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.nextPosition(inputJson));
        assertEquals("Missing angle", exception.getMessage());

    }

    /**
     * Test the nextPosition method with start missing.
     */
    @Test
    void testNextPosition_6(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("angle", "17.4");

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.nextPosition(inputJson));
        assertEquals("Missing lng or lat", exception.getMessage());

    }

    /**
     * Test the isInRegion method with valid data where the point is not in the region.
     */
    @Test
    void testIsInRegion_1(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -3.192473);
        position.put("lat", 55.946233);

        ObjectNode region = inputJson.putObject("region");
        region.put("name", "central");

        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 170).put("lat", 5);
        vertices.addObject().put("lng", 4).put("lat", -4);
        vertices.addObject().put("lng", -4).put("lat", 2);
        vertices.addObject().put("lng", 170).put("lat", 5);

        boolean inRegion = lngLatHandling.isInRegion(inputJson);

        assertFalse(inRegion, "The point is not in the region");

    }


    /**
     * Test the isInRegion method with valid data where the point is not in the region and the region has no name.
     */
    @Test
    void testIsInRegion_2(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);

        ObjectNode region = inputJson.putObject("region");
        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 170).put("lat", 5);
        vertices.addObject().put("lng", 4).put("lat", -4);
        vertices.addObject().put("lng", -4).put("lat", 2);
        vertices.addObject().put("lng", 170).put("lat", 5);

        boolean inRegion = lngLatHandling.isInRegion(inputJson);

        assertFalse(inRegion, "The point is not in the region");

    }


    /**
     * Test the isInRegion method with missing region.
     */
    @Test
    void testIsInRegion_3(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);


        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.isInRegion(inputJson));
        assertEquals("No region found", exception.getMessage());

    }


    /**
     * Test the isInRegion method with a region that is not closed.
     */
    @Test
    void testIsInRegion_4(){
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);

        ObjectNode region = inputJson.putObject("region");
        region.put("name", "central");

        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 170).put("lat", 5);
        vertices.addObject().put("lng", 4).put("lat", -4);
        vertices.addObject().put("lng", -4).put("lat", 2);

        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.isInRegion(inputJson));
        assertEquals("Region is not closed", exception.getMessage());

    }

    /**
     * Test the isInRegion method with a region that is collinear.
     */
    @Test
    void testIsInRegion_5(){
        ObjectNode inputJson = objectMapper.createObjectNode();


        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);

        ObjectNode region = inputJson.putObject("region");
        region.put("name", "central");

        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 1).put("lat", 2);
        vertices.addObject().put("lng", 2).put("lat", 3);
        vertices.addObject().put("lng", 3).put("lat", 4);
        vertices.addObject().put("lng", 4).put("lat", 5);
        vertices.addObject().put("lng", 5).put("lat", 6);
        vertices.addObject().put("lng", 6).put("lat", 7);
        vertices.addObject().put("lng", 1).put("lat", 2);



        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.isInRegion(inputJson));
        assertEquals("Collinear region", exception.getMessage());

    }


    /**
     * Test the isInRegion method with a region that has too few points.
     */
    @Test
    void testIsInRegion_6(){
        ObjectNode inputJson = objectMapper.createObjectNode();


        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);

        ObjectNode region = inputJson.putObject("region");
        region.put("name", "central");

        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 170).put("lat", 5);
        vertices.addObject().put("lng", 12).put("lat", 44);
        vertices.addObject().put("lng", 170).put("lat", 5);



        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.isInRegion(inputJson));
        assertEquals("Two few points", exception.getMessage());

    }


    /**
     * Test the isInRegion method with a region that has a string instead of a number.
     */
    @Test
    void testIsInRegion_7(){
        ObjectNode inputJson = objectMapper.createObjectNode();


        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);

        ObjectNode region = inputJson.putObject("region");
        region.put("name", "central");

        ArrayNode vertices = region.putArray("vertices");

        vertices.addObject().put("lng", 170).put("lat", 5);
        vertices.addObject().put("lng", "4").put("lat", -4);
        vertices.addObject().put("lng", -4).put("lat", 2);
        vertices.addObject().put("lng", 170).put("lat", 5);




        InvalidBodyException exception = assertThrows(InvalidBodyException.class, () -> lngLatHandling.isInRegion(inputJson));
        assertEquals("lng and lat must be numbers", exception.getMessage());

    }


}
