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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.saplic.print.entities.CajaRegistradora;
import com.saplic.print.entities.UserSession;
import com.saplic.print.util.ComboItem;

public class SelectCaja extends JFrame {
	private JPanel panel;
	private JComboBox comboBox;
	private JLabel lblCajaRegistradora;
	private JButton btnSeleccionarCaja;
	
	private Image dogImage;
    private SystemTray sysTray;
    private PopupMenu menu;
    private MenuItem item1;
    private MenuItem item2;
    
	//For icon app
	TrayIcon trayIcon;
	SystemTray tray;
	
	private static final String RUTA_IMG = "./printPOS_lib/";
	
	public SelectCaja( List<ComboItem> cajitas ) {
		super( "Seleccion de la Caja Registradora" );
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(10, 24, 422, 104);
		panel.setBorder(BorderFactory.createTitledBorder("Seleccione la Caja Registradora a Utilizar"));
		panel.setLayout(null);
		getContentPane().add(panel);
		this.setResizable(false);
		
		comboBox = new JComboBox( cajitas.toArray() );
		comboBox.setBounds(128, 27, 163, 22);
		panel.add(comboBox);
		
		lblCajaRegistradora = new JLabel("Caja Registradora:");
		lblCajaRegistradora.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblCajaRegistradora.setBounds(10, 31, 123, 14);
		panel.add(lblCajaRegistradora);
		
		btnSeleccionarCaja = new JButton("Seleccionar Caja");
		btnSeleccionarCaja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				crearInterfaz();
			}
		});
		btnSeleccionarCaja.setBounds(128, 60, 163, 23);
		panel.add(btnSeleccionarCaja);
		
		this.setSize(450, 192);
		this.setVisible(true);
		
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
		setIconImage(Toolkit.getDefaultToolkit().getImage("./printericon.png"));
        //End for icon app
        
        addWindowListener(
			new WindowAdapter(){
				public void windowClosing( WindowEvent event )
				{
					dispose();
					System.exit(NORMAL);
				}
			}
		);
        
        Action doLogin = new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
		    	crearInterfaz();
		    }
		};
		
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "doLogin");
		panel.getActionMap().put("doLogin", doLogin);
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
	
	public void crearInterfaz(){
		CajaRegistradora cajaRegistradora = new CajaRegistradora();
		cajaRegistradora = (CajaRegistradora) (((ComboItem) comboBox.getSelectedItem()).getValue());
		
		if( cajaRegistradora.getId() != null ){
			if( cajaRegistradora.getId() < -2 || cajaRegistradora.getId() == 0){
				JOptionPane.showMessageDialog(new JFrame(), 
						"Debe Seleccionar la Caja Registradora:", 
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			UserSession.getUsr().setCajaRegistradora(cajaRegistradora);
			UserSession.getUsr().setCaja( cajaRegistradora.getId() );
			
			//Creamos la interfaz para controlar las impresiones
			Main main = new Main();
			main.setSize(500, 400);
			main.setVisible(true);
			main.iniciarImpresion();
			this.dispose();	
		}else{
			JOptionPane.showMessageDialog(new JFrame(), 
					"Debe Seleccionar la Caja Registradora:", 
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}	
	}
}
