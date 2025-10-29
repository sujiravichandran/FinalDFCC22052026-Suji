package com.teclever.dfcc.reportgeneration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
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

public class ReportGeneration {

	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public Response generateBreifReportForCurrentExecution(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "BriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "D:\\Images\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//			System.out.println("Reportpath Check:" + filePath);

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
//			System.out.println("Entred 1st");
//			imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
			imagePath = "D:\\Images\\BEL.jpeg";

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

		String title = "Breif Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
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
//			System.out.println("Entred 2nd");
			imagePath1 = "D:\\Images\\" + fileName;

		} else {
			// imagePath1 =
			// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";

			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
//			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
//			System.out.println("Entred 3rd");
			imagePath3 = "D:\\Images\\" + fileName;

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

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		/*
		 * for (int i = 1; i <= 100; i++) { ResultExecutionDTO resultExecutionDTO = new
		 * ResultExecutionDTO(); // "Test Name", "Rdf File Detials", "D*Count",
		 * "End At", "Stage Name" ,"Status"
		 * resultExecutionDTO.setTestFileName("Test Name -" + i);
		 * resultExecutionDTO.setDStarCount(i + "");
		 * resultExecutionDTO.setRdfFile("RDF FILE NAME -" + i);
		 * resultExecutionDTO.setEndTime("End Time  -00:00:00");
		 * resultExecutionDTO.setStageName("StageName  -" + "Same");
		 * resultExecutionDTO.setStatus("Status - " + i);
		 * resultExecutionDTOList.add(resultExecutionDTO);
		 * 
		 * }
		 */

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
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "DFCC High Level Testing";
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
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		String stageName = "";
		if (resultExecutionDTOList != null) {
			stageName = resultExecutionDTOList.get(0).getStageName();

		}

		if (resultExecutionDTOList != null) {
			PdfPTable table = new PdfPTable(5); // 10 columns
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			table.setWidths(columnWidths);

			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
			String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At", "Status" };

			PdfPCell mergedCell = new PdfPCell(new Paragraph(stageName));
			mergedCell.setColspan(5);
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

			/*
			 * PdfPTable newTable = new PdfPTable(5); // 10 columns
			 * newTable.setWidthPercentage(100); // Width 100%
			 * newTable.setSpacingBefore(10f); // Space before table
			 * newTable.setSpacingAfter(10f); // Space after table
			 * 
			 * float[] newColumnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			 * newTable.setWidths(newColumnWidths);
			 * 
			 * PdfPCell newmergedCell = new PdfPCell(new Paragraph(stageName));
			 * newmergedCell.setColspan(5); newmergedCell.setFixedHeight(20);
			 * newmergedCell.setBackgroundColor(skyBlueColor);
			 * newmergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * newTable.addCell(newmergedCell);
			 * 
			 * for (String header : headers) { PdfPCell cell = new PdfPCell(new
			 * Phrase(header, headFont)); cell.setBackgroundColor(BaseColor.GRAY);
			 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); newTable.addCell(cell); }
			 */

			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {

				table.addCell(new Phrase(resultExecutionDTO.getTestMode()));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
				table.addCell(new Phrase(resultExecutionDTO.getDStarCount()));
				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
				// table.addCell(new Phrase(dto.getStageName()));
//					table.addCell(new Phrase(resultExecutionDTO.getStatus()));
				if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {

					table.addCell(new Phrase("FAIL"));
				} else {
					table.addCell(new Phrase("PASS"));
				}

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

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 */

		res.setResponseMessage("Brief Results Report Download Successfully...!");
		res.setResponseCode(1);
//		System.out.println("Brief result download check::" + res.getResponseMessage());
		return res;
	}

	// Details Report For Last Stage
	public Response generateDetailedReportForCurrentExecution(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "DetailedReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		// String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		String filePath = "";

		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}
		res.setDownloadPath(filePath);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		res.setDownloadPath(filePath);

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
			imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
			// imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
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

		String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
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
			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
//			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		} else {
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

		// Correct One

		/*
		 * PdfContentByte canvas = writer.getDirectContent(); float x =
		 * document.leftMargin(); float y = document.getPageSize().getHeight() -
		 * document.topMargin() - 100; // Adjust this value to position at // the top
		 * float width = document.getPageSize().getWidth() - document.leftMargin() -
		 * document.rightMargin(); float height = 100f; // Height of the rectangular box
		 * 
		 * // Draw the rectangular box canvas.setColorStroke(BaseColor.BLACK);
		 * canvas.rectangle(x, y, width, height); canvas.stroke();
		 * 
		 * // Add images and text inside the rectangular box String imagePath1 = ""; if
		 * (!DFCCConstant.isJarBuild) { imagePath1 =
		 * "C:\\Users\\Teclever\\Downloads\\BEL.jpeg"; } else {
		 * 
		 * imagePath1 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg"; }
		 * 
		 * // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";
		 * 
		 * String imagePath3 = ""; if (!DFCCConstant.isJarBuild) { imagePath3 =
		 * "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png"; } else { imagePath3 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg"; } // String
		 * imagePath3 = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		 * 
		 * Image img1 = Image.getInstance(imagePath1); Image img3 =
		 * Image.getInstance(imagePath3);
		 * 
		 * float imgWidth = (width - 20) / 3; // Calculate the width for each image
		 * float imgHeight = height - 20; // Calculate the height for each image
		 * 
		 * img1.scaleToFit(imgWidth, imgHeight + 10); img3.scaleToFit(imgWidth,
		 * imgHeight);
		 * 
		 * float imgY = y + 10; float imgX1 = x + 10; float imgX3 = x + 2 * (imgWidth +
		 * 10); // Adjusted to skip the middle section
		 * 
		 * img1.setAbsolutePosition(imgX1, imgY + 30); img3.setAbsolutePosition(imgX3,
		 * imgY + 10);
		 * 
		 * document.add(img1); document.add(img3);
		 */

		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		Map<String, String> sessionDetailsMap = new HashMap<String, String>();
		// Fetching The Session Details

		if (!sessionId.substring(0, 4).equals("TSSN")) {
			sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		} else {
			sessionDetailsMap = resultExecutionManagement.getTrailSessionDetailsBySessionId(sessionId);

		}

		String text = "DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor skyBlueColor = new BaseColor(0, 176, 196, 222);
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Session Details", headerFont1);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);

		Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("Created By                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionDetailedListForStages(sessionId);

		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();

		/*
		 * for (int i = 0; i < 100; i++) { ResultDetailedDTO resultDetailedDTO = new
		 * ResultDetailedDTO();
		 * 
		 * resultDetailedDTO.setExpectedValue(i + ".00");
		 * resultDetailedDTO.setFaultyChannel("CH" + i);
		 * resultDetailedDTO.setMeasuredValue(i + "80");
		 * resultDetailedDTO.setRdfName("rdf" + i); resultDetailedDTO.setStepName("" +
		 * i); resultDetailedDTO.setSignalName("SN_" + i);
		 * resultDetailedDTO.setTpfFileName("TPF_" + i);
		 * resultDetailedDTO.setTpgph("TPGH" + i); resultDetailedDTO.setUnit("UN-" + i);
		 * resultDetailedDTO.setTestName("TN-" + i);
		 * resultDetailedDTOList.add(resultDetailedDTO); }
		 */

		String stageName = "";
		if (resultExecutionDTOList != null) {
			stageName = resultExecutionDTOList.get(0).getStageName();

		}

		// Create table
		PdfPTable table = new PdfPTable(10); // 8 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 1f, 1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "Test Name", "RDF File", "File Name", "TPGPH NO", "STEP NO", "SIGNAL NAME",
				"Expected Value", "Measured Value", "FAULTY SRU", "UNIT" };

		PdfPCell mergedCell = new PdfPCell(new Paragraph(stageName));
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

		// Set the number of header rows
		table.setHeaderRows(1);

		// Add rows from list
		if (resultDetailedDTOList != null) {
			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				/*
				 * table.addCell(new Phrase(dto.getTestName())); table.addCell(new
				 * Phrase(dto.getTpgph())); table.addCell(new Phrase(dto.getStepName()));
				 * table.addCell(new Phrase(dto.getExpectedValue())); table.addCell(new
				 * Phrase(dto.getMeasuredValue())); table.addCell(new Phrase(dto.getUnit()));
				 * table.addCell(new Phrase(dto.getTpfFileName())); table.addCell(new
				 * Phrase(dto.getSignalName())); table.addCell(new
				 * Phrase(dto.getFaultyChannel())); table.addCell(new Phrase(dto.getRdfName()));
				 */
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

			}
		}

		document.add(table);
		document.close();

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 */ GetObjResponse sessionRes = new GetObjResponse();
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

		res.setResponseMessage("Details Session Results Download Successfully...!");
		res.setResponseCode(1);

		res.setResponseCode(1);
		return res;
	}

