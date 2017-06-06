package com.mkt.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.mkt.proxy.check.WiremockServerNameRequire;
import com.mkt.proxy.check.WiremockServerRunningRequire;
import com.mkt.proxy.stub.StubModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Component
public class WiremockServerModifier {

	@Autowired
	private WiremockServerCache wiremockServerCache;

	@Autowired
	private StubModifier wiremockStubModifier;


	@WiremockServerNameRequire
	public String register(final String wireMockServerName, final String proxyTargetServer) {
		resetWiremockServer(wireMockServerName);

		WireMockServer wireMockServer = createWireMockServer(proxyTargetServer);

		wireMockServer.start();

		wiremockServerCache.put(wireMockServerName, wireMockServer);

		return generateRegistInformation(wireMockServerName, wireMockServer.port(), proxyTargetServer);
	}

	private void resetWiremockServer(final String wireMockServerName) {
		Optional<WireMockServer> wireMockServerOptional = wiremockServerCache.findOptional(wireMockServerName);

		if (isRunning(wireMockServerOptional)) {
			shutDown(wireMockServerOptional.get());
			wiremockServerCache.remove(wireMockServerName);
		}
	}

	private WireMockServer createWireMockServer(final String proxyTargetServer) {
		WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());

		wireMockServer.stubFor(
			get(urlMatching(".*"))
				.willReturn(
					wiremockStubModifier.getProxyAllTargetServerStub(proxyTargetServer)
				)
				.atPriority(100)
		);

		return wireMockServer;
	}

	@WiremockServerNameRequire
	public void shutDown(final String wireMockServerName) {
		final Optional<WireMockServer> wireMockServerOptional = wiremockServerCache.findOptional(wireMockServerName);

		if (wireMockServerOptional.isPresent()) {
			WireMockServer wireMockServer = wireMockServerOptional.get();

			wireMockServer.shutdownServer();
			wireMockServer.stop();

			wiremockServerCache.remove(wireMockServerName);
		}
	}

	private void shutDown(WireMockServer wireMockServer) {
		wireMockServer.shutdownServer();
		wireMockServer.stop();
	}

	@WiremockServerRunningRequire
	public String getWiremockServerInformation(final String wireMockServerName) {
		WireMockServer wireMockServer = wiremockServerCache.find(wireMockServerName);
		return getHostAddress() + ":" + wireMockServer.port();
	}

	private String generateRegistInformation(final String wireMockServerName, final int wireMockServerPort, final String proxyTargetServer) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("wireMockServerName : " + wireMockServerName);
		stringBuilder.append(" (");
		stringBuilder.append(getHostAddress() + ":" + wireMockServerPort);
		stringBuilder.append(") - ");
		stringBuilder.append("proxyTargetServer : " + proxyTargetServer);

		return stringBuilder.toString();
	}

	public boolean isRunning(final String wireMockServerName) {
		final Optional<WireMockServer> wireMockServerOptional = wiremockServerCache.findOptional(wireMockServerName);
		return isRunning(wireMockServerOptional);
	}

	private boolean isRunning(Optional<WireMockServer> wireMockServer) {
		return wireMockServer.isPresent() && wireMockServer.get().isRunning();
	}

	private String getHostAddress() {
		String hostAddress;
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new IllegalStateException("getHostAddress failed");
		}

		return hostAddress;
	}
}
