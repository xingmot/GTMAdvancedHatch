package com.xingmot.gtmadvancedhatch.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jetbrains.annotations.Nullable;

/**
 * @author qiuyeqaq
 */
// code by github qiuye2024github GTL_Extend
@SuppressWarnings("UnnecessaryUnicodeEscape")
public class FormattingUtil {

    public static final DecimalFormat DECIMAL_FORMAT_1F = new DecimalFormat("#,##0.0");
    public static final DecimalFormat DECIMAL_FORMAT_2F = new DecimalFormat("#,##0.00");
    public static final DecimalFormat DECIMAL_FORMAT_SIC_2F = new DecimalFormat("0.00E00");

    public static String formatNumberReadable(BigDecimal number) {
        return formatNumberReadable(number, false);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli) {
        return formatNumberReadable(number, milli, DECIMAL_FORMAT_1F, null);
    }

    public static String formatNumberReadable(BigDecimal number, boolean milli, NumberFormat fmt, @Nullable String unit) {
        StringBuilder sb = new StringBuilder();
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal oneThousand = new BigDecimal(1000);
        if (number.compareTo(zero) < 0) {
            number = number.abs();
            sb.append('-');
        } else {
            sb.append('+');
        }

        if (milli && number.compareTo(oneThousand) >= 0) {
            milli = false;
            number = number.divide(oneThousand, MathContext.DECIMAL128);
        }

        int exp = 0;
        if (number.compareTo(oneThousand) >= 0) {
            exp = (int) (Math.log10(number.doubleValue()) / 3);
            if (exp > 7) exp = 7;
            if (exp > 0) number = number.divide(BigDecimal.valueOf(Math.pow(1000, exp)), MathContext.DECIMAL128);
        }

        sb.append(fmt.format(number));
        if (exp > 0) sb.append("KMGTPEZ".charAt(exp - 1));
        else if (milli && number.compareTo(zero) != 0) sb.append('m');

        if (unit != null) sb.append(unit);
        return sb.toString();
    }
}
