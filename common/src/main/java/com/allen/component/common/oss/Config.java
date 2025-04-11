package com.allen.component.common.oss;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Config {
    public static final String OPTION_VISIBILITY = "visibility";
    public static final String OPTION_DIRECTORY_VISIBILITY = "directory_visibility";

    private final Map<String, Object> options;

    public Config() {
        this.options = new HashMap<>();
    }

    public Config(Map<String, Object> options) {
        this.options = new HashMap<>(options);
    }

    public <T> T get(String property, T defaultValue) {
        return (T) options.getOrDefault(property, defaultValue);
    }

    public Config extend(Map<String, Object> options) {
        Map<String, Object> mergedOptions = new HashMap<>(this.options);
        mergedOptions.putAll(options);
        return new Config(mergedOptions);
    }

    public Config withDefaults(Map<String, Object> defaults) {
        BiFunction<Object, Object, Object> mergeFn = (value1, value2) -> value1 != null ? value1 : value2;
        Map<String, Object> mergedOptions = new HashMap<>(defaults);
        options.forEach((key, value) -> mergedOptions.merge(key, value, mergeFn));
        return new Config(mergedOptions);
    }

}
