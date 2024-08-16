package com.teclever.dfcc.datastore.logbookmanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.teclever.datastore.entities.ApplicationLogBook;
import com.teclever.datastore.response.ApplicationLogBookResponse;
import com.teclever.datastore.service.ApplicationLogBookService;
import com.teclever.dfcc.datastore.dto.ApplicationLogBookDto;

public class ApplicationLogbookManagement {

	
	//ADD -- APPLICATION LOGBOOK
	public ApplicationLogBookResponse addApplicationLogBook(ApplicationLogBookDto applicationLogBookDto) {
		ApplicationLogBookService service = new ApplicationLogBookService();

		ApplicationLogBook applicationLogBook = new ApplicationLogBook();
		
		applicationLogBook.setAppLogId(applicationLogBookDto.getAppLogId());
		applicationLogBook.setUutId(applicationLogBookDto.getUutId());
		applicationLogBook.setUutSerialNumber(applicationLogBookDto.getUutSerialNumber());
		applicationLogBook.setSessionId(applicationLogBookDto.getSessionId());
		applicationLogBook.setUsername(applicationLogBookDto.getUsername());
		applicationLogBook.setDetails(applicationLogBookDto.getDetails());
		applicationLogBook.setTimestamp(new Date());
		
		ApplicationLogBookResponse serviceResponse = service.addApplicationLogBook(applicationLogBook);
		return serviceResponse;
	}
	
	//GET -- APPLICATION LOGBOOK BY UUT ID
	public List<ApplicationLogBookDto> getApplicationLogBooksByUUTId(String uutId) {
		ApplicationLogBookService service = new ApplicationLogBookService();
		ApplicationLogBookResponse response = service.getApplicationLogBooksByUUTId(uutId);
        List<ApplicationLogBookDto> dtoList = new ArrayList<>();

        if (response.getResponseCode() == 1) {
            List<ApplicationLogBook> applicationlogBooks = response.getApplicationLogBooks();

            for (ApplicationLogBook appLogBook : applicationlogBooks) {
                ApplicationLogBookDto dto = new ApplicationLogBookDto();
                dto.setAppLogId(appLogBook.getAppLogId());
                dto.setUutId(appLogBook.getUutId());
                dto.setUutSerialNumber(appLogBook.getUutSerialNumber());
                dto.setSessionId(appLogBook.getSessionId());
                dto.setUsername(appLogBook.getUsername());
                dto.setTimestamp(appLogBook.getTimestamp());
                dto.setDetails(appLogBook.getDetails());

                dtoList.add(dto);
            }
        } else {
            System.err.println("Failed to fetch Application log books: " + response.getResponseMessage());
        }

        return dtoList;
    }
	
	
	//GET -  APPLICATION LOG BOOKS BY UUT ID AND SERIAL NUMBER
	public List<ApplicationLogBookDto> getApplicationLogBooksByUUTIdAndSerialNo(String uutId, String uutSerialNumber) {
		ApplicationLogBookService service = new ApplicationLogBookService();
		ApplicationLogBookResponse response = service.getApplicationLogBooksByUUTIdAndUUTSerialNo(uutId, uutSerialNumber);
        List<ApplicationLogBookDto> dtoList = new ArrayList<>();

        if (response.getResponseCode() == 1) {
            List<ApplicationLogBook> applicationlogBooks = response.getApplicationLogBooks();

            for (ApplicationLogBook appLogBook : applicationlogBooks) {
                ApplicationLogBookDto dto = new ApplicationLogBookDto();
                dto.setAppLogId(appLogBook.getAppLogId());
                dto.setUutId(appLogBook.getUutId());
                dto.setUutSerialNumber(appLogBook.getUutSerialNumber());
                dto.setSessionId(appLogBook.getSessionId());
                dto.setUsername(appLogBook.getUsername());
                dto.setTimestamp(appLogBook.getTimestamp());
                dto.setDetails(appLogBook.getDetails());

                dtoList.add(dto);
            }
        } else {
            System.err.println("Failed to fetch Application log books: " + response.getResponseMessage());
        }

        return dtoList;
    }
	
	
	
	//GET -  APPLICATION LOG BOOKS BY UUT ID AND SERIAL NUMBER AND SESSION ID
	public List<ApplicationLogBookDto> getApplicationLogBooksByUUTIdAndSerialNoAndSessionId(String uutId, String uutSerialNumber,String sessionId) {
		ApplicationLogBookService service = new ApplicationLogBookService();
		ApplicationLogBookResponse response = service.getApplicationLogBooksByUUTIdAndSerialNumberAndsessionId(uutId, uutSerialNumber,sessionId);
        List<ApplicationLogBookDto> dtoList = new ArrayList<>();

        if (response.getResponseCode() == 1) {
            List<ApplicationLogBook> applicationlogBooks = response.getApplicationLogBooks();

            for (ApplicationLogBook appLogBook : applicationlogBooks) {
                ApplicationLogBookDto dto = new ApplicationLogBookDto();
                dto.setAppLogId(appLogBook.getAppLogId());
                dto.setUutId(appLogBook.getUutId());
                dto.setUutSerialNumber(appLogBook.getUutSerialNumber());
                dto.setSessionId(appLogBook.getSessionId());
                dto.setUsername(appLogBook.getUsername());
                dto.setTimestamp(appLogBook.getTimestamp());
                dto.setDetails(appLogBook.getDetails());

                dtoList.add(dto);
            }
        } else {
            System.err.println("Failed to fetch Application log books: " + response.getResponseMessage());
        }

        return dtoList;
    }
	
	//GET -  APPLICATION LOG BOOKS BY UUT ID AND SERIAL NUMBER AND SESSION ID AND DATE
		public List<ApplicationLogBookDto> getApplicationLogBooksByDate(String uutId, String uutSerialNumber,String sessionId,Date fromDate, Date toDate) {
			ApplicationLogBookService service = new ApplicationLogBookService();
			ApplicationLogBookResponse response = service.getApplicationLogBooksByUUTIdAndSerialNumberAndSessionIdAndDateRange(uutId, uutSerialNumber,sessionId,fromDate,toDate);
	        List<ApplicationLogBookDto> dtoList = new ArrayList<>();

	        if (response.getResponseCode() == 1) {
	            List<ApplicationLogBook> applicationlogBooks = response.getApplicationLogBooks();

	            for (ApplicationLogBook appLogBook : applicationlogBooks) {
	                ApplicationLogBookDto dto = new ApplicationLogBookDto();
	                dto.setAppLogId(appLogBook.getAppLogId());
	                dto.setUutId(appLogBook.getUutId());
	                dto.setUutSerialNumber(appLogBook.getUutSerialNumber());
	                dto.setSessionId(appLogBook.getSessionId());
	                dto.setUsername(appLogBook.getUsername());
	                dto.setTimestamp(appLogBook.getTimestamp());
	                dto.setDetails(appLogBook.getDetails());

	                dtoList.add(dto);
	            }
	        } else {
	            System.err.println("Failed to fetch Application log books: " + response.getResponseMessage());
	        }

	        return dtoList;
	    }
	
}
