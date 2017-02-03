package com.saplic.print.entities;
import java.io.Serializable;

public class InfoEmpresa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codEmpresa;
	private String nit;
	private String nrc;
	private String nombre;
	private String nombreComercial;
	private String giro;
	private String direccion;
	private String telefono1;
	private String telefono2;
	private String fax;
	private byte tipoContribuyente;
	private String estado = "INA";
	
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
	
	public String getNombreComercial() {
		return nombreComercial;
	}
	public void setNombreComercial(String nombreComercial) {
		this.nombreComercial = nombreComercial;
	}
	
	public String getCodEmpresa() {
		return codEmpresa;
	}
	public void setCodEmpresa(String codEmpresa) {
		this.codEmpresa = codEmpresa;
	}
	
	public String getNit() {
		if(nit == null)
			return "";
		return nit;
	}
	public void setNit(String nit) {
		this.nit = nit;
	}
	
	public String getNrc() {
		return nrc;
	}
	public void setNrc(String nrc) {
		this.nrc = nrc;
	}
	
	public String getGiro() {
		return giro;
	}
	public void setGiro(String giro) {
		this.giro = giro;
	}
	
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
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
	
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public byte getTipoContribuyente() {
		return tipoContribuyente;
	}
	public void setTipoContribuyente(byte tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}
	
	
}
