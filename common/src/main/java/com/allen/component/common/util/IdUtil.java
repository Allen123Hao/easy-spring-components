package com.allen.component.common.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;


public class IdUtil {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$",
            // "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern UUID_PATTERN_WITH_NO_HYPHENATED = Pattern.compile(
            // "^[0-9a-f]{32}$",
            "^[0-9a-z]{32}$",
            Pattern.CASE_INSENSITIVE
    );

    private IdUtil() {
    }

    /**
     * 获取uuid，格式:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     *
     * @return uuid
     */
    public static String retrieveUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }

    /**
     * 判断字符串是否是 uuid
     *
     * @param str 字符串
     * @return 是否是 uuid
     */
    public static boolean isUUID(String str) {
        return str != null && (UUID_PATTERN.matcher(str).matches() || UUID_PATTERN_WITH_NO_HYPHENATED.matcher(str).matches());
    }

    /**
     * 获取uuid,不带分隔符
     *
     * @return 不带分隔符的uuid
     */
    public static String retrieveUUIDWithNoHyphenated() {
        return retrieveUUID().replace("-", "");
    }

}
