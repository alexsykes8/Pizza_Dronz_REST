package uk.ac.ed.inf.pizzadronz.model.GeoJson;

import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;

import java.util.ArrayList;
import java.util.List;

/**
 * Create region objects.
 *
 * <p>This class allows creation of region objects in order to map no fly zones and central to geoJSON strings.</p>

 */

public class Region extends Geometry {

    public Region(String type, LngLat[] vertices) {
        this.type = type;
        List<Object> coordinatesPlain = new ArrayList<>();
        for (LngLat lngLat : vertices) {
            List<Double> coordinatePair = new ArrayList<>();
            coordinatePair.add(lngLat.getLng());
            coordinatePair.add(lngLat.getLat());
            coordinatesPlain.add(coordinatePair);
        }
        coordinates.add(coordinatesPlain);

    }
}
