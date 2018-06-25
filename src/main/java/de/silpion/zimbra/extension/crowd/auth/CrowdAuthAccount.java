package de.silpion.zimbra.extension.crowd.auth;

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

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;

public class CrowdAuthAccount {
    private static final String AUTH_MECH_PREFIX = CrowdAuthHandler.AUTH_MECH_NAME + ":";
    
    protected final RestCrowdClient client;
    protected final Account account;
    
    public CrowdAuthAccount(RestCrowdClient client, Account account) {
        this.client = client;
        this.account = account;
    }
    
    public void authenticate(String password) throws Exception {
        client.authenticateUser(getPrincipal(), password);
    }
    
    private String getPrincipal() throws Exception {
        Optional<String> username;
        
        // TODO: It should be possible to do this via Streams but I failed.
        username = getForeignPrincipal();
        if (username.isPresent()) {
            return username.get();
        }
        username = searchCrowdPrincipal();
        if (username.isPresent()) {
            return username.get();
        }
        
        throw new ObjectNotFoundException("Account not found");
    }
    
    private Optional<String> getForeignPrincipal() {
        final String[] foreignPrincipals = account.getForeignPrincipal();
        for (String principal : foreignPrincipals) {
            if (principal.startsWith(AUTH_MECH_PREFIX)) {
                return Optional.of(principal.substring(AUTH_MECH_PREFIX.length()));
            }
        }
        return Optional.empty();
    }
    
    private Optional<String> searchCrowdPrincipal() throws Exception {
        final String address = account.getName();
        final SearchRestriction query = Restriction
                .on(UserTermKeys.EMAIL)
                .exactlyMatching(address);
        
        final List<String> users = client.searchUserNames(query, 0, 2);
        
        switch (users.size()) {
        case 0:
            ZimbraLog.account.debug("Found no user with mail address %s in Crowd directory", address);
            return Optional.empty();
        case 1:
            final String username = users.get(0);
            ZimbraLog.account.debug("Found one user with mail address %s in Crowd directory: %s", address, username);
            return Optional.of(username);
        default:
            ZimbraLog.account.debug("Found more than one user with mail address %s in Crowd directory: %s", address, String.join(", ", users));
            // TODO: Use a different exception
            throw new ObjectNotFoundException("Account name not unique");    
        }
    }
    
}
