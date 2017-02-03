package com.saplic.print.entities;

import java.io.Serializable;

public class DetOrdenCompra implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Float cantidad;
	private String nombre;
	
	public DetOrdenCompra() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Float getCantidad() {
		return cantidad;
	}
	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
