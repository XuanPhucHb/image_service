package com.nxp.api.service;
//

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.nxp.api.config.LogConfig;
import com.nxp.api.entity.UserToken;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@PropertySource("classpath:application.properties")
@Service
public class JwtService {

	public static Map<String, UserToken> MAP_USER_TOKEN = new HashMap<>();

	static Logger logger = LogConfig.getLogger(JwtService.class);
	public static final String USERNAME = "username";
	public static final String SECRET_KEY = "11111111111111111111111111111111";
	@Value("${service.expired_time}")
	public int EXPIRE_TIME;

	public String generateTokenLogin(String username) {
		String token = null;
		try {
			JWSSigner signer = new MACSigner(generateShareSecret());

			JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
			builder.claim(USERNAME, username);
			builder.expirationTime(generateExpirationDate());

			JWTClaimsSet claimsSet = builder.build();
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

			signedJWT.sign(signer);

			token = signedJWT.serialize();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	private JWTClaimsSet getClaimsFromToken(String token) {
		JWTClaimsSet claims = null;
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			JWSVerifier verifier = new MACVerifier(generateShareSecret());
			if (signedJWT.verify(verifier)) {
				claims = signedJWT.getJWTClaimsSet();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return claims;
	}

	private Date generateExpirationDate() {
		return new Date(System.currentTimeMillis() + EXPIRE_TIME);
	}

	private Long getExpirationDateFromToken(String token) {
		Long expiration = 0L;
		if (MAP_USER_TOKEN.size() > 0) {
			UserToken userToken = MAP_USER_TOKEN.get(token);
			if (userToken != null) {
				expiration = userToken.getExpiredTime();
			} else {
				return null;
			}

		}
		return expiration;
	}

	public String getUsernameFromToken(String token) {
		String username = null;
		try {
			JWTClaimsSet claims = getClaimsFromToken(token);
			if (claims != null)
				username = claims.getStringClaim(USERNAME).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
	}

	private byte[] generateShareSecret() {
		byte[] sharedSecret = new byte[32];
		sharedSecret = SECRET_KEY.getBytes();
		return sharedSecret;
	}

	private Boolean isTokenExpired(String token) {
		Long expiration = getExpirationDateFromToken(token);
		if (expiration == null)
			return true;
		return expiration < System.currentTimeMillis();
	}

	public Boolean validateTokenLogin(String token) {
		if (token == null || token.trim().length() == 0) {
			return false;
		}
		String username = getUsernameFromToken(token);
		if (username == null || username.isEmpty()) {
			return false;
		}
		if (isTokenExpired(token)) {
			return false;
		} else {
			UserToken userToken = MAP_USER_TOKEN.get(token);
			if (userToken != null)
				userToken.setExpiredTime(System.currentTimeMillis() + EXPIRE_TIME);
		}
		return true;
	}
}
