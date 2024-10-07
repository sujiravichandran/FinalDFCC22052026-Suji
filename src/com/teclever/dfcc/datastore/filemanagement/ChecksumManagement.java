package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teclever.datastore.dto.FileChecksumDetailDto;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SingleChecksum;
import com.teclever.datastore.entities.VDDChecksum;
import com.teclever.datastore.service.SingleChecksumService;
import com.teclever.datastore.service.VDDChecksumService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.ChecksumDto;
import com.teclever.dfcc.datastore.dto.ChecksumResponse;
import com.teclever.dfcc.datastore.dto.ValidateResponse;

public class ChecksumManagement {

	private String scriptFileParentPath;

	public ChecksumResponse getListOfVDD() {
		ChecksumResponse checksumResponse = new ChecksumResponse();
		Response res = new Response();
		try {
			VDDChecksumService vDDService = new VDDChecksumService();

			GetResponse vDDServiceResponse = vDDService.getChecksum();
			if (vDDServiceResponse.getCode() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage("Get VDDChecksum Unsuccessfull");
				checksumResponse.setResponse(res);
				return checksumResponse;
			}

			List<ChecksumDto> listofVDDDto = new ArrayList<>();
			System.out.println(vDDServiceResponse.getCode());
			for (Object object : vDDServiceResponse.getResponseList()) {
				VDDChecksum vdd = (VDDChecksum) object;
				ChecksumDto vDDDto = new ChecksumDto(vdd.getFileName(), vdd.getFilePath(), vdd.getChecksum());
				listofVDDDto.add(vDDDto);
			}
			res.setResponseCode(1);
			res.setResponseMessage("Get VDDChecksum Successfull");
			checksumResponse.setResponse(res);
			checksumResponse.setvDDList(listofVDDDto);
			return checksumResponse;

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDDChecksum Unsuccessfull " + e);
			checksumResponse.setResponse(res);
			e.printStackTrace();
			return checksumResponse;
		}

	}

	public Response addVDDList(List<ChecksumDto> lst) {
		Response res = new Response();
		try {
			if (lst.isEmpty()) {
				res.setResponseCode(0);
				res.setResponseMessage("Fetching VDDChecksum Unsuccessful: Please Input List Of Data");
				return res;
			}

			VDDChecksumService vDDService = new VDDChecksumService();
			List<VDDChecksum> listofVDD = new ArrayList<>();
			Set<String> fileNamesAndPaths = new HashSet<>();

			for (ChecksumDto vDDDto : lst) {
				String fileNameAndPath = vDDDto.getFileName() + "#" + vDDDto.getFilePath();
				if (!fileNamesAndPaths.contains(fileNameAndPath)) {
					VDDChecksum vdd = new VDDChecksum();
					vdd.setFileName(vDDDto.getFileName());
					vdd.setFilePath(vDDDto.getFilePath());
					vdd.setChecksum(vDDDto.getChecksum());
					vdd.setSingleChecksum(vDDDto.isSingleChecksum());

					listofVDD.add(vdd);
					fileNamesAndPaths.add(fileNameAndPath); // Avoid adding duplicate entries
				} else {
					System.out.println("File Name " + vDDDto.getFileName() + " File path " + vDDDto.getFilePath());
				}
			}

			res = vDDService.addListOfChecksum(listofVDD);
			return res;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessful: " + e);
			return res;
		}

	}

	public Response addVDD(ChecksumDto vDDDto) {

		Response res = new Response();
		try {
			VDDChecksum vdd = new VDDChecksum();
			vdd.setFileName(vDDDto.getFileName());
			vdd.setFilePath(vDDDto.getFilePath());
			vdd.setChecksum(vDDDto.getChecksum());
			VDDChecksumService vDDService = new VDDChecksumService();

			res = vDDService.addChecksum(vdd);
			return res;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			return res;
		}

	}

