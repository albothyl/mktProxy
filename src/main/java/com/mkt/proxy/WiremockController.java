package com.mkt.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.mkt.proxy.stub.StubModifier;
import com.mkt.proxy.stub.StubDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/*
	TODO :
	1. JPA 적용
	2. Stub의 다양화 (현재는 get, urlPattern만 지원함)
*/

@RestController
public class WiremockController {

	@Autowired
	private WiremockServerModifier wiremockServerModifier;

	@Autowired
	private StubModifier stubModifier;

	@RequestMapping(value = "/wiremockServer", method = RequestMethod.POST)
	public String register(final String wiremockServerName, final String proxyTargetServer) {
		final String registrationInformation = wiremockServerModifier.register(wiremockServerName, proxyTargetServer);

		return registrationInformation;
	}

	@RequestMapping(value = "/wiremockServer", method = RequestMethod.DELETE)
	public void shutdown(final String wiremockServerName) {
		wiremockServerModifier.shutDown(wiremockServerName);
	}

	@RequestMapping(value = "/wiremockServer", method = RequestMethod.GET)
	public String getInformation(final String wiremockServerName) throws JsonProcessingException {
		return wiremockServerModifier.getWiremockServerInformation(wiremockServerName);
	}

	@RequestMapping(value = "/stub", method = RequestMethod.POST)
	public void add(final String wiremockServerName, final String mockingApiPath, final String responseJsonData) {
		stubModifier.addStub(wiremockServerName, mockingApiPath, responseJsonData);
	}

	@RequestMapping(value = "/stub", method = RequestMethod.DELETE)
	public void remove(final String wiremockServerName, final String stubId) {
		stubModifier.removeStub(wiremockServerName, UUID.fromString(stubId));
	}

	@RequestMapping(value = "/stub", method = RequestMethod.GET)
	public String getMappings(final String wiremockServerName) throws JsonProcessingException {
		final List<StubDTO> stubMappings = stubModifier.getStubList(wiremockServerName);

		return new ObjectMapper().writeValueAsString(stubMappings);
	}

	@RequestMapping(value = "/stub/origin", method = RequestMethod.GET)
	public String getOriginMappings(final String wiremockServerName) throws JsonProcessingException {
		final List<StubMapping> stubMappings = stubModifier.getOriginStubList(wiremockServerName);

		return new ObjectMapper().writeValueAsString(stubMappings);
	}

}
