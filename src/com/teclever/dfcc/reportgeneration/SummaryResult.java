package com.teclever.dfcc.reportgeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;

public class SummaryResult {

	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public Response generateHistoryResultForSession(String sessionId)
			throws DocumentException, MalformedURLException, IOException, ParseException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "History_Report_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\sharn\\Downloads\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//			////System.out.println("Reportpath Check:" + filePath);

		}
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();
		res.setDownloadPath(filePath);

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// To Create Header

		// Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\sharn\\Downloads\\bel_logo_hindi.png";

		} else {
//			Before changing for tecelever testing
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";

//			After changing for Tecelever testing
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.png";
//			imagePath =  currentDirectory + File.separator + "Images"+File.separator+"BEL.png";
		}

		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(100);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		Font lineFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD, BaseColor.BLACK);

		String line = "____________________________________________________________";
		;
		Paragraph linePara = new Paragraph(line, lineFont);
		linePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(linePara);

		String title = "History Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.newPage();

		PdfContentByte canvas = writer.getDirectContent();
		float x = document.leftMargin();
		float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
																					// the top
		float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		float height = 100f; // Height of the rounded rectangular box

		// Set the corner radius for the rectangle
		float cornerRadius = 20f; // Adjust this value to change how rounded the corners are

		// Draw the rounded rectangular box
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius); // x, y, width, height, corner radius
		canvas.stroke();

		// Add images and text inside the rounded rectangular box
		String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
//			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
//			////System.out.println("Entred 2nd");
			imagePath1 = "C:\\Users\\sharn\\Downloads\\" + "bel_logo_hindi.png";

		} else {
			// imagePath1 =
			// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";

			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
//			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
//			////System.out.println("Entred 3rd");
			imagePath3 = "C:\\Users\\sharn\\Downloads\\" + "TECLEVER_logo (1).png";

		} else {
//			Before Changing for Tecelever Testing
//			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";

			imagePath3 = currentDirectory + File.separator + "Images" + File.separator + "TECLEVER_logo.png";

		}

		Image img1 = Image.getInstance(imagePath1);
		Image img3 = Image.getInstance(imagePath3);

		float imgWidth = (width - 40) / 3; // Calculate the width for each image
		float imgHeight = height - 40; // Calculate the height for each image

		img1.scaleToFit(imgWidth - 05, imgHeight - 05);
		img3.scaleToFit(imgWidth - 10, imgHeight - 10);

		float imgY = y + 10;
		float imgX1 = x + 10;
		float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

		img1.setAbsolutePosition(imgX1, imgY + 30);
		img3.setAbsolutePosition(imgX3 + 30, imgY + 20);

		document.add(img1);
		document.add(img3);

		Map<String, String> stageIdName = new HashMap<String, String>();

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSession(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		stageIdName = resultExecutionManagement.getStageIdName();

		// Fetching The Session Details
		Map<String, String> sessionDetailsMap = new HashMap<String, String>();
		if (!sessionId.substring(0, 4).equals("TSSN")) {
			sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		} else {
			sessionDetailsMap = resultExecutionManagement.getTrailSessionDetailsBySessionId(sessionId);
		}
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		// User Defined Colour..
		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor skyBlueColor = new BaseColor(0, 176, 196, 222);
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlightCementFont1 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "   DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

		Font headerFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n"));

		/*
		 * Paragraph SessionDetails = new Paragraph("Session Details", headerFont1);
		 * SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the
		 * heading document.add(SessionDetails); document.add(new Paragraph("\n" ));
		 */

		Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont1);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont1);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont1);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		// Create Times New Roman font with bigger size
		BaseFont timesNewRoman = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

		document.add(new Paragraph("\n"));
		Font bigCenterFont = new Font(timesNewRoman, 14, Font.BOLD);

		// Create Chunk
		Chunk briefReport = new Chunk("Brief Report", bigCenterFont);

		// Create Paragraph and center it
		Paragraph briefParagraph = new Paragraph();
		briefParagraph.add(briefReport);
		briefParagraph.setAlignment(Element.ALIGN_CENTER);

		// Add to document
		document.add(briefParagraph);

		String stageNameTestMode = "";
		String previousStage = ""; // To track the previous stage and decide when to start a new table

		if (resultExecutionDTOList != null) {
			// Create a table with 3 columns
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100); // Set table width to 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 0.4f, 2f, 2f, 1f }; // Column widths
			table.setWidths(columnWidths);
			int sno = 0;
			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
			String[] headers = { "S.No", "File Name", "Time", "Result" };

			// Add an initial empty row at the start for spacing before the first stage
			PdfPCell emptyCell = new PdfPCell(new Phrase("")); // Empty cell
			emptyCell.setColspan(3);
			emptyCell.setFixedHeight(10); // Adjust height as needed for spacing
			emptyCell.setBorder(Rectangle.NO_BORDER); // Remove border for clean spacing
			table.addCell(emptyCell);

			// Loop through resultExecutionDTOList to process each item
			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {
				String currentStageTestMode = resultExecutionDTO.getTestMode().split("-")[1];
				currentStageTestMode = stageIdName.get(resultExecutionDTO.getStageId());

				if (!currentStageTestMode.equals(previousStage)) {

					PdfPCell emptyCellForSpacing = new PdfPCell(new Phrase("")); // Empty cell
					emptyCellForSpacing.setColspan(4);
					emptyCellForSpacing.setFixedHeight(10); // Adjust height as needed for spacing
					emptyCellForSpacing.setBorder(Rectangle.NO_BORDER); // Remove border for clean spacing
					table.addCell(emptyCellForSpacing);

					// Add the current stage heading to the table
					PdfPCell mergedCell = new PdfPCell(new Paragraph(currentStageTestMode));
					mergedCell.setColspan(4);
					mergedCell.setFixedHeight(20);
					mergedCell.setBackgroundColor(skyBlueColor);
					mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(mergedCell);

					// Add headers to the table
					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
						sno = 1;
					}

					// Add an empty row for space between stages
					/*
					 * PdfPCell emptyCellForSpacing1 = new PdfPCell(new Phrase("")); // Empty cell
					 * emptyCellForSpacing1.setColspan(4); emptyCellForSpacing1.setFixedHeight(10);
					 * // Adjust height as needed for spacing
					 * emptyCellForSpacing1.setBorder(Rectangle.NO_BORDER); // Remove border for
					 * clean spacing table.addCell(emptyCellForSpacing1);
					 */

					// Update previousStage with currentStageTestMode
					previousStage = currentStageTestMode;
				}

				// Add the row data for the current stage
				SimpleDateFormat source = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
				SimpleDateFormat target = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				table.addCell(new Phrase(sno + ""));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
