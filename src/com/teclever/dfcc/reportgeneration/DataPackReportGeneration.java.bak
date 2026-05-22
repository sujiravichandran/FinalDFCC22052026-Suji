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
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
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
import com.teclever.datastore.entities.BuildConfiguration;
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
import com.teclever.dfcc.buildconfiguration.BuildConfigurationManagement;
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

public class DataPackReportGeneration {

	private static final String COPYRIGHT_TEXT = "Powered By Teclever Solutions Pvt Ltd, Bangalore.";

	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public Response generateDataPackReport(String sessionName)
			throws DocumentException, MalformedURLException, IOException {

		Response res = new Response();
		Document document = new Document(PageSize.A4);

		// File name with timestamp
		String fileName = "Datapack" + sessionName + "_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		// File path
		String filePath;
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\User\\Downloads\\" + fileName;
		} else {
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}

		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
		writer.setPageEvent(new HeaderFooter());
		document.open();
		res.setDownloadPath(filePath);

		// Add spacing before header
		for (int i = 0; i < 8; i++) {
			document.add(new Paragraph("\n"));
		}

		// Add BEL logo (Header)
		String imagePath;
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\User\\Downloads\\bel_logo_hindi.png";
		} else {
			imagePath = currentDirectory + File.separator + "Images" + File.separator + "bel_logo_hindi.png";
		}

		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(100);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Header line and title
		Font lineFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD, BaseColor.BLACK);

		String line = "____________________________________________________________";
		Paragraph linePara = new Paragraph(line, lineFont);
		linePara.setAlignment(Element.ALIGN_CENTER);
		document.add(linePara);

		String title = "Data Pack Report\nof\nDFCC High Level\nTesting";
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		document.newPage();

		// Canvas for drawing box and content
		PdfContentByte canvas = writer.getDirectContent();

		float x = document.leftMargin();
		float y = document.getPageSize().getHeight() - document.topMargin() - 60;
		float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
		float height = 70f;
		float cornerRadius = 20f;

		// Draw rounded rectangle
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius);
		canvas.stroke();

		// Add images inside the box
		String imagePath1 = !DFCCConstant.isJarBuild ? "C:\\Users\\User\\Downloads\\bel_logo_hindi.png"
				: currentDirectory + File.separator + "Images" + File.separator + "bel_logo_hindi.png";

		String imagePath3 = !DFCCConstant.isJarBuild ? "C:\\Users\\User\\Downloads\\TECLEVER_logo.png"
				: currentDirectory + File.separator + "Images" + File.separator + "TECLEVER_logo.png";

		Image img1 = Image.getInstance(imagePath1); // BEL logo
		Image img3 = Image.getInstance(imagePath3); // TECLEVER logo

		float imgWidth = (width - 20) / 4;
		float imgHeight = height - 30;

		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);

		// Center images vertically within the 70pt rectangle
		float imgY = y + (height - imgHeight) / 8;
		// Adjusted X positions for balance
		float imgX1 = x + 10; // BEL logo
		float imgX3 = x + width - imgWidth - 5; // TECLEVER logo

		img1.setAbsolutePosition(imgX1, imgY + 14);
		img3.setAbsolutePosition(imgX3, imgY + 14);

		document.add(img1);
		document.add(img3);

		// Add centered text between logos
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		String text = "DFCC DATA PACK REPORT";
		String unitTitle = sessionName;

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font), x + (width / 2),
				imgY + (imgHeight / 2) + 15, 0);

		Font fontMin = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n"));

		Font unitFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

		Paragraph unitPara = new Paragraph(unitTitle, unitFont);
		unitPara.setAlignment(Element.ALIGN_CENTER);
		unitPara.setSpacingBefore(10);
		unitPara.setSpacingAfter(5);
		document.add(unitPara);
		
		
		//Here Fetching The Brief Report...
	
		
		
		
		//Here Fetching The Detailed Report...
		
		

		document.close();
		res.setResponseMessage("Data Pack Report Downloaded Successfully::!");
		res.setResponseCode(1);
		return res;
	}

	static class HeaderFooter extends PdfPageEventHelper {

		private static final String COPYRIGHT_TEXT = "Powered by Teclever Solutions Pvt. Ltd., Bangalore.";

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();

			Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
			Phrase footer = new Phrase("Powered by Teclever Solutions Pvt. Ltd., Bangalore.", footerFont);

			Rectangle pageSize = document.getPageSize();

			// X,Y coordinates for bottom-right alignment
			float x = pageSize.getRight() - document.rightMargin(); // right edge within margin
			float y = pageSize.getBottom() + 20; // 20pt above page bottom

			// Right-align footer text
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
		}

	}

}