	// Brief Report for Selected Stages
	public Response generateBreifReportForCurrentExecution(String sessionId, String stageId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "BriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;

			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//			System.out.println("Reportpath Check:" + filePath);
		}

		res.setDownloadPath(filePath);

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		res.setDownloadPath(filePath);

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
			imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";

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

		String title = "Breif Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
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
			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";
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

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSelectedStages(sessionId,
				stageId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		/*
		 * for (int i = 1; i <= 100; i++) { ResultExecutionDTO resultExecutionDTO = new
		 * ResultExecutionDTO(); // "Test Name", "Rdf File Detials", "D*Count",
		 * "End At", "Stage Name" ,"Status"
		 * resultExecutionDTO.setTestFileName("Test Name -" + i);
		 * resultExecutionDTO.setDStarCount(i + "");
		 * resultExecutionDTO.setRdfFile("RDF FILE NAME -" + i);
		 * resultExecutionDTO.setEndTime("End Time  -00:00:00");
		 * resultExecutionDTO.setStageName("StageName-" + "Same");
		 * resultExecutionDTO.setStatus("Status - " + i);
		 * resultExecutionDTOList.add(resultExecutionDTO);
		 * 
		 * }
		 */

		// Map<String, String> sessionDetailsMap =
		// resultExecutionManagement.getSessionDetailsBySessionId(sessionId);

		// Session Details Fetching
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
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "DFCC High Level Testing";
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
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		String stageName = "";
		if (resultExecutionDTOList != null) {
			stageName = resultExecutionDTOList.get(0).getStageName();

		}

		if (resultExecutionDTOList != null) {
			PdfPTable table = new PdfPTable(5); // 10 columns
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			table.setWidths(columnWidths);

			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
			String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At", "Status" };

			PdfPCell mergedCell = new PdfPCell(new Paragraph(stageName));
			mergedCell.setColspan(5);
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

			/*
			 * PdfPTable newTable = new PdfPTable(5); // 10 columns
			 * newTable.setWidthPercentage(100); // Width 100%
			 * newTable.setSpacingBefore(10f); // Space before table
			 * newTable.setSpacingAfter(10f); // Space after table
			 * 
			 * float[] newColumnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			 * newTable.setWidths(newColumnWidths);
			 * 
			 * PdfPCell newmergedCell = new PdfPCell(new Paragraph(stageName));
			 * newmergedCell.setColspan(5); newmergedCell.setFixedHeight(20);
			 * newmergedCell.setBackgroundColor(skyBlueColor);
			 * newmergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * newTable.addCell(newmergedCell);
			 * 
			 * for (String header : headers) { PdfPCell cell = new PdfPCell(new
			 * Phrase(header, headFont)); cell.setBackgroundColor(BaseColor.GRAY);
			 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); newTable.addCell(cell); }
			 */

			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {

				table.addCell(new Phrase(resultExecutionDTO.getTestMode()));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
				table.addCell(new Phrase(resultExecutionDTO.getDStarCount()));
				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
				// table.addCell(new Phrase(dto.getStageName()));
//					table.addCell(new Phrase(resultExecutionDTO.getStatus()));
				if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {

					table.addCell(new Phrase("FAIL"));
				} else {
					table.addCell(new Phrase("PASS"));
				}

			}
			document.add(table);
		}

		document.close();

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 */

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

		res.setResponseMessage("Breif Results Download Successfully...!");
		res.setResponseCode(1);
		return res;
	}

