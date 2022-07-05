package com.mukut.spring.filter;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mukut.spring.config.MDCFilterConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(callSuper = false)
public class MDCFilter extends OncePerRequestFilter {

	private final String responseHeader;
	private final String mdcKey;
	private final String requestHeader;

	public MDCFilter() {
		responseHeader = MDCFilterConfiguration.DEFAULT_HEADER_TOKEN;
		mdcKey = MDCFilterConfiguration.DEFAULT_MDC_UUID_TOKEN_KEY;
		requestHeader = MDCFilterConfiguration.DEFAULT_HEADER_TOKEN;
	}

	public MDCFilter(final String responseHeader, final String mdcTokenKey, final String requestHeader) {
		this.responseHeader = responseHeader;
		this.mdcKey = mdcTokenKey;
		this.requestHeader = requestHeader;
	}

	@Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws java.io.IOException, ServletException {
		String srcIpKey = "srcIpKey", hostKey = "hostKey", refererKey = "refererKey", xForwardedForKey = "xForwardedForKey";
        try {
            final String token = extractToken(request);
            final String srcIp = request.getRemoteAddr();
            final String host = request.getHeader("Host");
            final String referer = request.getHeader("Referer");
            final String xForwardedFor = request.getHeader("X-Forwarded-For");
            MDC.put(mdcKey, token);
            MDC.put(srcIpKey, srcIp);
            MDC.put(hostKey, host);
            MDC.put(refererKey, referer);
            MDC.put(xForwardedForKey, xForwardedFor);
            if (StringUtils.hasText(responseHeader)) {
                response.addHeader(responseHeader, token);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(mdcKey);
            MDC.remove(srcIpKey);
            MDC.remove(hostKey);
            MDC.remove(refererKey);
            MDC.remove(xForwardedForKey);
        }
    }

	private String extractToken(final HttpServletRequest request) {
		final String token;
		if (StringUtils.hasText(requestHeader) && StringUtils.hasText(request.getHeader(requestHeader))) {
			token = request.getHeader(requestHeader);
		} else {
			token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		}
		return token;
	}
	
	@Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

}
