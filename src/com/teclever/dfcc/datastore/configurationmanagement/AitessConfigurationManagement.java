package com.teclever.dfcc.datastore.configurationmanagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.AitessConfiguration;
import com.teclever.datastore.entities.CardDetails;
import com.teclever.datastore.response.AitessConfigurationResponse;
import com.teclever.datastore.response.AitessDriverResponse;
import com.teclever.datastore.response.UUTMasterDetailsServiceResponse;
import com.teclever.datastore.service.AitessConfigurationService;
import com.teclever.datastore.service.CardDetailsService;
import com.teclever.datastore.service.UUTMasterDetailsService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.datastore.dto.AitessConfigurationDto;
import com.teclever.dfcc.datastore.dto.AitessDriverDto;
import com.teclever.dfcc.datastore.dto.CardDetailsDTO;
import com.teclever.dfcc.datastore.dto.CardDetailsResponseDTO;
import com.teclever.dfcc.datastore.dto.UUTMasterDetailsDto;

public class AitessConfigurationManagement {

	// API : GET ALL UUT
	public UUTMasterDetailsDto[] getAllUUT() {
		UUTMasterDetailsService service = new UUTMasterDetailsService();
		UUTMasterDetailsServiceResponse serviceResponse = service.getAllUutDetails();
		UUTMasterDetailsDto[] dtoArray = new UUTMasterDetailsDto[0];

		if (serviceResponse.getResponseCode() == 1) {
			String[][] data = serviceResponse.getData();
			dtoArray = new UUTMasterDetailsDto[data.length];

			for (int i = 0; i < data.length; i++) {
				String uutId = data[i][0];
				String uutType = data[i][1];
				UUTMasterDetailsDto dto = new UUTMasterDetailsDto(uutType);
				dto.setUutId(uutId);
				dtoArray[i] = dto;
			}
		}
		return dtoArray;
	}

	// API : ADD AITESS CONFIG
	public AitessConfigurationResponse addAitessConfig(AitessConfigurationDto aitessConfigurationDto) {
		AitessConfigurationService service = new AitessConfigurationService();

		AitessConfiguration aitessConfiguration = new AitessConfiguration();
		aitessConfiguration.setAitessId(aitessConfigurationDto.getAitessId());
		aitessConfiguration.setAitessName(aitessConfigurationDto.getAitessName());
		aitessConfiguration.setAitessCommand(aitessConfigurationDto.getAitessCommand());
		aitessConfiguration.setAitessVersion(aitessConfigurationDto.getAitessVersion());
		aitessConfiguration.setDriverName(aitessConfigurationDto.getDriverName());
		aitessConfiguration.setLoadDriverCommand(aitessConfigurationDto.getLoadDriverCommand());
		aitessConfiguration.setUnloadDriverCommand(aitessConfigurationDto.getUnloadDriverCommand());
		aitessConfiguration.setDeleteStatus(aitessConfigurationDto.isDeleteStatus());

		AitessConfigurationResponse serviceResponse = service.addAitessConfiguration(aitessConfiguration);
		return serviceResponse;
	}

	// API : GET AITESS CONFIG LIST
	public List<AitessConfigurationDto> getAitessConfig() {
		AitessConfigurationService service = new AitessConfigurationService();
		AitessConfigurationResponse serviceResponse = service.getAllAitessConfiguration();

		List<AitessConfigurationDto> dtoList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<AitessConfiguration> configurationList = serviceResponse.getConfigurations();

			for (AitessConfiguration configuration : configurationList) {
				AitessConfigurationDto dto = new AitessConfigurationDto();
				dto.setAitessId(configuration.getAitessId());
				dto.setAitessName(configuration.getAitessName());
				dto.setAitessCommand(configuration.getAitessCommand());
				dto.setAitessVersion(configuration.getAitessVersion());
				dto.setDriverName(configuration.getDriverName());
				dto.setLoadDriverCommand(configuration.getLoadDriverCommand());
				dto.setUnloadDriverCommand(configuration.getUnloadDriverCommand());

				dtoList.add(dto);
			}
		} else {
			System.err.println("Failed to fetch Aitess configurations: " + serviceResponse.getResponseMessage());
		}

