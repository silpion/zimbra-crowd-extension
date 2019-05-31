package de.silpion.zimbra.extension.crowd.client;
/*-
 * #%L
 * Zimbra Crowd Authentication Extension
 * %%
 * Copyright (C) 2018 Silpion IT-Solutions GmbH
 * %%
 * This file contains proprietary code and must not be copied or distributed
 * without the express permission of the copyright holder.
 * #L%
 */

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;

import de.silpion.zimbra.extension.crowd.auth.CrowdAuthMech;


public class CrowdClientFactory {
    // These are modeled after the crowd.properties file as described at
    // https://confluence.atlassian.com/crowd/the-crowd-properties-file-98665664.html
    private static final String LC_KEY_CROWD_SERVER_URL = "crowd_server_url";
    private static final String LC_KEY_CROWD_APPLICATION_NAME = "crowd_application_name";
    private static final String LC_KEY_CROWD_APPLICATION_PASSWORD = "crowd_application_password";
    
    private static final RestCrowdClientFactory FACTORY = new RestCrowdClientFactory();
    private static final ConcurrentHashMap<String, RestCrowdClient> CLIENTS = new ConcurrentHashMap<>();
    
    private final Domain domain;
    private final List<String> args;

    public CrowdClientFactory(Domain domain) {
        this(domain, null);
    }

    public CrowdClientFactory(Domain domain, List<String> args) {
        this.domain = domain;
        this.args = Optional.ofNullable(args)
                .orElseGet(() -> new CrowdAuthMech(domain).getArgs());
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
        final String url = getArg(0, LC_KEY_CROWD_SERVER_URL);
        final String applicationName = getArg(1, LC_KEY_CROWD_APPLICATION_NAME);
        final String applicationPassword = getArg(2, LC_KEY_CROWD_APPLICATION_PASSWORD);

        ZimbraLog.extensions.info("Creating new Crowd client for domain %s using application %s at URL %s",
                domain,
                applicationName,
                url);
        return (RestCrowdClient) FACTORY.newInstance(url, applicationName, applicationPassword);
    }
    
    private String getArg(int index, String key) {
        String value = "";
        
        if (args.size() > index) {
            value = args.get(index);
        }
        if (!StringUtil.isNullOrEmpty(value)) {
            return value;
        }
        
        value = LC.get(key);
        if (!StringUtil.isNullOrEmpty(value)) {
            return value;
        }
        
        throw new IllegalStateException("Can't create Crowd client: Missing value for " + key);
    }
    

}
