package com.teclever.dfcc.datastore.configurationmanagement;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.entities.MacroButtonMap;
import com.teclever.datastore.entities.MacroButtonMasterDetails;
import com.teclever.datastore.response.ButtonNamesResponse;
import com.teclever.datastore.response.MacroButtonMapResponse;
import com.teclever.datastore.response.MacroButtonMasterDetailsResponse;
import com.teclever.datastore.service.MacroButtonMapService;
import com.teclever.datastore.service.MacroButtonMasterDetailsService;
import com.teclever.dfcc.datastore.dto.MacroButtonMapDto;
import com.teclever.dfcc.datastore.dto.MacroButtonMasterDetailsDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class MacroConfigurationManagement {

	//API GET MACRO BUTTON LIST BASED ON UUT ID
    public List<MacroButtonMasterDetailsDto> getAllMacroButtonsByUutId(String uutId) {
        List<MacroButtonMasterDetailsDto> buttonDtoList = new ArrayList<>();
        MacroButtonMasterDetailsService masterDetailsService = new MacroButtonMasterDetailsService();
        MacroButtonMasterDetailsResponse response = masterDetailsService.getButtonsByUutId(uutId);
        
        if (response.getResponseCode() == 1) {
            for (MacroButtonMasterDetails button : response.getMacroButtons()) {
                MacroButtonMasterDetailsDto buttonDto = new MacroButtonMasterDetailsDto();
                buttonDto.setButtonNumber(button.getButtonNumber());
                buttonDto.setButtonId(button.getButtonId());
                buttonDto.setUutId(button.getUutId());
                buttonDtoList.add(buttonDto);
            }
        } else {
            System.out.println("Error: " + response.getResponseMessage());
        }

        return buttonDtoList;
    }
    
    
    //API : UPDATING THE MACRO BUTTON (ENTERING BUTTON NAME AND COMMAND)
    public List<MacroButtonMapDto> updateMacroButtonMap(List<MacroButtonMapDto> buttonMapDtoList) {
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

        List<MacroButtonMapDto> updatedButtonMapDtoList = new ArrayList<>();
        for (MacroButtonMap buttonMap : response.getButtonMapList()) {
            MacroButtonMapDto buttonMapDto = new MacroButtonMapDto();
            buttonMapDto.setButtonId(buttonMap.getButtonId());
            buttonMapDto.setButtonNumber(buttonMap.getButtonNumber());
            buttonMapDto.setButtonName(buttonMap.getButtonName());
            buttonMapDto.setCommand(buttonMap.getCommand());
            buttonMapDto.setUutId(buttonMap.getUutId());
            updatedButtonMapDtoList.add(buttonMapDto);
        }

        return updatedButtonMapDtoList;
    }
    
    // GET BUTTON NAMES BY UUTID
    public ButtonNamesResponse getButtonNamesByUutId(String uutId) {
        ButtonNamesResponse response = new ButtonNamesResponse();
        List<String> buttonNames = new ArrayList<>();
        try {
            SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
            try (Session session = sessionFactory.openSession()) {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
                Root<MacroButtonMap> root = criteriaQuery.from(MacroButtonMap.class);

                criteriaQuery.select(root.get("buttonName"));
                criteriaQuery.where(criteriaBuilder.equal(root.get("uutId"), uutId),
                        criteriaBuilder.notEqual(root.get("buttonName"), "<NOT SET>"));

                Query<String> query = session.createQuery(criteriaQuery);
                buttonNames = query.getResultList();
            }
            response.setButtonNames(buttonNames);
            response.setResponseCode(1);
            response.setResponseMessage("Button names retrieved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(0);
            response.setResponseMessage("Failed to retrieve button names: " + e.getMessage());
        }

        return response;
    }

    
}

