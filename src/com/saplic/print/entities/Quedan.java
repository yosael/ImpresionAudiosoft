package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;

public class Quedan implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String proveedor;
	private Double monto;
	private Date fechaEmision;
	private Date fechaPago;
	private String documentos;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}
	public String getDocumentos() {
		return documentos;
	}
	public void setDocumentos(String documentos) {
		this.documentos = documentos;
	}
}
