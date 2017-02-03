package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;

public class CajaRegistradora implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Short numero;
	private String estado; //ACT=Activo; INA=INACTIVO;
	private String numResolucion;
	private Date fechaResolucion;
	private Integer sucursalId;
	
	public CajaRegistradora() {
		this.estado = "ACT";
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public String getNumResolucion() {
		return numResolucion;
	}

	public void setNumResolucion(String numResolucion) {
		this.numResolucion = numResolucion;
	}

	public Date getFechaResolucion() {
		return fechaResolucion;
	}

	public void setFechaResolucion(Date fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public Integer getSucursalId() {
		return sucursalId;
	}

	public void setSucursalId(Integer sucursalId) {
		this.sucursalId = sucursalId;
	}
}
