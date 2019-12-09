package org.dice_research.opal.launuts.lau;

import org.dice_research.opal.launuts.Vocabularies;

public class LauContainerUK {

    private static final long serialVersionUID = 1L;

    public String lauCode;
    public String lauNameLatin;
    public String lauNameNational;
    public String nuts3code;
    public String population ;
    public String totalArea ;
    public String cityName ;


    private String getSimpleName() {
        String simpleName = lauNameNational;
        String[] parts = lauNameNational.split(",");
        if (parts.length > 1) {
            simpleName = parts[0];
        }
        return simpleName;
    }

    public String getUri() {
        return Vocabularies.NS_LAU_UK + lauCode;
    }

    @Override
    public String toString() {
        String stringBuilder = getSimpleName() +
                " | " +lauNameLatin + " | " +lauCode +" | " +population +" | " + totalArea;

        return stringBuilder;
    }
}
