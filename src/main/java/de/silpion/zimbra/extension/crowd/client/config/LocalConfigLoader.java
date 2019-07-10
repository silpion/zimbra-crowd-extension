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

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.atlassian.crowd.integration.Constants;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.StringUtil;

public abstract class LocalConfigLoader extends PropertiesFileConfigLoader {
    private static final Map<String, String> KEY_MAP = new KeyMapper(Constants.class).apply();

    @Override
    public Properties getProperties() {
        final Properties properties = super.getProperties();

        for (Entry<String, String> key : KEY_MAP.entrySet()) {
            final String value = LC.get(key.getKey());
            if (StringUtil.isNullOrEmpty(value)) {
                continue;
            }
            properties.setProperty(key.getValue(), value);
        }
        return properties;
    }
}
