package com.teclever.dfcc.advanceddataanalysis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.itextpdf.text.BaseColor;
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
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.RDF1553BCode;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.reportgeneration.ReportGeneration;

public class Report1553B {
	
	


	private static final String COPYRIGHT_TEXT = "Powered By Teclever Solutions Pvt Ltd, Bangalore.";

	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public Response generate1553Report(String sessionId, String stageId,String serialNo,String stageName,String sessionName)
			throws DocumentException, MalformedURLException, IOException {

		Response res = new Response();
		Document document = new Document(PageSize.A4.rotate());

		// File name with timestamp
		String fileName = "1553B_Result"
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
		for (int i = 0; i < 3; i++) {
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
		
		document.add(new Paragraph("\n\n\n"));

		String title = "1553B INTERFACE - TIME TAG\n REGISTER VERFICATION";
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Channel -01
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
		float imgX3 = x + width - imgWidth + 60; // TECLEVER logo

		img1.setAbsolutePosition(imgX1, imgY + 10);
		img3.setAbsolutePosition(imgX3, imgY + 14);

		document.add(img1);
		document.add(img3);

		// Add centered text between logos
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font fontMin = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

		Font fontBoldUnderline = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD | Font.UNDERLINE, BaseColor.BLACK);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
				new Phrase("1553B INTERFACE - TIME TAG REGISTER VERIFICATION", fontBoldUnderline), x + (width / 2) + 40,
				imgY + (imgHeight / 2) + 15, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("DFCC CHANNEL 1", font), x + (width / 2),
				imgY + (imgHeight / 2) - 5, 0);

		document.add(new Paragraph("\n" + "\n" + "\n" + "\n"));

		PdfPTable infoTable = new PdfPTable(3);
		infoTable.setWidthPercentage(100);
		infoTable.setWidths(new float[] { 1f, 1f, 1f }); // Equal widths for left, center, right

		PdfPCell cell1 = new PdfPCell(new Phrase("Serial No : " + serialNo, fontMin));
		PdfPCell cell2 = new PdfPCell(new Phrase("Session : " + sessionName, fontMin));
		PdfPCell cell3 = new PdfPCell(new Phrase("Stage Name : " + stageName, fontMin));

		// Remove borders
		cell1.setBorder(Rectangle.NO_BORDER);
		cell2.setBorder(Rectangle.NO_BORDER);
		cell3.setBorder(Rectangle.NO_BORDER);

