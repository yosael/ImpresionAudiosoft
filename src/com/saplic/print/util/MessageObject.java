package com.saplic.print.util;

public class MessageObject {

	public static final Byte ERROR = 1;
	public static final Byte INFO = 2;
	
	public MessageObject(Byte tp, String msg) {
		tipo = tp;
		mensaje = msg;
	}
	
	private Byte tipo;
	private String mensaje;
	
	public Byte getTipo() {
		return tipo;
	}
	
	public void setTipo(Byte tipo) {
		this.tipo = tipo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
}
