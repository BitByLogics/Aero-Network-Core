package net.aeronetwork.core.util.nms;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtil {

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
    public static final String NMS_VERSION = "net.minecraft.server." + VERSION + ".";
    public static final String CRAFT_BUKKIT_VERSION = "org.bukkit.craftbukkit." + VERSION + ".";

    // Prevents initialization
    private NMSUtil() {
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?> getNMSClass(String name) {
        return getClass(NMS_VERSION + name);
    }

    public static Class<?> wrapperToPrimitive(Class<?> clazz) {
        if(clazz == Boolean.class) return boolean.class;
        if(clazz == Integer.class) return int.class;
        if(clazz == Double.class) return double.class;
        if(clazz == Float.class) return float.class;
        if(clazz == Long.class) return long.class;
        if(clazz == Short.class) return short.class;
        if(clazz == Byte.class) return byte.class;
        if(clazz == Void.class) return void.class;
        if(clazz == Character.class) return char.class;
        return clazz;
    }

    public static Class<?>[] toParamTypes(Object... params) {
        Class<?>[] classes = new Class<?>[params.length];
        for(int i = 0; i < params.length; i++)
            classes[i] = wrapperToPrimitive(params[i].getClass());
        return classes;
    }

    public static Object callDeclaredMethod(Object object, String method, Object... params) {
        try {
            Method m = object.getClass().getDeclaredMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(object, params);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object callMethod(Object object, String method, Object... params) {
        try {
            Method m = object.getClass().getMethod(method, toParamTypes(params));
            m.setAccessible(true);
            return m.invoke(object, params);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object callDeclaredConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getDeclaredConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object callConstructor(Class<?> clazz, Object... params) {
        try {
            Constructor<?> con = clazz.getConstructor(toParamTypes(params));
            con.setAccessible(true);
            return con.newInstance(params);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getDeclaredField(Object object, String field) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object getField(Object object, String field) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void setDeclaredField(Object object, String field, Object value) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void setField(Object object, String field, Object value) {
        try {
            Field f = object.getClass().getField(field);
            f.setAccessible(true);
            f.set(object, value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
