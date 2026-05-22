package com.teclever.dfcc.advanceddataanalysis;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.Interface1553B_Mode;
import com.teclever.datastore.entities.RDF1553BCode;
import com.teclever.datastore.service.Interface1553B_ModeService;
import com.teclever.datastore.service.RDF1553BCodeService;
import com.teclever.datastore.utils.GetResponse;
import com.teclever.dfcc.resultstore.dto.StepDto;
import com.teclever.dfcc.stateMachine.StateMachine;

public class DataAnalysis1553_BManagement {
	
	// Add The Required Step For 1553B For An Stage...
	public List<RDF1553BCode> addTheRequiredStepsFor1553(List<StepDto>steps,String stageId)
	{
		List<RDF1553BCode> lst = new ArrayList<RDF1553BCode>();

		try {
			

			Rdf1553ResultFetchingDTO rdf1553ResultFetchingDTO = new Rdf1553ResultFetchingDTO();
			for (StepDto stepDto : steps) {

				if (stepDto.getReadingInfo().size() > 0) {

					String input = stepDto.getReadingInfo().get(0);

					// Remove parentheses and split by comma
					String[] parts = input.replaceAll("[()]", "").split(",");
					String firstElement = parts[0].trim().replaceAll("\\.$", "");
					String secondElement = parts[1].trim().replaceAll("\\.$", "");
					String thirdElement = parts[2].trim().replaceAll("\\.$", "");
					String  fourthElement1= parts[3].trim(); 
					String fourthElement = fourthElement1.split("\\s+")[0].replaceAll("\\.$", "");
					
					
					int number = 0;
					switch (stepDto.getStep()) {

					// Ch-1
					case "6216":

						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}

						rdf1553ResultFetchingDTO.setValue_6216(number);
						break;
					case "6232":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6232(number);
						break;
					case "6248":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6248(number);
						break;
					case "6264":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6264(number);
						break;
					case "6280":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6280(number);
						break;
					case "6296":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6296(number);
						break;
					case "6312":
						if (firstElement.matches("-?\\d+")) {
							number = Integer.parseInt(firstElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("First element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6312(number);
						break;

					// Ch-2
					case "6348":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6348(number);
						break;
					case "6364":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6364(number);
						break;
					case "6380":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6380(number);
						break;
					case "6396":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6396(number);
						break;
					case "6412":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6412(number);
						break;
					case "6428":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6428(number);
						break;
					case "6444":
						if (secondElement.matches("-?\\d+")) {
							number = Integer.parseInt(secondElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Second element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6444(number);
						break;

					// Ch-3
					case "6480":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6480(number);
						break;
					case "6496":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6496(number);
						break;
					case "6512":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6512(number);
						break;
					case "6528":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6528(number);
						break;
					case "6544":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6544(number);
						break;
					case "6560":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6560(number);
						break;
					case "6576":
						if (thirdElement.matches("-?\\d+")) {
							number = Integer.parseInt(thirdElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Third element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6576(number);
						break;

					// Ch-4
					case "6612":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6612(number);
						break;
					case "6628":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6628(number);
						break;
					case "6644":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6644(number);
						break;
					case "6660":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6660(number);
						break;
					case "6676":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6676(number);
						break;
					case "6692":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6692(number);
						break;
					case "6708":
						if (fourthElement.matches("-?\\d+")) {
							number = Integer.parseInt(fourthElement);
							////System.out.println("Number: " + number);
						} else {
							////System.out.println("Fourth element is not a valid number");
						}
						rdf1553ResultFetchingDTO.setValue_6708(number);
						break;

					default:
						////System.out.println("Unknown step: " + stepDto.getStep());
						break;
					}
				}
			}

			// Adding To Database....
			RDF1553BCode ch1RDF1553BCode = new RDF1553BCode();
			RDF1553BCode ch2RDF1553BCode = new RDF1553BCode();
			RDF1553BCode ch3RDF1553BCode = new RDF1553BCode();
			RDF1553BCode ch4RDF1553BCode = new RDF1553BCode();

			// ---------------------- CHANNEL 1 ----------------------
			ch1RDF1553BCode.setChannelNo("ch1");
			ch1RDF1553BCode.setStageId(stageId);
			ch1RDF1553BCode.setSessionId(StateMachine.currentSessionDetails.getSessionId());

			ch1RDF1553BCode.setValueCh_1(rdf1553ResultFetchingDTO.getValue_6216() + "");
			ch1RDF1553BCode.setValueCh_2(rdf1553ResultFetchingDTO.getValue_6232() + "");
			ch1RDF1553BCode.setValueCh_3(rdf1553ResultFetchingDTO.getValue_6248() + "");
			ch1RDF1553BCode.setValueCh_4(rdf1553ResultFetchingDTO.getValue_6264() + "");
			ch1RDF1553BCode.setValueCh_5(rdf1553ResultFetchingDTO.getValue_6280() + "");
			ch1RDF1553BCode.setValueCh_6(rdf1553ResultFetchingDTO.getValue_6296() + "");
			ch1RDF1553BCode.setValueCh_7(rdf1553ResultFetchingDTO.getValue_6312() + "");

			// Differences
//			ch1RDF1553BCode.setDiff_1and2(
//					rdf1553ResultFetchingDTO.getValue_6216() - rdf1553ResultFetchingDTO.getValue_6232() + "");
//			ch1RDF1553BCode.setDiff_2and3(
//					rdf1553ResultFetchingDTO.getValue_6232() - rdf1553ResultFetchingDTO.getValue_6248() + "");
//			ch1RDF1553BCode.setDiff_3and4(
//					rdf1553ResultFetchingDTO.getValue_6248() - rdf1553ResultFetchingDTO.getValue_6264() + "");
//			ch1RDF1553BCode.setDiff_4and5(
//					rdf1553ResultFetchingDTO.getValue_6264() - rdf1553ResultFetchingDTO.getValue_6280() + "");
//			ch1RDF1553BCode.setDiff_5and6(
//					rdf1553ResultFetchingDTO.getValue_6280() - rdf1553ResultFetchingDTO.getValue_6296() + "");
//			ch1RDF1553BCode.setDiff_6and7(
//					rdf1553ResultFetchingDTO.getValue_6296() - rdf1553ResultFetchingDTO.getValue_6312() + "");
			
			
			ch1RDF1553BCode.setDiff_1and2(
					 rdf1553ResultFetchingDTO.getValue_6232()-rdf1553ResultFetchingDTO.getValue_6216() + "");
			ch1RDF1553BCode.setDiff_2and3(
					 rdf1553ResultFetchingDTO.getValue_6248()-rdf1553ResultFetchingDTO.getValue_6232() + "");
			ch1RDF1553BCode.setDiff_3and4(
					 rdf1553ResultFetchingDTO.getValue_6264()-rdf1553ResultFetchingDTO.getValue_6248() + "");
			ch1RDF1553BCode.setDiff_4and5(
					 rdf1553ResultFetchingDTO.getValue_6280()-rdf1553ResultFetchingDTO.getValue_6264() + "");
			ch1RDF1553BCode.setDiff_5and6(
					 rdf1553ResultFetchingDTO.getValue_6296()-rdf1553ResultFetchingDTO.getValue_6280() + "");
			ch1RDF1553BCode.setDiff_6and7(
					rdf1553ResultFetchingDTO.getValue_6312()-rdf1553ResultFetchingDTO.getValue_6296() + "");
			
			
			
			

			// PASS / FAIL
			ch1RDF1553BCode.setRes_1and2(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6216() - rdf1553ResultFetchingDTO.getValue_6232()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6216() - rdf1553ResultFetchingDTO.getValue_6232()) == 313
									? "PASS"
									: "FAIL");
			ch1RDF1553BCode.setRes_2and3(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6232() - rdf1553ResultFetchingDTO.getValue_6248()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6232() - rdf1553ResultFetchingDTO.getValue_6248()) == 313
									? "PASS"
									: "FAIL");
			ch1RDF1553BCode.setRes_3and4(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6248() - rdf1553ResultFetchingDTO.getValue_6264()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6248() - rdf1553ResultFetchingDTO.getValue_6264()) == 313
									? "PASS"
									: "FAIL");
			ch1RDF1553BCode.setRes_4and5(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6264() - rdf1553ResultFetchingDTO.getValue_6280()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6264() - rdf1553ResultFetchingDTO.getValue_6280()) == 313
									? "PASS"
									: "FAIL");
			ch1RDF1553BCode.setRes_5and6(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6280() - rdf1553ResultFetchingDTO.getValue_6296()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6280() - rdf1553ResultFetchingDTO.getValue_6296()) == 313
									? "PASS"
									: "FAIL");
			ch1RDF1553BCode.setRes_6and7(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6296() - rdf1553ResultFetchingDTO.getValue_6312()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6296() - rdf1553ResultFetchingDTO.getValue_6312()) == 313
									? "PASS"
									: "FAIL");

			// ---------------------- CHANNEL 2 ----------------------
			ch2RDF1553BCode.setChannelNo("ch2");
			ch2RDF1553BCode.setStageId(stageId);
			ch2RDF1553BCode.setSessionId(StateMachine.currentSessionDetails.getSessionId());

			ch2RDF1553BCode.setValueCh_1(rdf1553ResultFetchingDTO.getValue_6348() + "");
			ch2RDF1553BCode.setValueCh_2(rdf1553ResultFetchingDTO.getValue_6364() + "");
			ch2RDF1553BCode.setValueCh_3(rdf1553ResultFetchingDTO.getValue_6380() + "");
			ch2RDF1553BCode.setValueCh_4(rdf1553ResultFetchingDTO.getValue_6396() + "");
			ch2RDF1553BCode.setValueCh_5(rdf1553ResultFetchingDTO.getValue_6412() + "");
			ch2RDF1553BCode.setValueCh_6(rdf1553ResultFetchingDTO.getValue_6428() + "");
			ch2RDF1553BCode.setValueCh_7(rdf1553ResultFetchingDTO.getValue_6444() + "");

//			ch2RDF1553BCode.setDiff_1and2(
//					rdf1553ResultFetchingDTO.getValue_6348() - rdf1553ResultFetchingDTO.getValue_6364() + "");
//			ch2RDF1553BCode.setDiff_2and3(
//					rdf1553ResultFetchingDTO.getValue_6364() - rdf1553ResultFetchingDTO.getValue_6380() + "");
//			ch2RDF1553BCode.setDiff_3and4(
//					rdf1553ResultFetchingDTO.getValue_6380() - rdf1553ResultFetchingDTO.getValue_6396() + "");
//			ch2RDF1553BCode.setDiff_4and5(
//					rdf1553ResultFetchingDTO.getValue_6396() - rdf1553ResultFetchingDTO.getValue_6412() + "");
//			ch2RDF1553BCode.setDiff_5and6(
//					rdf1553ResultFetchingDTO.getValue_6412() - rdf1553ResultFetchingDTO.getValue_6428() + "");
//			ch2RDF1553BCode.setDiff_6and7(
//					rdf1553ResultFetchingDTO.getValue_6428() - rdf1553ResultFetchingDTO.getValue_6444() + "");
			
			
			ch2RDF1553BCode.setDiff_1and2(
					rdf1553ResultFetchingDTO.getValue_6364()-rdf1553ResultFetchingDTO.getValue_6348() + "");
			ch2RDF1553BCode.setDiff_2and3(
			     	rdf1553ResultFetchingDTO.getValue_6380()-rdf1553ResultFetchingDTO.getValue_6364() + "");
			ch2RDF1553BCode.setDiff_3and4(
					 rdf1553ResultFetchingDTO.getValue_6396()-rdf1553ResultFetchingDTO.getValue_6380() + "");
			ch2RDF1553BCode.setDiff_4and5(
					 rdf1553ResultFetchingDTO.getValue_6412()-rdf1553ResultFetchingDTO.getValue_6396()  + "");
			ch2RDF1553BCode.setDiff_5and6(
				 rdf1553ResultFetchingDTO.getValue_6428() -	rdf1553ResultFetchingDTO.getValue_6412() + "");
			ch2RDF1553BCode.setDiff_6and7(
					 rdf1553ResultFetchingDTO.getValue_6444() -rdf1553ResultFetchingDTO.getValue_6428()+ "");


			ch2RDF1553BCode.setRes_1and2(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6348() - rdf1553ResultFetchingDTO.getValue_6364()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6348() - rdf1553ResultFetchingDTO.getValue_6364()) == 313
									? "PASS"
									: "FAIL");
			ch2RDF1553BCode.setRes_2and3(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6364() - rdf1553ResultFetchingDTO.getValue_6380()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6364() - rdf1553ResultFetchingDTO.getValue_6380()) == 313
									? "PASS"
									: "FAIL");
			ch2RDF1553BCode.setRes_3and4(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6380() - rdf1553ResultFetchingDTO.getValue_6396()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6380() - rdf1553ResultFetchingDTO.getValue_6396()) == 313
									? "PASS"
									: "FAIL");
			ch2RDF1553BCode.setRes_4and5(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6396() - rdf1553ResultFetchingDTO.getValue_6412()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6396() - rdf1553ResultFetchingDTO.getValue_6412()) == 313
									? "PASS"
									: "FAIL");
			ch2RDF1553BCode.setRes_5and6(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6412() - rdf1553ResultFetchingDTO.getValue_6428()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6412() - rdf1553ResultFetchingDTO.getValue_6428()) == 313
									? "PASS"
									: "FAIL");
			ch2RDF1553BCode.setRes_6and7(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6428() - rdf1553ResultFetchingDTO.getValue_6444()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6428() - rdf1553ResultFetchingDTO.getValue_6444()) == 313
									? "PASS"
									: "FAIL");

			// ---------------------- CHANNEL 3 ----------------------
			ch3RDF1553BCode.setChannelNo("ch3");
			ch3RDF1553BCode.setStageId(stageId);
			ch3RDF1553BCode.setSessionId(StateMachine.currentSessionDetails.getSessionId());

			ch3RDF1553BCode.setValueCh_1(rdf1553ResultFetchingDTO.getValue_6480() + "");
			ch3RDF1553BCode.setValueCh_2(rdf1553ResultFetchingDTO.getValue_6496() + "");
			ch3RDF1553BCode.setValueCh_3(rdf1553ResultFetchingDTO.getValue_6512() + "");
			ch3RDF1553BCode.setValueCh_4(rdf1553ResultFetchingDTO.getValue_6528() + "");
			ch3RDF1553BCode.setValueCh_5(rdf1553ResultFetchingDTO.getValue_6544() + "");
			ch3RDF1553BCode.setValueCh_6(rdf1553ResultFetchingDTO.getValue_6560() + "");
			ch3RDF1553BCode.setValueCh_7(rdf1553ResultFetchingDTO.getValue_6576() + "");

