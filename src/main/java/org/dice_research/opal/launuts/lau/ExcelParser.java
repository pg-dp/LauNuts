package org.dice_research.opal.launuts.lau;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExcelParser implements LauReaderInterface {
    Workbook workbook = null;
    Sheet sheet = null;
    List<LauContainer> lauContainerList = new LinkedList<LauContainer>();
    Map<String, List<String>> getCodes = new HashMap<>();

    @Override
    public LauReaderInterface setLauSourceDirectory(File directory) throws LauReaderException, IOException {
        workbook = WorkbookFactory.create(directory);
        return this;
    }

    @Override
    public List<String> getCountryIds() throws LauReaderException {

        List<String> countryIds = new ArrayList<String>();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            countryIds.add(workbook.getSheetAt(i).getSheetName());
        }

        return countryIds;
    }

    @Override
    public Map<String, List<String>> getCodes(String countryId) throws LauReaderException {

        String nutsCode = null;
        List<String> lauCodes = new ArrayList<>();
        LauContainer container = new LauContainer();


        if (getCountryIds().contains(countryId)) {
            sheet = workbook.getSheet(countryId);

            Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                for (int cellIndex = 0; cellIndex <= 3; cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    if (cell != null) {
                        String cellValue = cell.getStringCellValue();
                        if (cellIndex == 0) {
                            container.nuts3code = cellValue;
                            if (!cellValue.equals(nutsCode)) {
                                if (nutsCode != null) {
                                    getCodes.put(nutsCode, lauCodes);
                                }
                                nutsCode = cellValue;
                                lauCodes = new ArrayList<>();
                            }
                        } else if (cellIndex == 1) {
                            lauCodes.add(cellValue);
                            container.lauCode = cellValue;
                        } else if (cellIndex == 2) {
                            container.lauNameNational = cellValue;
                        } else if (cellIndex == 3) {
                            container.lauNameLatin = cellValue;
                        }
                    }
                    lauContainerList.add(container);
                }
            }
        }

        System.out.println(getCodes);
        return getCodes;
    }

    @Override
    public LauContainer getData(String nutsCode, String lauCode) throws LauReaderException {
        for (LauContainer container : lauContainerList) {
            if (nutsCode.equals(container.nuts3code) && lauCode.equals(container.lauCode))
                return container;
        }

        return null;
    }

    @Override
    public List<String> getKeys() throws LauReaderException {

        List<String> getKeys = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        Row row = sheet.getRow(0);
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = dataFormatter.formatCellValue(cell);
            getKeys.add(cellValue);
        }

        return getKeys;
    }
}
