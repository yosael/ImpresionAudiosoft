package com.saplic.print.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Cotizacion implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaIngreso;
	private String Descripcion;
	private Double totalCost;
	private Integer numSucursal;
	private Cliente cliente;
	private Integer numCaja;
	private List<CotizacionDet> detCotizacion;
	private Usuario usrEfectua;
	private Usuario usrIngresa;
	private InfoEmpresa infoEmpresa;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	public String getDescripcion() {
		return Descripcion;
	}
	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Integer getNumSucursal() {
		return numSucursal;
	}
	public void setNumSucursal(Integer numSucursal) {
		this.numSucursal = numSucursal;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Integer getNumCaja() {
		return numCaja;
	}
	public void setNumCaja(Integer numCaja) {
		this.numCaja = numCaja;
	}
	public List<CotizacionDet> getDetCotizacion() {
		return detCotizacion;
	}
	public void setDetCotizacion(List<CotizacionDet> detCotizacion) {
		this.detCotizacion = detCotizacion;
	}
	public Usuario getUsrEfectua() {
		return usrEfectua;
	}
	public void setUsrEfectua(Usuario usrEfectua) {
		this.usrEfectua = usrEfectua;
	}
	public Usuario getUsrIngresa() {
		return usrIngresa;
	}
	public void setUsrIngresa(Usuario usrIngresa) {
		this.usrIngresa = usrIngresa;
	}
	public InfoEmpresa getInfoEmpresa() {
		return infoEmpresa;
	}
	public void setInfoEmpresa(InfoEmpresa infoEmpresa) {
		this.infoEmpresa = infoEmpresa;
	}
	
}
