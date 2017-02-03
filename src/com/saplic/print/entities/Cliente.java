package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;
 
public class Cliente implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer tipoCliente;
	private String nombre;
	private String apellido;
	private String direccion;
	private String razonSocial;
	private String telFijo;
	private String telCelular;
	private String email;
	private String dui;
	private String nit;
	private String numRegistro;
	private String giro;
	private Date fechaNacimiento;
	private String clasificacion1;
	private String clasificacion2;
	private String tipoDeCliente;
	private String Departamento;
	private String Municipio;
	private Short genero;
	private Byte tipoContribuyente;
	private Boolean exento=false;
	private String estado;
	private String contacto;
	
	private String clasificacion;	
	private String nombreTarIva;
	private Byte categoria;
	
	
	public Cliente(){

	}
	
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getTelFijo() {
		return telFijo;
	}

	public void setTelFijo(String telFijo) {
		this.telFijo = telFijo;
	}

	public String getTelCelular() {
		return telCelular;
	}

	public void setTelCelular(String telCelular) {
		this.telCelular = telCelular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDui() {
		return dui;
	}

	public void setDui(String dui) {
		this.dui = dui;
	}

	public String getNit() {
		if(nit == null)
			return "";
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}
    
	public String getNumRegistro() {
		return numRegistro;
	}

	public void setNumRegistro(String nreg) {
		this.numRegistro = nreg;
	}
	
	public String getGiro() {
		return giro;
	}

	public void setGiro(String giro) {
		this.giro = giro;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Short getGenero() {
		return genero;
	}

	public void setGenero(Short genero) {
		this.genero = genero;
	}
    
	public Byte getTipoContribuyente() {
		if(tipoContribuyente == null)
			return (byte)0;
		
		return tipoContribuyente;
	}
    
	public void setTipoContribuyente(Byte tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}
	
	public Boolean getExento() {
		if(exento != null)
			return exento;
		else
			return false;
	}

	public void setExento(Boolean exento) {
		this.exento = exento;
	}
	
	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	} 
	
	public Integer getTipoCliente() { 
		return tipoCliente;
	}

	public void setTipoCliente(Integer tipoCliente) {
		this.tipoCliente = tipoCliente;
	}
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public String getDuionit() {
		if(dui != null && !dui.trim().equals(""))
			return dui;
		else if(nit != null && !nit.trim().equals(""))
			return nit;
		else
			return "";
	}

	public String getPersonaoempresa() {
		if(tipoCliente != null) {
			if(tipoCliente < 3)
				return nombre + ((apellido!=null && !apellido.trim().equalsIgnoreCase(""))?" " + apellido:"");
			else
				return razonSocial;
		} else
			return nombre + ((apellido!=null && !apellido.trim().equalsIgnoreCase(""))?" " + apellido:"");
	}

	public String getClasificacion1() {
		return clasificacion1;
	}

	public void setClasificacion1(String clasificacion1) {
		this.clasificacion1 = clasificacion1;
	}

	public String getClasificacion2() {
		return clasificacion2;
	}

	public void setClasificacion2(String clasificacion2) {
		this.clasificacion2 = clasificacion2;
	}

	public String getTipoDeCliente() {
		return tipoDeCliente;
	} 

	public void setTipoDeCliente(String tipoDeCliente) {
		this.tipoDeCliente = tipoDeCliente;
	}
	
	public String getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	
	public String getNombreTarIva() {
		return nombreTarIva;
	}

	public void setNombreTarIva(String nombreTarIva) {
		this.nombreTarIva = nombreTarIva;
	}
	
	public Byte getCategoria() {
		return categoria;
	}

	public void setCategoria(Byte categoria) {
		this.categoria = categoria;
	}

	public String getMunicipio() {
		return Municipio;
	}

	public void setMunicipio(String municipio) {
		Municipio = municipio;
	}

	public String getDepartamento() {
		return Departamento;
	}

	public void setDepartamento(String departamento) {
		Departamento = departamento;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	
}
