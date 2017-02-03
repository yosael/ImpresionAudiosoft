package com.saplic.print.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.saplic.print.entities.Cliente;
import com.saplic.print.entities.ClienteDoc;
import com.saplic.print.entities.ComprobanteImpresion;
import com.saplic.print.entities.Cotizacion;
import com.saplic.print.entities.CotizacionDet;
import com.saplic.print.entities.DetOrdenCompra;
import com.saplic.print.entities.DetVentaDoc;
import com.saplic.print.entities.DetVentaProdServ;
import com.saplic.print.entities.EmpresaDoc;
import com.saplic.print.entities.InfoEmpresa;
import com.saplic.print.entities.OrdenCompra;
import com.saplic.print.entities.OrdenDeTrabajo;
import com.saplic.print.entities.Quedan;
import com.saplic.print.entities.SolicitudImpresion;
import com.saplic.print.entities.UserSession;
import com.saplic.print.entities.Usuario;
import com.saplic.print.entities.VentaDoc;
import com.saplic.print.entities.VentaProdServ;
import com.saplic.print.util.ConnectionManager;
import com.saplic.print.util.EpsonEscCommand;
import com.saplic.print.util.Numalet;
import com.saplic.print.util.PrintJobWatcher;
import com.saplic.print.util.Printer;
import com.saplic.print.util.PrinterFactory;
import com.saplic.print.util.PrinterUtils;
import com.saplic.print.util.PropertiesLoader;

import sun.security.mscapi.RSACipher;

public class Print implements Job {

	Connection connection = null;
	PreparedStatement preparedStatementSelect = null;
	PreparedStatement preparedStatementUpdate = null;
	private final short MAX_CHAR_TKT = 40;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat mf = new DecimalFormat("0.00");
	DecimalFormat intf = new DecimalFormat("0");
	DecimalFormat centf = new DecimalFormat("00");
	private static final String REGEX_CAMPO_IMPRESION = "\\[\\|.+?:.*?\\|\\]";
	private Numalet numalet = new Numalet();
	private String impresora = null;
	private short MAX_CHAR_CPR = (short) 0;
	// de numero de mes a nombre
	private static Map<Integer, String> meses;

	static {
		meses = new HashMap<Integer, String>();
		meses.put(0, "ENERO");
		meses.put(1, "FEBRERO");
		meses.put(2, "MARZO");
		meses.put(3, "ABRIL");
		meses.put(4, "MAYO");
		meses.put(5, "JUNIO");
		meses.put(6, "JULIO");
		meses.put(7, "AGOSTO");
		meses.put(8, "SEPTIEMBRE");
		meses.put(9, "OCTUBRE");
		meses.put(10, "NOVIEMBRE");
		meses.put(11, "DICIEMBRE");
	}

	// Consultar ventas que no han sido impresas
	//private static final String selectSolImpSQL = "SELECT * FROM solicitud_impresion WHERE 1 = 1 AND usuario_id = ? ";
	private static final String selectSolImpSQLUser = "SELECT * FROM solicitud_impresion WHERE 1 = 1 AND usuario_id = ? ";
	private static final String selectSolImpSQLAll = "SELECT * FROM solicitud_impresion WHERE 1 = 1 ";
	//private static final String selectInfoEmpresaSQL = "SELECT * FROM info_empresa WHERE 1 = 1 AND sgrprd_id = ? "; cambiado el 13/01/2017
	private static final String selectInfoEmpresaSQL = "SELECT * FROM empresa_doc WHERE 1 = 1 AND empdoc_id = ? ";
	/*private static final String selectClienteSQL = "SELECT c.client_id,c.nombre,c.apellido,c.razon_social,c.tel_celular,c.direccion,c.num_registro,c.nit,c.giro,c.tipo_cliente,c.contacto "
			+ " FROM cliente c WHERE 1 = 1 AND c.client_id = ?";*/
	private static final String selectClienteSQL = "SELECT * "
			+ " FROM cliente c WHERE 1 = 1 AND c.cliente_id = ?";
	
	private static final String selectClienteDocSQL = "SELECT * "
			+ " FROM cliente_doc c WHERE 1 = 1 AND c.id = ?";
	
	private static final String deleteSolPrintSQL = "DELETE FROM solicitud_impresion WHERE sol_print_id = ?";
	
	
	// Para la impresion de Facturas, hojas de trabajo y creditos fiscales
	private static final String selectVentaProdServSQL = "SELECT venta_prod_serv.*, sucursal.empresa_id infemp_id, usuario.nombre_usuario FROM venta_prod_serv "
			+ "INNER JOIN sucursal ON venta_prod_serv.sucursal_id = sucursal.id "
			+ "LEFT JOIN usuario ON usuario.id = venta_prod_serv.usrdsc_id WHERE 1 = 1 AND vtaprsv_id = ?"; // Se cambio venta_prod_serv.usrven_id por usrdsc_id el 13/01/2017
	
	//Query para ventas doc
	private static final String selectVentaDocSQL = "SELECT venta_doc.*, sucursal.empresa_id infemp_id, usuario.nombre_usuario,comprob.id id_comprobante FROM venta_doc "
			+ "INNER JOIN sucursal ON venta_doc.sucursal_id = sucursal.id "
			+ "LEFT JOIN usuario ON usuario.id = venta_doc.usuario_id left join comprobante_impresion comprob on(comprob.id=venta_doc.comprobante_impresion_id) WHERE 1 = 1 AND vtadoc_id = ?";
	
	private static final String selectComprobanteByVentaDoc= "SELECT * from comprobante_impresion where id= ? ";
	
	
	private static final String selectDetVentaSQL = "SELECT * FROM det_venta_prod_serv WHERE 1 = 1 AND venta_id = ?";
	
	
	//Para detalle venta doc
	private static final String selectDetVentaDocSQL = "SELECT * FROM det_venta_doc WHERE 1 = 1 AND vtadoc_id = ?";
	
	// Para la impresion de cotizaciones
	private static final String selectCotizacionSQL = "SELECT cotizacion.*, sucursal.empresa_id infemp_id "
			+ "	FROM cotizacion, sucursal WHERE 1 = 1 AND cotizacion.sucursal_id = sucursal.id AND cotizacion.id = ? ";
	private static final String selectCotizacionDetSQL = "SELECT * FROM cotizacion_det WHERE 1 = 1 AND cotizacion_id = ?";

	// para obtener las orden de trabajo de una venta en especifico
	private static final String selectOrdenTrabajoSQL = "SELECT * FROM orden_trabajo WHERE venta_id=?";

	// Para obtener datos del usuario vendedor
	private static final String selectVendedorSQL = "SELECT * FROM usuario WHERE id=?";

	// Para obtener informacion del quedan
	private static final String selectQuedanSQL = "SELECT q.id, q.monto, p.razon_social, q.fecha_pago, q.fecha_emision, string_agg(c.numero_factura, ',')"
			+ " FROM quedan q JOIN det_quedan dq ON dq.quedan = q.id JOIN compra c ON c.id = dq.compra JOIN proveedor p ON p.id = c.proveedor_id"
			+ " GROUP BY q.id, q.monto, p.razon_social, q.fecha_pago, q.fecha_emision HAVING q.id=?";
	// Para obtenre informacion de las ordenes de compra
	private static final String selectOrdenCompraSQL = "SELECT oc.*, pr.razon_social FROM orden_compra oc LEFT JOIN proveedor pr ON oc.proveedor_id = pr.id WHERE oc.id = ?";
	private static final String selectOrdenCompraDetSQL = "SELECT ocd.cantidad, p.nombre FROM orden_compra_det ocd LEFT JOIN producto p ON p.id = ocd.producto_id WHERE orden_id = ?";

