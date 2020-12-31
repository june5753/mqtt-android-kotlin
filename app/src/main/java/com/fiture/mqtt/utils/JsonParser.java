package com.fiture.mqtt.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonParser {
    private final Gson mGson;

    private JsonParser() {
        mGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    private static final class Holder {
        private static final JsonParser PARSER = new JsonParser();
    }

    public static JsonParser getParser() {
        return Holder.PARSER;
    }

    public String toJson(Object object) {
        try {
            return mGson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toJson(Object object, Type type) {
        try {
            return mGson.toJson(object, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T fromJson(String json, Class<T> clz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            if (String.class.equals(clz)) {
                return (T) json;
            }
            if (Void.class.equals(clz)) {
                return null;
            }
            if (Boolean.class.equals(clz) || boolean.class.equals(clz)) {
                return (T) Boolean.valueOf(json);
            }
            if (Integer.class.equals(clz) || int.class.equals(clz)) {
                return (T) Integer.valueOf(json);
            }
            if (Long.class.equals(clz) || long.class.equals(clz)) {
                return (T) Long.valueOf(json);
            }
            if (Float.class.equals(clz) || float.class.equals(clz)) {
                return (T) Float.valueOf(json);
            }
            if (Double.class.equals(clz) || double.class.equals(clz)) {
                return (T) Double.valueOf(json);
            }
            return mGson.fromJson(json, clz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T fromJson(String json, Type type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            if (String.class.equals(type)) {
                return (T) json;
            }
            if (Void.class.equals(type)) {
                return null;
            }
            if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                return (T) Boolean.valueOf(json);
            }
            if (Integer.class.equals(type) || int.class.equals(type)) {
                return (T) Integer.valueOf(json);
            }
            if (Long.class.equals(type) || long.class.equals(type)) {
                return (T) Long.valueOf(json);
            }
            if (Float.class.equals(type) || float.class.equals(type)) {
                return (T) Float.valueOf(json);
            }
            if (Double.class.equals(type) || double.class.equals(type)) {
                return (T) Double.valueOf(json);
            }
            return mGson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
