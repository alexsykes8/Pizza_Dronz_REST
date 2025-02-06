package uk.ac.ed.inf.pizzadronz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the LngLat handling endpoints using MockMvc for correct response codes.
 */

@SpringBootTest
@AutoConfigureMockMvc
public class LngLatHandlingMockMvcTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * test validate order with valid input
     * @throws Exception
     */
    @Test
    void testValidateOrder() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();
        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                        .andExpect(status().isOk())
                        .andReturn();
    }


    /**
     * test getDistanceTo with pos1 lng is not a number
     */
    @Test
    void testGetDistanceTo_2() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();
        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", "-3.192473");
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    /**
     *test getDistanceTo with pos1 missing
     */
    @Test
    void testGetDistanceTo_3() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    /**
     *test getDistanceTo with pos2 missing
     */
    @Test
    void testGetDistanceTo_4() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position3 = inputJson.putObject("position3");
        position3.put("lng", -8.733829);
        position3.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    /**
     *test getDistanceTo with pos1 lng out of range
     */
    @Test
    void testGetDistanceTo_5() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -1113.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     *test getDistanceTo with pos2 lng out of range
     */
    @Test
    void testGetDistanceTo_6() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", 3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -180.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    /**
     *test getDistanceTo with pos2 lng out of range
     */
    @Test
    void testGetDistanceTo_7() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -189);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    /**
     *test getDistanceTo with pos1 lng out of range
     */
    @Test
    void testGetDistanceTo_8() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -200);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    /**
     * test get distance to with valid input
     */
    @Test
    void testGetDistanceTo_9() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -90.192473);
        position1.put("lat", -35.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -87.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }
    /**
     * test get distance to with valid input
     */
    @Test
    void testGetDistanceTo_10() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", -38.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.733829);
        position2.put("lat", -38.2863479);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * test getDistanceTo with pos2 lat out of range
     */
    @Test
    void testGetDistanceTo_11() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -4.9387);
        position2.put("lat", 100);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * test getDistanceTo with pos1 lat out of range
     */
    @Test
    void testGetDistanceTo_12() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -28.29786);
        position1.put("lat", 112);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * test getDistanceTo with value not double
     * @throws Exception
     */
    @Test
    void testGetDistanceTo_13() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -28.29786);
        position1.put("lat", 70.2786);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", "2ar8");

        mockMvc.perform(post("/distanceTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }


    /**
     * test close to with valid input
     * @throws Exception
     */
    @Test
    void testCloseTo_1() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.1922473);
        position1.put("lat", 55.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -8.733829);
        position2.put("lat", 38.286379);

        mockMvc.perform(post("/isCloseTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }


    /**
     * test close to with valid input
     * @throws Exception
     */
    @Test
    void testCloseTo_2() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position1 = inputJson.putObject("position1");
        position1.put("lng", -3.192473);
        position1.put("lat", 39.946233);

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        mockMvc.perform(post("/isCloseTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }


    /**
     * test close to with empty pos1
     * @throws Exception
     */
    @Test
    void testCloseTo_3() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        mockMvc.perform(post("/isCloseTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * test close to with empty pos2
     * @throws Exception
     */
    @Test
    void testCloseTo_4() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();


        ObjectNode position2 = inputJson.putObject("position2");
        position2.put("lng", -3.19248);
        position2.put("lat", 39.946379);

        mockMvc.perform(post("/isCloseTo")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    /**
     * test next position with valid input
     * @throws Exception
     */
    @Test
    void testNextPosition_1() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        inputJson.put("angle", 45);

        LngLat expectedLngLat = new LngLat(-3.192366933982822, 55.946339066017174);

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * test next position with start lat not double
     * @throws Exception
     */
    @Test
    void testNextPosition_2() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", "55.946233");

        inputJson.put("angle", 45);

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }


    /**
     * test next position with angle not double
     */
    @Test
    void testNextPosition_3() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        inputJson.put("angle", "45");

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }


    /**
     * test next position with valid input
     */
    @Test
    void testNextPosition_4() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        inputJson.put("angle", 17.4);

        LngLat expectedLngLat = new LngLat(-3.1924281438811617, 55.946376136049274);

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();
    }
    /**
     * test next position with angle missing
     */
    @Test
    void testNextPosition_5() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode start = inputJson.putObject("start");
        start.put("lng", -3.192473);
        start.put("lat", 55.946233);

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    /**
     * test next position with start missing
     */
    @Test
    void testNextPosition_6() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        inputJson.put("angle", "17.4");

        mockMvc.perform(post("/nextPosition")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * test is in region with valid input
     */
    @Test
    void testIsInRegion_1() throws Exception {
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

        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();

    }


    /**
     * test is in region with valid input but no region name
     * @throws Exception
     */
    @Test
    void testIsInRegion_2() throws Exception {
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

        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isOk())
                .andReturn();

    }


    /**
     * test is in region with no region
     * @throws Exception
     */
    @Test
    void testIsInRegion_3() throws Exception {
        ObjectNode inputJson = objectMapper.createObjectNode();

        ObjectNode position = inputJson.putObject("position");
        position.put("lng", -2);
        position.put("lat", 3);


        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }


    /**
     * test is in region with invalid input as the region is not closed
     * @throws Exception
     */
    @Test
    void testIsInRegion_4() throws Exception {
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

        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }


    /**
     * test is in region with invalid input as the region is collinear
     * @throws Exception
     */
    @Test
    void testIsInRegion_5() throws Exception {
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


        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * test is in region with invalid input as the region has too few points
     * @throws Exception
     */
    @Test
    void testIsInRegion_6() throws Exception {
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



        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    /**
     * test is in region with invalid input as the region has a string value
     * @throws Exception
     */
    @Test
    void testIsInRegion_7() throws Exception {
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




        mockMvc.perform(post("/isInRegion")
                        .contentType("application/json")
                        .content(inputJson.toString()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

}