	// Selected Stages Detailed Report
	public Response generateDetailedReportForCurrentExecution(String sessionId, String stageId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "DetailedReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		// String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		String filePath = "";

		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			// filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

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

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSelectedStages(sessionId,
				stageId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		// To Create Header

		// Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
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

		String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		document.newPage();

		res.setDownloadPath(filePath);

		String stageName = "";
		if (resultExecutionDTOList != null) {
			stageName = resultExecutionDTOList.get(0).getStageName();

		}

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
			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
//			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";	
			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		} else {
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

		// Correct One

		/*
		 * PdfContentByte canvas = writer.getDirectContent(); float x =
		 * document.leftMargin(); float y = document.getPageSize().getHeight() -
		 * document.topMargin() - 100; // Adjust this value to position at // the top
		 * float width = document.getPageSize().getWidth() - document.leftMargin() -
		 * document.rightMargin(); float height = 100f; // Height of the rectangular box
		 * 
		 * // Draw the rectangular box canvas.setColorStroke(BaseColor.BLACK);
		 * canvas.rectangle(x, y, width, height); canvas.stroke();
		 * 
		 * // Add images and text inside the rectangular box String imagePath1 = ""; if
		 * (!DFCCConstant.isJarBuild) { imagePath1 =
		 * "C:\\Users\\Teclever\\Downloads\\BEL.jpeg"; } else {
		 * 
		 * imagePath1 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg"; }
		 * 
		 * // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";
		 * 
		 * String imagePath3 = ""; if (!DFCCConstant.isJarBuild) { imagePath3 =
		 * "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png"; } else { imagePath3 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg"; } // String
		 * imagePath3 = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		 * 
		 * Image img1 = Image.getInstance(imagePath1); Image img3 =
		 * Image.getInstance(imagePath3);
		 * 
		 * float imgWidth = (width - 20) / 3; // Calculate the width for each image
		 * float imgHeight = height - 20; // Calculate the height for each image
		 * 
		 * img1.scaleToFit(imgWidth, imgHeight + 10); img3.scaleToFit(imgWidth,
		 * imgHeight);
		 * 
		 * float imgY = y + 10; float imgX1 = x + 10; float imgX3 = x + 2 * (imgWidth +
		 * 10); // Adjusted to skip the middle section
		 * 
		 * img1.setAbsolutePosition(imgX1, imgY + 30); img3.setAbsolutePosition(imgX3,
		 * imgY + 10);
		 * 
		 * document.add(img1); document.add(img3);
		 */

		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);
		// Map<String, String> sessionDetailsMap =
		// resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// To Fetch....
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		// Session Details Fetching
		Map<String, String> sessionDetailsMap = new HashMap<String, String>();
		if (!sessionId.substring(0, 4).equals("TSSN")) {
			sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		} else {
			sessionDetailsMap = resultExecutionManagement.getTrailSessionDetailsBySessionId(sessionId);

		}

		String text = "DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 20 + imgWidth / 2, imgY + imgHeight / 2, 0);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor skyBlueColor = new BaseColor(0, 176, 196, 222);
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Session Details", headerFont1);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);

		Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("Created By                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionDetailedListForStages(sessionId, stageId);
		Map<String, String> stageIdName = new HashMap<String, String>();
		stageIdName = resultExecutionManagement.getStageIdName();

		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();

		/*
		 * for (int i = 0; i < 100; i++) { ResultDetailedDTO resultDetailedDTO = new
		 * ResultDetailedDTO();
		 * 
		 * resultDetailedDTO.setExpectedValue(i + ".00");
		 * resultDetailedDTO.setFaultyChannel("CH" + i);
		 * resultDetailedDTO.setMeasuredValue(i + "80");
		 * resultDetailedDTO.setRdfName("rdf" + i); resultDetailedDTO.setStepName("" +
		 * i); resultDetailedDTO.setSignalName("SN_" + i);
		 * 
		 * resultDetailedDTO.setTpfFileName("TPF_" + i);
		 * resultDetailedDTO.setTpgph("TPGH" + i); resultDetailedDTO.setUnit("UN-" + i);
		 * resultDetailedDTO.setTestName("TN-" + i);
		 * resultDetailedDTOList.add(resultDetailedDTO); }
		 */

		// Create table
		/*
		 * PdfPTable table = new PdfPTable(10); // 10 columns
		 * table.setWidthPercentage(100); // Width 100% table.setSpacingBefore(10f); //
		 * Space before table table.setSpacingAfter(10f); // Space after table
		 * 
		 * 
		 * // Set Column widths float[] columnWidths = {1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f,
		 * 1f, 1f, 1f, 1f}; table.setWidths(columnWidths);
		 * 
		 * PdfPCell mergedCell = new PdfPCell(new Paragraph(stageIdName.get(stageId)));
		 * mergedCell.setColspan(10); mergedCell.setFixedHeight(20);
		 * mergedCell.setBackgroundColor(skyBlueColor);
		 * mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * table.addCell(mergedCell);
		 * 
		 * 
		 * // Add table header Font headFont = new Font(Font.FontFamily.HELVETICA, 12,
		 * Font.BOLD, BaseColor.WHITE); String[] headers = {"Test Name", "TPGPH",
		 * "Step Name", "Expected Value", "Measured Value", "Unit", "TPF File Name",
		 * "Signal Name", "Faulty Channel", "RDF Name"}; for (String header : headers) {
		 * PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
		 * cell.setBackgroundColor(BaseColor.GRAY);
		 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell); }
		 * 
		 * // Set the number of header rows table.setHeaderRows(1);
		 * 
		 * // Add rows from list if (resultDetailedDTOList != null) { for
		 * (ResultDetailedDTO dto : resultDetailedDTOList) { table.addCell(new
		 * Phrase(dto.getTestName())); table.addCell(new Phrase(dto.getTpgph()));
		 * table.addCell(new Phrase(dto.getStepName())); table.addCell(new
		 * Phrase(dto.getExpectedValue())); table.addCell(new
		 * Phrase(dto.getMeasuredValue())); table.addCell(new Phrase(dto.getUnit()));
		 * table.addCell(new Phrase(dto.getTpfFileName())); table.addCell(new
		 * Phrase(dto.getSignalName())); table.addCell(new
		 * Phrase(dto.getFaultyChannel())); table.addCell(new Phrase(dto.getRdfName()));
		 * } }
		 */

		// Create table
		PdfPTable table = new PdfPTable(10); // 9 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 1f, 1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "File Name", "TPGPH NO", "STEP NO", "SIGNAL NAME", "Expected Value", "Measured Value",
				"FAULTY SRU", "UNIT" };

		PdfPCell mergedCell = new PdfPCell(new Paragraph(stageName));
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

		// Set the number of header rows
		table.setHeaderRows(1);

		// Add rows from list
		if (resultDetailedDTOList != null) {
			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				/*
				 * table.addCell(new Phrase(dto.getTestName())); table.addCell(new
				 * Phrase(dto.getTpgph())); table.addCell(new Phrase(dto.getStepName()));
				 * table.addCell(new Phrase(dto.getExpectedValue())); table.addCell(new
				 * Phrase(dto.getMeasuredValue())); table.addCell(new Phrase(dto.getUnit()));
				 * table.addCell(new Phrase(dto.getTpfFileName())); table.addCell(new
				 * Phrase(dto.getSignalName())); table.addCell(new
				 * Phrase(dto.getFaultyChannel())); table.addCell(new Phrase(dto.getRdfName()));
				 */
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

			}
		}
		document.add(table);
		document.close();

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 * res.setResponseMessage("Detailed Results Report Download Successfully...!");
		 */

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

		res.setResponseCode(1);

