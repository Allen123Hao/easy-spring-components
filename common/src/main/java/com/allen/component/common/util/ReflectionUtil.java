package com.allen.component.common.util;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Allen
 * @version 1.0
 * @since 2024/4/22 15:29
 */
public class ReflectionUtil {

    /**
     * 通过反射，根据字段路径获取对象的嵌套字段值。
     *
     * @param object 对象实例
     * @param fieldPath 字段路径，如 "course.courseUuid"
     * @return 字段值
     * @throws ReflectiveOperationException 如果反射操作失败
     */
    public static Object getNestedFieldValue(Object object, String fieldPath) throws ReflectiveOperationException {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (fieldPath == null || fieldPath.isEmpty()) {
            throw new IllegalArgumentException("Field path cannot be null or empty");
        }

        // 分割字段路径
        String[] fields = fieldPath.split("\\.");
        Object currentValue = object;

        // 遍历字段路径
        for (String field : fields) {
            if (currentValue instanceof Map) {
                // 如果当前值是Map，使用Map的get方法获取key对应的value
                currentValue = ((Map<?, ?>) currentValue).get(field);
            }else{
                Field currentField = currentValue.getClass().getDeclaredField(field);
                currentField.setAccessible(true); // 确保私有属性可访问
                currentValue = currentField.get(currentValue);
            }
            // 如果在路径中的某点得到null，则返回null
            if (currentValue == null) {
                return null;
            }
        }

        return currentValue;
    }
}
