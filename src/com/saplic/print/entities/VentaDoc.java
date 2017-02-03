package com.saplic.print.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class VentaDoc implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Long correlativo;
	private String serie;  
	private Date fecha;
	private Sucursal sucursal;
	private Float total = new Float(0);
	private Float iva = new Float(0);
	private Float percibido = new Float(0);
	private Float retenido = new Float(0);
	private String formaPago;	
	private String estado;
	private Float descuento = new Float(0);
	private ComprobanteImpresion comprobante;
	private ClienteDoc cliente;
	private List<DetVentaDoc> detVentas = new ArrayList<DetVentaDoc>();
	private Usuario usuario;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public Long getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Long correlativo) {
		this.correlativo = correlativo;
	}
	
	
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	
	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public Float getIva() {
		return iva;
	}

	public void setIva(Float iva) {
		this.iva = iva;
	}

	
	public Float getPercibido() {
		return percibido;
	}

	public void setPercibido(Float percibido) {
		this.percibido = percibido;
	}

	
	public Float getRetenido() {
		return retenido;
	}

	public void setRetenido(Float retenido) {
		this.retenido = retenido;
	}

	//EFE = Efectivo, TRJ = Tarjeta, CHQ = Cheque, CRD = Credito
	
	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	
	public Float getDescuento() {
		return descuento;
	}

	public void setDescuento(Float descuento) {
		this.descuento = descuento;
	}

	
	public ComprobanteImpresion getComprobante() {
		return comprobante;
	}

	public void setComprobante(ComprobanteImpresion comprobante) {
		this.comprobante = comprobante;
	}

	
	public ClienteDoc getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDoc cliente) {
		this.cliente = cliente;
	}

	
	public List<DetVentaDoc> getDetVentas() {
		return detVentas;
	}

	public void setDetVentas(List<DetVentaDoc> detVentas) {
		this.detVentas = detVentas;
	}

	
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	//APL = Aplicado, ANU = Anulado
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	
	
}
