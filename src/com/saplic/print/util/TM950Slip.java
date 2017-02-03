package com.saplic.print.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TM950Slip implements Printer {

    private String device;

    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter pw;

    @Override
    public void init(String device) throws IOException {
        fw = new FileWriter(device);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);

        // Slip printer command
        setCommand(new char[]{0x1B,'c','0',4});
    }

    @Override
    public void write(String line) {
        pw.println(line);
    }

    @Override
    public void close() {
        pw.close();
    }

    @Override
    public void setCommand(char[] command) {
        pw.write(command);
    }
}
