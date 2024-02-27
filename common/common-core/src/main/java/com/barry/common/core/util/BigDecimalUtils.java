package com.barry.common.core.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * BigDecimalUtil
 */
public class BigDecimalUtils {
    /**
     * 默认除法运算精度
     * 参与计算的过程数据默认保留10位精度
     */
    private static final int DEF_DIV_SCALE = 10;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * 这个类不能实例化
     */
    private BigDecimalUtils() {
    }

    /**
     * is null
     * @param v bigDecimal
     * @return result
     */
    public static boolean isNull(BigDecimal v) {
        return v == null;
    }

    /**
     * is equals zero
     * @param v bigDecimal
     * @return result
     */
    public static boolean eqZero(BigDecimal v) {
        return v.compareTo(ZERO) == 0;
    }

    /**
     * is less than zero
     * @param v bigDecimal
     * @return result
     */
    public static boolean ltZero(BigDecimal v) {
        return v.compareTo(ZERO) < 0;
    }

    /**
     * is less equals than zero
     * @param v bigDecimal
     * @return result
     */
    public static boolean leZero(BigDecimal v) {
        return v.compareTo(ZERO) <= 0;
    }

    /**
     * is greater than zero
     * @param v bigDecimal
     * @return result
     */
    public static boolean gtZero(BigDecimal v) {
        return v.compareTo(ZERO) > 0;
    }

    /**
     * is greater equals than zero
     * @param v bigDecimal
     * @return result
     */
    public static boolean geZero(BigDecimal v) {
        return v.compareTo(ZERO) >= 0;
    }

    /**
     * v1 > v2
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static boolean gt(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == 1;
    }

    /**
     * v1 >= v2
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static boolean ge(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == 1 ||  v1.compareTo(v2) == 0;
    }


    /**
     * v1 < v2
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static boolean lt(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == -1;
    }

    /**
     * v1 <= v2
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static boolean le(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == -1 ||  v1.compareTo(v2) == 0;
    }

    /**
     * v1 = v2
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static boolean eq(BigDecimal v1, BigDecimal v2) {
        if (v1 == null || v2 == null) {
            return false;
        }
        return v1.compareTo(v2) == 0;
    }

    /**
     * min
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static BigDecimal min(BigDecimal v1, BigDecimal v2) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.min(v2);
    }

    /**
     * max
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static BigDecimal max(BigDecimal v1, BigDecimal v2) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.max(v2);
    }

    /**
     * 提供精确的加法运算。.
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.add(v2);
    }

    /**
     * 提供精确的减法运算。.
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.subtract(v2);
    }

    /**
     * 提供精确的乘法运算。.
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.multiply(v2);
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。.
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。.
     * 默认策略为HALF_EVEN
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        return v1.divide(v2, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 对两个数做取余数运算, 默认精度为小数点后10位
     *
     * @param v1 v1
     * @param v2 v2
     * @return result
     */
    public static BigDecimal rem(BigDecimal v1, BigDecimal v2) {
        return rem(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 对两个数做取余数运算
     * 默认策略为HALF_EVEN
     *
     * @param v1 v1
     * @param v2 v2
     * @param scale 小数点后保留几位
     * @return result
     */
    public static BigDecimal rem(BigDecimal v1, BigDecimal v2, int scale) {
        if (isNull(v1) || isNull(v2)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        MathContext mc = new MathContext(scale, RoundingMode.HALF_EVEN);
        return v1.remainder(v2, mc);
    }


    /**
     * 提供精确的小数位四舍五入处理。.
     * 默认策略为HALF_EVEN
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static BigDecimal round(BigDecimal v, int scale) {
        if (isNull(v)) {
            throw new IllegalArgumentException("Argument is null.");
        }
        MathContext mc = new MathContext(scale, RoundingMode.HALF_EVEN);
        return v.round(mc);
    }
}
