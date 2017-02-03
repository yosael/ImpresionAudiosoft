package com.saplic.print.util;

import java.io.IOException;

public interface Printer
{
    /**
     * init: inicializa el dispositivo
     * @param device envia al puerto donde se encuentra la TM
     * @throws IOException
     */
    public void init(String device) throws IOException;

    /**
     * write : escribe la cadena en el dispositivo
     * @param line cadena a escribir
     */
    public void write(String line);

    /**
     * close: cierra el dispositivo
     */
    public void close();

    /**
     * setCommand : Envia un comando al dispositivo
     * @param command comando a enviar al dispositivo
     */
    public void setCommand(char[] command);
}
