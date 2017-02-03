package com.saplic.print.util;

import java.util.Map;
import java.util.Set;

public class DialogMessageGenerator {
	
	public static String getMessageDialog(Map<String, MessageObject> mensajes) {
		Set<String> llaves = mensajes.keySet();
		StringBuilder bld = new StringBuilder();
		for(String ky : llaves) {
			bld.append("+ ");
			bld.append(mensajes.get(ky).getMensaje());
			bld.append("\n");
		}
		return bld.toString();
	}
}