	private String tipoDocumento = "";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SolicitudImpresion solicitudImpresion = new SolicitudImpresion();
		System.out.println("Ejecutanto el Proceso");
		System.out.println("Inicio: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
		System.out.println("+");

		if (ConnectionManager.getConnection() != null)
			connection = ConnectionManager.getConnection();

		if (impresora == null || impresora.trim().isEmpty())
			impresora = PropertiesLoader.appProperties.getProperty("global.printer.name");

		if (MAX_CHAR_CPR <= 0)
			MAX_CHAR_CPR = new Short(PropertiesLoader.appProperties.getProperty("global.printer.columns").toString());

		try {
			
			
			connection.setAutoCommit(false);
			/*if (UserSession.getUsr().getCaja() == -1) {
				preparedStatementSelect = connection.prepareStatement(selectSolImpSQLUser);
				preparedStatementSelect.setInt(1, UserSession.getUsr().getId());
			} else if (UserSession.getUsr().getCaja() == -2) {
				preparedStatementSelect = connection.prepareStatement(selectSolImpSQLAll);
			} else {
				preparedStatementSelect = connection.prepareStatement(selectSolImpSQL);
				preparedStatementSelect.setInt(1, UserSession.getUsr().getId());
				preparedStatementSelect.setInt(2, UserSession.getUsr().getCaja());
			}*/
			
			
			
				//preparedStatementSelect = connection.prepareStatement(selectSolImpSQLAll);
				preparedStatementSelect = connection.prepareStatement(selectSolImpSQLUser); //Para que solo salgan las solicitudes de impresion del usuario logueado
				preparedStatementSelect.setInt(1, UserSession.getUsr().getId());
				
				System.out.println("Se ejecuto la consulta de solicitudes");
					
			ResultSet result = preparedStatementSelect.executeQuery();

			while (result.next()) {
				
				
				System.out.println("Entro a contennido asig");
				System.out.println("id Solicitud "+  result.getInt("sol_print_id"));
				
				solicitudImpresion.setId(result.getInt("sol_print_id"));
				solicitudImpresion.setFecha(result.getDate("fecha"));
				solicitudImpresion.setUsuario(new Usuario());
				solicitudImpresion.getUsuario().setId(result.getInt("usuario_id"));
				solicitudImpresion.setVenta(new VentaProdServ());
				solicitudImpresion.getVenta().setId(result.getInt("venta_id"));
				solicitudImpresion.setVentaDoc(new VentaDoc());
				solicitudImpresion.getVentaDoc().setId(result.getInt("ventadoc_id"));
			
				break;
			}

			if (solicitudImpresion.getId() == null) {
				System.out.println("solicitudImpresion.getId() == null ");
				return;
			}
			
			System.out.println("LLEgo al primer if");
			
			System.out.println("id ventaDoc" + solicitudImpresion.getVentaDoc().getId());
			//Al registrar la solicitud de impresion el id del usuario tiene q ser el que de click en imprimir 
			if (solicitudImpresion.getVenta().getId() != null && solicitudImpresion.getVenta().getId() != 0) { // la && solicitudImpresion.getUsuario().equals(UserSession.getUsr().getId()) 
																												// solicitud
																												// pertenece
																												// a
																												// una
																												// venta
				preparedStatementSelect = connection.prepareStatement(selectVentaProdServSQL);
				preparedStatementSelect.setInt(1, solicitudImpresion.getVenta().getId());
				ResultSet rs = preparedStatementSelect.executeQuery();

				while (rs.next()) {
					// Obteniendo la venta variada
					VentaProdServ ventaVariada = new VentaProdServ();
					ventaVariada.setId(rs.getInt("vtaprsv_id"));
					
					ventaVariada.setFechaVenta(rs.getDate("fecha_venta"));
					ventaVariada.setTipoVenta(rs.getString("tipo_venta"));
					ventaVariada.setMonto(rs.getFloat("monto"));
					
					ventaVariada.setCliente(new Cliente());
					ventaVariada.getCliente().setId(rs.getInt("cliente_id"));
				
					ventaVariada.setUsrEfectua(new Usuario());
					ventaVariada.getUsrEfectua().setId(rs.getInt("usrefe_id"));
				
					ventaVariada.getCliente().setId(rs.getInt("cliente_id"));
				
					ventaVariada.setDetVenta(new ArrayList<DetVentaProdServ>());
					// Informacion de a quien se le atribuye la venta
					// ventaVariada.setCodVendedor(rs.getString("cod_vendedor"));
					

					// Obtenemos la informacion de la empresa, sucursal y caja
					preparedStatementSelect = connection.prepareStatement(selectInfoEmpresaSQL);
					//preparedStatementSelect.setInt(1, ventaVariada.getSucursal().getId()); cambiado el 13/01/2017
					preparedStatementSelect.setInt(1,1);//Id del centro audiologico medico en tabla empresa_doc
					ResultSet rsI = preparedStatementSelect.executeQuery();

					while (rsI.next()) {
						// Obteniendo la venta variada
						InfoEmpresa infEmp = new InfoEmpresa();
						//infEmp.setId(rs.getInt("sgrprd_id"));
						infEmp.setId(rsI.getInt("empdoc_id"));
						//infEmp.setCodEmpresa(rs.getString("cod_empresa"));
						infEmp.setNit(rsI.getString("nit"));
						infEmp.setNrc(rsI.getString("nrc"));
						//infEmp.setGiro(rs.getString("giro"));
						infEmp.setDireccion(rsI.getString("direccion"));
						infEmp.setTelefono1(rsI.getString("telefono1"));
						infEmp.setNombre(rsI.getString("nombre"));
						//infEmp.setNombreComercial(rs.getString("nombre_comercial"));
						infEmp.setNombreComercial(rsI.getString("nombre"));
						//ventaVariada.setInfoEmpresa(infEmp);
						break;
					}

					// Obtener datos del vendedor
					// Obtenemos la informacion de la empresa, sucursal y caja
					preparedStatementSelect = connection.prepareStatement(selectVendedorSQL);
					//preparedStatementSelect.setInt(1, ventaVariada.getUsrVendedor().getId());
					preparedStatementSelect.setInt(1, ventaVariada.getUsrEfectua().getId());
					ResultSet rsV = preparedStatementSelect.executeQuery();

					while (rsV.next()) {
						// Obteniendo la venta variada

						ventaVariada.getUsrEfectua().setNombreCompleto(rsV.getString("nombre_completo"));

						break;
					}

					// Obteniendo el detalle de la venta
					preparedStatementSelect = connection.prepareStatement(selectDetVentaSQL);
					preparedStatementSelect.setInt(1, ventaVariada.getId());
					ResultSet rs1 = preparedStatementSelect.executeQuery();

					while (rs1.next()) {
						DetVentaProdServ detVentaProdServ = new DetVentaProdServ();
						detVentaProdServ.setId(rs1.getInt("vtaprsv_id"));
						detVentaProdServ.setVenta(ventaVariada);
						detVentaProdServ.setDetalle(rs1.getString("detalle"));
						//detVentaProdServ.setDetalleResumen(rs1.getString("detalle_resumen"));
						detVentaProdServ.setCantidad(rs1.getDouble("cantidad"));
						//detVentaProdServ.setPrecioUnitario(rs1.getFloat("precio_unitario"));
						//detVentaProdServ.setTipoVenta(rs1.getString("tipo_venta"));
						detVentaProdServ.setTipoVenta(ventaVariada.getTipoVenta());
						detVentaProdServ.setMonto(rs1.getFloat("monto"));

						// Agregando el detalle de la venta a la venta
						ventaVariada.getDetVenta().add(detVentaProdServ);
					}

					// Obteniendo el cliente
					if (ventaVariada.getCliente().getId() != null && ventaVariada.getCliente().getId() > 0) {
						preparedStatementSelect = connection.prepareStatement(selectClienteSQL);
						preparedStatementSelect.setInt(1, ventaVariada.getCliente().getId());
						ResultSet rs2 = preparedStatementSelect.executeQuery();

						while (rs2.next()) {
							Cliente cliente = new Cliente();
							/*cliente.setId(rs2.getInt("cliente_id"));
							cliente.setNombre(rs2.getString("nombre"));
							cliente.setApellido(rs2.getString("apellido"));
							cliente.setRazonSocial(rs2.getString("razon_social"));
							cliente.setTelCelular(rs2.getString("tel_celular"));
							cliente.setDireccion(rs2.getString("direccion"));
							cliente.setNumRegistro(rs2.getString("num_registro"));
							cliente.setNit(rs2.getString("nit"));
							cliente.setGiro(rs2.getString("giro"));
							cliente.setTipoCliente(rs2.getInt("tipo_cliente"));*/
							
							cliente.setId(rs2.getInt("cliente_id"));
							cliente.setNombre(rs2.getString("nombres"));
							cliente.setApellido(rs2.getString("apellidos"));
							//cliente.setRazonSocial(rs2.getString("razon_social"));
							cliente.setTelCelular(rs2.getString("telefono1"));
							cliente.setDireccion(rs2.getString("direccion"));
							cliente.setNumRegistro(rs2.getString("cliente_id"));
							cliente.setGiro(rs2.getString("giro"));
							//cliente.setNit(rs2.getString("nit"));
							//cliente.setGiro(rs2.getString("giro"));
							//cliente.setTipoCliente(rs2.getInt("tipo_cliente"));
							
							ventaVariada.setCliente(cliente);
							break;
						}
					}

					// obteniendo la orden de trabajo 18/07/2016 		comentado el 13/01/2017
					/*preparedStatementSelect = connection.prepareStatement(selectOrdenTrabajoSQL);
					preparedStatementSelect.setInt(1, ventaVariada.getId());
					ResultSet rs3 = preparedStatementSelect.executeQuery();
					while (rs3.next()) {
						OrdenDeTrabajo orden = new OrdenDeTrabajo();
						orden.setId(rs3.getInt("odt_id"));
						orden.setFechaIngreso(rs3.getDate("fechaingreso"));
						orden.setFechaVenta(rs3.getDate("fechaventa"));
						orden.setFechaEntrega(rs3.getTimestamp("fechaentrega"));
						// orden.setFechaEntrega(rs3.getDate("fechaentrega"));
						orden.setAnticipo(rs3.getDouble("anticipo"));
						orden.setDescripcion(rs3.getString("descripcion"));
						orden.setCodigo(rs3.getString("codigo"));
						//ventaVariada.setOrdenDeTrabajo(orden);
					}*/
					// fin

					// Actualizando el estado del registro de la venta
					preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
					preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
					preparedStatementUpdate.executeUpdate();
					connection.commit();
					System.out.println("Preparandose para imprimir comprobante");
					crearDoc(ventaVariada);
					System.out.println("Finalizacion: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
					break;
				} // Fin while venta variada
			} 
			
			//Codigo nuevo para registrar venta_doc y realizar impresion
			else if (solicitudImpresion.getVentaDoc().getId() != null && solicitudImpresion.getVentaDoc().getId() != 0) { // la && solicitudImpresion.getUsuario().equals(UserSession.getUsr().getId()) 
																												// solicitud
																												// pertenece
																												// a
			  System.out.println("Entro a venta Doc ***");																			// una
																												// venta
				//preparedStatementSelect = connection.prepareStatement(selectVentaProdServSQL);
				preparedStatementSelect = connection.prepareStatement(selectVentaDocSQL);
				preparedStatementSelect.setInt(1, solicitudImpresion.getVentaDoc().getId());
				ResultSet rs = preparedStatementSelect.executeQuery();

				while (rs.next()) {
					// Obteniendo la venta variada
					System.out.println("Entro a while de  venta Doc ***");	
					
					VentaDoc ventaVariada = new VentaDoc();
					ventaVariada.setId(rs.getInt("vtadoc_id"));
					
					ventaVariada.setFecha(rs.getDate("fecha"));
					//ventaVariada.set(rs.getString("tipo_venta"));
					ventaVariada.setTotal(rs.getFloat("total"));
					
					ventaVariada.setCliente(new ClienteDoc());
					ventaVariada.getCliente().setId(rs.getInt("cliente_id"));
				
					ventaVariada.setUsuario(new Usuario());
					ventaVariada.getUsuario().setId(rs.getInt("usuario_id"));
					
					ventaVariada.setComprobante(new ComprobanteImpresion());
					ventaVariada.getComprobante().setId(rs.getInt("id_comprobante"));
				
					//ventaVariada.getCliente().setId(rs.getInt("cliente_id"));
				
					ventaVariada.setDetVentas(new ArrayList<DetVentaDoc>());
					// Informacion de a quien se le atribuye la venta
					// ventaVariada.setCodVendedor(rs.getString("cod_vendedor"));
					
					
					//NOTA: agregar condicion que si existe un comprobante
					
					//Informacion del comprobante.
					preparedStatementSelect = connection.prepareStatement(selectComprobanteByVentaDoc); // por id de comprobante que tiene la ventadoc
					preparedStatementSelect.setInt(1, ventaVariada.getComprobante().getId());
					ResultSet rsCom = preparedStatementSelect.executeQuery();
					
					Integer idEmpresa=0;
					while(rsCom.next())
					{
						idEmpresa=rsCom.getInt("empdoc_id");
						ComprobanteImpresion comprobante = new ComprobanteImpresion();
						
						
						comprobante.setCodigo(rsCom.getString("codigo"));
						comprobante.setNombre(rsCom.getString("nombre"));
						comprobante.setImpresor("impresor");
						comprobante.setPagina("pagina");
						comprobante.setEmpresaDoc(new EmpresaDoc());
						comprobante.getEmpresaDoc().setId(rsCom.getInt("empdoc_id"));
						comprobante.setTipo(rsCom.getString("tipo"));
						System.out.println("Id del comprobante "+rsCom.getString("tipo"));
						
						ventaVariada.setComprobante(comprobante);
					}
					
					//Agregar condicion que sis existe una empresa

					// Obtenemos la informacion de la empresa, sucursal y caja
					preparedStatementSelect = connection.prepareStatement(selectInfoEmpresaSQL); 
					//preparedStatementSelect.setInt(1, ventaVariada.getSucursal().getId()); cambiado el 13/01/2017
					preparedStatementSelect.setInt(1,idEmpresa);//Id del centro audiologico medico en tabla empresa_doc      ******* Tiene que ser el id que tiene el comprobante *********
					ResultSet rsI = preparedStatementSelect.executeQuery();

					while (rsI.next()) {
						// Obteniendo la venta variada
						//InfoEmpresa infEmp = new InfoEmpresa();
						EmpresaDoc infEmp = new EmpresaDoc();
						//infEmp.setId(rs.getInt("sgrprd_id"));
						infEmp.setId(rsI.getInt("empdoc_id"));
						//infEmp.setCodEmpresa(rs.getString("cod_empresa"));
						infEmp.setNit(rsI.getString("nit"));
						infEmp.setNrc(rsI.getString("nrc"));
						//infEmp.setGiro(rs.getString("giro"));
						infEmp.setDireccion(rsI.getString("direccion"));
						infEmp.setTelefono1(rsI.getString("telefono1"));
						infEmp.setNombre(rsI.getString("nombre"));
						//infEmp.setNombreComercial(rs.getString("nombre_comercial"));
						infEmp.setTipoContribuyente(rsI.getInt("tipo_contribuyente"));
						infEmp.setExento(rsI.getBoolean("exento"));
						
						
						ventaVariada.getComprobante().setEmpresaDoc(infEmp);
						break;
					}

					
					//Agrgar condicion que si existe un usuairo
					
					// Obtener datos del vendedor
					// Obtenemos la informacion de la empresa, sucursal y caja
					preparedStatementSelect = connection.prepareStatement(selectVendedorSQL);
					//preparedStatementSelect.setInt(1, ventaVariada.getUsrVendedor().getId());
					preparedStatementSelect.setInt(1, ventaVariada.getUsuario().getId());
					ResultSet rsV = preparedStatementSelect.executeQuery();

					while (rsV.next()) {
						// Obteniendo la venta variada

						ventaVariada.getUsuario().setNombreCompleto(rsV.getString("nombre_completo"));

						break;
					}

					// Obteniendo el detalle de la venta
					preparedStatementSelect = connection.prepareStatement(selectDetVentaDocSQL);
					preparedStatementSelect.setInt(1, ventaVariada.getId());
					ResultSet rs1 = preparedStatementSelect.executeQuery();

					while (rs1.next()) {
						
						DetVentaDoc detVentaDoc = new DetVentaDoc();
						detVentaDoc.setId(rs1.getInt("dtvtadoc_id"));
						detVentaDoc.setVenta(ventaVariada);
						detVentaDoc.setDetalle(rs1.getString("detalle"));
						//detVentaProdServ.setDetalleResumen(rs1.getString("detalle_resumen"));
						detVentaDoc.setCantidad(rs1.getInt("cantidad"));
						//detVentaProdServ.setPrecioUnitario(rs1.getFloat("precio_unitario"));
						//detVentaProdServ.setTipoVenta(rs1.getString("tipo_venta"));
						
						detVentaDoc.setTipo(rs1.getString("tipo"));
						
						detVentaDoc.setPrecioUnitario(rs1.getFloat("precio_unitario"));
						detVentaDoc.setTotal(rs1.getFloat("total"));

						// Agregando el detalle de la venta a la venta
						ventaVariada.getDetVentas().add(detVentaDoc);
					}

					// Obteniendo el cliente
					if (ventaVariada.getCliente().getId() != null && ventaVariada.getCliente().getId() > 0) {
						preparedStatementSelect = connection.prepareStatement(selectClienteDocSQL);
						preparedStatementSelect.setInt(1, ventaVariada.getCliente().getId());
						ResultSet rs2 = preparedStatementSelect.executeQuery();

						while (rs2.next()) {
							
							ClienteDoc cliente = new ClienteDoc();
							
							/*cliente.setId(rs2.getInt("cliente_id"));
							cliente.setNombre(rs2.getString("nombre"));
							cliente.setApellido(rs2.getString("apellido"));
							cliente.setRazonSocial(rs2.getString("razon_social"));
							cliente.setTelCelular(rs2.getString("tel_celular"));
							cliente.setDireccion(rs2.getString("direccion"));
							cliente.setNumRegistro(rs2.getString("num_registro"));
							cliente.setNit(rs2.getString("nit"));
							cliente.setGiro(rs2.getString("giro"));
							cliente.setTipoCliente(rs2.getInt("tipo_cliente"));*/
							
							cliente.setId(rs2.getInt("id"));
							cliente.setNombre(rs2.getString("nombre"));
							cliente.setApellido(rs2.getString("apellido"));
							//cliente.setRazonSocial(rs2.getString("razon_social"));
							cliente.setTelefono1(rs2.getString("telefono1"));
							cliente.setDireccion(rs2.getString("direccion"));
							cliente.setNrc(rs2.getString("nrc")); //???
							cliente.setNit(rs2.getString("nit"));
							cliente.setGiro(rs2.getString("giro"));
							//cliente.setTipoContribuyente(rs2.getInt("tipo_cliente"));
							
							ventaVariada.setCliente(cliente);
							break;
						}
					}

					// obteniendo la orden de trabajo 18/07/2016 		comentado el 13/01/2017
					/*preparedStatementSelect = connection.prepareStatement(selectOrdenTrabajoSQL);
					preparedStatementSelect.setInt(1, ventaVariada.getId());
					ResultSet rs3 = preparedStatementSelect.executeQuery();
					while (rs3.next()) {
						OrdenDeTrabajo orden = new OrdenDeTrabajo();
						orden.setId(rs3.getInt("odt_id"));
						orden.setFechaIngreso(rs3.getDate("fechaingreso"));
						orden.setFechaVenta(rs3.getDate("fechaventa"));
						orden.setFechaEntrega(rs3.getTimestamp("fechaentrega"));
						// orden.setFechaEntrega(rs3.getDate("fechaentrega"));
						orden.setAnticipo(rs3.getDouble("anticipo"));
						orden.setDescripcion(rs3.getString("descripcion"));
						orden.setCodigo(rs3.getString("codigo"));
						//ventaVariada.setOrdenDeTrabajo(orden);
					}*/
					// fin

					// Actualizando el estado del registro de la venta
					preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
					preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
					preparedStatementUpdate.executeUpdate();
					connection.commit();
					System.out.println("Preparandose para imprimir comprobante");
					crearDoc(ventaVariada);
					System.out.println("Finalizacion: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
					break;
				} // Fin while ventaDoc variada
				
				
			}else if (solicitudImpresion.getCotizacion().getId() != null
					&& solicitudImpresion.getCotizacion().getId() != 0) {// la
																			// solicitud
																			// pertenece
																			// a
																			// una
																			// cotización
				preparedStatementSelect = connection.prepareStatement(selectCotizacionSQL);
				preparedStatementSelect.setInt(1, solicitudImpresion.getCotizacion().getId());
				ResultSet rs = preparedStatementSelect.executeQuery();

				while (rs.next()) {
					// Obteniendo la cotizacion variada
					Cotizacion cotizacionVariada = new Cotizacion();
					cotizacionVariada.setId(rs.getInt("id"));
					cotizacionVariada.setNumSucursal(rs.getInt("sucursal_id"));
					cotizacionVariada.setFechaIngreso(rs.getDate("fechaingreso"));
					cotizacionVariada.setDescripcion(rs.getString("descripcion"));
					cotizacionVariada.setTotalCost(rs.getDouble("total_cost"));
					cotizacionVariada.setCliente(new Cliente());
					cotizacionVariada.getCliente().setId(rs.getInt("client_id"));
					cotizacionVariada.setUsrEfectua(new Usuario());
					cotizacionVariada.getUsrEfectua().setId(rs.getInt("usrefe_id"));
					cotizacionVariada.setDetCotizacion(new ArrayList<CotizacionDet>());

					// ventaVariada.setCodVendedor(rs.getString("cod_vendedor"));

					// Obtenemos la informacion de la empresa, sucursal y caja
					preparedStatementSelect = connection.prepareStatement(selectInfoEmpresaSQL);
					preparedStatementSelect.setInt(1, cotizacionVariada.getNumSucursal());
					ResultSet rsI = preparedStatementSelect.executeQuery();

					while (rsI.next()) {
						// Informacion de la empresa
						InfoEmpresa infEmp = new InfoEmpresa();
						infEmp.setId(rs.getInt("sgrprd_id"));
						infEmp.setCodEmpresa(rs.getString("cod_empresa"));
						infEmp.setNit(rs.getString("nit"));
						infEmp.setNrc(rs.getString("nrc"));
						infEmp.setGiro(rs.getString("giro"));
						infEmp.setDireccion(rs.getString("direccion"));
						infEmp.setTelefono1(rs.getString("telefono_1"));
						infEmp.setNombre(rs.getString("nombre"));
						infEmp.setNombreComercial(rs.getString("nombre_comercial"));
						cotizacionVariada.setInfoEmpresa(infEmp);
						break;
					}

					// Obteniendo el detalle de la cotización
					preparedStatementSelect = connection.prepareStatement(selectCotizacionDetSQL);
					preparedStatementSelect.setInt(1, cotizacionVariada.getId());
					ResultSet rs1 = preparedStatementSelect.executeQuery();

					while (rs1.next()) {
						CotizacionDet cotizacionDet = new CotizacionDet();
						cotizacionDet.setId(rs1.getInt("id"));
						cotizacionDet.setCotizacion(cotizacionVariada);
						cotizacionDet.setDescripcion(rs1.getString("descripcion"));
						cotizacionDet.setCantidad(rs1.getDouble("cantidad"));
						cotizacionDet.setPrecioUnitario(rs1.getDouble("precio_unitario"));
						cotizacionDet.setMonto(rs1.getDouble("monto"));

						// Agregando el detalle de la venta a la venta
						cotizacionVariada.getDetCotizacion().add(cotizacionDet);
					}

					// Obteniendo el cliente
					if (cotizacionVariada.getCliente().getId() != null && cotizacionVariada.getCliente().getId() > 0) {
						preparedStatementSelect = connection.prepareStatement(selectClienteSQL);
						preparedStatementSelect.setInt(1, cotizacionVariada.getCliente().getId());
						ResultSet rs2 = preparedStatementSelect.executeQuery();

						while (rs2.next()) {
							Cliente cliente = new Cliente();
							cliente.setId(rs2.getInt("client_id"));
							cliente.setNombre(rs2.getString("nombre"));
							cliente.setApellido(rs2.getString("apellido"));
							cliente.setContacto(rs2.getString("contacto"));
							cliente.setRazonSocial(rs2.getString("razon_social"));
							cliente.setTelCelular(rs2.getString("tel_celular"));
							cliente.setDireccion(rs2.getString("direccion"));
							cliente.setNumRegistro(rs2.getString("num_registro"));
							cliente.setNit(rs2.getString("nit"));
							cliente.setGiro(rs2.getString("giro"));
							cliente.setTipoCliente(rs2.getInt("tipo_cliente"));
							cotizacionVariada.setCliente(cliente);
							break;
						}
					}

					// Actualizando el estado del registro de la venta
					preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
					preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
					preparedStatementUpdate.executeUpdate();
					connection.commit();
					System.out.println("Preparandose para imprimir comprobante");

					crearDoc(cotizacionVariada);
					System.out.println("Finalizacion: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
					break;
				} // Fin while cotizacion variada
			} else if (solicitudImpresion.getQuedan().getId() != null && solicitudImpresion.getQuedan().getId() != 0) {
				// Imprimiremos el comprobante de un quedan
				Quedan quedanImprimir = new Quedan();
				preparedStatementSelect = connection.prepareStatement(selectQuedanSQL);
				preparedStatementSelect.setInt(1, solicitudImpresion.getQuedan().getId());
				ResultSet rs = preparedStatementSelect.executeQuery();
				while (rs.next()) {
					quedanImprimir.setId(rs.getInt("id"));
					quedanImprimir.setFechaEmision(rs.getDate("fecha_emision"));
					quedanImprimir.setFechaPago(rs.getDate("fecha_pago"));
					quedanImprimir.setMonto(rs.getDouble("monto"));
					quedanImprimir.setProveedor(rs.getString("razon_social"));
					quedanImprimir.setDocumentos(rs.getString("string_agg"));

					// Actualizando el estado del registro de la venta
					preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
					preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
					preparedStatementUpdate.executeUpdate();
					connection.commit();
					System.out.println("Preparandose para imprimir Quedan");

					crearDoc(quedanImprimir);
					System.out.println("Finalizacion: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
					break;
				} // Fin while quedan a imprimir
			} else if (solicitudImpresion.getOrdenCompra().getId() != null
					&& solicitudImpresion.getOrdenCompra().getId() != 0) {
				OrdenCompra ordenImprimir = new OrdenCompra();
				preparedStatementSelect = connection.prepareStatement(selectOrdenCompraSQL);
				preparedStatementSelect.setInt(1, solicitudImpresion.getOrdenCompra().getId());
				ResultSet rs = preparedStatementSelect.executeQuery();
				while (rs.next()) {
					ordenImprimir.setId(rs.getInt("id"));
					ordenImprimir.setFecha(rs.getDate("fechaingreso"));
					ordenImprimir.setEncargado(rs.getString("encargado"));
					ordenImprimir.setProveedor(rs.getString("razon_social"));
					ordenImprimir.setRecibira(rs.getString("entregar_a"));

					// llenamos el detalle
					ordenImprimir.setDetOrdenCompra(new ArrayList<DetOrdenCompra>());
					preparedStatementSelect = connection.prepareStatement(selectOrdenCompraDetSQL);
					preparedStatementSelect.setInt(1, solicitudImpresion.getOrdenCompra().getId());
					ResultSet rs2 = preparedStatementSelect.executeQuery();
					while (rs2.next()) {
						DetOrdenCompra detalle = new DetOrdenCompra();
						detalle.setCantidad(rs2.getFloat("cantidad"));
						detalle.setNombre(rs2.getString("nombre"));
						ordenImprimir.getDetOrdenCompra().add(detalle);
					}

					// Actualizando el estado del registro de la venta
					preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
					preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
					preparedStatementUpdate.executeUpdate();
					connection.commit();
					System.out.println("Preparandose para imprimir Quedan");

					crearDoc(ordenImprimir);
					System.out.println("Finalizacion: " + new SimpleDateFormat("hh:mm:ss:S").format(new Date()));
					break;

				} // fin del while rs;

			} else { // si por algun motivo la solicitud no pertence a ninguna
						// venta, cotizacion o quedan la removemos del listado
				preparedStatementUpdate = connection.prepareStatement(deleteSolPrintSQL);
				preparedStatementUpdate.setInt(1, solicitudImpresion.getId());
				preparedStatementUpdate.executeUpdate();
				connection.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void crearDoc(OrdenCompra ordenImprimir) {
		try {
			Map<String, String> informacion = new HashMap<String, String>(); // mapa
																				// de
																				// llave,
																				// valor
																				// para
																				// lo
																				// que
																				// se
																				// usara
																				// en
																				// el
																				// reporte
			List<HashMap<String, String>> detalle = new ArrayList<HashMap<String, String>>(); // mapas
																								// de
																								// llave,
																								// valor
																								// para
																								// la
																								// información
																								// de
																								// cada
																								// detalle
			// agregamos la información del cliente al mapa
			Calendar c = new GregorianCalendar();
			c.setTime(ordenImprimir.getFecha());
			informacion.put("oc.diames",
					String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + " de " + meses.get(c.get(Calendar.MONTH)));
			informacion.put("oc.anio", String.valueOf(c.get(Calendar.YEAR)));
			informacion.put("proveedor.nombre",
					ordenImprimir.getProveedor()
							+ (ordenImprimir.getEncargado() != null && !ordenImprimir.getEncargado().isEmpty()
									? "(" + ordenImprimir.getEncargado() + ")" : ""));
			informacion.put("oc.recibira", ordenImprimir.getRecibira());
			for (DetOrdenCompra doc : ordenImprimir.getDetOrdenCompra()) {
				HashMap<String, String> det = new HashMap<String, String>();
				det.put("detalle.cantidad", mf.format(doc.getCantidad()));
				det.put("detalle.nombre", doc.getNombre());
				detalle.add(det);
			}

			String salida = preparePrintString("config\\cprOrdenCompraDefault.txt", informacion, detalle);

			String metodoImpresion = PropertiesLoader.appProperties.getProperty("global.printer.method");
			if (metodoImpresion == null || metodoImpresion.isEmpty()
					|| metodoImpresion.equalsIgnoreCase("serialport")) {
				Printer receipt = PrinterFactory.getPrinterSlip();
				receipt.init(impresora);
				receipt.write(salida);
				receipt.close();
			} else if (metodoImpresion != null && !metodoImpresion.isEmpty()
					&& metodoImpresion.equalsIgnoreCase("printername")) {
				impresion(salida, impresora);
			}
			ConnectionManager.getTextArea().setText(ConnectionManager.getTextArea().getText() + "[OC] " + " "
					+ ordenImprimir.getProveedor() + EpsonEscCommand.LINE_FEED);
		} catch (Exception e) {
			// Si se da un error lo agregamos al log de la pantalla principal
			e.printStackTrace();

			StringWriter str = new StringWriter();
			PrintWriter writer = new PrintWriter(str);
			e.printStackTrace(writer);

			ConnectionManager.getTextArea().setText("[ERROR]: " + str.getBuffer().toString());
		}
	}

	private void crearDoc(Quedan quedanImprimir) {
		try {
			Calendar c = new GregorianCalendar();
			c.setTime(quedanImprimir.getFechaEmision());
			Map<String, String> informacion = new HashMap<String, String>();
			List<HashMap<String, String>> detalle = new ArrayList<HashMap<String, String>>();
			informacion.put("quedan.total", mf.format(quedanImprimir.getMonto()));
			informacion.put("prov.nombre", quedanImprimir.getProveedor());
			informacion.put("quedan.comprobantes", quedanImprimir.getDocumentos());
			informacion.put("quedan.numalet", numalet.convertNumToLetters(quedanImprimir.getMonto(), true));
			informacion.put("quedan.fecha", df.format(quedanImprimir.getFechaPago()));
			informacion.put("quedan.dia", String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			informacion.put("quedan.mes", meses.get(c.get(Calendar.MONTH)));
			informacion.put("quedan.anio", String.valueOf(c.get(Calendar.YEAR)).substring(2));

			String salida = preparePrintString("config\\cprQuedanDefault.txt", informacion, detalle);

			String metodoImpresion = PropertiesLoader.appProperties.getProperty("global.printer.method");
			if (metodoImpresion == null || metodoImpresion.isEmpty()
					|| metodoImpresion.equalsIgnoreCase("serialport")) {
				Printer receipt = PrinterFactory.getPrinterSlip();
				receipt.init(impresora);
				receipt.write(salida);
				receipt.close();
			} else if (metodoImpresion != null && !metodoImpresion.isEmpty()
					&& metodoImpresion.equalsIgnoreCase("printername")) {
				impresion(salida, impresora);
			}
			if (quedanImprimir.getId() != null)
				ConnectionManager.getTextArea().setText(ConnectionManager.getTextArea().getText() + "[QUEDAN] " + " "
						+ quedanImprimir.getProveedor() + EpsonEscCommand.LINE_FEED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void crearDoc(VentaProdServ ventaVariada) {
		try {
			Map<String, String> informacion = new HashMap<String, String>();
			List<HashMap<String, String>> detalle = new ArrayList<HashMap<String, String>>();
			informacion.put("usuario.nombre", UserSession.getUsr().getNombreUsuario());
			/*informacion.put("vendedor.nombre", ventaVariada.getVendedor());
			// cambio para imprimir la fecha de entrega de la orden de trabajo
			if (ventaVariada.getOrdenDeTrabajo() != null) {
				informacion.put("venta.fechaentrega",
						String.valueOf((ventaVariada.getOrdenDeTrabajo().getFechaEntrega())));
				informacion.put("autorizo", ventaVariada.getUsrVendedor().getNombreCompleto());
			}*/

			// Seteamos todas las variables en un hashmap
			if (ventaVariada.getCliente().getId() != null && ventaVariada.getCliente().getId() > 0) {
				informacion.put("cliente.nombre", ventaVariada.getCliente().getPersonaoempresa());
				informacion.put("cliente.dui", ventaVariada.getCliente().getDui());
				informacion.put("cliente.nit", ventaVariada.getCliente().getNit());
				informacion.put("cliente.duinit", ventaVariada.getCliente().getDuionit());

				if (ventaVariada.getCliente().getDireccion() != null) {
					if (ventaVariada.getCliente().getDireccion().length() < 40)
						informacion.put("cliente.direccionl1", ventaVariada.getCliente().getDireccion());
					else if (ventaVariada.getCliente().getDireccion().length() >= 40) {
						informacion.put("cliente.direccionl1",
								ventaVariada.getCliente().getDireccion().substring(0, 40));
						informacion.put("cliente.direccionl2", ventaVariada.getCliente().getDireccion().substring(40,
								ventaVariada.getCliente().getDireccion().length()));
					}
				} else {
					informacion.put("cliente.direccionl1", "");
					informacion.put("cliente.direccionl2", "");
				}
				informacion.put("cliente.nrc", ventaVariada.getCliente().getNumRegistro());
				informacion.put("cliente.giro", ventaVariada.getCliente().getGiro());
				informacion.put("cliente.municipio", ventaVariada.getCliente().getMunicipio());
				informacion.put("cliente.departamento", ventaVariada.getCliente().getDepartamento());
				informacion.put("cliente.telefono",
						(ventaVariada.getCliente().getTelCelular() != null ? ventaVariada.getCliente().getTelCelular()
								: (ventaVariada.getCliente().getTelFijo() != null
										? ventaVariada.getCliente().getTelFijo() : "")));
			} else {
				informacion.put("cliente.nombre", "");
				informacion.put("cliente.dui", "");
				informacion.put("cliente.nit", "");
				informacion.put("cliente.duinit", "");
				informacion.put("cliente.direccionl1", "");
				informacion.put("cliente.direccionl2", "");
				informacion.put("cliente.direccionl3", "");
				informacion.put("cliente.nrc", "");
				informacion.put("cliente.giro", "");
				informacion.put("cliente.municipio", "");
				informacion.put("cliente.departamento", "");
				informacion.put("cliente.codigocli", "");
				informacion.put("cliente.telefono", "");
			}

			informacion.put("venta.total", mf.format(ventaVariada.getMonto()));
			informacion.put("venta.totalentero", intf.format(ventaVariada.getMonto()));
			informacion.put("venta.totaldecimal", centf.format((ventaVariada.getMonto() * 100) % 100));
			//informacion.put("venta.detServ", ventaVariada.getDetalleServicio()); // "venta.detServ"
																					// es
																					// el
																					// nombre
																					// que
																					// lleva
																					// en
																					// el
																					// formato
																					// .txt
			/*if (ventaVariada.getMontocxc() > 0.0) {
				informacion.put("venta.formapagocont", "");
				informacion.put("venta.formapagocrd", "X");
				informacion.put("venta.formapagotxt", "CREDITO");
				
			} else {
				informacion.put("venta.formapagocont", "X");
				informacion.put("venta.formapagocrd", "");
				informacion.put("venta.formapagotxt", "CONTADO");
				
			}*/

			informacion.put("venta.subtotal", mf.format(ventaVariada.getMonto()));
			/*
			 * Este cambia en base al tipo de docuementos si es factura o
			 * credito fiscal
			 */
			/*if ("C".equals(ventaVariada.getTipoDeDocumento())) {
				informacion.put("venta.subtotal2", mf.format(ventaVariada.getMonto() + ventaVariada.getIva()));
			} else {
				//informacion.put("venta.subtotal2", mf.format(ventaVariada.getMonto() - ventaVariada.getRetenido()));
			}*/
			/*informacion.put("venta.totalnosujeta", mf.format(ventaVariada.getVentasNoSujetas()));
			informacion.put("venta.totalexenta", mf.format(ventaVariada.getVentasExentas()));
			informacion.put("venta.iva", mf.format(ventaVariada.getIva()));*/
			String str = numalet.convertNumToLetters(ventaVariada.getMonto().doubleValue(), true) + "DOLARES";
			if (str.length() < 60) {
				informacion.put("venta.numalet1", str);
				informacion.put("venta.numalet2", "");
			} else {
				informacion.put("venta.numalet1", str.substring(0, 58));
				informacion.put("venta.numalet2", str.substring(59, str.length()));
			}
			informacion.put("venta.fecha", df.format(ventaVariada.getFechaVenta()));
			// informacion.put("venta.codvendedor",
			// ventaVariada.getCodVendedor());
			// Sacamos el detalle	// Modificacion 13-09-16
			for (DetVentaProdServ tmpDet : ventaVariada.getDetVenta()) {
				
				HashMap<String, String> mapDet = new HashMap<String, String>();
				Double cant = tmpDet.getCantidad();
				mapDet.put("detalle.cantidad", cant.toString());
				mapDet.put("detalle.descripcion", tmpDet.getDetalle());
				//mapDet.put("detalle.preciounitario", mf.format(tmpDet.getPrecioUnitario()));
				mapDet.put("detalle.preciounitario", mf.format(tmpDet.getMonto()));
				
				if (tmpDet.getTipoVenta().equals("G")) {
					mapDet.put("detalle.gravado", mf.format(tmpDet.getMonto()));
					mapDet.put("detalle.exento", null);
					mapDet.put("detalle.nosujeto", null);
				} else if (tmpDet.getTipoVenta().equals("J")) {
					mapDet.put("detalle.nosujeto", mf.format(tmpDet.getMonto()));
					mapDet.put("detalle.exento", null);
					mapDet.put("detalle.gravado", null);
				} else if (tmpDet.getTipoVenta().equals("E")) {
					mapDet.put("detalle.exento", mf.format(tmpDet.getMonto()));
					mapDet.put("detalle.gravado", null);
					mapDet.put("detalle.nosujeto", null);
				}
				
				mapDet.put("detalle.totalentero", intf.format(tmpDet.getMonto()));
				mapDet.put("detalle.totaldecimal", centf.format(Math.round((tmpDet.getMonto() * 100) % 100)));
				detalle.add(mapDet);
			}
			/* 23/06/2016 */
			
			ventaVariada.setTipoDeDocumento("F"); //En algun punto es encesario agregar este tipo de documentp
			if (!ventaVariada.getTipoDeDocumento().equals("T")) {
				String formato;
				tipoDocumento = ventaVariada.getTipoDeDocumento();
				if (ventaVariada.getTipoDeDocumento().equals("F")) {
					formato = "config\\" + (ventaVariada.getCodComprobante() != null
							&& ventaVariada.getCodComprobante().trim().length() > 0 ? ventaVariada.getCodComprobante()
									: "cprFacAudiomed") //nuevo formato de factura para prueba: formato actual: cprFacDefault ; nuevo: cprFacMatriz.txt cprFacAudiomed
							+ ".txt";
				} else if (ventaVariada.getTipoDeDocumento().equals("C")) {
					formato = "config\\" + (ventaVariada.getCodComprobante() != null
							&& ventaVariada.getCodComprobante().trim().length() > 0 ? ventaVariada.getCodComprobante()
									: "cprCcfDefault")
							+ ".txt";
				} else if (ventaVariada.getTipoDeDocumento().equals("H")) {
					formato = "config\\" + (ventaVariada.getCodComprobante() != null
							&& ventaVariada.getCodComprobante().trim().length() > 0 ? ventaVariada.getCodComprobante()
									: "cprHTDefault")
							+ ".txt";
				} else {
					formato = "config\\cprXMatriz.txt";
				}

				// generamos el string con formato de impresion para la venta
				// 789
				String salida = preparePrintString(formato, informacion, detalle);

				String metodoImpresion = PropertiesLoader.appProperties.getProperty("global.printer.method");
				if (metodoImpresion == null || metodoImpresion.isEmpty()
						|| metodoImpresion.equalsIgnoreCase("serialport")) {
					Printer receipt = PrinterFactory.getPrinterSlip();
					receipt.init(impresora);
					receipt.write(salida);
					receipt.close();
				} else if (metodoImpresion != null && !metodoImpresion.isEmpty()
						&& metodoImpresion.equalsIgnoreCase("printername")) {
					impresion(salida, impresora);
				}
			} else {
				// Si es un ticket se le da formato aqui mismo descomentariar si
				// se pretente impresion centralizada de tickets
				/*
				 * StringBuilder strBld = new StringBuilder();
				 * strBld.append(ventaVariada.getInfoEmpresa().getNombre());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(ventaVariada.getInfoEmpresa().getDireccion());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NRC: " + ventaVariada.getInfoEmpresa().getNrc());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "IVA DECRETO LEGISLATIVO 296");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NIT: " + ventaVariada.getInfoEmpresa().getNit());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Giro: " + ventaVariada.getInfoEmpresa().getGiro());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Caja # " + UserSession.getUsr().getCaja());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Ticket # " + ventaVariada.getNumDocumento());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append(getTextoRellenado(new
				 * SimpleDateFormat("dd/MM/yyyy").format(ventaVariada.
				 * getFechaVenta()), (short)(MAX_CHAR_TKT/2), true, "L"));
				 * strBld.append(getTextoRellenado(new SimpleDateFormat(
				 * "hh:mm a").format(ventaVariada.getFechaVenta()),
				 * (short)(MAX_CHAR_TKT/2), true, "R"));
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Vende " +
				 * ventaVariada.getUsrEfectua().getNombreCompleto());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Cant. Detalle      P. Uni.  Total  ");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append("----------------------------------------");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * for( DetVentaProdServ detVenta : ventaVariada.getDetVenta()
				 * ){ strBld.append(getTextoRellenado(intf.format(detVenta.
				 * getCantidad()), (short)6, true, "R"));
				 * strBld.append(getTextoRellenado(detVenta.getDetalle(),
				 * (short)17, true, "L"));
				 * strBld.append(getTextoRellenado(mf.format(detVenta.
				 * getPrecioUnitario()), (short)7, true, "R"));
				 * strBld.append(getTextoRellenado(mf.format(detVenta.getMonto()
				 * ), (short)8, true, "R"));
				 * if(detVenta.getTipoVenta().equals("J")) strBld.append(" N");
				 * else if(detVenta.getTipoVenta().equals("G")) strBld.append(
				 * " G"); else if(detVenta.getTipoVenta().equals("E"))
				 * strBld.append(" E");
				 * strBld.append(EpsonEscCommand.LINE_FEED); }
				 * 
				 * if( ventaVariada.getCliente().getId() != null &&
				 * ventaVariada.getCliente().getId() > 0 ){
				 * strBld.append(ventaVariada.getCliente().getNombre() + " " +
				 * ventaVariada.getCliente().getApellido()); }
				 * 
				 * strBld.append(getTextoRellenado("Subtotal gravado: " +
				 * mf.format(ventaVariada.getMonto()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("Subtotal exento: " +
				 * mf.format(ventaVariada.getVentasExentas()), MAX_CHAR_TKT,
				 * true, "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("Subtotal no sujeto: " +
				 * mf.format(ventaVariada.getVentasNoSujetas()), MAX_CHAR_TKT,
				 * true, "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("TOTAL: " +
				 * mf.format(ventaVariada.getTotal()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(numalet.convertNumToLetters(ventaVariada.
				 * getTotal().doubleValue(), true ) + "DÃ“LARES");
				 * 
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append(getTextoRellenado("Cambio: " +
				 * mf.format(ventaVariada.getCambio()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(
				 * "G = Gravado,  E = Exento,  N = No sujeto");
				 * 
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Llenar si la venta es igual/mayor a $200.00");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Nombre: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NIT: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "DUI: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Firma: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Serie autorizada del " +
				 * ventaVariada.getCodAutTick() + "|" +
				 * ventaVariada.getNumDocIni());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("NO SE HACEN CAMBIOS SIN TICKET");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NO SE HACEN DEVOLUCIONES DE DINERO");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "MUCHAS GRACIAS POR SU PREFERENCIA!!!");
				 * strBld.append(EpsonEscCommand.FF);
				 */
			}
			// Agregamos la información de lo que se imprimio al log de la
			// pantalla principal
			if (ventaVariada.getCliente().getId() != null)
				ConnectionManager.getTextArea().setText(ConnectionManager.getTextArea().getText() + "[VENTA] " + " "
						+ ventaVariada.getCliente().getPersonaoempresa() + EpsonEscCommand.LINE_FEED);
		} catch (Exception e) {
			// Si se da un error lo agregamos al log de la pantalla principal
			e.printStackTrace();

			StringWriter str = new StringWriter();
			PrintWriter writer = new PrintWriter(str);
			e.printStackTrace(writer);

			ConnectionManager.getTextArea().setText("[ERROR]: " + str.getBuffer().toString());
		}
	}
	
	private void crearDoc(VentaDoc ventaVariada) {
		try {
			Map<String, String> informacion = new HashMap<String, String>();
			List<HashMap<String, String>> detalle = new ArrayList<HashMap<String, String>>();
			informacion.put("usuario.nombre", UserSession.getUsr().getNombreUsuario());
			/*informacion.put("vendedor.nombre", ventaVariada.getVendedor());
			// cambio para imprimir la fecha de entrega de la orden de trabajo
			if (ventaVariada.getOrdenDeTrabajo() != null) {
				informacion.put("venta.fechaentrega",
						String.valueOf((ventaVariada.getOrdenDeTrabajo().getFechaEntrega())));
				informacion.put("autorizo", ventaVariada.getUsrVendedor().getNombreCompleto());
			}*/

			// Seteamos todas las variables en un hashmap
			if (ventaVariada.getCliente().getId() != null && ventaVariada.getCliente().getId() > 0) {
				informacion.put("cliente.nombre", ventaVariada.getCliente().getPersonaoempresa());
				informacion.put("cliente.dui", ventaVariada.getCliente().getDui());
				informacion.put("cliente.nit", ventaVariada.getCliente().getNit());
				
				if(ventaVariada.getCliente().getDui()==null && ventaVariada.getCliente().getNit()!=null)
					informacion.put("cliente.duinit", ventaVariada.getCliente().getNit());
				else if(ventaVariada.getCliente().getDui()!=null && ventaVariada.getCliente().getNit()==null)
					informacion.put("cliente.duinit", ventaVariada.getCliente().getDui());

				if (ventaVariada.getCliente().getDireccion() != null) {
					if (ventaVariada.getCliente().getDireccion().length() < 40)
						informacion.put("cliente.direccionl1", ventaVariada.getCliente().getDireccion());
					else if (ventaVariada.getCliente().getDireccion().length() >= 40) {
						informacion.put("cliente.direccionl1",
								ventaVariada.getCliente().getDireccion().substring(0, 40));
						informacion.put("cliente.direccionl2", ventaVariada.getCliente().getDireccion().substring(40,
								ventaVariada.getCliente().getDireccion().length()));
					}
				} else {
					informacion.put("cliente.direccionl1", "");
					informacion.put("cliente.direccionl2", "");
				}
				informacion.put("cliente.nrc", ventaVariada.getCliente().getNrc());
				informacion.put("cliente.giro", ventaVariada.getCliente().getGiro());
				//informacion.put("cliente.municipio", ventaVariada.getCliente().getDireccion());
				informacion.put("cliente.municipio", "");
				//informacion.put("cliente.departamento", ventaVariada.getCliente().getDireccion());
				informacion.put("cliente.departamento", "");
				informacion.put("cliente.telefono",
						(ventaVariada.getCliente().getTelefono1() != null ? ventaVariada.getCliente().getTelefono1()
								: (ventaVariada.getCliente().getTelefono2() != null
										? ventaVariada.getCliente().getTelefono2() : "")));
			} else {
				informacion.put("cliente.nombre", "");
				informacion.put("cliente.dui", "");
				informacion.put("cliente.nit", "");
				informacion.put("cliente.duinit", "");
				informacion.put("cliente.direccionl1", "");
				informacion.put("cliente.direccionl2", "");
				informacion.put("cliente.direccionl3", "");
				informacion.put("cliente.nrc", "");
				informacion.put("cliente.giro", "");
				informacion.put("cliente.municipio", "");
				informacion.put("cliente.departamento", "");
				informacion.put("cliente.codigocli", "");
				informacion.put("cliente.telefono", "");
			}

			informacion.put("venta.total", mf.format(ventaVariada.getTotal()));
			informacion.put("venta.totalentero", intf.format(ventaVariada.getTotal()));
			informacion.put("venta.totaldecimal", centf.format((ventaVariada.getTotal() * 100) % 100));
			//informacion.put("venta.detServ", ventaVariada.getDetalleServicio()); // "venta.detServ"
																					// es
																					// el
																					// nombre
																					// que
																					// lleva
																					// en
																					// el
																					// formato
																					// .txt
			/*if (ventaVariada.getMontocxc() > 0.0) {
				informacion.put("venta.formapagocont", "");
				informacion.put("venta.formapagocrd", "X");
				informacion.put("venta.formapagotxt", "CREDITO");
				
			} else {
				informacion.put("venta.formapagocont", "X");
				informacion.put("venta.formapagocrd", "");
				informacion.put("venta.formapagotxt", "CONTADO");
				
			}*/

			informacion.put("venta.subtotal", mf.format(ventaVariada.getTotal()));
			/*
			 * Este cambia en base al tipo de docuementos si es factura o
			 * credito fiscal
			 */
			/*if ("C".equals(ventaVariada.getTipoDeDocumento())) {
				informacion.put("venta.subtotal2", mf.format(ventaVariada.getMonto() + ventaVariada.getIva()));
			} else {
				//informacion.put("venta.subtotal2", mf.format(ventaVariada.getMonto() - ventaVariada.getRetenido()));
			}*/
			/*informacion.put("venta.totalnosujeta", mf.format(ventaVariada.getVentasNoSujetas()));
			informacion.put("venta.totalexenta", mf.format(ventaVariada.getVentasExentas()));
			informacion.put("venta.iva", mf.format(ventaVariada.getIva()));*/
			String str = numalet.convertNumToLetters(ventaVariada.getTotal().doubleValue(), true) + "DOLARES";
			if (str.length() < 60) {
				informacion.put("venta.numalet1", str);
				informacion.put("venta.numalet2", "");
			} else {
				informacion.put("venta.numalet1", str.substring(0, 58));
				informacion.put("venta.numalet2", str.substring(59, str.length()));
			}
			informacion.put("venta.fecha", df.format(ventaVariada.getFecha()));
			// informacion.put("venta.codvendedor",
			// ventaVariada.getCodVendedor());
			// Sacamos el detalle	// Modificacion 13-09-16
			for (DetVentaDoc tmpDet : ventaVariada.getDetVentas()) {
				HashMap<String, String> mapDet = new HashMap<String, String>();
				Double cant = tmpDet.getCantidad().doubleValue();
				mapDet.put("detalle.cantidad", cant.toString());
				mapDet.put("detalle.descripcion", tmpDet.getDetalle());
				//mapDet.put("detalle.preciounitario", mf.format(tmpDet.getPrecioUnitario()));
				mapDet.put("detalle.preciounitario", mf.format(tmpDet.getPrecioUnitario()));
				System.out.println("Total detalle"+ tmpDet.getTotal());
				if (tmpDet.getTipo().equals("G")) {
					mapDet.put("detalle.gravado", mf.format(tmpDet.getTotal()));
					mapDet.put("detalle.exento", null);
					mapDet.put("detalle.nosujeto", null);
				} else if (tmpDet.getTipo().equals("J")) {
					mapDet.put("detalle.nosujeto", mf.format(tmpDet.getTotal()));
					mapDet.put("detalle.exento", null);
					mapDet.put("detalle.gravado", null);
				} else if (tmpDet.getTipo().equals("E")) {
					mapDet.put("detalle.exento", mf.format(tmpDet.getTotal()));
					mapDet.put("detalle.gravado", null);
					mapDet.put("detalle.nosujeto", null);
				}
				
				mapDet.put("detalle.totalentero", intf.format(tmpDet.getTotal()));
				mapDet.put("detalle.totaldecimal", centf.format(Math.round((tmpDet.getTotal() * 100) % 100)));
				detalle.add(mapDet);
			}
			/* 23/06/2016 */
			
			System.out.println("LLego a seleccionar el archivo");
			//ventaVariada.setTipoDeDocumento("F"); //En algun punto es encesario agregar este tipo de documentp
			if (!ventaVariada.getComprobante().getTipo().equals("T")) {
				String formato;
				tipoDocumento = ventaVariada.getComprobante().getTipo();
				if (ventaVariada.getComprobante().getTipo().equals("FAC")) {
					System.out.println("Entro al acrchivo de factura");
					formato = "config\\" + (ventaVariada.getComprobante().getCodigo() != null
							&& ventaVariada.getComprobante().getCodigo().trim().length() > 0 ? ventaVariada.getComprobante().getCodigo()
									: "cprFacAudiomed") //nuevo formato de factura para prueba: formato actual: cprFacDefault ; nuevo: cprFacMatriz.txt cprFacAudiomed
							+ ".txt";
				} else if (ventaVariada.getComprobante().getTipo().equals("CCF")) {
					System.out.println("LLego a archivo CCF");
					formato = "config\\" + (ventaVariada.getComprobante().getCodigo() != null
							&& ventaVariada.getComprobante().getCodigo().trim().length() > 0 ? ventaVariada.getComprobante().getCodigo()
									: "cprCcfDefault")
							+ ".txt";
				} else if (ventaVariada.getComprobante().getTipo().equals("OTR")) {
					formato = "config\\" + (ventaVariada.getComprobante().getCodigo() != null
							&& ventaVariada.getComprobante().getCodigo().trim().length() > 0 ? ventaVariada.getComprobante().getCodigo()
									: "cprHTDefault")
							+ ".txt";
				} else {
					formato = "config\\cprXMatriz.txt";
				}

				// generamos el string con formato de impresion para la venta
				// 789
				String salida = preparePrintString(formato, informacion, detalle);
				System.out.println("Paso del preparePrintString");
				String metodoImpresion = PropertiesLoader.appProperties.getProperty("global.printer.method");
				if (metodoImpresion == null || metodoImpresion.isEmpty()
						|| metodoImpresion.equalsIgnoreCase("serialport")) {
					System.out.println("ENtro al archivo de impresora");
					Printer receipt = PrinterFactory.getPrinterSlip();
					receipt.init(impresora);
					receipt.write(salida);
					receipt.close();
				} else if (metodoImpresion != null && !metodoImpresion.isEmpty()
						&& metodoImpresion.equalsIgnoreCase("printername")) {
					impresion(salida, impresora);
				}
			} else {
				// Si es un ticket se le da formato aqui mismo descomentariar si
				// se pretente impresion centralizada de tickets
				/*
				 * StringBuilder strBld = new StringBuilder();
				 * strBld.append(ventaVariada.getInfoEmpresa().getNombre());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(ventaVariada.getInfoEmpresa().getDireccion());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NRC: " + ventaVariada.getInfoEmpresa().getNrc());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "IVA DECRETO LEGISLATIVO 296");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NIT: " + ventaVariada.getInfoEmpresa().getNit());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Giro: " + ventaVariada.getInfoEmpresa().getGiro());
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Caja # " + UserSession.getUsr().getCaja());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Ticket # " + ventaVariada.getNumDocumento());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append(getTextoRellenado(new
				 * SimpleDateFormat("dd/MM/yyyy").format(ventaVariada.
				 * getFechaVenta()), (short)(MAX_CHAR_TKT/2), true, "L"));
				 * strBld.append(getTextoRellenado(new SimpleDateFormat(
				 * "hh:mm a").format(ventaVariada.getFechaVenta()),
				 * (short)(MAX_CHAR_TKT/2), true, "R"));
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Vende " +
				 * ventaVariada.getUsrEfectua().getNombreCompleto());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Cant. Detalle      P. Uni.  Total  ");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append("----------------------------------------");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * for( DetVentaProdServ detVenta : ventaVariada.getDetVenta()
				 * ){ strBld.append(getTextoRellenado(intf.format(detVenta.
				 * getCantidad()), (short)6, true, "R"));
				 * strBld.append(getTextoRellenado(detVenta.getDetalle(),
				 * (short)17, true, "L"));
				 * strBld.append(getTextoRellenado(mf.format(detVenta.
				 * getPrecioUnitario()), (short)7, true, "R"));
				 * strBld.append(getTextoRellenado(mf.format(detVenta.getMonto()
				 * ), (short)8, true, "R"));
				 * if(detVenta.getTipoVenta().equals("J")) strBld.append(" N");
				 * else if(detVenta.getTipoVenta().equals("G")) strBld.append(
				 * " G"); else if(detVenta.getTipoVenta().equals("E"))
				 * strBld.append(" E");
				 * strBld.append(EpsonEscCommand.LINE_FEED); }
				 * 
				 * if( ventaVariada.getCliente().getId() != null &&
				 * ventaVariada.getCliente().getId() > 0 ){
				 * strBld.append(ventaVariada.getCliente().getNombre() + " " +
				 * ventaVariada.getCliente().getApellido()); }
				 * 
				 * strBld.append(getTextoRellenado("Subtotal gravado: " +
				 * mf.format(ventaVariada.getMonto()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("Subtotal exento: " +
				 * mf.format(ventaVariada.getVentasExentas()), MAX_CHAR_TKT,
				 * true, "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("Subtotal no sujeto: " +
				 * mf.format(ventaVariada.getVentasNoSujetas()), MAX_CHAR_TKT,
				 * true, "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(getTextoRellenado("TOTAL: " +
				 * mf.format(ventaVariada.getTotal()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(numalet.convertNumToLetters(ventaVariada.
				 * getTotal().doubleValue(), true ) + "DÃ“LARES");
				 * 
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append(getTextoRellenado("Cambio: " +
				 * mf.format(ventaVariada.getCambio()), MAX_CHAR_TKT, true,
				 * "R")); strBld.append(
				 * "G = Gravado,  E = Exento,  N = No sujeto");
				 * 
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Llenar si la venta es igual/mayor a $200.00");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Nombre: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NIT: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "DUI: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "Firma: ______________________________");
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("Serie autorizada del " +
				 * ventaVariada.getCodAutTick() + "|" +
				 * ventaVariada.getNumDocIni());
				 * strBld.append(EpsonEscCommand.LINE_FEED);
				 * 
				 * strBld.append("NO SE HACEN CAMBIOS SIN TICKET");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "NO SE HACEN DEVOLUCIONES DE DINERO");
				 * strBld.append(EpsonEscCommand.LINE_FEED); strBld.append(
				 * "MUCHAS GRACIAS POR SU PREFERENCIA!!!");
				 * strBld.append(EpsonEscCommand.FF);
				 */
			}
			// Agregamos la información de lo que se imprimio al log de la
			// pantalla principal
			if (ventaVariada.getCliente().getId() != null)
				ConnectionManager.getTextArea().setText(ConnectionManager.getTextArea().getText() + "[VENTA] " + " "
						+ ventaVariada.getCliente().getPersonaoempresa() + EpsonEscCommand.LINE_FEED);
		} catch (Exception e) {
			// Si se da un error lo agregamos al log de la pantalla principal
			e.printStackTrace();

			StringWriter str = new StringWriter();
			PrintWriter writer = new PrintWriter(str);
			e.printStackTrace(writer);

			ConnectionManager.getTextArea().setText("[ERROR]: " + str.getBuffer().toString());
		}
	}

	private void crearDoc(Cotizacion cotizacionVariada) {
		try {
			tipoDocumento = "Z";
			Map<String, String> informacion = new HashMap<String, String>(); // mapa
																				// de
																				// llave,
																				// valor
																				// para
																				// lo
																				// que
																				// se
																				// usara
																				// en
																				// el
																				// reporte
			List<HashMap<String, String>> detalle = new ArrayList<HashMap<String, String>>(); // mapas
																								// de
																								// llave,
																								// valor
																								// para
																								// la
																								// información
																								// de
																								// cada
																								// detalle
			// agregamos la información del cliente al mapa
			if (cotizacionVariada.getCliente().getId() != null && cotizacionVariada.getCliente().getId() > 0) {
				// verificamos, en caso se tenga razon social se usa la misma,
				// sino el nombre de forma apellidos, nombre
				if (cotizacionVariada.getCliente().getRazonSocial() != null
						&& !cotizacionVariada.getCliente().getRazonSocial().isEmpty()) {
					informacion.put("cliente.nombre", cotizacionVariada.getCliente().getRazonSocial());
				} else {
					informacion.put("cliente.nombre", cotizacionVariada.getCliente().getApellido() + ", "
							+ cotizacionVariada.getCliente().getNombre());
				}
				if (cotizacionVariada.getCliente().getDireccion() != null) {
					informacion.put("cliente.direccion", cotizacionVariada.getCliente().getDireccion());
				} else {
					informacion.put("cliente.direccion", "");
				}
				informacion.put("cliente.telefono",
						(cotizacionVariada.getCliente().getTelCelular() != null
								&& !cotizacionVariada.getCliente().getTelCelular().isEmpty()
										? cotizacionVariada.getCliente().getTelCelular() : "")
						+ (cotizacionVariada.getCliente().getTelCelular() != null
								&& !cotizacionVariada.getCliente().getTelCelular().isEmpty()
								&& cotizacionVariada.getCliente().getTelFijo() != null
								&& !cotizacionVariada.getCliente().getTelFijo().isEmpty() ? " o " : "")
						+ (cotizacionVariada.getCliente().getTelFijo() != null
								&& !cotizacionVariada.getCliente().getTelFijo().isEmpty()
										? cotizacionVariada.getCliente().getTelCelular() : ""));
			}

			// ingresamos la informacón general de la cotizacion
			informacion.put("cotiz.fecha", df.format(cotizacionVariada.getFechaIngreso()));
			if (cotizacionVariada.getDescripcion() != null) {
				if (cotizacionVariada.getDescripcion().length() > 140) {
					informacion.put("cotiz.descripcionl1", cotizacionVariada.getDescripcion().substring(0, 70));
					informacion.put("cotiz.descripcionl2", cotizacionVariada.getDescripcion().substring(70, 140));
					informacion.put("cotiz.descripcionl3", cotizacionVariada.getDescripcion().substring(140,
							cotizacionVariada.getDescripcion().length()));
				} else if (cotizacionVariada.getDescripcion().length() > 70) {
					informacion.put("cotiz.descripcionl1", cotizacionVariada.getDescripcion().substring(0, 70));
					informacion.put("cotiz.descripcionl2", cotizacionVariada.getDescripcion().substring(70,
							cotizacionVariada.getDescripcion().length()));
					informacion.put("cotiz.descripcionl3", "");
				} else {
					informacion.put("cotiz.descripcionl1", cotizacionVariada.getDescripcion());
					informacion.put("cotiz.descripcionl2", "");
					informacion.put("cotiz.descripcionl3", "");
				}
			}
			informacion.put("cotiz.totalentero", intf.format(cotizacionVariada.getTotalCost()));
			informacion.put("cotiz.totaldecimal",
					mf.format(Math.round((cotizacionVariada.getTotalCost() * 100) % 100)));

			// llenamos el mapa de los detalles
			for (CotizacionDet det : cotizacionVariada.getDetCotizacion()) {
				HashMap<String, String> mapDet = new HashMap<String, String>();
				mapDet.put("detalle.cantidad", mf.format(det.getCantidad().doubleValue()));
				mapDet.put("detalle.descripcion", det.getDescripcion());
				mapDet.put("detalle.preciounitario", mf.format(det.getPrecioUnitario()));
				mapDet.put("detalle.totalentero", intf.format(det.getMonto()));
				mapDet.put("detalle.totaldecimal", centf.format(Math.round((det.getMonto() * 100) % 100)));
				detalle.add(mapDet);
			}

			String salida = preparePrintString("config\\cprCotizacionDefault.txt", informacion, detalle);

			String metodoImpresion = PropertiesLoader.appProperties.getProperty("global.printer.method");
			if (metodoImpresion == null || metodoImpresion.isEmpty()
					|| metodoImpresion.equalsIgnoreCase("serialport")) {
				Printer receipt = PrinterFactory.getPrinterSlip();
				receipt.init(impresora);
				receipt.write(salida);
				receipt.close();
			} else if (metodoImpresion != null && !metodoImpresion.isEmpty()
					&& metodoImpresion.equalsIgnoreCase("printername")) {
				impresion(salida, impresora);
			}
			if (cotizacionVariada.getCliente().getId() != null)
				ConnectionManager.getTextArea().setText(ConnectionManager.getTextArea().getText() + "[COTIZ] " + " "
						+ cotizacionVariada.getCliente().getPersonaoempresa() + EpsonEscCommand.LINE_FEED);
		} catch (Exception e) {
			// Si se da un error lo agregamos al log de la pantalla principal
			e.printStackTrace();

			StringWriter str = new StringWriter();
			PrintWriter writer = new PrintWriter(str);
			e.printStackTrace(writer);

			ConnectionManager.getTextArea().setText("[ERROR]: " + str.getBuffer().toString());
		}
	}

	/*
	 * @Params: formato - string con la direccion del txt que servira de guia
	 * para la impresion informacion - diferentes contenidos de caracter
	 * generico que seran reemplazados en el formato detalle - información de la
	 * diferente información que compone un detalle y que sera reemplazada en el
	 * formato
	 * 
	 * @Description: Genera el string a imprimir mediante la lectura de un
	 * formato dato y el reemplazo de los diferentes elementos dentro del mismo.
	 */
	private String preparePrintString(String formato, Map<String, String> informacion,
			List<HashMap<String, String>> detalle) {
		StringBuilder salida = new StringBuilder();
		try {
			// 23-06-2016 Procedemos a crear el string basado en el formato
			// reemplazando las coincidencias segun los mapas que acabamos de
			// crear 789
			BufferedReader br = null;
			br = new BufferedReader(new FileReader(formato));
			String linea = br.readLine();

			while (linea != null) {
				List<String> arrLineas;
				Integer linePosition;
				String style = "";
				// Verificamos si es una linea normal o si tiene detalle
				if (linea.contains("{|") && linea.contains("|}")) {
					if (linea.contains("<|") && linea.contains("|>")) {
						style = linea.substring(linea.indexOf("<|"), linea.indexOf("|>") + 2);
					} else {
						style = "";
					}
					linea = linea.replace("{|", "").replace("|}", "");
					String[] pedazos = linea.split("==");
					byte numDetalles = new Byte(pedazos[0].replaceAll("\\<\\|.+?:.*?\\|\\>", ""));
					linea = pedazos[1];
					String lineaTmp = linea;
					byte contApl = 0;

					Integer ln = (13 / detalle.size()); // 13 lineas son las del
														// Hoja de trabajo en el
														// formato impreso,
														// y se calcula el
														// numero de lineas que
														// corresponden a cada
														// item para la
														// impresion

					for (HashMap<String, String> tmpHashDet : detalle) {
						linePosition = 0;
						arrLineas = new ArrayList<String>();
						List<String> coincidencias = new ArrayList<String>();
						Matcher m = Pattern.compile(REGEX_CAMPO_IMPRESION).matcher(lineaTmp);
						while (m.find())
							coincidencias.add(m.group());

						linea = lineaTmp;
						for (String coinci : coincidencias) {
							String[] pedTemp = coinci.replace("[|", "").replace("|]", "").split(":"); // [|campo:tamaño:alineacion{:multiple}|]
							String[] tmpLns = replacePlaceHolder(coinci, tmpHashDet.get(pedTemp[0]));
							linea = linea.replace(coinci, tmpLns[0]);

							// If para tipo de documento
							if (tipoDocumento.equals("H") || tipoDocumento.equals("Z")) {

								if (tmpLns.length > 1) {
									for (byte linCnt = 1; linCnt < ln; linCnt++) {
										if (arrLineas == null || linCnt > arrLineas.size()) {
											if (linCnt == tmpLns.length)
												linCnt = 14;
											else {
												arrLineas.add(formatearSubLinea("", linePosition, tmpLns[linCnt]));
											}
										} else {
											arrLineas.set(linCnt - 1, formatearSubLinea(arrLineas.get(linCnt - 1),
													linePosition, tmpLns[linCnt]));
										}
									}
								}
							}

							else if (pedTemp.length == 4) {
								if (tmpLns.length > 1) {
									for (byte linCnt = 1; linCnt < tmpLns.length
											&& linCnt < Integer.valueOf(pedTemp[3]); linCnt++) {
										if (arrLineas == null || linCnt > arrLineas.size()) {
											arrLineas.add(formatearSubLinea("", linePosition, tmpLns[linCnt]));
										} else {
											arrLineas.set(linCnt - 1, formatearSubLinea(arrLineas.get(linCnt - 1),
													linePosition, tmpLns[linCnt]));
										}
									}
								}
							}

							linePosition += Integer.valueOf(pedTemp[1]);
						}

						salida.append(style + adaptarLinea(linea));
						salida.append(EpsonEscCommand.LINE_FEED);

						if (arrLineas != null && arrLineas.size() > 0)
							for (byte linCnt = 0; linCnt < arrLineas.size(); linCnt++) {
								salida.append(adaptarLinea(arrLineas.get(linCnt)));
								salida.append(EpsonEscCommand.LINE_FEED);
								contApl++;
							}
						contApl++;
					}

					for (; contApl <= numDetalles; contApl++)
						salida.append(EpsonEscCommand.LINE_FEED);

				} else {
					List<String> coincidencias = new ArrayList<String>();
					Matcher m = Pattern.compile(REGEX_CAMPO_IMPRESION).matcher(linea);
					linePosition = 0;
					arrLineas = new ArrayList<String>();
					while (m.find())
						coincidencias.add(m.group());

					for (String coinci : coincidencias) {
						String[] pedTemp = coinci.replace("[|", "").replace("|]", "").split(":");

						String[] tmpLns = replacePlaceHolder(coinci, informacion.get(pedTemp[0]));
						linea = linea.replace(coinci, tmpLns[0]);
						if (pedTemp.length == 4) {
							if (tmpLns.length > 1) {
								for (byte linCnt = 1; linCnt < tmpLns.length
										&& linCnt < Integer.valueOf(pedTemp[4]); linCnt++) {
									if (arrLineas == null || linCnt > arrLineas.size()) {
										arrLineas.add(formatearSubLinea("", linePosition, tmpLns[linCnt]));
									} else {
										arrLineas.set(linCnt - 1, formatearSubLinea(arrLineas.get(linCnt - 1),
												linePosition, tmpLns[linCnt]));
									}
								}
							}
						}
						linePosition += Integer.valueOf(pedTemp[1]);
					}
					salida.append(adaptarLinea(linea));
					salida.append(EpsonEscCommand.LINE_FEED);
					if (arrLineas != null && arrLineas.size() > 0)
						for (byte linCnt = 0; linCnt < arrLineas.size(); linCnt++) {
							salida.append(adaptarLinea(arrLineas.get(linCnt)));
							salida.append(EpsonEscCommand.LINE_FEED);
						}
				}

				linea = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.err.println("Error en preparePrintString");
			e.printStackTrace();
		}
		return salida.toString();
	}

	private String adaptarLinea(String linea) {
		if (linea.length() > MAX_CHAR_CPR)
			return linea.substring(0, MAX_CHAR_CPR);
		else
			return linea;
	}

	private String[] replacePlaceHolder(String pattern, String value) {
		String[] res = null;
		try {
			pattern = pattern.replace("[|", "").replace("|]", "");
			String[] pedazos = pattern.split(":");
			if (value == null || value.trim().equals("")) {
				res = new String[1];
				res[0] = getTextoRellenado("", new Short(pedazos[1]), true, pedazos[2]);
			} else {
				if (value.length() <= new Short(pedazos[1])) {
					res = new String[1];
					res[0] = getTextoRellenado(value, new Short(pedazos[1]), true, pedazos[2]);
				} else {
					String[] arrDetLrg = splitTxtDetalle(value, new Short(pedazos[1]));
					res = new String[arrDetLrg.length];
					byte cntLin = 0;
					for (String lin : arrDetLrg)
						res[cntLin++] = getTextoRellenado(lin, new Short(pedazos[1]), true, pedazos[2]);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return res;
	}

	private String getTextoRellenado(String texto, short longitud, boolean truncar, String alineacion) {
		String res = "";
		short rem = 0;
		if (texto == null)
			texto = "";
		// texto = texto.trim();
		rem = (short) (longitud - texto.length());

		if (rem <= 0)
			res = truncar ? texto.substring(0, longitud) : texto;
		else {
			res = texto;
			for (short cnt = 1; cnt <= rem; cnt++)
				if (alineacion.equalsIgnoreCase("R"))
					res = " " + res;
				else
					res += " ";
		}
		return res;
	}

	/*
	 * private String getTextoRellenado(String texto, short longitud, boolean
	 * truncar, String alineacion) { String res = ""; short rem = 0; if(texto ==
	 * null) texto = ""; //texto = texto.trim(); rem = (short)(longitud -
	 * texto.length());
	 * 
	 * if(rem <= 0) res = truncar?texto.substring(0, longitud):texto; else { res
	 * = texto; for(short cnt = 1; cnt <= rem; cnt++)
	 * if(alineacion.equalsIgnoreCase("R")) res = " " + res; else res += " "; }
	 * return res; }
	 */

	private String[] splitTxtDetalle(String detalle, short numCaracteres) {
		int longStr = detalle.length();
		String[] res = null;

		if (longStr > numCaracteres) {
			int numPedazos = ((longStr - (longStr % numCaracteres)) / numCaracteres)
					+ (longStr % numCaracteres > 0 ? 1 : 0);
			res = new String[numPedazos];

			if (numPedazos > 1) {
				byte idxArr = 0;
				for (short cntCar = 0; cntCar < longStr; cntCar += numCaracteres) {
					String strDet = detalle.substring(cntCar,
							(cntCar + numCaracteres > longStr ? longStr : cntCar + numCaracteres));
					res[idxArr++] = strDet;
				}
			} else
				res[0] = detalle;
		} else {
			res = new String[1];
			res[0] = detalle;
		}

		return res;
	}

	private String formatearSubLinea(String valorActual, Integer posicionInicial, String valorAgregar) {
		String res = "";
		if (posicionInicial > 0) {
			res = getTextoRellenado(valorActual, posicionInicial.shortValue(), true, "L");
		}
		return res.concat(valorAgregar);
	}

	private void impresion(String texto, String nombreImpresora) {
		// Input the file
		// texto += new String(EpsonEscCommand.CUTPAPER);
		/*
		 * IMPRESION DE TEXTO DIRECTO ByteArrayInputStream textStream = null;
		 * try { textStream = new ByteArrayInputStream(texto.getBytes("UTF8"));
		 * } catch (Exception ffne) { ffne.printStackTrace(); } if (textStream
		 * == null) return; Font normalFont = new Font ("serif", Font.PLAIN, 5);
		 * Font boldFont = new Font ("serif", Font.BOLD, 5);
		 * 
		 * System.err.println(texto); // Set the document type //DocFlavor
		 * myFormat = DocFlavor.INPUT_STREAM.AUTOSENSE; DocFlavor myFormat =
		 * DocFlavor.INPUT_STREAM.AUTOSENSE; // Create a Doc Doc myDoc = new
		 * SimpleDoc(textStream, myFormat, null); // Build a set of attributes
		 * PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		 * aset.add(new PrinterResolution(240,144,ResolutionSyntax.DPI));
		 * aset.add(new Copies(1));
		 * 
		 * PrintService[] services =
		 * PrintServiceLookup.lookupPrintServices(myFormat, aset);
		 * PrintServiceAttributeSet printServiceAttributeSet = new
		 * HashPrintServiceAttributeSet(); printServiceAttributeSet.add(new
		 * PrinterName(nombreImpresora, null)); services =
		 * PrintServiceLookup.lookupPrintServices(null,
		 * printServiceAttributeSet);
		 * 
		 * // Create a print job from one of the print services if
		 * (services.length > 0) { DocPrintJob job =
		 * services[0].createPrintJob(); try { PrintJobWatcher pjw = new
		 * PrintJobWatcher(job); job.print(myDoc, aset); pjw.waitForDone();
		 * 
		 * } catch (PrintException pe) { pe.printStackTrace(); } }
		 */

		// Build a set of attributes
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(new PrinterResolution(240, 288, ResolutionSyntax.DPI));
		aset.add(new Copies(1));

		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, aset);
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(nombreImpresora, null));
		services = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);

		// Create a print job from one of the print services
		if (services.length > 0) {
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = PrinterUtils.getMinimumMarginPageFormat(printerJob);
			printerJob.setPrintable(PrinterUtils.createPrintable(texto), pageFormat);
			try {
				printerJob.print();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}
}