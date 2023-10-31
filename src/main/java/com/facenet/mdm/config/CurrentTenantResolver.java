package com.facenet.mdm.config;

import com.facenet.mdm.security.SecurityUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantResolver implements CurrentTenantIdentifierResolver {

    public static final String DEFAULT_SCHEMA = "voyage_management_system";

    @Override
    public String resolveCurrentTenantIdentifier() {
        return SecurityUtils.getTenantId() != null ? SecurityUtils.getTenantId() : DEFAULT_SCHEMA;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
