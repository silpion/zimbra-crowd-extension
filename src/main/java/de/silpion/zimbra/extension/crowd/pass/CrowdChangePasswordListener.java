/*******************************************************************************
 * Copyright 2018, 2019 Silpion IT-Solutions GmbH
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
package de.silpion.zimbra.extension.crowd.pass;

import java.util.Map;

import com.atlassian.crowd.exception.CrowdException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.ldap.ChangePasswordListener;

import de.silpion.zimbra.extension.crowd.client.CrowdAccount;

public class CrowdChangePasswordListener extends ChangePasswordListener {

    public void register(String id) {
        ChangePasswordListener.register(id, this);
    }


    @Override
    public void preModify(Account account, String password, @SuppressWarnings("rawtypes") Map context, Map<String, Object> attributes) throws ServiceException {
        try {
            new CrowdAccount(account).changePassword(password);
        }
        catch (CrowdException e) {
            ZimbraLog.account.debug("Crowd: Password change failed: %s", e.getMessage(), e);
            throw AccountServiceException.CHANGE_PASSWORD();
        }
        catch (Exception e) {
            ZimbraLog.account.error("Crowd: Unexpected %s: %s", e.getClass().getName(), e.getMessage(), e);
            throw AccountServiceException.CHANGE_PASSWORD();
        }
    }
    
    @Override
    public void postModify(Account account, String password, @SuppressWarnings("rawtypes") Map context) {
       ZimbraLog.account.debug("Password changed succesfully");
    }

}
