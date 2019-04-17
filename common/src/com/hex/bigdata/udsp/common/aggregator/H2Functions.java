package com.hex.bigdata.udsp.common.aggregator;

/**
 * 自定义函数
 */
public class H2Functions {

    /**
     * String转Double
     *
     * @param str
     * @return
     */
    public static double parserString2Double(String str) {
        if (null == str) {
            return 0f;
        }
        try {
            return Double.parseDouble (str);
        } catch (Exception e) {
            return 0f;
        }
    }
}
