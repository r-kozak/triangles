package com.kozak.triangles.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    public static String toMD5(String inStr) throws NoSuchAlgorithmException {
	MessageDigest md5 = MessageDigest.getInstance("MD5");
	md5.reset();
	md5.update(inStr.getBytes());
	byte[] md5Str = md5.digest();
	BigInteger bigInt = new BigInteger(1, md5Str);
	String hashStr = bigInt.toString(16);

	while (hashStr.length() < 32) {
	    hashStr = "0" + hashStr;
	}
	return hashStr;
    }
}
