package com.teclever.dfcc.datastore.filemanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.RunPathMaster;
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.VDDDto;
import com.teclever.dfcc.datastore.dto.VDDResponse;

public class ValidateChecksum {

	public Response validate() {
		Response response = new Response();
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			Map<String, Set<RunPathMaster>> map = runPathMasterService.getAllRunPathMaster();
			if (map == null) {
				response.setResponseCode(0);
				response.setResponseMessage("Run Config is Empty ");
				return response;
			}
			Set<String> setOfTestPlanFile = new HashSet<>();
			Set<String> setOfSymbols = new HashSet<>();
			Set<String> setOfMacro = new HashSet<>();
			Set<String> setOfDownloadFile = new HashSet<>();

			String tpfFilePath = "";
			String symboleFilePath = "";
			String macrosFilePath = "";
			String downloadFilePath = "";
			for (Map.Entry<String, Set<RunPathMaster>> mapData : map.entrySet()) {
				for (RunPathMaster runPathMaster : mapData.getValue()) {
					switch (runPathMaster.getMasterPath()) {
					case "tpf":
						List<TestFileDto> listOfTestPlanFile = TestPlanFileManagement
								.getAllTestFilesByRunPathMasterId(runPathMaster.getRunPathMasterId());
						for (TestFileDto testFileDto : listOfTestPlanFile) {
							setOfTestPlanFile.add(testFileDto.getTestFileName());
						}
						tpfFilePath = runPathMaster.getLocation();
						break;

					case "symbols":
						List<SymbolDto> listOfSymbols = SymbolFileManagement
								.getAllSymbolsByRunPathMasterId(runPathMaster.getRunPathMasterId());

						for (SymbolDto symbolDto : listOfSymbols) {
							setOfSymbols.add(symbolDto.getFileName());
						}
						symboleFilePath = runPathMaster.getLocation();
						break;
					case "macros":
						List<MacroDto> macroDto = MacroFileManagement
								.getAllMacrobyRunPathMassterId(runPathMaster.getRunPathMasterId());
						for (MacroDto macro : macroDto) {
							setOfMacro.add(macro.getFileName());
						}
						macrosFilePath = runPathMaster.getLocation();
						break;
					case "download":
						List<DownloadFileDto> listOfDownloadFile = DownloadFileManagement
								.getAllDownloadFilesByRunPathId(runPathMaster.getRunPathMasterId());
						for (DownloadFileDto macro : listOfDownloadFile) {
							setOfDownloadFile.add(macro.getDownloadFileName());
						}
						downloadFilePath = runPathMaster.getLocation();
						break;

					default:
						break;
					}
				}
			}
			VDDManagement vddManagement = new VDDManagement();
			VDDResponse vddResponse = vddManagement.getListOfVDD();
			List<VDDDto> listOfVdd = vddResponse.getvDDList();
//			System.out.println(listOfVdd.size()+" "+(setOfTestPlanFile.size()+ setOfSymbols.size() + setOfMacro.size() +setOfDownloadFile.size())+"="+setOfTestPlanFile.size()+ "+" + setOfSymbols.size() + "+" + setOfMacro.size() + "+"
//					+ setOfDownloadFile.size());
//			if (listOfVdd.size() != (setOfTestPlanFile.size() + setOfSymbols.size() + setOfMacro.size()
//					+ setOfDownloadFile.size())) {
//				response.setResponseCode(0);
//				response.setResponseMessage("File Count Mist Matching ");
//				return response;
//			}
			Map<String, String> temparyMap = new HashMap<>();
			temparyMap.putAll(SystemConfigManagement.calculateChecksums(tpfFilePath));
			temparyMap.putAll(SystemConfigManagement.calculateChecksums(symboleFilePath));
			temparyMap.putAll(SystemConfigManagement.calculateChecksums(macrosFilePath));
			temparyMap.putAll(SystemConfigManagement.calculateChecksums(downloadFilePath));
			for (VDDDto vddDto : listOfVdd) {
				if ((temparyMap.get(vddDto.getFileName()) != null)) {
					System.out.println(vddDto.getFileCheckSum());
					if (!(temparyMap.get(vddDto.getFilePath() + vddDto.getFileName())
							.equals(vddDto.getFileCheckSum()))) {
						response.setResponseCode(0);
						response.setResponseMessage("CheckSum is MisMatch ");
						return response;
					}

				} else {
					response.setResponseCode(0);
					response.setResponseMessage("File Name misMatch ");
					return response;
				}
			}
		} catch (Exception e) {
			response.setResponseCode(0);
			response.setResponseMessage("");
			e.printStackTrace();

		}
		response.setResponseCode(1);
		response.setResponseMessage("Validate Succesfull ");
		return response;
	}

}
