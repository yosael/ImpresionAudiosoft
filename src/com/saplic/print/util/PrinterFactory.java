package com.saplic.print.util;

public class PrinterFactory {

    public static Printer getPrinterSlip()
    {
        return new TM950Slip();
    }

    public static Printer getPrinterReceipt()
    {
        return new TM950Receipt();
    }

}
