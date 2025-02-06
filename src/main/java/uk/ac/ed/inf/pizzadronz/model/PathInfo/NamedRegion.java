package uk.ac.ed.inf.pizzadronz.model.PathInfo;

import java.util.Arrays;


/**
 * used to represent no fly zones or central area
  */

public class NamedRegion{
    private String name;
    private LngLat[] vertices;
    // used to store the max and min lng and lat values of the region to create bounds for more efficient in region checks
    private Double maxLng;
    private Double maxLat;
    private Double minLng;
    private Double minLat;


    public NamedRegion() {
    }

    public NamedRegion(String name, LngLat[] vertices) {
        this.name = name;
        this.vertices = vertices;
    }

    public Double getMaxLng() {
        return maxLng;
    }
    public Double getMaxLat() {
        return maxLat;
    }
    public Double getMinLng() {
        return minLng;
    }
    public Double getMinLat() {
        return minLat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLat[] getVertices() {
        return vertices;
    }

    public void setVertices(LngLat[] vertices) {
        this.vertices = vertices;
        setMinMaxLngLat();
    }


    /**
     * sets the max and min lng and lat values of the region to create bounds for more efficient in region checks
     */
    public void setMinMaxLngLat() {
        for (LngLat vertex : vertices) {
            if (maxLng == null || vertex.getLng() > maxLng) {
                maxLng = vertex.getLng();
            }
            if (maxLat == null || vertex.getLat() > maxLat) {
                maxLat = vertex.getLat();
            }
            if (minLng == null || vertex.getLng() < minLng) {
                minLng = vertex.getLng();
            }
            if (minLat == null || vertex.getLat() < minLat) {
                minLat = vertex.getLat();
            }
        }
    }


    @Override
    public String toString() {
        return "{\"name\": \"" + name + "\", \"vertices\": " + Arrays.toString(vertices) + "}";
    }
}
