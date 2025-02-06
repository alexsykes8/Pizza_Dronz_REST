package uk.ac.ed.inf.pizzadronz.model.GeoJson;

/**
 * Create feature objects.
 *
 * <p>This class allows creation of feature objects in order to map to geoJSON.</p>

 */
public class Feature {
    public String type;
    public Geometry geometry;
    public Object properties;

    public Feature(String type, Geometry geometry, Object properties) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
    }
}
