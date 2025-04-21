package com.teclever.dfcc.reportgeneration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.teclever.datastore.configuration.DataStoreConfiguration;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.dto.StageLevelResponse;
import com.teclever.datastore.dto.SubLevelResponseDto;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.entities.TrailSessionEntity;
import com.teclever.datastore.service.LevelFiveMasterService;
import com.teclever.datastore.service.LevelFourMasterSevice;
import com.teclever.datastore.service.LevelOneMasterService;
import com.teclever.datastore.service.LevelThreeService;
import com.teclever.datastore.service.LevelTwoMasterService;
import com.teclever.datastore.service.SessionService;
import com.teclever.datastore.service.TrailSessionEntityService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.configurationmanagement.ReportCofigurationManagement;
import com.teclever.dfcc.datastore.dto.ReportConfigDto;
import com.teclever.dfcc.datastore.dto.ReportConfigResponse;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.dto.StageRemarksResponse;
import com.teclever.dfcc.datastore.dto.StagesRemarksDto;
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.reportgeneration.l.TOCEntry;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;
import com.teclever.dfcc.resultstore.dto.StageSummaryDetails;
import com.teclever.dfcc.resultstore.dto.SummaryDetails;
import com.teclever.dfcc.stateMachine.StateMachine.currentSessionDetails;

public class ReportGenerationNew extends PdfPageEventHelper {