		// Alignment
		cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);

		// Add to table
		infoTable.addCell(cell1);
		infoTable.addCell(cell2);
		infoTable.addCell(cell3);

		// Add to document
		document.add(infoTable);

		// Create a table with 9 columns
		PdfPTable table = new PdfPTable(9);
		table.setWidthPercentage(100);
		table.setSpacingBefore(20f);
		table.setSpacingAfter(20f);

		// Column widths
		float[] columnWidths = { 1f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
		table.setWidths(columnWidths);

		// Fonts
		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.WHITE);
		Font cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK);
		BaseColor headerColor = new BaseColor(0, 102, 204); // BEL blue

		// ---------------- HEADER ROW ----------------
		String[] headers = { "STEP", "OPERATION", "TTR1_I\n003C C03C\n(Decimal)", "TTR1_II\n003C C07C\n(Decimal)",
				"TTR1_III\n003C C0BC\n(Decimal)", "TTR1_IV\n003C C0FC\n(Decimal)", "TTR1_V\n003C C13C\n(Decimal)",
				"TTR1_VI\n003C C17C\n(Decimal)", "TTR1_VII\n003C C1BC\n(Decimal)" };

		// Add header cells
		for (int i = 0; i < headers.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headers[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			// Merge the first two header cells vertically (header + 1st data row)
			if (i == 0 || i == 1) {
				headerCell.setRowspan(2);
			}

			table.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		// NOTE: We skip first two columns (STEP and OPERATION) because they are merged
		// vertically
		String[] firstDataRow = { "TEST:6216", "TEST:6232", "TEST:6248", "TEST:6264", "TEST:6280", "TEST:6296",
				"TEST:6312" };
		for (String val : firstDataRow) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			table.addCell(cell);
		}

		// ---------------- SECOND DATA ROW ----------------
		DataAnalysis1553_BManagement dataAnalysis1553_BManagement = new DataAnalysis1553_BManagement();
		List<RDF1553BCode> lstRdf1553 = dataAnalysis1553_BManagement.get1553BValuesForChannel(sessionId, stageId,
				"ch1");
		RDF1553BCode rDF1553BCode = new RDF1553BCode();
		if (lstRdf1553.size() > 0) {
			rDF1553BCode = lstRdf1553.get(0);
		}
		String[] secondDataRow = { "01", "Record TTR Value", rDF1553BCode.getValueCh_1(), rDF1553BCode.getValueCh_2(),
				rDF1553BCode.getValueCh_3(), rDF1553BCode.getValueCh_4(), rDF1553BCode.getValueCh_5(),
				rDF1553BCode.getValueCh_6(), rDF1553BCode.getValueCh_7() };
		for (String val : secondDataRow) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			table.addCell(cell);
		}

		// Add the table to the document
		document.add(table);

		// Create a table with 9 columns
		PdfPTable tableCh12 = new PdfPTable(8);
		tableCh12.setWidthPercentage(100);
		tableCh12.setSpacingBefore(20f);
		tableCh12.setSpacingAfter(20f);

		// Column widths
		float[] columnWidths2 = { 0.6f, 1f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f };
		tableCh12.setWidths(columnWidths2);

		// Fonts

		// ---------------- HEADER ROW ----------------
		String[] headersCh1Tl1 = { " ", " ", "TTR1_II-TTR1_I", "TTR1_III-TTR1_II", "TTR1_VI-TTR1_III", "TTR1_V-TTR1_IV",
				"TTR1_VI-TTR1_V", "TTR1_VII-TTR1_VI" };

		// Add header cells
		for (int i = 0; i < headersCh1Tl1.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headersCh1Tl1[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			tableCh12.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		String[] firstDataRowCh1Tbl2 = { " 02", " Compute Difference", rDF1553BCode.getDiff_1and2(),
				rDF1553BCode.getDiff_2and3(), rDF1553BCode.getDiff_3and4(), rDF1553BCode.getDiff_4and5(),
				rDF1553BCode.getDiff_5and6(), rDF1553BCode.getDiff_6and7() };

		for (String val : firstDataRowCh1Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val.substring(1), cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh12.addCell(cell);
		}

		// -----------SECOND DATA ROW-------------------

		String[] secondDataRowCh1Tbl2 = { "03", "DECLARE :\n (P:PASS; F:Fail) \n P: Diff = 312/313 \n F :OTHER WISE",
				rDF1553BCode.getRes_1and2(), rDF1553BCode.getRes_2and3(), rDF1553BCode.getRes_2and3(),
				rDF1553BCode.getRes_3and4(), rDF1553BCode.getRes_4and5(), rDF1553BCode.getRes_5and6(),
				rDF1553BCode.getRes_6and7() };

		for (String val : secondDataRowCh1Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh12.addCell(cell);
		}

		document.add(tableCh12);

		document.add(new Paragraph(
				"NOTE:          Valid Range for TTR Value is 0-65535(Decimal). If the Computed Difference at STEP '02' is Negative,a Value of 65536 has to be added to account for the Wrap-arounf Feature.\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TB NO.:127  PASS/FAIL\n\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TEST CARRIED OUT BY :"));

		Font signaturefont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
		String signature = "                                                                                                                                                                                             (Signature With Date and Time)";
		Paragraph configurationDetails = new Paragraph(signature, signaturefont);
		// Center align the heading
		document.add(configurationDetails);

		// Channel 02
		document.newPage();
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius);
		canvas.stroke();
		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);
		img1.setAbsolutePosition(imgX1, imgY + 10);
		img3.setAbsolutePosition(imgX3, imgY + 14);
		document.add(img1);
		document.add(img3);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
				new Phrase("1553B INTERFACE - TIME TAG REGISTER VERIFICATION", fontBoldUnderline), x + (width / 2) + 40,
				imgY + (imgHeight / 2) + 15, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("DFCC CHANNEL 2", font), x + (width / 2),
				imgY + (imgHeight / 2) - 5, 0);

		document.add(new Paragraph("\n" + "\n" + "\n" + "\n"));
		document.add(infoTable);

		// Create a table with 9 columns
		PdfPTable tableCh2 = new PdfPTable(9);
		tableCh2.setWidthPercentage(100);
		tableCh2.setSpacingBefore(20f);
		tableCh2.setSpacingAfter(20f);

		// Column widths
		float[] columnWidthsCh2 = { 1f, 1.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
		tableCh2.setWidths(columnWidthsCh2);

		// Add header cells
		for (int i = 0; i < headers.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headers[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			// Merge the first two header cells vertically (header + 1st data row)
			if (i == 0 || i == 1) {
				headerCell.setRowspan(2);
			}

			tableCh2.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		// NOTE: We skip first two columns (STEP and OPERATION) because they are merged
		// vertically
		String[] firstDataRowCh2 = { "TEST:6348", "TEST:6364", "TEST:6380", "TEST:6396", "TEST:6412", "TEST:64286",
				"TEST:6444" };
		for (String val : firstDataRowCh2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh2.addCell(cell);
		}

		// ---------------- SECOND DATA ROW ----------------
		List<RDF1553BCode> lstRdf1553Ch2 = dataAnalysis1553_BManagement.get1553BValuesForChannel(sessionId, stageId,
				"ch2");
		RDF1553BCode rDF1553BCodeCh2 = new RDF1553BCode();
		if (lstRdf1553Ch2.size() > 0) {
			rDF1553BCodeCh2 = lstRdf1553Ch2.get(0);
		}
		String[] secondDataRowCh2 = { "01", "Record TTR Value", rDF1553BCodeCh2.getValueCh_1(),
				rDF1553BCodeCh2.getValueCh_2(), rDF1553BCodeCh2.getValueCh_3(), rDF1553BCodeCh2.getValueCh_4(),
				rDF1553BCodeCh2.getValueCh_5(), rDF1553BCodeCh2.getValueCh_6(), rDF1553BCodeCh2.getValueCh_7() };

		for (String val : secondDataRowCh2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh2.addCell(cell);
		}

		// Add the table to the document
		document.add(tableCh2);

		// Create a table with 9 columns
		PdfPTable tableCh22 = new PdfPTable(8);
		tableCh22.setWidthPercentage(100);
		tableCh22.setSpacingBefore(20f);
		tableCh22.setSpacingAfter(20f);

		// Column widths
		float[] columnWidths22 = { 0.6f, 1f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f, 0.8f };
		tableCh22.setWidths(columnWidths22);

		// Fonts

		// Add header cells
		for (int i = 0; i < headersCh1Tl1.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headersCh1Tl1[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			tableCh22.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		String[] firstDataRowCh2Tbl2 = { " 02", " Compute Difference", rDF1553BCodeCh2.getDiff_1and2(),
				rDF1553BCodeCh2.getDiff_2and3(), rDF1553BCodeCh2.getDiff_3and4(), rDF1553BCodeCh2.getDiff_4and5(),
				rDF1553BCodeCh2.getDiff_5and6(), rDF1553BCodeCh2.getDiff_6and7() };

		for (String val : firstDataRowCh2Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val.substring(1), cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh22.addCell(cell);
		}

		// -----------SECOND DATA ROW-------------------

		String[] secondDataRowCh2Tbl2 = { "03", "DECLARE :\n (P:PASS; F:Fail) \n P: Diff = 312/313 \n F :OTHER WISE",
				rDF1553BCodeCh2.getRes_1and2(), rDF1553BCodeCh2.getRes_2and3(), rDF1553BCodeCh2.getRes_2and3(),
				rDF1553BCodeCh2.getRes_3and4(), rDF1553BCodeCh2.getRes_4and5(), rDF1553BCodeCh2.getRes_5and6(),
				rDF1553BCodeCh2.getRes_6and7() };

		for (String val : secondDataRowCh2Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh22.addCell(cell);
		}

		document.add(tableCh22);

		document.add(new Paragraph(
				"NOTE:          Valid Range for TTR Value is 0-65535(Decimal). If the Computed Difference at STEP '02' is Negative,a Value of 65536 has to be added to account for the Wrap-arounf Feature.\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TB NO.:127  PASS/FAIL\n\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TEST CARRIED OUT BY :"));
		document.add(configurationDetails);

		// CHANNEL 3
		document.newPage();
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius);
		canvas.stroke();
		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);
		img1.setAbsolutePosition(imgX1, imgY + 10);
		img3.setAbsolutePosition(imgX3, imgY + 14);
		document.add(img1);
		document.add(img3);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
				new Phrase("1553B INTERFACE - TIME TAG REGISTER VERIFICATION", fontBoldUnderline), x + (width / 2) + 40,
				imgY + (imgHeight / 2) + 15, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("DFCC CHANNEL 3", font), x + (width / 2),
				imgY + (imgHeight / 2) - 5, 0);

		document.add(new Paragraph("\n" + "\n" + "\n" + "\n"));
		document.add(infoTable);

		// Create a table with 9 columns
		PdfPTable tableCh3 = new PdfPTable(9);
		tableCh3.setWidthPercentage(100);
		tableCh3.setSpacingBefore(20f);
		tableCh3.setSpacingAfter(20f);

		// Column widths
		tableCh3.setWidths(columnWidthsCh2);

		// Add header cells
		for (int i = 0; i < headers.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headers[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			// Merge the first two header cells vertically (header + 1st data row)
			if (i == 0 || i == 1) {
				headerCell.setRowspan(2);
			}

			tableCh3.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		// NOTE: We skip first two columns (STEP and OPERATION) because they are merged
		// vertically
		String[] firstDataRowCh3 = { "TEST:6480", "TEST:6496", "TEST:6512", "TEST:6528", "TEST:6544", "TEST:6560",
				"TEST:6576" };

		for (String val : firstDataRowCh3) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh3.addCell(cell);
		}

		// ---------------- SECOND DATA ROW ----------------
		List<RDF1553BCode> lstRdf1553Ch3 = dataAnalysis1553_BManagement.get1553BValuesForChannel(sessionId, stageId,
				"ch3");
		RDF1553BCode rDF1553BCodeCh3 = new RDF1553BCode();
		if (lstRdf1553Ch3.size() > 0) {
			rDF1553BCodeCh3 = lstRdf1553Ch3.get(0);
		}

		String[] secondDataRowCh3 = { "01", "Record TTR Value", rDF1553BCodeCh3.getValueCh_1(),
				rDF1553BCodeCh3.getValueCh_2(), rDF1553BCodeCh3.getValueCh_3(), rDF1553BCodeCh3.getValueCh_4(),
				rDF1553BCodeCh3.getValueCh_5(), rDF1553BCodeCh3.getValueCh_6(), rDF1553BCodeCh3.getValueCh_7() };

		for (String val : secondDataRowCh3) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh3.addCell(cell);
		}

		// Add the table to the document
		document.add(tableCh3);

		// Create a table with 9 columns
		PdfPTable tableCh32 = new PdfPTable(8);
		tableCh32.setWidthPercentage(100);
		tableCh32.setSpacingBefore(20f);
		tableCh32.setSpacingAfter(20f);

		// Column widths
		tableCh32.setWidths(columnWidths22);
		for (int i = 0; i < headersCh1Tl1.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headersCh1Tl1[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			tableCh32.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		String[] firstDataRowCh3Tbl2 = { " 02", " Compute Difference", rDF1553BCodeCh3.getDiff_1and2(),
				rDF1553BCodeCh3.getDiff_2and3(), rDF1553BCodeCh3.getDiff_3and4(), rDF1553BCodeCh3.getDiff_4and5(),
				rDF1553BCodeCh3.getDiff_5and6(), rDF1553BCodeCh3.getDiff_6and7() };

		for (String val : firstDataRowCh3Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val.substring(1), cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh32.addCell(cell);
		}

		// -----------SECOND DATA ROW-------------------

		String[] secondDataRowCh3Tbl2 = { "03", "DECLARE :\n (P:PASS; F:Fail) \n P: Diff = 312/313 \n F :OTHER WISE",
				rDF1553BCodeCh3.getRes_1and2(), rDF1553BCodeCh3.getRes_2and3(), rDF1553BCodeCh3.getRes_2and3(),
				rDF1553BCodeCh3.getRes_3and4(), rDF1553BCodeCh3.getRes_4and5(), rDF1553BCodeCh3.getRes_5and6(),
				rDF1553BCodeCh3.getRes_6and7() };

		for (String val : secondDataRowCh3Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh32.addCell(cell);
		}

		document.add(tableCh32);

		document.add(new Paragraph(
				"NOTE:          Valid Range for TTR Value is 0-65535(Decimal). If the Computed Difference at STEP '02' is Negative,a Value of 65536 has to be added to account for the Wrap-arounf Feature.\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TB NO.:127  PASS/FAIL\n\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TEST CARRIED OUT BY :"));
		document.add(configurationDetails);

//Channel 4 

		document.newPage();
		canvas.setColorStroke(BaseColor.BLACK);
		canvas.roundRectangle(x, y, width, height, cornerRadius);
		canvas.stroke();
		img1.scaleToFit(imgWidth, imgHeight + 10);
		img3.scaleToFit(imgWidth, imgHeight);
		img1.setAbsolutePosition(imgX1, imgY + 10);
		img3.setAbsolutePosition(imgX3, imgY + 14);
		document.add(img1);
		document.add(img3);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
				new Phrase("1553B INTERFACE - TIME TAG REGISTER VERIFICATION", fontBoldUnderline), x + (width / 2) + 40,
				imgY + (imgHeight / 2) + 15, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("DFCC CHANNEL 4", font), x + (width / 2),
				imgY + (imgHeight / 2) - 5, 0);

		document.add(new Paragraph("\n" + "\n" + "\n" + "\n"));
		document.add(infoTable);

		// Create a table with 9 columns
		PdfPTable tableCh4 = new PdfPTable(9);
		tableCh4.setWidthPercentage(100);
		tableCh4.setSpacingBefore(20f);
		tableCh4.setSpacingAfter(20f);

		// Column widths
		tableCh4.setWidths(columnWidthsCh2);

		// Add header cells
		for (int i = 0; i < headers.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headers[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			// Merge the first two header cells vertically (header + 1st data row)
			if (i == 0 || i == 1) {
				headerCell.setRowspan(2);
			}

			tableCh4.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		// NOTE: We skip first two columns (STEP and OPERATION) because they are merged
		// vertically
		String[] firstDataRowCh4 = { "TEST:6612", "TEST:6628", "TEST:6644", "TEST:6660", "TEST:6676", "TEST:6692",
				"TEST:6708" };

		for (String val : firstDataRowCh4) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh4.addCell(cell);
		}

		// ---------------- SECOND DATA ROW ----------------
		List<RDF1553BCode> lstRdf1553Ch4 = dataAnalysis1553_BManagement.get1553BValuesForChannel(sessionId, stageId,
				"ch4");
		RDF1553BCode rDF1553BCodeCh4 = new RDF1553BCode();
		if (lstRdf1553Ch4.size() > 0) {
			rDF1553BCodeCh4 = lstRdf1553Ch4.get(0);
		}

		String[] secondDataRowCh4 = { "01", "Record TTR Value", rDF1553BCodeCh4.getValueCh_1(),
				rDF1553BCodeCh4.getValueCh_2(), rDF1553BCodeCh4.getValueCh_3(), rDF1553BCodeCh4.getValueCh_4(),
				rDF1553BCodeCh4.getValueCh_5(), rDF1553BCodeCh4.getValueCh_6(), rDF1553BCodeCh4.getValueCh_7() };

		for (String val : secondDataRowCh4) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh4.addCell(cell);
		}

		// Add the table to the document
		document.add(tableCh4);

		// Create a table with 9 columns
		PdfPTable tableCh42 = new PdfPTable(8);
		tableCh42.setWidthPercentage(100);
		tableCh42.setSpacingBefore(20f);
		tableCh42.setSpacingAfter(20f);

		// Column widths
		tableCh42.setWidths(columnWidths22);

		for (int i = 0; i < headersCh1Tl1.length; i++) {
			PdfPCell headerCell = new PdfPCell(new Phrase(headersCh1Tl1[i], headerFont));
			headerCell.setBackgroundColor(headerColor);
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);

			tableCh42.addCell(headerCell);
		}

		// ---------------- FIRST DATA ROW ----------------
		String[] firstDataRowCh4Tbl2 = { " 02", " Compute Difference", rDF1553BCodeCh4.getDiff_1and2(),
				rDF1553BCodeCh4.getDiff_2and3(), rDF1553BCodeCh4.getDiff_3and4(), rDF1553BCodeCh4.getDiff_4and5(),
				rDF1553BCodeCh4.getDiff_5and6(), rDF1553BCodeCh4.getDiff_6and7() };

		for (String val : firstDataRowCh4Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val.substring(1), cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh42.addCell(cell);
		}

		// -----------SECOND DATA ROW-------------------

		String[] secondDataRowCh4Tbl2 = { "03", "DECLARE :\n (P:PASS; F:Fail) \n P: Diff = 312/313 \n F :OTHER WISE",
				rDF1553BCodeCh4.getRes_1and2(), rDF1553BCodeCh4.getRes_2and3(), rDF1553BCodeCh4.getRes_2and3(),
				rDF1553BCodeCh4.getRes_3and4(), rDF1553BCodeCh4.getRes_4and5(), rDF1553BCodeCh4.getRes_5and6(),
				rDF1553BCodeCh4.getRes_6and7() };

		for (String val : secondDataRowCh4Tbl2) {
			PdfPCell cell = new PdfPCell(new Phrase(val, cellFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(4f);
			tableCh42.addCell(cell);
		}

		document.add(tableCh42);

		document.add(new Paragraph(
				"NOTE:          Valid Range for TTR Value is 0-65535(Decimal). If the Computed Difference at STEP '02' is Negative,a Value of 65536 has to be added to account for the Wrap-arounf Feature.\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TB NO.:127  PASS/FAIL\n\n"));
		document.add(new Paragraph(
				"                                                                                                                                                                          TEST CARRIED OUT BY :"));
		document.add(configurationDetails);
		
		String sessionNameUnit = sessionName.split("_")[0];
		
		if(sessionNameUnit.equals("DFCC-MK1"))
		{
			
			
			
			
		}

		document.close();
		res.setResponseMessage("Build Configuration Report Downloaded Successfully::!");
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
	        float x = pageSize.getRight() - document.rightMargin();  // right edge within margin
	        float y = pageSize.getBottom() + 20;                     // 20pt above page bottom

	        // Right-align footer text
	        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, x, y, 0);
	    }

	}



}
