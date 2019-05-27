package de.silpion.zimbra.extension.crowd.pass;

import java.util.Map;

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
        catch (Exception e) {
            ZimbraLog.account.debug("Crowd: Password change failed: %s", e.getMessage(), e);
            throw AccountServiceException.CHANGE_PASSWORD();
        }
    }
    
    @Override
    public void postModify(Account account, String password, @SuppressWarnings("rawtypes") Map context) {
       ZimbraLog.account.debug("Password changed succesfully");
    }

}
