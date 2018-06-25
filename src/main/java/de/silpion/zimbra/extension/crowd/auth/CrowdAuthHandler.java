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
import java.util.Map;
import java.util.Optional;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;

import de.silpion.zimbra.extension.crowd.CrowdClient;

public class CrowdAuthHandler extends ZimbraCustomAuth {
    public static final String AUTH_MECH_NAME = "crowd";
    
    private static final String AUTH_MECH_PREFIX = AUTH_MECH_NAME + ":"; 

    @Override
    public void authenticate(Account account, String password, Map<String, Object> context, List<String> args) throws Exception {
        final String username = getForeignPrincipal(account).orElse(account.getName());
        
        CrowdClient.getClient(account, args).authenticateUser(username, password);
    }
    
    private Optional<String> getForeignPrincipal(Account account) {
        final String[] foreignPrincipals = account.getForeignPrincipal();
        for (String principal : foreignPrincipals) {
            if (principal.startsWith(AUTH_MECH_PREFIX)) {
                return Optional.of(principal.substring(AUTH_MECH_PREFIX.length()));
            }
        }
        return Optional.empty();
    }
}
