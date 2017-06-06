package com.mkt.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class WiremockServerCache {

	private Map<String, WireMockServer> wireMockServerMap = Maps.newHashMap();

	public void put(final String wireMockServerName, WireMockServer wireMockServer) {
		wireMockServerMap.put(wireMockServerName, wireMockServer);
	}

	public void remove(final String wireMockServerName) {
		wireMockServerMap.remove(wireMockServerName);
	}

	public WireMockServer find(final String wireMockServerName) {
		return wireMockServerMap.get(wireMockServerName);
	}

	public Optional<WireMockServer> findOptional(final String wireMockServerName) {
		return Optional.ofNullable(wireMockServerMap.get(wireMockServerName));
	}
}
