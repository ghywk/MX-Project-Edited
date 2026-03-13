package kireiko.dev.anticheat.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            return clazz.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            Method method = obj.getClass().getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            Method method = clazz.getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object newInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Object getObject(String packages, Class<?> clazz, String path) {
        try {
            Class<?> target = Class.forName(packages + clazz.getName());
            Field field = target.getDeclaredField(path);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            return null;
        }
    }
}