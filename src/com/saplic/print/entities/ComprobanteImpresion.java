package com.saplic.print.entities;




public class ComprobanteImpresion {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 	
	private String nombre;
	private String impresor;
	private String tipo;
	private String pagina;
	private EmpresaDoc empresaDoc;
	private String codigo;
	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public String getImpresor() {
		return impresor;
	}

	public void setImpresor(String impresor) {
		this.impresor = impresor;
	}

	
	public String getPagina() {
		return pagina;
	}

	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

	
	public EmpresaDoc getEmpresaDoc() {
		return empresaDoc;
	}

	public void setEmpresaDoc(EmpresaDoc empresaDoc) {
		this.empresaDoc = empresaDoc;
	}

	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
