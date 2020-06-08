package org.dice_research.opal.launuts;

import org.dice_research.opal.launuts.lau.ExcelParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExcelParserTest {
    public static ExcelParser excelParser = new ExcelParser();

    @Test
    public void testGetCodes() throws Exception {
        // confirms the number of nuts mapped
        excelParser.setLauSourceDirectory();
        assertEquals(401, excelParser.getCodes("DE").size());
    }

    @Test
    public void testCountryId() throws Exception {
        //confirms the no . of countries in the xlsx
        excelParser.setLauSourceDirectory();
        List<String> countryIds = excelParser.getCountryIds();
        assertEquals(39, countryIds.size());
    }

}