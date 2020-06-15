package org.dice_research.opal.launuts.polygons;

import org.dice_research.opal.launuts.lau.LauReaderInterface;

import java.io.File;
import java.util.List;

/**
 * Interface for polygon parser.
 * <p>
 * TODO This is a development recommendation, changes are allowed.
 *
 * @author Adrian Wilke
 * @see NUTS (NUTS 2016, geoJSON, all 5 scales)
 * https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units/nuts
 * @see LAU (LAU 2018, Shapefile)
 * https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units/lau
 * @see Licensing and copyright
 * https://ec.europa.eu/eurostat/web/gisco/geodata/reference-data/administrative-units-statistical-units
 */
public interface PolygonParserInterface {

    /**
     * Returns center points of multi-polygons for a related LAU code.
     */
    public List<Point> getLauCenterPoints(String lauCode) throws PolygonParserException;

    /**
     * Returns a multi-polygon for a related LAU code.
     */
    public MultiPolygon getLauPolygon(String lauCode) throws PolygonParserException;

    /**
     * Returns center points of multi-polygons for a related NUTS code.
     */
    public List<Point> getNutsCenterPoints(String nutsCode) throws PolygonParserException;

    /**
     * Returns a multi-polygon for a related NUTS code.
     */
    public MultiPolygon getNutsPolygon(String nutsCode) throws PolygonParserException;

    /**
     * Sets source directory for polygon files. Should contain Shapefile and/or
     * geoJSON format.
     */
    public LauReaderInterface setSourceDirectory(File directory) throws PolygonParserException;
}