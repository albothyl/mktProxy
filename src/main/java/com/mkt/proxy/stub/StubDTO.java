package com.mkt.proxy.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(staticName = "create")
public class StubDTO {
	private UUID stubId;
	private String requestMethod;
	private String urlPattern;
	private String responseBody;
	private int statusCode;
}
