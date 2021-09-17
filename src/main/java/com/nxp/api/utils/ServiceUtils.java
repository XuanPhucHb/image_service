package com.nxp.api.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceUtils {

	public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static String getMd5(String input) {
		try {

			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRoleToString(int roleInt) {
		switch (roleInt) {
		case 1:
			return "ADMIN";
		case 2:
			return "USER";
		}
		return null;
	}

	public static String convertDateToString(Date date){
		if(date == null){
			return null;
		}
		return sdf.format(date);
	}
}
