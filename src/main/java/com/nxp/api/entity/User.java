package com.nxp.api.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "roles", "authorities", "id" })
@Data
public class User {
	private int id;
	private String username;
	private String password;
	private String fullname;
	private String[] roles = new String[] { "ROLE_ADMIN" };

	public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", roles=" + roles + '}';
	}
}
