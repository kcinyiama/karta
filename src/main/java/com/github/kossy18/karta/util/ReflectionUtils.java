/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Only intended for internal use.
 */
public abstract class ReflectionUtils {

    public static Class getClassObject(String name) throws ClassNotFoundException {
        AssertUtils.notNull(name);
        return Class.forName(name);
    }

    public static boolean isFieldExist(Class clazz, String name) {
        try {
            clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return false;
        }
        return true;
    }

    public static boolean isInterfaceOf(Class clazz, String interfaceName) throws ClassNotFoundException {
        boolean found = false;
        for (Class anInterface : clazz.getInterfaces()) {
            if (anInterface.getName().equals(interfaceName)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static Map<String, Class<?>[]> buildMethodMap(Class clazz, String startsWith) {
        Map<String, Class<?>[]> methodMap = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith(startsWith)) {
                methodMap.put(method.getName(), method.getParameterTypes());
            }
        }
        return methodMap;
    }

    public static <T> void findAndInvokeMethod(Class clazz, T classEntity, String fieldName, String methodPrefix, Class<?>[] parameterTypes, Object... methodArg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getMethod(clazz, fieldName, methodPrefix, parameterTypes);
        method.invoke(classEntity, methodArg);
    }

    private static Method getMethod(Class clazz, String fieldName, String methodPrefix, Class<?>[] parameterTypes) throws NoSuchMethodException {
        Method method;
        try {
            method = clazz.getMethod(getCamelCaseName(methodPrefix, fieldName), parameterTypes);
        } catch (NoSuchMethodException e) {
            method = clazz.getMethod(getSnakeCaseName(methodPrefix, fieldName), parameterTypes);
        }
        return method;
    }

    public static String getCamelCaseName(String prefix, String fieldName) {
        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String getSnakeCaseName(String prefix, String fieldName) {
        return prefix + "_" + fieldName;
    }
}
