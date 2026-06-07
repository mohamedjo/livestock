package com.shabic.livestock.config.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String correlationId = CorrelationId.resolveFromHeaders(request::getHeader);
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
		}
		CorrelationIdContext.set(correlationId);
		response.setHeader(CorrelationId.HEADER_NAME, correlationId);
		log.info("correlationId={} {} {}", correlationId, request.getMethod(), request.getRequestURI());
		try {
			filterChain.doFilter(request, response);
		} finally {
			CorrelationIdContext.clear();
		}
	}
}
