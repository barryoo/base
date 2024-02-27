package com.barry.common.core.enums;

import com.barry.common.core.exception.BusinessException;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * WeightUnitEnum
 *
 */
public enum WeightUnitEnum {
    /**
     * 国际单位
     */
    MG("Milligram", "毫克", new BigDecimal("0.000001")),
    G("Gram", "克", new BigDecimal("0.001")),
    KG("Kilogram", "千克", new BigDecimal("1")),
    T("Tonne", "吨", new BigDecimal("1000")),
    /**
     * 英制单位
     */
    OZ("Ounce", "盎司", new BigDecimal("0.028349523125")),
    LB("Pound", "磅", new BigDecimal("0.45359237")),
    ST("Stone", "英石", new BigDecimal("6.35029318")),
    LT("Long Ton", "长吨", new BigDecimal("1016.0469088")),
    STT("Short Ton", "短吨", new BigDecimal("907.18474"));

    ;
    private final String enName;
    private final String cnName;
    private final BigDecimal advanceFromKm;

    WeightUnitEnum(String enName, String cnName, BigDecimal advanceFromKm) {
        this.enName = enName;
        this.cnName = cnName;
        this.advanceFromKm = advanceFromKm;
    }

    public String getEnName() {
        return enName;
    }

    public String getCnName() {
        return cnName;
    }

    public BigDecimal getAdvanceFromKm() {
        return advanceFromKm;
    }

    @Nullable
    public static WeightUnitEnum fromString(String codeOrEnName) {
        for (WeightUnitEnum weightUnitEnum : WeightUnitEnum.values()) {
            if (weightUnitEnum.name().equalsIgnoreCase(codeOrEnName) || weightUnitEnum.getEnName().equalsIgnoreCase(codeOrEnName)) {
                return weightUnitEnum;
            }
        }
        return null;
    }

    public static BigDecimal getConvertRate(WeightUnitEnum weightUnitEnum1, WeightUnitEnum weightUnitEnum2) {
        if (weightUnitEnum1 == null || weightUnitEnum2 == null) {
            throw new BusinessException("weightUnitEnum1 or weightUnitEnum2 is null");
        }
        return weightUnitEnum1.getAdvanceFromKm().divide(weightUnitEnum2.getAdvanceFromKm(), 10, RoundingMode.HALF_UP);
    }
}
