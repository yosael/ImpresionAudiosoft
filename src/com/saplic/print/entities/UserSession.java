package com.saplic.print.entities;

public class UserSession {
	private static Usuario usr;
	
    static {}
    
    public static void setUsr(Usuario tmpUsr) {
    	usr = tmpUsr;
    }
    
    public static Usuario getUsr() {
    	return usr;
    }
}