//				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));

				String formattedEndTime = target.format(source.parse(resultExecutionDTO.getEndTime()));// sai30122025
																										// first
				table.addCell(new Phrase(formattedEndTime));

				if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {
					table.addCell(new Phrase("FAIL"));
				} else {
					table.addCell(new Phrase("PASS"));
				}
				sno++;
			}

			// Add the table to the document after processing all results
			document.add(table);
			document.add(new Paragraph("\n" + "\n"));
		}

		document.newPage();
		Chunk briefReport1 = new Chunk("Detailed Report", bigCenterFont);
		Paragraph briefParagraph1 = new Paragraph();
		briefParagraph1.add(briefReport1);
		briefParagraph1.setAlignment(Element.ALIGN_CENTER);

		// Add to document
		document.add(briefParagraph1);
		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForSession(sessionId);
		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();

//		for (int i = 0; i < 100; i++) {
//
//			ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
//
//			if (i <= 20 && i >= 0) {
//				resultDetailedDTO.setStageId("L1_001");
//
//			}
//			if (i <= 40 && i > 20) {
//				resultDetailedDTO.setStageId("L2_002");
//
//			}
//			if (i <= 60 && i > 40) {
//				resultDetailedDTO.setStageId("L3_001");
//
//			}
//			if (i <= 80 && i > 60) {
//				resultDetailedDTO.setStageId("L4_001");
//
//			}
//			if (i <= 100 && i > 80) {
//				resultDetailedDTO.setStageId("L5_001");
//
//			}
//
//			resultDetailedDTO.setExpectedValue(i + ".00");
//			resultDetailedDTO.setFaultyChannel("CH" + i);
//			resultDetailedDTO.setMeasuredValue(i + "80");
//			resultDetailedDTO.setRdfName("rdf" + i);
//			resultDetailedDTO.setStepName("" + i);
//			resultDetailedDTO.setSignalName("SN_" + i);
//			resultDetailedDTO.setTpfFileName("TPF_" + i);
//			resultDetailedDTO.setTpgph("TPGH" + i);
//			resultDetailedDTO.setUnit("UN-" + i);
//			resultDetailedDTO.setTestName("TN-" + i);
//			resultDetailedDTOList.add(resultDetailedDTO);
//		}

		String stageName = "";

		if (resultDetailedDTOList != null && !resultDetailedDTOList.isEmpty()) {
			stageName = resultDetailedDTOList.get(0).getStageId();
		}
		if (resultDetailedDTOList != null && !resultDetailedDTOList.isEmpty()) {
			PdfPTable table = new PdfPTable(10); // 10 columns
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f };
			table.setWidths(columnWidths);

			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

			PdfPCell mergedCell = new PdfPCell(new Paragraph(stageIdName.get(stageName)));
			mergedCell.setColspan(10);
			mergedCell.setFixedHeight(20);
			mergedCell.setBackgroundColor(skyBlueColor);
			mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(mergedCell);
