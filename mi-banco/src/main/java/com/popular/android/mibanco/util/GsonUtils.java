package com.popular.android.mibanco.util;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.popular.android.mibanco.App;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Utilities class to centralize all aspects related to GSON library management
 */
public final class GsonUtils {

    private static Gson gsonInstance;
    private static Gson gsonExcludeInstance;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateSerializer());
        builder.registerTypeAdapter(Uri.class, new GsonUriSerializer());
        gsonInstance = builder.create();

        builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateSerializer());
        builder.registerTypeAdapter(Uri.class, new GsonUriSerializer());
        gsonExcludeInstance = builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    private GsonUtils() {
    }

    public static Gson getGsonInstance() {
        return gsonInstance;
    }

    public static Gson getGsonExcludeInstance() {
        return gsonExcludeInstance;
    }

    public static String toJson(Object src) {
        return toJson(gsonInstance, src);
    }

    public static String toJson(Gson instance, Object src) {
        String parsedJson = null;
        try {
            parsedJson = instance.toJson(src);
        } catch (Exception e) {
            App.submitException(e);
        }

        return parsedJson;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return fromJson(gsonInstance, json, classOfT);
    }

    public static <T> T fromJson(Gson instance, String json, Class<T> classOfT) {
        T resultObject = null;
        try {
            resultObject = instance.fromJson(json, classOfT);
        } catch (Exception e) {
            App.submitException(e);
        }
        return resultObject;
    }

    public static <T> T fromJson(Gson instance, String json, Type type) {
        T resultObject = null;
        try {
            resultObject = instance.fromJson(json, type);
        } catch (Exception e) {
            App.submitException(e);
        }
        return resultObject;
    }

    public static <T> T fromJson(JsonReader jsonReader, Class<T> classOfT) {
        return fromJson(gsonInstance, jsonReader, classOfT);
    }

    public static <T> T fromJson(Gson instance, JsonReader jsonReader, Class<T> classOfT) {
        T resultObject = null;
        try {
            resultObject = instance.fromJson(jsonReader, classOfT);
        } catch (Exception e) {
            App.submitException(e);
        }
        return resultObject;
    }

    private static class GsonDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsLong() * 1000);
        }

        @Override
        public JsonElement serialize(final Date src, final Type typeOfSrc, final JsonSerializationContext context) {
            return new JsonPrimitive(String.valueOf(src.getTime() / 1000));
        }
    }

    /**
     * Class to manage json serialization
     */
    public static class GsonUriSerializer implements JsonSerializer<Uri>, JsonDeserializer<Uri> {

        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Uri deserialize(final JsonElement src, final Type srcType, final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }
}
