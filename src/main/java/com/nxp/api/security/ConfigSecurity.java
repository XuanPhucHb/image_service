package com.nxp.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {
	@Bean
	public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
		JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
		jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager());
		return jwtAuthenticationTokenFilter;
	}

	@Bean
	public RestAuthenticationEntryPoint restServicesEntryPoint() {
		return new RestAuthenticationEntryPoint();
	}

	@Bean
	public CustomAccessDeniedHandler customAccessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().ignoringAntMatchers("/dvdd/v1/**");

		http.authorizeRequests().antMatchers("/dvdd/v1/account/login**").permitAll();
//		http.authorizeRequests().antMatchers("/dvdd/v1/account/add**").permitAll();
		http.authorizeRequests().antMatchers("/dvdd/v1/operator/landing/**").permitAll();
		http.authorizeRequests().antMatchers("/dvdd/v1/packagedata/landing/**").permitAll();
		http.authorizeRequests().antMatchers("/dvdd/v1/sale/landing/**").permitAll();
		http.authorizeRequests().antMatchers("/dvdd/v1/banner/landing/**").permitAll();
		http.authorizeRequests().antMatchers("/dvdd/v1/news/landing/**").permitAll();
//		http.authorizeRequests().antMatchers("/dvdd/v1/news/**").permitAll();

		http.antMatcher("/dvdd/v1/**").httpBasic().authenticationEntryPoint(restServicesEntryPoint()).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(HttpMethod.GET, "/dvdd/v1/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
				.antMatchers(HttpMethod.POST, "/dvdd/v1/**").access("hasRole('ROLE_ADMIN')")
				.antMatchers(HttpMethod.DELETE, "/dvdd/v1/**").access("hasRole('ROLE_ADMIN')").and()
				.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
	}
}
