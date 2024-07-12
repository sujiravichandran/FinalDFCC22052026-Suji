package com.teclever.dfcc.datastore.filemanagement;

import java.util.ArrayList;
import java.util.List;
import com.teclever.datastore.entities.CardMemory;
import com.teclever.datastore.response.CardMemoryResponse;
import com.teclever.datastore.service.CardMemoryService;
import com.teclever.dfcc.datastore.dto.CardMemoryDto;

public class CardMemoryManagement {

	// SAVE
	public CardMemoryResponse addCardMemory(CardMemoryDto cardMemoryDto) {
		CardMemoryService service = new CardMemoryService();

		CardMemory cardMemory = new CardMemory();
		cardMemory.setId(cardMemoryDto.getId());
		cardMemory.setMemoryType(cardMemoryDto.getMemoryType());
		cardMemory.setStartAddress(cardMemoryDto.getStartAddress());
		cardMemory.setEndAddress(cardMemoryDto.getEndAddress());

		CardMemoryResponse serviceResponse = service.addCardMemory(cardMemory);
		return serviceResponse;
	}

	// GET
	public List<CardMemoryDto> getCardMemory() {
		CardMemoryService service = new CardMemoryService();
		CardMemoryResponse serviceResponse = service.getAllCardMemory();

		List<CardMemoryDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<CardMemory> memoryList = serviceResponse.getMemories();

			for (CardMemory memory : memoryList) {
				CardMemoryDto dto = new CardMemoryDto();
				dto.setId(memory.getId());
				dto.setMemoryType(memory.getMemoryType());
				dto.setStartAddress(memory.getStartAddress());
				dto.setEndAddress(memory.getEndAddress());
				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch Card Memory Details: " + serviceResponse.getResponseMessage());
		}

		return dtoList;
	}

}
