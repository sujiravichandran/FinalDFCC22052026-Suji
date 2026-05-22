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
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
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
import com.teclever.dfcc.datastore.dto.StageRemarksResponse;
import com.teclever.dfcc.datastore.dto.StagesRemarksDto;
import com.teclever.dfcc.datastore.dto.UserLoginDetailsDto;
import com.teclever.dfcc.datastore.filemanagement.SessionFileManagement;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.datastore.usermanagement.UserManagementModule;
import com.teclever.dfcc.reportgeneration.l.TOCEntry;
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
    private static PdfTemplate totalPageTemplate;
    private static Image totalPageImage;
    

    private static List<TOCEntry> tocEntries = new ArrayList<>();
    private static Map<String, PdfTemplate> tocPlaceholder = new HashMap<String, PdfTemplate>();
    static List<PdfTemplate> templateList = new ArrayList<PdfTemplate>();
    private static Map<String, Integer> pageByTitle = new HashMap<>();
    static Font tocFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

    static Map<String, Map<String, Map<String, String>>> data1 = null;
    static PdfWriter writer;
    static Document document = new Document(PageSize.A4);

    private static String dfccPartNo;
    private static String dfccSLNo;
    private static String uutID;
    private static String uutType;

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
            filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
            updatedFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + "updated_" + fileName;
        } else {
            filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
            updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;
        }

        String excelPath = "";
        if (!DFCCConstant.isJarBuild) {
            excelPath = "C:\\Users\\Teclever\\Downloads\\New folder\\REPORT_FIELDS_ESS.xlsx";
        } else {
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

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f, 0);

            float startX = 02.0f;
            float endX = 35.0f;
            float y = 440.0f;

            canvas.saveState();
            canvas.setLineWidth(1f);
            canvas.moveTo(startX, y);
            canvas.lineTo(endX, y);
            canvas.stroke();
            canvas.restoreState();
        }
        stamper.close();
        reader.close();
        res.setResponseMessage("updated_" + fileName);
        return res;
    }

    private void addImageToFirstPage(PdfWriter writer, Document document) throws IOException, DocumentException {
        UserManagementModule user = new UserManagementModule();
        UserLoginDetailsDto u = user.getUserByUserId(currentSessionDetails.getUserId());

        Blob signatureBlob = u.getDigitalSignature();
        if(signatureBlob!=null)
		{
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

    public Response generatePQTReportContent(String sessionId)
            throws Exception,DocumentException, MalformedURLException, IOException{
        
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

        String fileName = "PQTContent"
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

        String filePath = "";
        String updatedFilePath = "";
        if (!DFCCConstant.isJarBuild) {
            filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
            updatedFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + "updated_" + fileName;
        } else {
            filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
            updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;
        }

        String excelPath = "";
        if (!DFCCConstant.isJarBuild) {
            excelPath = "C:\\Users\\Teclever\\Downloads\\New folder\\REPORT_FIELDS_PQT1.xlsx";
        } else {
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
//        08122025
//        essReportSummary(document, data, sessionId);
          pqtReportSummary(document, data, sessionId);
        document.close();

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

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f, 0);

            canvas.saveState();
            canvas.setLineWidth(1f);
            canvas.moveTo(02.0f, 440.0f);
            canvas.lineTo(35.0f, 440.0f);
            canvas.stroke();
            canvas.restoreState();
        }
        stamper.close();
        reader.close();
        res.setResponseMessage("updated_" + fileName);
        return res;
    }
    
    public Response generateHISTORYReportContent(String sessionId)
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

        String fileName = "HistoryContent"
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

        String filePath = "";
        String updatedFilePath = "";
        if (!DFCCConstant.isJarBuild) {
            filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
            updatedFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + "updated_" + fileName;
        } else {
            filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
            updatedFilePath = currentDirectory + File.separator + "Reports" + File.separator + "updated_" + fileName;
        }

        String excelPath = "";
        if (!DFCCConstant.isJarBuild) {
            excelPath = "C:\\Users\\Teclever\\Downloads\\New folder\\REPORT_FIELDS_HISTORY1.xlsx";
        } else {
            excelPath = currentDirectory + File.separator + "REPORT_FIELDS_HISTORY.xlsx";
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

            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageNumberText, font), 382.0f, 709.0f, 0);

            canvas.saveState();
            canvas.setLineWidth(1f);
            canvas.moveTo(02.0f, 440.0f);
            canvas.lineTo(35.0f, 440.0f);
            canvas.stroke();
            canvas.restoreState();
        }
        stamper.close();
        reader.close();
        res.setResponseMessage("updated_" + fileName);
        return res;
    }

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
				filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
				contentFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + res1.getResponseMessage();
			} else {
				filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
				contentFilePath = currentDirectory + File.separator + "Reports" + File.separator
						+ res1.getResponseMessage();
			}

			res.setDownloadPath(filePath);

			List<String> pdfFiles = new ArrayList<String>();
			pdfFiles.add(contentFilePath);

			ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
			ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
			reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "ESS");
			int annexureCount = 1;
			for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
				File file = new File(reportConfigDTO.getFileName());
				ImageToPdfConverter img = new ImageToPdfConverter();
				boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
				// 03

				String annexureFilePath = currentDirectory + File.separator + "Annexure" + annexureCount + ".pdf";
				if (!DFCCConstant.isJarBuild)// mani
					annexureFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + "Annexure" + annexureCount
							+ ".pdf";

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
			if (DFCCConstant.isJarBuild) {
				pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");
			} else

			{
				// "C:\\Users\\Teclever\\Downloads\\New folder\\"+"Annexure" + annexureCount +
				// ".pdf";
				pdfFiles.add(
						"C:\\Users\\Teclever\\Downloads\\New folder\\" + "Annexure" + finalAnnextureCount + ".pdf");
			}
