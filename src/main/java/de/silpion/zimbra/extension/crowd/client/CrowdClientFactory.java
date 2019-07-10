/*******************************************************************************
 * Copyright 2018, 2019 Silpion IT-Solutions GmbH
 * Copyright 2018, 2019 iVentureGroup GmbH
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
package de.silpion.zimbra.extension.crowd.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.atlassian.crowd.integration.Constants;
import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.ClientResourceLocator;

import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;


public class CrowdClientFactory {
    private static final RestCrowdClientFactory FACTORY = new RestCrowdClientFactory();
    private static final ConcurrentHashMap<String, RestCrowdClient> CLIENTS = new ConcurrentHashMap<>();
    
    private final Domain domain;
    private final ClientResourceLocator config;

    public CrowdClientFactory(Domain domain) {
        this(domain, null);
    }

    public CrowdClientFactory(Domain domain, ClientResourceLocator config) {
        this.domain = domain;
        this.config = config;
    }

    public static void shutdown() {
        for (Map.Entry<String, RestCrowdClient> client : CLIENTS.entrySet()) {
            try {
                client.getValue().shutdown();
            }
            catch (Exception e) {
                ZimbraLog.extensions.debug("Crowd: Failed to shut down client %s: %s", client.getKey(), e.getMessage(), e);
            }
        }
    }


    public RestCrowdClient getClient() throws Exception {
        final String key = domain.getId();
        RestCrowdClient client = CLIENTS.computeIfAbsent(key, this::newInstance);
        try {
            client.testConnection();
        }
        catch (Exception e) {
            ZimbraLog.extensions.error("Crowd client for domain %s failed connection test, shutting down", key);
            CLIENTS.remove(key);
            client.shutdown();
            throw e;
        }
        return client;
    }
    
    
    private RestCrowdClient newInstance(String domain) {
        final ClientProperties properties = getProperties();
        ZimbraLog.extensions.info("Creating new Crowd client for domain %s using application %s at URL %s",
                domain,
                properties.getApplicationName(),
                properties.getBaseURL());
        return (RestCrowdClient) FACTORY.newInstance(properties);
    }
    
    private ClientProperties getProperties() {
        final ClientProperties properties = ClientPropertiesImpl.newInstanceFromResourceLocator(config);
        if (StringUtil.isNullOrEmpty(properties.getBaseURL())) {
            throw new IncompleteConfigException(Constants.PROPERTIES_FILE_SECURITY_SERVER_URL);
        }
        if (StringUtil.isNullOrEmpty(properties.getApplicationName())) {
            throw new IncompleteConfigException(Constants.PROPERTIES_FILE_APPLICATION_NAME);
        }
        if (StringUtil.isNullOrEmpty(properties.getApplicationPassword())) {
            throw new IncompleteConfigException(Constants.PROPERTIES_FILE_APPLICATION_PASSWORD);
        }
        return properties;
    }
    
    public static class IncompleteConfigException extends IllegalStateException {
        private static final long serialVersionUID = 1L;

        public IncompleteConfigException(String property) {
            super("Can't create Crowd client: Missing value for " + property);
        }
    }
}
