package com.saplic.print.entities;

import java.io.Serializable;

 
public class ClienteDoc implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	private Integer id; 
	private String nombre; 
	private String apellido; 
	private String giro; 
	private String direccion;
	private String dui;
	private String nit;
	private String telefono1;
	private String telefono2;
	private String nrc;
	private Short tipoContribuyente;
	private boolean exento = false;
	private boolean omisionMinimoRet = false;
	private Integer idCliente;
	
	
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
	
	
	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	
	public String getDui() {
		return dui;
	}

	public void setDui(String dui) {
		this.dui = dui;
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

	
	public Short getTipoContribuyente() {
		return tipoContribuyente;
	}

	public void setTipoContribuyente(Short tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}

	
	public boolean isExento() {
		return exento;
	}

	public void setExento(boolean exento) {
		this.exento = exento;
	}

	
	public String getGiro() {
		return giro;
	}

	public void setGiro(String giro) {
		this.giro = giro;
	}
	
	
	public boolean isOmisionMinimoRet() {
		return omisionMinimoRet;
	}

	public void setOmisionMinimoRet(boolean omisionMinimoRet) {
		this.omisionMinimoRet = omisionMinimoRet;
	}

	
	public String getPersonaoempresa() {
		if(tipoContribuyente != null) {
			if(tipoContribuyente < 3)
				return nombre + " " + apellido;
			else
				return nombre;
		} else
			return nombre + " " + apellido;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
	
	
	
}
