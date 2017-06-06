package com.mkt.proxy.stub;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.core.convert.converter.Converter;

public class StubConverter implements Converter<StubMapping, StubDTO> {

	@Override
	public StubDTO convert(final StubMapping stubMapping) {
		return StubDTO.create(
			stubMapping.getId(),
			stubMapping.getRequest().getMethod().getName(),
			stubMapping.getRequest().getUrlPattern(),
			stubMapping.getResponse().getBody(),
			stubMapping.getResponse().getStatus());
	}
}
