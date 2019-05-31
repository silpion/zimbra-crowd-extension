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

import java.util.stream.Collectors;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

import de.silpion.zimbra.extension.crowd.auth.CrowdAuthHandler;
import de.silpion.zimbra.extension.crowd.auth.CrowdAuthMech;
import de.silpion.zimbra.extension.crowd.pass.CrowdChangePasswordListener;

public class CrowdExtension implements ZimbraExtension {
    // This string is used to refer to this extension
    public static final String ID = "crowd";
    
    public String getName() {
        return ID;
    }
    
    public void init() throws ExtensionException, ServiceException {
        new CrowdAuthHandler().register(ID);
        new CrowdChangePasswordListener().register(ID);
        
        final String s = Provisioning.getInstance().getAllDomains().stream()
            .filter(d -> new CrowdAuthMech(d).isEnabled())
            .map(Domain::getId)
            .collect(Collectors.joining(", "));
        ZimbraLog.extensions.info("Crowd authentication enabled for domains: %s", s.isEmpty() ? "(none)" : s);
    }

    public void destroy() {
        ZimbraLog.extensions.debug("Crowd extension destroyed");
    }
}