		return res;
	}

	// For Session Id - Brief Report
	/*
	 * public Response generateBreifReportForCurrentSession(String sessionId) throws
	 * DocumentException, MalformedURLException, IOException { //yyyyMMdd_HHmmss
	 * //dd-MM-yyyy Response res = new Response(); Document document = new
	 * Document(PageSize.A4);
	 * 
	 * String fileName = "BriefReport_" + new
	 * SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()
	 * ) + ".pdf";
	 * 
	 * String filePath = ""; if (!DFCCConstant.isJarBuild) { filePath =
	 * "C:\\Users\\Teclever\\Downloads\\" + fileName; } else { filePath =
	 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
	 * } PdfWriter writer = PdfWriter.getInstance(document, new
	 * FileOutputStream(filePath)); document.open();
	 * 
	 * ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
	 * writer.setPageEvent(event); document.open();
	 * 
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * 
	 * // To Create Header
	 * 
	 * // Add The BEL Logo String imagePath = ""; if (!DFCCConstant.isJarBuild) {
	 * imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg"; } else { imagePath =
	 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg"; }
	 * 
	 * Image img = Image.getInstance(imagePath); img.scaleAbsolute(2, 1);
	 * img.scalePercent(100); img.setAlignment(Element.ALIGN_CENTER);
	 * document.add(img); document.add(new Paragraph("\n")); document.add(new
	 * Paragraph("\n"));
	 * 
	 * Font lineFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD,
	 * BaseColor.BLACK); Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24,
	 * Font.BOLD, BaseColor.BLACK);
	 * 
	 * String line = "____________________________________________________________";
	 * ; Paragraph linePara = new Paragraph(line, lineFont);
	 * linePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
	 * document.add(linePara);
	 * 
	 * String title = "Breif Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n"
	 * + "Testing"; ; Paragraph titlePara = new Paragraph(title, titleFont);
	 * titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
	 * document.add(titlePara);
	 * 
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * document.newPage();
	 * 
	 * 
	 * PdfContentByte canvas = writer.getDirectContent(); float x =
	 * document.leftMargin(); float y = document.getPageSize().getHeight() -
	 * document.topMargin() - 100; // Adjust this value to position at the top float
	 * width = document.getPageSize().getWidth() - document.leftMargin() -
	 * document.rightMargin(); float height = 100f; // Height of the rounded
	 * rectangular box
	 * 
	 * // Set the corner radius for the rectangle float cornerRadius = 20f; //
	 * Adjust this value to change how rounded the corners are
	 * 
	 * // Draw the rounded rectangular box canvas.setColorStroke(BaseColor.BLACK);
	 * canvas.roundRectangle(x, y, width, height, cornerRadius); // x, y, width,
	 * height, corner radius canvas.stroke();
	 * 
	 * // Add images and text inside the rounded rectangular box String imagePath1 =
	 * ""; if (!DFCCConstant.isJarBuild) { imagePath1 =
	 * "C:\\Users\\Teclever\\Downloads\\BEL.jpeg"; } else { imagePath1 =
	 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg"; }
	 * 
	 * String imagePath3 = ""; if (!DFCCConstant.isJarBuild) { imagePath3 =
	 * "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png"; } else { imagePath3 =
	 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg"; }
	 * 
	 * Image img1 = Image.getInstance(imagePath1); Image img3 =
	 * Image.getInstance(imagePath3);
	 * 
	 * float imgWidth = (width - 40) / 3; // Calculate the width for each image
	 * float imgHeight = height -40; // Calculate the height for each image
	 * 
	 * img1.scaleToFit(imgWidth-05, imgHeight-05); img3.scaleToFit(imgWidth-10,
	 * imgHeight-10);
	 * 
	 * float imgY = y + 10; float imgX1 = x + 10; float imgX3 = x + 2 * (imgWidth +
	 * 10); // Adjusted to skip the middle section
	 * 
	 * img1.setAbsolutePosition(imgX1, imgY + 30);
	 * img3.setAbsolutePosition(imgX3+30, imgY + 20);
	 * 
	 * document.add(img1); document.add(img3);
	 * 
	 * 
	 * //To Fetch.... ResultExecutionResponse resultExecutionResponse = new
	 * ResultExecutionResponse(); ResultExecutionManagement
	 * resultExecutionManagement = new ResultExecutionManagement();
	 * resultExecutionResponse =
	 * resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId)
	 * ; List<ResultExecutionDTO> resultExecutionDTOList = new
	 * ArrayList<ResultExecutionDTO>(); // resultExecutionDTOList =
	 * resultExecutionResponse.getResultDTOList();
	 * 
	 * for(int i=0;i<=100;i++) { ResultExecutionDTO resultExecutionDTO = new
	 * ResultExecutionDTO(); // /"Test Name", "Rdf File Detials", "D*Count",
	 * "End At", "Stage Name" ,"Status"
	 * resultExecutionDTO.setTestFileName("Test Name -"+i);
	 * resultExecutionDTO.setDStarCount(i+"");
	 * resultExecutionDTO.setRdfFile("RDF FILE NAME -"+i);
	 * resultExecutionDTO.setEndTime("End Time  -00:00:00");
	 * resultExecutionDTO.setStageName("StageName  -"+i);
	 * resultExecutionDTO.setStatus("Status - "+i);
	 * resultExecutionDTOList.add(resultExecutionDTO);
	 * 
	 * }
	 * 
	 * Map<String,String> sessionDetailsMap =
	 * resultExecutionManagement.getSessionDetailsBySessionId(sessionId); // Add
	 * text in place of the second image Font font = new
	 * Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK); Font
	 * headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD,
	 * BaseColor.BLACK);
	 * 
	 * //User Defined Colour.. BaseColor tecBlueColor = new BaseColor(0, 79, 104,
	 * 255); // RGB values (Red, Green, Blue) BaseColor skyBlueColor = new
	 * BaseColor(0,176,196,222); BaseColor belBlueColor = new BaseColor(1, 75, 174,
	 * 255); // RGB values (Red, Green, Blue) BaseColor tecGreenColor = new
	 * BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue) BaseColor
	 * tecCementColor = new BaseColor(151, 185, 196); Font highlightCementFont = new
	 * Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor); Font
	 * highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10,
	 * Font.BOLDITALIC, tecBlueColor); Font highlightbelBlueColor = new
	 * Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);
	 * 
	 * 
	 * 
	 * String text = "DFCC High Level Testing"; ColumnText.showTextAligned(canvas,
	 * Element.ALIGN_CENTER, new Phrase(text, font), x + imgWidth + 10 + imgWidth /
	 * 2, imgY + imgHeight / 2, 0);
	 * 
	 * 
	 * Font headerFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD,
	 * BaseColor.BLACK); document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n"
	 * + "\n"+"\n"));
	 * 
	 * 
	 * 
	 * Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
	 * Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"),
	 * highlightCementFont); Paragraph sessionNameDetailsParagraph = new
	 * Paragraph(); sessionNameDetailsParagraph.add(sessionNameChunk);
	 * sessionNameDetailsParagraph.add(sessionDetailsChunk);
	 * document.add(sessionNameDetailsParagraph);
	 * 
	 * 
	 * Chunk userNameChunk = new Chunk("User Name                ", headerFont);
	 * Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"),
	 * highlightCementFont); Paragraph userNameDetailsParagraph = new Paragraph();
	 * userNameDetailsParagraph.add(userNameChunk);
	 * userNameDetailsParagraph.add(userNameDetailsChunk);
	 * document.add(userNameDetailsParagraph);
	 * 
	 * 
	 * Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
	 * Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"),
	 * highlightCementFont); Paragraph dfccPartNoDetailsParagraph = new Paragraph();
	 * dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
	 * dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
	 * document.add(dfccPartNoDetailsParagraph);
	 * 
	 * 
	 * 
	 * 
	 * PdfPTable table = new PdfPTable(5); table.setWidthPercentage(100);
	 * table.setSpacingBefore(10f); table.setSpacingAfter(10f);
	 * 
	 * float[] columnWidths = {1.5f, 2.5f, 1.5f, 2f ,1f};
	 * table.setWidths(columnWidths);
	 * 
	 * Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
	 * BaseColor.WHITE); String[] headers = {"Test Name", "Rdf File Detials",
	 * "D*Count", "End At" ,"Status"}; for (String header : headers) { PdfPCell cell
	 * = new PdfPCell(new Phrase(header, headFont));
	 * cell.setBackgroundColor(BaseColor.GRAY);
	 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell); }
	 * 
	 * // Set the number of header rows table.setHeaderRows(1); // Add rows from
	 * list String stageName = ""; if (resultExecutionDTOList != null) { stageName =
	 * resultExecutionDTOList.get(0).getStageName(); PdfPCell mergedCell = new
	 * PdfPCell(new Paragraph(stageName)); mergedCell.setColspan(5);
	 * mergedCell.setFixedHeight(20); mergedCell.setBackgroundColor(skyBlueColor);
	 * mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * table.addCell(mergedCell); }
	 * 
	 * 
	 * if (resultExecutionDTOList != null) {
	 * 
	 * 
	 * for (ResultExecutionDTO dto : resultExecutionDTOList) { if
	 * (!stageName.equals(dto.getStageName())) { PdfPCell mergedCell = new
	 * PdfPCell(new Paragraph(dto.getStageName())); mergedCell.setColspan(5);
	 * mergedCell.setFixedHeight(20); mergedCell.setBackgroundColor(skyBlueColor);
	 * mergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 * table.addCell(mergedCell); } table.addCell(new
	 * Phrase(dto.getTestFileName())); table.addCell(new Phrase(dto.getRdfFilePath()
	 * + dto.getRdfFile())); table.addCell(new Phrase(dto.getDStarCount()));
	 * table.addCell(new Phrase(dto.getEndTime())); //table.addCell(new
	 * Phrase(dto.getStageName())); table.addCell(new Phrase(dto.getStatus())); } }
	 * 
	 * document.add(table); document.close();
	 * 
	 * return res; }
	 */

	// Create the Report ESS and PQT
	public Response generateBreifReportESSPQTSession(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "BriefReport_Session.pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + fileName;
		} else {
//			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		res.setDownloadPath(filePath);
		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		// getResultExecutionListBriefListForSession
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSession(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

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
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n"));

		Chunk sessionNameChunk = new Chunk("Session Name             ", headerFont);
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

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

				// If the stage name is different from the previous stage, create a new table
				// and add a heading
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

				table.addCell(new Phrase(sno + ""));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
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

		document.close();
		res.setResponseMessage("Brief Session Results Download Successfully...!");
		res.setResponseCode(1);
		return res;
	}

	public Response generateAnnexure(int annexureCount) throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "Annexure" + annexureCount + ".pdf";
		} else {
//			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/fileName;
			filePath = currentDirectory + File.separator + "Annexure" + annexureCount + ".pdf";

		}
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

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
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD, BaseColor.BLACK);
		String title = "Annexure - " + annexureCount;

		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.close();
		res.setResponseMessage("Brief Session Results Download Successfully...!");
		res.setResponseCode(1);
		return res;
	}

	// Session Brief Report
	public Response generateBreifReportForCurrentSession(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "BriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + fileName;
		} else {
//			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGeneration.HeaderFooter event = new ReportGeneration.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		res.setDownloadPath(filePath);

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
			imagePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\BEL.jpeg";
		} else {
//			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";

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

		String title = "Breif Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
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
			imagePath1 = "C:\\Users\\VIGNESH-TEC\\Downloads\\BEL.jpeg";
		} else {
//          imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\VIGNESH-TEC\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";
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

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		/*
		 * for(int i=1;i<=100;i++) { ResultExecutionDTO resultExecutionDTO = new
		 * ResultExecutionDTO(); // "Test Name", "Rdf File Detials", "D*Count",
		 * "End At", "Stage Name" ,"Status"
		 * resultExecutionDTO.setTestFileName("Test Name -"+i);
		 * resultExecutionDTO.setDStarCount(i+"");
		 * resultExecutionDTO.setRdfFile("RDF FILE NAME -"+i);
		 * resultExecutionDTO.setEndTime("End Time  -00:00:00");
		 * resultExecutionDTO.setStageName("StageName  -"+i);
		 * resultExecutionDTO.setStatus("Status - "+i);
		 * resultExecutionDTOList.add(resultExecutionDTO);
		 * 
		 * }
		 */

		// Map<String,String> sessionDetailsMap =
		// resultExecutionManagement.getSessionDetailsBySessionId(sessionId);

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
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "DFCC High Level Testing";
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
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		String stageName = "";
		if (resultExecutionDTOList != null) {
			stageName = resultExecutionDTOList.get(0).getStageName();

		}

		if (resultExecutionDTOList != null) {
			PdfPTable table = new PdfPTable(5); // 10 columns
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table
			table.setSpacingAfter(10f); // Space after table
			float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			table.setWidths(columnWidths);

			Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
			String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At", "Status" };

			PdfPCell mergedCell = new PdfPCell(new Paragraph(stageName));
			mergedCell.setColspan(5);
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

			/*
			 * PdfPTable newTable = new PdfPTable(5); // 10 columns
			 * newTable.setWidthPercentage(100); // Width 100%
			 * newTable.setSpacingBefore(10f); // Space before table
			 * newTable.setSpacingAfter(10f); // Space after table
			 * 
			 * float[] newColumnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			 * newTable.setWidths(newColumnWidths);
			 * 
			 * PdfPCell newmergedCell = new PdfPCell(new Paragraph(stageName));
			 * newmergedCell.setColspan(5); newmergedCell.setFixedHeight(20);
			 * newmergedCell.setBackgroundColor(skyBlueColor);
			 * newmergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * newTable.addCell(newmergedCell);
			 * 
			 * for (String header : headers) { PdfPCell cell = new PdfPCell(new
			 * Phrase(header, headFont)); cell.setBackgroundColor(BaseColor.GRAY);
			 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); newTable.addCell(cell); }
			 */

			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {
				// boolean newTableFlag = false;

				if (stageName.equals(resultExecutionDTO.getStageName())) {

					table.addCell(new Phrase(resultExecutionDTO.getTestFileName()));
					table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
					table.addCell(new Phrase(resultExecutionDTO.getDStarCount()));
					table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
					// table.addCell(new Phrase(dto.getStageName()));
//					table.addCell(new Phrase(resultExecutionDTO.getStatus()));
					
					if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {

						table.addCell(new Phrase("FAIL"));
						
					} else {
						table.addCell(new Phrase("PASS"));
					}

				} else {

					stageName = resultExecutionDTO.getStageName();
					document.add(table);
					document.add(new Paragraph("\n" + "\n"));
					table = new PdfPTable(5);
					table.setWidthPercentage(100); // Width 100%
					table.setSpacingBefore(10f); // Space before table
					table.setSpacingAfter(10f); // Space after table
					// float[] columnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
					table.setWidths(columnWidths);

					// Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
					// BaseColor.WHITE);
					// String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At",
					// "Status" };

					mergedCell = new PdfPCell(new Paragraph(stageName));
					mergedCell.setColspan(5);
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

					table.addCell(new Phrase(resultExecutionDTO.getTestFileName()));
					table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
					table.addCell(new Phrase(resultExecutionDTO.getDStarCount()));
					table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
					// table.addCell(new Phrase(dto.getStageName()));
//					table.addCell(new Phrase(resultExecutionDTO.getStatus()));
					if (!resultExecutionDTO.getStatus().equals("SUCCESS")) {

						table.addCell(new Phrase("FAIL"));
					} else {
						table.addCell(new Phrase("PASS"));
					}

				}

				// document.add(newTable);
			}

		}

		document.close();

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 */

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

		res.setResponseMessage("Brief Session Results Download Successfully...!");
//		System.out.println("Check Response Method:"+res) ;
		res.setResponseCode(1);
		return res;
	}

	// Session Detailed Results
	public Response generateDetailedReportForCurrentSession(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "DetailedReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
//			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

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
			imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BEL>jpeg";

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

		String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		document.newPage();
		res.setDownloadPath(filePath);

		// Correct One

		/*
		 * PdfContentByte canvas = writer.getDirectContent(); float x =
		 * document.leftMargin(); float y = document.getPageSize().getHeight() -
		 * document.topMargin() - 100; // Adjust this value to position at // the top
		 * float width = document.getPageSize().getWidth() - document.leftMargin() -
		 * document.rightMargin(); float height = 100f; // Height of the rectangular box
		 * 
		 * // Draw the rectangular box canvas.setColorStroke(BaseColor.BLACK);
		 * canvas.rectangle(x, y, width, height); canvas.stroke();
		 * 
		 * 
		 * // Add images and text inside the rectangular box String imagePath1 = ""; if
		 * (!DFCCConstant.isJarBuild) { imagePath1 =
		 * "C:\\Users\\manik\\Downloads\\BEL.jpeg"; } else { imagePath1 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg"; }
		 * 
		 * // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";
		 * String imagePath3 = ""; if (!DFCCConstant.isJarBuild) { imagePath3 =
		 * "C:\\Users\\manik\\Downloads\\TECLEVER_logo.png"; } else { imagePath3 =
		 * "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";
		 * 
		 * }
		 * 
		 * Image img1 = Image.getInstance(imagePath1); Image img3 =
		 * Image.getInstance(imagePath3);
		 * 
		 * float imgWidth = (width - 20) / 3; // Calculate the width for each image
		 * float imgHeight = height - 20; // Calculate the height for each image
		 * 
		 * img1.scaleToFit(imgWidth, imgHeight + 10); img3.scaleToFit(imgWidth,
		 * imgHeight);
		 * 
		 * float imgY = y + 10; float imgX1 = x + 10; float imgX3 = x + 2 * (imgWidth +
		 * 10); // Adjusted to skip the middle section
		 * 
		 * img1.setAbsolutePosition(imgX1, imgY + 30); img3.setAbsolutePosition(imgX3,
		 * imgY + 10);
		 * 
		 * document.add(img1); document.add(img3);
		 */

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
			imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		} else {
//          imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BEL.jpeg";
			imagePath1 = currentDirectory + File.separator + "Images" + File.separator + "BEL.jpeg";
		}

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/TECLEVER_logo.png";
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

		// Add text in place of the second image
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		Map<String, String> stageIdName = resultExecutionManagement.getStageIdName();
		// Map<String,String> sessionDetailsMap =
		// resultExecutionManagement.getSessionDetailsBySessionId(sessionId);

		// Session Details Fetching
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
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		String text = "DFCC High Level Testing";
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
		Chunk sessionDetailsChunk = new Chunk(sessionDetailsMap.get("sessionName"), highlightCementFont);
		Paragraph sessionNameDetailsParagraph = new Paragraph();
		sessionNameDetailsParagraph.add(sessionNameChunk);
		sessionNameDetailsParagraph.add(sessionDetailsChunk);
		document.add(sessionNameDetailsParagraph);

		Chunk userNameChunk = new Chunk("User Name                ", headerFont);
		Chunk userNameDetailsChunk = new Chunk(sessionDetailsMap.get("userName"), highlightCementFont);
		Paragraph userNameDetailsParagraph = new Paragraph();
		userNameDetailsParagraph.add(userNameChunk);
		userNameDetailsParagraph.add(userNameDetailsChunk);
		document.add(userNameDetailsParagraph);

		Chunk dfccPartNoChunk = new Chunk("DFCC Part No             ", headerFont);
		Chunk dfccPartNoDetailsChunk = new Chunk(sessionDetailsMap.get("dfccPartNo"), highlightCementFont);
		Paragraph dfccPartNoDetailsParagraph = new Paragraph();
		dfccPartNoDetailsParagraph.add(dfccPartNoChunk);
		dfccPartNoDetailsParagraph.add(dfccPartNoDetailsChunk);
		document.add(dfccPartNoDetailsParagraph);

		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForSession(sessionId);

		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();

		/*
		 * for (int i = 0; i < 100; i++) {
		 * 
		 * ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();
		 * 
		 * if (i <= 20 && i>=0) { resultDetailedDTO.setStageId("L1_001");
		 * 
		 * } if (i <= 40&&i>20) { resultDetailedDTO.setStageId("L2_002");
		 * 
		 * } if (i <= 60&&i>40) { resultDetailedDTO.setStageId("L3_001");
		 * 
		 * } if (i <= 80&&i>60) { resultDetailedDTO.setStageId("L4_001");
		 * 
		 * } if (i <= 100&&i>80) { resultDetailedDTO.setStageId("L5_001");
		 * 
		 * }
		 * 
		 * resultDetailedDTO.setExpectedValue(i + ".00");
		 * resultDetailedDTO.setFaultyChannel("CH" + i);
		 * resultDetailedDTO.setMeasuredValue(i + "80");
		 * resultDetailedDTO.setRdfName("rdf" + i); resultDetailedDTO.setStepName("" +
		 * i); resultDetailedDTO.setSignalName("SN_" + i);
		 * resultDetailedDTO.setTpfFileName("TPF_" + i);
		 * resultDetailedDTO.setTpgph("TPGH" + i); resultDetailedDTO.setUnit("UN-" + i);
		 * resultDetailedDTO.setTestName("TN-" + i);
		 * resultDetailedDTOList.add(resultDetailedDTO); }
		 */

		// Create table
		/*
		 * PdfPTable table = new PdfPTable(10); // 10 columns
		 * table.setWidthPercentage(100); // Width 100% table.setSpacingBefore(10f); //
		 * Space before table table.setSpacingAfter(10f); // Space after table
		 * 
		 * 
		 * // Set Column widths float[] columnWidths = {1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f,
		 * 1f, 1f, 1f, 1f}; table.setWidths(columnWidths);
		 * 
		 * 
		 * // Add table header Font headFont = new Font(Font.FontFamily.HELVETICA, 12,
		 * Font.BOLD, BaseColor.WHITE); String[] headers = {"Test Name", "TPGPH",
		 * "Step Name", "Expected Value", "Measured Value", "Unit", "TPF File Name",
		 * "Signal Name", "Faulty Channel", "RDF Name"}; for (String header : headers) {
		 * PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
		 * cell.setBackgroundColor(BaseColor.GRAY);
		 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell); }
		 * 
		 * // Set the number of header rows table.setHeaderRows(1);
		 * 
		 * 
		 * // Add rows from list if (resultDetailedDTOList != null) { for
		 * (ResultDetailedDTO dto : resultDetailedDTOList) { table.addCell(new
		 * Phrase(dto.getTestName())); //1 table.addCell(new Phrase(dto.getTpgph()));
		 * //2 table.addCell(new Phrase(dto.getStepName())); //3 table.addCell(new
		 * Phrase(dto.getExpectedValue())); //4 table.addCell(new
		 * Phrase(dto.getMeasuredValue())); //5 table.addCell(new
		 * Phrase(dto.getUnit())); //6 table.addCell(new Phrase(dto.getTpfFileName()));
		 * //7 table.addCell(new Phrase(dto.getSignalName())); //8 table.addCell(new
		 * Phrase(dto.getFaultyChannel())); //9 table.addCell(new
		 * Phrase(dto.getRdfName())); //10 } } // Add table to document
		 * document.add(table);
		 */
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
			String[] headers = { "Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit",
					"TPF File Name", "Signal Name", "Faulty Channel", "RDF Name" };

			for (String header : headers) {
				PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
				cell.setBackgroundColor(BaseColor.GRAY);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}

			/*
			 * PdfPTable newTable = new PdfPTable(5); // 10 columns
			 * newTable.setWidthPercentage(100); // Width 100%
			 * newTable.setSpacingBefore(10f); // Space before table
			 * newTable.setSpacingAfter(10f); // Space after table
			 * 
			 * float[] newColumnWidths = { 2f, 2f, 1.4f, 1.5f, 1.5f };
			 * newTable.setWidths(newColumnWidths);
			 * 
			 * PdfPCell newmergedCell = new PdfPCell(new Paragraph(stageName));
			 * newmergedCell.setColspan(5); newmergedCell.setFixedHeight(20);
			 * newmergedCell.setBackgroundColor(skyBlueColor);
			 * newmergedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * newTable.addCell(newmergedCell);
			 * 
			 * for (String header : headers) { PdfPCell cell = new PdfPCell(new
			 * Phrase(header, headFont)); cell.setBackgroundColor(BaseColor.GRAY);
			 * cell.setHorizontalAlignment(Element.ALIGN_CENTER); newTable.addCell(cell); }
			 */

			for (ResultDetailedDTO resultDetailedDTO : resultDetailedDTOList) {
				// boolean newTableFlag = false;

				if (stageName.equals(resultDetailedDTO.getStageId())) {

					table.addCell(new Phrase(resultDetailedDTO.getTestName())); // 1
					table.addCell(new Phrase(resultDetailedDTO.getTpgph())); // 2
					table.addCell(new Phrase(resultDetailedDTO.getStepName())); // 3
					table.addCell(new Phrase(resultDetailedDTO.getExpectedValue())); // 4
					table.addCell(new Phrase(resultDetailedDTO.getMeasuredValue())); // 5
					table.addCell(new Phrase(resultDetailedDTO.getUnit())); // 6
					table.addCell(new Phrase(resultDetailedDTO.getTpfFileName())); // 7
					table.addCell(new Phrase(resultDetailedDTO.getSignalName())); // 8
					table.addCell(new Phrase(resultDetailedDTO.getFaultyChannel())); // 9
					table.addCell(new Phrase(resultDetailedDTO.getRdfName())); // 10

				} else {

					stageName = resultDetailedDTO.getStageId();
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

					mergedCell = new PdfPCell(new Paragraph(stageName));
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

					table.addCell(new Phrase(resultDetailedDTO.getTestName())); // 1
					table.addCell(new Phrase(resultDetailedDTO.getTpgph())); // 2
					table.addCell(new Phrase(resultDetailedDTO.getStepName())); // 3
					table.addCell(new Phrase(resultDetailedDTO.getExpectedValue())); // 4
					table.addCell(new Phrase(resultDetailedDTO.getMeasuredValue())); // 5
					table.addCell(new Phrase(resultDetailedDTO.getUnit())); // 6
					table.addCell(new Phrase(resultDetailedDTO.getTpfFileName())); // 7
					table.addCell(new Phrase(resultDetailedDTO.getSignalName())); // 8
					table.addCell(new Phrase(resultDetailedDTO.getFaultyChannel())); // 9
					table.addCell(new Phrase(resultDetailedDTO.getRdfName())); // 10

				}

				// document.add(newTable);
			}
			document.setPageSize(PageSize.A4.rotate());
			document.add(table);
		}

		// Close the document
		document.close();

		/*
		 * SessionService sessionService = new SessionService(); GetObjResponse
		 * getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		 * SessionEntity sessionentity = (SessionEntity) getObjResponse.getObject();
		 * String sessionPathString = sessionentity.getPath()+File.separator+"report";
		 * Path fileFullPath = Path.of(filePath); SessionFileManagement
		 * sessionFileManagement = new SessionFileManagement(); Path sessionPath
		 * =Path.of(sessionPathString) ;
		 * sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
		 */

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

		res.setResponseMessage("Details Session Results Download Successfully...!");
		res.setResponseCode(1);

		return res;
	}

	// For UutType -Breif Not Used
	public Response generateBreifReportForCurrentUutType(String uutTypeId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "BriefReport_" + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance())
				+ ".pdf";
		String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
