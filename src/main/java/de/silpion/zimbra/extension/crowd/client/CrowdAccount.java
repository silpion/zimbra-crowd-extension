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

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;

import de.silpion.zimbra.extension.crowd.CrowdExtension;

public class CrowdAccount {
    private static final String PREFIX = CrowdExtension.ID + ":";
    
    protected final Account account;
    protected final RestCrowdClient client;
    


    public CrowdAccount(Account account) throws Exception {
        this(account, getClient(account));
    }

    public CrowdAccount(Account account, List<String> args) throws Exception {
        this(account, getClient(account, args));
    }

    private CrowdAccount(Account account, RestCrowdClient client) {
        this.account = account;
        this.client  = client;
    }

    private static RestCrowdClient getClient(Account account) throws Exception {
        return getClient(account, null);
    }

    private static RestCrowdClient getClient(Account account, List<String> args) throws Exception {
        final Domain domain = Provisioning.getInstance().getDomain(account);
        return new CrowdClientFactory(domain, args).getClient();
    }


    public void authenticate(String password) throws Exception {
        client.authenticateUser(getPrincipal(), password);
    }
    
    public void changePassword(String password) throws Exception {
        client.updateUserCredential(getPrincipal(), password);
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

        return account.getUid();
    }
    
    private Optional<String> getForeignPrincipal() {
        final String[] foreignPrincipals = account.getForeignPrincipal();
        for (String principal : foreignPrincipals) {
            if (principal.startsWith(PREFIX)) {
                return Optional.of(principal.substring(PREFIX.length()));
            }
        }
        return Optional.empty();
    }
    
    private Optional<String> searchCrowdPrincipal() throws Exception {
        final String address = account.getName();
        final SearchRestriction query = Combine.allOf(
                Restriction
                    .on(UserTermKeys.EMAIL)
                    .exactlyMatching(address),
                Restriction
                    .on(UserTermKeys.ACTIVE)
                    .exactlyMatching(Boolean.TRUE)
            );
        
        final List<String> users = client.searchUserNames(query, 0, 2);
        
        switch (users.size()) {
        case 0:
            ZimbraLog.account.debug("Crowd: Found no active user with mail address %s", address);
            return Optional.empty();
        case 1:
            final String username = users.get(0);
            ZimbraLog.account.debug("Crowd: Found one active user with mail address %s: %s", address, username);
            return Optional.of(username);
        default:
            ZimbraLog.account.debug("Crowd: Found more than one active user with mail address %s: %s", address, String.join(", ", users));
            // TODO: Use a different exception
            throw new ObjectNotFoundException("Account name not unique");    
        }
    }
    
}
