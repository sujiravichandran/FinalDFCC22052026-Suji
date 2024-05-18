package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.exception.ConstraintViolationException;

import com.teclever.datastore.dto.FaultCodeMasterResponse;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.FaultCodeMaster;
import com.teclever.datastore.service.FaultCodeMasterService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.FaultCodeAddResponse;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;

public class FaultCodeManagement {
	public static String[] headers = { "FaultCodeId", "FaultCodeDescription" };

	public FaultCodeResponse getFaultCodeList() {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		try {
			FaultCodeMasterService faultCodeMasterService = new FaultCodeMasterService();

			GetResponse faultCodeServiceResponse = faultCodeMasterService.getAllFaultCodeMaster();
			if (faultCodeServiceResponse.getCode() == 0) {
				res.setResponseCode(faultCodeServiceResponse.getCode());
				res.setResponseMessage(faultCodeServiceResponse.geteMsg());
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}

			List<FaultCodeDTO> faultCodeList = new ArrayList<>();
			System.out.println(faultCodeServiceResponse.getCode());
			for (Object object : faultCodeServiceResponse.getResponseList()) {
				FaultCodeMaster faultCodeMaster = (FaultCodeMaster) object;
				FaultCodeDTO faultCodeDTO = new FaultCodeDTO(faultCodeMaster.getFaultCodeId(),
						faultCodeMaster.getFaultCode(), faultCodeMaster.getFaultCodeDescription(),
						faultCodeMaster.getFalutCodeFilePath());
				faultCodeList.add(faultCodeDTO);
			}
			res.setResponseCode(1);
			res.setResponseMessage("Get VDD Successfull");
			faultCodeResponse.setResponse(res);
			faultCodeResponse.setFaultCodeList(faultCodeList);
			return faultCodeResponse;

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			faultCodeResponse.setResponse(res);
			e.printStackTrace();
			return faultCodeResponse;
		}
	}

