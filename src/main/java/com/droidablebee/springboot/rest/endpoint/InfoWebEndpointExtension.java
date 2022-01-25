package com.droidablebee.springboot.rest.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
@EndpointWebExtension(endpoint = InfoEndpoint.class)
public class InfoWebEndpointExtension {

    @Autowired
    private InfoEndpoint delegate;

    @ReadOperation
    public WebEndpointResponse<Map<String, ?>> info() {

        Map<String, Object> info = new HashMap<>();

        //add existing values from unmodifiable map
        info.putAll(this.delegate.info());

        info.putAll(createCustomMap());

        return new WebEndpointResponse<>(info);
    }

    protected LinkedMultiValueMap<String, Number> createCustomMap() {

        return new LinkedMultiValueMap<>();
    }
}