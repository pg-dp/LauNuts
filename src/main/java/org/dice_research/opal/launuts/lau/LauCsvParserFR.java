package org.dice_research.opal.launuts.lau;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class LauCsvParserFR
{
    private final static String HEADER_LAUCODE = "LAU CODE";
    private final static String HEADER_LAUNAMENATIONAL = "LAU NAME NATIONAL";
    private final static String HEADER_NUTS3CODE = "NUTS 3 CODE";
    
    private List<LauContainerFR> lauList = new LinkedList<>();

    private CSVFormat getCsvFormat() {
        return CSVFormat.EXCEL.withHeader();
    }

// CSV parser for fetching     
    public LauCsvParserFR parse(String file) throws IOException {
    	
//    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            while ((line = br.readLine()) != null) {
//                // use comma as separator
//                String[] country = line.split(cvsSplitBy);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } 	
//    	
        Reader reader = new FileReader(file);
        Iterable<CSVRecord> records = getCsvFormat().parse(reader);
        for (CSVRecord record : records) {
            LauContainerFR container = new LauContainerFR();
            container.lauCode = record.get(HEADER_LAUCODE);
            container.lauNameNational = record.get(HEADER_LAUNAMENATIONAL);
            container.nuts3code = record.get(HEADER_NUTS3CODE);
            lauList.add(container);
        }
        return this;
    }

    public List<LauContainerFR> getLauList()
    {
        return lauList;
    }

    public static Map<String, LauContainerFR> createLauCodeToContainer
            (Collection<LauContainerFR> lauContainerFRList)
    {
        Map<String, LauContainerFR> map = new HashMap<>();
        for (LauContainerFR container : lauContainerFRList) {
            map.put(container.lauCode, container);
        }
        return map;
    }
}