//            pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");

			Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
			Response resDetailed = reportGeneration.generateDetailedReportESSPQTSession(sessionId);
			int resResultCode = resReport.getResponseCode();
			int resDetCode = resDetailed.getResponseCode();

			if (resResultCode == 1) {
				if (!DFCCConstant.isJarBuild) {// s@i
					pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\BriefReport_Session.pdf");
				} else {
					pdfFiles.add(
							currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
				}
			}

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
			res.setResponseMessage("Ess Report Download Successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

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
                filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\Reports\\" + fileName;
                contentFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + res1.getResponseMessage();
            } else {
                filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
                contentFilePath = currentDirectory + File.separator + "Reports" + File.separator + res1.getResponseMessage();
            }

            res.setDownloadPath(filePath);

            List<String> pdfFiles = new ArrayList<String>();
            pdfFiles.add(contentFilePath);

            ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
            ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
            reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
            int annexureCount = 1;
            for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
                File file = new File(reportConfigDTO.getFileName());
                ImageToPdfConverter img = new ImageToPdfConverter();
                boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
                String  annexureFilePath="";
                if(DFCCConstant.isJarBuild){//pr@s@d
                 annexureFilePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/" + "Annexure" + annexureCount + ".pdf";
                }else {
                annexureFilePath = "C:\\Users\\Teclever\\Downloads\\"+"Annexure" + annexureCount + ".pdf";
                }
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
            //mani03
          if(DFCCConstant.isJarBuild)
          {
            pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");
          }
          else
        	  
          {
        	  //"C:\\Users\\Teclever\\Downloads\\New folder\\"+"Annexure" + annexureCount + ".pdf";
        	  pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\"+ "Annexure" + finalAnnextureCount + ".pdf");
          }
            
            Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
            Response resDetReport = reportGeneration.generateDetailedReportESSPQTSession(sessionId);
            int resResultCode = resReport.getResponseCode();
            int resDetCode = resDetReport.getResponseCode();
            
            if (resResultCode == 1) {
                if (!DFCCConstant.isJarBuild) {
                    pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\BriefReport_Session.pdf");
                } else {
                    pdfFiles.add(currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
                }
            }
            
            if (resDetCode == 1) {
                if (!DFCCConstant.isJarBuild) {
                    pdfFiles.add("C:\\Users\\Teclever\\Downloads\\New folder\\DetailedReport_Session.pdf");
                } else {
                    pdfFiles.add(currentDirectory + File.separator + "Reports" + File.separator + "DetailedReport_Session.pdf");
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
            res.setResponseMessage("Pqt Report Download Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


//    Suji added For History Repot:
    
    public Response generateHISTORYReport(String sessionId) {
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
            res1 = generateHISTORYReportContent(sessionId);
            String fileName = "HISTORY_Report"
                    + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
            String filePath = "";
            String contentFilePath = "";
            if (!DFCCConstant.isJarBuild) {
                filePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + fileName;
                contentFilePath = "C:\\Users\\Teclever\\Downloads\\New folder\\" + res1.getResponseMessage();
            } else {
                filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
                contentFilePath = currentDirectory + File.separator + "Reports" + File.separator + res1.getResponseMessage();
            }

            res.setDownloadPath(filePath);

            List<String> pdfFiles = new ArrayList<String>();
            pdfFiles.add(contentFilePath);

            ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
            ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
            reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "History");
            int annexureCount = 1;
            for (ReportConfigDto reportConfigDTO : reportConfigResponse.getListOfReportConfigDto()) {
                File file = new File(reportConfigDTO.getFileName());
                ImageToPdfConverter img = new ImageToPdfConverter();
                boolean checkPdf = file.exists() && file.getName().toLowerCase().endsWith(".pdf");
                String annexureFilePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/" + "Annexure" + annexureCount + ".pdf";//change here path 
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
            if(DFCCConstant.isJarBuild) {
            pdfFiles.add(currentDirectory + File.separator + "Annexure" + finalAnnextureCount + ".pdf");
            }else {
            	 pdfFiles.add("C:\\Users\\Teclever\\Downloads\\BriefReport_Session.pdf");
            }
            Response resReport = reportGeneration.generateBreifReportESSPQTSession(sessionId);
            int resResultCode = resReport.getResponseCode();
            
            
            Response resDetReport = reportGeneration.generateDetailedReportESSPQTSession(sessionId);
            int resDetResultCode = resDetReport.getResponseCode();
            
            if (resResultCode == 1) {
                if (!DFCCConstant.isJarBuild) {
                    pdfFiles.add("C:\\Users\\Teclever\\Downloads\\BriefReport_Session.pdf");
                } else {
                    pdfFiles.add(currentDirectory + File.separator + "Reports" + File.separator + "BriefReport_Session.pdf");
                }
            }
            
            if (resDetResultCode == 1) {
                if (!DFCCConstant.isJarBuild) {
                    pdfFiles.add("C:\\Users\\Teclever\\Downloads\\DetailedReport_Session.pdf");
                } else {
                    pdfFiles.add(currentDirectory + File.separator + "Reports" + File.separator + "DetailedReport_Session.pdf");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            
//                            ////System.out.println("TOC"+ "         " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + "."
//                                            + tocPlaceHolderCountH3 + " " + subSubHeadingh3);
                            
             
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
                }

                if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Part No")) {
                    partNo = dfccPartNo;
                }

                if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Title")) {
                    title = secondCell.toString();
                }

                if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Preface")) {
                    preface = secondCell.toString();
                }

                if (firstCell != null && !firstCell.toString().equals("")
                        && firstCell.toString().equals("Report Title")) {
                    reportTitle = uutType;
                }

                if (firstCell != null && !firstCell.toString().equals("")
                        && firstCell.toString().equals("Prepared Date")) {
                    preparedDate = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
                }
                if (firstCell != null && !firstCell.toString().equals("")
                        && firstCell.toString().equals("Verified Date")) {
                    verifiedDate = new SimpleDateFormat("dd-MM-yy").format(Calendar.getInstance().getTime());
                }
                if (firstCell != null && !firstCell.toString().equals("")
                        && firstCell.toString().equals("Prepared By")) {
                    preparedBy = secondCell.toString();
                }
                if (firstCell != null && !firstCell.toString().equals("")
                        && firstCell.toString().equals("Verified By")) {
                    verifiedBy = secondCell.toString();
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

    public static void pqtReportSummary(Document document, Map<String, Map<String, Map<String, String>>> data,
            String sessionId) throws DocumentException, MalformedURLException, IOException,Exception {
        String imagePath = "";

        if (!DFCCConstant.isJarBuild) {
            imagePath = "src/Resources/Images/BellLogoRocket.png";
        } else {
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
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        BaseColor textColor = new BaseColor(22, 28, 99);
        BaseColor bcolor = new BaseColor(228, 239, 255);
        Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
        Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
        Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
        PdfPTable table = new PdfPTable(3);
        float[] columnWidths = { 2, 2, 2 };
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
        int summaryPlaceHolderCountSub;
        int summaryPlaceHolderCountH3;
        //Added for to calculate space to move the heading to next page
        float pageHeight = document.getPageSize().getHeight();
        float topMargin = document.topMargin();
        float bottomMargin = document.bottomMargin();
        float usableHeight = pageHeight - topMargin - bottomMargin;
        float thresholdHeight = bottomMargin + usableHeight * 0.25f; 
    	float height = bottomMargin + usableHeight * 0.3f; 
        float Hheight=bottomMargin+usableHeight*0.35f;
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
            String heading = entry.getKey();
            Map<String, Map<String, String>> subheadings = entry.getValue();

            if (heading.equalsIgnoreCase("Table Of Contents")) {
                createTOCByRead1(document, data);
                document.newPage();
                continue;
            }
            //this flag added for page number issue(to match in index pg and normal pag ) in pdfReport
            boolean flagPageInc = false;
            heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
            BaseFont baseFont1 = BaseFont.createFont();
            String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
            //changed for to move heading
        	float currentY = writer.getVerticalPosition(true);
            if (heading.contains("Preface")) {
                // Preface heading: just add
                document.add(new Paragraph(heading,
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
            }	//added for heading issue to move next page, where ever threshold height and cuurentY like that all changes done for heading issue  
            else if (currentY < thresholdHeight) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc = true;
            }else if (currentY < height) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc = true;
            }
            else if (currentY < Hheight) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc = true;
            }
            else {
                // Normal behavior: add heading
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
            }

            if (tocPlaceholder.containsKey(headingPageNum)) {
            	int countPgNum=0;
                PdfTemplate template = tocPlaceholder.get(headingPageNum);
                template.beginText();
                template.setFontAndSize(baseFont1, 8);
                //This is added for adding "0" (to look good) to single digit
                String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                if (writer.getPageNumber() > 10) {
                	if(flagPageInc) {
                		countPgNum=1;
                	}
                    template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                    template.showText(pageNum);
                    template.endText();
                }
                //this one is added for pageNumber space issue (after printing pgNum in index page after pg number space is coming for that we added) in ReportPdf
                else if(writer.getPageNumber()==10) {
                	System.err.println(writer.getPageNumber()+"-3");
                	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                    template.showText(pageNum);
                    template.endText();
                }
                else {
                    template.setTextMatrix(45 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                    template.showText(pageNum);
                    template.endText();
                }
                tocPlaceholder.remove(headingPageNum);
            }

            if (heading.contains("(TABLE)")) {
                document.add(new Paragraph("\n"));
                for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
                    String subheading = subEntry.getKey();
                    String content = subEntry.getKey();

                    String[] subheadinglines = subheading.split("\n");
                    String lines = subheadinglines[0];
                    String[] lineArray = lines.split("|");

                    for (int i = 0; i <= subheadinglines.length - 1; i++) {
                        String[] lineSeparting = subheadinglines[i].split(";");
                        PdfPTable table1 = new PdfPTable(lineSeparting.length);

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
            } else if (heading.contains("Appendix")) {
            	
                if (tocPlaceholder.containsKey(headingPageNum)) {
                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                    int countPgNum=0;
                    template.beginText();
                    template.setFontAndSize(baseFont1, 8);
                    if (writer.getPageNumber() > 10) {
                    	if(flagPageInc) {
                    		countPgNum=1;
                    	}
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }else if(writer.getPageNumber()==10) {
                    	System.err.println(writer.getPageNumber()+"-31");
                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                        template.showText(String.valueOf(writer.getPageNumber()-1));
                        template.endText();
                    } 
                    
                    else {
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }
                    tocPlaceholder.remove(headingPageNum);
                }

                document.add(new Paragraph("\n"));
                ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
                ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
                reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "PQT");
                List<ReportConfigDto> reportConfigDtoList = reportConfigResponse.getListOfReportConfigDto();

                if (reportConfigDtoList.size() > 0) {
                    PdfPTable tableAppendix = new PdfPTable(2);
                    float[] columnWidthsAppedix = { 2.5f, 2.5f };
                    tableAppendix.setWidths(columnWidthsAppedix);

                    Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                    String[] headers = { "Reference", "Description" };
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tableAppendix.addCell(cell);
                    }

                    tableAppendix.setHeaderRows(1);

                    int AppendixCount = 1;
                    for (ReportConfigDto dto : reportConfigDtoList) {
                        tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
                        tableAppendix.addCell(new Phrase(dto.getLevelOneName()));
                        AppendixCount++;
                    }
                    tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
                    tableAppendix.addCell(new Phrase("PQT test result summary"));
                    document.add(tableAppendix);
                }
            } else if (heading.contains("Session Details Summary")) {
                if (heading.contains("Session Details Summary")) {
                    document.add(new Paragraph(" " + headingPageNum + "." + " ",
                            new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                }

                if (tocPlaceholder.containsKey(headingPageNum)) {
                	int countPgNum=0;
                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                    template.beginText();
                    template.setFontAndSize(baseFont1, 8);
                    if (writer.getPageNumber() > 10) {
                    	if(flagPageInc) {
                    		countPgNum=1;
                    	}
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }else if(writer.getPageNumber()==10) {
                    	System.err.println(writer.getPageNumber()+"-32");
                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                        template.showText(String.valueOf(writer.getPageNumber()-1));
                        template.endText();
                    }
                    else {
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }
                    tocPlaceholder.remove(headingPageNum);
                }
                document.add(new Paragraph("\n"));

                PdfPTable tableStageResult = new PdfPTable(6);
                float[] columnWidthsStageResults = { 1f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f };
                tableStageResult.setWidths(columnWidthsStageResults);

                Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                String[] headers = { "S.No", "Stage Name", "Start Date", "End Date", "Remarks", "Results" };
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableStageResult.addCell(cell);
                }

                tableStageResult.setHeaderRows(1);

                int sNoCount = 1;
                SessionManagement sessionManagement = new SessionManagement();
                StageRemarksResponse stageRemarksResponse = new StageRemarksResponse();
                stageRemarksResponse = sessionManagement.getStagesRemarks(sessionId, "ESS");

                List<StagesRemarksDto> getStagesRemarksList = new ArrayList<StagesRemarksDto>();
                getStagesRemarksList = stageRemarksResponse.getRemarks();
                Map<String, String> stageIdName = new HashMap<String, String>();
                stageIdName = sessionManagement.getAllStageIdName();

                SummaryDetails summaryDetails = agetResultForSessionSummaryByLevelOne(getStagesRemarksList, sessionId);
                Map<String, String> levelOneIdResults = summaryDetails.getLevelOneIdResult();
                Map<String, String> levelOneIdStartDate = summaryDetails.getLevelOneIdStartDate();
                Map<String, String> levelOneIdEndDate = summaryDetails.getLevelOneIdEndDate();
                for (StagesRemarksDto dto : getStagesRemarksList) {
                    PdfPCell serialNumberCell = new PdfPCell(new Phrase(String.valueOf(sNoCount)));
                    serialNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableStageResult.addCell(serialNumberCell);

                    tableStageResult.addCell(new Phrase(dto.getLevelOneName()));
                    tableStageResult.addCell(new Phrase(levelOneIdStartDate.get(dto.getLevelOneStageId())));
                    tableStageResult.addCell(new Phrase(levelOneIdEndDate.get(dto.getLevelOneStageId())));
                    tableStageResult.addCell(new Phrase(dto.getRemarks()));
                    tableStageResult.addCell(new Phrase(levelOneIdResults.get(dto.getLevelOneStageId())));
                    sNoCount++;
                }

                document.add(tableStageResult);
            } else {
                List<String> subHeadingTags = new ArrayList<String>();
                List<String> headingTags = new ArrayList<String>();

                summaryPlaceHolderCountSub = 1;
                for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
                    String subheading = subEntry.getKey();
                    Map<String, String> h3Map = subEntry.getValue();
                    
                    float currentY1 = writer.getVerticalPosition(true);
                    float thresholdHeight1 = bottomMargin + usableHeight * 0.18f; 
                    float height1 = bottomMargin + usableHeight * 0.24f; 
                    float height2 = bottomMargin + usableHeight * 0.3f;
                    String subHeadingPageNum = "";
                    if (subheading.contains("(h2)")) {
                        subheading = subheading.replaceAll("(h2)", "");
                        String subSubHeading = subheading.substring(0, subheading.length() - 2);

                        subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub + " "
                                + subSubHeading;
                        
                                //sai03-12-2025
                        
                        if (currentY1 < thresholdHeight1) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}else if (currentY1 < height1) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}
            			else if (currentY1 < height2) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}
                        else {
                        document.add(new Paragraph("  " + subHeadingPageNum,
                                new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                    }

                        if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                        	int countPgNum=0;
                            PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                            template.beginText();
                            template.setFontAndSize(baseFont1, 8);
                            String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                            if (writer.getPageNumber() > 10) {
                            	if(flagPageInc) {
                            		countPgNum=1;
                            	}
                                template.setTextMatrix(
                                        50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                template.showText(pageNum);
                                template.endText();
                            }else if(writer.getPageNumber()==10) {
                            	System.err.println(writer.getPageNumber()+"-34");
                            	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                template.showText(pageNum);
                                template.endText();
                            }
                            else {
                                template.setTextMatrix(
                                        45 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                template.showText(pageNum);
                                template.endText();
                            }
                            tocPlaceholder.remove(subHeadingPageNum);
                        }

                        summaryPlaceHolderCountH3 = 1;
                        
                        for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
                            String h3 = h3Entry.getKey();
                            String content = h3Entry.getValue();
                            String h3PageNum = "";
                            float currentY2 = writer.getVerticalPosition(true);
                            float thresholdHeight2 = bottomMargin + usableHeight * 0.15f; 
                            if (h3.contains("(h3)")) {
                                h3 = h3.replaceAll("(h3)", "");
                                h3 = h3.substring(0, h3.length() - 2);

                                h3PageNum = "         " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
                                        + "." + summaryPlaceHolderCountH3 + " " + h3;
                                //this for moving heading to next page 
                                if (currentY2 < thresholdHeight2) {
		            			    document.newPage();
		            			    document.add(new Paragraph("        " + h3PageNum,
											new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
		            			    flagPageInc=true;
		            			}else {
		            				document.add(new Paragraph("        " + h3PageNum,
											new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
		                    }
							   System.err.println(">--->h3pqt "+h3PageNum);
//                             
                                if (tocPlaceholder.containsKey(h3PageNum)) {
                                	int countPgNum=0;
                                    PdfTemplate template = tocPlaceholder.get(h3PageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(pageNum);
                                        template.endText();
                                    }else if(writer.getPageNumber()==10) {
                                    	System.err.println(writer.getPageNumber()+"-33");
                                    	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                        template.showText(pageNum);
                                        template.endText();
                                    } 
                                    
                                    else {
                                        template.setTextMatrix(45
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(pageNum);
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(h3PageNum);
                                }

                                float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                                ColumnText ct = new ColumnText(writer.getDirectContent());
                                ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                        document.top() - document.bottom());
                                ct.setLeading(0, 1.2f);
                                ct.setAlignment(Element.ALIGN_LEFT);

                                Phrase phrase = new Phrase(content);
                                ct.addText(phrase);

                                int status = ct.go(true);
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
//                                        document.newPage();
                                        ct.go();
                                    }
                                }

                                if (!headingTags.contains(headingPageNum)) {
                                	
                                    if (tocPlaceholder.containsKey(headingPageNum)) {
                                    	int countPgNum=0;
                                        PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        } else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(headingPageNum);
                                    }
                                    headingTags.add(headingPageNum);
                                }

                                if (!subHeadingTags.contains(subHeadingPageNum)) {
                                	   int countPgNum=0;
                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }else if(writer.getPageNumber()==10) {
                                        	System.err.println(writer.getPageNumber()+"-3");
                                        	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber()-1));
                                            template.endText();
                                        } 
                                        
                                        else {
                                            template.setTextMatrix(45 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }
                                    subHeadingTags.add(subHeadingPageNum);
                                }

                                if (paragraphHeight < 700) {
                                    Paragraph contentParagraph = new Paragraph(content,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    contentParagraph.setIndentationLeft(5f);
                                    contentParagraph.setIndentationRight(5f);
                                    contentParagraph.setSpacingBefore(10f);
                                    contentParagraph.setSpacingAfter(10f);
                                    document.add(contentParagraph);
                                } else {
                                    Paragraph contentParagraph = new Paragraph(content,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    contentParagraph.setSpacingBefore(10f);
                                    contentParagraph.setSpacingAfter(10f);
                                    document.add(contentParagraph);
                                }
                            } else {
                                float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                                ColumnText ct = new ColumnText(writer.getDirectContent());
                                ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                        document.top() - document.bottom());
                                ct.setLeading(0, 1.2f);
                                ct.setAlignment(Element.ALIGN_LEFT);

                                Phrase phrase = new Phrase(h3);
                                ct.addText(phrase);

                                int status = ct.go(true);
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
                                        ct.go();
                                    }
                                }

                                if (paragraphHeight < 700) {
                                 	
                                    if (!headingTags.contains(headingPageNum)) {
                                    	int countPgNum=0;
                                        if (tocPlaceholder.containsKey(headingPageNum)) {
                                            PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                            template.beginText();
                                            template.setFontAndSize(baseFont1, 8);
                                            if (writer.getPageNumber() > 10) {
                                            	if(flagPageInc) {
                                            		countPgNum=1;
                                            	}
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            }else if(writer.getPageNumber()==10) {
                                            	System.err.println(writer.getPageNumber()+"-3#");
                                            	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                                template.showText(String.valueOf(writer.getPageNumber()-1));
                                                template.endText();
                                            }
                                            
                                            else {
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            }
                                            tocPlaceholder.remove(headingPageNum);
                                        }
                                        headingTags.add(headingPageNum);
                                    }

                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                    	int countPgNum=0;
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }else if(writer.getPageNumber()==10) {
                                        	System.err.println(writer.getPageNumber()+"-3");
                                        	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber()-1));
                                            template.endText();
                                        } 
                                        else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }

                                    Paragraph h3Paragraph = new Paragraph(h3,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    h3Paragraph.setIndentationLeft(5f);
                                    h3Paragraph.setIndentationRight(5f);
                                    h3Paragraph.setSpacingBefore(10);
                                    h3Paragraph.setSpacingAfter(10);
                                    document.add(h3Paragraph);
                                } else {
                                    if (!headingTags.contains(headingPageNum)) {
                                                int countPgNum=0;
                                        if (tocPlaceholder.containsKey(headingPageNum)) {
                                            PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                            template.beginText();
                                            template.setFontAndSize(baseFont1, 8);
                                            if (writer.getPageNumber() > 10) {
                                            	if(flagPageInc) {
                                            		countPgNum=1;
                                            	}
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            } else {
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            }
                                            tocPlaceholder.remove(headingPageNum);
                                        }
                                        headingTags.add(headingPageNum);
                                    }

                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                    	int countPgNum=0;
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }else if(writer.getPageNumber()==10) {
                                        	System.err.println(writer.getPageNumber()+"-3r");
                                        	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber()-1));
                                            template.endText();
                                        }
                                        else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }

                                    Paragraph h3Paragraph = new Paragraph(h3,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    h3Paragraph.setIndentationLeft(5f);
                                    h3Paragraph.setIndentationRight(5f);
                                    h3Paragraph.setSpacingBefore(10);
                                    h3Paragraph.setSpacingAfter(10);
                                    document.add(h3Paragraph);
                                }
                            }
                            summaryPlaceHolderCountH3++;
                        }
                    } else {
                        float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                        ColumnText ct = new ColumnText(writer.getDirectContent());
                        ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                document.top() - document.bottom());
                        ct.setLeading(0, 1.2f);
                        ct.setAlignment(Element.ALIGN_LEFT);

                        Phrase phrase = new Phrase(subheading);
                        ct.addText(phrase);

                        int status = ct.go(true);
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
                                ct.go();
                            }
                        }

                        if (paragraphHeight < 700) {
                            if (!headingTags.contains(headingPageNum)) {
                            int countPgNum=0;
                                if (tocPlaceholder.containsKey(headingPageNum)) {
                                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }else if(writer.getPageNumber()==10) {
                                    	System.err.println(writer.getPageNumber()+"-3d");
                                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                        template.showText(String.valueOf(writer.getPageNumber()-1));
                                        template.endText();
                                    } 
                                    else {
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(headingPageNum);
                                }
                                headingTags.add(headingPageNum);
                            }

                            if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                int countPgNum=0;
                                template.beginText();
                                template.setFontAndSize(baseFont1, 8);
                                if (writer.getPageNumber() > 10) {
                                	if(flagPageInc) {
                                		countPgNum=1;
                                	}
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }else if(writer.getPageNumber()==10) {
                                	System.err.println(writer.getPageNumber()+"-3g");
                                	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                    template.showText(String.valueOf(writer.getPageNumber()-1));
                                    template.endText();
                                } 
                                
                                else {
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }
                                tocPlaceholder.remove(subHeadingPageNum);
                            }
                            Paragraph paragraph = new Paragraph(subheading,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                            paragraph.setIndentationLeft(20);
                            paragraph.setIndentationRight(20);
                            paragraph.setSpacingBefore(10);
                            paragraph.setSpacingAfter(10);
                            document.add(paragraph);
                        } else {
                            if (!headingTags.contains(headingPageNum)) {
                                if (tocPlaceholder.containsKey(headingPageNum)) {
                                	int countPgNum=0;
                                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }else if(writer.getPageNumber()==10) {
                                    	System.err.println(writer.getPageNumber()+"-3f");
                                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                        template.showText(String.valueOf(writer.getPageNumber()-1));
                                        template.endText();
                                    } 
                                    else {
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(headingPageNum);
                                }
                                headingTags.add(headingPageNum);
                            }

                            if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                            	
                            	int countPgNum=0;
                                PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                template.beginText();
                                template.setFontAndSize(baseFont1, 8);
                                if (writer.getPageNumber() > 10) {
                                	if(flagPageInc) {
                                		countPgNum=1;
                                	}
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }else if(writer.getPageNumber()==10) {
                                	System.err.println(writer.getPageNumber()+"-3t");
                                	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                    template.showText(String.valueOf(writer.getPageNumber()-1));
                                    template.endText();
                                }
                                
                                else {
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }
                                tocPlaceholder.remove(subHeadingPageNum);
                            }

                            Paragraph paragraph = new Paragraph(subheading,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                            paragraph.setSpacingBefore(10);
                            paragraph.setSpacingAfter(10);
                            document.add(paragraph);
                        }

                        for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
                            String content = h3Entry.getValue();
                            document.add(new Paragraph("            " + content,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10)));
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

    public static void essReportSummary(Document document, Map<String, Map<String, Map<String, String>>> data,
            String sessionId) throws DocumentException, MalformedURLException, IOException {
        String imagePath = "";
    

        if (!DFCCConstant.isJarBuild) {
            imagePath = "src/Resources/Images/BellLogoRocket.png";
        } else {
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
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        BaseColor textColor = new BaseColor(22, 28, 99);
        BaseColor bcolor = new BaseColor(228, 239, 255);
        Font boldFont1 = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font boldFont4 = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD);
        Font boldFont2 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
        Font boldFont3 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font boldFont5 = new Font(Font.FontFamily.COURIER, 8, Font.BOLD);
        PdfPTable table = new PdfPTable(3);
        float[] columnWidths = { 2, 2, 2 };
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
        int summaryPlaceHolderCountSub;
        int summaryPlaceHolderCountH3;
        //sai03122025 added for to move heading to next page
        float pageHeight = document.getPageSize().getHeight();
		float topMargin = document.topMargin();
		float bottomMargin = document.bottomMargin();
		float usableHeight = pageHeight - topMargin - bottomMargin;
		float thresholdHeight = bottomMargin + usableHeight * 0.25f; 
		float height = bottomMargin + usableHeight * 0.3f; 
		float Hheight = bottomMargin + usableHeight * 0.35f; 
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : data.entrySet()) {
            String heading = entry.getKey();
            Map<String, Map<String, String>> subheadings = entry.getValue();
            //this flag added for pagenumber issue(missMatch in index and actual page) in pdfreport
              boolean flagPageInc=false;
            if (heading.equalsIgnoreCase("Table Of Contents")) {
                createTOCByRead1(document, data);
                document.newPage();
                continue;
            }

            heading = heading.replaceAll("(-h1)", "").replaceAll("()", "");
            BaseFont baseFont1 = BaseFont.createFont();
            String headingPageNum = "  " + summaryPlaceHolderCount + " " + heading;
        	float currentY = writer.getVerticalPosition(true);
        	//added for heading issue to move next page, where ever threshold height and cuurentY is there that all changes done for heading issue in pdf report
        	
            if (heading.contains("Preface")) {
                // Preface heading: just add
                document.add(new Paragraph(heading,
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
            } else if (currentY < thresholdHeight) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc=true;
            }else if (currentY < height) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc=true;
            }
            else if (currentY < Hheight) {
                // Not enough space: move to next page first
                document.newPage();
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                flagPageInc=true;
            }
            else {
                // Normal behavior: add heading
                document.add(new Paragraph(" " + headingPageNum + ".",
                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
            }

            if (tocPlaceholder.containsKey(headingPageNum)) {
            	int countPgNum=0;
                PdfTemplate template = tocPlaceholder.get(headingPageNum);
                template.beginText();
                template.setFontAndSize(baseFont1, 8);
                String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                if (writer.getPageNumber() > 10) {
                	if(flagPageInc) {
                		countPgNum=1;
                	}
                    template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                    template.showText(pageNum);
                    template.endText();
                }
                //added for pagenumber space issue 1112225 
                else if(writer.getPageNumber()==10) {
//                	System.err.println(writer.getPageNumber()+"-3bbb");
                	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                    template.showText(pageNum);
                    template.endText();
                } 
                else {
                    template.setTextMatrix(45 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                    template.showText(pageNum);
                    template.endText();
                }
                tocPlaceholder.remove(headingPageNum);
            }

            if (heading.contains("(TABLE)")) {
                document.add(new Paragraph("\n"));
                for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
                    String subheading = subEntry.getKey();
                    String content = subEntry.getKey();

                    String[] subheadinglines = subheading.split("\n");
                    String lines = subheadinglines[0];
                    String[] lineArray = lines.split("|");

                    for (int i = 0; i <= subheadinglines.length - 1; i++) {
                        String[] lineSeparting = subheadinglines[i].split(";");
                        PdfPTable table1 = new PdfPTable(lineSeparting.length);

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
            } else if (heading.contains("Appendix")) {
            	
//                document.add(new Paragraph(" " + headingPageNum + "." + " ",
//                        new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));

                if (tocPlaceholder.containsKey(headingPageNum)) {
                	int countPgNum=0;
                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                    template.beginText();
                    template.setFontAndSize(baseFont1, 8);
                    if (writer.getPageNumber() > 10) {
                    	if(flagPageInc) {
                    		countPgNum=1;
                    	}
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }else if(writer.getPageNumber()==10) {
                    	System.err.println(writer.getPageNumber()+"-3ess");
                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                        template.showText(String.valueOf(writer.getPageNumber()-1));
                        template.endText();
                    }
                    
                    else {
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }
                    tocPlaceholder.remove(headingPageNum);
                }

                document.add(new Paragraph("\n"));
                ReportConfigResponse reportConfigResponse = new ReportConfigResponse();
                ReportCofigurationManagement reportCofigurationManagement = new ReportCofigurationManagement();
                reportConfigResponse = reportCofigurationManagement.getAllReportConfig(sessionId, "ESS");
                List<ReportConfigDto> reportConfigDtoList = reportConfigResponse.getListOfReportConfigDto();

                if (reportConfigDtoList.size() > 0) {
                    PdfPTable tableAppendix = new PdfPTable(2);
                    float[] columnWidthsAppedix = { 2.5f, 2.5f };
                    tableAppendix.setWidths(columnWidthsAppedix);

                    Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                    String[] headers = { "Reference", "Description" };
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                        cell.setBackgroundColor(BaseColor.GRAY);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tableAppendix.addCell(cell);
                    }

                    tableAppendix.setHeaderRows(1);

                    int AppendixCount = 1;
                    for (ReportConfigDto dto : reportConfigDtoList) {
                        tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
                        tableAppendix.addCell(new Phrase(dto.getLevelOneName()));
                        AppendixCount++;
                    }
                    tableAppendix.addCell(new Phrase("Annexure - " + AppendixCount));
                    tableAppendix.addCell(new Phrase("ESS test result summary"));
                    document.add(tableAppendix);
                }
            } else if (heading.contains("Session Details Summary")) {
                if (heading.contains("Session Details Summary")) {
                    document.add(new Paragraph(" " + headingPageNum + "." + " ",
                            new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.GRAY)));
                }

                if (tocPlaceholder.containsKey(headingPageNum)) {
                	int countPgNum=0;
                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                    template.beginText();
                    template.setFontAndSize(baseFont1, 8);
                    //this is added for to add 0 for single digit
                    String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                    if (writer.getPageNumber() > 10) {
                    	if(flagPageInc) {
                    		countPgNum=1;
                    	}
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                0);
                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                        template.endText();
                    }//chnaged for pageNumberspace issue10122025
                    else if(writer.getPageNumber()==10) {
                    	System.err.println(writer.getPageNumber()+"-34www");
                    	template.setTextMatrix(58 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                        template.showText(pageNum);
                        template.endText();
                    } 
                    else {
                        template.setTextMatrix(50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                0);
                        template.showText(pageNum);
                        template.endText();
                    }
                    tocPlaceholder.remove(headingPageNum);
                }
                document.add(new Paragraph("\n"));

                PdfPTable tableStageResult = new PdfPTable(6);
                float[] columnWidthsStageResults = { 1f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f };
                tableStageResult.setWidths(columnWidthsStageResults);

                Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                String[] headers = { "S.No", "Stage Name", "Start Date", "End Date", "Remarks", "Results" };
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setBackgroundColor(BaseColor.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableStageResult.addCell(cell);
                }

                tableStageResult.setHeaderRows(1);

                int sNoCount = 1;
                SessionManagement sessionManagement = new SessionManagement();
                StageRemarksResponse stageRemarksResponse = new StageRemarksResponse();
                stageRemarksResponse = sessionManagement.getStagesRemarks(sessionId, "ESS");

                List<StagesRemarksDto> getStagesRemarksList = new ArrayList<StagesRemarksDto>();
                getStagesRemarksList = stageRemarksResponse.getRemarks();
                Map<String, String> stageIdName = new HashMap<String, String>();
                stageIdName = sessionManagement.getAllStageIdName();

                SummaryDetails summaryDetails = agetResultForSessionSummaryByLevelOne(getStagesRemarksList, sessionId);
                Map<String, String> levelOneIdResults = summaryDetails.getLevelOneIdResult();
                Map<String, String> levelOneIdStartDate = summaryDetails.getLevelOneIdStartDate();
                Map<String, String> levelOneIdEndDate = summaryDetails.getLevelOneIdEndDate();
                for (StagesRemarksDto dto : getStagesRemarksList) {
                    PdfPCell serialNumberCell = new PdfPCell(new Phrase(String.valueOf(sNoCount)));
                    serialNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableStageResult.addCell(serialNumberCell);

                    tableStageResult.addCell(new Phrase(dto.getLevelOneName()));
                    tableStageResult.addCell(new Phrase(levelOneIdStartDate.get(dto.getLevelOneStageId())));
                    tableStageResult.addCell(new Phrase(levelOneIdEndDate.get(dto.getLevelOneStageId())));
                    tableStageResult.addCell(new Phrase(dto.getRemarks()));
                    tableStageResult.addCell(new Phrase(levelOneIdResults.get(dto.getLevelOneStageId())));
                    sNoCount++;
                }

                document.add(tableStageResult);
            } else {
                List<String> subHeadingTags = new ArrayList<String>();
                List<String> headingTags = new ArrayList<String>();

                summaryPlaceHolderCountSub = 1;
                for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
                    String subheading = subEntry.getKey();
                    Map<String, String> h3Map = subEntry.getValue();
                    
                    float currentY1 = writer.getVerticalPosition(true);
                    float thresholdHeight1 = bottomMargin + usableHeight * 0.18f; 
                    float height1 = bottomMargin + usableHeight * 0.24f; 
                    float height2 = bottomMargin + usableHeight * 0.3f;
                    String subHeadingPageNum = "";
                    if (subheading.contains("(h2)")) {
                        subheading = subheading.replaceAll("(h2)", "");
                        String subSubHeading = subheading.substring(0, subheading.length() - 2);

                        subHeadingPageNum = "     " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub + " "
                                + subSubHeading;
                        
                  //sai03-12-2025 for moving heading into next page
                        
                        if (currentY1 < thresholdHeight1) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}else if (currentY1 < height1) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}
            			else if (currentY1 < height2) {
            			    document.newPage();
            			    document.add(new Paragraph("  " + subHeadingPageNum,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            			    flagPageInc=true;
            			}
                        else {
                        document.add(new Paragraph("  " + subHeadingPageNum,
                                new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
                    }
                        ////System.out.println(">--->"+subHeadingPageNum);
                        if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                        	int countPgNum=0;
                            PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                            template.beginText();
                            template.setFontAndSize(baseFont1, 8);
                            //This is added for adding the 0 for single digit 11052025
                            String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                            if (writer.getPageNumber() > 10) {
                            	if(flagPageInc) {
                            		countPgNum=1;
                            	}
                                template.setTextMatrix(
                                        50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                template.showText(pageNum);
                                template.endText();
                            }//added for pagenumber space issue
                            else if(writer.getPageNumber()==10) {
                            	System.err.println(writer.getPageNumber()+"-34ess");
                            	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                template.showText(pageNum);
                                template.endText();
                            } 
                            
                            else {
                                template.setTextMatrix(
                                        45 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                template.showText(pageNum);
                                template.endText();
                            }
                            tocPlaceholder.remove(subHeadingPageNum);
                        }

                        summaryPlaceHolderCountH3 = 1;
                       

                        for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
                            String h3 = h3Entry.getKey();
                            String content = h3Entry.getValue();
                            String h3PageNum = "";
                            float currentY2 = writer.getVerticalPosition(true);
                            float thresholdHeight2 = bottomMargin + usableHeight * 0.15f; 
                            if (h3.contains("(h3)")) {
                                h3 = h3.replaceAll("(h3)", "");
                                h3 = h3.substring(0, h3.length() - 2);

                                h3PageNum = "         " + summaryPlaceHolderCount + "." + summaryPlaceHolderCountSub
                                        + "." + summaryPlaceHolderCountH3 + " " + h3;
                                //Here Need to Add h3PageNum With Proper Format...
                                if (currentY2 < thresholdHeight2) {
		            			    document.newPage();
		            			    document.add(new Paragraph("        " + h3PageNum,
											new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
		            			    flagPageInc=true;
		            			}
                                
                                else {
		            				document.add(new Paragraph("        " + h3PageNum,
											new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD)));
		                    }
							
//                                document.add(new Paragraph("  " + h3PageNum,
//                                        new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
                                ////System.out.println(">--->h3 "+h3PageNum);
                                if (tocPlaceholder.containsKey(h3PageNum)) {
                                	int countPgNum=0;
                                    PdfTemplate template = tocPlaceholder.get(h3PageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    String pageNum = String.format("%02d", writer.getPageNumber() - 1);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(pageNum);
                                        template.endText();
                                    } else if(writer.getPageNumber()==10) {
                                    	System.err.println(writer.getPageNumber()+"-34ess");
                                    	template.setTextMatrix(53 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                        template.showText(pageNum);
                                        template.endText();
                                    } 
                                    else {
                                        template.setTextMatrix(45
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(h3PageNum);
                                }

                                float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                                ColumnText ct = new ColumnText(writer.getDirectContent());
                                ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                        document.top() - document.bottom());
                                ct.setLeading(0, 1.2f);
                                ct.setAlignment(Element.ALIGN_LEFT);

                                Phrase phrase = new Phrase(content);
                                ct.addText(phrase);

                                int status = ct.go(true);
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
//                                        document.newPage();
                                        ct.go();
                                    }
                                }

                                if (!headingTags.contains(headingPageNum)) {
                                	int countPgNum=0;
                                    if (tocPlaceholder.containsKey(headingPageNum)) {
                                    	
                                        PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        } else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(headingPageNum);
                                    }
                                    headingTags.add(headingPageNum);
                                }

                                if (!subHeadingTags.contains(subHeadingPageNum)) {
                                	int countPgNum=0;
                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        } else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }
                                    subHeadingTags.add(subHeadingPageNum);
                                }

                                if (paragraphHeight < 700) {
                                    Paragraph contentParagraph = new Paragraph(content,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    contentParagraph.setIndentationLeft(5f);
                                    contentParagraph.setIndentationRight(5f);
                                    contentParagraph.setSpacingBefore(10f);
                                    contentParagraph.setSpacingAfter(10f);
                                    document.add(contentParagraph);
                                } else {
                                    Paragraph contentParagraph = new Paragraph(content,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    contentParagraph.setSpacingBefore(10f);
                                    contentParagraph.setSpacingAfter(10f);
                                    document.add(contentParagraph);
                                }
                            } else {
                                float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                                ColumnText ct = new ColumnText(writer.getDirectContent());
                                ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                        document.top() - document.bottom());
                                ct.setLeading(0, 1.2f);
                                ct.setAlignment(Element.ALIGN_LEFT);

                                Phrase phrase = new Phrase(h3);
                                ct.addText(phrase);

                                int status = ct.go(true);
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
                                        ct.go();
                                    }
                                }

                                if (paragraphHeight < 700) {
                                 	
                                    if (!headingTags.contains(headingPageNum)) {
                                    	int countPgNum=0;
                                        if (tocPlaceholder.containsKey(headingPageNum)) {
                                            PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                            template.beginText();
                                            template.setFontAndSize(baseFont1, 8);
                                            if (writer.getPageNumber() > 10) {
                                            	if(flagPageInc) {
                                            		countPgNum=1;
                                            	}
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            } else {
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            }
                                            tocPlaceholder.remove(headingPageNum);
                                        }
                                        headingTags.add(headingPageNum);
                                    }

                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                       int countPgNum=0;
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        } else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }

                                    Paragraph h3Paragraph = new Paragraph(h3,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    h3Paragraph.setIndentationLeft(5f);
                                    h3Paragraph.setIndentationRight(5f);
                                    h3Paragraph.setSpacingBefore(10);
                                    h3Paragraph.setSpacingAfter(10);
                                    document.add(h3Paragraph);
                                } else {
                                    if (!headingTags.contains(headingPageNum)) {
                                   int countPgNum=0;
                                        if (tocPlaceholder.containsKey(headingPageNum)) {
                                            PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                            template.beginText();
                                            template.setFontAndSize(baseFont1, 8);
                                            if (writer.getPageNumber() > 10) {
                                            	if(flagPageInc) {
                                            		countPgNum=1;
                                            	}
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            } else {
                                                template.setTextMatrix(50 - baseFont1
                                                        .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                                template.showText(String.valueOf(writer.getPageNumber() - 1));
                                                template.endText();
                                            }
                                            tocPlaceholder.remove(headingPageNum);
                                        }
                                        headingTags.add(headingPageNum);
                                    }

                                    if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                                    	int countPgNum=0;
                                        PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                        template.beginText();
                                        template.setFontAndSize(baseFont1, 8);
                                        if (writer.getPageNumber() > 10) {
                                        	if(flagPageInc) {
                                        		countPgNum=1;
                                        	}
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        } else {
                                            template.setTextMatrix(50 - baseFont1
                                                    .getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14), 0);
                                            template.showText(String.valueOf(writer.getPageNumber() - 1));
                                            template.endText();
                                        }
                                        tocPlaceholder.remove(subHeadingPageNum);
                                    }

                                    Paragraph h3Paragraph = new Paragraph(h3,
                                            new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                                    h3Paragraph.setIndentationLeft(5f);
                                    h3Paragraph.setIndentationRight(5f);
                                    h3Paragraph.setSpacingBefore(10);
                                    h3Paragraph.setSpacingAfter(10);
                                    document.add(h3Paragraph);
                                }
                            }
                            summaryPlaceHolderCountH3++;
                        }
                    } else {
                        float remainingSpace = writer.getVerticalPosition(true) - document.bottomMargin();
                        ColumnText ct = new ColumnText(writer.getDirectContent());
                        ct.setSimpleColumn(36, 36, document.right() - document.left(),
                                document.top() - document.bottom());
                        ct.setLeading(0, 1.2f);
                        ct.setAlignment(Element.ALIGN_LEFT);

                        Phrase phrase = new Phrase(subheading);
                        ct.addText(phrase);

                        int status = ct.go(true);
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
                                ct.go();
                            }
                        }

                        if (paragraphHeight < 700) {
                            if (!headingTags.contains(headingPageNum)) {
                              int countPgNum=0;
                                if (tocPlaceholder.containsKey(headingPageNum)) {
                                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    } else {
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(headingPageNum);
                                }
                                headingTags.add(headingPageNum);
                            }

                            if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                            	int countPgNum=0;
                                PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                template.beginText();
                                template.setFontAndSize(baseFont1, 8);
                                if (writer.getPageNumber() > 10) {
                                	if(flagPageInc) {
                                		countPgNum=1;
                                	}
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                } else {
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }
                                tocPlaceholder.remove(subHeadingPageNum);
                            }
                            Paragraph paragraph = new Paragraph(subheading,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                            paragraph.setIndentationLeft(20);
                            paragraph.setIndentationRight(20);
                            paragraph.setSpacingBefore(10);
                            paragraph.setSpacingAfter(10);
                            document.add(paragraph);
                        } else {
                            if (!headingTags.contains(headingPageNum)) {
                            	int countPgNum=0;
                                if (tocPlaceholder.containsKey(headingPageNum)) {
                                    PdfTemplate template = tocPlaceholder.get(headingPageNum);
                                    template.beginText();
                                    template.setFontAndSize(baseFont1, 8);
                                    if (writer.getPageNumber() > 10) {
                                    	if(flagPageInc) {
                                    		countPgNum=1;
                                    	}
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    } else {
                                        template.setTextMatrix(50
                                                - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                                0);
                                        template.showText(String.valueOf(writer.getPageNumber() - 1));
                                        template.endText();
                                    }
                                    tocPlaceholder.remove(headingPageNum);
                                }
                                headingTags.add(headingPageNum);
                            }

                            if (tocPlaceholder.containsKey(subHeadingPageNum)) {
                            	int countPgNum=0;
                            	
                                PdfTemplate template = tocPlaceholder.get(subHeadingPageNum);
                                ////System.out.println("subHeadingPageNum"+subHeadingPageNum+"template"+template);
                                template.beginText();
                                template.setFontAndSize(baseFont1, 8);
                                if (writer.getPageNumber() > 10) {
                                	if(flagPageInc) {
                                		countPgNum=1;
                                	}
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 12),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                } else {
                                    template.setTextMatrix(
                                            50 - baseFont1.getWidthPoint(String.valueOf(writer.getPageNumber()+countPgNum), 14),
                                            0);
                                    template.showText(String.valueOf(writer.getPageNumber() - 1));
                                    template.endText();
                                }
                                tocPlaceholder.remove(subHeadingPageNum);
                            }

                            Paragraph paragraph = new Paragraph(subheading,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
                            paragraph.setSpacingBefore(10);
                            paragraph.setSpacingAfter(10);
                            document.add(paragraph);
                        }

                        for (Map.Entry<String, String> h3Entry : h3Map.entrySet()) {
                            String content = h3Entry.getValue();
                            document.add(new Paragraph("            " + content,
                                    new Font(Font.FontFamily.TIMES_ROMAN, 10)));
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

//					if (!overallResult.equals("PASS") || overallResult != null) {
//						break;
//					}
					
					if (overallResult != null) {
						if (!overallResult.equals("PASS")) {
							break;
						}
					}
					
					
					
				}
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

    public static void addborder(PdfWriter writer) {
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        template.rectangle(36, 36, PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
        template.stroke();
        cb.addTemplate(template, 0, 0);
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
                addHeader(document, writer, currentPageNumber, totalPageCount);
                addborder(writer);
                currentPageNumber++;
            } catch (Exception e) {
                ////System.out.println(e.getLocalizedMessage());
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

        private void addHeader(Document document, PdfWriter writer, int currentPage, int totalPages)
                throws DocumentException, IOException {
            PdfPTable table = new PdfPTable(6);
            float[] columnWidths = { 2, 1, 3, 1, 2, 1 };
            table.setWidths(columnWidths);
            String imagePath = "src/Resources/Images/BELLOGO.png";

            if (!DFCCConstant.isJarBuild) {
                imagePath = "src/Resources/Images/BELLOGO.png";
            } else {
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

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPageCount = writer.getPageNumber();
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            totalPageCount = writer.getPageNumber();
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase(COPYRIGHT_TEXT, new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));

            int pageNumber = writer.getPageNumber();
            Rectangle pageSize = document.getPageSize();
            float x = (pageSize.getLeft() + pageSize.getRight()) / 2.2f;
            float y = pageSize.getBottom() + 15;

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
        }
    }
}