//			String[] headers = { "Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit",
//					"TPF File Name", "Signal Name", "Faulty Channel", "RDF Name" };

			String[] headers = { "Test Name", "RDF File", "File Name", "TPGPH NO", "STEP NO", "SIGNAL NAME",
					"Expected Value", "Measured Value", "FAULTY SRU", "UNIT" };

			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
				cell.setBackgroundColor(BaseColor.GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				// boolean newTableFlag = false;

				if (stageName.equals(dto.getStageId())) {

					table.addCell(new Phrase(dto.getTestMode()));
					table.addCell(new Phrase(dto.getRdfName()));
					table.addCell(new Phrase(dto.getTpfFileName()));// 1
					table.addCell(new Phrase(dto.getTpgph()));// 2
					table.addCell(new Phrase(dto.getStepName()));// 3
					table.addCell(new Phrase(dto.getSignalName()));// 4
					table.addCell(new Phrase(dto.getExpectedValue()));// 5
					table.addCell(new Phrase(dto.getMeasuredValue()));// 6
					table.addCell(new Phrase(dto.getFaultyChannel()));// 7
					table.addCell(new Phrase(dto.getUnit()));// 8

				} else {

					stageName = dto.getStageId();
					document.add(table);
					document.add(new Paragraph("\n" + "\n"));
					table = new PdfPTable(10);
					table.setWidthPercentage(100); // Width 100%
					table.setSpacingBefore(10f); // Space before table
					table.setSpacingAfter(10f); // Space after table
					// float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
					table.setWidths(columnWidths);

					// Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
					// BaseColor.WHITE);
					// String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At",
					// "Status" };

					mergedCell = new PdfPCell(new Paragraph(stageIdName.get(stageName)));
					mergedCell.setColspan(10);
					mergedCell.setFixedHeight(20);
					mergedCell.setBackgroundColor(skyBlueColor);
					mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(mergedCell);

					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}

					table.addCell(new Phrase(dto.getTestMode())); //
					table.addCell(new Phrase(dto.getRdfName())); //
					table.addCell(new Phrase(dto.getTpfFileName()));// 1
					table.addCell(new Phrase(dto.getTpgph()));// 2
					table.addCell(new Phrase(dto.getStepName()));// 3
					table.addCell(new Phrase(dto.getSignalName()));// 4
					table.addCell(new Phrase(dto.getExpectedValue()));// 5
					table.addCell(new Phrase(dto.getMeasuredValue()));// 6
					table.addCell(new Phrase(dto.getFaultyChannel()));// 7
					table.addCell(new Phrase(dto.getUnit()));// 8

				}

				// document.add(newTable);
			}
			document.add(table);
		}

		document.close();

		GetObjResponse sessionRes = new GetObjResponse();
		String sessionPathString = "";// sessionentity.getPath()+File.separator+"report";

		if (!sessionId.substring(0, 4).equals("TSSN")) {
			SessionService sessionService = new SessionService();
			sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionEntity = new SessionEntity();
			sessionEntity = (SessionEntity) sessionRes.getObject();
			sessionPathString = sessionEntity.getPath();

		} else {
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
			TrailSessionEntity trailSessionEntity = new TrailSessionEntity();
			trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
			sessionPathString = trailSessionEntity.getPath();
		}

		sessionPathString = sessionPathString + File.separator + "report";
		Path fileFullPath = Path.of(filePath);
		SessionFileManagement sessionFileManagement = new SessionFileManagement();
		Path sessionPath = Path.of(sessionPathString);
		sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);

		res.setResponseMessage("History Results Report Download Successfully...!");
		res.setResponseCode(1);
		return res;
	}
	
	
	
	public Response generateSummaryReportForSession(String sessionId)
			throws DocumentException, MalformedURLException, IOException, ParseException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "Summary_Report_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\sharn\\Downloads\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//			////System.out.println("Reportpath Check:" + filePath);

		}
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();
		res.setDownloadPath(filePath);

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// To Create Header

		// Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\sharn\\Downloads\\bel_logo_hindi.png";

		} else {
//			Before changing for tecelever testing
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";

//			After changing for Tecelever testing
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.png";
//			imagePath =  currentDirectory + File.separator + "Images"+File.separator+"BEL.png";
		}

		Map<String, Map<String, String>> briefDataSummary = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> detailedDataSummary = new HashMap<String, Map<String, String>>();
		
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(100);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		Font lineFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD, BaseColor.BLACK);

		String line = "____________________________________________________________";
		;
		Paragraph linePara = new Paragraph(line, lineFont);
		linePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(linePara);

		String title = "Summary Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.newPage();

		PdfContentByte canvas = writer.getDirectContent();
		float x = document.leftMargin();
		float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
																					// the top
		float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		float height = 100f; // Height of the rounded rectangular box

		// Set the corner radius for the rectangle
		float cornerRadius = 20f; // Adjust this value to change how rounded the corners are

		// Draw the rounded rectangular box
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius); // x, y, width, height, corner radius
		canvas.stroke();

		// Add images and text inside the rounded rectangular box
		String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
