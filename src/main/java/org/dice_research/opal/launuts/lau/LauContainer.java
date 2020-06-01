package org.dice_research.opal.launuts.lau;

import org.dice_research.opal.launuts.Vocabularies;

import java.io.Serializable;

/**
 * Container for LAU data. Used in {@link LauCsvParser}.
 *
 * @author Adrian Wilke
 */
public class LauContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    public String lauCode;
    public String lauNameLatin;
    public String lauNameNational;
    public String nuts3code;
    public String change;
    public String population;
    public String totalArea;
    public String degubra;
    public String degChange;
    public String coastalArea;
    public String coastalAreaChange;
    public String cityId;
    public String cityIdChange;
    public String cityName;
    public String greaterCityId;
    public String greaterCityIdChange;
    public String greaterCityName;
    public String fuaId;
    public String fuaIdChange;
    public String fuaName;

    public String getSimpleName() {
        String simpleName = lauNameNational;
        String[] parts = lauNameNational.split(",");
        if (parts.length > 1) {
            simpleName = parts[0];
        }
        return simpleName;
    }

    public String getCountryName() {
        return "";
    }

    public String getUri() {
        return Vocabularies.NS_LAU_DE + lauCode;
    }

    public String getLauNameLatin() {
        return lauNameLatin;
    }

    public String getLauCode() {
        return lauCode;
    }

    public String getLauNameNational() {
        return lauNameNational;
    }

    public String getNuts3code() {
        return nuts3code;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSimpleName());
        stringBuilder.append(" | ");
        stringBuilder.append(lauNameLatin);
        stringBuilder.append(" | ");
        stringBuilder.append(lauCode);
        return stringBuilder.toString();
    }
}