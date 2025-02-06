package uk.ac.ed.inf.pizzadronz.model.GeoJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Create geometry objects.
 *
 * <p>This class allows creation of geometry objects in order to map paths, points and regions to geoJSON strings.</p>

 */
public abstract class Geometry {
    protected String type;
    protected List<Object> coordinates = new ArrayList<>();

    public String getType() {
        return type;
    }

    public List<Object> getCoordinates() {
        return coordinates;
    }
}