//			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
//			////System.out.println("Entred 2nd");
			imagePath1 = "C:\\Users\\sharn\\Downloads\\" + "bel_logo_hindi.png";

		} else {
			// imagePath1 =
			// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";

			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
//			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
//			////System.out.println("Entred 3rd");
			imagePath3 = "C:\\Users\\sharn\\Downloads\\" + "TECLEVER_logo (1).png";

		} else {
//			Before Changing for Tecelever Testing
//			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";

			imagePath3 = currentDirectory + File.separator + "Images" + File.separator + "TECLEVER_logo.png";

		}

		Image img1 = Image.getInstance(imagePath1);
		Image img3 = Image.getInstance(imagePath3);

		float imgWidth = (width - 40) / 3; // Calculate the width for each image
		float imgHeight = height - 40; // Calculate the height for each image

		img1.scaleToFit(imgWidth - 05, imgHeight - 05);
		img3.scaleToFit(imgWidth - 10, imgHeight - 10);

		float imgY = y + 10;
		float imgX1 = x + 10;
		float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

		img1.setAbsolutePosition(imgX1, imgY + 30);
		img3.setAbsolutePosition(imgX3 + 30, imgY + 20);

		document.add(img1);
		document.add(img3);

		Map<String, String> stageIdName = new HashMap<String, String>();


		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSession(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		stageIdName = resultExecutionManagement.getStageIdName();

		// Fetching The Session Details
		Map<String, String> sessionDetailsMap = new HashMap<String, String>();
		if (!sessionId.substring(0, 4).equals("TSSN")) {
			sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		} else {
			sessionDetailsMap = resultExecutionManagement.getTrailSessionDetailsBySessionId(sessionId);
		}
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		// User Defined Colour..
		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor skyBlueColor = new BaseColor(0, 176, 196, 222);
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlightCementFont1 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "   DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

		Font headerFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n"));

		/*
		 * Paragraph SessionDetails = new Paragraph("Session Details", headerFont1);
		 * SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the
		 * heading document.add(SessionDetails); document.add(new Paragraph("\n" ));
		 */

		Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont1);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont1);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont1);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		// Create Times New Roman font with bigger size
		BaseFont timesNewRoman = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

		document.add(new Paragraph("\n"));
		Font bigCenterFont = new Font(timesNewRoman, 14, Font.BOLD);

		// Create Chunk
		Chunk briefReport = new Chunk("Brief Report", bigCenterFont);

		// Create Paragraph and center it
		Paragraph briefParagraph = new Paragraph();
		briefParagraph.add(briefReport);
		briefParagraph.setAlignment(Element.ALIGN_CENTER);

		// Add to document
		document.add(briefParagraph);

		String stageNameTestMode = "";
		String previousStage = ""; // To track the previous stage and decide when to start a new table

		if (resultExecutionDTOList != null) {
			// Create a table with 3 columns
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100); // Set table width to 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 0.4f, 2f, 2f, 1f }; // Column widths
			table.setWidths(columnWidths);
			int sno = 0;
			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
			String[] headers = { "S.No", "File Name", "Time", "Result" };

			// Add an initial empty row at the start for spacing before the first stage
			PdfPCell emptyCell = new PdfPCell(new Phrase("")); // Empty cell
			emptyCell.setColspan(3);
			emptyCell.setFixedHeight(10); // Adjust height as needed for spacing
			emptyCell.setBorder(Rectangle.NO_BORDER); // Remove border for clean spacing
			table.addCell(emptyCell);

			// Loop through resultExecutionDTOList to process each item
			int	passCount = 0;
			int	failCount = 0;
			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {
				String currentStageTestMode = resultExecutionDTO.getTestMode().split("-")[1];
				
				currentStageTestMode = resultExecutionDTO.getStageId();
				currentStageTestMode = stageIdName.get(resultExecutionDTO.getStageId());
			
				if (!currentStageTestMode.equals(previousStage)) {
					Map<String, String> details = new HashMap<String, String>();
		

					PdfPCell emptyCellForSpacing = new PdfPCell(new Phrase("")); // Empty cell
					emptyCellForSpacing.setColspan(4);
					emptyCellForSpacing.setFixedHeight(10); // Adjust height as needed for spacing
					emptyCellForSpacing.setBorder(Rectangle.NO_BORDER); // Remove border for clean spacing
					table.addCell(emptyCellForSpacing);

					// Add the current stage heading to the table
					PdfPCell mergedCell = new PdfPCell(new Paragraph(currentStageTestMode));
					mergedCell.setColspan(4);
					mergedCell.setFixedHeight(20);
					mergedCell.setBackgroundColor(skyBlueColor);
					mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(mergedCell);

					// Add headers to the table
					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
						sno = 1;
					}

					// Add an empty row for space between stages
					/*
					 * PdfPCell emptyCellForSpacing1 = new PdfPCell(new Phrase("")); // Empty cell
					 * emptyCellForSpacing1.setColspan(4); emptyCellForSpacing1.setFixedHeight(10);
					 * // Adjust height as needed for spacing
					 * emptyCellForSpacing1.setBorder(Rectangle.NO_BORDER); // Remove border for
					 * clean spacing table.addCell(emptyCellForSpacing1);
					 */

					// Update previousStage with currentStageTestMode
					details.put("passCount", passCount+"");
					details.put("failCount", failCount+"");
					details.put("totalCount",passCount+failCount+"");
					passCount = 0;
					failCount = 0;
					briefDataSummary.put( resultExecutionDTO.getStageId(), sessionDetailsMap);
					previousStage = currentStageTestMode;
				}

				// Add the row data for the current stage
				SimpleDateFormat source = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
				SimpleDateFormat target = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				table.addCell(new Phrase(sno + ""));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
