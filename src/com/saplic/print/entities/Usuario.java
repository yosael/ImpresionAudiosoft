package com.saplic.print.entities;

import java.util.Date;

public class Usuario {
	private Integer id;
	private String nombreUsuario;
	private String nombreCompleto;
	private String codVendedor;
	private Integer caja;
	private Integer sucursal;
	private String pass;
	private Date fechaUltimoAcceso; 
	private Integer numeroIntentos;
	private String estado; //ACT=Activo; INA=Inactivo
	private boolean accionEspecial;
	private boolean notificacionInv = false;
	private String codigoRol;
	//Caja registradora del usuario
	private CajaRegistradora cajaRegistradora;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getCodVendedor() {
		return codVendedor;
	}
	public void setCodVendedor(String codVendedor) {
		this.codVendedor = codVendedor;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public Date getFechaUltimoAcceso() {
		return fechaUltimoAcceso;
	}
	public void setFechaUltimoAcceso(Date fechaUltimoAcceso) {
		this.fechaUltimoAcceso = fechaUltimoAcceso;
	}
	public Integer getNumeroIntentos() {
		return numeroIntentos;
	}
	public void setNumeroIntentos(Integer numeroIntentos) {
		this.numeroIntentos = numeroIntentos;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public boolean isAccionEspecial() {
		return accionEspecial;
	}
	public void setAccionEspecial(boolean accionEspecial) {
		this.accionEspecial = accionEspecial;
	}
	public boolean isNotificacionInv() {
		return notificacionInv;
	}
	public void setNotificacionInv(boolean notificacionInv) {
		this.notificacionInv = notificacionInv;
	}
	public String getCodigoRol() {
		return codigoRol;
	}
	public void setCodigoRol(String codigoRol) {
		this.codigoRol = codigoRol;
	}
	public Integer getCaja() {
		return caja;
	}
	public void setCaja(Integer caja) {
		this.caja = caja;
	}
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}
	public CajaRegistradora getCajaRegistradora() {
		return cajaRegistradora;
	}
	public void setCajaRegistradora(CajaRegistradora cajaRegistradora) {
		this.cajaRegistradora = cajaRegistradora;
	}
}
