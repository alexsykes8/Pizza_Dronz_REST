package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a point on the globe by its longitude and latitude.
 */
public class LngLat {
    private double lng;
    private double lat;

    public LngLat() {
    }

    @JsonCreator
    public LngLat(@JsonProperty("lng") double lng, @JsonProperty("lat") double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "{\"lng\": " + lng + ", \"lat\": " + lat + '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LngLat lngLat = (LngLat) o;
        return Double.compare(lngLat.lng, lng) == 0 && Double.compare(lngLat.lat, lat) == 0;
    }

    public int hashCode() {
        return Objects.hash(lng, lat);
    }
}
