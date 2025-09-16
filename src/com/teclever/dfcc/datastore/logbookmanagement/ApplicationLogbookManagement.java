package com.teclever.dfcc.datastore.logbookmanagement;

import java.text.SimpleDateFormat;
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
	
	
	
	//GET -  APPLICATION LOG BOOKS 
		public List<ApplicationLogBookDto> getApplicationLogBooksByDate(String uutId, String uutSerialNumber,String sessionId,Date fromDate, Date toDate,String username) {
			ApplicationLogBookService service = new ApplicationLogBookService();
			ApplicationLogBookResponse response = service.getApplicationLogBooks(uutId, uutSerialNumber,sessionId,fromDate,toDate,username);
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

					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String formattedTimestamp = formatter.format(appLogBook.getTimestamp());
					System.out.println("Formated Date Time" + formattedTimestamp);
					dto.setFormatedTimeStamp(formattedTimestamp);
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
