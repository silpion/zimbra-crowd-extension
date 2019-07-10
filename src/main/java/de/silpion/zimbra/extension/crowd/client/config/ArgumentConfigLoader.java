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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ArgumentConfigLoader extends LocalConfigLoader {
    private final List<String> arguments;

    public ArgumentConfigLoader(List<String> arguments) {
        this.arguments = arguments != null ? arguments : Collections.emptyList(); 
    }

    @Override
    public Properties getProperties() {
        final Properties properties = super.getProperties();
        properties.putAll(parseArguments());
        return properties;
    }

    private Properties parseArguments() {
        final Properties loader = new Properties();
        for (String argument : arguments) {
            try (Reader reader = new StringReader(argument)) {
                loader.load(reader);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unexpected " + e.toString(), e);
            }
        }
        return loader;
    }
}
