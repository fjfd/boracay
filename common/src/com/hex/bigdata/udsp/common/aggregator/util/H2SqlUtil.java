package com.hex.bigdata.udsp.common.aggregator.util;

import com.hex.bigdata.udsp.common.aggregator.H2Functions;

/**
 * Created by JunjieM on 2019-1-29.
 */
public class H2SqlUtil {

    private static final String H2_FUNCTION_CLASS_PATH = H2Functions.class.getName ();

    public static String createStringToDoubleFunction() {
        return "CREATE ALIAS IF NOT EXISTS f_todouble FOR \"" + H2_FUNCTION_CLASS_PATH + ".parserString2Double\"";
    }

    public static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public static String createTable(String tableName) {
        return "CREATE TABLE " + tableName + "";
    }

    public static String queryAggData(String tableName) {
        return "";
    }

    public static String tablesInfo(String tableName) {
        return "SELECT count(*) FROM INFORMATION_SCHEMA.tables WHERE table_name = upper('" + tableName + "')";
    }

    public static void main(String[] args) {
        System.out.println (H2Functions.class.getName ());
    }
}
