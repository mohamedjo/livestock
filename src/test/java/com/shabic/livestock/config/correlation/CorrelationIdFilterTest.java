package com.shabic.livestock.config.correlation;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

	@Mock private FilterChain filterChain;

	private final CorrelationIdFilter filter = new CorrelationIdFilter();

	@AfterEach
	void tearDown() {
		CorrelationIdContext.clear();
		MDC.clear();
	}

	@Test
	void usesIncomingHeaderAndEchoesItInResponse() throws Exception {
		var request = new MockHttpServletRequest();
		request.addHeader(CorrelationId.HEADER_NAME, "gateway-corr-id");
		var response = new MockHttpServletResponse();

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		assertThat(response.getHeader(CorrelationId.HEADER_NAME)).isEqualTo("gateway-corr-id");
		assertThat(MDC.get(CorrelationId.MDC_KEY)).isNull();
	}

	@Test
	void acceptsAlternativeGatewayHeaderNames() throws Exception {
		var request = new MockHttpServletRequest();
		request.addHeader("X-Request-Id", "gateway-request-id");
		var response = new MockHttpServletResponse();

		filter.doFilterInternal(request, response, filterChain);

		assertThat(response.getHeader(CorrelationId.HEADER_NAME)).isEqualTo("gateway-request-id");
	}

	@Test
	void generatesCorrelationIdWhenHeaderMissing() throws Exception {
		var request = new MockHttpServletRequest();
		var response = new MockHttpServletResponse();

		filter.doFilterInternal(request, response, filterChain);

		assertThat(response.getHeader(CorrelationId.HEADER_NAME)).isNotBlank();
		assertThat(MDC.get(CorrelationId.MDC_KEY)).isNull();
	}
}
