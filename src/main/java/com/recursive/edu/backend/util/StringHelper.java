/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.util;

import com.recursive.edu.backend.constants.UserConstants;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public class StringHelper {
    public static String getOrDefault(String actualValue, String defaultValue) {
        return isNotEmpty(actualValue) ? actualValue: defaultValue;
    }

    public static boolean isEmpty(String value) {
        return (value == null || value.trim().length() == 0);
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static String getName(String firstName, String middleName, String lastName) {
        return String.format(UserConstants.NAME_FORMAT,
                firstName, isEmpty(middleName) ? "": middleName, lastName);
    }
}
