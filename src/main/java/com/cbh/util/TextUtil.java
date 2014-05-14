package com.cbh.util;

import java.util.Collection;
import java.util.Map;

public class TextUtil {
    public static boolean isValidate(String content) {
        return content != null && !"".equals(content.trim());
    }

    public static boolean isValidate(Collection<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean isValidate(Map<?, ?> map) {
        if (map != null && map.size() > 0) {
            return true;
        }
        return false;
    }
}
