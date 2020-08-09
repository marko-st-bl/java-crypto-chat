package org.unibl.etf.kripto.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;

public class PasswordUtil {

	public static String getHash(String salt, String password) {
		MessageDigest digest;
		String retVal = "";
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(salt.getBytes(StandardCharsets.UTF_8));
			retVal = new String(Hex.encode(digest.digest(password.getBytes(StandardCharsets.UTF_8))));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
