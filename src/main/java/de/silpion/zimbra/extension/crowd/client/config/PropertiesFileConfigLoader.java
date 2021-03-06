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

import java.nio.file.Paths;
import java.util.Properties;

import com.atlassian.crowd.integration.Constants;
import com.atlassian.crowd.service.client.ClientResourceLocator;

import com.zimbra.common.localconfig.LC;

public abstract class PropertiesFileConfigLoader extends ClientResourceLocator {
    private static final String ZIMBRA_CONF_DIR = Paths.get(LC.zimbra_home.value(), "conf").toString();
    private static final boolean SKIP_VALIDATION = Boolean.getBoolean(Constants.PROPERTIES_FILE + ".skip.validation");

    public PropertiesFileConfigLoader() {
        super(Constants.PROPERTIES_FILE, ZIMBRA_CONF_DIR);
    }

    @Override
    protected String formatFileLocation(String fileLocation, boolean skipValidation) {
        return super.formatFileLocation(fileLocation, skipValidation || SKIP_VALIDATION);
    }

    @Override
    public Properties getProperties() {
        if (getResourceLocation() == null) {
            return new Properties();
        }
        final Properties properties = super.getProperties();
        if (properties == null) {
            return new Properties();
        }
        return properties;
    }
}
