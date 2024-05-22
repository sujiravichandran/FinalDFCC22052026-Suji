package com.teclever.dfcc.datastore.dto;

public class AitessDriverDto {
	
	
    private String aitessName;
    private String driverName;

    public AitessDriverDto(String aitessName, String driverName) {
        this.aitessName = aitessName;
        this.driverName = driverName;
    }

    public String getAitessName() {
        return aitessName;
    }

    public void setAitessName(String aitessName) {
        this.aitessName = aitessName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
