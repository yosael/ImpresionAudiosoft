package com.saplic.print.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JTextArea;

public class ConnectionManager {
	private static Connection connection = null;
	private static String driver = "";
	private static String url = "";
	private static String user = "";
	private static String pass = "";
	
	//Global Components
	private static JTextArea textArea;
	
    static {}
    
    public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		ConnectionManager.connection = connection;
	}
	
	public static void setProperties(){
		try{
			//Tranfiriendo las propiedades cargadas a la entidad
			driver = PropertiesLoader.appProperties.getProperty("global.id.driver");
			url = PropertiesLoader.appProperties.getProperty("global.id.url");
			user = PropertiesLoader.appProperties.getProperty("global.id.user");
			pass = PropertiesLoader.appProperties.getProperty("global.id.pass");
						
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	public static void setProperties(String sdriver, String surl, String suser, String spass){
		try {
			driver = sdriver;
			url = surl;
			user = suser;
			pass = spass;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean crearConexion(){
    	try {
			Class.forName( driver );
			System.out.println("PostgreSQL JDBC Driver Registrado!");
			System.out.println("Usuario: "+user);
			System.out.println("Contraseña: "+pass);
			
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado el Driver de postgresql!");
			e.printStackTrace();
			return false;
		} 
    	
    	try {
			connection = DriverManager.getConnection( url, user, pass );
			if (connection != null) {
				System.out.println("Conexion establecida correctamente!");
			} else {
				System.out.println("Conexion fallida!");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("Conexion fallida!");
			e.printStackTrace();
			return false;
		} 
    	
    	return true;
    }    
    
    public static boolean cerrarConexion(){
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Conexion Cerrada Correctamente");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("La conexion no pudo cerrarse");
				return false;
			} finally {
				connection = null;
			}
		}
		return true;
    }    

	public static JTextArea getTextArea() {
		return textArea;
	}

	public static void setTextArea(JTextArea textArea) {
		ConnectionManager.textArea = textArea;
	}
}
