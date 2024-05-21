package com.teclever.dfcc.datastore.filemanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.VDD;
import com.teclever.datastore.service.VDDService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.VDDDto;
import com.teclever.dfcc.datastore.dto.VDDResponse;

public class VDDManagement {

	public VDDResponse getListOfVDD() {
		VDDResponse vDDResponse = new VDDResponse();
		Response res = new Response();
		try {
			VDDService vDDService = new VDDService();

			GetResponse vDDServiceResponse = vDDService.getVDD();
			if (vDDServiceResponse.getCode() == 0) {
				res.setResponseCode(0);
				res.setResponseMessage("Get VDD Unsuccessfull");
				vDDResponse.setResponse(res);
				return vDDResponse;
			}

			List<VDDDto> listofVDDDto = new ArrayList<>();
			System.out.println(vDDServiceResponse.getCode());
			for (Object object : vDDServiceResponse.getResponseList()) {
				VDD vdd = (VDD) object;
				VDDDto vDDDto = new VDDDto(vdd.getvDDFileCheckSum(), vdd.getvDDFIlePath(), vdd.getVDDfileName(),
						vdd.getFileName());
				listofVDDDto.add(vDDDto);
			}
			res.setResponseCode(1);
			res.setResponseMessage("Get VDD Successfull");
			vDDResponse.setResponse(res);
			vDDResponse.setvDDList(listofVDDDto);
			return vDDResponse;

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			vDDResponse.setResponse(res);
			e.printStackTrace();
			return vDDResponse;
		}

	}

	public Response addVDDList(List<VDDDto> lst) {
		Response res = new Response();
		try {
			if (!(lst.size() > 0)) {
				res.setResponseCode(0);
				res.setResponseMessage("Fetching VDD Unsuccessfull : Please Input List Of Data ");
				return res;
			}
			VDDService vDDService = new VDDService();

			List<VDD> listofVDD = new ArrayList<>();

			Set<String> fileNamesAndPaths = new HashSet<>();
			for (VDDDto vDDDto : lst) {
				String fileNameAndPath = vDDDto.getFileName() + "#" + vDDDto.getFilePath();
				if (!fileNamesAndPaths.contains(fileNameAndPath)) {
//			for (VDDDto vDDDto : lst) {
					VDD vdd = new VDD();

					vdd.setFileName(vDDDto.getBaseFileName());
					vdd.setVDDfileName(vDDDto.getFileName());
					vdd.setvDDFIlePath(vDDDto.getFilePath());
					vdd.setvDDFileCheckSum(vDDDto.getFileCheckSum());

					listofVDD.add(vdd);
				}else {
					System.out.println(" File Name " +vDDDto.getFileName() + "File path " + vDDDto.getFilePath());
				}
			}
			res = vDDService.addListOfVDD(listofVDD);
			return res;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			return res;
		}

	}

	public Response addVDD(VDDDto vDDDto) {
		
		Response res = new Response();
		try {
			VDD vdd = new VDD();
			vdd.setFileName(vDDDto.getBaseFileName());
			vdd.setVDDfileName(vDDDto.getFileName());
			vdd.setvDDFIlePath(vDDDto.getFilePath());
			vdd.setvDDFileCheckSum(vDDDto.getFileCheckSum());
			VDDService vDDService = new VDDService();

			res = vDDService.addVDD(vdd);
			return res;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			return res;
		}

	}

	public VDDResponse extractingVDDFile(String path) {
		VDDResponse vDDResponse = new VDDResponse();
		Response res = new Response();
		try {
			String baseFileName = path.substring(path.lastIndexOf("/") + 1);
			System.out.println("Reading file: " + baseFileName + "  " + path.lastIndexOf("/"));

			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line/* = reader.readLine() */;
			List<VDDDto> vDDList = new ArrayList<>();
			while ((line = reader.readLine()) != null) {

				String[] parts = line.split("\\s+");

				String fileCheckSum = parts[0];
				String fileName = parts[1].substring(parts[1].lastIndexOf("/") + 1);
				String filePathString = parts[1].substring(0, parts[1].lastIndexOf("/") + 1); 


				VDDDto vDDDto = new VDDDto(fileCheckSum, filePathString, fileName, path);
				vDDList.add(vDDDto);

			}
			addVDDList(vDDList);
			reader.close();
			res.setResponseCode(1);
			res.setResponseMessage("Get Data From File Successfull");
			vDDResponse.setResponse(res);
			return vDDResponse;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			vDDResponse.setResponse(res);
			e.printStackTrace();
			return vDDResponse;
		}

	}
}