//				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));

				String formattedEndTime = target.format(source.parse(resultExecutionDTO.getEndTime()));// sai30122025
																										// first
				table.addCell(new Phrase(formattedEndTime));

				if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {
					table.addCell(new Phrase("FAIL"));
					failCount++;
				} else {
					table.addCell(new Phrase("PASS"));
					passCount++;
				}
				sno++;
			}

			// Add the table to the document after processing all results
			document.add(table);
			document.add(new Paragraph("\n" + "\n"));
		}

		document.newPage();
		Chunk briefReport1 = new Chunk("Detailed Report", bigCenterFont);
		Paragraph briefParagraph1 = new Paragraph();
		briefParagraph1.add(briefReport1);
		briefParagraph1.setAlignment(Element.ALIGN_CENTER);

		// Add to document
		document.add(briefParagraph1);
		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForSession(sessionId);
		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();

//		for (int i = 0; i < 100; i++) {
//
//			ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
//
//			if (i <= 20 && i >= 0) {
//				resultDetailedDTO.setStageId("L1_001");
//
//			}
//			if (i <= 40 && i > 20) {
//				resultDetailedDTO.setStageId("L2_002");
//
//			}
//			if (i <= 60 && i > 40) {
//				resultDetailedDTO.setStageId("L3_001");
//
//			}
//			if (i <= 80 && i > 60) {
//				resultDetailedDTO.setStageId("L4_001");
//
//			}
//			if (i <= 100 && i > 80) {
//				resultDetailedDTO.setStageId("L5_001");
//
//			}
//
//			resultDetailedDTO.setExpectedValue(i + ".00");
//			resultDetailedDTO.setFaultyChannel("CH" + i);
//			resultDetailedDTO.setMeasuredValue(i + "80");
//			resultDetailedDTO.setRdfName("rdf" + i);
//			resultDetailedDTO.setStepName("" + i);
//			resultDetailedDTO.setSignalName("SN_" + i);
//			resultDetailedDTO.setTpfFileName("TPF_" + i);
//			resultDetailedDTO.setTpgph("TPGH" + i);
//			resultDetailedDTO.setUnit("UN-" + i);
//			resultDetailedDTO.setTestName("TN-" + i);
//			resultDetailedDTOList.add(resultDetailedDTO);
//		}

		String stageName = "";

		if (resultDetailedDTOList != null) {
			stageName = resultDetailedDTOList.get(0).getStageId();
		}
		if (resultDetailedDTOList != null) {
			PdfPTable table = new PdfPTable(10); // 10 columns
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f };
			table.setWidths(columnWidths);

			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

			PdfPCell mergedCell = new PdfPCell(new Paragraph(stageIdName.get(stageName)));
			mergedCell.setColspan(10);
			mergedCell.setFixedHeight(20);
			mergedCell.setBackgroundColor(skyBlueColor);
			mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(mergedCell);
