package uk.ac.ed.inf.pizzadronz.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.pizzadronz.exception.InvalidBodyException;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.*;

import java.awt.geom.Path2D;

import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.DRONE_IS_CLOSE_DISTANCE_SMALL;
import static uk.ac.ed.inf.pizzadronz.constant.SystemConstants.DRONE_MOVE_DISTANCE_SMALL;


@RestController

/**
 * Deals with the LngLat calculations.
 *
 * <p>This class provides methods to deal with intermediate calculations related to CW1 endpoints.</p>

 */
public class LngLatHandling {

    /**
     * Gets the distance between two points
     * @param pairRequest the two points to calculate the distance between
     * @return the distance between the two points
     */
    @PostMapping("/distanceTo")
    public double getDistanceTo(@RequestBody JsonNode pairRequest) {
        // Checks that the LngLatPair properties are valid and converts them to LngLatPair object
        LngLatPair lngLatPair = validateAndExtractPair(pairRequest);
        // Checks that each LngLat object is valid
        validDistancePair(lngLatPair);
        return pythag(lngLatPair);
    }

    /**
     * Checks if the distance between two points is less than a certain value
     * @param pairRequest the two points to check if they are close
     * @return true if the distance between the two points is less than the DRONE_IS_CLOSE_DISTANCE_SMALL constant
     */
    @PostMapping("/isCloseTo")
    public boolean isCloseTo(@RequestBody JsonNode pairRequest) {

        return getDistanceTo(pairRequest) < DRONE_IS_CLOSE_DISTANCE_SMALL;
    }

    /**
     * Calculates the next position of the drone given a starting position and an angle
     * @param positionRequest the starting position and angle to calculate the next position
     * @return the next position of the drone
     */
    @PostMapping("/nextPosition")
    public LngLat nextPosition(@RequestBody JsonNode positionRequest){
        //Validates input as a request object and converts to NextPositionRequest object
        NextPositionRequest nextPositionRequest = validateAndExtractNextPosition(positionRequest);
        //Checks that the position is valid
        validPosition(nextPositionRequest.getStart());
        //Checks that the angle is valid
        validAngle(nextPositionRequest.getAngle());
        //Calculates the new position by converting to radians and using trigonometry
        double angleRadians = Math.toRadians(nextPositionRequest.getAngle());
        double newLong = nextPositionRequest.getStart().getLng()
                + DRONE_MOVE_DISTANCE_SMALL * Math.sin(angleRadians);
        double newLat = nextPositionRequest.getStart().getLat()
                + DRONE_MOVE_DISTANCE_SMALL * Math.cos(angleRadians);

        return new LngLat(newLong, newLat);
    }

    /**
     * Checks if a point is within a region
     * @param JsonRegionRequest the point and region to check if the point is within the region
     * @return true if the point is within the region
     */
    @PostMapping("/isInRegion")
    public boolean isInRegion(@RequestBody JsonNode JsonRegionRequest) {
        //Validates input as a request object and converts to IsInRegionRequest object
        IsInRegionRequest regionRequest = validateAndExtractRegion(JsonRegionRequest);
        return IsInRegionHelper(regionRequest);
    }

    /**
     * Checks if a point is within a region
     * @param regionRequest
     * @return true if the point is within the region
     */
    public static boolean IsInRegionHelper(IsInRegionRequest regionRequest){
        //Checks that the position is valid
        validPosition(regionRequest.getPosition());
        LngLat[] vertexes = regionRequest.getRegion().getVertices();
        //checks if the region is closed
        if (vertexes[0].getLng() != vertexes[vertexes.length-1].getLng() || vertexes[0].getLat() != vertexes[vertexes.length-1].getLat()) {
            throw new InvalidBodyException("Region is not closed");
        }

        //Counts the number of vertexes
        int counter = 0;
        for (LngLat vertex : vertexes){
            validPosition(vertex);
            counter++;
        }
        //checks if the polygon has less than three vertexes. -1 because one of those vertexes is a repeat, back to the start
        if (counter-1 < 3){
            throw new InvalidBodyException("Two few points");
        }
        //checks if the points are collinear
        for (int i = 0; i < vertexes.length - 2; i++) {
            if (areCollinear(vertexes[i], vertexes[i+1], vertexes[i+2])) {
                throw new InvalidBodyException("Collinear region");
            }
        }

        //Creates a region object and uses the contains method to check if the point is within the region
        Path2D region = new Path2D.Double();
        region.moveTo(regionRequest.getRegion().getVertices()[0].getLng(), regionRequest.getRegion().getVertices()[0].getLat());
        for (LngLat vertex : regionRequest.getRegion().getVertices()) {
            region.lineTo(vertex.getLng(), vertex.getLat());
        }
        region.closePath();
        return region.contains(regionRequest.getPosition().getLng(), regionRequest.getPosition().getLat());
    }


