package com.saplic.print.entities;

import java.io.Serializable;





public class DetVentaDoc implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private VentaDoc venta;
	private Integer cantidad;
	private String detalle;
	private Float precioUnitario;
	private Float total;
	private String tipo;
	//private Producto producto;
	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public VentaDoc getVenta() {
		return venta;
	}

	public void setVenta(VentaDoc venta) {
		this.venta = venta;
	}

	
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	
	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	
	public Float getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(Float precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	
	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	/*public Inventario getProducto() {
		return producto;
	}

	public void setProducto(Inventario producto) {
		this.producto = producto;
	}	*/
}
