package org.dice_research.opal.launuts.polygons.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.apache.commons.io.IOUtils;
import org.dice_research.opal.launuts.lau.LauReaderInterface;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.PolygonParserInterface;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LauParser implements PolygonParserInterface{

	public static void main(String[] args) throws IOException, Exception {

		File file = new File(new LauParser().getClass().getClassLoader()
				.getResource("launuts_geojson_and_shape_files/lau_1_1_million/LAU_2018.shp").getFile());
		Map<String, Object> map = new HashMap<>();
		map.put("url", file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);
		String typeName = dataStore.getTypeNames()[0];
		JSONArray all_polygons = new JSONArray();

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
		FeatureIterator<SimpleFeature> features = collection.features();
		int total_number_of_laus = 0;
		while (features.hasNext()) {

			SimpleFeature feature = features.next();

			if (feature.getAttributes().toArray()[3].toString().contains("DE_")) {
				// Case study: DE_08326074(MultiPolygon), DE_08326054(Polygon)

				JSONObject a_polygon_object = new JSONObject();
				byte[] byteArrray = feature.getAttributes().toArray()[2].toString().getBytes();
				a_polygon_object.put("gisco_id", feature.getAttributes().toArray()[3].toString());
				a_polygon_object.put("lau_label", feature.getAttributes().toArray()[2].toString().replace("Ã¶", "ö")
						.replace("Ã¤", "ä").replace("Ã¼", "ü").replace("Ã", "Ü").replace("Ã", "Ö"));
				a_polygon_object.put("lau_code", feature.getAttributes().toArray()[1].toString());

				GeometryFactory geometryFactory = new GeometryFactory();
				JSONArray PolygonCoordinates = new JSONArray();

				WKTReader reader = new WKTReader(geometryFactory);
				MultiPolygon multi_polygon = (MultiPolygon) reader
						.read(feature.getAttributes().toArray()[0].toString());

				String proccess_builder_parameter = '"' + multi_polygon.toString() + '"';

				/**
				 * Call to Node.JS library to parse WKT to GeoJSON. The response from Node.Js is
				 * stored in a temporary file in JSON format.
				 */
				ProcessBuilder pb = new ProcessBuilder("node", "wkt_to_json_parser.js", proccess_builder_parameter);
				pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
				pb.redirectError(ProcessBuilder.Redirect.INHERIT);

				Process process = pb.start();
				// String output = IOUtils.toString(process.getInputStream(),
				// StandardCharsets.UTF_8);
				if (total_number_of_laus > 10)
					process.destroy();
				else
					process.waitFor();

				JSONParser node_response_parser = new JSONParser();

				// Read the Node response from this location.
				Reader node_response = new FileReader(
						"src/main/resources/launuts_geojson_and_shape_files/node_response.json");

				JSONObject jsonObject = (JSONObject) node_response_parser.parse(node_response);

				// For evaluating if a lau is a polygon or multi_polygon
				JSONArray coordinates = (JSONArray) jsonObject.get("coordinates");
				int children_of_coordinates = coordinates.size();
				JSONArray first_child_of_coordinates = (JSONArray) coordinates.get(0);

				if (children_of_coordinates > 1) {
					a_polygon_object.put("geometry_type", "MultiPolygon");
					a_polygon_object.put("coordinates", coordinates);
				} else {
					a_polygon_object.put("geometry_type", "Polygon");
					a_polygon_object.put("coordinates", first_child_of_coordinates);
				}
				all_polygons.add(a_polygon_object);

				// Runtime visual response
				ObjectMapper mapper = new ObjectMapper();
				System.out.println(mapper.writeValueAsString(a_polygon_object));
				System.out.println("Total number of Laus processed: " + total_number_of_laus);
				total_number_of_laus++;

			}

		}

		features.close();
		dataStore.dispose();

		try (FileWriter json_result = new FileWriter("LAU_Polygons.json")) {
			json_result.write(all_polygons.toJSONString());
			json_result.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Point> getLauCenterPoints(String lauCode) throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.dice_research.opal.launuts.polygons.MultiPolygon getLauPolygon(String lauCode)
			throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Point> getNutsCenterPoints(String nutsCode) throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.dice_research.opal.launuts.polygons.MultiPolygon getNutsPolygon(String nutsCode)
			throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LauReaderInterface setSourceDirectory(File directory) throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

}
