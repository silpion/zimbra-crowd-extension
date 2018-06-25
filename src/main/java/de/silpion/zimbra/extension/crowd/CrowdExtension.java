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




import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

import de.silpion.zimbra.extension.crowd.auth.CrowdAuthHandler;

public class CrowdExtension implements ZimbraExtension {

    public String getName() {
        return "Crowd";
    }
    
    public void init() throws ExtensionException, ServiceException {
        ZimbraCustomAuth.register(CrowdAuthHandler.AUTH_MECH_NAME, new CrowdAuthHandler());
    }
    
    public void destroy() {
        
    }
   

}
