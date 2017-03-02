/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.reactive.result.method.annotation;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.ResolvableMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.session.MockWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ServerWebExchangeArgumentResolver}.
 * @author Rossen Stoyanchev
 */
public class ServerWebExchangeArgumentResolverTests {

	private final ServerWebExchangeArgumentResolver resolver = new ServerWebExchangeArgumentResolver();

	private ServerWebExchange exchange;

	private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();


	@Before
	public void setup() throws Exception {
		ServerHttpRequest request = MockServerHttpRequest.get("/path").build();
		ServerHttpResponse response = new MockServerHttpResponse();

		WebSessionManager sessionManager = new MockWebSessionManager(mock(WebSession.class));
		this.exchange = new DefaultServerWebExchange(request, response, sessionManager);
	}


	@Test
	public void supportsParameter() throws Exception {
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerWebExchange.class)));
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerHttpRequest.class)));
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(ServerHttpResponse.class)));
		assertTrue(this.resolver.supportsParameter(this.testMethod.arg(HttpMethod.class)));
		assertFalse(this.resolver.supportsParameter(this.testMethod.arg(String.class)));
	}

	@Test
	public void resolveArgument() throws Exception {
		testResolveArgument(this.testMethod.arg(ServerWebExchange.class), this.exchange);
		testResolveArgument(this.testMethod.arg(ServerHttpRequest.class), this.exchange.getRequest());
		testResolveArgument(this.testMethod.arg(ServerHttpResponse.class), this.exchange.getResponse());
		testResolveArgument(this.testMethod.arg(HttpMethod.class), HttpMethod.GET);
	}


	private void testResolveArgument(MethodParameter parameter, Object expected) {
		Mono<Object> mono = this.resolver.resolveArgument(parameter, new BindingContext(), this.exchange);
		assertSame(expected, mono.block());
	}


	@SuppressWarnings("unused")
	public void handle(
			ServerWebExchange exchange,
			ServerHttpRequest request,
			ServerHttpResponse response,
			WebSession session,
			HttpMethod httpMethod,
			String s) {
	}

}
