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

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;

import de.silpion.zimbra.extension.crowd.CrowdAccount;
import de.silpion.zimbra.extension.crowd.CrowdClientFactory;
import de.silpion.zimbra.extension.crowd.CrowdExtension;

public class CrowdAuthHandler extends ZimbraCustomAuth {
    @Override
    public void authenticate(Account account, String password, Map<String, Object> context, List<String> args) throws Exception {
        new CrowdAccount(CrowdClientFactory.getClient(account, args), account)
            .authenticate(password);
    }
}
