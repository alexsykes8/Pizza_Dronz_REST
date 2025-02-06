package uk.ac.ed.inf.pizzadronz.model.GeoJson;

import java.util.List;

/**
 * Create GeoJSON objects.
 *
 * <p>This class allows creation of GeoJSON objects in order to map to geoJSON strings.</p>

 */
public class GeoJson {
    public String type;
    public List<Feature> features;

    public GeoJson(String type, List<Feature> features) {
        this.type = type;
        this.features = features;
    }
}
