package com.hex.bigdata.udsp.common.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by junjiem on 2017-3-6.
 */
public class EnumTrans {

    public static DataType transDataType(String dataTypeStr) {
        DataType type = DataType.STRING;
        if (StringUtils.isNotBlank(dataTypeStr)) {
            if ("BIGINT".equals(dataTypeStr)) {
                type = DataType.BIGINT;
            } else if ("BOOLEAN".equals(dataTypeStr)) {
                type = DataType.BOOLEAN;
            } else if ("CHAR".equals(dataTypeStr)) {
                type = DataType.CHAR;
            } else if ("DECIMAL".equals(dataTypeStr)) {
                type = DataType.DECIMAL;
            } else if ("DOUBLE".equals(dataTypeStr)) {
                type = DataType.DOUBLE;
            } else if ("FLOAT".equals(dataTypeStr)) {
                type = DataType.FLOAT;
            } else if ("INT".equals(dataTypeStr)) {
                type = DataType.INT;
            } else if ("SMALLINT".equals(dataTypeStr)) {
                type = DataType.SMALLINT;
            } else if ("TIMESTAMP".equals(dataTypeStr)) {
                type = DataType.TIMESTAMP;
            } else if ("TINYINT".equals(dataTypeStr)) {
                type = DataType.TINYINT;
            } else if ("VARCHAR".equals(dataTypeStr)) {
                type = DataType.VARCHAR;
            }
        }
        return type;
    }

    public static Operator transOperator(String operatorStr) {
        Operator operator = Operator.EQ;
        if (StringUtils.isNotBlank(operatorStr)) {
            if ("!=".equals(operatorStr)) {
                operator = Operator.NE;
            } else if (">".equals(operatorStr)) {
                operator = Operator.GT;
            } else if (">=".equals(operatorStr)) {
                operator = Operator.GE;
            } else if ("<".equals(operatorStr)) {
                operator = Operator.LT;
            } else if ("<=".equals(operatorStr)) {
                operator = Operator.LE;
            } else if ("like".equals(operatorStr)) {
                operator = Operator.LK;
            } else if ("in".equals(operatorStr)) {
                operator = Operator.IN;
            } else if ("right like".equals(operatorStr)) {
                operator = Operator.RLIKE;
            }
        }
        return operator;
    }

    public static Stats transStats(String statsStr) {
        Stats stats = Stats.NONE;
        if (StringUtils.isNotBlank(statsStr)) {
            if ("AVG".equals(statsStr)) {
                stats = Stats.AVG;
            } else if ("CONCAT".equals(statsStr)) {
                stats = Stats.CONCAT;
            } else if ("COUNT".equals(statsStr)) {
                stats = Stats.COUNT;
            } else if ("MAX".equals(statsStr)) {
                stats = Stats.MAX;
            } else if ("MIN".equals(statsStr)) {
                stats = Stats.MIN;
            } else if ("SUM".equals(statsStr)) {
                stats = Stats.SUM;
            }
        }
        return stats;
    }

    public static Order transOrder(String orderStr) {
        Order order = Order.ASC;
        if (StringUtils.isNotBlank(orderStr)) {
            if ("DESC".equals(orderStr)) {
                order = Order.DESC;
            }
        }
        return order;
    }

    public static boolean transTrue(String booleanStr) {
        Boolean flg = true;
        if (StringUtils.isNotBlank(booleanStr)) {
            if ("1".equals(booleanStr)) {
                flg = false;
            }
        }
        return flg;
    }
}
