package org.dice_research.opal.launuts.lau;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class LauCsvParserUK
{

    private final static String HEADER_LAUCODE = "LAU CODE";
    private final static String HEADER_LAUNAMENATIONAL = "LAU NAME NATIONAL";
    private final static String HEADER_NUTS3CODE = "NUTS 3 CODE";
    private final static String HEADER_POPULATION = "POPULATION";
    private final static String HEADER_TOTALAREA = "TOTAL AREA (m2)";
    private final static String HEADER_DEGURBA = "DEGURBA";
    private final static String HEADER_CITYID = "CITY_ID";
    private final static String HEADER_CITYNAME = "CITY_NAME";

    private List<LauContainerUK> lauList = new LinkedList<>();

    private CSVFormat getCsvFormat() {
        return CSVFormat.EXCEL.withHeader();
    }

    public LauCsvParserUK parse(String file) throws IOException {
        Reader reader = new FileReader(file);
        Iterable<CSVRecord> records = getCsvFormat().parse(reader);
        for (CSVRecord record : records) {
            LauContainerUK container = new LauContainerUK();
            container.lauCode = record.get(HEADER_LAUCODE);
            container.lauNameNational = record.get(HEADER_LAUNAMENATIONAL);
            container.nuts3code = record.get(HEADER_NUTS3CODE);
            container.cityName = record.get(HEADER_CITYNAME);
            container.population = record.get(HEADER_POPULATION);
            container.totalArea = record.get(HEADER_TOTALAREA);

            lauList.add(container);
        }
        return this;
    }

    public List<LauContainerUK> getLauList()
    {
        return lauList;
    }

    public static Map<String, LauContainerUK> createLauCodeToContainer
            (Collection<LauContainerUK> lauContainerUKList)
    {
        Map<String, LauContainerUK> map = new HashMap<>();
        for (LauContainerUK container : lauContainerUKList) {
            map.put(container.lauCode, container);
        }
        return map;
    }
}
