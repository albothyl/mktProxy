package com.mkt.proxy.stub;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.mkt.proxy.WiremockServerCache;
import com.mkt.proxy.check.WiremockServerRunningRequire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Component
public class StubModifier {

	@Autowired
	private WiremockServerCache wiremockServerCache;

	private StubConverter stubConverter = new StubConverter();

	@WiremockServerRunningRequire
	public void addStub(final String wireMockServerName, final String mockingApiPath, final String responseJsonData) {
		WireMockServer wireMockServer = wiremockServerCache.find(wireMockServerName);

		WireMock wireMock = new WireMock(wireMockServer.port());
		wireMock.register(
			get(urlMatching(mockingApiPath))
				.willReturn(
					getDefaultResponseBuilder()
						.withBody(responseJsonData)
				)
				.atPriority(1)
		);
	}

	@WiremockServerRunningRequire
	public void removeStub(final String wireMockServerName, final UUID stubId) {
		WireMockServer wireMockServer = wiremockServerCache.find(wireMockServerName);
		StubMapping singleStubMapping = wireMockServer.getSingleStubMapping(stubId);

		wireMockServer.removeStub(singleStubMapping);
	}

	@WiremockServerRunningRequire
	public List<StubDTO> getStubList(final String wireMockServerName) {
		WireMockServer wireMockServer = wiremockServerCache.find(wireMockServerName);

		final List<StubMapping> stubMappings = wireMockServer.getStubMappings();

		return stubMappings.stream().map(stubConverter::convert).collect(Collectors.toList());
	}

	@WiremockServerRunningRequire
	public List<StubMapping> getOriginStubList(final String wireMockServerName) {
		WireMockServer wireMockServer = wiremockServerCache.find(wireMockServerName);
		return wireMockServer.getStubMappings();
	}

	private ResponseDefinitionBuilder getDefaultResponseBuilder() {
		return aResponse()
			.withStatus(HttpStatus.OK.value())
			.withHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
	}

	public ResponseDefinitionBuilder getProxyAllTargetServerStub(final String proxyTargetServer) {
		return getDefaultResponseBuilder()
			.proxiedFrom(proxyTargetServer);
	}
}
