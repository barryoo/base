package com.barry.common.core.unit;

import com.barry.common.core.enums.LengthUnitEnum;
import com.barry.common.core.util.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Length
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Length {
    private BigDecimal value;
    private LengthUnitEnum unit;

    public BigDecimal convertTo(LengthUnitEnum lengthUnitEnum) {
        return value.multiply(LengthUnitEnum.getConvertRate(unit, lengthUnitEnum));
    }

    public Length(BigDecimal value, String unit) {
        this.value = value;
        LengthUnitEnum unitEnum = LengthUnitEnum.fromString(unit);
        Assert.check(unitEnum != null, "unit is not supported");
        this.unit = unitEnum;
    }
}
