package com.aitheras.trainer.util;

import java.io.PrintWriter;
import java.io.StringWriter;

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
}
