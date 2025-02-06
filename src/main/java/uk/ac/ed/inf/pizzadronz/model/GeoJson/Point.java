package uk.ac.ed.inf.pizzadronz.model.GeoJson;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;

/**
 * Create point objects.
 *
 * <p>This class allows creation of point objects in order to map restaurant locations to geoJSON strings.</p>

 */
public class Point extends Geometry {
    public Point(String type, LngLat lngLat) {
        this.type = type;
        // Point has a single coordinate pair (LngLat)
        coordinates.add(lngLat.getLng());
        coordinates.add(lngLat.getLat());
    }
}

