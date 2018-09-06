package com.hex.bigdata.udsp.common.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by PC on 2017/4/21.
 */
public class ObjectUtil {

    /**
     * 拷贝对象
     *
     * @param obj1
     * @param obj2
     */
    public static void copyObject(Object obj1, Object obj2) {
        Field[] fields1 = obj1.getClass().getDeclaredFields();
        Field[] fields2 = obj2.getClass().getDeclaredFields();
        if (fields1 != null && fields2 != null) {
            for (Field field1 : fields1) {
                try {
                    String name1 = field1.getName();
                    PropertyDescriptor pd1 = new PropertyDescriptor(name1, obj1.getClass());
                    Method getMethod = pd1.getReadMethod(); // 获得get方法
                    Object obj = getMethod.invoke(obj1); // 执行get方法返回一个Object
                    for (Field field2 : fields2) {
                        String name2 = field2.getName();
                        if (name1.equals(name2)) {
                            PropertyDescriptor pd2 = new PropertyDescriptor(name2, obj2.getClass());
                            Method setMethod = pd2.getWriteMethod(); // 获得set方法
                            setMethod.invoke(obj2, obj); // 执行set方法将Object的值放入
                        }
                    }
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 实例化对象
     *
     * @param implClass
     * @return
     */
    public static Object newInstance(String implClass) {
        Object obj = null;
        try {
            obj = Class.forName(implClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
