package com.codereviewer.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    public static int countOccurrences(String text, String pattern) {
        if (isEmpty(text) || isEmpty(pattern)) {
            return 0;
        }

        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }

    public static String removeComments(String code) {
        // 简单的注释移除（不处理字符串内的注释）
        return code.replaceAll("//.*", "")
                .replaceAll("/\\*.*?\\*/", "");
    }

    public static boolean isValidJavaIdentifier(String identifier) {
        if (isEmpty(identifier)) {
            return false;
        }

        if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
            return false;
        }

        for (int i = 1; i < identifier.length(); i++) {
            if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}