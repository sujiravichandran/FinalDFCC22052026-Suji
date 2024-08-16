package com.teclever.dfcc.datastore.logbookmanagement;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.teclever.datastore.entities.UUTLogBook;
import com.teclever.datastore.response.UUTLogBookResponse;
import com.teclever.datastore.service.UUTLogBookService;
import com.teclever.dfcc.datastore.dto.UUTLogBookDto;

public class UUTLogbookManagement {

	// ADD -- UUT LOGBOOK
	public UUTLogBookResponse addUUTLogBook(UUTLogBookDto uutLogBookDto) {
		UUTLogBookService service = new UUTLogBookService();

		UUTLogBook uutLogBook = new UUTLogBook();

		uutLogBook.setLogId(uutLogBookDto.getLogId());
		uutLogBook.setUutId(uutLogBookDto.getUutId());
		uutLogBook.setUutSerialNumber(uutLogBookDto.getUutSerialNumber());
		uutLogBook.setSessionId(uutLogBookDto.getSessionId());
		uutLogBook.setUsername(uutLogBookDto.getUsername());
		uutLogBook.setDetails(uutLogBookDto.getDetails());
		uutLogBook.setTimestamp(new Date());

		UUTLogBookResponse serviceResponse = service.addUUTLogBook(uutLogBook);
		return serviceResponse;
	}

	// GET -- UUT LOGBOOK BY UUT ID
	public List<UUTLogBookDto> getUUTLogBooks(String uutId) {
		UUTLogBookService service = new UUTLogBookService();
		UUTLogBookResponse response = service.getUUTLogBooksByUUTId(uutId);
		List<UUTLogBookDto> dtoList = new ArrayList<>();

		if (response.getResponseCode() == 1) {
			List<UUTLogBook> logBooks = response.getLogBooks();

			for (UUTLogBook logBook : logBooks) {
				UUTLogBookDto dto = new UUTLogBookDto();
				dto.setLogId(logBook.getLogId());
				dto.setUutId(logBook.getUutId());
				dto.setUutSerialNumber(logBook.getUutSerialNumber());
				dto.setSessionId(logBook.getSessionId());
				dto.setUsername(logBook.getUsername());
				dto.setTimestamp(logBook.getTimestamp());
				dto.setDetails(logBook.getDetails());

				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch UUT log books: " + response.getResponseMessage());
		}

		return dtoList;
	}

	// GET -- UUT LOGBOOK BY UUT ID AND SERIAL NUMBER
	public List<UUTLogBookDto> getUUTLogBooksByUUTIdAndSerialNo(String uutId, String uutSerialNumber) {
		UUTLogBookService service = new UUTLogBookService();
		UUTLogBookResponse response = service.getUUTLogBooksByUUTIdAndSerialNo(uutId, uutSerialNumber);
		List<UUTLogBookDto> dtoList = new ArrayList<>();

		if (response.getResponseCode() == 1) {
			List<UUTLogBook> logBooks = response.getLogBooks();

			for (UUTLogBook logBook : logBooks) {
				UUTLogBookDto dto = new UUTLogBookDto();
				dto.setLogId(logBook.getLogId());
				dto.setUutId(logBook.getUutId());
				dto.setUutSerialNumber(logBook.getUutSerialNumber());
				dto.setSessionId(logBook.getSessionId());
				dto.setUsername(logBook.getUsername());
				dto.setTimestamp(logBook.getTimestamp());
				dto.setDetails(logBook.getDetails());

				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch UUT log books: " + response.getResponseMessage());
		}

		return dtoList;
	}

	// GET -- UUT LOGBOOK BY UUT ID AND SERIAL NUMBER AND SESSIONID
	public List<UUTLogBookDto> getUUTLogBooks(String uutId, String uutSerialNumber, String sessionId) {
		UUTLogBookService service = new UUTLogBookService();
		UUTLogBookResponse response = service.getUUTLogBooksByUUTIdAndSerialNoAndSessionId(uutId, uutSerialNumber,
				sessionId);
		List<UUTLogBookDto> dtoList = new ArrayList<>();

		if (response.getResponseCode() == 1) {
			List<UUTLogBook> logBooks = response.getLogBooks();

			for (UUTLogBook logBook : logBooks) {
				UUTLogBookDto dto = new UUTLogBookDto();
				dto.setLogId(logBook.getLogId());
				dto.setUutId(logBook.getUutId());
				dto.setUutSerialNumber(logBook.getUutSerialNumber());
				dto.setSessionId(logBook.getSessionId());
				dto.setUsername(logBook.getUsername());
				dto.setTimestamp(logBook.getTimestamp());
				dto.setDetails(logBook.getDetails());

				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch UUT log books: " + response.getResponseMessage());
		}

		return dtoList;
	}

	public List<UUTLogBookDto> getUUTLogBooksByDate(String uutId, String uutSerialNumber, String sessionId,
			Date fromDate, Date toDate) {
		UUTLogBookService service = new UUTLogBookService();
		UUTLogBookResponse response = service.getUUTLogBooksByUUTIdAndSerialNumberAndSessionIdAndDateRange(uutId,
				uutSerialNumber, sessionId, fromDate, toDate);
		List<UUTLogBookDto> dtoList = new ArrayList<>();

		if (response.getResponseCode() == 1) {
			List<UUTLogBook> uutLogBooks = response.getLogBooks();

			for (UUTLogBook uutLogBook : uutLogBooks) {
				UUTLogBookDto dto = new UUTLogBookDto();
				dto.setLogId(uutLogBook.getLogId());
				dto.setUutId(uutLogBook.getUutId());
				dto.setUutSerialNumber(uutLogBook.getUutSerialNumber());
				dto.setSessionId(uutLogBook.getSessionId());
				dto.setUsername(uutLogBook.getUsername());
				dto.setTimestamp(uutLogBook.getTimestamp());
				dto.setDetails(uutLogBook.getDetails());

				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch UUT log books: " + response.getResponseMessage());
		}

		return dtoList;
	}

}
