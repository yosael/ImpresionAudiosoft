package com.saplic.print.entities;

import java.io.Serializable;

public class DetVentaProdServ implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private VentaProdServ venta;
	private String detalle;
	private String detalleResumen;
	private Double cantidad;
	private Float cantidadDescuento;
	private Float precioUnitario;
	private String tipoVenta;
	private Float monto;
	private boolean escondido;
	private boolean garantia;
	private String abreviaturaUni;
	private Double costoMercaderia;
	//pruebaComent
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
		
	public Float getMonto() {
		if(monto == null)
			return 0f;
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
	
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	
	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
	public Double getCantidad() {
		return cantidad;
	}
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	
	public boolean isEscondido() {
		return escondido;
	}
	public void setEscondido(boolean escondido) {
		this.escondido = escondido;
	}
	
	public String getDetalleResumen() {
		return detalleResumen;
	}
	public void setDetalleResumen(String detalleResumen) {
		this.detalleResumen = detalleResumen;
	}

	public Double getTotalDet() {
		if(cantidad == null || cantidad == 0 ||
				monto == null || monto == 0)
			return 0d;
		else
			return Double.valueOf(cantidad * monto);
	}
	
	public Double getTotalDetIva() {
		if(cantidad == null || cantidad == 0 ||
				monto == null || monto == 0)
			return 0d;
		else
			return Double.valueOf((cantidad * monto) + (cantidad * monto * 0.13));
	}
	
	public Float getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(Float precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	
	public String getTipoVenta() {
		return tipoVenta;
	}
	
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	
	public Float getCantidadDescuento() {
		return cantidadDescuento;
	}
	
	public void setCantidadDescuento(Float cantidadDescuento) {
		this.cantidadDescuento = cantidadDescuento;
	}
	
	public boolean isGarantia() {
		return garantia;
	}
	public void setGarantia(boolean garantia) {
		this.garantia = garantia;
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
}