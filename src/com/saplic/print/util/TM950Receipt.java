package com.saplic.print.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TM950Receipt implements Printer {

    private String device;

    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter pw;

    @Override
    public void init(String device) throws IOException {
        fw = new FileWriter(device);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
    }

    @Override
    public void write(String linea) {
            pw.println(linea);
    }

    @Override
    public void close() {
        pw.close();
    }

    @Override
    public void setCommand(char[] comando) {
       pw.write(comando);
    }
}
