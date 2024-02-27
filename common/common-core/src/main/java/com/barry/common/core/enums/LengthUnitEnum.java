package com.barry.common.core.enums;

import com.barry.common.core.exception.BusinessException;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * LengthUnitEnum
 *
 */
public enum LengthUnitEnum {
    /**
     * 国际单位
     */
    MM("Millimeter", "毫米", new BigDecimal("0.001")),
    CM("Centimeter", "厘米", new BigDecimal("0.01")),
    DM("Decimeter", "分米", new BigDecimal("0.1")),
    M("Meter", "米", new BigDecimal("1")),
    KM("Kilometer", "千米", new BigDecimal("1000")),
    /**
     * 英制单位
     */
    IN("Inch", "英寸", new BigDecimal("0.0254")),
    FT("Foot", "英尺", new BigDecimal("0.3048")),
    YD("Yard", "码", new BigDecimal("0.9144")),
    MI("Mile", "英里", new BigDecimal("1609.344"));
    private final String enName;
    private final String cnName;
    private final BigDecimal advanceFromMeter;

    LengthUnitEnum(String enName, String cnName, BigDecimal advanceFromMeter) {
        this.enName = enName;
        this.cnName = cnName;
        this.advanceFromMeter = advanceFromMeter;
    }

    public BigDecimal getAdvanceFromMeter() {
        return advanceFromMeter;
    }

    public String getEnName() {
        return enName;
    }

    public String getCnName() {
        return cnName;
    }

    @Nullable
    public static LengthUnitEnum fromString(String codeOrEnName) {
        for (LengthUnitEnum lengthUnitEnum : LengthUnitEnum.values()) {
            if (lengthUnitEnum.name().equalsIgnoreCase(codeOrEnName) || lengthUnitEnum.getEnName().equalsIgnoreCase(codeOrEnName)) {
                return lengthUnitEnum;
            }
        }
        return null;
    }

    public static BigDecimal getConvertRate(LengthUnitEnum lengthUnitEnum1, LengthUnitEnum lengthUnitEnum2) {
        if (lengthUnitEnum1 == null || lengthUnitEnum2 == null) {
            throw new BusinessException("lengthUnitEnum1 or lengthUnitEnum2 is null");
        }
        return lengthUnitEnum1.getAdvanceFromMeter().divide(lengthUnitEnum2.getAdvanceFromMeter(), 10, RoundingMode.HALF_UP);
    }
}
