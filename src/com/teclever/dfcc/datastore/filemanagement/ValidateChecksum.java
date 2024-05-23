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
import com.teclever.datastore.entities.RunConfiguration;
import com.teclever.datastore.entities.RunPathMaster;
import com.teclever.datastore.response.RunConfigurationResponse;
import com.teclever.datastore.service.RunConfigurationService;
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
		// Response Object
		ValidateResponse validateResponse = new ValidateResponse();
		Response response = new Response();
		try {

			RunPathMasterService runPathMasterService = new RunPathMasterService();
			// Get All RunPathMaster <RunConfigId , SET of RunPathMaster>
			Map<String, Set<RunPathMaster>> runConfigIdWithPathMasterId = runPathMasterService.getAllRunPathMaster();
			// Checking Run Path Master Table is Empty
			if (runConfigIdWithPathMasterId == null) {
				response.setResponseCode(0);
				response.setResponseMessage("Run Config is Empty ");
				validateResponse.setResponse(response);
				return validateResponse;
			}
			// File Name with Path Storing
			Set<String> fileNamewithPath = new HashSet<>();

			// Loop: RunConfigId with SET of RunPathMaster
			for (Map.Entry<String, Set<RunPathMaster>> mapData : runConfigIdWithPathMasterId.entrySet()) {
				// Loop: Set of RunPathMaster
				for (RunPathMaster runPathMaster : mapData.getValue()) {
					// Switch Case: Based on Master Path.
					switch (runPathMaster.getMasterPath()) {
					case "tpf":
						// Get List of TPF Table Data By RunPathMasterId
						List<TestFileDto> listOfTestPlanFile = TestPlanFileManagement
								.getAllTestFilesByRunPathMasterId(runPathMaster.getRunPathMasterId());
						for (TestFileDto testFileDto : listOfTestPlanFile) {
							// Putting into Set
							fileNamewithPath.add(testFileDto.getTestFileName());
						}
						break;

					case "symbols":
						// Get List of SYMBOLS Table Data By RunPathMasterId
						List<SymbolDto> listOfSymbols = SymbolFileManagement
								.getAllSymbolsByRunPathMasterId(runPathMaster.getRunPathMasterId());

						for (SymbolDto symbolDto : listOfSymbols) {
							// Putting into Set
							fileNamewithPath.add(symbolDto.getFileName());
						}
						break;
					case "macros":
						// Get List of MACROS Table Data By RunPathMasterId
						List<MacroDto> macroDto = MacroFileManagement
								.getAllMacrobyRunPathMassterId(runPathMaster.getRunPathMasterId());
						for (MacroDto macro : macroDto) {
							// Putting into Set
							fileNamewithPath.add(macro.getFileName());
						}
						break;
					case "download":
//						Get List of DOWNLOAD Table Data By RunPathMasterId 
						System.out.println(runPathMaster.getRunPathMasterId());
						List<DownloadFileDto> listOfDownloadFile = DownloadFileManagement
								.getAllDownloadFilesByRunPathId(runPathMaster.getRunPathMasterId());
						for (DownloadFileDto macro : listOfDownloadFile) {
							// Putting into Set
							fileNamewithPath.add(macro.getDownloadFileName());
						}
						break;

					default:
						break;
					}
				}
			}
			VDDManagement vddManagement = new VDDManagement();
			VDDResponse vddResponse = vddManagement.getListOfVDD();
			// Get VDD Table Data
			List<VDDDto> listOfVdd = vddResponse.getvDDList();
			Map<String, String> vddMap = new HashMap<>();

			List<CheckSum> listOfCheckSum = new ArrayList<>();

			for (VDDDto vddDto : listOfVdd) {
				// putting into vddMap key:fileName with path, value: file checksum
				vddMap.put(vddDto.getFilePath() + vddDto.getFileName(), vddDto.getFileCheckSum());
				// Checking All VDD File present in Tables(SYMBOLS,TEST FILE, MICROS ,DOWNLOAD)
				if (!(fileNamewithPath.contains(vddDto.getFileName()))) {
					CheckSum checkSum = new CheckSum();
					checkSum.setFile(vddDto.getFileName());
					checkSum.setMsg("No File");
					checkSum.setChecksumValue(vddDto.getFileCheckSum());
					listOfCheckSum.add(checkSum);
				}
			}
			for (String fileName : fileNamewithPath) {
				CheckSum checkSum = new CheckSum();
				// Checking All DB Tables(SYMBOLS,TEST FILE, MICROS ,DOWNLOAD) Files present in
				// VDD Table
				if (vddMap.get(fileName) == null) {
					checkSum.setFile(fileName);
					checkSum.setMsg("No VDD Info");

					listOfCheckSum.add(checkSum);
					continue;
				}
				// Passing DB File Name and VDD CheckSum and Get The CheckSum object as Return
				checkSum = validateChecksum(fileName, vddMap.get(fileName));
				// Adding into CheckSum List
				listOfCheckSum.add(checkSum);
			}

			response.setResponseCode(1);
			response.setResponseMessage("Validate Succesfull ");
			// CheckSum List Add to return Type
			validateResponse.setCheckSumList(listOfCheckSum);
			// Response is Add to return type
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

			} else if (fileCheckSum.equals(fileCalcCheckSum)) {
				responseCheckSum.setChecksumValue(fileCalcCheckSum);
				responseCheckSum.setMsg("OK");
			} else {
				responseCheckSum.setChecksumValue(fileCalcCheckSum);
				responseCheckSum.setMsg("NOT OK");

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

	public ValidateResponse validateUserChecksum(String uutId) {
		ValidateResponse validateResponse = new ValidateResponse();
		Response response = new Response();
		try {
			RunConfigurationService runConfigurationService = new RunConfigurationService();
			RunConfigurationResponse serviceResponse = runConfigurationService.getRunConfigurationByUutId(uutId);

			if (serviceResponse == null || serviceResponse.getResponseCode() == 0) {
				response.setResponseCode(0);
				response.setResponseMessage(serviceResponse.getResponseMessage());
				validateResponse.setResponse(response);
				return validateResponse;
			}
			List<RunConfiguration> runConfigurations = serviceResponse.getRunConfigurations();

			RunPathMasterService runPathMasterService = new RunPathMasterService();
			Map<String, Set<RunPathMaster>> map = runPathMasterService.getAllRunPathMaster();
			if (map == null) {
				response.setResponseCode(0);
				response.setResponseMessage("Run Config is Empty ");
				validateResponse.setResponse(response);
				return validateResponse;
			}

			Set<String> fileNamewithPath = new HashSet<>();

			for (RunConfiguration runConfig : runConfigurations) {
				for (RunPathMaster runPathMaster : map.get(runConfig.getRunConfigId())) {
					switch (runPathMaster.getMasterPath()) {
					case "tpf":
						List<TestFileDto> listOfTestPlanFile = TestPlanFileManagement
								.getAllTestFilesByRunPathMasterId(runPathMaster.getRunPathMasterId());
						for (TestFileDto testFileDto : listOfTestPlanFile) {
							fileNamewithPath.add(testFileDto.getTestFileName());
						}
						break;

					case "symbols":
						List<SymbolDto> listOfSymbols = SymbolFileManagement
								.getAllSymbolsByRunPathMasterId(runPathMaster.getRunPathMasterId());

						for (SymbolDto symbolDto : listOfSymbols) {
							fileNamewithPath.add(symbolDto.getFileName());
						}
						break;
					case "macros":
						List<MacroDto> macroDto = MacroFileManagement
								.getAllMacrobyRunPathMassterId(runPathMaster.getRunPathMasterId());
						for (MacroDto macro : macroDto) {
							fileNamewithPath.add(macro.getFileName());
						}
						break;
					case "download":
						System.out.println(runPathMaster.getRunPathMasterId());
						List<DownloadFileDto> listOfDownloadFile = DownloadFileManagement
								.getAllDownloadFilesByRunPathId(runPathMaster.getRunPathMasterId());
						for (DownloadFileDto macro : listOfDownloadFile) {
							fileNamewithPath.add(macro.getDownloadFileName());
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

			Map<String, String> vddMap = new HashMap<>();

			List<CheckSum> listOfCheckSum = new ArrayList<>();

			for (VDDDto vddDto : listOfVdd) {
				vddMap.put(vddDto.getFilePath() + vddDto.getFileName(), vddDto.getFileCheckSum());
			}

			for (String fileName : fileNamewithPath) {
				CheckSum checkSum = new CheckSum();
				if (vddMap.get(fileName) == null) {
					checkSum.setFile(fileName);
					checkSum.setMsg("No VDD Info");

					listOfCheckSum.add(checkSum);
					continue;
				}

				checkSum = validateChecksum(fileName, vddMap.get(fileName));
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
}
