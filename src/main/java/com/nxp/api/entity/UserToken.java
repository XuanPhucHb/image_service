package com.nxp.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToken {
    private String token;
	private String username;
	private long expiredTime;
}
