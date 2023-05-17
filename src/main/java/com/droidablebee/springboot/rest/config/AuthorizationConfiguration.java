package com.droidablebee.springboot.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationConfiguration {

    @Value("${app.authorization.enabled:true}")
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }
}