    /**
     * Converts a valid JsonNode input to an IsInRegionRequest object
     * @param JsonRegionRequest
     * @return IsInRegionRequest object from the input
     */
    private IsInRegionRequest validateAndExtractRegion(JsonNode JsonRegionRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Validate and extract position
        JsonNode positionNode = JsonRegionRequest.get("position");
        validateLngLat(positionNode);
        LngLat position = objectMapper.convertValue(positionNode, LngLat.class);

        // Validate and extract region
        JsonNode regionNode = JsonRegionRequest.get("region");
        validateRegion(regionNode);
        NamedRegion region = objectMapper.convertValue(regionNode, NamedRegion.class);

        return new IsInRegionRequest(position, region);
    }

    /**
     * Validates the region object
     * @param regionNode
     */
    private void validateRegion(JsonNode regionNode){
        if (regionNode == null){
            throw new InvalidBodyException("No region found");
        }
        JsonNode verticesNode = regionNode.get("vertices");
        if (verticesNode == null){
            throw new InvalidBodyException("No vertices found");
        }
        for (JsonNode vertex : verticesNode) {
            validateLngLat(vertex);
        }
    }

    /**
     * Converts a valid JsonNode input to a LngLatPair object
     * @param pairRequest
     * @return LngLatPair object from the input
     */
    private LngLatPair validateAndExtractPair(JsonNode pairRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Validate and extract position1
        JsonNode position1Node = pairRequest.get("position1");
        validateLngLat(position1Node);
        LngLat position1 = objectMapper.convertValue(position1Node, LngLat.class);

        // Validate and extract position2
        JsonNode position2Node = pairRequest.get("position2");
        validateLngLat(position2Node);
        LngLat position2 = objectMapper.convertValue(position2Node, LngLat.class);

        return new LngLatPair(position1, position2);
    }

    /**
     * Converts a valid JsonNode input to a NextPositionRequest object
     * @param positionRequest
     * @return NextPositionRequest object from the input
     */
    private NextPositionRequest validateAndExtractNextPosition(JsonNode positionRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode positionNode = positionRequest.get("start");
        validateLngLat(positionNode);
        LngLat position = objectMapper.convertValue(positionNode, LngLat.class);

        JsonNode angleNode = positionRequest.get("angle");
        if (angleNode == null) {
            throw new InvalidBodyException("Missing angle");
        }

        // Validate that angle is number
        if (!angleNode.isNumber()) {
            throw new InvalidBodyException("angle must be numbers");
        }
        double angle = objectMapper.convertValue(angleNode, double.class);

        return new NextPositionRequest(position, angle);
    }

    /**
     * Validates the position
     * @param positionNode
     */
    private void validateLngLat(JsonNode positionNode) {
        if (positionNode == null || !positionNode.has("lng") || !positionNode.has("lat")) {
            throw new InvalidBodyException("Missing lng or lat");
        }

        // Validate that lng and lat are numbers
        if (!positionNode.get("lng").isNumber() || !positionNode.get("lat").isNumber()) {
            throw new InvalidBodyException("lng and lat must be numbers");
        }
    }

    /**
     * Checks if three points are collinear
     * @param first the first point
     * @param second the second point
     * @param third the third point
     * @return true if the three points are collinear
     */
    public static boolean areCollinear(LngLat first, LngLat second, LngLat third) {
        return pythag(new LngLatPair(first, second)) + pythag(new LngLatPair(second, third)) == pythag(new LngLatPair(third, first));
    }

    /**
     * Calculates the distance between two points
     * @param lngLatPair the two points to calculate the distance between
     * @return the distance between the two points
     */
    public static Double pythag(LngLatPair lngLatPair) {
        return Math.sqrt(Math.pow(lngLatPair.getPosition1().getLat()-lngLatPair.getPosition2().getLat(),2)
                +Math.pow(lngLatPair.getPosition1().getLng()-lngLatPair.getPosition2().getLng(),2));
    }

    /**
     * Validates a LngLatPair
     * @param PairRequest
     */
    public void validDistancePair(LngLatPair PairRequest) {
        validPosition(PairRequest.getPosition1());
        validPosition(PairRequest.getPosition2());
    }

    /**
     * Validates a LngLat position
     * @param position
     */
    public static void validPosition(LngLat position){
        if (position == null){
            throw new InvalidBodyException("Position is null");
        }
        if (position.getLng() >= 180 || position.getLng() <= -180){
            throw new InvalidBodyException("Longitudinal position rejected");
        }
        if (position.getLat() <= -90 || position.getLat() >= 90){
            throw new InvalidBodyException("latitudinal position rejected");
        }
    }

    /**
     * Validates an angle
     * @param angle
     */
    public void validAngle(double angle) {
        if (angle < 0 || angle > 360){
            throw new InvalidBodyException("Angle must be between 0 and 360");
        }
    }
    


}
