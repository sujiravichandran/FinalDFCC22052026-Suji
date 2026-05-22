package com.teclever.dfcc.reportgeneration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
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
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class HistoryReport {
	
	
	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
	
	

	
	public Response generateSummaryResultForSessionNew(String sessionId)
			throws Exception {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		
		String fileName = "History_Report"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";		
		
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
		} else {
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;

		}

		res.setDownloadPath(filePath);
		
		ReportGeneration reportGeneration = new ReportGeneration();
		Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
		Response resDetailed = reportGeneration.generateDetailedReportESSPQTSession(sessionId);
		int resResultCode = resReport.getResponseCode();
		int resDetCode = resDetailed.getResponseCode();
		
		List<String> pdfFiles = new ArrayList<String>();
		Response reportFirst = new Response();
		reportFirst = generateSummaryResultFirstPage(sessionId);
		
		if(resResultCode==1||resDetCode==1)
		{
			pdfFiles.add(reportFirst.getDownloadPath());
		}
	
			//Brief Report
			if (resResultCode == 1) {
				if (!DFCCConstant.isJarBuild) {// s@i
					pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\BriefReport_Session.pdf");
				} else {
					pdfFiles.add(
							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
				}
			}
			
			
			//Detailed Data
			if (resDetCode == 1) {
				if (!DFCCConstant.isJarBuild) {
					pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\DetailedReport_Session.pdf");
				} else {
					pdfFiles.add(currentDirectory + File.separator + "Reports" + File.separator
							+ "DetailedReport_Session.pdf");
				}
			}
		
		
			Document document = new Document();
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
			document.open();

			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

			for (String pdf : pdfFiles) {
				PdfReader reader = new PdfReader(pdf);

				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					Paragraph sessionDetails = new Paragraph("Annexure - " + i, headerFont);
					sessionDetails.setAlignment(Element.ALIGN_CENTER);
					document.add(sessionDetails);
					copy.addPage(copy.getImportedPage(reader, i));
				}
				reader.close();
			}

			document.close();

			GetObjResponse sessionRes = new GetObjResponse();
			String sessionPathString = "";

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
			res.setResponseMessage("History Report Download Successfully!");
		
		return res;
	}
	
	
	public Response generateSummaryResultFirstPage(String sessionId)
			throws DocumentException, MalformedURLException, IOException, ParseException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy
		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "Summary_ReportFirst" + ".pdf";

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
			imagePath = "C:\\Users\\anujk\\Downloads\\bel_logo_hindi.png";

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

		String title = "History Report";
		;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		if (DFCCConstant.roleId.equals("RL_ID_4")) {

			UserManagementModule user = new UserManagementModule();
			UserLoginDetailsDto u = user.getUserByUserId(currentSessionDetails.getUserId());

			Blob signatureBlob = u.getDigitalSignature();
			if (signatureBlob != null) {
				byte[] imageBytes = convertBlobToByteArray(signatureBlob);

				Image image = Image.getInstance(imageBytes);

				float marginX = 250f; // distance from left edge
				float marginY = 200f; // distance from bottom edge
				float boxWidth = 100f;
				float boxHeight = 100f;

				image.scaleToFit(boxWidth, boxHeight);
				image.setAbsolutePosition(marginX, marginY); // bottom-left with margin

				PdfContentByte canvas1 = writer.getDirectContent();
				canvas1.addImage(image);
			}

		}
		document.close();

		return res;

	}
	
	
	
	  private byte[] convertBlobToByteArray(Blob blob) throws IOException {
	        InputStream inputStream = null;
	        try {
	            inputStream = blob.getBinaryStream();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	        byte[] buffer = new byte[4096];
	        int bytesRead;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            byteArrayOutputStream.write(buffer, 0, bytesRead);
	        }
	        return byteArrayOutputStream.toByteArray();
	    }

	
	public Response generateSummaryResultForSession(String sessionId)
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
			imagePath ="C:\\Users\\sharn\\Downloads\\bel_logo_hindi.png";
			
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

		String title = "History Report";
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

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSession(sessionId);
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
			SimpleDateFormat source = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
			SimpleDateFormat target =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			for (ResultExecutionDTO resultExecutionDTO : resultExecutionDTOList) {

				table.addCell(new Phrase(resultExecutionDTO.getTestMode()));
				table.addCell(new Phrase(resultExecutionDTO.getRdfFile()));
				table.addCell(new Phrase(resultExecutionDTO.getDStarCount()));
//				table.addCell(new Phrase(resultExecutionDTO.getEndTime()));
				String formattedEndTime =target.format(source.parse(resultExecutionDTO.getEndTime()));//sai30122025 first
				table.addCell(new Phrase(formattedEndTime));
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
//		////System.out.println("Brief result download check::" + res.getResponseMessage());
		return res;
	}

}