	public ChecksumResponse extractingFilesumFile(String path) {
	    ChecksumResponse vDDResponse = new ChecksumResponse();
	    Response res = new Response();
	    
	    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
	        String line;
	        List<ChecksumDto> vDDList = new ArrayList<>();
	        
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split("\\s+");
	            if (parts.length < 2) {
	                continue;
	            }

	            String fileCheckSum = parts[0];
	            String fileName = parts[1].substring(parts[1].lastIndexOf("/") + 1);
	            
	            // Check if the file is filesum.txt
	            String filePathString;
	            if (fileName.equals("filesum.txt")) {
	                // Use scriptFileParentPath for filesum.txt
	                filePathString = scriptFileParentPath+File.separator;
	            } else {
	                // Normal file processing
	                filePathString = parts[1].substring(0, parts[1].lastIndexOf("/") + 1);
	            }

	            // Create ChecksumDto and add to the list
	            ChecksumDto vDDDto = new ChecksumDto(fileName, filePathString, fileCheckSum, fileName.equals("filesum.txt"));
	            vDDList.add(vDDDto);
	        }
	        
	        // Presuming addVDDList adds the list to some storage or processing structure
	        addVDDList(vDDList);
	        
	        res.setResponseCode(1);
	        res.setResponseMessage("Get Data From File Successful");
	        vDDResponse.setResponse(res);
	        return vDDResponse;
	        
	    } catch (Exception e) {
	        res.setResponseCode(0);
	        res.setResponseMessage("Fetching Filesum Unsuccessful: " + e.getMessage());
	        vDDResponse.setResponse(res);
	        e.printStackTrace();
	        return vDDResponse;
	    }
	}

	public List<FileChecksumDetailDto> extractingFilesumFileUserLogin(String path) {
		List<FileChecksumDetailDto> checksumDetails = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\s+");
				if (parts.length < 2)
					continue;

				String fileCheckSum = parts[0];
				String fileName = parts[1].substring(parts[1].lastIndexOf("/") + 1);
				String filePathString = parts[1].substring(0, parts[1].lastIndexOf("/") + 1);

				FileChecksumDetailDto detail = new FileChecksumDetailDto(filePathString + fileName, fileCheckSum);
				checksumDetails.add(detail);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return checksumDetails;
	}

	public Response exportVDDDetails(String filePathFileName) {
		Response res = new Response();

		java.io.File file = new java.io.File(filePathFileName);
		file.getParentFile().mkdirs();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePathFileName))) {
			ChecksumResponse vDDResponse = getListOfVDD();
			List<ChecksumDto> vDDList = vDDResponse.getvDDList();

			for (ChecksumDto vDDDto : vDDList) {
				writer.write(vDDDto.getFilePath() + " " + vDDDto.getChecksum());
				writer.newLine();
			}

			res.setResponseCode(1);
			res.setResponseMessage("VDD File Generated Successfully");
		} catch (Exception ex) {
			res.setResponseCode(0);
			res.setResponseMessage("Error generating VDD file: " + ex.getMessage());
			ex.printStackTrace();
		}

		return res;
	}

	// used in ADMIN page
	public Response selectScriptFile(String scriptFile) {
		Response res = new Response();
		
		File scriptFileObj = new File(scriptFile);
		String scriptFileDir = scriptFileObj.getParent();
		System.out.println("SCRIPT FILE ------" + scriptFileDir);
		scriptFileParentPath = scriptFileDir;
		
		List<String> command = new ArrayList<>();
		command.add("/bin/bash");
		command.add("-c");
		command.add(scriptFile);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(new File(scriptFileDir));


		try {
			Process process = processBuilder.start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
			}

			try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String line;
				while ((line = errorReader.readLine()) != null) {
					System.err.println(line);
				}
			}

			int exitCode = process.waitFor();
			System.out.println("\nExited with error code: " + exitCode);



			// delete previous data
			VDDChecksumService s = new VDDChecksumService();
			s.deleteChecksum();

			String filesumFilePath = Paths.get(scriptFileDir, "filesum.txt").toString();
			String dirsumFilePath = Paths.get(scriptFileDir, "dirsum.txt").toString();

			//saving location to db
			SingleChecksumService a = new SingleChecksumService();
			SingleChecksum singleChecksum = new SingleChecksum(scriptFile, dirsumFilePath, filesumFilePath);
			a.addSingleChecksum(singleChecksum);
			
			extractingFilesumFile(filesumFilePath);
			extractingFilesumFile(dirsumFilePath);

			res.setResponseCode(1);
			res.setResponseMessage("Success");
			return res;

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			res.setResponseCode(1);
			res.setResponseMessage("Failed");
			return res;

		}
	}
	
	
		// used in USER LOGIN page
		public Response selectUserScriptFile(String scriptFile) {
			Response res = new Response();
			File scriptFileObj = new File(scriptFile);
			String scriptFileDir = scriptFileObj.getParent();
			scriptFileParentPath = scriptFileDir;
			
			List<String> command = new ArrayList<>();
			command.add("/bin/bash");
			command.add("-c");
			command.add(scriptFile);

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(new File(scriptFileDir));


			try {
				Process process = processBuilder.start();

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				}

				try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					String line;
					while ((line = errorReader.readLine()) != null) {
						System.err.println(line);
					}
				}

				int exitCode = process.waitFor();
				System.out.println("\nExited with error code: " + exitCode);


				res.setResponseCode(1);
				res.setResponseMessage("Success");
				return res;

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				res.setResponseCode(1);
				res.setResponseMessage("Failed");
				return res;

			}
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

	public ValidateResponse compare() {
	    VDDChecksumService service = new VDDChecksumService();
	    List<String> checksumValue = service.getChecksumOfFilesum();
	    List<FileChecksumDetailDto> dbFileChecksumDetails = service.getChecksumDetails();
	    Response response = new Response();
	    ValidateResponse validateResponse = new ValidateResponse();
	    List<CheckSum> checkSumList = new ArrayList<>();

	    try {
	    	//Path filePath = Paths.get(scriptFileParentPath, "filesum.txt");
	        String filePath = scriptFileParentPath+File.separator+"filesum.txt";
	        System.out.println("USER LOGIN TIME filesumPath:: --- " + filePath);
	        String fileChecksum = getFileChecksum(Paths.get(filePath).toFile());
	        CheckSum filesum = new CheckSum();
	        filesum.setFile(filePath);
	        filesum.setChecksumValue(fileChecksum);
	        filesum.setMsg("OK");

	        if (checksumValue.contains(fileChecksum)) {
	        	response.setResponseCode(1);
	        	response.setResponseMessage("CHECKSUM MATCHED");
	            validateResponse.setResponse(response);
	            checkSumList.add(filesum);
	        } else {
	        	response.setResponseCode(1);
	        	response.setResponseMessage("CHECKSUM NOT MATCHED");
	            validateResponse.setResponse(response);
	            checkSumList.addAll(performDetailedComparison(dbFileChecksumDetails, Paths.get(filePath)));
	        }

	        validateResponse.setCheckSumList(checkSumList);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return validateResponse;
	}

	
	
	
	private List<CheckSum> performDetailedComparison(List<FileChecksumDetailDto> dbFileChecksumDetails, Path filePath) {
	    List<FileChecksumDetailDto> fileSumDetails = extractingFilesumFileUserLogin(filePath.toString());

	    List<CheckSum> checkSumList = new ArrayList<>();

	    Map<String, String> fileSumMap = new HashMap<>();
	    for (FileChecksumDetailDto detail : fileSumDetails) {
	        fileSumMap.put(detail.getFullPath(), detail.getChecksum());
	    }

	    Map<String, String> dbChecksumMap = new HashMap<>();
	    for (FileChecksumDetailDto detail : dbFileChecksumDetails) {
	        dbChecksumMap.put(detail.getFullPath(), detail.getChecksum());
	    }

	    for (String filePathKey : fileSumMap.keySet()) {
	        if (!dbChecksumMap.containsKey(filePathKey)) {
	            checkSumList.add(new CheckSum(filePathKey,"", "Extra File"));
	        } else if (!fileSumMap.get(filePathKey).equals(dbChecksumMap.get(filePathKey))) {
	            checkSumList.add(new CheckSum(filePathKey,fileSumMap.get(filePathKey) ,"NOT OK"));
	        } 
//	        else {
//	            checkSumList.add(new CheckSum(filePathKey,fileSumMap.get(filePathKey), "OK"));
//	        }
	    }

	    for (String filePathKey : dbChecksumMap.keySet()) {
	        if (!fileSumMap.containsKey(filePathKey)) {
	            checkSumList.add(new CheckSum(filePathKey,dbChecksumMap.get(filePathKey) ,"No File"));
	        }
	    }

	    return checkSumList;
	}


}
