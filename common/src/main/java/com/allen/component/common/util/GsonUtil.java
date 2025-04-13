package com.hedgie.service.common;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 用户资产变更记录
 * @author : houchen
 * @since : 2023/3/20
 */
public class GsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                String datetime = json.getAsJsonPrimitive().getAsString();
                return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            })
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
                String datetime = json.getAsJsonPrimitive().getAsString();
                return LocalDate.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            })
            .create();

    public static String toJson(Object obj) {
        String result = GSON.toJson(obj);
        if (result.equals("null")) {
            return null;
        }
        return result;
    }

    public static <T> T toObject(String json, Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }

    public static <T> List<T> toList(String json, Class<T> tClass) {
        return GSON.fromJson(json, TypeToken.getParameterized(List.class, tClass).getType());
    }

    public static List<Map<String, Object>> toObject(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T toObjectByType(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static Map<String,Object> toMap(String json){
        return GSON.fromJson(json, new TypeToken<Map<String,Object>>(){}.getType());
    }

    public static Map<String,Object> toMap(Object obj){
        String json = toJson(obj);
        return toMap(json);
    }

    public static <T> T deepCopy(Object obj, Class<T> tClass){
        String json = toJson(obj);
        return toObject(json, tClass);
    }
}
