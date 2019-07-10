/*******************************************************************************
 * Copyright 2018, 2019 Silpion IT-Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.silpion.zimbra.extension.crowd.client.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyMapper {
    private static final String KEY_PREFIX = "crowd_";
    private static final String FIELD_PREFIX = "PROPERTIES_FILE_";

    private final Field[] fields;

    public KeyMapper(Class<?> clazz) {
        this.fields = clazz.getDeclaredFields();
    }

    public Map<String, String> apply() {
        return Collections.unmodifiableMap(Arrays.stream(fields)
            .filter(f -> Modifier.isPublic(f.getModifiers()))
            .filter(f -> Modifier.isStatic(f.getModifiers()))
            .filter(f -> f.getName().startsWith(FIELD_PREFIX))
            .collect(Collectors.toMap(KeyMapper::getKey, KeyMapper::getValue))
        );
    }

    private static String getKey(Field field) {
        final String name = field.getName();
        return new StringBuilder(name.length())
            .append(KEY_PREFIX)
            .append(name.substring(FIELD_PREFIX.length()).toLowerCase(Locale.ENGLISH))
            .toString();
    }

    private static String getValue(Field field) {
        try {
            return field.get(null).toString();
        }
        catch (IllegalAccessException e) {
            return null;
        }
    }
}
