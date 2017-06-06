package com.mkt.proxy.check;

import com.mkt.proxy.WiremockServerModifier;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class WiremockCheckAdvice {

	@Autowired
	private WiremockServerModifier wiremockServerModifier;


	@Before("@annotation(com.mkt.proxy.check.WiremockServerRunningRequire)")
	public void wiremockRunningCheck() {
		if (wiremockServerIsNotRunning()) {
			throw new IllegalStateException(" <WARNING> wiremockServer is not running!! <WARNING> ");
		}
	}

	@Before("@annotation(com.mkt.proxy.check.WiremockServerNameRequire)")
	public void serverNameCheck() {
		getWiremockServerName();
	}

	private boolean wiremockServerIsNotRunning() {
		return !wiremockServerModifier.isRunning(getWiremockServerName());
	}

	private String getWiremockServerName() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		final String wiremockServerName = request.getParameter("wiremockServerName");


		if (StringUtils.isEmpty(wiremockServerName)) {
			throw new IllegalArgumentException(" <WARNING> wiremockServerName is not found!! <WARNING> ");
		}

		return wiremockServerName;
	}
}
