package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.dice_research.opal.launuts.nuts.NutsContainer;
import org.dice_research.opal.launuts.polygons.parser.NutsParser;
import org.geotools.geometry.jts.WKTWriter2;

import io.github.galbiston.geosparql_jena.implementation.datatype.WKTDatatype;
import io.github.galbiston.geosparql_jena.implementation.vocabulary.Geo;

import org.json.simple.JSONObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.json.simple.JSONArray;

public class ModelBuilder {

	private Model model = ModelFactory.createDefaultModel();
	private Map<String, Resource> nuts3map = new HashMap<String, Resource>();

	private final static boolean ADD_TYPE_CONCEPT = false;
	private final static boolean ADD_NARROWER = false;

	ModelBuilder() {
		model.setNsPrefix("dct", Vocabularies.NS_DCTERMS);
		model.setNsPrefix("dcat", Vocabularies.NS_DCAT);
		model.setNsPrefix("launuts", Vocabularies.NS_LAUNUTS);
		model.setNsPrefix("lau", Vocabularies.NS_LAU);
		model.setNsPrefix("nuts", Vocabularies.NS_EU_NUTS);
		model.setNsPrefix("skos", Vocabularies.NS_SKOS);
		model.setNsPrefix("xsd", Vocabularies.NS_XSD);
		model.setNsPrefix("ogc", Vocabularies.NS_OGC);
		model.setNsPrefix("geo", Vocabularies.NS_GEO);
		model.setNsPrefix("dbr", Vocabularies.NS_DBR);
		model.setNsPrefix("dbo", Vocabularies.NS_DBO);

		// Additional prefixes to reduce model size
		model.setNsPrefix("laude", Vocabularies.NS_LAU_DE);
		model.setNsPrefix("nutscode", Vocabularies.NS_EU_NUTS_CODE);

		// Prefix for identifying a geometry type
		model.setNsPrefix("sf", "http://www.opengis.net/ont/sf#");
	}

	/**
	 * This method adds a polygon or several polygons for each lau and nuts
	 * depending on their geometry type. The polygons are added as a blanknode
	 * resource for dct:location predicate. The polygons are further represented by
	 * another blanknode through dcat:centroid predicate.
	 * 
	 * @param res
	 * @param resourceId
	 * @param polygons
	 * @param resourceIdType
	 */
	public void addPolygons(Resource res, String resourceId, JSONArray polygons, String resourceIdType) {

		Iterator<JSONObject> polygonsIterator = polygons.iterator();

		while (polygonsIterator.hasNext()) {

			JSONObject nextPolygon = polygonsIterator.next();

			if (nextPolygon.get(resourceIdType).toString().equals(resourceId)
					&& nextPolygon.get("valid_polygon").equals("true")) {

				Property polygonProperty = getModel().createProperty("http://www.opengis.net/ont/sf#Polygon");
				Property pointProperty = getModel().createProperty("http://www.opengis.net/ont/sf#Point");
				Property asWKTProperty = getModel().createProperty("http://www.opengis.net/ont/geosparql#asWKT");
				Property centroidProperty = getModel().createProperty("https://www.w3.org/ns/dcat#centroid");
				Property dctermsLocationProperty = getModel().createProperty("http://purl.org/dc/terms/Location");

				NutsParser nutsParser = new NutsParser();
				GeometryFactory geometryFactory = new GeometryFactory();
				JSONArray coordinates = (JSONArray) nextPolygon.get("coordinates");
				WKTWriter2 wktWriter = new WKTWriter2();

				if ("polygon".equalsIgnoreCase(nextPolygon.get("geometry_type").toString())) {
					Polygon outerRing = geometryFactory.createPolygon(
							nutsParser.getOuterRing(nutsParser.changeCoordinatesFormat(coordinates)), null);
					JSONArray innerRings = (JSONArray) nextPolygon.get("inner_rings");
					String outerRingCoordinates = wktWriter.write(outerRing);
					String centroidCoordinates = wktWriter.write(outerRing.getCentroid());
					Literal polygonWkt = ResourceFactory.createTypedLiteral(outerRingCoordinates, WKTDatatype.INSTANCE);
					Literal centroidWkt = ResourceFactory.createTypedLiteral(centroidCoordinates, WKTDatatype.INSTANCE);

					// This is a blank-node which contains polygon coordinates
					Resource polygonResource = getModel().createResource().addProperty(RDF.type, polygonProperty)
							.addProperty(asWKTProperty, polygonWkt);

					// This blank-node contains centroid point
					Resource centroidResource = getModel().createResource().addProperty(RDF.type, pointProperty)
							.addProperty(asWKTProperty, centroidWkt);

					if (innerRings.size() == 0)
						polygonResource.addProperty(centroidProperty, centroidResource);

					getModel().add(res, (Property) dctermsLocationProperty, polygonResource);
				} else
				
				//When a lau or nuts is a multipolygon
				{
					for (int i = 0; i < coordinates.size(); i++) {
						JSONArray childCordinates = (JSONArray) coordinates.get(i);
						Polygon outerRing = geometryFactory.createPolygon(
								nutsParser.getOuterRing(nutsParser.changeCoordinatesFormat(childCordinates)), null);
						JSONArray innerRings = nutsParser.getInnerRings(childCordinates);
						String outerRingCoordinates = wktWriter.write(outerRing);
						String centroidCoordinates = wktWriter.write(outerRing.getCentroid());
						Literal polygonWkt = ResourceFactory.createTypedLiteral(outerRingCoordinates,
								WKTDatatype.INSTANCE);
						Literal centroidWkt = ResourceFactory.createTypedLiteral(centroidCoordinates,
								WKTDatatype.INSTANCE);

						// This is a blank-node which contains polygon coordinates
						Resource polygonResource = getModel().createResource().addProperty(RDF.type, polygonProperty)
								.addProperty(asWKTProperty, polygonWkt);

						// This blank-node contains centroid point
						Resource centroidResource = getModel().createResource().addProperty(RDF.type, pointProperty)
								.addProperty(asWKTProperty, centroidWkt);

						if (innerRings.size() == 0)
							polygonResource.addProperty(centroidProperty, centroidResource);

						getModel().add(res, (Property) dctermsLocationProperty, polygonResource);
					}
				}
			}
		}
	}

