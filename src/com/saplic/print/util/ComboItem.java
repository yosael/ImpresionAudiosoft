package com.saplic.print.util;

public class ComboItem {
	private Object value;
    private String label;

    public ComboItem(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }

	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof ComboItem) {
			if(((ComboItem)obj).getValue().equals(this.getValue()))
				return true;
		}
			
		return false;
	}
    
    
}
