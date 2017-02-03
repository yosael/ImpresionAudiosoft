package com.saplic.print.gui;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.BasicConfigurator;
import org.jasypt.digest.StandardStringDigester;

import com.saplic.print.entities.CajaRegistradora;
import com.saplic.print.entities.UserSession;
import com.saplic.print.entities.Usuario;
import com.saplic.print.util.ComboItem;
import com.saplic.print.util.ConnectionManager;
import com.saplic.print.util.PrinterUtils;
import com.saplic.print.util.PropertiesLoader;


public class StartPoint extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField txtUsuario;
	private JPasswordField txtContrasena;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JButton btnEntrarSys;
	private JPanel panel;
	private static final String RUTA_IMG = "./config/";
	//private static final String RUTA_IMG = "./";


	private Image dogImage;
    private SystemTray sysTray;
    private PopupMenu menu;
    private MenuItem item1;
    private MenuItem item2;
	
	private Connection connection = null;
	private PreparedStatement preparedStatementSelect = null;

	//For icon app
	TrayIcon trayIcon;
    SystemTray tray;

	public StartPoint(){
		super( "Inicio de sesion" );
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(10, 24, 382, 127);
		panel.setBorder(BorderFactory.createTitledBorder("Acceso a impresion de comprobantes"));
		panel.setLayout(null);
		getContentPane().add(panel);
		this.setResizable(false);
		
		txtUsuario = new JTextField();
		txtUsuario.setBounds(109, 27, 163, 20);
		panel.add(txtUsuario);
		txtUsuario.setColumns(10);
		
		lblNewLabel = new JLabel("Usuario:");
		lblNewLabel.setBounds(21, 27, 78, 14);
		panel.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		lblNewLabel_1 = new JLabel("Contrasena:");
		lblNewLabel_1.setBounds(21, 58, 100, 14);
		panel.add(lblNewLabel_1);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		txtContrasena = new JPasswordField();
		txtContrasena.setBounds(109, 58, 163, 20);
		panel.add(txtContrasena);
		
		btnEntrarSys = new JButton("Entrar al Sistema");
		btnEntrarSys.setBounds(109, 89, 139, 23);
		panel.add(btnEntrarSys);
		btnEntrarSys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loguearse();
			}
		});
		btnEntrarSys.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		this.setSize(410, 200);
		
		//Inicializando la conexion
		if( ConnectionManager.getConnection() == null ){
			ConnectionManager.setProperties();
			
			if( !ConnectionManager.crearConexion() ){
				//Cancela la aplicacion
				System.exit( 1 );
			}
		}
		
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing( WindowEvent event ) {
					ConnectionManager.cerrarConexion();
					dispose();
					System.exit( 1 );
				}
			}
		);
		
		Action doLogin = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		    	loguearse();
		    }
		};
		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "doLogin");
		panel.getActionMap().put("doLogin", doLogin);
		
		//For Icon app
		if(SystemTray.isSupported()){
            tray=SystemTray.getSystemTray();
		
            Image image=Toolkit.getDefaultToolkit().getImage(RUTA_IMG + "printericon.png");
            ActionListener exitListener=new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
             };
             
             ActionListener abrirListener=new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                	 setVisible(true);
                     setExtendedState(JFrame.NORMAL);
                 }
              };
            PopupMenu popup=new PopupMenu();
            
            MenuItem defaultItem=new MenuItem("Salir");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            
            defaultItem=new MenuItem("Abrir");
            defaultItem.addActionListener(abrirListener);
            popup.add(defaultItem);
            
            trayIcon=new TrayIcon(image, "Impresion de Comprobantes", popup);
            trayIcon.setImageAutoSize(true);
        }

		addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==ICONIFIED){
                    try {
                        tray.add(trayIcon);
                        setVisible(true);
                    } catch (AWTException ex) {
            
                    }
                }

		        if(e.getNewState()==7){
		        	try{
			            tray.add(trayIcon);
			            setVisible(true);
			            //System.out.println("Agregando icono");
		            }catch(AWTException ex){
		            	//System.out.println("No se pudo agregar el icono");
		            }
		        }
		        if(e.getNewState()==MAXIMIZED_BOTH){
                    tray.remove(trayIcon);
                    setVisible(true);
                }
                if(e.getNewState()==NORMAL){
                    tray.remove(trayIcon);
                    setVisible(true);
            }
        }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage(RUTA_IMG + "printericon.png"));
        //End for icon app
	}
	
	private void mostrarSeleccionCaja() {
		
	}
	
	private void ocultarSeleccionCaja() {
		
	}
	
	private boolean loguearse() {
		
		this.connection = ConnectionManager.getConnection();
		StandardStringDigester digester = new StandardStringDigester();
		digester.setAlgorithm("SHA-1");   
		digester.setIterations(50000);  
		
		try{
			String selectUsuarioSQL = "SELECT * FROM usuario WHERE 1 = 1" +
				" AND estado = 'ACT'" +
				" AND nombre_usuario = ? ";
			
			Usuario usuario = new Usuario(); 
			preparedStatementSelect = connection.prepareStatement(selectUsuarioSQL);
			preparedStatementSelect.setString(1, txtUsuario.getText() );
			ResultSet rs = preparedStatementSelect.executeQuery();
			
			while (rs.next()) {
				
				usuario.setId( rs.getInt("id") );
				usuario.setNombreUsuario( rs.getString("nombre_usuario") );
				usuario.setNombreCompleto( rs.getString("nombre_completo") );
				usuario.setSucursal(rs.getInt("sucursal_id"));
				usuario.setPass( rs.getString("pass") );
				break;
			}
			
			if (digester.matches(txtContrasena.getText(), usuario.getPass())) {
				//System.out.println("Password valido");
				UserSession.setUsr(usuario);
				
				/*//Obtenemos la lista de cajas registradoras registradas 
				String selectCajaSQL = "SELECT * FROM caja_registradora WHERE 1 = 1" +
						" AND sucursal_id = ? AND estado <> 'INA' ";
				preparedStatementSelect = connection.prepareStatement(selectCajaSQL);
				preparedStatementSelect.setInt(1, usuario.getSucursal() );
				rs = preparedStatementSelect.executeQuery();
				List<CajaRegistradora> cajitas = new ArrayList<CajaRegistradora>();
				
				while (rs.next()) {
					CajaRegistradora nuevaCaja = new CajaRegistradora();
					nuevaCaja.setId( rs.getInt("id") );
					nuevaCaja.setEstado( rs.getString("estado") );
					nuevaCaja.setNumero( rs.getShort("numero") );
					nuevaCaja.setNumResolucion( rs.getString("num_resolucion") );
					nuevaCaja.setFechaResolucion( rs.getDate("fecha_resolucion") );
					nuevaCaja.setSucursalId( rs.getInt("sucursal_id") );
					
					//Agregamos la caja registradora creada
					cajitas.add( nuevaCaja );
					
					//break;
				}
					
				List<ComboItem> cajitasItem = new ArrayList<ComboItem>();
				CajaRegistradora cajitaFt = new CajaRegistradora();
				cajitaFt.setId(0);
				cajitasItem.add( new ComboItem(cajitaFt, "Seleccione") );
				
				if( cajitas != null && cajitas.size() > 0 ){
					//Si existe la caja registradora se le pide al usuario que escoja la caja
					for( CajaRegistradora cajaRegistradora : cajitas ){
						cajitasItem.add( new ComboItem(cajaRegistradora, cajaRegistradora.getNumero().toString()) );
					}
				}
				
				CajaRegistradora aux = new CajaRegistradora();
				aux.setId(-1);
				cajitasItem.add(new ComboItem(aux, "Todas del usuario"));
				aux = new CajaRegistradora();
				aux.setId(-2);
				cajitasItem.add(new ComboItem(aux, "Todas de todos"));
				
				if( cajitasItem != null && cajitasItem.size() > 0 ){
					//Si hay lista de cajar registradoras creamos la interfaz para 
					SelectCaja selectCaja = new SelectCaja( cajitasItem );
					this.dispose();
				}*/
				
				Main main = new Main();
				main.setSize(500, 400);
				main.setVisible(true);
				main.iniciarImpresion();
				this.dispose();
				
				return true;
			}else{
				System.out.println("Password Invalido");
				JOptionPane.showMessageDialog(new JFrame(), 
						"Sus Credenciales son incorrectas", "Error", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
		
		return false;
	}
	 
	public void crearIcono(){
		//check to see if system tray is supported on OS.
        if (SystemTray.isSupported()) {
        	sysTray = SystemTray.getSystemTray();
            //create dog image
            dogImage  = Toolkit.getDefaultToolkit().getImage(RUTA_IMG + "printericon.png");
            //create popupmenu
            menu = new PopupMenu();
            //create item
            item1 = new MenuItem("Salir");
            //add item to menu
            menu.add(item1);
            //add action listener to the item in the popup menu
            item1.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   System.exit(0);
               }
            });
            
            item2 = new MenuItem("Mostrar Aplicacion");
			//add actionListener to second menu item
			item2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//frame.setVisible(true);
				}
			});

			//add second item to popup menu
	        menu.add(item2);
            //create system tray icon.
            trayIcon = new TrayIcon(dogImage, "Print App.", menu);
            //add the tray icon to the system tray.
            try {
                sysTray.add(trayIcon);
            } catch(AWTException e) {
               System.out.println(e.getMessage());
            }
        }		
	}
	
	public static void main(String[] args) {
		//cargamos las propiedades
		PropertiesLoader.loadProperties();
		BasicConfigurator.configure();
		//cargamos las letras en el ambiente gráfico
		PrinterUtils.loadFonts("config\\fonts");
		StartPoint startPoint = new StartPoint();
		//startPoint.setSize(400, 300);
		startPoint.setVisible(true);
	}
}
