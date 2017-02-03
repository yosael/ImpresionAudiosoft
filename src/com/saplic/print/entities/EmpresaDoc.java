package com.saplic.print.entities;

import java.io.Serializable;



 
public class EmpresaDoc implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String direccion;
	private String nit;
	private String telefono1;
	private String telefono2;
	private String nrc;
	private int tipoContribuyente; 
	private boolean exento = false;
	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	
	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	
	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	
	public String getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	
	public String getNrc() {
		return nrc;
	}

	public void setNrc(String nrc) {
		this.nrc = nrc;
	}

	
	public int getTipoContribuyente() {
		return tipoContribuyente;
	}

	public void setTipoContribuyente(int tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}

	
	public boolean isExento() {
		return exento;
	}

	public void setExento(boolean exento) {
		this.exento = exento;
	}
}
