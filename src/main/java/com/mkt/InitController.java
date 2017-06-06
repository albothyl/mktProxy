package com.mkt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InitController {

	private static final String SWAGGER_UI = "redirect:/swagger-ui.html#/wiremock-controller";

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String init() {
		return SWAGGER_UI;
	}

}
