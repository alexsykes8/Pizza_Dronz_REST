package uk.ac.ed.inf.pizzadronz.model.PathInfo;

/**
 * Represents a pair of two LngLat objects.
 */
public class LngLatPair {
    private LngLat position1;
    private LngLat position2;

    public LngLatPair() {
    }

    public LngLatPair(LngLat position2, LngLat position1) {
        this.position2 = position2;
        this.position1 = position1;
    }

    public LngLat getPosition2() {
        return position2;
    }

    public void setPosition2(LngLat position2) {
        this.position2 = position2;
    }

    public LngLat getPosition1() {
        return position1;
    }

    public void setPosition1(LngLat position1) {
        this.position1 = position1;
    }
}
