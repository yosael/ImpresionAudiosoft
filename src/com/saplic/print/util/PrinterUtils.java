package com.saplic.print.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class PrinterUtils {
	static int w, h; //tamaño de la imagen original en caso de imprimir imagenes
	static BufferedImage img = null; //archivo que mantendra en memoria la imagen original
	
	/*
	 * Devuelve un formato de pagina con los margenes mínimos para el trabajo solicitado
	 */
	static public PageFormat getMinimumMarginPageFormat(PrinterJob printJob) {
	    PageFormat pf0 = printJob.defaultPage();
	    PageFormat pf1 = (PageFormat) pf0.clone();
	    Paper p = pf0.getPaper();
	    p.setImageableArea(0, 0,pf0.getWidth(), pf0.getHeight());
	    pf1.setPaper(p);
	    PageFormat pf2 = printJob.validatePage(pf1);
	    return pf2;     
	}
	
	/*
	 * Toma el formato ya con los valores de campos reemplazados
	 * y prepara la impresion de texto segun los formatos especificados
	 * de la forma:
	 * <|Familia:Estilo:Tamaño:Espacio entre lineas|>
	 */
	static public Printable createPrintable(final String text){
		return new Printable() {
			public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
				Graphics2D graphics2D = (Graphics2D)graphics;
				graphics2D.translate(pageFormat.getImageableX(),
				pageFormat.getImageableY());
				Font font = new Font(PropertiesLoader.appProperties.getProperty("font.family.default"), ("BOLD".equals(PropertiesLoader.appProperties.getProperty("font.style.default")) ? Font.BOLD : ("ITALIC".equals(PropertiesLoader.appProperties.getProperty("font.style.default")) ? Font.ITALIC : Font.PLAIN)), Integer.valueOf(PropertiesLoader.appProperties.getProperty("font.size.default"))); //fuente por defecto
				graphics2D.setFont(font);
				int pageWidth = graphics2D.getClipBounds().width;
				FontMetrics fontMetrics = graphics2D.getFontMetrics(font);
				int position = fontMetrics.getHeight();
				int height = Math.round(fontMetrics.getAscent() * Float.parseFloat(PropertiesLoader.appProperties.getProperty("font.lineheight.default")));
				Scanner s = new Scanner(text);
				while (s.hasNextLine()) {
					String line = s.nextLine();
					//verificamos si hay un tag de imagen. Es importante notar que estos tags no usan espacio del area de impresion, debe ponerse la posicion
					//para setearlo correctamente, si una linea solo tine tags de imagenes no se agregara la línea en blanco.
					if (line.contains("«|") && line.contains("|»")){
						List<String> coincidencias = new ArrayList<String>();
						Matcher m = Pattern.compile("\\«\\|.+?:.*?\\|\\»").matcher(line);
						while (m.find()){
							coincidencias.add(m.group());
						}
						for (String coinci : coincidencias){
							String[] pedTemp = coinci.replace("«|", "").replace("|»", "").split(":");
							try {
								img = ImageIO.read(new File(pedTemp[0]));
								w = img.getWidth(null); //obtiene el ancho
						        h = img.getHeight(null); //obtiene el alto
						        graphics2D.drawImage(img, Integer.valueOf(pedTemp[1]), Integer.valueOf(pedTemp[2]), Integer.valueOf(pedTemp[3]), Integer.valueOf(pedTemp[4]), 0, 0, w, h, null);
							} catch (IOException e) {
								//no se logra obtener la imagen simplemente no se imprimira
								e.printStackTrace();
							}
						}
						line = line.replaceAll("\\«\\|.+?:.*?\\|\\»","");
						if ("".equals(line))//si tras reemplazar todas las imagenes la linea esta vacia pasamos a la siguiente iteración
							continue;
					}
					if (line.contains("<|") && line.contains("|>")){ //contine una tag para setear el tipo de letra
						List<String> coincidencias = new ArrayList<String>();
						Matcher m = Pattern.compile("\\<\\|.+?:.*?\\|\\>").matcher(line);
						while (m.find()){
							coincidencias.add(m.group());
						}
						for (String coinci : coincidencias){
							String[] pedTemp = coinci.replace("<|", "").replace("|>", "").split(":");
							font = new Font(pedTemp[0],("BOLD".equals(pedTemp[1]) ? Font.BOLD : ("ITALIC".equals(pedTemp[1]) ? Font.ITALIC : Font.PLAIN)), Integer.valueOf(pedTemp[2]));
							graphics2D.setFont(font);
							fontMetrics = graphics2D.getFontMetrics(font);
							height = Math.round(fontMetrics.getAscent() * Float.parseFloat(pedTemp[3]));
						}
						line = line.replaceAll("\\<\\|.+?:.*?\\|\\>","");
					}
					int lineWidth = fontMetrics.stringWidth(line);
					while (lineWidth > pageWidth) {
						String lineCopy = line;
						String firstPart = "";
						while (lineWidth > pageWidth) {
							int index = lineCopy.lastIndexOf(' ');
							firstPart = lineCopy.substring(0, index);
							lineWidth =	fontMetrics.stringWidth(firstPart);
							lineCopy = firstPart;
						}
						position += height;
						graphics2D.drawString(firstPart, 0, position);
						line = line.substring(firstPart.length(), line.length()).trim();
						lineWidth = fontMetrics.stringWidth(line);
					}
					position += height;
					graphics2D.drawString(line, 0, position);
				}
				if (s != null){
					s.close();
				}
				return (pageIndex == 0 ? PAGE_EXISTS : NO_SUCH_PAGE);
			}
		};
	}
	
	/*
	 * Funcion que carga las fuentes de la carpeta config fonts al ambiente gráfico para poder utilzarlas
	 */
	static public void loadFonts(String path){
		try {	
			//registramos todas las fuentes de la carpeta fonts en el ambiente gráfico para poder utilizarlas
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			File[] fonts = new File(path).listFiles();
			for (File font : fonts){
				if (font.isFile()){
					//si es una archivo verificamos la extension para determinar si es una letra letra ttf o otf, de ser asi la agregamos.
					if (".ttf".equals(font.getName().substring(font.getName().lastIndexOf("."))) || ".otf".equals(font.getName().substring(font.getName().lastIndexOf(".")))){
						ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(font.getPath())));
					}
				} else if (font.isDirectory()){
					//si es un directorio pasamos a revisar su interior para ver si hay tipos de letras a agregar.
					loadFonts(font.getPath());
				}
			} 
	    } catch (Exception e) {
		     //Handle exception
			e.printStackTrace();
		}
	}

}