	public ModelBuilder addNuts(Collection<NutsContainer> nutsCollection, JSONArray nutsPolygons) {
		for (NutsContainer container : nutsCollection) {

			Resource nuts = getModel().createResource(container.getUri());

			if (ADD_TYPE_CONCEPT) {
				getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_CONCEPT);
			}
			getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS);
			if (container.nutsLevel == null) {
				System.err.println("Warning: no level for container " + container.getUri());
			} else {
				switch (container.nutsLevel) {
				case 0:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_0);
					break;
				case 1:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_1);
					break;
				case 2:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_2);
					break;
				case 3:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_3);
					break;
				default:
					break;
				}
			}

			if (container.parent != null) {
				// Not available for root
				getModel().add(nuts, Vocabularies.PROP_BROADER, model.getResource(container.parent.getUri()));
				if (ADD_NARROWER) {
					getModel().add(model.getResource(container.parent.getUri()), Vocabularies.PROP_NARROWER, nuts);
				}
			}

			getModel().add(nuts, Vocabularies.PROP_NOTATION, getModel().createLiteral(container.notation));

			if (container.prefLabel.size() == 1) {
				// Add only label
				String prefLabel = container.prefLabel.iterator().next();
				String simpleName = NutsContainer.toSimpleName(prefLabel);
				getModel().add(nuts, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(simpleName));
				if (!simpleName.equals(prefLabel)) {
					getModel().add(nuts, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(prefLabel));
				}
			} else {
				// Get shortest label
				String shortestLabel = NutsContainer.toSimpleName(container.prefLabel.iterator().next());
				for (String prefLabel : container.prefLabel) {
					prefLabel = NutsContainer.toSimpleName(prefLabel);
					if (prefLabel.length() < shortestLabel.length()) {
						shortestLabel = prefLabel;
					}
				}
				// Use short form of label as preferred label
				getModel().add(nuts, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(shortestLabel));
				// Add other variants
				for (String prefLabel : container.prefLabel) {
					if (!shortestLabel.equals(prefLabel)) {
						getModel().add(nuts, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(prefLabel));
					}
				}
			}
			addPolygons(nuts, container.notation, nutsPolygons, "nuts_id");

			nuts3map.put(container.notation, nuts);
		}
		return this;
	}

	public ModelBuilder addLau(List<LauContainer> lauList, JSONArray lauPolygons) {
		for (LauContainer container : lauList) {
			Resource lau = getModel().createResource(container.getUri());
			if (nuts3map.containsKey(container.nuts3code)) {

				if (ADD_TYPE_CONCEPT) {
					getModel().add(lau, Vocabularies.PROP_TYPE, Vocabularies.RES_CONCEPT);
				}
				getModel().add(lau, Vocabularies.PROP_TYPE, Vocabularies.RES_LAU);

				getModel().add(lau, Vocabularies.PROP_BROADER, nuts3map.get(container.nuts3code));
				if (ADD_NARROWER) {
					getModel().add(nuts3map.get(container.nuts3code), Vocabularies.PROP_NARROWER, lau);
				}

				getModel().add(lau, Vocabularies.PROP_NOTATION, getModel().createLiteral(container.lauCode));

				getModel().add(lau, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(container.getSimpleName()));
				if (!container.getSimpleName().equals(container.lauNameLatin)) {
					getModel().add(lau, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(container.lauNameLatin));
				}
			} else {
				System.err.println("Unknown NUTS3 code: " + container.nuts3code + " for " + container.lauCode);
				continue;
			}
			addPolygons(lau, container.lauCode, lauPolygons, "lau_code");
		}
		return this;
	}

	public ModelBuilder addGeoData(Map<String, DbpediaPlaceContainer> dbpediaIndex, Map<String, String> nutsToDbpedia,
			Map<String, String> lauToDbpedia) {

		for (Entry<String, String> nuts2dbp : nutsToDbpedia.entrySet()) {
			Resource res = ResourceFactory.createResource(nuts2dbp.getKey());
			if (getModel().containsResource(res) && dbpediaIndex.containsKey(nuts2dbp.getValue())) {
				Resource dbpediaRes = getDbpediaResource(dbpediaIndex.get(nuts2dbp.getValue()));
				getModel().add(res, Vocabularies.PROP_RELATEDMATCH, dbpediaRes);
			}
		}

		for (Entry<String, String> lau2dbp : lauToDbpedia.entrySet()) {
			Resource res = ResourceFactory.createResource(lau2dbp.getKey());
			if (getModel().containsResource(res) && dbpediaIndex.containsKey(lau2dbp.getValue())) {
				Resource dbpediaRes = getDbpediaResource(dbpediaIndex.get(lau2dbp.getValue()));
				getModel().add(res, Vocabularies.PROP_RELATEDMATCH, dbpediaRes);
			}
		}

		return this;
	}

	Resource getDbpediaResource(DbpediaPlaceContainer dbpediaPlaceContainer) {
		Resource res = ResourceFactory.createResource(dbpediaPlaceContainer.uri);
		if (getModel().containsResource(res)) {
			return res;
		} else {
			getModel().add(res, Vocabularies.PROP_TYPE, Vocabularies.RES_PLACE);

			Literal wkt = ResourceFactory.createTypedLiteral(
					"POINT(" + dbpediaPlaceContainer.lat + " " + dbpediaPlaceContainer.lon + ")", WKTDatatype.INSTANCE);
			getModel().addLiteral(res, Geo.HAS_GEOMETRY_PROP, wkt);
			getModel().addLiteral(res, Vocabularies.PROP_LAT, dbpediaPlaceContainer.lat);
			getModel().addLiteral(res, Vocabularies.PROP_LONG, dbpediaPlaceContainer.lon);
			return res;
		}
	}

	public Model getModel() {
		return model;
	}

	public String getTurtleComment() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# LAU and NUTS data for Germany\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Local Administrative Units (LAU)\n");
		stringBuilder.append("# Nomenclature of Territorial Units for Statistics (NUTS)\n");
		stringBuilder.append("# https://ec.europa.eu/eurostat/web/nuts/overview\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Data:\n");
		stringBuilder.append("# https://hobbitdata.informatik.uni-leipzig.de/OPAL/\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Generator software: \n");
		stringBuilder.append("# Data Science Group (DICE) at Paderborn University\n");
		stringBuilder.append("# Open Data Portal Germany (OPAL), Adrian Wilke\n");
		stringBuilder.append("# https://github.com/projekt-opal/LauNuts\n");
		stringBuilder.append("# \n");

		return stringBuilder.toString();
	}

	public ModelBuilder writeModel(File outputDirectory) throws IOException {
		File file = new File(outputDirectory, "launuts.ttl");
		file.getParentFile().mkdirs();
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(getTurtleComment().getBytes());
		RDFDataMgr.write(outputStream, model, Lang.TURTLE);
		return this;
	}

}