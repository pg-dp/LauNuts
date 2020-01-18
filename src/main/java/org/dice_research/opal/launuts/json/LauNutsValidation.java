package org.dice_research.opal.launuts.json;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LauNutsValidation {
	public static void main(String[] args) {
		String filename = "LAUs_Polygons.json";
		LauNutsValidation launutsobj = new LauNutsValidation();
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(filename)) {
			// Reads json file
			Object obj = jsonParser.parse(reader);
			JSONArray lauRead = (JSONArray) obj;
			// Here we are parsing lau object,
			// getting the indexes of each geometry type
			for (int i = 0; i < lauRead.size(); i++) {
				launutsobj.parseLauObject((JSONObject) lauRead.get(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private String parseLauObject(JSONObject type) {
		String lautype = (String) type.get("Initial_geometry_type");
		JSONArray coordinates = (JSONArray) type.get("Coordinates");
		if ((lautype.equalsIgnoreCase("Polygon") || lautype.equalsIgnoreCase("MultiPolygon"))
				&& coordinates.size() > 3) {
			// This polygon checks first and last coordinates are identical
			// and checks whether it is polygon or not
			if (coordinates.get(0).equals(coordinates.get(coordinates.size() - 1))) {
				return "Polygon - true";
			} else {
				if (lautype.equalsIgnoreCase("Polygon")) {
					return "Polygon - false";
				}
				// It will check the record, if it is multi-polygon or not
				for (int i = 1; i < coordinates.size(); i++) {
					if (coordinates.get(0).equals(coordinates.get(i))
							&& coordinates.get(i + 1).equals(coordinates.get(coordinates.size() - 1))) {
					}
				}
				return "Multipolygon - true";
			}
		}
		// Here we are checking whether geometry type is point or not
		else if (lautype.equalsIgnoreCase("Point") && (coordinates.size() == 1)) {
			return "Point - true";
		} else {
			return "Point - false";
		}
	}
}
