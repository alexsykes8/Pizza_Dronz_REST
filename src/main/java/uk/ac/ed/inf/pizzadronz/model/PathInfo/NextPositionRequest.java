package uk.ac.ed.inf.pizzadronz.model.PathInfo;

/**
 * Represents a request for the next position of the drone.
 */

public class NextPositionRequest {
    private LngLat start;
    private double angle;

    public NextPositionRequest() {
    }

    public NextPositionRequest(LngLat start, double angle) {
        this.start = start;
        this.angle = angle;
    }

    public LngLat getStart() {
        return start;
    }

    public void setStart(LngLat start) {
        this.start = start;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
