package com.teclever.dfcc.datastore.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.exception.ConstraintViolationException;

import com.opencsv.CSVReader;
import com.teclever.datastore.dto.FaultCodeMasterResponse;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.FaultCodeMaster;
import com.teclever.datastore.service.FaultCodeMasterService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.FaultCodeAddResponse;
import com.teclever.dfcc.datastore.dto.FaultCodeDTO;
import com.teclever.dfcc.datastore.dto.FaultCodeResponse;
import com.teclever.dfcc.utils.Debug;

public class FaultCodeConfiguration {
	public static String[] headers = { "FaultCodeId", "FaultCodeDescription" };

	public FaultCodeResponse getFaultCodeList(String uutId, String ofpConfigId) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		try {
			FaultCodeMasterService faultCodeMasterService = new FaultCodeMasterService();

			GetResponse faultCodeServiceResponse = faultCodeMasterService.getFaultCodeMaster(uutId, ofpConfigId);
			if (faultCodeServiceResponse.getCode() == 0) {
				res.setResponseCode(faultCodeServiceResponse.getCode());
				res.setResponseMessage(faultCodeServiceResponse.geteMsg());
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}

			List<FaultCodeDTO> faultCodeList = new ArrayList<>();
			for (Object object : faultCodeServiceResponse.getResponseList()) {
				FaultCodeMaster faultCodeMaster = (FaultCodeMaster) object;
				FaultCodeDTO faultCodeDTO = new FaultCodeDTO(faultCodeMaster.getFaultCodeId(),
						faultCodeMaster.getUutId(), faultCodeMaster.getOfpConfigId(), faultCodeMaster.getFaultCode(),
						faultCodeMaster.getFaultCodeDescription(), faultCodeMaster.getFalutCodeFilePath());
				faultCodeList.add(faultCodeDTO);
			}
			res.setResponseCode(1);
			res.setResponseMessage("Get FaultCode Successfull");
			faultCodeResponse.setResponse(res);
			faultCodeResponse.setFaultCodeList(faultCodeList);
			return faultCodeResponse;

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Fetching FaultCode Unsuccessfull ");
			faultCodeResponse.setResponse(res);
			e.printStackTrace();
			return faultCodeResponse;
		}
	}

	public FaultCodeResponse addListFaultCode(List<FaultCodeDTO> listOfFaultCodeDto, String faultCodeFilePath,String uutType,String ofpConfigId) {
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
					faultCodeMaster.setOfpConfigId(ofpConfigId);
					faultCodeMaster.setUutId(uutType);
					listofFaultCodeMaster.add(faultCodeMaster);
				} else {
					Debug.printDebug(" Repeted Fault Code " + faultCodeDTO.getFaultCode() + " Description "
							+ faultCodeDTO.getFaultCodeDescription());
				}
			}

			FaultCodeMasterResponse response = faultCodeMasterService.addListOfFaultCodeMaster(listofFaultCodeMaster);
			List<FaultCodeDTO> faultCodeList = new ArrayList<>();
			if (response.getResponse().getResponseCode() == 1 && response.getFaultCodeMasterList().size() > 0) {
				for (Object entry : response.getFaultCodeMasterList()) {
					FaultCodeMaster faultCodeMaster = (FaultCodeMaster) entry;
					FaultCodeDTO faultCodeDTO = new FaultCodeDTO(faultCodeMaster.getFaultCodeId(),
							faultCodeMaster.getUutId(), faultCodeMaster.getOfpConfigId(), faultCodeMaster.getFaultCode(),
							faultCodeMaster.getFaultCodeDescription(), faultCodeMaster.getFalutCodeFilePath());
					faultCodeList.add(faultCodeDTO);
				}
			}
			faultCodeResponse.setMapResponse(response.getExcelResponse());
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
					faultCodeMaster.getUutId(), faultCodeMaster.getOfpConfigId(), faultCodeMasterResponse.getFaultCode(),
					faultCodeMasterResponse.getFaultCodeDescription(), faultCodeMasterResponse.getFalutCodeFilePath());

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

	public FaultCodeResponse faultCodeFile(String path,String uutType,String ofpId) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		try {
			if (path.endsWith(".xlsx")) {
				return extractingFaultCodeExcelFile(path,uutType,ofpId);
			} else if (path.endsWith(".csv")) {
				return extractingFaultCodeCSVFile(path,uutType,ofpId);
			} else {
				res.setResponseCode(0);
				res.setResponseMessage("Please upload an Excel Or CSV file! ");
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Error while importing " + e.getLocalizedMessage());
			faultCodeResponse.setResponse(res);
			return faultCodeResponse;
		}
	}

	private FaultCodeResponse extractingFaultCodeExcelFile(String path,String uutType,String ofpId) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		Map<Integer, String> errors = new HashMap<>();

		try {
//			if (!path.endsWith(".xlsx")) {
//				res.setResponseCode(0);
//				res.setResponseMessage("Please upload an excel file! ");
//				faultCodeResponse.setResponse(res);
//				return faultCodeResponse;
//			}

			InputStream inputStream = new FileInputStream(new File(path));
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();
			List<FaultCodeDTO> listOfFaultCodeDto = new ArrayList<>();

			while (rows.hasNext()) {
				Row currentRow = rows.next();

				if (currentRow.getRowNum() == 0) {

					for (int cellIndex = 0; cellIndex < headers.length; cellIndex++) {
						Cell cell = currentRow.getCell(cellIndex);

						if (cell.getCellType() == CellType.STRING) {
							if (cellIndex == 0 && !cell.getStringCellValue().contains("FaultCode")) {
								res.setResponseMessage("In ROW " + currentRow.getRowNum() + " and CELL "
										+ cell.getColumnIndex() + " " + "value must be FaultCode");
								res.setResponseCode(0);
								workbook.close();
								faultCodeResponse.setResponse(res);
								return faultCodeResponse;
							} else if (cellIndex == 1 && !cell.getStringCellValue().contains("Description")) {
								res.setResponseMessage("In ROW " + currentRow.getRowNum() + " and CELL "
										+ cell.getColumnIndex() + " " + "value must be Description");
								res.setResponseCode(0);
								workbook.close();
								faultCodeResponse.setResponse(res);
								return faultCodeResponse;
							}
						}
					}
					continue;
				}

				FaultCodeDTO faultCodeDto = new FaultCodeDTO();
				/*
				 * Iterator<Cell> cellsInRow = currentRow.iterator(); while
				 * (cellsInRow.hasNext()) { Cell currentCell = cellsInRow.next(); switch
				 * (currentCell.getColumnIndex()) { case 0: if (currentCell.getCellType() !=
				 * CellType.BLANK) {
				 * 
				 * if (currentCell.getCellType() == CellType.NUMERIC) { String b = new
				 * DataFormatter().formatCellValue(currentRow.getCell(0));
				 * faultCodeDto.setFaultCode(Integer.parseInt(String.valueOf(b))); } } break;
				 * 
				 * case 1:
				 * faultCodeDto.setFaultCodeDescription(currentCell.getStringCellValue()); ;
				 * break;
				 * 
				 * default: break; } }
				 */

				boolean hasError = false;
				StringBuilder errorMsg = new StringBuilder();

				for (int cellIndex = 0; cellIndex < 2; cellIndex++) {
					Cell currentCell = currentRow.getCell(cellIndex);

					switch (cellIndex) {
					case 0:
						if (currentCell != null && currentCell.getCellType() != CellType.BLANK) {
							if (currentCell.getCellType() == CellType.NUMERIC
									|| StringUtils.isNumeric(new DataFormatter().formatCellValue(currentCell))) {
								String faultCodeStr = new DataFormatter().formatCellValue(currentCell);
								faultCodeDto.setFaultCode(Integer.parseInt(faultCodeStr));
							} else {
								hasError = true;
								errorMsg.append("Fault code is not a valid number at row ")
										.append(currentRow.getRowNum()).append(". ");
							}
						} else {
							hasError = true;
							errorMsg.append("Fault code is missing at row ").append(currentRow.getRowNum())
									.append(". ");
						}
						break;

					case 1:
						if (currentCell != null && currentCell.getCellType() == CellType.STRING
								&& StringUtils.isNotBlank(currentCell.getStringCellValue())) {
							faultCodeDto.setFaultCodeDescription(currentCell.getStringCellValue());
						} else {
							hasError = true;
							errorMsg.append("Fault code description is empty or not a string at row ")
									.append(currentRow.getRowNum()).append(". ");
						}
						break;

					default:
						break;
					}
				}

				if (hasError) {
					errors.put(currentRow.getRowNum(), errorMsg.toString());
				} else {
					listOfFaultCodeDto.add(faultCodeDto);
				}

//				listOfFaultCodeDto.add(faultCodeDto);
			}
			// If there are errors, set the response accordingly
			if (!errors.isEmpty()) {
				res.setResponseCode(0);
				res.setResponseMessage("Errors found during Excel import.");
				faultCodeResponse.setResponse(res);
				faultCodeResponse.setMapResponse(errors);
				workbook.close();
				return faultCodeResponse;
			}
			faultCodeResponse = addListFaultCode(listOfFaultCodeDto, path,uutType,ofpId);
			workbook.close();

		} catch (ConstraintViolationException ex) {
			Debug.printDebug("Exception is handled.............");
		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Error while importing " + e.getLocalizedMessage());
			faultCodeResponse.setResponse(res);
			return faultCodeResponse;
		}

		return faultCodeResponse;
	}

	private FaultCodeResponse extractingFaultCodeCSVFile(String path,String uutType,String ofpId) {
		FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
		Response res = new Response();
		Map<Integer, String> errors = new HashMap<>();
		try {
//			if (!path.endsWith(".csv")) {
//				res.setResponseCode(0);
//				res.setResponseMessage("Please upload an CSV file! ");
//				faultCodeResponse.setResponse(res);
//				return faultCodeResponse;
//			}
			CSVReader reader = new CSVReader(new FileReader(path));
			int lineNumber = 0;

			String[] line;
			// Check the header
			if ((line = reader.readNext()) != null) {
				lineNumber++;
				if (line.length < 2 || !line[0].equalsIgnoreCase("FaultCode")
						|| !line[1].equalsIgnoreCase("Description")) {
					res.setResponseCode(0);
					res.setResponseMessage("CSV header must be 'FaultCode' and 'Description'");
					errors.put(lineNumber, "Line " + lineNumber + "CSV header must be 'faultCode' and 'Description'");
					faultCodeResponse.setResponse(res);
					faultCodeResponse.setMapResponse(errors);
					return faultCodeResponse;
				}
			} else {
				res.setResponseCode(0);
				res.setResponseMessage("CSV file is empty");
				faultCodeResponse.setResponse(res);
				return faultCodeResponse;
			}
			List<FaultCodeDTO> listOfFaultCodeDto = new ArrayList<>();
			while ((line = reader.readNext()) != null) {
				lineNumber++;
				// Check for empty lines
				if (line.length == 0) {
					continue; // skip empty lines
				}
				FaultCodeDTO faultCodeDto = new FaultCodeDTO();

				boolean hasError = false;
				StringBuilder errorMsg = new StringBuilder();

				if (line.length < 2) {
					errors.put(lineNumber, "Line " + lineNumber + " is missing fields.");
					continue;
				}
				// Validate fault code
				if (StringUtils.isNumeric(line[0])) {
					faultCodeDto.setFaultCode(Integer.parseInt(line[0]));
				} else {
					hasError = true;
					errorMsg.append("Fault code is not a valid number at line ").append(lineNumber).append(". ");
				}

				// Validate fault code description
				if (StringUtils.isNotBlank(line[1])) {
					faultCodeDto.setFaultCodeDescription(line[1]);
				} else {
					hasError = true;
					errorMsg.append("Fault code description is empty at line ").append(lineNumber).append(". ");
				}
				faultCodeDto.setOfpConfigId(ofpId);
				faultCodeDto.setUutId(uutType);
				
				if (hasError) {
					errors.put(lineNumber, errorMsg.toString());
				} else {
					listOfFaultCodeDto.add(faultCodeDto);
				}
			}
			// If there are errors, set the response accordingly
			if (!errors.isEmpty()) {
				res.setResponseCode(0);
				res.setResponseMessage("Errors found during CSV import.");
				faultCodeResponse.setResponse(res);
				faultCodeResponse.setMapResponse(errors);
				return faultCodeResponse;
			}
			faultCodeResponse = addListFaultCode(listOfFaultCodeDto, path,uutType,ofpId);

		} catch (Exception e) {
			res.setResponseCode(0);
			res.setResponseMessage("Error while importing " + e.getLocalizedMessage());
			faultCodeResponse.setResponse(res);
			e.printStackTrace();
			return faultCodeResponse;
		}

		return faultCodeResponse;
	}
}