	static String currentDirectory = new File(
			ReportGenerationNew.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
	private static final String COPYRIGHT_TEXT = "Powered By Teclever Solutions Pvt Ltd, Bangalore.";
	private static String reportType = "";
	private static String uUTType = "";
	private static String partNo = "";
	private static String title = "";
	private static String preface = "";
	private static String reportTitle = "";
	private static String preparedBy = "";
	private static String verifiedBy = "";
	private static String preparedDate = "";
	private static String verifiedDate = "";
	private static int currentPageNumber = 1;
	private static int totalPageCount = 0;
	private static int tocPlaceHolderCount = 1;
	private static int summaryPlaceHolderCount = 2;
	private static int tocPlaceHolderCountSub = 1;
	private static int summaryPlaceHolderCountSub = 1;
	private static int tocPlaceHolderCountH3 = 1;
	private static int summaryPlaceHolderCountH3 = 1;
	private static int totalPageNo = 0;
	// Declare totalPageTemplate as a class-level variable
	private static PdfTemplate totalPageTemplate;
	private static Image totalPageImage;

	private static List<TOCEntry> tocEntries = new ArrayList<>(); // List to store TOC entries
	// table to store placeholder for all chapters and sections
	private static Map<String, PdfTemplate> tocPlaceholder = new HashMap<String, PdfTemplate>();
//	private final static Map<String,Map<String,PdfTemplate>> tocSubPlaceHolder = new HashMap<String,Map<String,PdfTemplate>>();

	// store the chapters and sections with their title here.
	private static Map<String, Integer> pageByTitle = new HashMap<>();
	static Font tocFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

	static Map<String, Map<String, Map<String, String>>> data1 = null;
	static PdfWriter writer;
	static Document document = new Document(PageSize.A4);

//	static String excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";

	/*
	 * public static void main(String[] args) { // Create a document String
	 * pdfFilePath = "C:\\Users\\Teclever\\Downloads\\ReadExcelContent01nEW.pdf";
	 * String excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx"; try
	 * {
	 * 
	 * Map<String, Map<String, Map<String, String>>> data =
	 * readExcelForHeadingSubHeadings(excelPath); data1 = data; writer =
	 * PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
	 * document.open();
	 * 
	 * ReportGenerationNew.HeaderFooter event = new
	 * ReportGenerationNew.HeaderFooter(); writer.setPageEvent(event);
	 * document.open(); // createTOCByRead(); createSummary1(document, data); //
	 * addTableOfContents(document);
	 * 
	 * // HeaderFooter event1 = new HeaderFooter(); // writer.setPageEvent(event1);
	 * } catch (DocumentException | FileNotFoundException e) { e.printStackTrace();
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } finally { // Close the document document.close(); }
	 * 
	 */

	public String getPartno(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<SessionEntity> query = session.createQuery("FROM SessionEntity WHERE sessionId = :sessionId",
						SessionEntity.class);
				query.setParameter("sessionId", sessionId);
				SessionEntity sessionEntity = query.uniqueResult();
				return (sessionEntity != null) ? sessionEntity.getDfccPartNo() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getTrailPartno(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<TrailSessionEntity> query = session.createQuery(
						"FROM TrailSessionEntity WHERE trailSessionId = :trailSessionId", TrailSessionEntity.class);
				query.setParameter("trailSessionId", sessionId);

				TrailSessionEntity trailSessionEntity = query.uniqueResult();
				return (trailSessionEntity != null) ? trailSessionEntity.getDfccPartNo() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getSLNo(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<SessionEntity> query = session.createQuery("FROM SessionEntity WHERE sessionId = :sessionId",
						SessionEntity.class);
				query.setParameter("sessionId", sessionId);
				SessionEntity sessionEntity = query.uniqueResult();
				return (sessionEntity != null) ? sessionEntity.getDfccSNo() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getTrailSLNo(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<TrailSessionEntity> query = session.createQuery(
						"FROM TrailSessionEntity WHERE trailSessionId = :trailSessionId", TrailSessionEntity.class);
				query.setParameter("trailSessionId", sessionId);
				TrailSessionEntity trailSessionId = query.uniqueResult();
				return (trailSessionId != null) ? trailSessionId.getDfccSNo() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getUUTType(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<SessionEntity> query = session.createQuery("FROM SessionEntity WHERE sessionId = :sessionId",
						SessionEntity.class);
				query.setParameter("sessionId", sessionId);
				SessionEntity sessionEntity = query.uniqueResult();
				return (sessionEntity != null) ? sessionEntity.getUutId() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getTrailUUTType(String sessionId) {
		try {
			SessionFactory sessionFactory = DataStoreConfiguration.getSessionFactory();
			try (Session session = sessionFactory.openSession()) {
				Query<TrailSessionEntity> query = session.createQuery("FROM TrailSessionEntity WHERE trailSessionId = :trailSessionId",
						TrailSessionEntity.class);
				query.setParameter("trailSessionId", sessionId);
				TrailSessionEntity trailSessionEntity = query.uniqueResult();
				return (trailSessionEntity != null) ? trailSessionEntity.getUutTypeId() : null;

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String dfccPartNo;
	private static String dfccSLNo;
	private static String uutID;
	private static String uutType;

	// Generation Of Content For Ess Report..
	public Response generateEssReportContent(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		if (sessionId.startsWith("SASN")) {
			dfccPartNo = getPartno(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			dfccPartNo = getTrailPartno(sessionId);
		}
		
		if (sessionId.startsWith("SASN")) {
			dfccSLNo = getSLNo(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			dfccSLNo = getTrailSLNo(sessionId);
		}
		if (sessionId.startsWith("SASN")) {
		uutID = getUUTType(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			uutID = getTrailUUTType(sessionId);
		}

		if (uutID.equals("UUT1")) {
			uutType = "DFCC-MK1 " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT2")) {
			uutType = "DFCC-MK1A " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT3")) {
			uutType = "DFCC-MK2" + "- " + dfccSLNo;
		}


		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "ESSContent"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String updatedFilePath = "";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + fileName;
			updatedFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "updated_" + fileName;
		} else {
			// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// "ESSContent.pdf";
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
			updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;
		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\VIGNESH-TEC\\Downloads\\REPORT_FIELDS_ESS.xlsx";
		} else {
			// excelPath =
			// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS_ESS.xlsx";

			excelPath = currentDirectory + File.separator + "REPORT_FIELDS_ESS.xlsx";

		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);

		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();

		addImageToFirstPage(writer, document);

		essReportSummary(document, data, sessionId);
		document.close();

		// For Putting Page No
		PdfReader reader = new PdfReader(filePath);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(updatedFilePath));

		int totalPages = reader.getNumberOfPages();

		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		Font font = new Font(baseFont, 12, Font.NORMAL);
		String pageNumberText = "";
		for (int i = 1; i <= totalPages; i++) {
			PdfContentByte canvas = stamper.getOverContent(i);

			if (totalPages < 10) {
				pageNumberText = "0" + totalPages;
			} else {
				pageNumberText = "" + totalPages;
			}

			ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f,
					0);

			// PdfContentByte canvas1 = stamper.getOverContent(i);
			// Set the start and end points of the line
			float startX = 02.0f; // X-coordinate of the start point
			float endX = 35.0f; // X-coordinate of the end point
			float y = 440.0f; // Y-coordinate (same for start and end to make it horizontal)

			canvas.saveState();
			canvas.setLineWidth(1f); // Set line width as needed
			canvas.moveTo(startX, y); // Move to the start point
			canvas.lineTo(endX, y); // Draw to the end point
			canvas.stroke(); // Actually draw the line
			canvas.restoreState();

		}
		stamper.close();
		reader.close();
		// res.setResponseMessage(fileName);
		res.setResponseMessage("updated_" + fileName);
		return res;
	}

	private void addImageToFirstPage(PdfWriter writer, Document document) throws IOException, DocumentException {
		UserManagementModule user = new UserManagementModule();
		UserLoginDetailsDto u = user.getUserByUserId(currentSessionDetails.getUserId());

		Blob signatureBlob = u.getDigitalSignature();
		byte[] imageBytes = convertBlobToByteArray(signatureBlob);

		Image image = Image.getInstance(imageBytes);

		float x = 250f;
		float y = 400f;
		float boxWidth = 100f;
		float boxHeight = 100f;

		image.scaleToFit(boxWidth, boxHeight);
		image.setAbsolutePosition(x, y);
		PdfContentByte canvas = writer.getDirectContent();
		canvas.addImage(image);
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

	// Befroe Generation Of PQT Report Content
//	public Response generatePQTReportContent(String sessionId) throws DocumentException, MalformedURLException, IOException {
//
//		Response res = new Response();
//		Document document = new Document(PageSize.A4);
//		
//		String fileName = "PQTContent"
//				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
//
//		String filePath = "";
//		String updatedFilePath = "";
//		if (!DFCCConstant.isJarBuild) {
//			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + fileName;
//			updatedFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "updated_" + fileName;
//		} else {
//			// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +"PQTContent.pdf";
//			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//			updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;
//
//		}
//
//		String excelPath = "";
//		if (!DFCCConstant.isJarBuild) {
//			excelPath = "C:\\Users\\VIGNESH-TEC\\Downloads\\REPORT_FIELDS_PQT1.xlsx";
//		} else {
//			//excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS_PQT.xlsx";
//			
//			excelPath = currentDirectory +File.separator+"REPORT_FIELDS_PQT.xlsx";
//			
//		}
//
//		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
//		data1 = data;
//		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
//		document.open();
//
//		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
//		writer.setPageEvent(event);
//		document.open();
//	    addImageToFirstPage(writer, document);
//		pqtReportSummary(document, data,sessionId);
//		document.close();		
//		
//		
//		//For Putting Page No
//		PdfReader reader = new PdfReader(filePath);
//		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(updatedFilePath));
//
//		int totalPages = reader.getNumberOfPages();
//
//		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
//		Font font = new Font(baseFont, 12, Font.NORMAL);
//		String pageNumberText = "";
//		for (int i = 1; i <= totalPages; i++) {
//			PdfContentByte canvas = stamper.getOverContent(i);
//
//			if (totalPages < 10) {
//				pageNumberText = "0" + totalPages;
//			} else {
//				pageNumberText = "" + totalPages;
//			}
//
//			ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f,
//					0);
//			canvas.saveState();
//			canvas.setLineWidth(1f);
//			canvas.moveTo(02.0f, 440.0f);
//			canvas.lineTo(35.0f, 440.0f);
//			canvas.stroke();
//			canvas.restoreState();
//
//		}
//		stamper.close();
//		reader.close();
//		// res.setResponseMessage(fileName);
//		res.setResponseMessage("updated_" + fileName);
//		return res;
//	}

//	After Generation Of PQT Report Content
	public Response generatePQTReportContent(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		
		if (sessionId.startsWith("SASN")) {
			dfccPartNo = getPartno(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			dfccPartNo = getTrailPartno(sessionId);
		}
		
		if (sessionId.startsWith("SASN")) {
			dfccSLNo = getSLNo(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			dfccSLNo = getTrailSLNo(sessionId);
		}
		if (sessionId.startsWith("SASN")) {
		uutID = getUUTType(sessionId);
		} else if(sessionId.startsWith("TSSN")) {
			uutID = getTrailUUTType(sessionId);
		}

		if (uutID.equals("UUT1")) {
			uutType = "DFCC-MK1 " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT2")) {
			uutType = "DFCC-MK1A " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT3")) {
			uutType = "DFCC-MK2" + "- " + dfccSLNo;
		}

		if (uutID.equals("UUT1")) {
			uutType = "DFCC-MK1 " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT2")) {
			uutType = "DFCC-MK1A " + "- " + dfccSLNo;
		}
		if (uutID.equals("UUT3")) {
			uutType = "DFCC-MK2" + "- " + dfccSLNo;
		}


		Response res = new Response();
		Document document = new Document(PageSize.A4);

		String fileName = "PQTContent"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		String filePath = "";
		String updatedFilePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + fileName;
			updatedFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "updated_" + fileName;
		} else {
			// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/"
			// +"PQTContent.pdf";
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
			updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;

		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\VIGNESH-TEC\\Downloads\\REPORT_FIELDS_PQT1.xlsx";
		} else {
			// excelPath =
			// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS_PQT.xlsx";

			excelPath = currentDirectory + File.separator + "REPORT_FIELDS_PQT.xlsx";

		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		addImageToFirstPage(writer, document);
		pqtReportSummary(document, data, sessionId);
		document.close();

		// For Putting Page No
		PdfReader reader = new PdfReader(filePath);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(updatedFilePath));

		int totalPages = reader.getNumberOfPages();

		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		Font font = new Font(baseFont, 12, Font.NORMAL);
		String pageNumberText = "";
		for (int i = 1; i <= totalPages; i++) {
			PdfContentByte canvas = stamper.getOverContent(i);

			if (totalPages < 10) {
				pageNumberText = "0" + totalPages;
			} else {
				pageNumberText = "" + totalPages;
			}

			ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f,
					0);

			// PdfContentByte canvas1 = stamper.getOverContent(i);
			// Set the start and end points of the line
			float startX = 02.0f; // X-coordinate of the start point
			float endX = 35.0f; // X-coordinate of the end point
			float y = 440.0f; // Y-coordinate (same for start and end to make it horizontal)

			canvas.saveState();
			canvas.setLineWidth(1f);
			canvas.moveTo(02.0f, 440.0f);
			canvas.lineTo(35.0f, 440.0f);
			canvas.stroke();
			canvas.restoreState();

		}
		stamper.close();
		reader.close();
		// res.setResponseMessage(fileName);
		res.setResponseMessage("updated_" + fileName);
		return res;
	}

	// Generation Of ESS Report Content
	public Response generateEssReport(String sessionId) {

		currentPageNumber = 1;
		tocPlaceholder = new HashMap<String, PdfTemplate>();
		pageByTitle = new HashMap<>();
		tocPlaceHolderCount = 1;
		summaryPlaceHolderCount = 2;
		tocPlaceHolderCountSub = 1;
		summaryPlaceHolderCountSub = 1;
		tocPlaceHolderCountH3 = 1;
		summaryPlaceHolderCountH3 = 1;

		Response res = new Response();
		Response res1 = new Response();
		ReportGeneration reportGeneration = new ReportGeneration();
		try {
			res1 = generateEssReportContent(sessionId);

			String fileName = "ESS_Report"
					+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
			String filePath = "";
			String contentFilePath = "";
			if (!DFCCConstant.isJarBuild) {
				filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
				contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();
			} else {
				// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
				// fileName;
				filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
				// contentFilePath =
				// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
				// "PQTContent.pdf";
				contentFilePath = currentDirectory + File.separator + "Reports" + File.separator
						+ res1.getResponseMessage();

			}

			res.setDownloadPath(filePath);

			List<String> pdfFiles = new ArrayList<String>();

			Map<String, String> filesPathStageFullPath = new HashMap<String, String>();
			pdfFiles.add(contentFilePath);
			ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
			ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
			reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "ESS");
			int annexureCount = 1;
			for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
				File file = new File(reportConfigDTO.getFileName());
				ImageToPdfConverter img = new ImageToPdfConverter();
				boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");

				// String annexureFilePath =
				// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/" + "Annexure"
				// + annexureCount + ".pdf";
				String annexureFilePath = currentDirectory + File.separator + "Annexure" + annexureCount + ".pdf";
				File annexureFile = new File(annexureFilePath);
				if (checkPdf) {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);
					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(reportConfigDTO.getFileName());
				} else {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);
					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(img.pdfConvertor(reportConfigDTO.getFileName()));
				}
				annexureCount++;
			}

			reportGeneration.generateAnnexure(annexureCount);

			int finalAnnextureCount = annexureCount;

			pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");

			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
			int resResultCode = resReport.getResponseCode();
			if (resResultCode == 1) {
				if (!DFCCConstant.isJarBuild) {
					pdfFiles.add("C:\\Users\\VIGNESH-TEC\\Downloads\\BriefReport_Session.pdf");
				} else {
					pdfFiles.add(
							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
				}
				// pdfFiles.add(contentFilePath);
			}

			Document document = new Document();
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
			document.open();

			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

			// Loop through each PDF file
			for (String pdf : pdfFiles) {
				PdfReader reader = new PdfReader(pdf);

				// Loop through each page of the current PDF
				for (int i = 1; i <= reader.getNumberOfPages(); i++) {

					// Create the heading for each page (Annexure - X)
					Paragraph sessionDetails = new Paragraph("Annexure - " + i, headerFont);
					sessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
					document.add(sessionDetails); // Add the "Annexure - X" text to the document

					// Now add the actual page content from the PDF to the merged document
					copy.addPage(copy.getImportedPage(reader, i));
				}

				reader.close(); // Close the reader for this PDF
			}

			document.close(); // Close the document after adding all content

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
			res.setResponseMessage("Ess Report Download Successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

//	Before Suji Change
//	public Response generatePQTReport(String sessionId) {
//
//		currentPageNumber = 1;
//		tocPlaceholder = new HashMap<String, PdfTemplate>();
//		pageByTitle = new HashMap<>();
//		tocPlaceHolderCount = 1;
//		summaryPlaceHolderCount = 2;
//		tocPlaceHolderCountSub = 1;
//		summaryPlaceHolderCountSub = 1;
//		tocPlaceHolderCountH3 = 1;
//		summaryPlaceHolderCountH3 = 1;
//
//		Response res = new Response();
//		Response res1 = new Response();
//		ReportGeneration reportGeneration = new ReportGeneration();
//		try {
//
//			res1 = generatePQTReportContent(sessionId);
//			String fileName = "PQT_Report"
//					+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
////			Before Suji
////			String filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
////			String contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();
//			
////			After Suji
//			String filePath = "";
//			String contentFilePath = "";
//			if (!DFCCConstant.isJarBuild) {
//				filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
//				contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();
//			} else {
//				// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
//				// fileName;
//
//				filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
//				// contentFilePath =
//				// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
//				// "PQTContent.pdf";
//				contentFilePath = currentDirectory + File.separator + "Reports" + File.separator
//						+ res1.getResponseMessage();
//
//			}
//
//			res.setDownloadPath(filePath);
//
//			List<String> pdfFiles = new ArrayList<>();
//			pdfFiles.add(contentFilePath);
//
//			// Adding all other PDFs
//			ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
//			ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
//			reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
//			int annexureCount = 1;
//			for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
//				File file = new File(reportConfigDTO.getFileName());
//				ImageToPdfConverter img = new ImageToPdfConverter();
//				boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
////				Before Suji
////				String annexureFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "Annexure" + annexureCount + ".pdf";
//				
////				After Suji
//				String annexureFilePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/" + "Annexure" + annexureCount + ".pdf";
//				File annexureFile = new File(annexureFilePath);
//				if (checkPdf) {
//					if (annexureFile.exists()) {
//						pdfFiles.add(annexureFilePath);
//					} else {
//						reportGeneration.generateAnnexure(annexureCount);
//						pdfFiles.add(annexureFilePath);
//					}
//					pdfFiles.add(reportConfigDTO.getFileName());
//				} else {
//					if (annexureFile.exists()) {
//						pdfFiles.add(annexureFilePath);
//					} else {
//						reportGeneration.generateAnnexure(annexureCount);
//						pdfFiles.add(annexureFilePath);
//					}
//					pdfFiles.add(img.pdfConvertor(reportConfigDTO.getFileName()));
//				}
//				annexureCount++;
//			}
//
//			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
//			int resResultCode = resReport.getResponseCode();
//			if (resResultCode == 1) {
//				if (!DFCCConstant.isJarBuild) {
//					pdfFiles.add("C:\\Users\\VIGNESH-TEC\\Downloads\\BriefReport_Session.pdf");
//				} else {
//					pdfFiles.add(
//							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
//				}
//				//pdfFiles.add(contentFilePath);
//			}
//
//			// Create a new document for merging
//			Document document = new Document();
//			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
//			document.open(); // Open the document
//
//			// Header settings
//			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
//			int pdfCounter = 1;
//
//			// Loop through all PDF files
//			for (String inputPdf : pdfFiles) {
//				// First, add the heading "PDF - X"
//				Paragraph pdfHeading = new Paragraph("PDF - " + pdfCounter, headerFont);
//				pdfHeading.setAlignment(Element.ALIGN_CENTER);
//				document.add(pdfHeading); // Add heading to the document
//				document.add(new Paragraph("\n")); // Add a small gap
//
//				// Now let's process the PDF and add the pages
//				PdfReader reader = new PdfReader(inputPdf);
//				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePath, true));
//
//				// Loop through each page and add header on the first page
//				int numPages = reader.getNumberOfPages();
//				for (int i = 1; i <= numPages; i++) {
//					// For the first page of each PDF, we add a header
//					if (i == 1) {
//						// Create a new canvas for adding text on top of the page
//						PdfContentByte canvas = stamper.getOverContent(i);
//						ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
//								new Phrase("PDF - " + pdfCounter, headerFont), 300, 750, 0); // You can adjust x, y
//																								// positions as needed
//					}
//
//					// Copy page content to new document
//					PdfImportedPage page = copy.getImportedPage(reader, i);
//					copy.addPage(page);
//				}
//
//				stamper.close(); // Close the stamper for the current file
//				reader.close(); // Close the reader for the current PDF
//
//				pdfCounter++; // Increment PDF counter for the next document
//			}
//
//			document.close(); // Close the final document
//
//			// Copy the file to session path (optional step)
//			GetObjResponse sessionRes = new GetObjResponse();
//			String sessionPathString = "";
//			if (!sessionId.substring(0, 4).equals("TSSN")) {
//				SessionService sessionService = new SessionService();
//				sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
//				SessionEntity sessionEntity = (SessionEntity) sessionRes.getObject();
//				sessionPathString = sessionEntity.getPath();
//			} else {
//				TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
//				sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
//				TrailSessionEntity trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
//				sessionPathString = trailSessionEntity.getPath();
//			}
//
//			sessionPathString = sessionPathString + File.separator + "report";
//			Path fileFullPath = Path.of(filePath);
//			SessionFileManagement sessionFileManagement = new SessionFileManagement();
//			Path sessionPath = Path.of(sessionPathString);
//			sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);
//
//			res.setResponseCode(1);
//			res.setResponseMessage("PQT Report Download Successfully...!");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return res;
//	}

//	After Suji Change
	public Response generatePQTReport(String sessionId) {

		currentPageNumber = 1;
		tocPlaceholder = new HashMap<String, PdfTemplate>();
		pageByTitle = new HashMap<>();
		tocPlaceHolderCount = 1;
		summaryPlaceHolderCount = 2;
		tocPlaceHolderCountSub = 1;
		summaryPlaceHolderCountSub = 1;
		tocPlaceHolderCountH3 = 1;
		summaryPlaceHolderCountH3 = 1;

		Response res = new Response();
		Response res1 = new Response();
		ReportGeneration reportGeneration = new ReportGeneration();
		try {

			res1 = generatePQTReportContent(sessionId);
			String fileName = "PQT_Report"
					+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
			String filePath = "";
			String contentFilePath = "";
			if (!DFCCConstant.isJarBuild) {
				filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
				contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();
			} else {
				// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
				// fileName;
				filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
				// contentFilePath =
				// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
				// "PQTContent.pdf";
				contentFilePath = currentDirectory + File.separator + "Reports" + File.separator
						+ res1.getResponseMessage();

			}

			res.setDownloadPath(filePath);

			List<String> pdfFiles = new ArrayList<String>();

			Map<String, String> filesPathStageFullPath = new HashMap<String, String>();
			pdfFiles.add(contentFilePath);

			// Adding all other PDFs
			ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
			ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
			reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
			int annexureCount = 1;
			for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
				File file = new File(reportConfigDTO.getFileName());
				ImageToPdfConverter img = new ImageToPdfConverter();
				boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
				String annexureFilePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/" + "Annexure"
						+ annexureCount + ".pdf";
				File annexureFile = new File(annexureFilePath);
				if (checkPdf) {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);

					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(reportConfigDTO.getFileName());
				} else {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);
					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(img.pdfConvertor(reportConfigDTO.getFileName()));
				}
				annexureCount++;
			}

			reportGeneration.generateAnnexure(annexureCount);

			int finalAnnextureCount = annexureCount;

			pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");
			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);

			int resResultCode = resReport.getResponseCode();
			if (resResultCode == 1) {
				if (!DFCCConstant.isJarBuild) {
					pdfFiles.add("C:\\Users\\VIGNESH-TEC\\Downloads\\BriefReport_Session.pdf");
				} else {
					pdfFiles.add(
							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
				}
				// pdfFiles.add(contentFilePath);
			}

			// Create a new document for merging
			Document document = new Document();
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
			document.open(); // Open the document

			// Header settings
			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

			// Loop through each PDF file
			for (String pdf : pdfFiles) {
				PdfReader reader = new PdfReader(pdf);

				// Loop through each page of the current PDF
				for (int i = 1; i <= reader.getNumberOfPages(); i++) {

					// Create the heading for each page (Annexure - X)
					Paragraph sessionDetails = new Paragraph("Annexure - " + i, headerFont);
					sessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
					document.add(sessionDetails); // Add the "Annexure - X" text to the document

					// Now add the actual page content from the PDF to the merged document
					copy.addPage(copy.getImportedPage(reader, i));
				}

				reader.close(); // Close the reader for this PDF
			}

			document.close(); // Close the document after adding all content

			// Copy the file to session path (optional step)
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
			res.setResponseMessage("Pqt Report Download Successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public Response generatePQTReport2(String sessionId) {

		currentPageNumber = 1;
		tocPlaceholder = new HashMap<String, PdfTemplate>();
		pageByTitle = new HashMap<>();
		tocPlaceHolderCount = 1;
		summaryPlaceHolderCount = 2;
		tocPlaceHolderCountSub = 1;
		summaryPlaceHolderCountSub = 1;
		tocPlaceHolderCountH3 = 1;
		summaryPlaceHolderCountH3 = 1;

		Response res = new Response();
		Response res1 = new Response();
		ReportGeneration reportGeneration = new ReportGeneration();
		try {

			res1 = generatePQTReportContent(sessionId);
			String fileName = "PQT_Report"
					+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
			String filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
			String contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();

			res.setDownloadPath(filePath);

			List<String> pdfFiles = new ArrayList<>();
			pdfFiles.add(contentFilePath);

			// Adding all other PDFs
			ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
			ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
			reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
			int annexureCount = 1;
			for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
				File file = new File(reportConfigDTO.getFileName());
				ImageToPdfConverter img = new ImageToPdfConverter();
				boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
				String annexureFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + "Annexure" + annexureCount + ".pdf";
				File annexureFile = new File(annexureFilePath);
				if (checkPdf) {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);
					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(reportConfigDTO.getFileName());
				} else {
					if (annexureFile.exists()) {
						pdfFiles.add(annexureFilePath);
					} else {
						reportGeneration.generateAnnexure(annexureCount);
						pdfFiles.add(annexureFilePath);
					}
					pdfFiles.add(img.pdfConvertor(reportConfigDTO.getFileName()));
				}
				annexureCount++;
			}

			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
			int resResultCode = resReport.getResponseCode();
			if (resResultCode == 1) {
				if (!DFCCConstant.isJarBuild) {
					pdfFiles.add("C:\\Users\\VIGNESH-TEC\\Downloads\\BriefReport_Session.pdf");
				} else {
					pdfFiles.add(
							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
				}
//		pdfFiles.add(contentFilePath);
			}

			// Create a new document for merging
			Document document = new Document();
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
			document.open(); // Open the document

			// Header settings
			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
			int pdfCounter = 1;

			// Loop through all PDF files
			for (String inputPdf : pdfFiles) {
				// First, add the heading "PDF - X"
				Paragraph pdfHeading = new Paragraph("PDF - " + pdfCounter, headerFont);
				pdfHeading.setAlignment(Element.ALIGN_CENTER);
				document.add(pdfHeading); // Add heading to the document
				document.add(new Paragraph("\n")); // Add a small gap

				// Now let's process the PDF and add the pages
				PdfReader reader = new PdfReader(inputPdf);
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePath, true));

				// Loop through each page and add header on the first page
				int numPages = reader.getNumberOfPages();
				for (int i = 1; i <= numPages; i++) {
					// For the first page of each PDF, we add a header
					if (i == 1) {
						// Create a new canvas for adding text on top of the page
						PdfContentByte canvas = stamper.getOverContent(i);
						ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
								new Phrase("PDF - " + pdfCounter, headerFont), 300, 750, 0); // You can adjust x, y
																								// positions as needed
					}

					// Copy page content to new document
					PdfImportedPage page = copy.getImportedPage(reader, i);
					copy.addPage(page);
				}

				stamper.close(); // Close the stamper for the current file
				reader.close(); // Close the reader for the current PDF

				pdfCounter++; // Increment PDF counter for the next document
			}

			document.close(); // Close the final document

			// Copy the file to session path (optional step)
			GetObjResponse sessionRes = new GetObjResponse();
			String sessionPathString = "";
			if (!sessionId.substring(0, 4).equals("TSSN")) {
				SessionService sessionService = new SessionService();
				sessionRes = sessionService.getSessionDetailBySessionStageId(sessionId);
				SessionEntity sessionEntity = (SessionEntity) sessionRes.getObject();
				sessionPathString = sessionEntity.getPath();
			} else {
				TrailSessionEntityService trailSessionEntityService = new TrailSessionEntityService();
				sessionRes = trailSessionEntityService.getSessionDetailBySessionId(sessionId);
				TrailSessionEntity trailSessionEntity = (TrailSessionEntity) sessionRes.getObject();
				sessionPathString = trailSessionEntity.getPath();
			}

			sessionPathString = sessionPathString + File.separator + "report";
			Path fileFullPath = Path.of(filePath);
			SessionFileManagement sessionFileManagement = new SessionFileManagement();
			Path sessionPath = Path.of(sessionPathString);
			sessionFileManagement.copyFilesToOutputFolder(fileFullPath, sessionPath);

			res.setResponseCode(1);
			res.setResponseMessage("PQT Report Download Successfully...!");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// Generate PQT Report..
	public Response generatePQTReport1(String sessionId) {

		currentPageNumber = 1;
		tocPlaceholder = new HashMap<String, PdfTemplate>();
		pageByTitle = new HashMap<>();
		tocPlaceHolderCount = 1;
		summaryPlaceHolderCount = 2;
		tocPlaceHolderCountSub = 1;
		summaryPlaceHolderCountSub = 1;
		tocPlaceHolderCountH3 = 1;
		summaryPlaceHolderCountH3 = 1;

		Response res = new Response();
		Response res1 = new Response();
		try {
			res1 = generatePQTReportContent(sessionId);
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fileName = "PQT_Report"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		String contentFilePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\Reports\\" + fileName;
			contentFilePath = "C:\\Users\\VIGNESH-TEC\\Downloads\\" + res1.getResponseMessage();
		} else {
			// filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// fileName;

			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
			// contentFilePath =
			// "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" +
			// "PQTContent.pdf";
			contentFilePath = currentDirectory + File.separator + "Reports" + File.separator
					+ res1.getResponseMessage();

		}

		res.setDownloadPath(filePath);

		List<String> pdfFiles = new ArrayList<String>();
		Map<String, String> filesPathStageFullPath = new HashMap<String, String>();
		pdfFiles.add(contentFilePath);
		ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
		ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
		reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
		for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
			File file = new File(reportConfigDTO.getFileName());
			ImageToPdfConverter img = new ImageToPdfConverter();

			boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
			if (checkPdf) {
				pdfFiles.add(reportConfigDTO.getFileName());
			} else {
				pdfFiles.add(img.pdfConvertor(reportConfigDTO.getFileName()));
			}
		}

		try {
			ReportGeneration reportGeneration = new ReportGeneration();
			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
			int resResultCode = resReport.getResponseCode();
			if (resResultCode == 1) {
				pdfFiles.add("C:\\Users\\VIGNESH-TEC\\Downloads\\BriefReport_Session.pdf");
				// pdfFiles.add(currentDirectory + File.separator +
				// "Reports"+File.separator+"BriefReport_Session.pdf");
			}
			// Initialize the document and PdfCopy
			Document document = new Document();
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(filePath));
			document.open(); // Open the document to start adding content

			// Font settings for the header
			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

			// Variable to keep track of the PDF number
			int pdfCounter = 1;

			for (String inputPdf : pdfFiles) {
				// Add a heading for the current PDF
				if (pdfCounter > 2 && pdfCounter < pdfFiles.size()) {
					Paragraph pdfHeading = new Paragraph("PDF - " + pdfCounter, headerFont);
					pdfHeading.setAlignment(Element.ALIGN_CENTER); // Center the heading
					document.add(pdfHeading); // Add the heading to the document
				}

				// Add a small gap between heading and content
				document.add(new Paragraph("\n")); // Empty paragraph for spacing

				// Read the current PDF file
				PdfReader reader = new PdfReader(inputPdf);
				int numPages = reader.getNumberOfPages();

				// Loop through each page in the current PDF file
				for (int i = 1; i <= numPages; i++) {
					PdfImportedPage page = copy.getImportedPage(reader, i);
					copy.addPage(page); // Add the page from the input PDF to the merged output
				}
				reader.close(); // Close the reader for the current PDF

				// Increment the PDF counter for the next PDF
				pdfCounter++;
			}

			document.close(); // Close the document after adding all content

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
			res.setResponseMessage("PQT Report Download Successfully...!");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// Generation Of Breif Report For Given SessionId and StageId
	public Response generateBreifReportForCurrentExecution(String sessionId, String stageId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "StageBriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		} else {
			excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS.xlsx";
		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		// createTOCByRead();
		createSummary1(document, data);

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSelectedStages(sessionId,
				stageId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		document.newPage();

		/*
		 * GetObjResponse getObjResponse = new GetObjResponse(); SessionService
		 * sessionService = new SessionService(); getObjResponse =
		 * sessionService.getSessionDetailBySessionStageId(sessionId); SessionEntity
		 * sessionEntity =(SessionEntity) getObjResponse.getObject();
		 */

		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		// document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String, String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();

		Paragraph uutNameDetails = new Paragraph(" Uut Type Name      ", headerFont);
		uutNameDetails.add(new Chunk("    " + sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name       ", headerFont);
		SessionNameDetails.add(new Chunk("     " + sessionDetailsMap.get("sessionName"), highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         ", headerFont);
		StageNameDetails
				.add(new Chunk("     " + stageIdName.get(resultExecutionResponse.getStageName()), highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          ", headerFont);
		userNameDetails.add(new Chunk("     " + sessionDetailsMap.get("userName"), highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     " + sessionDetailsMap.get("dfccPartNo"), highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table

		PdfPTable table = new PdfPTable(4); // 4 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 2.5f, 2.5f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "S.NO", "Time Of Execution", "Executed File Name", "Result" };

		for (int i = 0; i < headers.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			if (i == 0) {
				cell.setPaddingLeft(10f);
			}
			if (i == headers.length - 1) {
				cell.setPaddingRight(10f);
			}

			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo = 1;

		for (ResultExecutionDTO dto : resultExecutionDTOList) {
			Font greenFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
			Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

			PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(sNo)));
			cell1.setPaddingLeft(10f); // Padding on the left side for the first column
			table.addCell(cell1);

			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getTestFileName()));

			PdfPCell cell4 = new PdfPCell(new Phrase(dto.getStatus()));
			cell4.setPaddingRight(10f); // Padding on the right side for the last column
			table.addCell(cell4);

			sNo++;
		}

		document.add(table);
		document.close();

		return res;
	}

	// Generation Of Detail Report For Last Executed Stage
	public Response generateDetailedReportForCurrentExecution(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "CurrentExecutionBriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		} else {
			excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS.xlsx";
		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		// createTOCByRead();
		createSummary1(document, data);

		// To Fetch....
		ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultDetailedResponse = resultExecutionManagement.getResultExecutionDetailedListForStages(sessionId);
		List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
		resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();
		document.newPage();

		/*
		 * GetObjResponse getObjResponse = new GetObjResponse(); SessionService
		 * sessionService = new SessionService(); getObjResponse =
		 * sessionService.getSessionDetailBySessionStageId(sessionId); SessionEntity
		 * sessionEntity =(SessionEntity) getObjResponse.getObject();
		 */

		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		// document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String, String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();

		Paragraph uutNameDetails = new Paragraph(" Uut Type Name      ", headerFont);
		uutNameDetails.add(new Chunk("    " + sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name       ", headerFont);
		SessionNameDetails.add(new Chunk("     " + sessionDetailsMap.get("sessionName"), highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         ", headerFont);
		StageNameDetails
				.add(new Chunk("     " + stageIdName.get(resultDetailedResponse.getStageName()), highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          ", headerFont);
		userNameDetails.add(new Chunk("     " + sessionDetailsMap.get("userName"), highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     " + sessionDetailsMap.get("dfccPartNo"), highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table

		PdfPTable table = new PdfPTable(8); // 4 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 1.2f, 1.4f, 1.5f, 1.5f, 1.5f, 1.3f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "S.NO", "File Name", "TPGPH NO", "SIGNAL NAME", "STEP NO", "EXPECTED VALUE (MIN)",
				"EXPECTED VALUE (MAX)", "MEASURED VALUE " };

		for (int i = 0; i < headers.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo = 1;

		/*
		 * for (ResultDetailedDTO dto : resultDetailedDTOList) { Font greenFont = new
		 * Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN); Font
		 * redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL,
		 * BaseColor.RED);
		 * 
		 * 
		 * 
		 * sNo++; }
		 */

		document.add(table);
		document.close();

		return res;
	}

	// Generation Of Details Report For Given SessionId and StageId
	public Response generateDetailedReportForCurrentExecution(String sessionId, String stageId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "StageBriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		} else {
			excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS.xlsx";
		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		// createTOCByRead();
		createSummary1(document, data);

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForSelectedStages(sessionId,
				stageId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		document.newPage();

		/*
		 * GetObjResponse getObjResponse = new GetObjResponse(); SessionService
		 * sessionService = new SessionService(); getObjResponse =
		 * sessionService.getSessionDetailBySessionStageId(sessionId); SessionEntity
		 * sessionEntity =(SessionEntity) getObjResponse.getObject();
		 */

		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		// document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String, String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();

		Paragraph uutNameDetails = new Paragraph(" Uut Type Name      ", headerFont);
		uutNameDetails.add(new Chunk("    " + sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name       ", headerFont);
		SessionNameDetails.add(new Chunk("     " + sessionDetailsMap.get("sessionName"), highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         ", headerFont);
		StageNameDetails
				.add(new Chunk("     " + stageIdName.get(resultExecutionResponse.getStageName()), highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          ", headerFont);
		userNameDetails.add(new Chunk("     " + sessionDetailsMap.get("userName"), highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     " + sessionDetailsMap.get("dfccPartNo"), highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table

		PdfPTable table = new PdfPTable(4); // 4 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 2.5f, 2.5f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "S.NO", "Time Of Execution", "Executed File Name", "Result" };

		for (int i = 0; i < headers.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			if (i == 0) {
				cell.setPaddingLeft(10f);
			}
			if (i == headers.length - 1) {
				cell.setPaddingRight(10f);
			}

			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo = 1;

		for (ResultExecutionDTO dto : resultExecutionDTOList) {
			Font greenFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
			Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

			PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(sNo)));
			cell1.setPaddingLeft(10f); // Padding on the left side for the first column
			table.addCell(cell1);

			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getTestFileName()));

			PdfPCell cell4 = new PdfPCell(new Phrase(dto.getStatus()));
			cell4.setPaddingRight(10f); // Padding on the right side for the last column
			table.addCell(cell4);

			sNo++;
		}

		document.add(table);
		document.close();

		return res;
	}

	// Session Result Report
	public Response generateSessionReportForStages(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "SessionReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}

		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		} else {
			excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS.xlsx";
		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		// createTOCByRead();
		createSummary1(document, data);

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();

		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		document.newPage();

		/*
		 * GetObjResponse getObjResponse = new GetObjResponse(); SessionService
		 * sessionService = new SessionService(); getObjResponse =
		 * sessionService.getSessionDetailBySessionStageId(sessionId); SessionEntity
		 * sessionEntity =(SessionEntity) getObjResponse.getObject();
		 */

		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		// document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String, String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();

		Paragraph uutNameDetails = new Paragraph(" Uut Type Name      ", headerFont);
		uutNameDetails.add(new Chunk("    " + sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name       ", headerFont);
		SessionNameDetails.add(new Chunk("     " + sessionDetailsMap.get("sessionName"), highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         ", headerFont);
		StageNameDetails
				.add(new Chunk("     " + stageIdName.get(resultExecutionResponse.getStageName()), highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          ", headerFont);
		userNameDetails.add(new Chunk("     " + sessionDetailsMap.get("userName"), highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     " + sessionDetailsMap.get("dfccPartNo"), highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table

		PdfPTable table = new PdfPTable(4); // 4 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 2.5f, 2.5f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "S.NO", "Time Of Execution", "Executed File Name", "Result" };

		for (int i = 0; i < headers.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			if (i == 0) {
				cell.setPaddingLeft(10f);
			}
			if (i == headers.length - 1) {
				cell.setPaddingRight(10f);
			}

			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo = 1;

		for (ResultExecutionDTO dto : resultExecutionDTOList) {
			Font greenFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
			Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

			PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(sNo)));
			cell1.setPaddingLeft(10f); // Padding on the left side for the first column
			table.addCell(cell1);

			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getTestFileName()));

			PdfPCell cell4 = new PdfPCell(new Phrase(dto.getStatus()));
			cell4.setPaddingRight(10f); // Padding on the right side for the last column
			table.addCell(cell4);

			sNo++;
		}

		document.add(table);
		document.close();

		return res;
	}

	public static void createTOCByRead1(Document document, Map<String, Map<String, Map<String, String>>> data)
			throws DocumentException {
		Paragraph tocTitle = new Paragraph("Table of Contents",
				new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK));
		tocTitle.setAlignment(Element.ALIGN_CENTER);
		document.add(tocTitle);

		tocPlaceHolderCount = 1;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();

			final String title = heading;
			final Chunk chunk = new Chunk(title).setLocalGoto(title);

			if (!heading.equals("Table of Contents")) {
				heading = title.replaceAll("-h1", "");
				Paragraph tocTitleHeading = new Paragraph("  " + tocPlaceHolderCount + " " + heading,
						new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLDITALIC, BaseColor.BLACK));
				tocTitleHeading.setAlignment(Element.ALIGN_LEFT);
				document.add(tocTitleHeading);
			}

			document.add(new VerticalPositionMark() {
				@Override
				public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx,
						final float ury, final float y) {
					final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
					if (!title.equals("Table of Contents")) {
						tocPlaceholder.put("  " + tocPlaceHolderCount + " " + title, createTemplate);
						canvas.addTemplate(createTemplate, urx - 55, y);
					}
				}
			});

			Map<String, Map<String, String>> subheadingMap = entry.getValue();
			tocPlaceHolderCountSub = 1;

			for (Map.Entry<String, Map<String, String>> subEntry : subheadingMap.entrySet()) {
				String subheading = subEntry.getKey();
				Map<String, String> h3Map = subEntry.getValue();

				if (subheading.contains("(h2)")) {
					final String subTitle = subheading;
					final Chunk subChunk = new Chunk(subTitle).setLocalGoto(subTitle);

					subheading = subTitle.replaceAll("(h2)", "");
					String subSubHeading = subheading.substring(0, subheading.length() - 2);

					Paragraph subheadingParagraph = new Paragraph(
							"      " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + " " + subSubHeading,
							new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK));
					subheadingParagraph.setAlignment(Element.ALIGN_LEFT);
					document.add(subheadingParagraph);

					document.add(new VerticalPositionMark() {
						@Override
						public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx,
								final float ury, final float y) {
							final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
							String subheading = subTitle.replaceAll("(h2)", "");
							String subSubHeading = subheading.substring(0, subheading.length() - 2);

							tocPlaceholder.put(
									"     " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + " " + subSubHeading,
									createTemplate);
							canvas.addTemplate(createTemplate, urx - 55, y);
						}
					});

					tocPlaceHolderCountH3 = 1;

					for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
						String h3 = h3Entry.getKey();
						String content = h3Entry.getValue();

						if (h3.contains("(h3)")) {
							final String h3Title = h3;
							final Chunk h3Chunk = new Chunk(h3Title).setLocalGoto(h3Title);

							h3 = h3.replaceAll("(h3)", "");
							String subSubHeadingh3 = h3.substring(0, h3.length() - 2);

							Paragraph h3Paragraph = new Paragraph(
									"         " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + "."
											+ tocPlaceHolderCountH3 + " " + subSubHeadingh3,
									new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK));
							h3Paragraph.setAlignment(Element.ALIGN_LEFT);
							document.add(h3Paragraph);

							document.add(new VerticalPositionMark() {
								@Override
								public void draw(final PdfContentByte canvas, final float llx, final float lly,
										final float urx, final float ury, final float y) {
									final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
									String h3 = h3Title.replaceAll("(h3)", "");
									String subHeadingh3 = h3.substring(0, h3.length() - 2);
									tocPlaceholder.put("         " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub
											+ "." + tocPlaceHolderCountH3 + " " + subHeadingh3, createTemplate);
									canvas.addTemplate(createTemplate, urx - 55, y);
								}
							});

							tocPlaceHolderCountH3++;
						}
					}

					tocPlaceHolderCountSub++;
				}
			}

			tocPlaceHolderCount++;
		}
	}

	public static void addTableOfContents(Document document) throws DocumentException {
		document.newPage();
		Paragraph tocTitle = new Paragraph("Table of Contents",
				new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK));
		tocTitle.setAlignment(Element.ALIGN_CENTER);
		document.add(tocTitle);

		for (TOCEntry entry : tocEntries) {
			Paragraph tocEntry = new Paragraph(entry.title + " ....... " + entry.page,
					new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK));
			tocEntry.setAlignment(Element.ALIGN_LEFT);
			document.add(tocEntry);
		}
	}

	public static void addborder(PdfWriter writer) {
		PdfContentByte cb = writer.getDirectContent();

		// Create a PdfTemplate for the entire page
		PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(), PageSize.A4.getHeight());

		// Draw a rectangle around the entire page
		template.rectangle(36, 36, PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
		template.stroke();

		cb.addTemplate(template, 0, 0);

	}

	// Data
	public static Map<String, Map<String, Map<String, String>>> readExcelForHeadingSubHeadings(String filePath)
			throws IOException {
		Map<String, Map<String, Map<String, String>>> data = new LinkedHashMap<>();
		FileInputStream fis = new FileInputStream(filePath);
		Workbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		String currentHeading = null;
		String currentSubheading = null;
		Map<String, Map<String, String>> subheadingMap = null;
		Map<String, String> h3Map = null;

		int rowCount = 0;
		for (Row row : sheet) {

			if (rowCount < 10) {

				Cell firstCell = row.getCell(0);
				Cell secondCell = row.getCell(1);
				Cell thirdCell = row.getCell(2);

				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Report Type")) {
					reportType = secondCell.toString();

//					 System.out.println("ESSS :::::Report Type" + reportType);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Part No")) {
//					partNo = secondCell.toString();
					partNo = dfccPartNo;

//					 System.out.println("ESS Part No" + partNo);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Title")) {
					title = secondCell.toString();
//					System.out.println("Title----->" + title);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Preface")) {
					preface = secondCell.toString();

				}

				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Report Title")) {
//					reportTitle = secondCell.toString();

					reportTitle = uutType;

				}

				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Prepared Date")) {
//					preparedDate = secondCell.toString();

					preparedDate = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
					// System.out.println("Prepared Date" + preparedDate);

				}
				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Verified Date")) {
//					verifiedDate = secondCell.toString();
					verifiedDate = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());

					// System.out.println("Verfied Date" + preface);

				}
				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Prepared By")) {
					preparedBy = secondCell.toString();
					// System.out.println("Prepared By" + preparedBy);

				}
				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Verified By")) {
					verifiedBy = secondCell.toString();
					// System.out.println("Verified By" + verifiedBy);

				}

			} else {
				Cell firstCell = row.getCell(0);
				Cell secondCell = row.getCell(1);
				Cell thirdCell = row.getCell(2);

				if (firstCell != null && !firstCell.toString().isEmpty()) {
					currentHeading = firstCell.toString().replaceAll("-h1", "");
					subheadingMap = new LinkedHashMap<>();
					data.put(currentHeading, subheadingMap);
				}

				if (secondCell != null && !secondCell.toString().isEmpty()) {
					currentSubheading = secondCell.toString().replaceAll("-h2", "");

					h3Map = new LinkedHashMap<>();
					if (subheadingMap != null) {
						subheadingMap.put(currentSubheading, h3Map);
					}
				}

				if (thirdCell != null && h3Map != null) {
					String h3 = thirdCell.toString().replaceAll("-h3", "");
					StringBuilder content = new StringBuilder();
					for (int i = 3; i < row.getLastCellNum(); i++) {
						Cell cell = row.getCell(i);
						if (cell != null) {
							content.append(cell.toString()).append(" ");
						}
					}
					h3Map.put(h3, content.toString().trim());
				}
			}
			rowCount++;
		}
		workbook.close();
		fis.close();
		return data;
	}

	// Before Suji changePQT Report
//	public static void pqtReportSummary(Document document, Map<String, Map<String, Map<String, String>>> data,
//			String sessionId) throws DocumentException, MalformedURLException, IOException {
//		// Starting Page
//
//		String imagePath = ""  ;
//		
//		
//		if (!DFCCConstant.isJarBuild) {
//			imagePath = "src/Resources/Images/BellLogoRocket.png";
//		} else {
//			//imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BellLogoRocket.png";
//			 imagePath = currentDirectory + File.separator + "Images"+File.separator+"BellLogoRocket.png";
//			System.out.println("CURRENT DIRECTORY"+currentDirectory);
//
//		}
//		Image img = Image.getInstance(imagePath);
//		img.scaleAbsolute(2, 1);
//		img.scalePercent(50);
//		img.setAlignment(Element.ALIGN_CENTER);
//		document.add(img);
//
//		Paragraph preface = new Paragraph();
//		preface.setAlignment(Element.ALIGN_CENTER);
//
//		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
//		Paragraph titlePara = new Paragraph(title, titleFont); // Assuming 'title' is a variable containing the title
//																// text
//		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
//		document.add(titlePara);
//
//		document.add(new Paragraph("\n"));
//		document.add(new Paragraph("\n"));
//
//		// Tab Adding
//		BaseColor textColor = new BaseColor(22, 28, 99);
//		BaseColor bcolor = new BaseColor(228, 239, 255);
//		Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
//		Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
//		Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
//		Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
//		Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
//		PdfPTable table = new PdfPTable(3);
//		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
//		table.setWidths(columnWidths);
//		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
//		cell1.setRowspan(1);
//		cell1.setColspan(3);
//		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
//		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(cell1);
//
//		PdfPCell cell2 = new PdfPCell(new Phrase(
//				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
//		cell2.setRowspan(2);
//		cell2.setColspan(3);
//		cell2.setFixedHeight(80f);
//		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
//		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(cell2);
//
//		PdfPCell cell3 = new PdfPCell(
//				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));
//
//		cell3.setRowspan(3);
//		cell3.setColspan(1);
//		cell3.setFixedHeight(100f);
//		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
//		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(cell3);
//
//		PdfPCell cell4 = new PdfPCell(
//				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
//		cell4.setRowspan(3);
//		cell4.setColspan(1);
//		cell4.setFixedHeight(60f);
//		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
//		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(cell4);
//
//		PdfPCell cell5 = new PdfPCell(
//				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
//		cell5.setRowspan(3);
//		cell5.setColspan(1);
//		cell5.setFixedHeight(60f);
//		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
//		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(cell5);
//
//		document.add(table);
//
//		document.newPage();
//		// int summaryPlaceHolderCount = 1;
//		int summaryPlaceHolderCountSub;
//		int summaryPlaceHolderCountH3;
//
//		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
//			String heading = entry.getKey();
//			Map<String, Map<String, String>> subheadings = entry.getValue();
//
//			if (heading.equalsIgnoreCase("Table Of Contents")) {
//				createTOCByRead1(document, data);
//				document.newPage();
//				continue;
//			}
//
//			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
//
//			document.add(
//					new Paragraph(" " + heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
//			// document.add(new Paragraph("\n"));
//
//			BaseFont baseFont1 = BaseFont.createFont();
//			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
//			System.out.println("Summary headingPageNum---->" + headingPageNum);
//
//			if (tocPlaceholder.containsKey(headingPageNum)) {
//				PdfTemplate template = tocPlaceholder.get(headingPageNum);
//				template.beginText();
//				template.setFontAndSize(baseFont1, 8);
//				if (writer.getPageNumber() > 10) {
//					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
//					template.showText(String.valueOf(writer.getPageNumber() - 1));
//					template.endText();
//				}
//
//				else {
//					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
//					template.showText(String.valueOf(writer.getPageNumber() - 1));
//					template.endText();
//
//				}
//			}
//
//			if (heading.contains("(TABLE)")) {
//				document.add(new Paragraph("\n"));
//				// String heading = entry.getKey();
//				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
//					String subheading = subEntry.getKey();
//					String content = subEntry.getKey();
//
//					// System.out.println("Content In Table:" + content);
//					String sep = content;
//					// System.out.println("sub Heading :" + subheading);
//					String[] subheadinglines = subheading.split("\n");
//
//					// System.out.println("Content Spilt :"+Arrays.toString(contentSpilt));
//					// System.out.println("subheadinglines Length" + subheadinglines.length);
//
//					String lines = subheadinglines[0];
//					// System.out.println("Lines " + lines);
//
//					String[] lineArray = lines.split("|");
//					// System.out.println("Line Array Length" + lineArray.length);
//
//					for (int i = 0; i <= subheadinglines.length - 1; i++) {
//						String[] lineSeparting = subheadinglines[i].split(";");
//						// System.out.println("Line Separting Length" + lineSeparting.length);
//						// System.out.println("subheadinglines---> Index" + i + "---" +
//						// subheadinglines[i]);
//						PdfPTable table1 = new PdfPTable(lineSeparting.length);
//
//						// table1.setWidthPercentage(100); // Width 100%
//						// table1.setSpacingBefore(10f); // Space before table
//						// table1.setSpacingAfter(10f); // Space after table
//
//						if (lineSeparting.length == 2) {
//							float[] columnWidthsInner = { 0.5f, 5f };
//							table1.setWidths(columnWidthsInner);
//
//						}
//						if (lineSeparting.length == 3) {
//							float[] columnWidthsInner = { 0.8f, 2f, 2f };
//							table1.setWidths(columnWidthsInner);
//
//						}
//						if (lineSeparting.length == 4) {
//							float[] columnWidthsInner = { 0.5f, 2f, 2f, 2.2f };
//							table1.setWidths(columnWidthsInner);
//
//						}
//						if (lineSeparting.length == 5) {
//							// SN;Test;Description;Results;Remarks
//							float[] columnWidthsInner = { 0.4f, 2f, 2f, 0.7f, 1.2f };
//							table1.setWidths(columnWidthsInner);
//
//						}
//						if (lineSeparting.length == 6) {
//							float[] columnWidthsInner = { 0.8f, 2f, 2f, 1f, 1f };
//							table1.setWidths(columnWidthsInner);
//
//						}
//
//						if (i == 0) {
//
//							Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
//							for (String header : lineSeparting) {
//								PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
//								cell.setBackgroundColor(BaseColor.GRAY);
//								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//								table1.addCell(cell);
//								// table1.setHeaderRows(1);
//
//							}
//						} else {
//
//							for (String cellContent : lineSeparting) {
//								PdfPCell cellCont = new PdfPCell(new Phrase(cellContent));
//								table1.addCell(cellCont);
//
//							}
//
//						}
//						document.add(table1);
//						// System.out.println("");
//
//					}
//
//					// document.add(new Paragraph(subheading, new Font(Font.FontFamily.HELVETICA,
//					// 10, Font.ITALIC)));
//					// document.add(new Paragraph(content, new Font(Font.FontFamily.HELVETICA,
//					// 10)));
//				}
//
//			}
//
//			else if (heading.contains("Appendix")) {
//
//				document.add(new Paragraph("\n"));
//				ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
//				ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
//				reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
//				List<ReportConfigDto> reportConfigDtoList = reportConfigResponse.getListOfReportConfigDto();
//			/*	int t = 1;
//				for (int i = 0; i <= 10; i++) {
//					ReportConfigDto r = new ReportConfigDto();
//					r.setFileName("C://Downloads//file" + t);
//					r.setLevelOneName("LevelOne/LevelTwo/LevelThree/LevelFour/LevelFive" + t);
//					reportConfigDtoList.add(r);
//				}*/
//				if (reportConfigDtoList.size() > 0) {
//					PdfPTable tableAppendix = new PdfPTable(3); // 3 columns
//					// tableAppendix.setWidthPercentage(100); // Width 100%
//					// tableAppendix.setSpacingBefore(20f); // Space before the table (e.g., 20
//					// units)
//					// tableAppendix.setSpacingAfter(20f); // Space after the table (e.g., 20 units)
//
//					// Set Column widths
//					float[] columnWidthsAppedix = { 0.8f, 2.5f, 2.5f };
//					tableAppendix.setWidths(columnWidthsAppedix);
//
//					// Add table header
//					Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
//					String[] headers = { "S.No", "Stage Name Description", "Appendix No" };
//					for (String header : headers) {
//						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
//						cell.setBackgroundColor(BaseColor.GRAY);
//						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//						tableAppendix.addCell(cell);
//					}
//
//					tableAppendix.setHeaderRows(1);
//
//					// Add rows from the list
//					int AppendixCount = 1;
//					for (ReportConfigDto dto : reportConfigDtoList) {
//						PdfPCell serialNumberCell = new PdfPCell(new Phrase(String.valueOf(AppendixCount)));
//						serialNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//						tableAppendix.addCell(serialNumberCell);
//
//						// tableAppendix.addCell(new Phrase(String.valueOf(AppendixCount)));
//						tableAppendix.addCell(new Phrase(dto.getLevelOneName()));
//						tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
//						AppendixCount++;
//					}
//
//					// Add the table to the document with space before and after
//					document.add(tableAppendix);
//				}
//
//			} /*else if (heading.contains("Session Details Summary")) {
//				System.out.println("Session Details Summary---For PQT Report");
//
//			}*/
//
//			else {
//
//				summaryPlaceHolderCountSub = 1;
//				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
//					String subheading = subEntry.getKey();
//					Map<String, String> h3Map = subEntry.getValue();
//
//					if (subheading.contains("(h2)")) {
//						
//						
//						
//						subheading = subheading.replaceAll("(h2)", "");
//						String subSubHeading = subheading.substring(0, subheading.length()-2);
//						
//						// subheading = subheading.replaceAll("(h2)", "");
//						// subheading = subheading.replace("()", "");
//
//						String subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
//								+ " " + subSubHeading;
//						System.out.println("Summary subHeadingPageNum" + subHeadingPageNum);
//						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
//							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
//							template.beginText();
//							template.setFontAndSize(baseFont1, 8);
//							if (writer.getPageNumber() > 10) {
//								template.setTextMatrix(
//										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
//								template.showText(String.valueOf(writer.getPageNumber() - 1));
//								template.endText();
//							} else {
//								template.setTextMatrix(
//										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
//								template.showText(String.valueOf(writer.getPageNumber() - 1));
//								template.endText();
//
//							}
//						}
//
//						summaryPlaceHolderCountH3 = 1;
//
//						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
//							String h3 = h3Entry.getKey();
//							String content = h3Entry.getValue();
//
//							if (h3.contains("(h3)")) {
//								h3 = h3.replaceAll("(h3)", "");
//								h3 = h3.substring(0,h3.length()-2);
//								
//								String h3PageNum = "         " + summaryPlaceHolderCount + "."
//										+ summaryPlaceHolderCountSub + "." + summaryPlaceHolderCountH3 + " " + h3;
//								System.out.println("Summary h3PageNum" + h3PageNum);
//								if (tocPlaceholder.containsKey(h3PageNum)) {
//									PdfTemplate template = tocPlaceholder.get(h3PageNum);
//									template.beginText();
//									template.setFontAndSize(baseFont1, 8);
//									if (writer.getPageNumber() > 10) {
//										template.setTextMatrix(50
//												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
//												0);
//										template.showText(String.valueOf(writer.getPageNumber() - 1));
//										template.endText();
//									} else {
//										template.setTextMatrix(50
//												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
//												0);
//										template.showText(String.valueOf(writer.getPageNumber() - 1));
//										template.endText();
//									}
//								}
//
//								// Get the remaining space on the current page
//								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
//
//								// Create a ColumnText object
//								ColumnText ct = new ColumnText(writer.getDirectContent());
//								ct.setSimpleColumn(36, 36, document.right() - document.left(),
//										document.top() - document.bottom());
//								ct.setLeading(0, 1.2f); // Adjust leading if needed
//								ct.setAlignment(Element.ALIGN_LEFT);
//
//								// Add a phrase or paragraph to the ColumnText
//								Phrase phrase = new Phrase(content);
//								ct.addText(phrase);
//
//								// Simulate adding the paragraph to measure its height (but not actually adding
//								// it yet)
//								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)
//
//								// Calculate the height of the paragraph
//								float paragraphHeight = document.top() - ct.getYLine();
//
//								if (paragraphHeight < 700) {
//									if (remainingSpace >= paragraphHeight) {
//										ct.go(); // This will actually add the content to the page
//									} else {
//										document.newPage(); // Add a new page if the content won't fit
//										ct.go(); // Then add the content to the new page
//									}
//								} else {
//									if (remainingSpace >= paragraphHeight) {
//										ct.go(); // This will actually add the content to the page
//									} else {
//										document.newPage(); // Add a new page if the content won't fit
//										ct.go(); // Then add the content to the new page
//									}
//								}
//
//								document.add(new Paragraph("        " + h3,
//										new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
//
//								if (paragraphHeight < 700) {
//									Paragraph contentParagraph = new Paragraph(content,
//											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//									contentParagraph.setIndentationLeft(5f); // Indent from the left margin
//									contentParagraph.setIndentationRight(5f); // Indent from the right margin
//									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
//									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
//									document.add(contentParagraph);
//								} else {
//									Paragraph contentParagraph = new Paragraph(content,
//											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//									// contentParagraph.setIndentationLeft(5f); // Indent from the left margin
//									// contentParagraph.setIndentationRight(5f); // Indent from the right margin
//									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
//									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
//									document.add(contentParagraph);
//
//								}
//
//								/*
//								 * document.add(new Paragraph("            " + content, new
//								 * Font(Font.FontFamily.TIMES_ROMAN, 10)));
//								 */
//							} else {
//
//								// Now Added
//
//								// Get the remaining space on the current page
//								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
//
//								// Create a ColumnText object
//								ColumnText ct = new ColumnText(writer.getDirectContent());
//								ct.setSimpleColumn(36, 36, document.right() - document.left(),
//										document.top() - document.bottom());
//								ct.setLeading(0, 1.2f); // Adjust leading if needed
//								ct.setAlignment(Element.ALIGN_LEFT);
//
//								// Add a phrase or paragraph to the ColumnText
//								Phrase phrase = new Phrase(h3);
//								ct.addText(phrase);
//
//								// Simulate adding the paragraph to measure its height (but not actually adding
//								// it yet)
//								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)
//
//								// Calculate the height of the paragraph
//								float paragraphHeight = document.top() - ct.getYLine();
//
//								if (paragraphHeight < 700) {
//									if (remainingSpace >= paragraphHeight) {
//										ct.go(); // This will actually add the content to the page
//									} else {
//										document.newPage(); // Add a new page if the content won't fit
//										ct.go(); // Then add the content to the new page
//									}
//								} else {
//									if (remainingSpace >= paragraphHeight) {
//										ct.go(); // This will actually add the content to the page
//									} else {
//										// document.newPage(); // Add a new page if the content won't fit
//										ct.go(); // Then add the content to the new page
//									}
//								}
//
//								document.add(new Paragraph("    " + subheading,
//										new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
//
//								if (paragraphHeight < 700) {
//									Paragraph h3Paragraph = new Paragraph(h3,
//											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
//									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
//									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
//									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
//									document.add(h3Paragraph);
//								} else {
//									Paragraph h3Paragraph = new Paragraph(h3,
//											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
//									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
//									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
//									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
//									document.add(h3Paragraph);
//
//								}
//
//								// document.add(new Paragraph(" " + h3,
//								// new Font(Font.FontFamily.TIMES_ROMAN, 10)));
//							}
//							summaryPlaceHolderCountH3++;
//						}
//					} else {
//
//						// Get the remaining space on the current page
//						float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
//
//						// Create a ColumnText object
//						ColumnText ct = new ColumnText(writer.getDirectContent());
//						ct.setSimpleColumn(36, 36, document.right() - document.left(),
//								document.top() - document.bottom());
//						ct.setLeading(0, 1.2f); // Adjust leading if needed
//						ct.setAlignment(Element.ALIGN_LEFT);
//
//						// Add a phrase or paragraph to the ColumnText
//						Phrase phrase = new Phrase(subheading);
//						ct.addText(phrase);
//
//						// Simulate adding the paragraph to measure its height (but not actually adding
//						// it yet)
//						int status = ct.go(true); // The 'true' argument means we are simulating (not writing)
//
//						// Calculate the height of the paragraph
//						float paragraphHeight = document.top() - ct.getYLine();
//
//						System.out.println("Heading   :" + heading + "    " + "-->" + paragraphHeight);
//
//						if (paragraphHeight < 700) {
//							if (remainingSpace >= paragraphHeight) {
//								ct.go();
//							} else {
//								document.newPage();
//								ct.go();
//							}
//						} else {
//							if (remainingSpace >= paragraphHeight) {
//								ct.go();
//							} else {
//								// document.newPage();
//								ct.go();
//							}
//						}
//
//						if (paragraphHeight < 700) {
//							Paragraph paragraph = new Paragraph(subheading,
//									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//							paragraph.setIndentationLeft(20); // Indent from the left margin
//							paragraph.setIndentationRight(20); // Indent from the right margin
//							paragraph.setSpacingBefore(10); // Space before the paragraph
//							paragraph.setSpacingAfter(10); // Space after the paragraph
//							document.add(paragraph);
//						} else {
//							Paragraph paragraph = new Paragraph(subheading,
//									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
//							paragraph.setSpacingBefore(10); // Space before the paragraph
//							paragraph.setSpacingAfter(10); // Space after the paragraph
//							document.add(paragraph);
//						}
//
//						// document.add(new Paragraph(" " + subheading,
//						// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
//
//						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
//							String content = h3Entry.getValue();
//							document.add(
//									new Paragraph("            " + content, new Font(Font.FontFamily.TIMES_ROMAN, 10)));
//						}
//					}
//					summaryPlaceHolderCountSub++;
//				}
//				if (heading.equals("Preface")) {
//					System.out.println("----------------Preface---------");
//					document.newPage();
//				}
//				document.add(new Paragraph("\n"));
//
//			}
//			
//
//			summaryPlaceHolderCount++;
//		}
//	}

//After Suji Change
	public static void pqtReportSummary(Document document, Map<String, Map<String, Map<String, String>>> data,
			String sessionId) throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "";

		if (!DFCCConstant.isJarBuild) {
			imagePath = "src/Resources/Images/BellLogoRocket.png";
		} else {
			// imagePath =
			// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BellLogoRocket.png";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BellLogoRocket.png";

		}
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont); // Assuming 'title' is a variable containing the title
																// text
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Tab Adding
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
		Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
		Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(
				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
		cell2.setRowspan(2);
		cell2.setColspan(3);
		cell2.setFixedHeight(80f);
		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(
				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));

		cell3.setRowspan(3);
		cell3.setColspan(1);
		cell3.setFixedHeight(100f);
		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(
				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
		cell4.setRowspan(3);
		cell4.setColspan(1);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(
				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
		cell5.setRowspan(3);
		cell5.setColspan(1);
		cell5.setFixedHeight(60f);
		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		document.add(table);

		document.newPage();
		// int summaryPlaceHolderCount = 1;
		int summaryPlaceHolderCountSub;
		int summaryPlaceHolderCountH3;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, Map<String, String>> subheadings = entry.getValue();

			if (heading.equalsIgnoreCase("Table Of Contents")) {
				createTOCByRead1(document, data);
				document.newPage();
				continue;
			}

			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
			// document.add(
			// new Paragraph(" " + heading, new Font(Font.FontFamily.TIMES_ROMAN, 14,
			// Font.BOLD, BaseColor.GRAY)));
			// document.add(new Paragraph("\n"));

			BaseFont baseFont1 = BaseFont.createFont();
			if (heading.contains("Preface")) {
				document.add(
						new Paragraph(heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

			} else {
				document.add(new Paragraph(" " + headingPageNum + "." + " ",
						new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
			}

			if (tocPlaceholder.containsKey(headingPageNum)) {
				PdfTemplate template = tocPlaceholder.get(headingPageNum);
				template.beginText();
				template.setFontAndSize(baseFont1, 8);
				if (writer.getPageNumber() > 10) {
					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
					template.showText(String.valueOf(writer.getPageNumber() - 1));
					template.endText();
				}

				else {
					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
					template.showText(String.valueOf(writer.getPageNumber() - 1));
					template.endText();

				}
			}

			if (heading.contains("(TABLE)")) {
				document.add(new Paragraph("\n"));
				// String heading = entry.getKey();
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					String content = subEntry.getKey();

					// System.out.println("Content In Table:" + content);
					String sep = content;
					// System.out.println("sub Heading :" + subheading);
					String[] subheadinglines = subheading.split("\n");

					// System.out.println("Content Spilt :"+Arrays.toString(contentSpilt));
					// System.out.println("subheadinglines Length" + subheadinglines.length);

					String lines = subheadinglines[0];
					// System.out.println("Lines " + lines);

					String[] lineArray = lines.split("|");
					// System.out.println("Line Array Length" + lineArray.length);

					for (int i = 0; i <= subheadinglines.length - 1; i++) {
						String[] lineSeparting = subheadinglines[i].split(";");
						// System.out.println("Line Separting Length" + lineSeparting.length);
						// System.out.println("subheadinglines---> Index" + i + "---" +
						// subheadinglines[i]);
						PdfPTable table1 = new PdfPTable(lineSeparting.length);

						// table1.setWidthPercentage(100); // Width 100%
						// table1.setSpacingBefore(10f); // Space before table
						// table1.setSpacingAfter(10f); // Space after table

						if (lineSeparting.length == 2) {
							float[] columnWidthsInner = { 0.5f, 5f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 3) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 4) {
							float[] columnWidthsInner = { 0.5f, 2f, 2f, 2.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 5) {
							// SN;Test;Description;Results;Remarks
							float[] columnWidthsInner = { 0.4f, 2f, 2f, 0.7f, 1.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 6) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f, 1f, 1f };
							table1.setWidths(columnWidthsInner);

						}

						if (i == 0) {

							Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
							for (String header : lineSeparting) {
								PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
								cell.setBackgroundColor(BaseColor.GRAY);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								table1.addCell(cell);
								// table1.setHeaderRows(1);

							}
						} else {

							for (String cellContent : lineSeparting) {
								PdfPCell cellCont = new PdfPCell(new Phrase(cellContent));
								table1.addCell(cellCont);

							}

						}
						document.add(table1);
					}

				}

			}

			else if (heading.contains("Appendix")) {

				document.add(new Paragraph("\n"));
				ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
				ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
				reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
				List<ReportConfigDto> reportConfigDtoList = reportConfigResponse.getListOfReportConfigDto();

				if (reportConfigDtoList.size() > 0) {
					PdfPTable tableAppendix = new PdfPTable(2); // 2 columns
					float[] columnWidthsAppedix = { 2.5f, 2.5f };
					tableAppendix.setWidths(columnWidthsAppedix);

					// Add table header
					Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
					String[] headers = { "Reference", "Description" };
					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						tableAppendix.addCell(cell);
					}

					tableAppendix.setHeaderRows(1);

					// Add rows from the list
					int AppendixCount = 1;
					for (ReportConfigDto dto : reportConfigDtoList) {
						tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
						tableAppendix.addCell(new Phrase(dto.getLevelOneName()));
						AppendixCount++;
					}

					tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
					tableAppendix.addCell(new Phrase("PQT test result summary"));
					// Add the table to the document with space before and after
					document.add(tableAppendix);
				}

			} /*
				 * else if (heading.contains("Session Details Summary")) {
				 * 
				 * }
				 */

			else {
				List<String> subHeadingTags = new ArrayList<String>();
				List<String> headingTags = new ArrayList<String>();

				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					Map<String, String> h3Map = subEntry.getValue();
					String subHeadingPageNum = "";
					if (subheading.contains("(h2)")) {

						subheading = subheading.replaceAll("(h2)", "");
						String subSubHeading = subheading.substring(0, subheading.length() - 2);

						subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub + " "
								+ subSubHeading;
						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
							template.beginText();
							template.setFontAndSize(baseFont1, 8);
							if (writer.getPageNumber() > 10) {
								template.setTextMatrix(
										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
								template.showText(String.valueOf(writer.getPageNumber() - 1));
								template.endText();
							} else {
								template.setTextMatrix(
										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
								template.showText(String.valueOf(writer.getPageNumber() - 1));
								template.endText();

							}
						}

						summaryPlaceHolderCountH3 = 1;
						// 10-04-2025
						document.add(new Paragraph("  " + subHeadingPageNum,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String h3 = h3Entry.getKey();
							String content = h3Entry.getValue();
							String h3PageNum = "";
							if (h3.contains("(h3)")) {
								h3 = h3.replaceAll("(h3)", "");
								h3 = h3.substring(0, h3.length() - 2);

								h3PageNum = "         " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
										+ "." + summaryPlaceHolderCountH3 + " " + h3;
								if (tocPlaceholder.containsKey(h3PageNum)) {
									PdfTemplate template = tocPlaceholder.get(h3PageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									} else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}
								}

								// Get the remaining space on the current page
								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

								// Create a ColumnText object
								ColumnText ct = new ColumnText(writer.getDirectContent());
								ct.setSimpleColumn(36, 36, document.right() - document.left(),
										document.top() - document.bottom());
								ct.setLeading(0, 1.2f); // Adjust leading if needed
								ct.setAlignment(Element.ALIGN_LEFT);

								// Add a phrase or paragraph to the ColumnText
								Phrase phrase = new Phrase(content);
								ct.addText(phrase);

								// Simulate adding the paragraph to measure its height (but not actually adding
								// it yet)
								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

								// Calculate the height of the paragraph
								float paragraphHeight = document.top() - ct.getYLine();

								if (paragraphHeight < 700) {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								} else {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								}
								// H1 Heading.... Checking 05
								if (!headingTags.contains(headingPageNum)) {
									if (tocPlaceholder.containsKey(headingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(headingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										}

										else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//									Suji Undo
//									document.add(
//											new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

									headingTags.add(headingPageNum);
								}

								// H2 Sub Heading Last
								if (!subHeadingTags.contains(subHeadingPageNum)) {
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
									subHeadingTags.add(subHeadingPageNum);
//								SUji Undo
//								document.add(new Paragraph("  " + subHeadingPageNum,
//										new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
								}

								document.add(new Paragraph("        " + h3PageNum,
										new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));

								if (paragraphHeight < 700) {
									Paragraph contentParagraph = new Paragraph(content,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									contentParagraph.setIndentationLeft(5f); // Indent from the left margin
									contentParagraph.setIndentationRight(5f); // Indent from the right margin
									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
									document.add(contentParagraph);
								} else {
									Paragraph contentParagraph = new Paragraph(content,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									// contentParagraph.setIndentationLeft(5f); // Indent from the left margin
									// contentParagraph.setIndentationRight(5f); // Indent from the right margin
									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
									document.add(contentParagraph);

								}

								/*
								 * document.add(new Paragraph("            " + content, new
								 * Font(Font.FontFamily.TIMES_ROMAN, 10)));
								 */
							} else {

								// Now Added

								// Get the remaining space on the current page
								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

								// Create a ColumnText object
								ColumnText ct = new ColumnText(writer.getDirectContent());
								ct.setSimpleColumn(36, 36, document.right() - document.left(),
										document.top() - document.bottom());
								ct.setLeading(0, 1.2f); // Adjust leading if needed
								ct.setAlignment(Element.ALIGN_LEFT);

								// Add a phrase or paragraph to the ColumnText
								Phrase phrase = new Phrase(h3);
								ct.addText(phrase);

								// Simulate adding the paragraph to measure its height (but not actually adding
								// it yet)
								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

								// Calculate the height of the paragraph
								float paragraphHeight = document.top() - ct.getYLine();

								if (paragraphHeight < 700) {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								} else {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										// document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								}

								// document.add(new Paragraph(" " + subHeadingPageNum,
								// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
								// May Be Need to Replace with h3PageNum
								if (paragraphHeight < 700) {
									// H1 Headings....Checking04
									if (!headingTags.contains(headingPageNum)) {
										if (tocPlaceholder.containsKey(headingPageNum)) {
											PdfTemplate template = tocPlaceholder.get(headingPageNum);
											template.beginText();
											template.setFontAndSize(baseFont1, 8);
											if (writer.getPageNumber() > 10) {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();
											}

											else {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();

											}
										}

										headingTags.add(headingPageNum);
//										Suji Undo
//										document.add(
//												new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
//									
									}

									// H2 Sub Heading
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//									Suji Undo
//									document.add(new Paragraph("  " + subHeadingPageNum,
//											new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

									Paragraph h3Paragraph = new Paragraph(h3,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
									document.add(h3Paragraph);
								} else {
									// H1 Heading
									// H1 Headings....Checking04
									if (!headingTags.contains(headingPageNum)) {
										if (tocPlaceholder.containsKey(headingPageNum)) {
											PdfTemplate template = tocPlaceholder.get(headingPageNum);
											template.beginText();
											template.setFontAndSize(baseFont1, 8);
											if (writer.getPageNumber() > 10) {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();
											}

											else {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();

											}
										}

										headingTags.add(headingPageNum);
//										Suji Undo
//										document.add(
//												new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

									}

									// H2 Sub Heading
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//Suji Undo
//									document.add(new Paragraph("  " + subHeadingPageNum,
//											new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

									Paragraph h3Paragraph = new Paragraph(h3,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
									document.add(h3Paragraph);

								}

								// document.add(new Paragraph(" " + h3,
								// new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							}
							summaryPlaceHolderCountH3++;
						}
					} else {

						// Get the remaining space on the current page
						float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

						// Create a ColumnText object
						ColumnText ct = new ColumnText(writer.getDirectContent());
						ct.setSimpleColumn(36, 36, document.right() - document.left(),
								document.top() - document.bottom());
						ct.setLeading(0, 1.2f); // Adjust leading if needed
						ct.setAlignment(Element.ALIGN_LEFT);

						// Add a phrase or paragraph to the ColumnText
						Phrase phrase = new Phrase(subheading);
						ct.addText(phrase);

						// Simulate adding the paragraph to measure its height (but not actually adding
						// it yet)
						int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

						// Calculate the height of the paragraph
						float paragraphHeight = document.top() - ct.getYLine();


						if (paragraphHeight < 700) {
							if (remainingSpace >= paragraphHeight) {
								ct.go();
							} else {
								document.newPage();
								ct.go();
							}
						} else {
							if (remainingSpace >= paragraphHeight) {
								ct.go();
							} else {
								// document.newPage();
								ct.go();
							}
						}

						if (paragraphHeight < 700) {
							// H1 Headings....Checking 02
							if (!headingTags.contains(headingPageNum)) {
								if (tocPlaceholder.containsKey(headingPageNum)) {
									PdfTemplate template = tocPlaceholder.get(headingPageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}

									else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();

									}
								}
//								Suji Undo
//								document.add(
//										new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

								headingTags.add(headingPageNum);
							}

							// H2 Sub Heading
							if (tocPlaceholder.containsKey(subHeadingPageNum)) {
								PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
								template.beginText();
								template.setFontAndSize(baseFont1, 8);
								if (writer.getPageNumber() > 10) {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								} else {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();

								}
							}
//							Suji Undo
//							document.add(new Paragraph("  " + subHeadingPageNum,
//									new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
							Paragraph paragraph = new Paragraph(subheading,
									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
							paragraph.setIndentationLeft(20); // Indent from the left margin
							paragraph.setIndentationRight(20); // Indent from the right margin
							paragraph.setSpacingBefore(10); // Space before the paragraph
							paragraph.setSpacingAfter(10); // Space after the paragraph
							document.add(paragraph);
						} else {
							// H1 Heading....Checking03
							if (!headingTags.contains(headingPageNum)) {
								if (tocPlaceholder.containsKey(headingPageNum)) {
									PdfTemplate template = tocPlaceholder.get(headingPageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}

									else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();

									}
								}
//								Suji Undo
//								document.add(
//										new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

								headingTags.add(headingPageNum);
							}

							// H2 Sub Heading
							if (tocPlaceholder.containsKey(subHeadingPageNum)) {
								PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
								template.beginText();
								template.setFontAndSize(baseFont1, 8);
								if (writer.getPageNumber() > 10) {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								} else {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();

								}
							}
							// H1 Headings..
							// document.add(new Paragraph(" " + subHeadingPageNum,
//						//			new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

							Paragraph paragraph = new Paragraph(subheading,
									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
							paragraph.setSpacingBefore(10); // Space before the paragraph
							paragraph.setSpacingAfter(10); // Space after the paragraph
							document.add(paragraph);
						}

						// document.add(new Paragraph(" " + subheading,
						// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String content = h3Entry.getValue();
							document.add(
									new Paragraph("            " + content, new Font(Font.FontFamily.TIMES_ROMAN, 10)));
						}
					}
					summaryPlaceHolderCountSub++;
				}
				if (heading.equals("Preface")) {
					document.newPage();
				}
				document.add(new Paragraph("\n"));

			}

			summaryPlaceHolderCount++;
		}
	}

	// ESS Report
	public static void essReportSummary(Document document, Map<String, Map<String, Map<String, String>>> data,
			String sessionId) throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "";

		if (!DFCCConstant.isJarBuild) {
			imagePath = "src/Resources/Images/BellLogoRocket.png";
		} else {
			// imagePath =
			// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BellLogoRocket.png";
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "BellLogoRocket.png";

		}

		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont); // Assuming 'title' is a variable containing the title
																// text
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Tab Adding
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
		Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
		Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(
				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
		cell2.setRowspan(2);
		cell2.setColspan(3);
		cell2.setFixedHeight(80f);
		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(
				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));

		cell3.setRowspan(3);
		cell3.setColspan(1);
		cell3.setFixedHeight(100f);
		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(
				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
		cell4.setRowspan(3);
		cell4.setColspan(1);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(
				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
		cell5.setRowspan(3);
		cell5.setColspan(1);
		cell5.setFixedHeight(60f);
		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		document.add(table);

		document.newPage();
		// int summaryPlaceHolderCount = 1;
		int summaryPlaceHolderCountSub;
		int summaryPlaceHolderCountH3;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, Map<String, String>> subheadings = entry.getValue();

			if (heading.equalsIgnoreCase("Table Of Contents")) {
				createTOCByRead1(document, data);
				document.newPage();
				continue;
			}

			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
			BaseFont baseFont1 = BaseFont.createFont();
			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
			if (heading.contains("Preface")) {
				document.add(
						new Paragraph(heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

			} else {
				document.add(new Paragraph(" " + headingPageNum + "." + " ",
						new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
			}

			if (tocPlaceholder.containsKey(headingPageNum)) {
				PdfTemplate template = tocPlaceholder.get(headingPageNum);
				template.beginText();
				template.setFontAndSize(baseFont1, 8);
				if (writer.getPageNumber() > 10) {
					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
					template.showText(String.valueOf(writer.getPageNumber() - 1));
					template.endText();
				}

				else {
					template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
					template.showText(String.valueOf(writer.getPageNumber() - 1));
					template.endText();

				}
			}

			if (heading.contains("(TABLE)")) {
				document.add(new Paragraph("\n"));
				// String heading = entry.getKey();
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					String content = subEntry.getKey();

					// System.out.println("Content In Table:" + content);
					String sep = content;
					// System.out.println("sub Heading :" + subheading);
					String[] subheadinglines = subheading.split("\n");

					// System.out.println("Content Spilt :"+Arrays.toString(contentSpilt));
					// System.out.println("subheadinglines Length" + subheadinglines.length);

					String lines = subheadinglines[0];
					// System.out.println("Lines " + lines);

					String[] lineArray = lines.split("|");
					// System.out.println("Line Array Length" + lineArray.length);

					for (int i = 0; i <= subheadinglines.length - 1; i++) {
						String[] lineSeparting = subheadinglines[i].split(";");
						// System.out.println("Line Separting Length" + lineSeparting.length);
						// System.out.println("subheadinglines---> Index" + i + "---" +
						// subheadinglines[i]);
						PdfPTable table1 = new PdfPTable(lineSeparting.length);

						// table1.setWidthPercentage(100); // Width 100%
						// table1.setSpacingBefore(10f); // Space before table
						// table1.setSpacingAfter(10f); // Space after table

						if (lineSeparting.length == 2) {
							float[] columnWidthsInner = { 0.5f, 5f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 3) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 4) {
							float[] columnWidthsInner = { 0.5f, 2f, 2f, 2.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 5) {
							// SN;Test;Description;Results;Remarks
							float[] columnWidthsInner = { 0.4f, 2f, 2f, 0.7f, 1.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 6) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f, 1f, 1f };
							table1.setWidths(columnWidthsInner);

						}

						if (i == 0) {

							Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
							for (String header : lineSeparting) {
								PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
								cell.setBackgroundColor(BaseColor.GRAY);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								table1.addCell(cell);
								// table1.setHeaderRows(1);

							}
						} else {

							for (String cellContent : lineSeparting) {
								PdfPCell cellCont = new PdfPCell(new Phrase(cellContent));
								table1.addCell(cellCont);

							}

						}
						document.add(table1);
					}

				}

			}

			else if (heading.contains("Appendix")) {

				document.add(new Paragraph(" " + headingPageNum + "." + " ",
						new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

				if (tocPlaceholder.containsKey(headingPageNum)) {
					PdfTemplate template = tocPlaceholder.get(headingPageNum);
					template.beginText();
					template.setFontAndSize(baseFont1, 8);
					if (writer.getPageNumber() > 10) {
						template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
								0);
						template.showText(String.valueOf(writer.getPageNumber() - 1));
						template.endText();
					}

					else {
						template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
								0);
						template.showText(String.valueOf(writer.getPageNumber() - 1));
						template.endText();

					}
				}

				document.add(new Paragraph("\n"));
				ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
				ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
				reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "ESS");
				List<ReportConfigDto> reportConfigDtoList = reportConfigResponse.getListOfReportConfigDto();

				if (reportConfigDtoList.size() > 0) {
					PdfPTable tableAppendix = new PdfPTable(2); // 2 columns
					float[] columnWidthsAppedix = { 2.5f, 2.5f };
					tableAppendix.setWidths(columnWidthsAppedix);

					// Add table header
					Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
					String[] headers = { "Reference", "Description" };
					for (String header : headers) {
						PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
						cell.setBackgroundColor(BaseColor.GRAY);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						tableAppendix.addCell(cell);
					}

					tableAppendix.setHeaderRows(1);

					// Add rows from the list
					int AppendixCount = 1;
					for (ReportConfigDto dto : reportConfigDtoList) {
						// tableAppendix.addCell(new Phrase(String.valueOf(AppendixCount)));
						tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
						tableAppendix.addCell(new Phrase(dto.getLevelOneName()));
						AppendixCount++;
					}
					tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
					tableAppendix.addCell(new Phrase("ESS test result summary"));

					// Add the table to the document with space before and after
					document.add(tableAppendix);
				}

			} else if (heading.contains("Session Details Summary")) {

				if (heading.contains("Session Details Summary")) {
					document.add(new Paragraph(" " + headingPageNum + "." + " ",
							new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
				}

				if (tocPlaceholder.containsKey(headingPageNum)) {
					PdfTemplate template = tocPlaceholder.get(headingPageNum);
					template.beginText();
					template.setFontAndSize(baseFont1, 8);
					if (writer.getPageNumber() > 10) {
						template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
								0);
						template.showText(String.valueOf(writer.getPageNumber() - 1));
						template.endText();
					}

					else {
						template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
								0);
						template.showText(String.valueOf(writer.getPageNumber() - 1));
						template.endText();

					}
				}
				document.add(new Paragraph("\n"));


				PdfPTable tableStageResult = new PdfPTable(6); // 4 columns
				// tableAppendix.setWidthPercentage(100); // Width 100%
				// tableAppendix.setSpacingBefore(20f); // Space before the table (e.g., 20
				// units)
				// tableAppendix.setSpacingAfter(20f); // Space after the table (e.g., 20 units)

				// Set Column widths
				float[] columnWidthsStageResults = { 1f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f };
				tableStageResult.setWidths(columnWidthsStageResults);

				// Add table header
				Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
				String[] headers = { "S.No", "Stage Name", "Start Date", "End Date", "Remarks", "Results" };
				for (String header : headers) {
					PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
					cell.setBackgroundColor(BaseColor.GRAY);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					tableStageResult.addCell(cell);
				}

				tableStageResult.setHeaderRows(1);

				// Add rows from the list

				int sNoCount = 1;

				SessionManagement sessionManagement = new SessionManagement();

				StageRemarksResponse stageRemarksResponse = new StageRemarksResponse();
				stageRemarksResponse = sessionManagement.getStagesRemarks(sessionId, "ESS");

				List<StagesRemarksDto> getStagesRemarksList = new ArrayList<StagesRemarksDto>();
				getStagesRemarksList = stageRemarksResponse.getRemarks();
				Map<String, String> stageIdName = new HashMap<String, String>();
				stageIdName = sessionManagement.getAllStageIdName();
				Map<String, String> stageIdResult = new HashMap<String, String>();
				SessionFileManagement sessionFileManagement = new SessionFileManagement();
				Map<String, Map<String, String>> LevelOneListOfIds = new HashMap<>();

				SummaryDetails summaryDetails = agetResultForSessionSummaryByLevelOne(getStagesRemarksList, sessionId);
				Map<String, String> levelOneIdResults = summaryDetails.getLevelOneIdResult();
				Map<String, String> levelOneIdStartDate = summaryDetails.getLevelOneIdStartDate();
				Map<String, String> levelOneIdEndDate = summaryDetails.getLevelOneIdEndDate();
				for (StagesRemarksDto dto : getStagesRemarksList) {
					PdfPCell serialNumberCell = new PdfPCell(new Phrase(String.valueOf(sNoCount)));
					serialNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					tableStageResult.addCell(serialNumberCell);

					// tableAppendix.addCell(new Phrase(String.valueOf(AppendixCount)));
					tableStageResult.addCell(new Phrase(dto.getLevelOneName()));
					tableStageResult.addCell(new Phrase(levelOneIdStartDate.get(dto.getLevelOneStageId())));
					tableStageResult.addCell(new Phrase(levelOneIdEndDate.get(dto.getLevelOneStageId())));
					tableStageResult.addCell(new Phrase(dto.getRemarks()));
					tableStageResult.addCell(new Phrase(levelOneIdResults.get(dto.getLevelOneStageId())));
					sNoCount++;
				}

				// Add the table to the document with space before and after
				document.add(tableStageResult);
			} else {
				List<String> subHeadingTags = new ArrayList<String>();
				List<String> headingTags = new ArrayList<String>();

				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					Map<String, String> h3Map = subEntry.getValue();
					String subHeadingPageNum = "";
					if (subheading.contains("(h2)")) {

						subheading = subheading.replaceAll("(h2)", "");
						String subSubHeading = subheading.substring(0, subheading.length() - 2);

						subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub + " "
								+ subSubHeading;
						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
							template.beginText();
							template.setFontAndSize(baseFont1, 8);
							if (writer.getPageNumber() > 10) {
								template.setTextMatrix(
										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
								template.showText(String.valueOf(writer.getPageNumber() - 1));
								template.endText();
							} else {
								template.setTextMatrix(
										50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
								template.showText(String.valueOf(writer.getPageNumber() - 1));
								template.endText();

							}
						}

						summaryPlaceHolderCountH3 = 1;
						// 09-04-2025
						document.add(new Paragraph("  " + subHeadingPageNum,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String h3 = h3Entry.getKey();
							String content = h3Entry.getValue();
							String h3PageNum = "";
							if (h3.contains("(h3)")) {
								h3 = h3.replaceAll("(h3)", "");
								h3 = h3.substring(0, h3.length() - 2);

								h3PageNum = "         " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
										+ "." + summaryPlaceHolderCountH3 + " " + h3;
								if (tocPlaceholder.containsKey(h3PageNum)) {
									PdfTemplate template = tocPlaceholder.get(h3PageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									} else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}
								}

								// Get the remaining space on the current page
								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

								// Create a ColumnText object
								ColumnText ct = new ColumnText(writer.getDirectContent());
								ct.setSimpleColumn(36, 36, document.right() - document.left(),
										document.top() - document.bottom());
								ct.setLeading(0, 1.2f); // Adjust leading if needed
								ct.setAlignment(Element.ALIGN_LEFT);

								// Add a phrase or paragraph to the ColumnText
								Phrase phrase = new Phrase(content);
								ct.addText(phrase);

								// Simulate adding the paragraph to measure its height (but not actually adding
								// it yet)
								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

								// Calculate the height of the paragraph
								float paragraphHeight = document.top() - ct.getYLine();

								if (paragraphHeight < 700) {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								} else {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								}
								// H1 Heading.... Checking 05
								if (!headingTags.contains(headingPageNum)) {
									if (tocPlaceholder.containsKey(headingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(headingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										}

										else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//									Suji Undo
//									document.add(
//											new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

									headingTags.add(headingPageNum);
								}

								// H2 Sub Heading Last
								if (!subHeadingTags.contains(subHeadingPageNum)) {
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
									subHeadingTags.add(subHeadingPageNum);
//								Suji Undo
//								document.add(new Paragraph("  " + subHeadingPageNum,
//										new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
								}
//Suji Undo
//								document.add(new Paragraph("        " + h3PageNum,
//										new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));

								if (paragraphHeight < 700) {
									Paragraph contentParagraph = new Paragraph(content,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									contentParagraph.setIndentationLeft(5f); // Indent from the left margin
									contentParagraph.setIndentationRight(5f); // Indent from the right margin
									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
									document.add(contentParagraph);
								} else {
									Paragraph contentParagraph = new Paragraph(content,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									// contentParagraph.setIndentationLeft(5f); // Indent from the left margin
									// contentParagraph.setIndentationRight(5f); // Indent from the right margin
									contentParagraph.setSpacingBefore(10f); // Space before the paragraph
									contentParagraph.setSpacingAfter(10f); // Space after the paragraph
									document.add(contentParagraph);

								}

								/*
								 * document.add(new Paragraph("            " + content, new
								 * Font(Font.FontFamily.TIMES_ROMAN, 10)));
								 */
							} else {

								// Now Added

								// Get the remaining space on the current page
								float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

								// Create a ColumnText object
								ColumnText ct = new ColumnText(writer.getDirectContent());
								ct.setSimpleColumn(36, 36, document.right() - document.left(),
										document.top() - document.bottom());
								ct.setLeading(0, 1.2f); // Adjust leading if needed
								ct.setAlignment(Element.ALIGN_LEFT);

								// Add a phrase or paragraph to the ColumnText
								Phrase phrase = new Phrase(h3);
								ct.addText(phrase);

								// Simulate adding the paragraph to measure its height (but not actually adding
								// it yet)
								int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

								// Calculate the height of the paragraph
								float paragraphHeight = document.top() - ct.getYLine();

								if (paragraphHeight < 700) {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								} else {
									if (remainingSpace >= paragraphHeight) {
										ct.go(); // This will actually add the content to the page
									} else {
										// document.newPage(); // Add a new page if the content won't fit
										ct.go(); // Then add the content to the new page
									}
								}

								// document.add(new Paragraph(" " + subHeadingPageNum,
								// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
								// May Be Need to Replace with h3PageNum
								if (paragraphHeight < 700) {
									// H1 Headings....Checking04
									if (!headingTags.contains(headingPageNum)) {
										if (tocPlaceholder.containsKey(headingPageNum)) {
											PdfTemplate template = tocPlaceholder.get(headingPageNum);
											template.beginText();
											template.setFontAndSize(baseFont1, 8);
											if (writer.getPageNumber() > 10) {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();
											}

											else {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();

											}
										}

										headingTags.add(headingPageNum);
//										Suji Undo
//										document.add(
//												new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

									}

									// H2 Sub Heading
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//									Suji Undo
//									document.add(new Paragraph("  " + subHeadingPageNum,
//											new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

									Paragraph h3Paragraph = new Paragraph(h3,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
									document.add(h3Paragraph);
								} else {
									// H1 Heading
									// H1 Headings....Checking04
									if (!headingTags.contains(headingPageNum)) {
										if (tocPlaceholder.containsKey(headingPageNum)) {
											PdfTemplate template = tocPlaceholder.get(headingPageNum);
											template.beginText();
											template.setFontAndSize(baseFont1, 8);
											if (writer.getPageNumber() > 10) {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();
											}

											else {
												template.setTextMatrix(50 - baseFont1
														.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
												template.showText(String.valueOf(writer.getPageNumber() - 1));
												template.endText();

											}
										}

										headingTags.add(headingPageNum);
//										Suji Undo
//										document.add(
//												new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

									}

									// H2 Sub Heading
									if (tocPlaceholder.containsKey(subHeadingPageNum)) {
										PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
										template.beginText();
										template.setFontAndSize(baseFont1, 8);
										if (writer.getPageNumber() > 10) {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();
										} else {
											template.setTextMatrix(50 - baseFont1
													.getWidthPoint(String.valueOf(writer.getPageNumber()), 14), 0);
											template.showText(String.valueOf(writer.getPageNumber() - 1));
											template.endText();

										}
									}
//									Suji Undo
//									document.add(new Paragraph("  " + subHeadingPageNum,
//											new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

									Paragraph h3Paragraph = new Paragraph(h3,
											new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
									h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
									h3Paragraph.setIndentationRight(5f); // Indent from the right margin
									h3Paragraph.setSpacingBefore(10); // Space before the paragraph
									h3Paragraph.setSpacingAfter(10); // Space after the paragraph
									document.add(h3Paragraph);

								}

								// document.add(new Paragraph(" " + h3,
								// new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							}
							summaryPlaceHolderCountH3++;
						}
					} else {

						// Get the remaining space on the current page
						float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();

						// Create a ColumnText object
						ColumnText ct = new ColumnText(writer.getDirectContent());
						ct.setSimpleColumn(36, 36, document.right() - document.left(),
								document.top() - document.bottom());
						ct.setLeading(0, 1.2f); // Adjust leading if needed
						ct.setAlignment(Element.ALIGN_LEFT);

						// Add a phrase or paragraph to the ColumnText
						Phrase phrase = new Phrase(subheading);
						ct.addText(phrase);

						// Simulate adding the paragraph to measure its height (but not actually adding
						// it yet)
						int status = ct.go(true); // The 'true' argument means we are simulating (not writing)

						// Calculate the height of the paragraph
						float paragraphHeight = document.top() - ct.getYLine();


						if (paragraphHeight < 700) {
							if (remainingSpace >= paragraphHeight) {
								ct.go();
							} else {
								document.newPage();
								ct.go();
							}
						} else {
							if (remainingSpace >= paragraphHeight) {
								ct.go();
							} else {
								// document.newPage();
								ct.go();
							}
						}

						if (paragraphHeight < 700) {
							// H1 Headings....Checking 02
							if (!headingTags.contains(headingPageNum)) {
								if (tocPlaceholder.containsKey(headingPageNum)) {
									PdfTemplate template = tocPlaceholder.get(headingPageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}

									else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();

									}
								}
//								Suji Undo
//								document.add(
//										new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

								headingTags.add(headingPageNum);
							}

							// H2 Sub Heading
							if (tocPlaceholder.containsKey(subHeadingPageNum)) {
								PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
								template.beginText();
								template.setFontAndSize(baseFont1, 8);
								if (writer.getPageNumber() > 10) {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								} else {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();

								}
							}
//							Suji Undo
//							document.add(new Paragraph("  " + subHeadingPageNum,
//									new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
							Paragraph paragraph = new Paragraph(subheading,
									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
							paragraph.setIndentationLeft(20); // Indent from the left margin
							paragraph.setIndentationRight(20); // Indent from the right margin
							paragraph.setSpacingBefore(10); // Space before the paragraph
							paragraph.setSpacingAfter(10); // Space after the paragraph
							document.add(paragraph);
						} else {
							// H1 Heading....Checking03
							if (!headingTags.contains(headingPageNum)) {
								if (tocPlaceholder.containsKey(headingPageNum)) {
									PdfTemplate template = tocPlaceholder.get(headingPageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									if (writer.getPageNumber() > 10) {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();
									}

									else {
										template.setTextMatrix(50
												- baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
												0);
										template.showText(String.valueOf(writer.getPageNumber() - 1));
										template.endText();

									}
								}
//								Suji Undo
//								document.add(
//										new Paragraph(" " + headingPageNum+"."+" ", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

								headingTags.add(headingPageNum);
							}

							// H2 Sub Heading
							if (tocPlaceholder.containsKey(subHeadingPageNum)) {
								PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
								template.beginText();
								template.setFontAndSize(baseFont1, 8);
								if (writer.getPageNumber() > 10) {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								} else {
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 14),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();

								}
							}
							// H1 Headings..
							// document.add(new Paragraph(" " + subHeadingPageNum,
//						//			new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

							Paragraph paragraph = new Paragraph(subheading,
									new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
							paragraph.setSpacingBefore(10); // Space before the paragraph
							paragraph.setSpacingAfter(10); // Space after the paragraph
							document.add(paragraph);
						}

						// document.add(new Paragraph(" " + subheading,
						// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String content = h3Entry.getValue();
							document.add(
									new Paragraph("            " + content, new Font(Font.FontFamily.TIMES_ROMAN, 10)));
						}
					}
					summaryPlaceHolderCountSub++;
				}
				if (heading.equals("Preface")) {
					document.newPage();
				}
				document.add(new Paragraph("\n"));

			}

			summaryPlaceHolderCount++;
		}
	}

	// Summary Results Method
	public static SummaryDetails agetResultForSessionSummaryByLevelOne(List<StagesRemarksDto> getStagesRemarksList,
			String sessionId) {
		SummaryDetails summaryDetails = new SummaryDetails();
		Map<String, String> levelOneResults = new HashMap<>();
		Map<String, String> levelOneIdEndDate = new HashMap<>();
		Map<String, String> levelOneIdStartDate = new HashMap<>();
		try {

			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
			LevelTwoMasterService levelTwoMasterService = new LevelTwoMasterService();
			LevelThreeService levelMasterThreeService = new LevelThreeService();
			LevelFourMasterSevice levelFourMasterSevice = new LevelFourMasterSevice();
			LevelFiveMasterService levelFiveMasterService = new LevelFiveMasterService();


			for (StagesRemarksDto dto : getStagesRemarksList) {
				// Reinitialize levelIds for each iteration to avoid NullPointerException
				List<String> levelIds = new ArrayList<>();

				String levelOneId = dto.getLevelOneStageId();

				StageLevelResponse stageLevelResponselevelTwo = levelTwoMasterService
						.getLevelTwoMasterByLevleOneId(levelOneId);
				List<SubLevelResponseDto> listOfSubLevelDto2 = new ArrayList<>();
				listOfSubLevelDto2 = (List<SubLevelResponseDto>) stageLevelResponselevelTwo.getStageLevelList();
				for (SubLevelResponseDto subLevelResponseDto : listOfSubLevelDto2) {
					if (!subLevelResponseDto.getNextLevel().equals("Y")) {
						levelIds.add(subLevelResponseDto.getLevelId());
					} else {

						StageLevelResponse stageLevelResponselevelThree = levelMasterThreeService
								.getLevelThreeMasterByLevleTwoId(subLevelResponseDto.getLevelId());
						List<SubLevelResponseDto> listOfSubLevelDto3 = new ArrayList<>();
						listOfSubLevelDto3 = (List<SubLevelResponseDto>) stageLevelResponselevelThree
								.getStageLevelList();

						for (SubLevelResponseDto subLevelResponseDto3 : listOfSubLevelDto3) {
							if (!subLevelResponseDto3.getNextLevel().equals("Y")) {
								levelIds.add(subLevelResponseDto3.getLevelId());
							} else {

								StageLevelResponse stageLevelResponselevelFour = levelFourMasterSevice
										.getLevelFourMasterByLevleThreeId(subLevelResponseDto3.getLevelId());
								List<SubLevelResponseDto> listOfSubLevelDto4 = new ArrayList<>();
								listOfSubLevelDto4 = (List<SubLevelResponseDto>) stageLevelResponselevelFour
										.getStageLevelList();

								for (SubLevelResponseDto subLevelResponseDto4 : listOfSubLevelDto4) {
									if (!subLevelResponseDto4.getNextLevel().equals("Y")) {
										levelIds.add(subLevelResponseDto4.getLevelId());
									} else {
										StageLevelResponse stageLevelResponselevelFive = levelFiveMasterService
												.getLevelFiveMasterByLevleFourId(subLevelResponseDto4.getLevelId());
										List<SubLevelResponseDto> listOfSubLevelDto5 = new ArrayList<>();
										listOfSubLevelDto5 = (List<SubLevelResponseDto>) stageLevelResponselevelFive
												.getStageLevelList();

										for (SubLevelResponseDto subLevelResponseDto5 : listOfSubLevelDto5) {
											levelIds.add(subLevelResponseDto5.getLevelId());
										}

									}

								}
							}

						}
					}

				}
				String overallResult = "";
				String endDate = "";
				String startDate = "";
				for (String levelId : levelIds) {
					SessionFileManagement sessionFileManagement = new SessionFileManagement();
					StageSummaryDetails stagesSummaryDetails = new StageSummaryDetails();
					stagesSummaryDetails = sessionFileManagement.getResultsForLevelId(sessionId, levelId);
					overallResult = stagesSummaryDetails.getResult();
					endDate = stagesSummaryDetails.getEndDate();
					startDate = stagesSummaryDetails.getStartDate();

					if (!overallResult.equals("PASS") || overallResult != null) {
						break;
					}
				}
				// Make sure to put the overallResult after the loop
				levelOneResults.put(levelOneId, overallResult);
				levelOneIdEndDate.put(levelOneId, endDate);
				levelOneIdStartDate.put(levelOneId, startDate);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		summaryDetails.setLevelOneIdResult(levelOneResults);
		summaryDetails.setLevelOneIdStartDate(levelOneIdStartDate);
		summaryDetails.setLevelOneIdEndDate(levelOneIdEndDate);
		return summaryDetails;
	}

	// To Get the Result For Each Level Ids
//	public static Map<String, String> getResultForSessionSummaryByLevelOne1(List<StagesRemarksDto> getStagesRemarksList,
//			String sessionId) {
//		Map<String, String> levelOneResults = new HashMap<>();
//		try {
//
//			LevelOneMasterService levelOneMasterService = new LevelOneMasterService();
//			LevelTwoMasterService levelTwoMasterService = new LevelTwoMasterService();
//			LevelThreeService levelMasterThreeService = new LevelThreeService();
//			LevelFourMasterSevice levelFourMasterSevice = new LevelFourMasterSevice();
//			LevelFiveMasterService levelFiveMasterService = new LevelFiveMasterService();
//			List<String> levelIds = new ArrayList<String>();
//
//			for (StagesRemarksDto dto : getStagesRemarksList) {
//				levelIds = null;
//				String levelOneId = dto.getLevelOneStageId();
//
//				StageLevelResponse stageLevelResponselevelTwo = levelTwoMasterService
//						.getLevelTwoMasterByLevleOneId(levelOneId);
//				List<SubLevelResponseDto> listOfSubLevelDto2 = new ArrayList<>();
//				listOfSubLevelDto2 = (List<SubLevelResponseDto>) stageLevelResponselevelTwo.getStageLevelList();
//				for (SubLevelResponseDto subLevelResponseDto : listOfSubLevelDto2) {
//					if (!subLevelResponseDto.getNextLevel().equals("Y")) {
//						levelIds.add(subLevelResponseDto.getLevelId());
//					} else {
//
//						StageLevelResponse stageLevelResponselevelThree = levelMasterThreeService
//								.getLevelThreeMasterByLevleTwoId(subLevelResponseDto.getLevelId());
//						List<SubLevelResponseDto> listOfSubLevelDto3 = new ArrayList<>();
//						listOfSubLevelDto3 = (List<SubLevelResponseDto>) stageLevelResponselevelThree
//								.getStageLevelList();
//
//						for (SubLevelResponseDto subLevelResponseDto3 : listOfSubLevelDto3) {
//							if (!subLevelResponseDto3.getNextLevel().equals("Y")) {
//								levelIds.add(subLevelResponseDto3.getLevelId());
//							} else {
//
//								StageLevelResponse stageLevelResponselevelFour = levelFourMasterSevice
//										.getLevelFourMasterByLevleThreeId(subLevelResponseDto3.getLevelId());
//								List<SubLevelResponseDto> listOfSubLevelDto4 = new ArrayList<>();
//								listOfSubLevelDto4 = (List<SubLevelResponseDto>) stageLevelResponselevelFour
//										.getStageLevelList();
//
//								for (SubLevelResponseDto subLevelResponseDto4 : listOfSubLevelDto4) {
//									if (!subLevelResponseDto4.getNextLevel().equals("Y")) {
//										levelIds.add(subLevelResponseDto4.getLevelId());
//									} else {
//										StageLevelResponse stageLevelResponselevelFive = levelFiveMasterService
//												.getLevelFiveMasterByLevleFourId(subLevelResponseDto4.getLevelId());
//										List<SubLevelResponseDto> listOfSubLevelDto5 = new ArrayList<>();
//										listOfSubLevelDto5 = (List<SubLevelResponseDto>) stageLevelResponselevelFive
//												.getStageLevelList();
//
//										for (SubLevelResponseDto subLevelResponseDto5 : listOfSubLevelDto5) {
//											levelIds.add(subLevelResponseDto5.getLevelId());
//										}
//
//									}
//
//								}
//							}
//
//						}
//					}
//
//				}
//				String overallResult = "";
//				for (String levelId : levelIds) {
//					SessionFileManagement sessionFileManagement = new SessionFileManagement();
//					overallResult = sessionFileManagement.getResultsForLevelId(sessionId, levelId);
//					if (!overallResult.equals("PASS")) {
//						break;
//					}
//					levelOneResults.put(levelOneId, overallResult);
//				}
//				levelOneResults.put(levelOneId, overallResult);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return levelOneResults;
//
//	}

	// =================Not Used Methods ======Need TO
	// Reference==================================//
	public static void createSummary1(Document document, Map<String, Map<String, Map<String, String>>> data)
			throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "src/Resources/Images/BellLogoRocket.png";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont); // Assuming 'title' is a variable containing the title
																// text
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Tab Adding
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
		Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
		Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(
				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
		cell2.setRowspan(2);
		cell2.setColspan(3);
		cell2.setFixedHeight(80f);
		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(
				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));

		cell3.setRowspan(3);
		cell3.setColspan(1);
		cell3.setFixedHeight(100f);
		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(
				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
		cell4.setRowspan(3);
		cell4.setColspan(1);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(
				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
		cell5.setRowspan(3);
		cell5.setColspan(1);
		cell5.setFixedHeight(60f);
		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		document.add(table);

		document.newPage();
		// int summaryPlaceHolderCount = 1;
		int summaryPlaceHolderCountSub;
		int summaryPlaceHolderCountH3;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, Map<String, String>> subheadings = entry.getValue();

			if (heading.equalsIgnoreCase("Table Of Contents")) {
				createTOCByRead1(document, data);
				document.newPage();
				continue;
			}

			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");

			document.add(
					new Paragraph(" " + heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
			// document.add(new Paragraph("\n"));

			BaseFont baseFont1 = BaseFont.createFont();
			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
			if (tocPlaceholder.containsKey(headingPageNum)) {
				PdfTemplate template = tocPlaceholder.get(headingPageNum);
				template.beginText();
				template.setFontAndSize(baseFont1, 8);
				template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
				if (writer.getPageNumber() - 1 < 10)
					template.showText(String.valueOf(writer.getPageNumber() - 1));
				template.endText();
			}

			if (!heading.contains("(TABLE)")) {
				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					Map<String, String> h3Map = subEntry.getValue();

					if (subheading.contains("(h2)")) {
						subheading = subheading.replaceAll("(h2)", "");
						document.add(new Paragraph("    " + subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						String subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
								+ " " + subheading;
						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
							template.beginText();
							template.setFontAndSize(baseFont1, 8);
							template.setTextMatrix(
									50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
							template.showText(String.valueOf(writer.getPageNumber() - 1));
							template.endText();
						}

						summaryPlaceHolderCountH3 = 1;

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String h3 = h3Entry.getKey();
							String content = h3Entry.getValue();

							if (h3.contains("(h3)")) {
								h3 = h3.replaceAll("(h3)", "");
								document.add(new Paragraph("        " + h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));

								String h3PageNum = "         " + summaryPlaceHolderCount + "."
										+ summaryPlaceHolderCountSub + "." + summaryPlaceHolderCountH3 + " " + h3;
								if (tocPlaceholder.containsKey(h3PageNum)) {
									PdfTemplate template = tocPlaceholder.get(h3PageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								}

								Paragraph contentParagraph = new Paragraph(content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
								contentParagraph.setIndentationLeft(5f); // Indent from the left margin
								contentParagraph.setIndentationRight(5f); // Indent from the right margin
								// contentParagraph.setSpacingBefore(10); // Space before the paragraph
								// contentParagraph.setSpacingAfter(10); // Space after the paragraph
								document.add(contentParagraph);

								document.add(new Paragraph("            " + content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							} else {

								// Now Added

								Paragraph h3Paragraph = new Paragraph(h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
								h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
								h3Paragraph.setIndentationRight(5f); // Indent from the right margin
								// h3Paragraph.setSpacingBefore(10); // Space before the paragraph
								// h3Paragraph.setSpacingAfter(10); // Space after the paragraph
								document.add(h3Paragraph);

								// document.add(new Paragraph(" " + h3,
								// new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							}
							summaryPlaceHolderCountH3++;
						}
					} else {

						// Now Added

						Paragraph paragraph = new Paragraph(subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
						paragraph.setIndentationLeft(20f); // Indent from the left margin
						paragraph.setIndentationRight(20f); // Indent from the right margin
						paragraph.setSpacingBefore(10f); // Space before the paragraph
						paragraph.setSpacingAfter(10f); // Space after the paragraph
						document.add(paragraph);

						// document.add(new Paragraph(" " + subheading,
						// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String content = h3Entry.getValue();
							document.add(
									new Paragraph("            " + content, new Font(Font.FontFamily.TIMES_ROMAN, 10)));
						}
					}
					summaryPlaceHolderCountSub++;
				}
				if (heading.equals("Preface")) {
					document.newPage();
				}
				document.add(new Paragraph("\n"));

			} else {
				document.add(new Paragraph("\n"));
				// String heading = entry.getKey();
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					String content = subEntry.getKey();

					String sep = content;
					String[] subheadinglines = subheading.split("\n");


					String lines = subheadinglines[0];

					String[] lineArray = lines.split("|");

					for (int i = 0; i <= subheadinglines.length - 1; i++) {
						String[] lineSeparting = subheadinglines[i].split(";");
						// subheadinglines[i]);
						PdfPTable table1 = new PdfPTable(lineSeparting.length);

						// table1.setWidthPercentage(100); // Width 100%
						// table1.setSpacingBefore(10f); // Space before table
						// table1.setSpacingAfter(10f); // Space after table

						if (lineSeparting.length == 2) {
							float[] columnWidthsInner = { 0.5f, 5f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 3) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 4) {
							float[] columnWidthsInner = { 0.5f, 2f, 2f, 2.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 5) {
							// SN;Test;Description;Results;Remarks
							float[] columnWidthsInner = { 0.4f, 2f, 2f, 0.7f, 1.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 6) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f, 1f, 1f };
							table1.setWidths(columnWidthsInner);

						}

						if (i == 0) {

							Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
							for (String header : lineSeparting) {
								PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
								cell.setBackgroundColor(BaseColor.GRAY);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								table1.addCell(cell);
								// table1.setHeaderRows(1);

							}
						} else {

							for (String cellContent : lineSeparting) {
								PdfPCell cellCont = new PdfPCell(new Phrase(cellContent));
								table1.addCell(cellCont);

							}

						}
						document.add(table1);
						// System.out.println("");

					}

					// document.add(new Paragraph(subheading, new Font(Font.FontFamily.HELVETICA,
					// 10, Font.ITALIC)));
					// document.add(new Paragraph(content, new Font(Font.FontFamily.HELVETICA,
					// 10)));
				}

			}
			summaryPlaceHolderCount++;
		}
	}

	public static void createSummary11(Document document, Map<String, Map<String, Map<String, String>>> data)
			throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "src/Resources/Images/BellLogoRocket.png";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont); // Assuming 'title' is a variable containing the title
																// text
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Tab Adding
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
		Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
		Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
		Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(
				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
		cell2.setRowspan(2);
		cell2.setColspan(3);
		cell2.setFixedHeight(80f);
		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(
				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));

		cell3.setRowspan(3);
		cell3.setColspan(1);
		cell3.setFixedHeight(100f);
		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(
				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
		cell4.setRowspan(3);
		cell4.setColspan(1);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(
				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
		cell5.setRowspan(3);
		cell5.setColspan(1);
		cell5.setFixedHeight(60f);
		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		document.add(table);

		document.newPage();
		// int summaryPlaceHolderCount = 1;
		int summaryPlaceHolderCountSub;
		int summaryPlaceHolderCountH3;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, Map<String, String>> subheadings = entry.getValue();

			if (heading.equalsIgnoreCase("Table Of Contents")) {
				createTOCByRead1(document, data);
				document.newPage();
				continue;
			}

			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");

			document.add(
					new Paragraph(" " + heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
			// document.add(new Paragraph("\n"));

			BaseFont baseFont1 = BaseFont.createFont();
			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
			if (tocPlaceholder.containsKey(headingPageNum)) {
				PdfTemplate template = tocPlaceholder.get(headingPageNum);
				template.beginText();
				template.setFontAndSize(baseFont1, 8);
				template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
				if (writer.getPageNumber() - 1 < 10)
					template.showText(String.valueOf(writer.getPageNumber() - 1));
				template.endText();
			}

			if (!heading.contains("(TABLE)")) {
				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					Map<String, String> h3Map = subEntry.getValue();

					if (subheading.contains("(h2)")) {
						subheading = subheading.replaceAll("(h2)", "");
						document.add(new Paragraph("    " + subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						String subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
								+ " " + subheading;
						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
							template.beginText();
							template.setFontAndSize(baseFont1, 8);
							template.setTextMatrix(
									50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
							template.showText(String.valueOf(writer.getPageNumber() - 1));
							template.endText();
						}

						summaryPlaceHolderCountH3 = 1;

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String h3 = h3Entry.getKey();
							String content = h3Entry.getValue();

							if (h3.contains("(h3)")) {
								h3 = h3.replaceAll("(h3)", "");
								document.add(new Paragraph("        " + h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));

								String h3PageNum = "         " + summaryPlaceHolderCount + "."
										+ summaryPlaceHolderCountSub + "." + summaryPlaceHolderCountH3 + " " + h3;
								if (tocPlaceholder.containsKey(h3PageNum)) {
									PdfTemplate template = tocPlaceholder.get(h3PageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								}

								Paragraph contentParagraph = new Paragraph(content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
								contentParagraph.setIndentationLeft(5f); // Indent from the left margin
								contentParagraph.setIndentationRight(5f); // Indent from the right margin
								// contentParagraph.setSpacingBefore(10); // Space before the paragraph
								// contentParagraph.setSpacingAfter(10); // Space after the paragraph
								document.add(contentParagraph);

								document.add(new Paragraph("            " + content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							} else {

								// Now Added

								Paragraph h3Paragraph = new Paragraph(h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
								h3Paragraph.setIndentationLeft(5f); // Indent from the left margin
								h3Paragraph.setIndentationRight(5f); // Indent from the right margin
								// h3Paragraph.setSpacingBefore(10); // Space before the paragraph
								// h3Paragraph.setSpacingAfter(10); // Space after the paragraph
								document.add(h3Paragraph);

								// document.add(new Paragraph(" " + h3,
								// new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							}
							summaryPlaceHolderCountH3++;
						}
					} else {

						// Now Added

						Paragraph paragraph = new Paragraph(subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
						paragraph.setIndentationLeft(20f); // Indent from the left margin
						paragraph.setIndentationRight(20f); // Indent from the right margin
						paragraph.setSpacingBefore(10f); // Space before the paragraph
						paragraph.setSpacingAfter(10f); // Space after the paragraph
						document.add(paragraph);

						// document.add(new Paragraph(" " + subheading,
						// new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String content = h3Entry.getValue();
							document.add(
									new Paragraph("            " + content, new Font(Font.FontFamily.TIMES_ROMAN, 10)));
						}
					}
					summaryPlaceHolderCountSub++;
				}
				if (heading.equals("Preface")) {
					document.newPage();
				}
				document.add(new Paragraph("\n"));
			} else {
				document.add(new Paragraph("\n"));
				// String heading = entry.getKey();
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					String content = subEntry.getKey();

					String sep = content;
					String[] subheadinglines = subheading.split("\n");


					String lines = subheadinglines[0];

					String[] lineArray = lines.split("|");

					for (int i = 0; i <= subheadinglines.length - 1; i++) {
						String[] lineSeparting = subheadinglines[i].split(";");
						// subheadinglines[i]);
						PdfPTable table1 = new PdfPTable(lineSeparting.length);

						// table1.setWidthPercentage(100); // Width 100%
						// table1.setSpacingBefore(10f); // Space before table
						// table1.setSpacingAfter(10f); // Space after table

						if (lineSeparting.length == 2) {
							float[] columnWidthsInner = { 0.5f, 5f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 3) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 4) {
							float[] columnWidthsInner = { 0.5f, 2f, 2f, 2.2f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 5) {
							// SN;Test;Description;Results;Remarks
							float[] columnWidthsInner = { 0.4f, 2f, 2f, 0.7f, 0.8f };
							table1.setWidths(columnWidthsInner);

						}
						if (lineSeparting.length == 6) {
							float[] columnWidthsInner = { 0.8f, 2f, 2f, 1f, 1f };
							table1.setWidths(columnWidthsInner);

						}

						if (i == 0) {

							Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
							for (String header : lineSeparting) {
								PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
								cell.setBackgroundColor(BaseColor.GRAY);
								cell.setHorizontalAlignment(Element.ALIGN_CENTER);
								table1.addCell(cell);
								// table1.setHeaderRows(1);

							}
						} else {

							for (String cellContent : lineSeparting) {
								PdfPCell cellCont = new PdfPCell(new Phrase(cellContent));
								table1.addCell(cellCont);

							}

						}
						document.add(table1);
						// System.out.println("");

					}

					// document.add(new Paragraph(subheading, new Font(Font.FontFamily.HELVETICA,
					// 10, Font.ITALIC)));
					// document.add(new Paragraph(content, new Font(Font.FontFamily.HELVETICA,
					// 10)));
				}

			}
			summaryPlaceHolderCount++;
		}
	}

	// With New Page Paragraph..
	/*
	 * public static void createSummary1(Document document, Map<String, Map<String,
	 * Map<String, String>>> data) throws DocumentException, MalformedURLException,
	 * IOException {
	 * 
	 * String imagePath = "src/Resources/Images/BellLogoRocket.png"; Image img =
	 * Image.getInstance(imagePath); img.scaleAbsolute(2, 1); img.scalePercent(50);
	 * img.setAlignment(Element.ALIGN_CENTER); document.add(img);
	 * 
	 * Paragraph preface = new Paragraph();
	 * preface.setAlignment(Element.ALIGN_CENTER);
	 * 
	 * Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD,
	 * BaseColor.BLACK); Paragraph titlePara = new Paragraph(title, titleFont); //
	 * Assuming 'title' is a variable containing the title text
	 * titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
	 * document.add(titlePara);
	 * 
	 * document.add(new Paragraph("\n")); document.add(new Paragraph("\n"));
	 * 
	 * // Tab Adding BaseColor textColor = new BaseColor(22, 28, 99); BaseColor
	 * bcolor = new BaseColor(228, 239, 255); Font boldFont1 = new
	 * Font(Font.FontFamily.HELVETICA, 13, Font.BOLD); Font boldFont4 = new
	 * Font(Font.FontFamily.HELVETICA, 6, Font.BOLD); Font boldFont2 = new
	 * Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL); Font boldFont3 = new
	 * Font(Font.FontFamily.HELVETICA, 10, Font.BOLD); Font boldFont5 = new
	 * Font(Font.FontFamily.COURIER, 8, Font.BOLD); PdfPTable table = new
	 * PdfPTable(3); float[] columnWidths = {2, 2, 2}; // Adjust column widths as
	 * necessary table.setWidths(columnWidths); PdfPCell cell1 = new PdfPCell(new
	 * Phrase("Prepared By", boldFont3)); cell1.setRowspan(1); cell1.setColspan(3);
	 * cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
	 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell1);
	 * 
	 * PdfPCell cell2 = new PdfPCell(new Phrase( "\n" + "\n" + "\n" + "\n" + "\n" +
	 * "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
	 * cell2.setRowspan(2); cell2.setColspan(3); cell2.setFixedHeight(80f);
	 * cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
	 * cell2.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell2);
	 * 
	 * PdfPCell cell3 = new PdfPCell( new Phrase("VerifiedBy" + "\n" + "\n" + "\n" +
	 * "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));
	 * 
	 * cell3.setRowspan(3); cell3.setColspan(1); cell3.setFixedHeight(100f);
	 * cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
	 * cell3.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell3);
	 * 
	 * PdfPCell cell4 = new PdfPCell( new Phrase("ReviewedBy" + "\n" + "\n" + "\n" +
	 * "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3)); cell4.setRowspan(3);
	 * cell4.setColspan(1); cell4.setFixedHeight(60f);
	 * cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
	 * cell4.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell4);
	 * 
	 * PdfPCell cell5 = new PdfPCell( new Phrase("ApporvedBy" + "\n" + "\n" + "\n" +
	 * "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3)); cell5.setRowspan(3);
	 * cell5.setColspan(1); cell5.setFixedHeight(60f);
	 * cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
	 * cell5.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell5);
	 * 
	 * document.add(table);
	 * 
	 * document.newPage();
	 * 
	 * int summaryPlaceHolderCount = 1; int summaryPlaceHolderCountSub; int
	 * summaryPlaceHolderCountH3;
	 * 
	 * for (Map.Entry<String, Map<String, Map<String, String>>> entry :
	 * data.entrySet()) { String heading = entry.getKey(); Map<String, Map<String,
	 * String>> subheadings = entry.getValue();
	 * 
	 * if (heading.equalsIgnoreCase("Table Of Contents")) {
	 * createTOCByRead1(document, data); document.newPage(); continue; }
	 * 
	 * heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
	 * 
	 * // Check available space for the heading if (writer.getVerticalPosition(true)
	 * - 20 < document.bottomMargin()) { document.newPage(); }
	 * 
	 * document.add(new Paragraph(" " + heading, new
	 * Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
	 * 
	 * BaseFont baseFont1 = BaseFont.createFont(); String headingPageNum = "  " +
	 * summaryPlaceHolderCount + " " + heading;
	 * System.out.println("Summary headingPageNum---->" + headingPageNum); if
	 * (tocPlaceholder.containsKey(headingPageNum)) { PdfTemplate template =
	 * tocPlaceholder.get(headingPageNum); template.beginText();
	 * template.setFontAndSize(baseFont1, 8); template.setTextMatrix(50 -
	 * baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0); if
	 * (writer.getPageNumber() - 1 < 10)
	 * template.showText(String.valueOf(writer.getPageNumber() - 1));
	 * template.endText(); }
	 * 
	 * if (!heading.contains("(TABLE)")) { summaryPlaceHolderCountSub = 1; for
	 * (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
	 * String subheading = subEntry.getKey(); Map<String, String> h3Map =
	 * subEntry.getValue();
	 * 
	 * if (subheading.contains("(h2)")) { subheading = subheading.replaceAll("(h2)",
	 * "");
	 * 
	 * // Check available space for the subheading if
	 * (writer.getVerticalPosition(true) - 20 < document.bottomMargin()) {
	 * document.newPage(); }
	 * 
	 * document.add(new Paragraph("    " + subheading, new
	 * Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
	 * 
	 * String subHeadingPageNum = "     " + summaryPlaceHolderCount + "." +
	 * summaryPlaceHolderCountSub + " " + subheading;
	 * System.out.println("Summary subHeadingPageNum" + subHeadingPageNum); if
	 * (tocPlaceholder.containsKey(subHeadingPageNum)) { PdfTemplate template =
	 * tocPlaceholder.get(subHeadingPageNum); template.beginText();
	 * template.setFontAndSize(baseFont1, 8); template.setTextMatrix( 50 -
	 * baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
	 * template.showText(String.valueOf(writer.getPageNumber() - 1));
	 * template.endText(); }
	 * 
	 * summaryPlaceHolderCountH3 = 1;
	 * 
	 * for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) { String h3 =
	 * h3Entry.getKey(); String content = h3Entry.getValue();
	 * 
	 * if (h3.contains("(h3)")) { h3 = h3.replaceAll("(h3)", "");
	 * 
	 * // Check available space for the h3 heading if
	 * (writer.getVerticalPosition(true) - 20 < document.bottomMargin()) {
	 * document.newPage(); }
	 * 
	 * document.add(new Paragraph("        " + h3, new
	 * Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
	 * 
	 * String h3PageNum = "         " + summaryPlaceHolderCount + "." +
	 * summaryPlaceHolderCountSub + "." + summaryPlaceHolderCountH3 + " " + h3;
	 * System.out.println("Summary h3PageNum" + h3PageNum); if
	 * (tocPlaceholder.containsKey(h3PageNum)) { PdfTemplate template =
	 * tocPlaceholder.get(h3PageNum); template.beginText();
	 * template.setFontAndSize(baseFont1, 8); template.setTextMatrix( 50 -
	 * baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
	 * template.showText(String.valueOf(writer.getPageNumber() - 1));
	 * template.endText(); }
	 * 
	 * Paragraph contentParagraph = new Paragraph(content, new
	 * Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
	 * contentParagraph.setIndentationLeft(5); // Indent from the left margin
	 * contentParagraph.setIndentationRight(5); // Indent from the right margin
	 * document.add(contentParagraph);
	 * 
	 * } else { // Check available space for the h3 content if
	 * (writer.getVerticalPosition(true) - 20 < document.bottomMargin()) {
	 * document.newPage(); }
	 * 
	 * Paragraph contentParagraph = new Paragraph(content, new
	 * Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
	 * contentParagraph.setIndentationLeft(10); // Further indent content under
	 * subheading contentParagraph.setIndentationRight(5); // Indent from the right
	 * margin document.add(contentParagraph); }
	 * 
	 * summaryPlaceHolderCountH3++; } summaryPlaceHolderCountSub++; } } }
	 * summaryPlaceHolderCount++; } }
	 */

	// Generation Of Breif Report
	public Response generateBreifReportForCurrentExecution(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "CurrentExecutionBriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
		} else {
			filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}
		String excelPath = "";
		if (!DFCCConstant.isJarBuild) {
			excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		} else {
			excelPath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/REPORT_FIELDS.xlsx";
		}

		Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
		data1 = data;
		writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		document.open();

		ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
		writer.setPageEvent(event);
		document.open();
		// createTOCByRead();
		createSummary1(document, data);

		// To Fetch....
		ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
		ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
		resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);
		List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
		resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
		document.newPage();

		/*
		 * GetObjResponse getObjResponse = new GetObjResponse(); SessionService
		 * sessionService = new SessionService(); getObjResponse =
		 * sessionService.getSessionDetailBySessionStageId(sessionId); SessionEntity
		 * sessionEntity =(SessionEntity) getObjResponse.getObject();
		 */

		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

		BaseColor tecBlueColor = new BaseColor(0, 79, 104, 255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1, 75, 174, 255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99, 137, 52, 255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151, 185, 196);
		Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
		Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
		Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		// document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String, String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String, String> uutIdName = DFCCConstant.getUutIdNameMap();

		Paragraph uutNameDetails = new Paragraph(" Uut Type Name      ", headerFont);
		uutNameDetails.add(new Chunk("    " + sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);

		Paragraph SessionNameDetails = new Paragraph(" Session Name       ", headerFont);
		SessionNameDetails.add(new Chunk("     " + sessionDetailsMap.get("sessionName"), highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(" Stage Name         ", headerFont);
		StageNameDetails
				.add(new Chunk("     " + stageIdName.get(resultExecutionResponse.getStageName()), highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(" User Name          ", headerFont);
		userNameDetails.add(new Chunk("     " + sessionDetailsMap.get("userName"), highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     " + sessionDetailsMap.get("dfccPartNo"), highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table

		PdfPTable table = new PdfPTable(4); // 4 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 2.5f, 2.5f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "S.NO", "Time Of Execution", "Executed File Name", "Result" };

		for (int i = 0; i < headers.length; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(headers[i], headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);

			if (i == 0) {
				cell.setPaddingLeft(10f);
			}
			if (i == headers.length - 1) {
				cell.setPaddingRight(10f);
			}

			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo = 1;

		/*
		 * for (ResultExecutionDTO dto : resultExecutionDTOList) { Font greenFont = new
		 * Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN); Font
		 * redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL,
		 * BaseColor.RED);
		 * 
		 * PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(sNo)));
		 * cell1.setPaddingLeft(10f); // Padding on the left side for the first column
		 * table.addCell(cell1);
		 * 
		 * table.addCell(new Phrase(dto.getEndTime())); table.addCell(new
		 * Phrase(dto.getTestFileName()));
		 * 
		 * PdfPCell cell4 = new PdfPCell(new Phrase(dto.getStatus()));
		 * cell4.setPaddingRight(10f); // Padding on the right side for the last column
		 * table.addCell(cell4);
		 * 
		 * sNo++; }
		 */

		document.add(table);
		document.close();

		return res;
	}

	public static void createSummary(Document document, Map<String, Map<String, Map<String, String>>> data)
			throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "src/Resources/Images/BellLogoRocket.png";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Tab Adding
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont4 = new Font(FontFamily.HELVETICA, 6, Font.BOLD);
		Font boldFont2 = new Font(FontFamily.HELVETICA, 6, Font.NORMAL);
		Font boldFont3 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
		Font boldFont5 = new Font(FontFamily.COURIER, 8, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase(
				"\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", boldFont4));
		cell2.setRowspan(2);
		cell2.setColspan(3);
		cell2.setFixedHeight(80f);
		cell2.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell(
				new Phrase("VerifiedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "LCA-TS/EW&A", boldFont3));

		cell3.setRowspan(3);
		cell3.setColspan(1);
		cell3.setFixedHeight(100f);
		cell3.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(
				new Phrase("ReviewedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "QM-EW & A", boldFont3));
		cell4.setRowspan(3);
		cell4.setColspan(1);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(
				new Phrase("ApporvedBy" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "OAQA-BEL", boldFont3));
		cell5.setRowspan(3);
		cell5.setColspan(1);
		cell5.setFixedHeight(60f);
		cell5.setVerticalAlignment(Element.ALIGN_BASELINE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		document.add(table);

		document.newPage();
		// summaryPlaceHolderCount = 1;

		for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, Map<String, String>> subheadings = entry.getValue();

			if (heading.equalsIgnoreCase("Table Of Contents")) {
				createTOCByRead1(document, data);
				document.newPage();
				continue;
			}

			// tocEntries.add(new TOCEntry(heading, writer.getPageNumber()));

			heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");

			document.add(
					new Paragraph(" " + heading, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
			document.add(new Paragraph("\n"));

			BaseFont baseFont1 = BaseFont.createFont();
			String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;

			if (tocPlaceholder.containsKey(headingPageNum)) {
				PdfTemplate template = tocPlaceholder.get(headingPageNum);
				template.beginText();
				template.setFontAndSize(baseFont1, 8);
				template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
				template.showText(String.valueOf(writer.getPageNumber() - 1));
				template.endText();
			}
			if (heading.contains("(TABLE)")) {

				Map<String, Map<String, String>> tablemap = new HashMap<String, Map<String, String>>();
				tablemap = data.get(heading);

			} else {
				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					Map<String, String> h3Map = subEntry.getValue();

					if (subheading.contains("(h2)")) {
						subheading = subheading.replaceAll("(h2)", "");
						document.add(new Paragraph("    " + subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						// tocEntries.add(new TOCEntry(subheading, writer.getPageNumber()));
						String subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
								+ " " + subheading;

						if (tocPlaceholder.containsKey(subHeadingPageNum)) {
							PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
							template.beginText();
							template.setFontAndSize(baseFont1, 8);
							template.setTextMatrix(
									50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12), 0);
							template.showText(String.valueOf(writer.getPageNumber() - 1));
							template.endText();
						}

						summaryPlaceHolderCountH3 = 1;

						for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
							String h3 = h3Entry.getKey();
							String content = h3Entry.getValue();

							if (h3.contains("(h3)")) {
								h3 = h3.replaceAll("(h3)", "");
								document.add(new Paragraph("        " + h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

								// tocEntries.add(new TOCEntry(h3, writer.getPageNumber()));
								String h3PageNum = "         " + summaryPlaceHolderCount + "."
										+ summaryPlaceHolderCountSub + "." + summaryPlaceHolderCountH3 + " " + h3;

								if (tocPlaceholder.containsKey(h3PageNum)) {
									PdfTemplate template = tocPlaceholder.get(h3PageNum);
									template.beginText();
									template.setFontAndSize(baseFont1, 8);
									template.setTextMatrix(
											50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()), 12),
											0);
									template.showText(String.valueOf(writer.getPageNumber() - 1));
									template.endText();
								}

								document.add(new Paragraph("            " + content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10)));
								summaryPlaceHolderCountH3++;
							} else {
								document.add(new Paragraph("            " + content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10)));

								summaryPlaceHolderCountH3++;
							}
						}
						summaryPlaceHolderCountSub++;
					} else {

						document.add(new Paragraph("    " + subheading,
								new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

						if (heading.equals("Preface")) {
							document.newPage();
						}
						summaryPlaceHolderCountSub++;
					}
				}

				document.add(new Paragraph("\n"));
			}
			summaryPlaceHolderCount++;
		}
	}

	static class HeaderFooter extends PdfPageEventHelper {

		protected PdfPTable table;
		protected float tableHeight;
		float[] columnWidths = new float[] { 25f, 45f, 20f };
		Font boldFont = new Font(FontFamily.HELVETICA, 17, Font.BOLD);

		Font LightFont = new Font(FontFamily.HELVETICA, 12);
		int reportId = 0;
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont1 = new Font(FontFamily.HELVETICA, 13, Font.BOLD);
		Font boldFont2 = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);

		@Override
		public void onStartPage(PdfWriter writer, Document document) {
			try {
				// document.setMargins(document.leftMargin(), document.rightMargin(), 100,
				// document.bottomMargin());
				addHeader(document, writer, currentPageNumber, totalPageCount);
				addborder(writer); // adding Margins
				currentPageNumber++;
			} catch (Exception e) {
			}
		}

		@Override
		public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition,
				final Paragraph title) {
			pageByTitle.put(title.getContent(), writer.getPageNumber());
		}

		@Override
		public void onSection(final PdfWriter writer, final Document document, final float paragraphPosition,
				final int depth, final Paragraph title) {
			pageByTitle.put(title.getContent(), writer.getPageNumber());
		}

		private void createTOC(final int count) throws DocumentException {
			final Chapter intro = new Chapter(new Paragraph("This is TOC ", boldFont), 0);
			intro.setNumberDepth(0);
			document.add(intro);

			for (int i = 1; i < count + 1; i++) {
				// Write "Chapter i"
				final String title = "Chapter " + i;
				final Chunk chunk = new Chunk(title).setLocalGoto(title);
				document.add(new Paragraph(chunk));

				document.add(new VerticalPositionMark() {
					@Override
					public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx,
							final float ury, final float y) {
						final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
						ReportGenerationNew.tocPlaceholder.put(title, createTemplate);

						canvas.addTemplate(createTemplate, urx - 50, y);
					}
				});
			}
		}

		// This method will be called to add the header
		private void addHeader(Document document, PdfWriter writer, int currentPage, int totalPages)
				throws DocumentException, IOException {

			PdfPTable table = new PdfPTable(6);
			float[] columnWidths = { 2, 1, 3, 1, 2, 1 }; // Adjust column widths as necessary
			table.setWidths(columnWidths);
			// \src\main\java\Resources\Images
			String imagePath = "src/Resources/Images/BELLOGO.png";

			if (!DFCCConstant.isJarBuild) {
				imagePath = "src/Resources/Images/BELLOGO.png";
			} else {
				// imagePath =
				// "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Images/BELLOGO.png";
				imagePath = currentDirectory + File.separator + "Images" + File.separator + "BELLOGO.png";

			}

			Image img = Image.getInstance(imagePath);
			img.scaleAbsolute(2f, 5f);
			img.scalePercent(100);

			PdfPCell imageCell = new PdfPCell(img, true);
			imageCell.setRowspan(1);
			imageCell.setColspan(2);
			imageCell.setFixedHeight(40f);
			imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(imageCell);

			PdfPCell cell2 = new PdfPCell(new Phrase(reportType, boldFont));
			cell2.setRowspan(1);
			cell2.setColspan(2);
			String S = "PART NUMBER" + "\n" + partNo;
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Paragraph(S, LightFont));
			cell3.setRowspan(1);
			cell3.setColspan(2);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase(reportTitle));
			cell4.setRowspan(3);
			cell4.setColspan(3);
			cell4.setFixedHeight(60f);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			String currentPageNo = "";
			if (currentPage < 10) {
				currentPageNo = "0" + currentPage;
			} else {
				currentPageNo = "" + currentPage;
			}

			PdfPCell cell5 = new PdfPCell(new Phrase("SHEET" + "\n" + currentPageNo));
			cell5.setRowspan(2);
			cell5.setColspan(1);
			cell5.setFixedHeight(30f);
			cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell5);

			PdfPCell cell6 = new PdfPCell(new Phrase("PREPARED BY" + "\n" + preparedBy));
			cell6.setRowspan(2);
			cell6.setColspan(1);
			cell6.setFixedHeight(30f);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Phrase("DATE" + "\n" + preparedDate));
			cell7.setRowspan(2);
			cell7.setColspan(1);
			cell7.setFixedHeight(30f);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell7);

			PdfPCell cell8 = new PdfPCell(new Phrase("TOTAL" + "\n" + ""));
			cell8.setRowspan(3);
			cell8.setColspan(1);
			cell8.setFixedHeight(30f);
			cell8.setVerticalAlignment(Element.ALIGN_BASELINE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell8);

			PdfPCell cell9 = new PdfPCell(new Phrase("VERIFIED BY" + "\n" + verifiedBy));
			cell9.setRowspan(3);
			cell9.setColspan(1);
			cell9.setFixedHeight(30f);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell9);

			// last Cell
			PdfPCell lastColumnCell = new PdfPCell(new Phrase("DATE" + "\n" + verifiedDate));
			lastColumnCell.setFixedHeight(30f);
			lastColumnCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			lastColumnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			lastColumnCell.setRowspan(1);
			table.addCell(lastColumnCell);

			table.setSpacingAfter(10);
			table.setWidthPercentage(100);
			document.add(table);

			table.writeSelectedRows(0, -1, document.leftMargin(), document.getPageSize().getHeight() - 36,
					writer.getDirectContent());

			PdfContentByte cb = writer.getDirectContent();
			Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

			cb.saveState();
			cb.beginText();
			BaseFont baseFont = null;
			try {
				baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}

			cb.setFontAndSize(baseFont, 8);

			cb.showTextAligned(Element.ALIGN_MIDDLE,
					"              This Document is the Property of Bhart Electronics Ltd, Banglore India. Reproduction, Utilization ",
					document.left() - 28, (document.top() + document.bottom()) / 2, 90);

			cb.showTextAligned(Element.ALIGN_MIDDLE,
					"              or disclosure to third parties in any form whatsover is not allowed without written consent of",
					document.left() - 18, (document.top() + document.bottom()) / 2, 90);

			cb.showTextAligned(Element.ALIGN_MIDDLE, "              proprietors ", document.left() - 8,
					(document.top() + document.bottom()) / 2, 90);

			cb.endText();
			cb.restoreState();
		}

		public static void addTableOfContents(Document document) throws DocumentException {
			document.newPage();
			Paragraph tocTitle = new Paragraph("Table of Contents",
					new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK));
			tocTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(tocTitle);

			for (TOCEntry entry : tocEntries) {
				Paragraph tocEntry = new Paragraph("     " + entry.title + " ....... " + entry.page,
						new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK));
				tocEntry.setAlignment(Element.ALIGN_LEFT);
				document.add(tocEntry);
			}
		}

		@Override
		public void onOpenDocument(PdfWriter writer, Document document) {
			totalPageCount = writer.getPageNumber();
		}

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			totalPageCount = writer.getPageNumber();

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