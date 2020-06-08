package org.dice_research.opal.launuts.polygons.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class NutsParserMethodsTest {

	@Test
	public void getInnerRings() {
		double[][] arrays = { { 50.13351, 8.81796 }, { 50.07466, 8.8237 }, { 50.06241, 8.71783 }, { 50.13673, 8.78065 },
				{ 50.13351, 8.81796 } };
		JSONObject coordinates = new JSONObject();
		coordinates.put("coordinates", arrays);
		JSONArray contentItems = new JSONArray();
		contentItems.add(coordinates);
		JSONArray inner_rings = NutsParser.getInnerRings(contentItems);
		System.out.println(inner_rings);
	}

//	@Test
//	public void hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd() {
//	}

	@Test
	public void getCoordinatesLatLongFormat() {
		double[][] arrays = { { 8.81796, 50.13351 }, { 8.8237, 50.07466 }, { 8.71783, 50.06241 }, { 8.78065, 50.13673 },
				{ 50.13351, 8.81796 } };
		JSONObject coordinates = new JSONObject();
		coordinates.put("coordinates", arrays);
		JSONArray contentItems = new JSONArray();
		contentItems.add(coordinates);
		JSONArray lat_long = NutsParser.getCoordinatesLatLongFormat(contentItems);
		System.out.println(lat_long);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void areValidPolygons() {
		double[][] arrays =  { { 50.13351, 8.81796 }, { 50.07466, 8.8237 }, { 50.06241, 8.71783 }, { 50.13673, 8.78065 },
				{ 50.13351, 8.81796 } };
		JSONObject coordinates = new JSONObject();
		coordinates.put("coordinates", arrays);
		JSONArray first_ring = new JSONArray();
		JSONArray coordinates_of_polygon = new JSONArray();
		first_ring.add(coordinates);
		coordinates_of_polygon.add(first_ring);
		String geometry_type = "polygon_type";
		Boolean valid_polygon = NutsParser.areValidPolygons(coordinates_of_polygon, geometry_type);
		Assert.assertEquals("It must return true", true, valid_polygon);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void areInValidPolygons() {
		double[][] arrays = { { 50.13351, 8.81796 }, { 50.07466, 8.8237 }, { 50.06241, 8.71783 },
				{ 50.13673, 8.78065 } };
		JSONObject coordinates = new JSONObject();
		coordinates.put("coordinates", arrays);
		JSONArray first_ring = new JSONArray();
		JSONArray coordinates_of_polygon = new JSONArray();
		first_ring.add(coordinates);
		coordinates_of_polygon.add(first_ring);
		String geometry_type = "multipolygon";
		Boolean invalid_polygon = NutsParser.areValidPolygons(coordinates_of_polygon, geometry_type);
		System.out.println(invalid_polygon);
		Assert.assertEquals("It must return false", false, invalid_polygon);

	}

//	@Test
//	public void createNutPolygons() {
//
//	}
}