		return dtoList;
	}

	// API : DELETE AITESS CONFIG
	public AitessConfigurationDto[] deleteAitessConfig(int aitessId) {
		AitessConfigurationService service = new AitessConfigurationService();
		AitessConfigurationResponse serviceResponse = service.removeAitessConfiguration(aitessId);

		AitessConfigurationDto[] dtoArray = new AitessConfigurationDto[0];

		if (serviceResponse.getResponseCode() == 1) {
			List<AitessConfiguration> configurationList = serviceResponse.getConfigurations();

			if (configurationList != null) {
				List<AitessConfigurationDto> dtoList = new ArrayList<>();

				for (AitessConfiguration configuration : configurationList) {
					AitessConfigurationDto dto = new AitessConfigurationDto();
					dto.setAitessId(configuration.getAitessId());
					dto.setAitessName(configuration.getAitessName());
					dto.setAitessCommand(configuration.getAitessCommand());
					dto.setAitessVersion(configuration.getAitessVersion());
					dto.setDriverName(configuration.getDriverName());
					dto.setLoadDriverCommand(configuration.getLoadDriverCommand());
					dto.setUnloadDriverCommand(configuration.getUnloadDriverCommand());

					dto.setDeleteStatus(configuration.isDeleteStatus());
					dtoList.add(dto);
				}

				dtoArray = dtoList.toArray(new AitessConfigurationDto[0]);
			}
		} else {
			System.err.println("Failed to remove Aitess configuration: " + serviceResponse.getResponseMessage());
		}

		return dtoArray;
	}

	// API : GET AITESS NAME AND DRIVER NAME BASED ON UUT ID IN RUN CONFIGURATION
	public List<AitessDriverDto> getAitessAndDriverName(String aitessName1) {
		AitessConfigurationService service = new AitessConfigurationService();
		AitessDriverResponse serviceResponse = service.extractAitessAndDriverNamesByAitessName(aitessName1);

		List<AitessDriverDto> resultList = new ArrayList<>();

		if (serviceResponse.getResponseCode() == 1) {
			List<Object[]> aitessAndDriverNames = serviceResponse.getAitessAndDriverNames();

			for (Object[] names : aitessAndDriverNames) {
				String aitessName = (String) names[0];
				String driverName = (String) names[1];
				AitessDriverDto dto = new AitessDriverDto(aitessName, driverName);
				resultList.add(dto);
			}
		} else {
			System.err.println("Failed to extract Aitess and Driver Names: " + serviceResponse.getResponseMessage());
		}

		return resultList;
	}

	
	
	
	
	// Card Details API's
	// To Get The Driver Name For The Selected UUTID