//			ch3RDF1553BCode.setDiff_1and2(
//					rdf1553ResultFetchingDTO.getValue_6480() - rdf1553ResultFetchingDTO.getValue_6496() + "");
//			ch3RDF1553BCode.setDiff_2and3(
//					rdf1553ResultFetchingDTO.getValue_6496() - rdf1553ResultFetchingDTO.getValue_6512() + "");
//			ch3RDF1553BCode.setDiff_3and4(
//					rdf1553ResultFetchingDTO.getValue_6512() - rdf1553ResultFetchingDTO.getValue_6528() + "");
//			ch3RDF1553BCode.setDiff_4and5(
//					rdf1553ResultFetchingDTO.getValue_6528() - rdf1553ResultFetchingDTO.getValue_6544() + "");
//			ch3RDF1553BCode.setDiff_5and6(
//					rdf1553ResultFetchingDTO.getValue_6544() - rdf1553ResultFetchingDTO.getValue_6560() + "");
//			ch3RDF1553BCode.setDiff_6and7(
//					rdf1553ResultFetchingDTO.getValue_6560() - rdf1553ResultFetchingDTO.getValue_6576() + "");
			
			ch3RDF1553BCode.setDiff_1and2(
				  rdf1553ResultFetchingDTO.getValue_6496() -rdf1553ResultFetchingDTO.getValue_6480() + "");
			ch3RDF1553BCode.setDiff_2and3(
				  rdf1553ResultFetchingDTO.getValue_6512()-rdf1553ResultFetchingDTO.getValue_6496()  + "");
			ch3RDF1553BCode.setDiff_3and4(
				  rdf1553ResultFetchingDTO.getValue_6528()-	rdf1553ResultFetchingDTO.getValue_6512()  + "");
			ch3RDF1553BCode.setDiff_4and5(
					rdf1553ResultFetchingDTO.getValue_6544()-rdf1553ResultFetchingDTO.getValue_6528()  + "");
			ch3RDF1553BCode.setDiff_5and6(
					rdf1553ResultFetchingDTO.getValue_6560() -rdf1553ResultFetchingDTO.getValue_6544() + "");
			ch3RDF1553BCode.setDiff_6and7(
				 rdf1553ResultFetchingDTO.getValue_6576()-	rdf1553ResultFetchingDTO.getValue_6560()  + "");

			ch3RDF1553BCode.setRes_1and2(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6480() - rdf1553ResultFetchingDTO.getValue_6496()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6480() - rdf1553ResultFetchingDTO.getValue_6496()) == 313
									? "PASS"
									: "FAIL");
			ch3RDF1553BCode.setRes_2and3(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6496() - rdf1553ResultFetchingDTO.getValue_6512()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6496() - rdf1553ResultFetchingDTO.getValue_6512()) == 313
									? "PASS"
									: "FAIL");
			ch3RDF1553BCode.setRes_3and4(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6512() - rdf1553ResultFetchingDTO.getValue_6528()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6512() - rdf1553ResultFetchingDTO.getValue_6528()) == 313
									? "PASS"
									: "FAIL");
			ch3RDF1553BCode.setRes_4and5(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6528() - rdf1553ResultFetchingDTO.getValue_6544()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6528() - rdf1553ResultFetchingDTO.getValue_6544()) == 313
									? "PASS"
									: "FAIL");
			ch3RDF1553BCode.setRes_5and6(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6544() - rdf1553ResultFetchingDTO.getValue_6560()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6544() - rdf1553ResultFetchingDTO.getValue_6560()) == 313
									? "PASS"
									: "FAIL");
			ch3RDF1553BCode.setRes_6and7(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6560() - rdf1553ResultFetchingDTO.getValue_6576()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6560() - rdf1553ResultFetchingDTO.getValue_6576()) == 313
									? "PASS"
									: "FAIL");

			// ---------------------- CHANNEL 4 ----------------------
			ch4RDF1553BCode.setChannelNo("ch4");
			ch4RDF1553BCode.setStageId(stageId);
			ch4RDF1553BCode.setSessionId(StateMachine.currentSessionDetails.getSessionId());

			ch4RDF1553BCode.setValueCh_1(rdf1553ResultFetchingDTO.getValue_6612() + "");
			ch4RDF1553BCode.setValueCh_2(rdf1553ResultFetchingDTO.getValue_6628() + "");
			ch4RDF1553BCode.setValueCh_3(rdf1553ResultFetchingDTO.getValue_6644() + "");
			ch4RDF1553BCode.setValueCh_4(rdf1553ResultFetchingDTO.getValue_6660() + "");
			ch4RDF1553BCode.setValueCh_5(rdf1553ResultFetchingDTO.getValue_6676() + "");
			ch4RDF1553BCode.setValueCh_6(rdf1553ResultFetchingDTO.getValue_6692() + "");
			ch4RDF1553BCode.setValueCh_7(rdf1553ResultFetchingDTO.getValue_6708() + "");

