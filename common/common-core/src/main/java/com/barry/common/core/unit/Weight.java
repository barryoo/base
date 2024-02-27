package com.barry.common.core.unit;

import com.barry.common.core.enums.WeightUnitEnum;
import com.barry.common.core.util.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Weight
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weight {
    private BigDecimal value;
    private WeightUnitEnum unit;

    public Weight(BigDecimal value, String unit) {
        this.value = value;
        WeightUnitEnum unitEnum = WeightUnitEnum.fromString(unit);
        Assert.check(unitEnum != null, "unit is not supported");
        this.unit = unitEnum;
    }

    public BigDecimal convertTo(WeightUnitEnum weightUnitEnum) {
        return value.multiply(WeightUnitEnum.getConvertRate(unit, weightUnitEnum));
    }
}
