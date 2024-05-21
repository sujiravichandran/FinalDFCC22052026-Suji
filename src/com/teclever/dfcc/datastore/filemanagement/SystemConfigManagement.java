package com.teclever.dfcc.datastore.filemanagement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.datastore.dto.CheckSum;
import com.teclever.dfcc.datastore.dto.SystemConfig;
import com.teclever.dfcc.datastore.dto.ValidateResponse;
public class SystemConfigManagement {
	private static final String SECRET_KEY = "Te6lever@2024.bel";
	static String currentDirectory = System.getProperty("user.dir");
	static String configFileName = currentDirectory+ "/SystemConfig.dat";
	static String directoryToCheck = currentDirectory +"/libs";
	static String parentJarFilePath = currentDirectory + "/DFCC-TesterV0.9.jar";
	private static SystemConfig configuration;
//	static String configFileName = "C:\\Deployment\\SystemConfig.dat";
//	static String directoryToCheck = "C:\\Deployment\\libs";
//	static String parentJarFilePath ="C:\\Deployment\\DFCC-TesterV0.9.jar";
	
	public static SystemConfig getConfiguration() {
		return configuration;
	}
	public static void setConfiguration(SystemConfig configuration) throws IOException  {
		SystemConfigManagement.configuration = configuration;
		saveSystemConfig(configuration, configFileName);
	}
	public static ValidateResponse validateConfig() {
	    Response response = new Response();
   //System.out.println("Current Directory: " + currentDirectory);
	    ValidateResponse validateResponse = new ValidateResponse();
	    try {
	        File dir = new File(configFileName);
	        if (!dir.exists()) {
	            response.setResponseCode(0);
	            response.setResponseMessage("Dat file Not present in path");
	            validateResponse.setResponse(response);
	            return validateResponse;
	        }
	        configuration = SystemConfigManagement.loadSystemConfig(configFileName);
	        if (configuration.getFirstLine() != null) {
	            if (configuration.getAdminName() == null || configuration.getAdminPassword() == null
	                    || configuration.getLaunchType() == null || configuration.getLoginType() == null) {
	                response.setResponseCode(0);
	                response.setResponseMessage("Invalid data in file");
	                validateResponse.setResponse(response);
		            return validateResponse;
	            }
	            if (configuration.getChecksums() == null) {
	                response.setResponseCode(0);
	                response.setResponseMessage("Checksum is Empty");
	                validateResponse.setResponse(response);
		            return validateResponse;
	            }
	            // Validate pJarFile name
	            ValidateResponse pJarFileValidationResponse = validatepJarFileName(configuration, parentJarFilePath);
	            if (pJarFileValidationResponse.getResponse().getResponseCode() == 0) {
	                return pJarFileValidationResponse;
	            }
	            
	            validateResponse = SystemConfigManagement.validateChecksum(configuration, directoryToCheck,parentJarFilePath);
	            return validateResponse;
	        } else {
	            response.setResponseCode(0);
	            response.setResponseMessage("Dat file Data Mismatch");
	            validateResponse.setResponse(response);
	            return validateResponse;
	        }
	     
	    } catch (Exception e) {
	        response.setResponseCode(0);
	        response.setResponseMessage("Error in SystemConfig " + e.getLocalizedMessage());
	        validateResponse.setResponse(response);
            return validateResponse;
	    }
	}
	// DECRYPT SYSTEM CONFIG FILE AND STORE IT TO OBJECT
	public static SystemConfig loadSystemConfig(String configFile) throws IOException {
		try {
			String encryptedContent = readFromFile(configFile);
			String decryptedContent = decrypt(encryptedContent);
			Gson gson = new Gson();
			return gson.fromJson(decryptedContent, SystemConfig.class);
		} catch (Exception e) {
			throw new IOException("Error loading system config: " + e.getMessage());
		}
	}
	private static String readFromFile(String fileName) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				contentBuilder.append(line).append("\n");
			}
		}
		return contentBuilder.toString();
	}
	// DECRYPTION
	private static String decrypt(String encryptedData) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyBytes = Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(SECRET_KEY.getBytes()), 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedData.replaceAll("\\s", ""));
		byte[] decryptedBytes = cipher.doFinal(decodedBytes);
		return new String(decryptedBytes);
	}
	// API --- UPDATING ADMIN PASSWORD AND LOGIN TYPE ---
	public static void updateSystemConfig(SystemConfig config, String newAdminPassword, String newLoginType,
			String configFile) throws IOException {
		config.setAdminPassword(newAdminPassword);
		config.setLoginType(newLoginType);
		saveSystemConfig(config, configFile);
	}
	// SAVING TO SYSTEM CONFIG FILE
	private static void saveSystemConfig(SystemConfig config, String configFile) throws IOException {
		try {
			// Serialize system config to JSON
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(config);
			// Encrypt and write content to file
			String encryptedContent = encrypt(jsonString);
			writeToFile(configFile, encryptedContent);
		} catch (Exception e) {
			throw new IOException("Error saving system config: " + e.getMessage());
		}
	}
	private static void writeToFile(String fileName, String content) throws IOException {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(content);
		}
	}
	private static String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyBytes = Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(SECRET_KEY.getBytes()), 16);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encryptedBytes = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	// Method to validate if pJarFile name matches
	public static ValidateResponse validatepJarFileName(SystemConfig config, String jarFilePath) {
	    Response response = new Response();
	    ValidateResponse validateResponse = new ValidateResponse();
	    File jarFile = new File(jarFilePath);
	    if (!jarFile.exists() || !jarFile.isFile()) {
	        response.setResponseCode(0);
	        response.setResponseMessage("Jar File Not Found");
	    } else if (!config.getpJarFile().equals(jarFilePath)) {
	        System.out.println(config.getpJarFile());
	        response.setResponseCode(0);
	        response.setResponseMessage("Jar File Name Mismatch");
	    } else {
	        // Jar file exists and names match, set response code to 1 indicating success
	        response.setResponseCode(1);
	        response.setResponseMessage("Jar File Found and Matched");
	    }
	    validateResponse.setResponse(response);
	    return validateResponse;
	}


	
	
	public static ValidateResponse validateChecksum(SystemConfig config, String directory,String parentJarFilePath) throws IOException {
	    Response response = new Response();
	    List<CheckSum> checkSumList = new ArrayList<>();
	    List<String> filesAndChecksums = config.getChecksums();
	    Map<String, String> calculatedChecksums = calculateChecksums(directory);
	    // Validate JAR file checksum
	    ValidateResponse jarFileValidationResponse = validateJarFileChecksum(config, parentJarFilePath);
	    response.setResponseCode(jarFileValidationResponse.getResponse().getResponseCode());
	    response.setResponseMessage(jarFileValidationResponse.getResponse().getResponseMessage());
	    checkSumList.addAll(jarFileValidationResponse.getCheckSumList());
	  
//	    CheckSum jarCheckSum = new CheckSum();
//	    jarCheckSum.setFile(parentJarFilePath);
//	    jarCheckSum.setChecksumValue("");
//	    jarCheckSum.setMsg("OK");
//	    checkSumList.add(jarCheckSum);
	  
	    // Check if all files in the system config exist in the calculated checksums
	    for (String fileChecksum : filesAndChecksums) {
	        String[] parts = fileChecksum.split(" : ");
	        String filename = parts[0];
	        String checksum = parts[1];
	        String calculatedChecksum = calculatedChecksums.get(filename);
	        CheckSum checkSum = new CheckSum();
	        checkSum.setFile(filename);
	        checkSum.setChecksumValue(calculatedChecksum);
	        if (calculatedChecksum == null) {
	            checkSum.setMsg("NOT OK");
	        } else if (!calculatedChecksum.equals(checksum)) {
	            checkSum.setMsg("NOT OK");
	        } else {
	            checkSum.setMsg("OK");
	        }
	        checkSumList.add(checkSum);
	    }
	    response.setResponseCode(1);
	    response.setResponseMessage("SystemConfig file read Successful");
	  
	    ValidateResponse validateResponse = new ValidateResponse();
	    validateResponse.setResponse(response);
	    validateResponse.setCheckSumList(checkSumList);
	    return validateResponse;
	}
	public static Map<String, String> calculateChecksums(String directory) throws IOException {
	    Map<String, String> checksums = new HashMap<>();
	    try {
	        Files.walk(Paths.get(directory))
	                .filter(Files::isRegularFile)
	                .forEach(file -> {
	                    try {
	                        String checksum = getFileChecksum(file.toFile());
	                        checksums.put(file.toString(), checksum);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                });
	    } catch (IOException e) {
	        throw new IOException("Error reading files from directory: " + e.getMessage());
	    }
	    return checksums;
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
	
	public static ValidateResponse validateJarFileChecksum(SystemConfig config, String jarFilePath) throws IOException {
	    Response response = new Response();
	    List<CheckSum> checkSumList = new ArrayList<>();
	    try {
	        File jarFile = new File(jarFilePath);
	        if (!jarFile.exists() || !jarFile.isFile()) {
	            throw new FileNotFoundException("JAR file not found: " + jarFilePath);
	        }
	        String checksum = getFileChecksum(jarFile);
	        String expectedChecksum = config.getpChecksum();
	        CheckSum checkSum = new CheckSum();
	        checkSum.setFile(jarFilePath);
	        checkSum.setChecksumValue(checksum);
	        if (checksum.equals(expectedChecksum)) {
	            checkSum.setMsg("OK");
	            response.setResponseCode(1);
	            response.setResponseMessage("OK");
	        } else {
	            checkSum.setMsg("NOT OK");
	            response.setResponseCode(0);
	            response.setResponseMessage("NOT OK");
	        }
	        checkSumList.add(checkSum);
	    } catch (IOException e) {
	        response.setResponseCode(0);
	        response.setResponseMessage("Error validating JAR file checksum: " + e.getMessage());
	    }
	    ValidateResponse validateResponse = new ValidateResponse();
	    validateResponse.setResponse(response);
	    validateResponse.setCheckSumList(checkSumList);
	    return validateResponse;
	}
	
}