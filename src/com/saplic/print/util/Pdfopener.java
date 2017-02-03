package com.saplic.print.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

public class Pdfopener {
	
	boolean printTicket = false;
	boolean withDialog = false;
	
	public void helpview(String FILE, String reportName) throws URISyntaxException, FileNotFoundException {
		
		String filepath = FILE; 
		
		SwingController controller = new SwingController();
		
		// Build a SwingViewFactory configured with the controller
		SwingViewBuilder factory = new SwingViewBuilder(controller);
		JPanel viewerComponentPanel = factory.buildViewerPanel();
		
		// add copy keyboard command
		ComponentKeyBinding.install(controller, viewerComponentPanel);
		
		// add interactive mouse link annotation support via callback
		controller.getDocumentViewController().setAnnotationCallback(
				new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));
		
				// Use the factory to build a JPanel that is pre-configured
				//with a complete, active Viewer UI.
				// Create a JFrame to display the panel in
				JFrame window = new JFrame(reportName);
				window.getContentPane().add(viewerComponentPanel);
				window.pack();
				window.setVisible(false);
				
				// Open a PDF document to view
				controller.openDocument(filepath);
				
				if( printTicket ){
					controller.print( withDialog );
				}
	}

	public boolean isPrintTicket() {
		return printTicket;
	}

	public void setPrintTicket(boolean printTicket) {
		this.printTicket = printTicket;
	}

	public boolean isWithDialog() {
		return withDialog;
	}

	public void setWithDialog(boolean withDialog) {
		this.withDialog = withDialog;
	}
}
