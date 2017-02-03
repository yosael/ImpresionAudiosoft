package com.saplic.print.gui;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.saplic.print.entities.UserSession;
import com.saplic.print.util.ConnectionManager;
import com.saplic.print.util.PrinterUtils;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton btnInitProcess;
	private JButton btnStopProcess;
	private Scheduler scheduler;
	private JobKey keyJob;
	private JPanel panel;
	
	//For icon app
	TrayIcon trayIcon;
    SystemTray tray;
    private JTextArea textArea;
    private JLabel lblConsolaDeErrores;
	
	public Main(){
		super( "Impresion de Documentos" );
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(28, 11, 389, 65);
		panel.setBorder(BorderFactory.createTitledBorder("Habilitar/Deshabilitar Impresion"));
		panel.setLayout(null);
		getContentPane().add(panel);
		this.setResizable(false);
		
		btnInitProcess = new JButton("Iniciar Proceso");
		btnInitProcess.setBounds(10, 27, 121, 23);
		panel.add(btnInitProcess);
		
		btnStopProcess = new JButton("Finalizar Proceso");
		btnStopProcess.setBounds(141, 27, 136, 23);
		panel.add(btnStopProcess);
		btnStopProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				detenerImpresion();
			}
		});
		btnStopProcess.setEnabled(false);
		btnInitProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				iniciarImpresion();
			}
		});
		
		textArea = new JTextArea();
		textArea.setRows(9);
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setBounds(28, 112, 389, 137);
		scroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		
		getContentPane().add(scroll);
		//getContentPane().add(textArea);
		ConnectionManager.setTextArea(textArea);
		
		lblConsolaDeErrores = new JLabel("Consola de Errores");
		lblConsolaDeErrores.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblConsolaDeErrores.setBounds(28, 87, 125, 14);
		getContentPane().add(lblConsolaDeErrores);
		
		System.out.println("Usuario: " + UserSession.getUsr().getNombreUsuario());
		 
		addWindowListener(
			new WindowAdapter(){
				public void windowClosing( WindowEvent event )
				{
					LabelFrame labelFrame = new LabelFrame();
					labelFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
					labelFrame.setResizable(false);
					labelFrame.setBounds(100, 150, 505, 300);
					labelFrame.setSize( 505, 250 );
					labelFrame.setVisible( true );
					
					detenerImpresion();
					dispose();
				}
			}
		);
		
		//For Icon app
		if(SystemTray.isSupported()){
            tray=SystemTray.getSystemTray();

            Image image=Toolkit.getDefaultToolkit().getImage("./printericon.png");
            ActionListener exitListener=new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Cerrando....");
                    System.exit(0);
                }
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Salir");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem=new MenuItem("Abrir");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
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
			            System.out.println("Agregando icono");
		            }catch(AWTException ex){
		            	System.out.println("No se pudo agregar el icono");
		            }
		        }
		        if(e.getNewState()==MAXIMIZED_BOTH){
                    tray.remove(trayIcon);
                    setVisible(true);
                    System.out.println("Icono removido");
                }
                if(e.getNewState()==NORMAL){
                    tray.remove(trayIcon);
                    setVisible(true);
                    System.out.println("Icono removido");
                }
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage("./printericon.png"));
        //End for icon app
	}
	
	public void iniciarImpresion(){
		//Proceso a ejecutar
		JobDetail job = JobBuilder.newJob(Print.class).withIdentity("printJobName", "group1").build();
		keyJob = job.getKey();
		
		//Disparador del Proceso cada 3 segundos
		Trigger trigger = TriggerBuilder
			.newTrigger()
			.withIdentity("triggerName", "group1")
			.withSchedule( SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).repeatForever() )
			.build();

		//Calendarizando el proceso
		try{
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
			
			this.btnInitProcess.setEnabled(false);
			this.btnStopProcess.setEnabled(true);
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
	
	public void detenerImpresion(){
		try {
			scheduler.interrupt(keyJob);
			//scheduler.deleteJob(keyJob);
			scheduler.shutdown();
			
			this.btnInitProcess.setEnabled(true);
			this.btnStopProcess.setEnabled(false);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public class LabelFrame extends JFrame{
		public JLabel label1;
		public JLabel label2;
		public JLabel label3;
		private JButton btnCancelar;
		private JButton btnAbrirDia;
		
		public LabelFrame(){
			super( "Confirmacion de Cierre de Aplicacion de Impresion de Comprobantes" );
			getContentPane().setLayout(null);
			
			JLabel label1 = new JLabel( "Esta seguro que quiere cerrar la aplicación de impresión?" );
			label1.setBounds(71, 22, 360, 14);
			getContentPane().add( label1 );
			
			JLabel label2 = new JLabel( new Date().toString() );
			label2.setBounds(144, 53, 171, 14);
			label2.setFont(new Font("Tahoma", Font.BOLD, 11));
			getContentPane().add( label2 );
			
			JLabel label3 = new JLabel( "Si lo hace, no podra imprimir facturas, creditos fiscales" );
			label3.setHorizontalAlignment(SwingConstants.CENTER);
			label3.setBounds(43, 90, 400, 14);
			getContentPane().add( label3 );
			
			JLabel label4 = new JLabel( "o recibos de caja." );
			label4.setHorizontalAlignment(SwingConstants.CENTER);
			label4.setBounds(43, 110, 400, 14);
			getContentPane().add( label4 );
			
			btnCancelar = new JButton("Cancelar");
			btnCancelar.setBounds(164, 150, 91, 23);
			getContentPane().add(btnCancelar);
			
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Main main = new Main();
					main.setSize(500, 400);
					main.setVisible(true);
					main.iniciarImpresion();
					dispose();
				}
			});
			 
			btnAbrirDia = new JButton("Si");
			btnAbrirDia.setBounds(265, 150, 50, 23);
			getContentPane().add(btnAbrirDia);
			
			btnAbrirDia.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ConnectionManager.cerrarConexion();
					detenerImpresion();
					System.exit( 0 );
				}
			});
		}
	}
}
