package com.saplic.print.entities;

import java.io.Serializable;

public class CotizacionDet implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Double cantidad;
	private Double precioUnitario;
	private Double monto;
	private String abreviaturaUni;
	private Double costoMercaderia;
	private String nomUnidad;
	private Cotizacion cotizacion;
	private String descripcion;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	public Double getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(Double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public String getAbreviaturaUni() {
		return abreviaturaUni;
	}
	public void setAbreviaturaUni(String abreviaturaUni) {
		this.abreviaturaUni = abreviaturaUni;
	}
	public Double getCostoMercaderia() {
		return costoMercaderia;
	}
	public void setCostoMercaderia(Double costoMercaderia) {
		this.costoMercaderia = costoMercaderia;
	}
	public String getNomUnidad() {
		return nomUnidad;
	}
	public void setNomUnidad(String nomUnidad) {
		this.nomUnidad = nomUnidad;
	}
	public Cotizacion getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(Cotizacion cotizacion) {
		this.cotizacion = cotizacion;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
