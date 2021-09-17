package com.nxp.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.nxp.api.config.LogConfig;
import com.nxp.api.service.AccountService;
import com.nxp.api.service.JwtService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.nxp.api.entity.Account;

public class JwtAuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

	public final static String TOKEN_HEADER = "authorization";
	Logger logger = LogConfig.getLogger(JwtAuthenticationTokenFilter.class);

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AccountService accountService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authToken = httpRequest.getHeader(TOKEN_HEADER);

		if (authToken != null) {
			if (jwtService.validateTokenLogin(authToken)) {
				logger.info("doFilter.token: " + authToken);
				String username = jwtService.getUsernameFromToken(authToken);

				Account account = accountService.loadUserByUsername(username.toLowerCase());
				if (account != null) {
					boolean enabled = true;
					boolean accountNonExpired = true;
					boolean credentialsNonExpired = true;
					boolean accountNonLocked = true;
					UserDetails userDetail = new User(username, account.getPassword(), enabled, accountNonExpired,
							credentialsNonExpired, accountNonLocked, account.getAuthorities());

					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetail, null, userDetail.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} else {
				String usernameToken = jwtService.getUsernameFromToken(authToken);
				for (int i = 0; i < AccountService.listAccounts.size(); i++) {
					if (AccountService.listAccounts.get(i).getUsername().equals(usernameToken)) {
						AccountService.listAccounts.remove(AccountService.listAccounts.get(i));
					}
				}
				if (JwtService.MAP_USER_TOKEN.size() > 0) {
					JwtService.MAP_USER_TOKEN.remove(authToken);
				}
			}
		}
		chain.doFilter(request, response);
	}
}
