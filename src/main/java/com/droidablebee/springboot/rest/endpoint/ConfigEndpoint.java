package com.droidablebee.springboot.rest.endpoint;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
@Validated //required for @Valid on method parameters such as @RequesParam, @PathVariable, @RequestHeader
public class ConfigEndpoint extends BaseEndpoint {

	@Autowired
	private Environment environment;

	@RequestMapping(path = "/v1/config", method = RequestMethod.GET)
	@ApiOperation(
			value = "Get config values",
			notes = "Returns config values.",
			response = Page.class)
    public Map<String, String> getConfig() {

		Map<String, String> config = new HashMap<>();
		config.put("url", environment.getProperty("my.url"));
		config.put("port", environment.getProperty("my.port"));

		return config;
    }
}