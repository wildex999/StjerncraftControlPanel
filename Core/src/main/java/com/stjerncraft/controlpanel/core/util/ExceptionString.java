package com.stjerncraft.controlpanel.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionString {
	public static String PrintException(Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		printWriter.flush();
		
		return writer.toString();
	}
}
