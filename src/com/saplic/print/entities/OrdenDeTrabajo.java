package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;

public class OrdenDeTrabajo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6376170869089398257L;
	private int id;

	private Integer serie;
	private Integer numero;
	private String codigo;
	private Date fechaIngreso;
	private Date fechaVenta;
	private Date fechaEntrega;
	private Boolean estado;
	private Double anticipo;
	private String formaPago;
	private String descripcion;
	private VentaProdServ venta;
	

	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
		
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo= codigo;
	}
	
	
	public Integer getSerie() {
		return serie;
	}
	public void setSerie(Integer serie) {
		this.serie = serie;
	}
	
	
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	
	
	public Double getAnticipo() {
		return anticipo;
	}
	public void setAnticipo(Double anticipo) {
		this.anticipo = anticipo;
	}
	
	
	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	public Date getFechaEntrega() {
		return fechaEntrega;
	}
	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	
	
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	
	
	/*
	 * Tipos de Estados:
	 * 
	 * Activa A
	 * Vendida V
	 * Perdida P
	 * 
	 */
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	//E = Efectivo, T = Tarjeta, C = Cheque
	
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	
	
	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
}
