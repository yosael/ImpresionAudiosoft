package com.saplic.print.util;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterResolution;
import javax.imageio.*;

public class Tester {
	
	public static void main(String[] args) {
		//Aqui llamamos las funciones de lo que queremos probar
		String text = "<|NK57 Monospace Cd Rg:PLAIN:12:1.4|>        Mario Acosta                              30/06/2016\n"
				+"          Calle el pedregal #25\n"
				+"      2263-8590\n"
				+"<|NK57 Monospace Cd Rg:PLAIN:12:1.8|>\n"
				+"                                            X\n"
				+"<|NK57 Monospace Cd Rg:PLAIN:12:2.56|>\n"
				+"<|NK57 Monospace Cd Rg:PLAIN:12:1.52|>"
				+"   100.00  Detalle de producto 1                  2.25       225 00\n"
				+"   100.00  Detalle de producto 2                  2.25       225 00\n"
				+"   100.00  Detalle de producto 3                  2.25       225 00\n"
				+"   100.00  Detalle de producto 4                  2.25       225 00\n"
				+"\n"
				+"\n"
				+"\n"
				+"\n"
				+"\n"
				+"\n"
				+"\n"
				+"     100.00  Detalle de producto 3                2.25       225 00\n"
				+"\n"
				+"     Cinco mil oquinientos cincuenta\n"
				+"     y cinco con 50/100 dolares\n"
				+"                                                            5555 50";
		testFormatedPrint(text);
		//testPrintImage("images/happy_penguin.jpg");
	}
	
	/*
	 * Funcion que permite probar queries directamente, con los parametros de conexion y 
	 * devuelve el listado de los valores en la primera columna del result set. El query
	 * es introducido desde la linea de comandos y termina la ejecucion del bucle cuando
	 * se ingresa salir o si se genera una excepcion.
	 */
	public static void testQueries(){
		Connection connection = null;	
		PreparedStatement preparedStatement = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String query;
		try {
			System.out.println("Estableciendo conección");
			if( ConnectionManager.getConnection() == null ){
				ConnectionManager.setProperties("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/pos_industriasma_db", "pos_industriasma_user", "industriasma");
				
				if( !ConnectionManager.crearConexion() ){
					//Cancela la aplicacion
					System.out.println("Cancelando aplicacion");
					System.exit( 1 );
				} else {
					System.out.println("Se creo la coneccion");
				}
			}
			connection = ConnectionManager.getConnection();
			if ( connection == null)
				return;
			connection.setAutoCommit(false);
			while (true){
				System.out.print("Query: ");
				 query = br.readLine();
				 if ("salir".equals(query))
					 break;
				 System.err.println(query);
				 preparedStatement = connection.prepareStatement(query);
				 ResultSet result = preparedStatement.executeQuery();
				 System.out.println("Resultados: " + result.getFetchSize());
				 while (result.next()){
					 System.out.println(result.getString(1));
				 }
			}
			connection.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Funcion que permite probar la impresion directa de texto
	 */
	public static void testTextPrint(String text){
		try {
			String defaultPrinter = PrintServiceLookup.lookupDefaultPrintService().getName();
		    System.out.println("Default printer: " + defaultPrinter);
		    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		    
		    // prints the famous hello world! plus a form feed
		    InputStream is = new ByteArrayInputStream(text.getBytes("UTF8"));

		    PrintRequestAttributeSet  pras = new HashPrintRequestAttributeSet();
		    pras.add(new Copies(1));
		    pras.add(new PrinterResolution(240,288,ResolutionSyntax.DPI));

		    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		    Doc doc = new SimpleDoc(is, flavor, null);
		    DocPrintJob job = service.createPrintJob();

		    PrintJobWatcher pjw = new PrintJobWatcher(job);
		    job.print(doc, pras);
		    pjw.waitForDone();
		    
		    
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Funcion que permite probar la impresion de texto estilizado
	 */
	public static void testFormatedPrint(String text){
		try {	
			//registramos todas las fuentes de la carpeta fonts en el ambiente gráfico para poder utilizarlas
			PrinterUtils.loadFonts("config\\fonts");
			PropertiesLoader.loadProperties();
			//desplegamos el listado de familias de fuentes del ambiente gráfico
			//de esta manera podemos verificar que se carguen correctamente y cuales
			//familias tenemos disponibles para usarlas
			/*for (String name : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()){
		    	 System.out.println("Family name: " + name);
		    }*/
			//creamos el trabajo de impresion
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = PrinterUtils.getMinimumMarginPageFormat(printerJob);
			//asignamos lo que se va a imprimir utilzando la funcion de PrinterUtils
			printerJob.setPrintable(PrinterUtils.createPrintable(text), pageFormat);
			//mostramos el modal para la seleccion de impresora
			if (printerJob.printDialog()) {
				try {
					printerJob.print();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Funcion para probar el mandar a imprimir una imagen
	 */
	
	static int w, h;
	static BufferedImage img = null;
	
	public static void testPrintImage(String path){
		try { 
			img = ImageIO.read(new File(path));
		    w = img.getWidth(null); //obtiene el ancho
            h = img.getHeight(null); //obtiene el alto
            //creamos el trabajo de impresion
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = PrinterUtils.getMinimumMarginPageFormat(printerJob);
			//asignamos lo que se va a imprimir utilzando la funcion de PrinterUtils
			printerJob.setPrintable(new Printable() {
				public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
					Graphics2D graphics2D = (Graphics2D)graphics;
					graphics2D.translate(pageFormat.getImageableX(),pageFormat.getImageableY());
					graphics2D.drawImage(img, 0, 0, 100, 100, 0, 0, w, h, null);
					return (pageIndex == 0 ? PAGE_EXISTS : NO_SUCH_PAGE);
				}
			}, pageFormat);
			//mostramos el modal para la seleccion de impresora
			if (printerJob.printDialog()) {
				try {
					printerJob.print();
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("Unable te read file");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
