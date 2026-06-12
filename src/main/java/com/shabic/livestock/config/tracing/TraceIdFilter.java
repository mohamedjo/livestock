package com.shabic.livestock.config.tracing;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TraceIdFilter extends OncePerRequestFilter {
	public static final String HEADER_NAME = "X-Trace-Id";

	private final Tracer tracer;

	public TraceIdFilter(Tracer tracer) {
		this.tracer = tracer;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} finally {
			String traceId = currentTraceId();
			if (traceId != null) {
				response.setHeader(HEADER_NAME, traceId);
			}
		}
	}

	@Nullable
	private String currentTraceId() {
		TraceContext context = tracer.currentTraceContext().context();
		return context != null ? context.traceId() : null;
	}
}
