package com.hex.bigdata.udsp.common.util;

/**
 * Created by JunjieM on 2018-9-4.
 */
public class IpAddressUtil {

    /**
     * 检查ip段表达式的合法性
     * 支持以下几种模式：
     * 1、正常的ip，如10.1.97.1
     * 2、以星号*代替0-255之间的任意数字，如10.1.97.*
     * 3、如10.1.97.[10-30]、10.1.97.[1-5,6-20]
     *
     * @param ipSection
     * @return
     */
    public static boolean isIpSection(String ipSection) {
        String[] array = ipSection.split("\\.");
        if (array.length != 4) {
            return false;
        }
        for (String item : array) {
            if (item.startsWith("[") && item.endsWith("]")) {
                if (!isIpPart(item)) {
                    return false;
                }
            } else if (item.equals("*") || isIpInt(item)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查区间表达式格式如[10-30]、[1-5,6-20]、[0,1-10]是否合法
     *
     * @return
     */
    private static boolean isIpPart(String ipPart) {
        ipPart = ipPart.substring(1, ipPart.length() - 1);
        if (ipPart.length() == 0) {
            return false;
        }
        String[] itemArray = ipPart.split(",");
        if (itemArray.length == 0) {
            return false;
        }
        for (String subStr : itemArray) {
            String[] subArray = subStr.split("-");
            if (subArray.length > 2 || subArray.length < 1) {
                return false;
            }
            if (subArray.length == 1) {
                if (!isIpInt(subArray[0])) {
                    return false;
                }
            } else {
                if (!isIpInt(subArray[0]) || !isIpInt(subArray[1])) {
                    return false;
                }
                if (Integer.valueOf(subArray[0]) >= Integer.valueOf(subArray[1])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断IP是否与IP区间表达式匹配
     *
     * @param ip
     * @param ipSection
     * @return
     */
    public static boolean isIpAddress(String ip, String ipSection) {
        String[] ipItems = ip.split("\\.");
        String[] ipSectionItems = ipSection.split("\\.");
        for (int i = 0; i < 4; i++) {
            if (ipSectionItems[i].startsWith("[") && ipSectionItems[i].endsWith("]")) {
                if (!isIpItem(ipItems[i], ipSectionItems[i])) {
                    return false;
                }
            } else if (ipSectionItems[i].equals("*")) {
                continue;
            } else if (isIpInt(ipSectionItems[i])) {
                if (!ipItems[i].equals(ipSectionItems[i])) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断数字是否属于IP整数
     *
     * @param ipPart
     * @return
     */
    private static boolean isIpInt(String ipPart) {
        try {
            Integer ipNumber = Integer.valueOf(ipPart);
            if (ipNumber != null && ipNumber >= 0 && ipNumber <= 255) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 检查区间表达式与ip对应的数字是否匹配
     *
     * @param ipPart
     * @param ipSectionPart
     * @return
     */
    private static boolean isIpItem(String ipPart, String ipSectionPart) {
        int ipNumber = Integer.valueOf(ipPart);
        ipSectionPart = ipSectionPart.substring(1, ipSectionPart.length() - 1);
        if (ipSectionPart.length() == 0) {
            return false;
        }
        String[] ipItemArray = ipSectionPart.split(",");
        if (ipItemArray.length == 0) {
            return false;
        }
        for (String ipItem : ipItemArray) {
            String[] array = ipItem.split("-");
            if (array.length > 2 || array.length < 1) {
                continue;
            }
            if (array.length == 1) {
                if (isIpInt(array[0]) && ipNumber == Integer.valueOf(array[0])) {
                    return true;
                }
            } else {
                if (isIpInt(array[0]) && isIpInt(array[1])
                        && ipNumber >= Integer.valueOf(array[0]) && ipNumber <= Integer.valueOf(array[1])) {
                    return true;
                }
            }
        }
        return false;
    }
}