//			String[] headers = { "Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit",
//					"TPF File Name", "Signal Name", "Faulty Channel", "RDF Name" };

			String[] headers = { "Test Name", "RDF File", "File Name", "TPGPH NO", "STEP NO", "SIGNAL NAME",
					"Expected Value", "Measured Value", "FAULTY SRU", "UNIT" };

			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
				cell.setBackgroundColor(BaseColor.GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				// boolean newTableFlag = false;

				if (stageName.equals(dto.getStageId())) {

					table.addCell(new Phrase(dto.getTestMode()));
					table.addCell(new Phrase(dto.getRdfName()));
					table.addCell(new Phrase(dto.getTpfFileName()));// 1
					table.addCell(new Phrase(dto.getTpgph()));// 2
					table.addCell(new Phrase(dto.getStepName()));// 3
					table.addCell(new Phrase(dto.getSignalName()));// 4
					table.addCell(new Phrase(dto.getExpectedValue()));// 5
					table.addCell(new Phrase(dto.getMeasuredValue()));// 6
					table.addCell(new Phrase(dto.getFaultyChannel()));// 7
					table.addCell(new Phrase(dto.getUnit()));// 8

				} else {

					stageName = dto.getStageId();
					document.add(table);
					document.add(new Paragraph("\n" + "\n"));
					table = new PdfPTable(10);
					table.setWidthPercentage(100); // Width 100%
					table.setSpacingBefore(10f); // Space before table
					table.setSpacingAfter(10f); // Space after table
					// float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
					table.setWidths(columnWidths);

					// Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
					// BaseColor.WHITE);
					// String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At",
					// "Status" };

					mergedCell = new PdfPCell(new Paragraph(stageIdName.get(stageName)));
					mergedCell.setColspan(10);
					mergedCell.setFixedHeight(20);
					mergedCell.setBackgroundColor(skyBlueColor);
					mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(mergedCell);

					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}

					table.addCell(new Phrase(dto.getTestMode())); //
					table.addCell(new Phrase(dto.getRdfName())); //
					table.addCell(new Phrase(dto.getTpfFileName()));// 1
					table.addCell(new Phrase(dto.getTpgph()));// 2
					table.addCell(new Phrase(dto.getStepName()));// 3
					table.addCell(new Phrase(dto.getSignalName()));// 4
					table.addCell(new Phrase(dto.getExpectedValue()));// 5
					table.addCell(new Phrase(dto.getMeasuredValue()));// 6
					table.addCell(new Phrase(dto.getFaultyChannel()));// 7
					table.addCell(new Phrase(dto.getUnit()));// 8

				}

				// document.add(newTable);
			}
			document.add(table);
		}

		document.close();

		GetObjResponse sessionRes = new GetObjResponse();
		String sessionPathString = "";// sessionentity.getPath()+File.separator+"report";

		if (!sessionId.substring(0, 4).equals("TSSN")) {
			SessionService sessionService = new SessionService();
			sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
			SessionEntity sessionEntity = new SessionEntity();
			sessionEntity = (SessionEntity) sessionRes.getObject();
			sessionPathString = sessionEntity.getPath();

		} else {
			TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
			sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
			TrailSessionEntity trailSessionEntity = new TrailSessionEntity();
			trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
			sessionPathString = trailSessionEntity.getPath();
		}

		sessionPathString = sessionPathString + File.separator + "report";
		Path fileFullPath = Path.of(filePath);
		SessionFileManagement sessionFileManagement = new SessionFileManagement();
		Path sessionPath = Path.of(sessionPathString);
		sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);

		res.setResponseMessage("Summary Results Report Download Successfully...!");
		res.setResponseCode(1);
		return res;
	}

}
