package com.aitheras.trainer.util;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Misc static helper methods
 * @author Steve Carton, stephen.carton@aitheras.com
 *
 */
public final class Util {
	private Util(){}
	public static String stackTrace(Throwable e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	public static String md5(String k) throws NoSuchAlgorithmException {
	    MessageDigest hasher = MessageDigest.getInstance("MD5");
	    byte[] kbh = hasher.digest(k.getBytes());
	    String bs = new BigInteger(1,kbh).toString(16);
	    return bs;
	}
	public static InputStream getResourceAsStream(String n) {
		InputStream in = Util.class.getResourceAsStream(n);
		return in;
	}
}
