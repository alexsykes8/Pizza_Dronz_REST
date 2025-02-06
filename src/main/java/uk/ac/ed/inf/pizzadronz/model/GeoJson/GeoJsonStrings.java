package uk.ac.ed.inf.pizzadronz.model.GeoJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.LngLat;
import uk.ac.ed.inf.pizzadronz.model.PathInfo.NamedRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Create GeoJSON strings.
 *
 * <p>This class has methods to create different types of geojson strings, with different combinations of feautures. For testing purposes, many geoJSON string conversion methods are provided</p>

 */
public class GeoJsonStrings {


    /**
     * Convert a path to a GeoJSON string
     * @param path to convert to GeoJSON
     * @return the GeoJSON string of the path
     * @throws JsonProcessingException
     */
    public static String GeoJSONPath(LngLat[] path) throws JsonProcessingException {
        List<List<Double>> lineCoordinates = new ArrayList<>();

        for (LngLat lngLat : path) {
            List<Double> coordinatePair = new ArrayList<>();
            coordinatePair.add(lngLat.getLng());
            coordinatePair.add(lngLat.getLat());
            lineCoordinates.add(coordinatePair);
        }

        Line geometry = new Line("LineString", lineCoordinates);

        Feature feature = new Feature("Feature", geometry, null);

        GeoJson geoJson = new GeoJson("FeatureCollection", List.of(feature));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(geoJson);
    }



    /**
     * Convert paths, points and regions to a GeoJSON string, allowing restaurants, paths and no-fly zones to be displayed
     * @param paths to convert to GeoJSON
     * @param points to convert to GeoJSON
     * @param regions to convert to GeoJSON
     * @return the GeoJSON string of the paths, points and regions
     * @throws JsonProcessingException
     */
    public static String GeoJSONPathsPointsRegionsToString(LngLat[][] paths, LngLat[] points, NamedRegion[] regions) throws JsonProcessingException {
        List<Feature> features = new ArrayList<>();

        for (LngLat[] path : paths) {
            if (path == null) {
                System.out.println("Path is null");
            }
            System.out.println(Arrays.toString(path));
            List<List<Double>> lineCoordinates = new ArrayList<>();

            for (LngLat lngLat : path) {
                List<Double> coordinatePair = new ArrayList<>();
                coordinatePair.add(lngLat.getLng());
                coordinatePair.add(lngLat.getLat());
                lineCoordinates.add(coordinatePair);
            }

            Line geometry = new Line("LineString", lineCoordinates);

            Feature pathFeature = new Feature("Feature", geometry, null);

            features.add(pathFeature);
        }

        for (LngLat point : points) {
            Point geometry = new Point("Point", point);

            features.add(new Feature("Feature", geometry, null));
        }

        for (NamedRegion regionVertices : regions) {
            Region region = new Region("Polygon", regionVertices.getVertices());

            Feature regionFeature = new Feature("Feature", region, null);

            features.add(regionFeature);
        }

        GeoJson geoJson = new GeoJson("FeatureCollection", features);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(geoJson);
    }


}
