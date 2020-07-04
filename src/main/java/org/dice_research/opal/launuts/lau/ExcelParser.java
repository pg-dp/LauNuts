package org.dice_research.opal.launuts.lau;

import org.apache.jena.ext.com.google.common.collect.Iterables;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * EU-28-LAU-2019-NUTS-2016.xlsx , this file contains 41 sheets ,
 * in which 39 sheets are of different countries having information
 * like laucode, nutscode,population, total area and so on .
 * This class parses all the information using Apache POI library
 * and map them into different usable maps. It implements {@link LauReaderInterface}
 * FurtherMore , LauCsvParser {@link LauCsvParser} is no more needed, ths class is self
 * sufficient for generating the knowledge graphs.
 *
 * @author Vikrant Singh
 */


public class ExcelParser implements LauReaderInterface {

    private static final String FILE_NAME = "EU-28-LAU-2019-NUTS-2016.xlsx";

    private Workbook workbook;
    private HashMap<String, Integer> getkeys;
    private List<String> countryIds;
    private HashMap<String, HashMap<String, LauContainer>> index;
    private HashMap<String, List<String>> countryIdToNutsCodes;
    private boolean parsed = false;

    public LauReaderInterface setLauSourceDirectory(File directory) throws IOException {

        workbook = WorkbookFactory.create(new File(directory, FILE_NAME));
        return this;
    }

    @Override
    public List<String> getCountryIds() {

        if (!parsed)
            parse();

        return countryIds;
    }

    @Override
    public Map<String, List<String>> getCodes(String countryId) {
        Map<String, List<String>> getCodes = new HashMap<String, List<String>>();
        HashMap<String, LauContainer> laucodeToLauContainerMap = new HashMap<String, LauContainer>();

        if (getCountryIds().contains(countryId)) {
            if(!parsed)
                parse();
        }
        else
            return null ;

        List<String> nutCodes = countryIdToNutsCodes.get(countryId); // gets the nuts for the given country
        for (String nutCode : nutCodes) {
            laucodeToLauContainerMap = index.get(nutCode);
            List<String> lauCodes = new ArrayList<>(laucodeToLauContainerMap.keySet());
            getCodes.put(nutCode, lauCodes);
        }

        getCodes.remove(""); // removes empty key , value pair
        return getCodes;
    }

    @Override
    public LauContainer getData(String nutsCode, String lauCode) {

        LauContainer container = null;
        Map<String, LauContainer> lauCodeToContainerMap = new HashMap<String, LauContainer>();;

        if (!parsed) {
            parse();
        }

        //gets the container , according to the Map index.
        if (index.containsKey(nutsCode)) {
            lauCodeToContainerMap = index.get(nutsCode);
            container = lauCodeToContainerMap.get(lauCode);
        }

        return container;
    }

    @Override
    public HashMap<String, Integer> getKeys() {

        if (!parsed) {
            parse();
        }

        return getkeys;
    }

    private void parse() {

        String countryId;
        getkeys = new HashMap<>();
        index = new HashMap<>();
        countryIdToNutsCodes = new HashMap<>();

        //creates an easy to read map according to sheet headers
        getkeys.put("nuts3code", 0);
        getkeys.put("lauCode", 1);
        getkeys.put("lauNameNational", 2);
        getkeys.put("lauNameLatin", 3);
        getkeys.put("change", 4);
        getkeys.put("population", 5);
        getkeys.put("totalArea", 6);
        getkeys.put("degurba", 7);
        getkeys.put("degChange", 8);
        getkeys.put("coastalArea", 9);
        getkeys.put("coastalAreaChange", 10);
        getkeys.put("cityId", 11);
        getkeys.put("cityIdChange", 12);
        getkeys.put("cityName", 13);
        getkeys.put("greaterCityId", 14);
        getkeys.put("greaterCityIdChange", 15);
        getkeys.put("greaterCityName", 16);
        getkeys.put("fuaId", 17);
        getkeys.put("fuaIdChange", 18);
        getkeys.put("fuaName", 19);


        if (countryIds == null) {
            countryIds = new LinkedList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);  // gets the excel sheet
                countryId = sheet.getSheetName(); // gets the sheet name
                List<String> nutsCodes = new ArrayList<>(); // stores the nutsCodesof a single Country

                if (countryId.length() != 2) // for sheets which are not countries sheet
                    continue;

                countryIds.add(countryId); // Adds the countries into a list countryIDs

                //Adds the data to the container for creating RDF Map
                for (Row row : Iterables.skip(sheet, 1)) {

                    HashMap<String, LauContainer> laucodeToLauContainerMap = new HashMap<>();
                    LauContainer container = new LauContainer();

                    container.nuts3code = cellValues(row, "nuts3code");
                    container.lauCode = cellValues(row, "lauCode");
                    container.lauNameLatin = cellValues(row, "lauNameLatin");
                    container.lauNameNational = cellValues(row, "lauNameNational");
                    container.change = cellValues(row, "change");
                    container.population = cellValues(row, "population");
                    container.totalArea = cellValues(row, "totalArea");
                    container.degurba = cellValues(row, "degurba");
                    container.degChange = cellValues(row, "degChange");
                    container.coastalArea = cellValues(row, "coastalArea");
                    container.coastalAreaChange = cellValues(row, "coastalAreaChange");
                    container.cityId = cellValues(row, "cityId");
                    container.cityIdChange = cellValues(row, "cityIdChange");
                    container.cityName = cellValues(row, "cityName");
                    container.greaterCityId = cellValues(row, "greaterCityId");
                    container.greaterCityIdChange = cellValues(row, "greaterCityIdChange");
                    container.greaterCityName = cellValues(row, "greaterCityName");
                    container.fuaId = cellValues(row, "fuaId");
                    container.fuaIdChange = cellValues(row, "fuaIdChange");
                    container.fuaName = cellValues(row, "fuaName");


                    //fills map index (NutsCode, Map(lauCodes,LauContainer))
                    if (!index.containsKey(container.nuts3code)) {
                        laucodeToLauContainerMap.put(container.lauCode, container);
                        index.put(container.nuts3code, laucodeToLauContainerMap);
                    } else {
                        laucodeToLauContainerMap = index.get(container.nuts3code);
                        laucodeToLauContainerMap.put(container.lauCode, container);
                        index.put(container.nuts3code, laucodeToLauContainerMap);
                    }

                    nutsCodes.add(container.nuts3code);
                }

                countryIdToNutsCodes.put(countryId, nutsCodes);
            }
        }

        parsed = true;
    }

    private String cellValues(Row row, String key) {
        //Return the cell value after formatting so that empty/numeric cells are returned
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(row.getCell(getkeys.get(key)));
    }
}
