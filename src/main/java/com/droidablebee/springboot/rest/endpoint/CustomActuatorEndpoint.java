package com.droidablebee.springboot.rest.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Custom actuator endpoint.
 */
@Component
@Endpoint(id = CustomActuatorEndpoint.CUSTOM)
public class CustomActuatorEndpoint {

    static final String CUSTOM = "custom";

    @ReadOperation
    public ResponseEntity<?> custom() {

        return ResponseEntity.ok(createCustomMap());
    }

    protected LinkedMultiValueMap<String, Number> createCustomMap() {

        return new LinkedMultiValueMap<>();
    }

}
