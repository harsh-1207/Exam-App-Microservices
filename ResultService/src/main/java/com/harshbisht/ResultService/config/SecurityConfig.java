package com.harshbisht.ResultService.config;

import com.harshbisht.ResultService.security.HeaderAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final HeaderAuthFilter headerAuthFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(c -> c.disable())
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(a ->
				a
					.requestMatchers(HttpMethod.POST, "/results/submit")
					.hasRole("STUDENT")
					.requestMatchers(HttpMethod.GET, "/results/my")
					.hasRole("STUDENT")
					.requestMatchers(HttpMethod.GET, "/results/exam/**")
					.hasAnyRole("TEACHER", "ADMIN")
					.requestMatchers(HttpMethod.GET, "/results/**")
					.hasAnyRole("STUDENT", "TEACHER", "ADMIN")
					.anyRequest()
					.denyAll()
			)
			.addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
