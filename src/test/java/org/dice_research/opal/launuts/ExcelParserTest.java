package org.dice_research.opal.launuts;

import org.dice_research.opal.launuts.lau.ExcelParser;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/*
 * This class implements the test cases for class ExcelParser.
 * It test for the container size, Header size of input file and available nut codes.
 *
 * Using Method sorter to runs test cases sequentially to decrease the runtime.
 *
 * Author: Amit Kumar
 *
 * */

//Test Cases names are appended with a,b,c,d to serialize the execution
//the test cases as , getCodes(CountryID) has to be executed before any other test case

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExcelParserTest {
    public static ExcelParser excelParser = new ExcelParser();

    @BeforeClass
    public static void initializeFile() throws IOException {
        excelParser.setLauSourceDirectory(new File("src/main/resources/org/dice_research/opal/launuts/EU_LAUNUTS_SHEET_2019"));
    }

    @Test
    public void atestCountryId() {
        //confirms the no . of countries in the xlsx
        List<String> countryIds = excelParser.getCountryIds();
        assertEquals(39, countryIds.size());
    }

    @Test
    public void btestGetCodes() {
        // confirms the number of nuts mapped
        assertEquals(401, excelParser.getCodes("DE").size());
    }

    @Test
    public void ctestDEContainer1() {
        //Test: Checking if the container is fetching correct value
        LauContainer file_name = excelParser.getData("DEF04", "01004000");
        assertEquals("Neumünster | Neumünster, Stadt | 01004000", file_name.toString());
    }

    @Test
    public void dtestDEContainer2() {
        //Test: Checking if the container is fetching correct value
        LauContainer file_name = excelParser.getData("DE93A", "03360006");
        assertEquals("Ebstorf | Ebstorf,Klosterflecken | 03360006", file_name.toString());
    }

    @Test
    public void etestHeaderSize() {
        //Test: Checking the size of header(Number of columns) in container.

        int header_size = excelParser.getKeys().size();
        assertEquals(20, header_size);
    }

    @Test
    public void ftestContainerUK() {
        //Test: Checking if container is not empty for country code : UK
        boolean hm = excelParser.getCodes("UK").isEmpty();
        assertFalse(hm);
    }

    @Test
    public void gtestGetCodesUK() {
        //Test: Checking the number of rows available in country code DE
        assertEquals(179, excelParser.getCodes("UK").size());

    }

    @Test
    public void htestUKContainer1() {
        //Test: Checking if the container is fetching correct value
        LauContainer file_name = excelParser.getData("UKF11", "E06000015");
        assertEquals("Derby |  | E06000015", file_name.toString());
    }
}