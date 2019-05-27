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

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;

import de.silpion.zimbra.extension.crowd.client.CrowdAccount;

public class CrowdAuthHandler extends ZimbraCustomAuth {

    public void register(String id) {
        ZimbraCustomAuth.register(id, this);
    }

    @Override
    public void authenticate(Account account, String password, Map<String, Object> context, List<String> args) throws Exception {
        try {
            new CrowdAccount(account, args).authenticate(password);
        }
        catch (Exception e) {
            ZimbraLog.account.debug("Crowd: Authentication failed: %s", e.getMessage(), e);
            throw e;
        }
    }
}
