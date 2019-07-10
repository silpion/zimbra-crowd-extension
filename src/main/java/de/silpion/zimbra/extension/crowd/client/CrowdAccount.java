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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.rest.service.RestCrowdClient;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.service.client.ClientResourceLocator;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;

import de.silpion.zimbra.extension.crowd.CrowdExtension;
import de.silpion.zimbra.extension.crowd.client.config.ArgumentConfigLoader;

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

    public CrowdAccount(Account account, ClientResourceLocator config) throws Exception {
        this(account, getClient(account, config));
    }

    private CrowdAccount(Account account, RestCrowdClient client) {
        this.account = account;
        this.client  = client;
    }

    private static RestCrowdClient getClient(Account account) throws Exception {
        return getClient(account, Collections.emptyList());
    }

    private static RestCrowdClient getClient(Account account, List<String> args) throws Exception {
        return getClient(account, new ArgumentConfigLoader(args));
    }

    private static RestCrowdClient getClient(Account account, ClientResourceLocator config) throws Exception {
        final Domain domain = Provisioning.getInstance().getDomain(account);
        return new CrowdClientFactory(domain, config).getClient();
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
