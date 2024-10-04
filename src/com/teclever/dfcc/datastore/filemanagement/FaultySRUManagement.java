package com.teclever.dfcc.datastore.filemanagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.entities.FaultySRU;
import com.teclever.datastore.service.FaultySRUService;
import com.teclever.dfcc.datastore.dto.FaultSRUResponse;
import com.teclever.dfcc.datastore.dto.FaultySRUDto;
import com.teclever.dfcc.datastore.dto.FaultySruResponse;

public class FaultySRUManagement {
	
	
	//SAVE TO DB
	public FaultySruResponse saveDataToDb(String excelFilePath,String uutId) {
		FaultySruResponse response = new FaultySruResponse();
		List<FaultySRU> list = readFaultySRUFromExcel(excelFilePath);
		if(excelFilePath!=null) {
			FaultySRUService c= new FaultySRUService();
			c.saveFaultySRUs(list, uutId);
			response.setResponseCode(1);
			response.setResponseMessage("Success");
			response.setFaultySRUs(list);	
		}else {
			response.setResponseCode(0);
			response.setResponseMessage("Failed");
		}
		return response;
	}
	
	//GET
	public FaultSRUResponse getFaultySRUsByUutId(String uutId) {
        FaultSRUResponse faultySRUResponse = new FaultSRUResponse();
        List<FaultySRUDto> faultySRUDtoList = new ArrayList<>();

        try (Session session = DataStoreConfiguration.getSessionFactory().openSession()) {

            Query<FaultySRU> query = session.createQuery("FROM FaultySRU WHERE uutId = :uutId", FaultySRU.class);
            query.setParameter("uutId", uutId);
            List<FaultySRU> faultySRUList = query.list();

            for (FaultySRU faultySRU : faultySRUList) {
                FaultySRUDto dto = new FaultySRUDto();
                dto.setRdfFileName(faultySRU.getRdfFileName());
                dto.setTpgph(faultySRU.getTpgph());
                dto.setStep(faultySRU.getStep());
                dto.setSignalName(faultySRU.getSignalName());
                dto.setFaultySRU(faultySRU.getFaultySRU());

                faultySRUDtoList.add(dto);
            }

            if (faultySRUDtoList.isEmpty()) {
                faultySRUResponse.setResponseCode(0);
                faultySRUResponse.setResponseMessage("No FaultySRU data found for UUT ID: " + uutId);
            } else {
                faultySRUResponse.setResponseCode(1);
                faultySRUResponse.setResponseMessage("Data fetched successfully");
                faultySRUResponse.setFaultySRUs(faultySRUDtoList); 
            }

        } catch (Exception e) {
            e.printStackTrace();
            faultySRUResponse.setResponseCode(0);
            faultySRUResponse.setResponseMessage("Error fetching data: " + e.getMessage());
        }

        return faultySRUResponse;
    }

	public List<FaultySRU> readFaultySRUFromExcel(String excelFilePath) {
        List<FaultySRU> faultySRUList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(fis)) {

            // Iterate over all sheets in the workbook
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName(); 
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {  
                        continue;
                    }

                    FaultySRU faultySRU = new FaultySRU();

                    faultySRU.setFaultySRU(sheetName);

                    faultySRU.setRdfFileName(row.getCell(1).getStringCellValue());
                    faultySRU.setTpgph(row.getCell(2).getStringCellValue());
                    faultySRU.setStep(row.getCell(3).getStringCellValue());
                    faultySRU.setSignalName(row.getCell(4).getStringCellValue());

                    faultySRUList.add(faultySRU);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return faultySRUList;
    }


}
