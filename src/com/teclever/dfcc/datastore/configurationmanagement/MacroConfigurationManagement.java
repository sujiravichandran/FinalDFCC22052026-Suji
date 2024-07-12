package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.ButtonInfo;
import com.teclever.datastore.entities.MacroButtonMap;
import com.teclever.datastore.response.ButtonNamesResponse;
import com.teclever.datastore.response.MacroButtonMapResponse;
import com.teclever.datastore.service.MacroButtonMapService;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class MacroConfigurationManagement {

    // API GET MACRO BUTTON LIST BASED ON UUT ID
    public List<MacroButtonMapDto> getAllMacroButtonsByUutId(String uutId) {
        List<MacroButtonMapDto> buttonDtoList = new ArrayList<>();
        MacroButtonMapService buttonMapService = new MacroButtonMapService();
        MacroButtonMapResponse response = buttonMapService.getButtonsByUutId(uutId);

        if (response.getResponseCode() == 1) {
            for (MacroButtonMap buttonMap : response.getButtonMapList()) {
                MacroButtonMapDto buttonDto = new MacroButtonMapDto();
                buttonDto.setButtonNumber(buttonMap.getButtonNumber());
                buttonDto.setButtonId(buttonMap.getButtonId());
                buttonDto.setUutId(buttonMap.getUutId());
                buttonDto.setButtonName(buttonMap.getButtonName());
                buttonDto.setCommand(buttonMap.getCommand());
                buttonDtoList.add(buttonDto);
            }
        } else {
            System.out.println("Error: " + response.getResponseMessage());
        }
        return buttonDtoList;
    }

 
    // API: UPDATING THE MACRO BUTTON (ENTERING BUTTON NAME AND COMMAND)
    public MacroButtonMapResponse updateMacroButtonMap(List<MacroButtonMapDto> buttonMapDtoList) {
        MacroButtonMapService buttonMapService = new MacroButtonMapService();
        // Convert DTOs to entities
        List<MacroButtonMap> buttonMapList = new ArrayList<>();
        for (MacroButtonMapDto dto : buttonMapDtoList) {
            MacroButtonMap buttonMap = new MacroButtonMap();
            buttonMap.setButtonId(dto.getButtonId());
            buttonMap.setButtonNumber(dto.getButtonNumber());
            buttonMap.setButtonName(dto.getButtonName());
            buttonMap.setCommand(dto.getCommand());
            buttonMap.setUutId(dto.getUutId());
            buttonMapList.add(buttonMap);
        }
        MacroButtonMapResponse response = buttonMapService.updateButtonDetails(buttonMapList);
        // No need to convert the response, return as is
        return response;
    }

    // GET BUTTON NAMES BY UUTID
    public ButtonNamesResponse getButtonNamesByUutId(String uutId) {
	    ButtonNamesResponse response = new ButtonNamesResponse();
	    List<ButtonInfo> buttonInfos = new ArrayList<>();
	    try {
	        SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
	        try (Session session = sessionFactory.openSession()) {
	            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
	            CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
	            Root<MacroButtonMap> root = criteriaQuery.from(MacroButtonMap.class);

	            criteriaQuery.multiselect(root.get("buttonName"), root.get("command"));
	            criteriaQuery.where(criteriaBuilder.equal(root.get("uutId"), uutId),
	                    criteriaBuilder.notEqual(root.get("buttonName"), "<NOT SET>"));


	            Query<Tuple> query = session.createQuery(criteriaQuery);
	            List<Tuple> results = query.getResultList();
	            for (Tuple tuple : results) {
	                String buttonName = tuple.get(0, String.class);
	                String buttonCommand = tuple.get(1, String.class);
	                buttonInfos.add(new ButtonInfo(buttonName, buttonCommand));
	            }
	        }
	        response.setButtonInfos(buttonInfos);
	        response.setResponseCode(1);
	        response.setResponseMessage("Button names and commands retrieved successfully");
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setResponseCode(0);
	        response.setResponseMessage("Failed to retrieve button names and commands: " + e.getMessage());
	    }

	    return response;
	}

}

