package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.RunPathMaster;
import com.teclever.datastore.service.RunPathMasterService;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.DownloadFileDto;
import com.teclever.dfcc.datastore.dto.MacroDto;
import com.teclever.dfcc.datastore.dto.SymbolDto;
import com.teclever.dfcc.datastore.dto.TestFileDto;
import com.teclever.dfcc.datastore.dto.VDDDto;
import com.teclever.dfcc.datastore.dto.VDDResponse;
import com.teclever.dfcc.datastore.dto.ValidateResponse;

public class ValidateChecksum {
	public ValidateResponse validate() {
		ValidateResponse validateResponse= new ValidateResponse();
		Response response = new Response();
		try {
			RunPathMasterService runPathMasterService = new RunPathMasterService();
			Map<String, Set<RunPathMaster>> map = runPathMasterService.getAllRunPathMaster();
			if (map == null) {
				response.setResponseCode(0);
				response.setResponseMessage("Run Config is Empty ");
				validateResponse.setResponse(response);
				return validateResponse;
			}
			Set<String> setOfTestPlanFile = new HashSet<>();
			Set<String> setOfSymbols = new HashSet<>();
			Set<String> setOfMacro = new HashSet<>();
			Set<String> setOfDownloadFile = new HashSet<>();

			for (Map.Entry<String, Set<RunPathMaster>> mapData : map.entrySet()) {
				for (RunPathMaster runPathMaster : mapData.getValue()) {
					switch (runPathMaster.getMasterPath()) {
					case "tpf":
						List<TestFileDto> listOfTestPlanFile = TestPlanFileManagement
								.getAllTestFilesByRunPathMasterId(runPathMaster.getRunPathMasterId());
						for (TestFileDto testFileDto : listOfTestPlanFile) {
							setOfTestPlanFile.add(testFileDto.getTestFileName());
						}
						break;

					case "symbols":
						List<SymbolDto> listOfSymbols = SymbolFileManagement
								.getAllSymbolsByRunPathMasterId(runPathMaster.getRunPathMasterId());

						for (SymbolDto symbolDto : listOfSymbols) {
							setOfSymbols.add(symbolDto.getFileName());
						}
						break;
					case "macros":
						List<MacroDto> macroDto = MacroFileManagement
								.getAllMacrobyRunPathMassterId(runPathMaster.getRunPathMasterId());
						for (MacroDto macro : macroDto) {
							setOfMacro.add(macro.getFileName());
						}
						break;
					case "download":
						List<DownloadFileDto> listOfDownloadFile = DownloadFileManagement
								.getAllDownloadFilesByRunPathId(runPathMaster.getRunPathMasterId());
						for (DownloadFileDto macro : listOfDownloadFile) {
							setOfDownloadFile.add(macro.getDownloadFileName());
						}
						break;

					default:
						break;
					}
				}
			}
			VDDManagement vddManagement = new VDDManagement();
			VDDResponse vddResponse = vddManagement.getListOfVDD();
			List<VDDDto> listOfVdd = vddResponse.getvDDList();
//			}
			Map<String, String> vddMap = new HashMap<>();

			for (VDDDto vddDto : listOfVdd) {
				vddMap.put(vddDto.getFilePath()+vddDto.getFileName(), vddDto.getFileCheckSum());
				System.out.println(vddDto.getFilePath()+vddDto.getFileName());
			}
			List<CheckSum> listOfCheckSum = new ArrayList<>();
			
			for(String testPlanFile:setOfTestPlanFile) {
				CheckSum checkSum = new CheckSum();
				if(vddMap.get(testPlanFile)==null) {
					checkSum.setFile(testPlanFile);
					checkSum.setMsg("No VDD File ");
					listOfCheckSum.add(checkSum);
					continue;
				}
				
				checkSum=validateChecksum(testPlanFile,vddMap.get(testPlanFile));
				listOfCheckSum.add(checkSum);
			}
			for(String symbolFile:setOfSymbols) {
				CheckSum checkSum = new CheckSum();
				if(vddMap.get(symbolFile)==null) {
					checkSum.setFile(symbolFile);
					checkSum.setMsg("No VDD File ");
					listOfCheckSum.add(checkSum);
					continue;
				}
				checkSum=validateChecksum(symbolFile,vddMap.get(symbolFile));
				listOfCheckSum.add(checkSum);
			}
			for(String macroFile:setOfMacro) {
				CheckSum checkSum = new CheckSum();
				if(vddMap.get(macroFile)==null) {
					checkSum.setFile(macroFile);
					checkSum.setMsg("No VDD File ");
					listOfCheckSum.add(checkSum);
					continue;
				}
				checkSum=validateChecksum(macroFile,vddMap.get(macroFile));
				listOfCheckSum.add(checkSum);
			}
			for(String downloadFile:setOfDownloadFile) {
				CheckSum checkSum = new CheckSum();
				if(vddMap.get(downloadFile)==null) {
					checkSum.setFile(downloadFile);
					checkSum.setMsg("No VDD File ");
					listOfCheckSum.add(checkSum);
					continue;
				}
				checkSum=validateChecksum(downloadFile,vddMap.get(downloadFile));
				listOfCheckSum.add(checkSum);
			}
			response.setResponseCode(1);
			response.setResponseMessage("Validate Succesfull ");
			validateResponse.setCheckSumList(listOfCheckSum);
			validateResponse.setResponse(response);
		} catch (Exception e) {
			response.setResponseCode(0);
			response.setResponseMessage("");
			validateResponse.setResponse(response);
			
			e.printStackTrace();

		}
		return validateResponse;
	}
	
	
	private CheckSum validateChecksum(String fullFileName, String fileCheckSum) {
		CheckSum responseCheckSum = new CheckSum();
		String fileCalcCheckSum;
		try {
			fileCalcCheckSum = getFileChecksum(new File(fullFileName));
			responseCheckSum.setFile(fullFileName);
			File file = new File(fullFileName);
			if (!file.exists()) {
				responseCheckSum.setChecksumValue("");
				responseCheckSum.setMsg("NOT OK");
				System.out.println("FileName NOT OK "+fullFileName + "::::::" );

			} else if(fileCheckSum.equals(fileCalcCheckSum)) {
				responseCheckSum.setChecksumValue(fileCalcCheckSum);
				responseCheckSum.setMsg("OK");
				System.out.println("FileName "+fullFileName + "::::::" +fileCalcCheckSum);
			} else {
				responseCheckSum.setChecksumValue(fileCalcCheckSum);
				responseCheckSum.setMsg("NOT OK");
				System.out.println("FileName NOT OK "+fullFileName + "::::::" +fileCalcCheckSum);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseCheckSum;
	}
	
	private static String getFileChecksum(File file) throws IOException {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = Files.readAllBytes(file.toPath());
			byte[] hashBytes = digest.digest(bytes);
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new IOException("MD5 algorithm not found: " + e.getMessage());
		}
	}
}