//			ch4RDF1553BCode.setDiff_1and2(
//					rdf1553ResultFetchingDTO.getValue_6612() - rdf1553ResultFetchingDTO.getValue_6628() + "");
//			ch4RDF1553BCode.setDiff_2and3(
//					rdf1553ResultFetchingDTO.getValue_6628() - rdf1553ResultFetchingDTO.getValue_6644() + "");
//			ch4RDF1553BCode.setDiff_3and4(
//					rdf1553ResultFetchingDTO.getValue_6644() - rdf1553ResultFetchingDTO.getValue_6660() + "");
//			ch4RDF1553BCode.setDiff_4and5(
//					rdf1553ResultFetchingDTO.getValue_6660() - rdf1553ResultFetchingDTO.getValue_6676() + "");
//			ch4RDF1553BCode.setDiff_5and6(
//					rdf1553ResultFetchingDTO.getValue_6676() - rdf1553ResultFetchingDTO.getValue_6692() + "");
//			ch4RDF1553BCode.setDiff_6and7(
//					rdf1553ResultFetchingDTO.getValue_6692() - rdf1553ResultFetchingDTO.getValue_6708() + "");
			
			ch4RDF1553BCode.setDiff_1and2(
					rdf1553ResultFetchingDTO.getValue_6628() -rdf1553ResultFetchingDTO.getValue_6612()  + "");
			ch4RDF1553BCode.setDiff_2and3(
					 rdf1553ResultFetchingDTO.getValue_6644()-rdf1553ResultFetchingDTO.getValue_6628()  + "");
			ch4RDF1553BCode.setDiff_3and4(
					rdf1553ResultFetchingDTO.getValue_6660()-rdf1553ResultFetchingDTO.getValue_6644()  + "");
			ch4RDF1553BCode.setDiff_4and5(
				  rdf1553ResultFetchingDTO.getValue_6676()-	rdf1553ResultFetchingDTO.getValue_6660() + "");
			ch4RDF1553BCode.setDiff_5and6(
				 rdf1553ResultFetchingDTO.getValue_6692()-	rdf1553ResultFetchingDTO.getValue_6676()  + "");
			ch4RDF1553BCode.setDiff_6and7(
					rdf1553ResultFetchingDTO.getValue_6708()-rdf1553ResultFetchingDTO.getValue_6692()  + "");

			ch4RDF1553BCode.setRes_1and2(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6612() - rdf1553ResultFetchingDTO.getValue_6628()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6612() - rdf1553ResultFetchingDTO.getValue_6628()) == 313
									? "PASS"
									: "FAIL");
			ch4RDF1553BCode.setRes_2and3(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6628() - rdf1553ResultFetchingDTO.getValue_6644()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6628() - rdf1553ResultFetchingDTO.getValue_6644()) == 313
									? "PASS"
									: "FAIL");
			ch4RDF1553BCode.setRes_3and4(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6644() - rdf1553ResultFetchingDTO.getValue_6660()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6644() - rdf1553ResultFetchingDTO.getValue_6660()) == 313
									? "PASS"
									: "FAIL");
			ch4RDF1553BCode.setRes_4and5(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6660() - rdf1553ResultFetchingDTO.getValue_6676()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6660() - rdf1553ResultFetchingDTO.getValue_6676()) == 313
									? "PASS"
									: "FAIL");
			ch4RDF1553BCode.setRes_5and6(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6676() - rdf1553ResultFetchingDTO.getValue_6692()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6676() - rdf1553ResultFetchingDTO.getValue_6692()) == 313
									? "PASS"
									: "FAIL");
			ch4RDF1553BCode.setRes_6and7(Math
					.abs(rdf1553ResultFetchingDTO.getValue_6692() - rdf1553ResultFetchingDTO.getValue_6708()) == 312
					|| Math.abs(
							rdf1553ResultFetchingDTO.getValue_6692() - rdf1553ResultFetchingDTO.getValue_6708()) == 313
									? "PASS"
									: "FAIL");

			lst.add(ch1RDF1553BCode);
			lst.add(ch2RDF1553BCode);
			lst.add(ch3RDF1553BCode);
			lst.add(ch4RDF1553BCode);

			RDF1553BCodeService rDF1553BCodeService = new RDF1553BCodeService();
			rDF1553BCodeService.delete1553RDFResultForSession(StateMachine.currentSessionDetails.getSessionId(),
					stageId);
			rDF1553BCodeService.add1553RDFResultForSession(lst);

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return lst;

	}

	public List<RDF1553BCode> get1553BValuesForChannel(String sessionId, String stageId, String chNo) {

		List<RDF1553BCode> rDF1553BCodelist = new ArrayList<RDF1553BCode>();
		try {
			RDF1553BCodeService rDF1553BCodeService = new RDF1553BCodeService();
			GetResponse get1553BRes = rDF1553BCodeService.get1553RDFResultForSession(sessionId, stageId, chNo);
			rDF1553BCodelist = (List<RDF1553BCode>) get1553BRes.getResponseList();
			////System.out.println("Check Response List for1553BBB" +rDF1553BCodelist.size());

		} catch (Exception ex) {
			ex.printStackTrace();
			////System.out.println(ex.getLocalizedMessage());
		}
		return rDF1553BCodelist;
	}
	
	//Add Interface 1553B Mode For Session And Stage...
	public Response addInterface1553BModeForStage(String sessionId,String stageId,List<Interface1553B_Mode> interfaceLst)
	{
		Response response = new Response();
		try {
			Interface1553B_ModeService interface1553B_ModeService = new Interface1553B_ModeService();
			interface1553B_ModeService.delete1553RDFInterfaceModeForSession(sessionId, stageId);
			interface1553B_ModeService.addInterface1553BForAllChannels(interfaceLst);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}
	
	public List<Interface1553B_Mode> getInterface1553BMode(String sessionId,String stageId)
	{
		List<Interface1553B_Mode> responseList = new ArrayList<Interface1553B_Mode>();
		try {
			Interface1553B_ModeService interface1553B_ModeService = new Interface1553B_ModeService();
			responseList = interface1553B_ModeService.getInterfaceModeChecks(sessionId, stageId);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return responseList;
	}
	

}
