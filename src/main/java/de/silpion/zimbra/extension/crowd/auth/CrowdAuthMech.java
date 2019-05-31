package de.silpion.zimbra.extension.crowd.auth;

/*-
 * #%L
 * Zimbra Crowd Authentication Extension
 * %%
 * Copyright (C) 2019 Silpion IT-Solutions GmbH
 * %%
 * This file contains proprietary code and must not be copied or distributed
 * without the express permission of the copyright holder.
 * #L%
 */

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.zimbra.common.util.QuotedStringParser;
import com.zimbra.cs.account.Domain;

import de.silpion.zimbra.extension.crowd.CrowdExtension;

public class CrowdAuthMech {
    private static final String AUTH_MECH_PREFIX = "custom:" + CrowdExtension.ID;
    
    private static final String SPACE = " ";

    private final String config;

    public CrowdAuthMech(Domain domain) {
        config = Optional.ofNullable(domain.getAuthMech()).orElse("");
    }
    
    public boolean isEnabled() {
        final int l = AUTH_MECH_PREFIX.length();
        return config.startsWith(AUTH_MECH_PREFIX) &&
                ((config.length() == l) || config.substring(l, l + 1).equals(SPACE));
    }
    
    public List<String> getArgs() {
        final int l = AUTH_MECH_PREFIX.length();
        if (!isEnabled() || config.length() == l) {
            return Collections.emptyList();
        }
        final QuotedStringParser parser = new QuotedStringParser(config.substring(l + SPACE.length()));
        return parser.parse();
    }
}