	public FaultCodeResponse addListFaultCode(List<FaultCodeDTO> listOfFaultCodeDto, String faultCodeFilePath) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		try {
			if (!(listOfFaultCodeDto.size() > 0)) {
				res.setResponseCode(0);
				res.setResponseMessage("Fetching VDD Unsuccessfull : Please Input List Of Data ");
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}
			FaultCodeMasterService faultCodeMasterService = new FaultCodeMasterService();

			List<FaultCodeMaster> listofFaultCodeMaster = new ArrayList<>();

			Set<String> faultCodes = new HashSet<>();
			for (FaultCodeDTO faultCodeDTO : listOfFaultCodeDto) {
				String fileNameAndPath = String.valueOf(faultCodeDTO.getFaultCode());
				if (!faultCodes.contains(fileNameAndPath)) {
					FaultCodeMaster faultCodeMaster = new FaultCodeMaster();

					faultCodeMaster.setFaultCode(faultCodeDTO.getFaultCode());
					faultCodeMaster.setFaultCodeDescription(faultCodeDTO.getFaultCodeDescription());
					faultCodeMaster.setFalutCodeFilePath(faultCodeFilePath);
					listofFaultCodeMaster.add(faultCodeMaster);
				} else {
					System.out.println(" Repeted Fault Code " + faultCodeDTO.getFaultCode() + " Description "
							+ faultCodeDTO.getFaultCodeDescription());
				}
			}

			FaultCodeMasterResponse response = faultCodeMasterService.addListOfFaultCodeMaster(listofFaultCodeMaster);
			List<FaultCodeDTO> faultCodeList = new ArrayList<>();
			if (response.getResponse().getResponseCode() == 1 && response.getFaultCodeMasterList().size() > 0) {
				for (Object entry : response.getFaultCodeMasterList()) {
					FaultCodeMaster faultCodeMaster = (FaultCodeMaster) entry;
					FaultCodeDTO faultCodeDTO = new FaultCodeDTO(faultCodeMaster.getFaultCodeId(),
							faultCodeMaster.getFaultCode(), faultCodeMaster.getFaultCodeDescription(),
							faultCodeMaster.getFalutCodeFilePath());
					faultCodeList.add(faultCodeDTO);
				}
			}
			faultCodeResponse.setExcelResponse(response.getExcelResponse());
			faultCodeResponse.setResponse(response.getResponse());
			faultCodeResponse.setFaultCodeList(faultCodeList);
			return faultCodeResponse;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching VDD Unsuccessfull " + e);
			faultCodeResponse.setResponse(res);
			return faultCodeResponse;
		}
	}

	public FaultCodeAddResponse addFaultCode(int faultCode, String faultCodeDescription, String faultCodefilePath) {
		FaultCodeAddResponse faultCodeAddResponse = new FaultCodeAddResponse();
		Response res = new Response();
		try {
			if (faultCode == 0) {
				res.setResponseCode(0);
				res.setResponseMessage("Add FaultCode Unsuccessfull : Please Input Valid FaultCode ");
				faultCodeAddResponse.setResponse(res);
				return faultCodeAddResponse;
			}
			FaultCodeMasterService faultCodeMasterService = new FaultCodeMasterService();

			FaultCodeMaster faultCodeMaster = new FaultCodeMaster();

			faultCodeMaster.setFaultCode(faultCode);
			faultCodeMaster.setFaultCodeDescription(faultCodeDescription);
			faultCodeMaster.setFalutCodeFilePath(faultCodefilePath);

			GetObjResponse response = faultCodeMasterService.addFaultCodeMaster(faultCodeMaster);
			FaultCodeMaster faultCodeMasterResponse = (FaultCodeMaster) response.getObject();
			FaultCodeDTO faultCodeDTO = new FaultCodeDTO(faultCodeMasterResponse.getFaultCodeId(),
					faultCodeMasterResponse.getFaultCode(), faultCodeMasterResponse.getFaultCodeDescription(),
					faultCodeMasterResponse.getFalutCodeFilePath());

			faultCodeAddResponse.setResponse(response.getResponse());
			faultCodeAddResponse.setFaultCodeMaster(faultCodeDTO);
			return faultCodeAddResponse;
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Add FaultCode Unsuccessfull ");
			faultCodeAddResponse.setResponse(res);
			return faultCodeAddResponse;
		}
	}

	public FaultCodeResponse extractingFaultCodeFile(String path) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		try {
			String baseFileName = path.substring(path.lastIndexOf("/") + 1);
			System.out.println(baseFileName + !path.endsWith(".xlsx"));
			InputStream inputStream = new FileInputStream(new File(path));
			if (!path.endsWith(".xlsx")) {
				res.setResponseCode(0);
				res.setResponseMessage("Please upload an excel file! ");
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			List<FaultCodeDTO> listOfFaultCodeDto = new ArrayList<>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (currentRow.getRowNum() == 0) {

					for (int cellIndex = 0; cellIndex < headers.length; cellIndex++) {
						Cell cell = currentRow.getCell(cellIndex);

						if (cell.getCellType() == CellType.STRING) {
							if (cellIndex == 0 && !cell.getStringCellValue().contains("faultCodeId")) {
								res.setResponseMessage("In ROW " + currentRow.getRowNum() + " and CELL "
										+ cell.getColumnIndex() + " " + "value must be faultCodeId");
								res.setResponseCode(0);
								workbook.close();
								faultCodeResponse.setResponse(res);
								return faultCodeResponse;
							} else if (cellIndex == 1 && !cell.getStringCellValue().contains("faultCodeDescription")) {
								res.setResponseMessage("In ROW " + currentRow.getRowNum() + " and CELL "
										+ cell.getColumnIndex() + " " + "value must be faultCodeDescription");
								res.setResponseCode(0);
								workbook.close();
								faultCodeResponse.setResponse(res);
								return faultCodeResponse;
							}
						}
					}
					rowNumber++;
					continue;
				}

				Iterator<Cell> cellsInRow = currentRow.iterator();
				FaultCodeDTO faultCodeDto = new FaultCodeDTO();
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();
					switch (currentCell.getColumnIndex()) {
					case 0:
						if (currentCell.getCellType() != CellType.BLANK) {

							if (currentCell.getCellType() == CellType.NUMERIC) {
								String b = new DataFormatter().formatCellValue(currentRow.getCell(0));
								faultCodeDto.setFaultCode(Integer.parseInt(String.valueOf(b)));
							}
						}
						break;

					case 1:
						faultCodeDto.setFaultCodeDescription(currentCell.getStringCellValue());
						;
						break;

					default:
						break;
					}

				}
				listOfFaultCodeDto.add(faultCodeDto);
			}
			faultCodeResponse = addListFaultCode(listOfFaultCodeDto, path);
			workbook.close();
//			if (faultCodeResponse.getResponse().getResponseCode() == 0) {
//				res = faultCodeResponse.getResponse();
//				workbook.close();
//				return faultCodeResponse;
//			}
//			res.setResponseCode(1);
//			res.setResponseMessage("Import Successful ");
//			faultCodeResponse.setResponse(res);
//			faultCodeResponse.setFaultCodeList(listOfFaultCodeDto);
//			workbook.close();
		} catch (ConstraintViolationException ex) {
			System.out.println("Exception is handled.............");
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Error while importing " + e.getLocalizedMessage());
			faultCodeResponse.setResponse(res);
			return faultCodeResponse;
		}

		return faultCodeResponse;
	}

}