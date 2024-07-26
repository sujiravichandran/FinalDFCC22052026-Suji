package com.teclever.dfcc.datastore.customtestmanagement;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.service.RunConfigurationService;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.filemanagement.MacroFileManagement;

public class AdvanceTestingManagement {

	public List<MacroDto> getAllMacrosForAdavanceTest(String uutTypeId, String testTypeId) {
		List<MacroDto> macros = new ArrayList<MacroDto>();
		try {
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutTypeId, testTypeId);
			macros = MacroFileManagement.getAllMacros(runConfigId);

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}

		return macros;
	}


	public List<String> getAllMacroNames(String uutTypeId, String testTypeId)
	{
		List<String> macroNames = new ArrayList<String>();
		try
		{
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			String runConfigId = runConfigurationService.getRunConfigIdByUutIdAndTestTypeId(uutTypeId, testTypeId);
			List<MacroDto>macros = MacroFileManagement.getAllMacros(runConfigId);

			for (MacroDto m : macros) {
				macroNames.add(m.getMacroName());
			}

		}catch(Exception ex)
		{
			System.out.println(ex.getLocalizedMessage());
		}
		return macroNames;

	}

}