//      String filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/"+fileName;

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

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
		String imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		// String imagePath =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";

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

		String title = "Brief Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		document.newPage();

		res.setDownloadPath(filePath);
		// Correct One

		PdfContentByte canvas = writer.getDirectContent();
		float x = document.leftMargin();
		float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
		// the top
		float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		float height = 100f; // Height of the rectangular box

		// Draw the rectangular box
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.rectangle(x, y, width, height);
		canvas.stroke();

		// Add images and text inside the rectangular box
		String imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		// String imagePath1 =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";

		// String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

		String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		// String imagePath3 =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/TECLEVER_logo.png";

		Image img1 = Image.getInstance(imagePath1);
		Image img3 = Image.getInstance(imagePath3);

		float imgWidth = (width - 20) / 3; // Calculate the width for each image
		float imgHeight = height - 20; // Calculate the height for each image

		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);

		float imgY = y + 10;
		float imgX1 = x + 10;
		float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

		img1.setAbsolutePosition(imgX1, imgY + 30);
		img3.setAbsolutePosition(imgX3, imgY + 10);

		document.add(img1);
		document.add(img3);

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForUnit(uutTypeId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();

		// Map<String,String> sessionDetailsMap =
		// resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		String text = "DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);

		Paragraph uutTypeDetails = new Paragraph(
				" UUT Name         :" + "     " + resultExecutionResponse.getStageName(), headerFont);
		SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
		document.add(uutTypeDetails);

		// Create table
		PdfPTable table = new PdfPTable(7); // 10 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 1.5f, 2.5f, 0.5f, 2f, 1f, 1f, 1f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "Test Name", "Rdf File Detials", "D*Count", "End At", "Stage Name", "Session Name",
				"Status" };
		for (String header : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		// Add rows from list
		for (ResultExecutionDTO dto : resultExecutionDTOList) {
			table.addCell(new Phrase(dto.getTestFileName()));
			table.addCell(new Phrase(dto.getRdfFilePath() + dto.getRdfFile()));
			table.addCell(new Phrase(dto.getDStarCount()));
			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getStageName()));
			table.addCell(new Phrase(dto.getSessionName()));
			table.addCell(new Phrase(dto.getStatus()));
		}

		// Add table to document
		document.add(table);

		// Close the document
		document.close();
		res.setResponseCode(1);
		return res;
	}

	// For UutType Detailed - Not Used
	public Response generateDetailedReportForCurrentUutType(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "DetailedReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		// String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		String filePath = "/home/teclever_java_app/aitessreport/" + fileName;

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

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
		String imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		// String imagePath =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";

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

		String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		document.newPage();
		res.setDownloadPath(filePath);

		// Correct One

		PdfContentByte canvas = writer.getDirectContent();
		float x = document.leftMargin();
		float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
		// the top
		float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		float height = 100f; // Height of the rectangular box

		// Draw the rectangular box
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.rectangle(x, y, width, height);
		canvas.stroke();

		// Add images and text inside the rectangular box
		String imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		// String imagePath1 =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";

		// String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

		String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
		// String imagePath3 =
		// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";

		Image img1 = Image.getInstance(imagePath1);
		Image img3 = Image.getInstance(imagePath3);

		float imgWidth = (width - 20) / 3; // Calculate the width for each image
		float imgHeight = height - 20; // Calculate the height for each image

		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);

		float imgY = y + 10;
		float imgX1 = x + 10;
		float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

		img1.setAbsolutePosition(imgX1, imgY + 30);
		img3.setAbsolutePosition(imgX3, imgY + 10);

		document.add(img1);
		document.add(img3);

		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		String text = "DFCC High Level Testing";
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
				x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     Session Name", headerFont);
		SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     Stage Name", headerFont);
		SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          :" + "      User Name", headerFont);
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages("");

		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		for (int i = 0; i < 100; i++) {
			ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();

			resultDetailedDTO.setExpectedValue(i + ".00");
			resultDetailedDTO.setFaultyChannel("CH" + i);
			resultDetailedDTO.setMeasuredValue(i + "80");
			resultDetailedDTO.setRdfName("rdf" + i);
			resultDetailedDTO.setStepName("" + i);
			resultDetailedDTO.setSignalName("SN_" + i);
			resultDetailedDTO.setTpfFileName("TPF_" + i);
			resultDetailedDTO.setTpgph("TPGH" + i);
			resultDetailedDTO.setUnit("UN-" + i);
			resultDetailedDTO.setTestName("TN-" + i);
			resultDetailedDTOList.add(resultDetailedDTO);
		}

		// Create table
		PdfPTable table = new PdfPTable(10); // 10 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit",
				"TPF File Name", "Signal Name", "Faulty Channel", "RDF Name" };
		for (String header : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		// Add rows from list
		for (ResultDetailedDTO dto : resultDetailedDTOList) {
			table.addCell(new Phrase(dto.getTestName()));
			table.addCell(new Phrase(dto.getTpgph()));
			table.addCell(new Phrase(dto.getStepName()));
			table.addCell(new Phrase(dto.getExpectedValue()));
			table.addCell(new Phrase(dto.getMeasuredValue()));
			table.addCell(new Phrase(dto.getUnit()));
			table.addCell(new Phrase(dto.getTpfFileName()));
			table.addCell(new Phrase(dto.getSignalName()));
			table.addCell(new Phrase(dto.getFaultyChannel()));
			table.addCell(new Phrase(dto.getRdfName()));
		}

		// Add table to document
		document.add(table);

		// Close the document
		document.close();
		res.setResponseCode(1);
		return res;
	}

	static class HeaderFooter extends PdfPageEventHelper {

		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			addborder(writer); // adding Margins
		}

		public static void addborder(PdfWriter writer) {
			PdfContentByte cb = writer.getDirectContent();

			// Create a PdfTemplate for the entire page
			PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(), PageSize.A4.getHeight());

			// Draw a rectangle around the entire page
			template.rectangle(25, 25, PageSize.A4.getWidth() - 50, PageSize.A4.getHeight() - 50);
			template.stroke();

			cb.addTemplate(template, 0, 0);
		}

		private static final String COPYRIGHT_TEXT = "Powered By Teclever Solutions Pvt Ltd, Bangalore.";

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase footer = new Phrase(COPYRIGHT_TEXT, new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));

			// Get the current page number
			int pageNumber = writer.getPageNumber();
			Rectangle pageSize = document.getPageSize();
			float x = (pageSize.getLeft() + pageSize.getRight()) / 2.2f;
			float y = pageSize.getBottom() + 15; // Adjust position

			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
		}

	}

}
