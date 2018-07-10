package de.silpion.zimbra.extension.crowd;

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
import java.util.concurrent.ConcurrentHashMap;

import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;

public class CrowdClientFactory {
    // These are modeled after the crowd.properties file as described at
    // https://confluence.atlassian.com/crowd/the-crowd-properties-file-98665664.html
    private static final String LC_KEY_CROWD_SERVER_URL = "crowd_server_url";
    private static final String LC_KEY_CROWD_APPLICATION_NAME = "crowd_application_name";
    private static final String LC_KEY_CROWD_APPLICATION_PASSWORD = "crowd_application_password";
    
    private static final RestCrowdClientFactory FACTORY = new RestCrowdClientFactory();
    private static final ConcurrentHashMap<String, RestCrowdClient> CLIENTS = new ConcurrentHashMap<>();
    
    
    public static RestCrowdClient getClient(Domain domain, List<String> args) throws Exception {
        final String key = domain.getId();
        RestCrowdClient client = CLIENTS.computeIfAbsent(key, id -> newInstance(args));
        try {
            client.testConnection();
        }
        catch (Exception e) {
            ZimbraLog.extensions.error("Crowd client failed connection test, shutting down");
            CLIENTS.remove(key);
            client.shutdown();
            throw e;
        }
        return client;
    }
    
    private static RestCrowdClient newInstance(List<String> args) {
        final String url = getArg(args, 0, LC_KEY_CROWD_SERVER_URL);
        final String applicationName = getArg(args, 1, LC_KEY_CROWD_APPLICATION_NAME);
        final String applicationPassword = getArg(args, 2, LC_KEY_CROWD_APPLICATION_PASSWORD);

        ZimbraLog.extensions.info("Creating new Crowd client for application %s at URL %s", applicationName, url);
        return (RestCrowdClient) FACTORY.newInstance(url, applicationName, applicationPassword);
    }
    
    private static String getArg(List<String> args, int index, String key) {
        String value = "";
        
        if (args.size() >= index) {
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
