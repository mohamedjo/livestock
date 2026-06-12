package com.shabic.livestock.config.tracing;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraceIdFilterTest {

	@Mock private Tracer tracer;
	@Mock private CurrentTraceContext currentTraceContext;
	@Mock private TraceContext traceContext;
	@Mock private FilterChain filterChain;

	@Test
	void echoesTraceIdInResponseAfterRequest() throws Exception {
		when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
		when(currentTraceContext.context()).thenReturn(traceContext);
		when(traceContext.traceId()).thenReturn("abc123trace");

		var filter = new TraceIdFilter(tracer);
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		assertThat(response.getHeader(TraceIdFilter.HEADER_NAME)).isEqualTo("abc123trace");
	}

	@Test
	void omitsHeaderWhenNoActiveTrace() throws Exception {
		when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
		when(currentTraceContext.context()).thenReturn(null);

		var filter = new TraceIdFilter(tracer);
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();

		filter.doFilterInternal(request, response, filterChain);

		assertThat(response.getHeader(TraceIdFilter.HEADER_NAME)).isNull();
	}
}
