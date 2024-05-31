package com.teclever.dfcc.datastore.processcontrolmanagement;

public class ProcessControlManagement {
	
	
	
	//loadRunConfiguration()
	public void loadDriver(String command)
	{
        LoadDriverProcessControl loadDriverProcessControl = LoadDriverProcessControl.getInstance();
		loadDriverProcessControl.launchLoadDriver(command);
	}
	
	
	
	

	

}
