package uk.ac.ed.inf.pizzadronz.model.GeoJson;

import java.util.List;

/**
 * Create line objects.
 *
 * <p>This class allows creation of line objects in order to map paths to geoJSON strings.</p>

 */
public class Line extends Geometry {

    public Line(String type, List<List<Double>>  lngLatList) {
        this.type = type;
        coordinates.addAll(lngLatList);
    }
}

