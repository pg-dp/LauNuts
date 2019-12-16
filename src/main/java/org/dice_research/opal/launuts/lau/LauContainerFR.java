package org.dice_research.opal.launuts.lau;
import org.dice_research.opal.launuts.Vocabularies;
import java.io.Serializable;


public class LauContainerFR {

//    private static final long serialVersionUID = 1L;

    public String lauCode;
    public String lauNameLatin;
    public String lauNameNational;
    public String nuts3code;

// 
    private String getSimpleName() {
        String simpleName = lauNameNational;
        String[] parts = lauNameNational.split(",");
        if (parts.length > 1) {
            simpleName = parts[0];
        }
        return simpleName;
    }

// fetching france LAU
    public String getUri() {
        return Vocabularies.NS_LAU_FR + lauCode;
    }

    @Override
    public String toString() {
        String stringBuilder = getSimpleName() +
                " | "
                +lauNameLatin + " | "
                +lauCode +" | ";

        return stringBuilder;
    }
}