//	public Map<Integer, Map<String, String>> getAitessIdDriverName(String uutId) {
//		Map<Integer, Map<String, String>> aitessIdDriverDetails = new HashMap<Integer, Map<String, String>>();
//		AitessConfigurationService service = new AitessConfigurationService();
//
//		AitessConfigurationResponse res = service.getAllAitessConfigurationByUutId(uutId);
//		List<AitessConfiguration> aitessList = new ArrayList();
//		aitessList = res.getConfigurations();
//		for (AitessConfiguration aitessConfiguration : aitessList) {
//			Map<String, String> driverDetails = new HashMap<String, String>();
//			driverDetails.put("driverName", aitessConfiguration.getDriverName());
//			driverDetails.put("driverVersion", aitessConfiguration.getDriverVersion());
//			aitessIdDriverDetails.put(aitessConfiguration.getAitessId(), driverDetails);
//		}
//		return aitessIdDriverDetails;
//	}
	
	
	
	

	// Card Details For Selected Driver Alis aitessId
	public CardDetailsResponseDTO getCardDetailsByAitessId(int aitessId) {
		CardDetailsResponseDTO response = new CardDetailsResponseDTO();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();
			GetResponse res = cardDetailsService.getCardDetailsByAitessId(aitessId);
			List<CardDetails> cardList = new ArrayList();
			cardList = (List<CardDetails>) res.getResponseList();
			List<CardDetailsDTO> cardDTOList = new ArrayList<CardDetailsDTO>();
			for (CardDetails cardDetails : cardList) {
				CardDetailsDTO cardDetailsDTO = new CardDetailsDTO();
				cardDetailsDTO.setCardName(cardDetails.getCardName());
				cardDetailsDTO.setAitessId(cardDetails.getAitessId());
				cardDetailsDTO.setCardDetailsId(cardDetails.getCardDetailsId());
				cardDetailsDTO.setCardIdentificationText(cardDetails.getCardIdentificationText());
				cardDetailsDTO.setTotalNumberOfCards(cardDetails.getTotalNumberOfCards());
				cardDTOList.add(cardDetailsDTO);
			}
			response.setCardLst(cardDTOList);
			response.setCode(1);
			response.setMsg("Fetched" + res.getMsg());
			if (cardList.size() > 0) {
				response.seteMsg(res.geteMsg());
			}

		} catch (Exception ex) {
			response.setCode(0);
			response.setMsg("Not Fetched");
			response.seteMsg(ex.getLocalizedMessage());
		}
		return response;
	}

	// Multiple Save
	public Response saveCardConfigDetails(List<CardDetailsDTO> lst) {
		Response response = new Response();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();
			CardDetailsDTO cardDetailsDTO = new CardDetailsDTO();
			List<CardDetails> cardDetailsList = new ArrayList<CardDetails>();
			for (CardDetailsDTO card : lst) {
				CardDetails cardDetails = new CardDetails();
				cardDetails.setAitessId(cardDetailsDTO.getAitessId());
				cardDetails.setCardIdentificationText(cardDetailsDTO.getCardIdentificationText());
				cardDetails.setDeleteStatus(false);
				cardDetails.setTotalNumberOfCards(cardDetailsDTO.getTotalNumberOfCards());
				cardDetailsList.add(cardDetails);
			}
			if (cardDetailsList.size() > 0) {
				response = cardDetailsService.addCardDetails(cardDetailsList);
			}
		} catch (Exception ex) {
			response.setResponseCode(0);
			response.setResponseMessage("Not Added");
		}
		return response;
	}

	// Single Save
	public Response saveCardConfigDetail(CardDetailsDTO cardDetailsDTO) {
		Response response = new Response();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();

			CardDetails cardDetails = new CardDetails();
			cardDetails.setAitessId(cardDetailsDTO.getAitessId());
			cardDetails.setCardIdentificationText(cardDetailsDTO.getCardIdentificationText());
			cardDetails.setDeleteStatus(false);
			cardDetails.setCardName(cardDetailsDTO.getCardName());
			cardDetails.setTotalNumberOfCards(cardDetailsDTO.getTotalNumberOfCards());
			response = cardDetailsService.addCardDetail(cardDetails);

		} catch (Exception ex) {
			response.setResponseCode(0);
			response.setResponseMessage("Not Added");
		}
		return response;
	}

	// Multiple Update
	public Response updateCardConfigDetails(List<CardDetailsDTO> lst) {
		Response response = new Response();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();
			CardDetailsDTO cardDetailsDTO = new CardDetailsDTO();
			List<CardDetails> cardDetailsList = new ArrayList<CardDetails>();
			for (CardDetailsDTO card : lst) {
				CardDetails cardDetails = new CardDetails();
				cardDetails.setCardDetailsId(cardDetailsDTO.getCardDetailsId());
				cardDetails.setAitessId(cardDetailsDTO.getAitessId());
				cardDetails.setCardIdentificationText(cardDetailsDTO.getCardIdentificationText());
				cardDetails.setDeleteStatus(false);
				cardDetails.setTotalNumberOfCards(cardDetailsDTO.getTotalNumberOfCards());
				cardDetailsList.add(cardDetails);
			}
			if (cardDetailsList.size() > 0) {
				response = cardDetailsService.updateCardDetails(cardDetailsList);
			}
		} catch (Exception ex) {
			response.setResponseCode(0);
			response.setResponseMessage("Not Updated");
		}
		return response;
	}

	// Single Update
	public Response updateCardConfigDetail(CardDetailsDTO cardDetailsDTO) {
		Response response = new Response();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();

			CardDetails cardDetails = new CardDetails();
			cardDetails.setCardDetailsId(cardDetailsDTO.getCardDetailsId());
			cardDetails.setCardName(cardDetailsDTO.getCardName());
			cardDetails.setAitessId(cardDetailsDTO.getAitessId());
			cardDetails.setCardIdentificationText(cardDetailsDTO.getCardIdentificationText());
			cardDetails.setDeleteStatus(false);
			cardDetails.setTotalNumberOfCards(cardDetailsDTO.getTotalNumberOfCards());
			response =cardDetailsService.updateCardDetail(cardDetails);
		} catch (Exception ex) {
			response.setResponseCode(0);
			response.setResponseMessage("Not Updated");
		}
		return response;
	}

	// Delete Card Details By cardDetailsId
	public Response deleteCardDetails(int cardDetailsId) {
		Response res = new Response();
		try {
			CardDetailsService cardDetailsService = new CardDetailsService();
			res = cardDetailsService.removeCardDetails(cardDetailsId);
		} catch (Exception ex) {
			res.setResponseCode(0);
			res.setResponseMessage("Not Deleted" + ex.getLocalizedMessage());
		}
		return res;
	}



}
