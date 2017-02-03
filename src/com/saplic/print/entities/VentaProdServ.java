package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.saplic.print.entities.Usuario;
import com.saplic.print.entities.OrdenDeTrabajo;

public class VentaProdServ implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaVenta;
	private String tipoVenta;
	private Float monto;
	private Float prcTarjeta;
	private Cliente cliente;
	private ClienteCorporativo cliCorp;
	private Sucursal sucursal;
	private String detalle;
	private Usuario usrEfectua;
	private Usuario usrDescuento;
	private String tipoDescuento;
	private Double cantidadDescuento;
	//private Integer idDetalle;
	private List<DetVentaProdServ> detVenta;
	private Double restante;
	private String codTipoVenta;
	private String codTip;
	
	
	private String tipoDeDocumento;
	private String codComprobante;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public String getTipoVenta() {
		return tipoVenta;
	}
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	public Float getMonto() {
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
	public Float getPrcTarjeta() {
		return prcTarjeta;
	}
	public void setPrcTarjeta(Float prcTarjeta) {
		this.prcTarjeta = prcTarjeta;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public ClienteCorporativo getCliCorp() {
		return cliCorp;
	}
	public void setCliCorp(ClienteCorporativo cliCorp) {
		this.cliCorp = cliCorp;
	}
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public Usuario getUsrEfectua() {
		return usrEfectua;
	}
	public void setUsrEfectua(Usuario usrEfectua) {
		this.usrEfectua = usrEfectua;
	}
	public Usuario getUsrDescuento() {
		return usrDescuento;
	}
	public void setUsrDescuento(Usuario usrDescuento) {
		this.usrDescuento = usrDescuento;
	}
	public String getTipoDescuento() {
		return tipoDescuento;
	}
	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}
	public Double getCantidadDescuento() {
		return cantidadDescuento;
	}
	public void setCantidadDescuento(Double cantidadDescuento) {
		this.cantidadDescuento = cantidadDescuento;
	}
	public List<DetVentaProdServ> getDetVenta() {
		return detVenta;
	}
	public void setDetVenta(List<DetVentaProdServ> detVenta) {
		this.detVenta = detVenta;
	}
	public Double getRestante() {
		return restante;
	}
	public void setRestante(Double restante) {
		this.restante = restante;
	}
	public String getCodTipoVenta() {
		return codTipoVenta;
	}
	public void setCodTipoVenta(String codTipoVenta) {
		this.codTipoVenta = codTipoVenta;
	}
	public String getCodTip() {
		return codTip;
	}
	public void setCodTip(String codTip) {
		this.codTip = codTip;
	}
	
	public String getTipoDeDocumento() {
		return tipoDeDocumento;
	}
	public void setTipoDeDocumento(String tipoDeDocumento) {
		this.tipoDeDocumento = tipoDeDocumento;
	}
	public String getCodComprobante() {
		return codComprobante;
	}
	public void setCodComprobante(String codComprobante) {
		this.codComprobante = codComprobante;
	}
	
	
	
	
	
}