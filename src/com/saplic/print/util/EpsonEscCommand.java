package com.saplic.print.util;

public class EpsonEscCommand {

	public static final char ESC = 27; //escape
    public static final char AT = 64; //@
    public static final char LINE_FEED = 10; //line feed/new line
    public static final char PARENTHESIS_LEFT = 40;
    public static final char BACKSLASH = 92;
    public static final char CR = 13; //carriage return
    public static final char TAB = 9; //horizontal tab
    public static final char FF = 12; //form feed expulsar hoja
    public static final char g = 103; //15cpi pitch
    public static final char p = 112; //used for choosing proportional mode or fixed-pitch
    public static final char t = 116; //used for character set assignment/selection
    public static final char l = 108; //used for setting left margin
    public static final char x = 120; //used for setting draft or letter quality (LQ) printing
    public static final char E = 69; //bold font on
    public static final char F = 70; //bold font off
    public static final char J = 74; //used for advancing paper vertically
    public static final char P = 80; //10cpi pitch
    public static final char Q = 81; //used for setting right margin
    public static final char $ = 36; //used for absolute horizontal positioning
    public static final char ARGUMENT_0 = 0;
    public static final char ARGUMENT_1 = 1;
    public static final char ARGUMENT_2 = 2;
    public static final char ARGUMENT_3 = 3;
    public static final char ARGUMENT_4 = 4;
    public static final char ARGUMENT_5 = 5;
    public static final char ARGUMENT_6 = 6;
    public static final char ARGUMENT_7 = 7;
    public static final char ARGUMENT_25 = 25;
    public static final char[] CUTPAPER = new char[]{0x1d,'V',1};
    
}
