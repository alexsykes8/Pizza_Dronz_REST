package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/** class used for testing, to allow the isInRegion method that takes JSON input to be used easily
* create with position and region that you want to test, and then convert to JSON to use as input
 */
public class IsInRegionRequest {
    private LngLat position;
    private NamedRegion region;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public IsInRegionRequest() {
    }

    public IsInRegionRequest(LngLat position, NamedRegion region) {
        this.position = position;
        this.region = region;
    }

    public LngLat getPosition() {
        return position;
    }



    public NamedRegion getRegion() {
        return region;
    }


    /**
     * Convert the IsInRegionRequest object to a JSON object
     * @return JsonNode the JSON object of the IsInRegionRequest
     * @throws JsonProcessingException
     */
    public JsonNode toJson() throws JsonProcessingException {
        return objectMapper.readTree(this.toString());
    }

    @Override
    public String toString() {
        return "{\"position\": " + position + ", " + "\"region\": " + region + '}';
    }
}
