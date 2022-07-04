package com.mukut.spring.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mukut.spring.filter.MDCFilter;

import lombok.Data;

@Configuration
@Data
public class MDCFilterConfiguration {
	
	public static final String DEFAULT_HEADER_TOKEN = "correlationId";
	public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "correlationId";

	private String responseHeader = DEFAULT_HEADER_TOKEN;
	private String mdcKey = DEFAULT_MDC_UUID_TOKEN_KEY;
	private String requestHeader = DEFAULT_HEADER_TOKEN;
	
	@Bean
	public FilterRegistrationBean<MDCFilter> servletRegistrationBean() {
		final FilterRegistrationBean<MDCFilter> registrationBean = new FilterRegistrationBean<>();
		final MDCFilter log4jMDCFilter = new MDCFilter(responseHeader, mdcKey, requestHeader);
		registrationBean.setFilter(log4jMDCFilter);
		registrationBean.setOrder(2);
		return registrationBean;
	}
	
}
