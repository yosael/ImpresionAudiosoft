package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;

public class SolicitudImpresion implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Usuario usuario;
	private Integer caja;
	private VentaProdServ venta;
	private Cotizacion cotizacion;
	private Date fecha;
	private Quedan quedan;
	private OrdenCompra ordenCompra;
	private VentaDoc ventaDoc;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Integer getCaja() {
		return caja;
	}
	public void setCaja(Integer caja) {
		this.caja = caja;
	}

	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
	public Cotizacion getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(Cotizacion cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Quedan getQuedan() {
		return quedan;
	}
	public void setQuedan(Quedan quedan) {
		this.quedan = quedan;
	}
	
	public OrdenCompra getOrdenCompra() {
		return ordenCompra;
	}
	public void setOrdenCompra(OrdenCompra ordenCompra) {
		this.ordenCompra = ordenCompra;
	}
	
	
	public VentaDoc getVentaDoc() {
		return ventaDoc;
	}
	public void setVentaDoc(VentaDoc ventaDoc) {
		this.ventaDoc = ventaDoc;
	}
	
	
}
