package org.dice_research.opal.launuts.lau;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExcelParser implements LauReaderInterface {

    public static final String FILE_NAME = "EU-28-LAU-2019-NUTS-2016.xlsx";
    Workbook workbook = null;
    Sheet sheet = null;
    List<LauContainer> lauContainerList = new LinkedList<LauContainer>();
    Map<String, List<String>> getCodes = new HashMap<>();
    HashMap<String, Integer> getkeys = new HashMap<String, Integer>();
    HashMap<String, LauContainer> laucodeToLauContainerMap = new HashMap<String, LauContainer>();
    HashMap<String, HashMap<String, LauContainer>> index;
    private List<String> countryIds;
    private boolean parsed = false;

    @Override
    public LauReaderInterface setLauSourceDirectory() throws IOException {

        workbook = WorkbookFactory.create(new File(FILE_NAME));
        return this;
    }

    @Override
    public List<String> getCountryIds() throws LauReaderException {

        if (!parsed)
            parse();

        return countryIds;
    }

    @Override
    public Map<String, List<String>> getCodes(String countryId) throws LauReaderException {

        if (getCountryIds().contains(countryId)) {
            sheet = workbook.getSheet(countryId);

            if (!parsed) {
                parse();
            }
        }

        return getCodes;

    }

    @Override
    public LauContainer getData(String nutsCode, String lauCode) throws LauReaderException {
        LauContainer container = null;

        if (!parsed) {
            parse();
        }

        Map<String, LauContainer> lauCodeToContainerMap;
        if (index.containsKey(nutsCode)) {
            lauCodeToContainerMap = index.get(nutsCode);
            container = lauCodeToContainerMap.get(lauCode);
        }

        return container;
    }

    @Override
    public HashMap<String, Integer> getKeys() throws LauReaderException {

        if (!parsed) {
            parse();
        }

        return getkeys;
    }

    private void parse() throws LauReaderException {

        String countryId;

        if (countryIds == null) {
            countryIds = new LinkedList<String>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                countryId = workbook.getSheetAt(i).getSheetName();
                if (countryId.length() == 2)
                    countryIds.add(countryId);
            }

            return;
        }


        //creates the easy to read map according to sheet headers
        getkeys.put("nuts3code", 0);
        getkeys.put("lauCode", 1);
        getkeys.put("lauNameNational", 2);
        getkeys.put("lauNameLatin", 3);
        getkeys.put("change", 4);
        getkeys.put("population", 5);
        getkeys.put("totalArea", 6);
        getkeys.put("degubra", 7);
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

        //TODO create J-Unit to check for the uniformity of the headers


        String nutsCode = null;
        String lauCode;
        List<String> lauCodes = new ArrayList<>();

        // creates the map getCodes which is  map<nutsCode ,list<laucodes>>
        Iterator<Row> rowIterator1 = sheet.rowIterator();
        while (rowIterator1.hasNext()) {
            Row row = rowIterator1.next();
            for (int cellIndex = 0; cellIndex <= 1; cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue();
                    if (cellIndex == 0) {
                        if (!cellValue.equals(nutsCode)) {
                            if (nutsCode != null) {
                                getCodes.put(nutsCode, lauCodes);
                            }
                            nutsCode = cellValue;
                            lauCodes = new ArrayList<>();
                        }
                    } else {
                        lauCodes.add(cellValue);
                    }
                }
            }
        }

//        for(Row row : sheet) {
//            if(row.getCell(0).getStringCellValue() != nutsCode) {
//                nutsCode = row.getCell(0).getStringCellValue();
//                lauCodes = new ArrayList<>();
//            }
//            lauCode = row.getCell(1).getStringCellValue();
//
//            lauCodes.add(lauCode);
//
//        }

        index = new HashMap<String, HashMap<String, LauContainer>>();

        for (Row row : sheet) {
            LauContainer container = new LauContainer();

            container.nuts3code = cellValues(row,"nuts3code");
            container.lauCode = cellValues(row,"lauCode");
            container.lauNameLatin = cellValues(row,"lauNameLatin");;
            container.lauNameNational = cellValues(row,"lauNameNational");
            container.change = cellValues(row,"change");
            container.population = cellValues(row,"population");
            container.population = cellValues(row,"totalArea");
            container.degubra = cellValues(row,"degubra");
            container.degChange = cellValues(row,"degChange");
            container.coastalArea = cellValues(row,"coastalArea");
            container.coastalAreaChange = cellValues(row,"coastalAreaChange");
            container.cityId = cellValues(row,"cityId");
            container.cityIdChange = cellValues(row,"cityIdChange");
            container.cityName = cellValues(row,"cityName");;
            container.greaterCityId = cellValues(row,"greaterCityId");
            container.greaterCityIdChange = cellValues(row,"greaterCityIdChange");
            container.greaterCityName = cellValues(row,"greaterCityName");
            container.fuaId = cellValues(row,"fuaId");
            container.fuaIdChange = cellValues(row,"fuaIdChange");
            container.fuaName = cellValues(row,"fuaName");

            lauContainerList.add(container);

            laucodeToLauContainerMap.put(container.lauCode, container);
            index.put(container.nuts3code, laucodeToLauContainerMap);

        }

        parsed = true;

    }

    public String cellValues(Row row , String key) {
        
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(row.getCell(getkeys.get(key))) ;
    }
}
