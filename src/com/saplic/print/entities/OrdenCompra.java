package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrdenCompra implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fecha;
	private String proveedor;
	private String encargado;
	private String recibira;
	private List<DetOrdenCompra> detOrdenCompra;
	
	public OrdenCompra() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	
	public String getEncargado() {
		return encargado;
	}
	public void setEncargado(String encargado) {
		this.encargado = encargado;
	}

	public String getRecibira() {
		return recibira;
	}

	public void setRecibira(String recibira) {
		this.recibira = recibira;
	}

	public List<DetOrdenCompra> getDetOrdenCompra() {
		return detOrdenCompra;
	}

	public void setDetOrdenCompra(List<DetOrdenCompra> detOrdenCompra) {
		this.detOrdenCompra = detOrdenCompra;
	}
}
