package com.nxp.api.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ACCOUNT")
//	@SequenceGenerator(name = "SEQ_ACCOUNT", sequenceName = "SEQ_ACCOUNT", allocationSize = 1)
	private Long id;

	@Column(unique = true, nullable = false)	
	private String username;

	@Column(nullable = false, updatable = false)
	private String password;

	@Column(nullable = false)
	private String fullname;

	private String email;

	private String phone;

	private String penname;

	@Column(updatable = false)
	private String createdBy;
	private String updatedBy;
	@Column(updatable = false)
	private Date createdDate;
	private Date updatedDate;

	@Transient
	private String[] roles = new String[] { "ROLE_ADMIN" };

	private Long roleId;

	public List<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
