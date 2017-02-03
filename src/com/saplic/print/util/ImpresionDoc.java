package com.saplic.print.util;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class ImpresionDoc {
	
	public ImpresionDoc(String FILE){
		
	    //Le decimos el tipo de datos que vamos a enviar a la impresora
	    //Tipo: bytes Subtipo: autodetectado
	    DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
	    
	  //Cogemos el servicio de impresión por defecto (impresora por defecto)
	    //PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		
	    //Creamos un trabajo de impresión
	    if (services.length > 0){
	    	PrintRequestAttributeSet attSet = new HashPrintRequestAttributeSet();
	    	PrintService service = ServiceUI.printDialog(null, 200, 200, services, defaultService, flavor, attSet);
	    	
	    	if (service != null){
	    		service = PrintServiceLookup.lookupDefaultPrintService();
	    		DocPrintJob pj = service.createPrintJob();
			    //Nuestro trabajo de impresión envía una cadena de texto
			    String ss = new String(FILE);
			    byte[] bytes;
			    //Transformamos el texto a bytes que es lo que soporta la impresora
			    //bytes= ss.getBytes();
			    bytes= ss.getBytes();
			    //Creamos un documento (Como si fuese una hoja de Word para imprimir)
			    Doc doc=new SimpleDoc( bytes,flavor,null );
			    //Obligado coger la excepción PrintException
			    
			    try {
			      //Mandamos a imprimir el documento
			      pj.print(doc, null);
			    }
			    catch (PrintException e) {
			      System.out.println("Error al imprimir: "+e.getMessage());
			    }
	    	}
	    }
	}
}
