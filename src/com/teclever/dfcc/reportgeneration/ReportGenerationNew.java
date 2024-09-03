package com.teclever.dfcc.reportgeneration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.SessionEntity;
import com.teclever.datastore.service.SessionService;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.datastore.sessionmanagement.SessionManagement;
import com.teclever.dfcc.reportgeneration.l.TOCEntry;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;

public class ReportGenerationNew extends PdfPageEventHelper {

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

	private static List<TOCEntry> tocEntries = new ArrayList<>(); // List to store TOC entries
	// table to store placeholder for all chapters and sections
	private final static Map<String, PdfTemplate> tocPlaceholder = new HashMap<String, PdfTemplate>();
//	private final static Map<String,Map<String,PdfTemplate>> tocSubPlaceHolder = new HashMap<String,Map<String,PdfTemplate>>();

	// store the chapters and sections with their title here.
	private final static Map<String, Integer> pageByTitle = new HashMap<>();
	static Font tocFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

	static Map<String, Map<String, Map<String, String>>> data1 = null;
	static PdfWriter writer;
	static Document document = new Document(PageSize.A4);

	static String excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";

	/*public static void main(String[] args) {
		// Create a document
		String pdfFilePath = "C:\\Users\\Teclever\\Downloads\\ReadExcelContent01nEW.pdf";
		String excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		try {

			Map<String, Map<String, Map<String, String>>> data = readExcelForHeadingSubHeadings(excelPath);
			data1 = data;
			writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			document.open();

			ReportGenerationNew.HeaderFooter event = new ReportGenerationNew.HeaderFooter();
			writer.setPageEvent(event);
			document.open();
			// createTOCByRead();
			createSummary1(document, data);
			// addTableOfContents(document);

			// HeaderFooter event1 = new HeaderFooter();
			// writer.setPageEvent(event1);
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Close the document
			document.close();
		}

		System.out.println("PDF saved to 1 PdfMarginsExample " + pdfFilePath);
		System.out.println();
	}*/

	// Generation Of Breif Report
	public Response generateBreifReportForCurrentExecution(String sessionId)
			throws DocumentException, MalformedURLException, IOException {
		// yyyyMMdd_HHmmss
		// dd-MM-yyyy

		Response res = new Response();
		Document document = new Document(PageSize.A4);
		String fileName = "BriefReport_"
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
//	      String filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/"+fileName;

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
		
		/*GetObjResponse getObjResponse = new GetObjResponse();
		SessionService sessionService = new SessionService();
		getObjResponse = sessionService.getSessionDetailBySessionStageId(sessionId);
		SessionEntity sessionEntity =(SessionEntity) getObjResponse.getObject();*/
		
		Map<String, String> sessionDetailsMap = resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
		// Add text in place of the second image
		Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);
		
		BaseColor tecBlueColor = new BaseColor(0,79,104,255); // RGB values (Red, Green, Blue)
		BaseColor belBlueColor = new BaseColor(1,75,174,255); // RGB values (Red, Green, Blue)
		BaseColor tecGreenColor = new BaseColor(99,137,52,255); // RGB values (Red, Green, Blue)
		BaseColor tecCementColor = new BaseColor(151,185,196);
     	Font highlightCementFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecCementColor);
    	Font highlighttecBlueColor = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC, tecBlueColor);
    	Font highlightbelBlueColor = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLDITALIC, belBlueColor);
       

		Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
		//document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
		Paragraph SessionDetails = new Paragraph("Stage Details", highlightbelBlueColor);
		SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(SessionDetails);
		SessionManagement sessionManagement = new SessionManagement();
		Map<String,String> stageIdName = sessionManagement.getAllStageIdName();
		Map<String,String> uutIdName = DFCCConstant.getUutIdNameMap();
		
		
		 
        
     	
     	
     	
     	
    	Paragraph uutNameDetails = new Paragraph(
				" Uut Type Name      " ,headerFont);
		uutNameDetails.add(new Chunk("    "+ sessionDetailsMap.get("uUtID"), highlightCementFont));
		uutNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(uutNameDetails);		
		
		Paragraph SessionNameDetails = new Paragraph(
				" Session Name       " , headerFont);
		SessionNameDetails.add(new Chunk("     "+ sessionDetailsMap.get("sessionName"),highlightCementFont));
		SessionNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(SessionNameDetails);

		Paragraph StageNameDetails = new Paragraph(
				" Stage Name         " , headerFont);
		StageNameDetails.add(new Chunk("     "+stageIdName.get(resultExecutionResponse.getStageName()),highlightCementFont));
		StageNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(StageNameDetails);

		Paragraph userNameDetails = new Paragraph(
				" User Name          " , headerFont);
		userNameDetails.add(new Chunk("     "+sessionDetailsMap.get("userName"),highlightCementFont));
		userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(userNameDetails);

		Paragraph dfccPartNoDetails = new Paragraph(
				" DFCC Part No       ", headerFont);
		dfccPartNoDetails.add(new Chunk("     "+sessionDetailsMap.get("dfccPartNo"),highlightCementFont));
		dfccPartNoDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
		document.add(dfccPartNoDetails);

		// Create table
		PdfPTable table = new PdfPTable(4); // 10 columns
		table.setWidthPercentage(100); // Width 100%
		table.setSpacingBefore(10f); // Space before table
		table.setSpacingAfter(10f); // Space after table

		// Set Column widths
		float[] columnWidths = { 0.5f, 2.5f, 2.5f, 0.8f };
		table.setWidths(columnWidths);

		// Add table header
		Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
		String[] headers = { "SL.NO", "Time Of Execution", "Executed File Name", "Result"};
		for (String header : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}

		// Set the number of header rows
		table.setHeaderRows(1);

		int sNo =1;
		// Add rows from list
		for (ResultExecutionDTO dto : resultExecutionDTOList) {
			Font greenFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
			Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

			table.addCell(new Phrase(sNo));
			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getDStarCount()));
			table.addCell(new Phrase(dto.getEndTime()));
			table.addCell(new Phrase(dto.getStatus()));
			/*
			 * if (!dto.getStatus().equals("SUCCESS")) { table.addCell(new
			 * Phrase(dto.getStatus(),greenFont)); } else { table.addCell(new
			 * Phrase(dto.getStatus(),redFont));
			 * 
			 * }
			 */
		
		}

		// Add table to document
		document.add(table);

		// Close the document
		document.close();

		System.out.println("PDF saved to Here2 PdfMarginsExample " + filePath);
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
						System.out.println("Heading For Place Holder--->" + "  " + tocPlaceHolderCount + " " + title);
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

					Paragraph subheadingParagraph = new Paragraph(
							"      " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + " "
									+ subheading.replaceAll("(h2)", ""),
							new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK));
					subheadingParagraph.setAlignment(Element.ALIGN_LEFT);
					document.add(subheadingParagraph);

					document.add(new VerticalPositionMark() {
						@Override
						public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx,
								final float ury, final float y) {
							final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
							String subheading = subTitle.replaceAll("(h2)", "");
							System.out.println("PlaceHolderSubHeading" + "     " + tocPlaceHolderCount + "."
									+ tocPlaceHolderCountSub + " " + subheading);
							tocPlaceholder.put(
									"     " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + " " + subheading,
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

							Paragraph h3Paragraph = new Paragraph(
									"         " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub + "."
											+ tocPlaceHolderCountH3 + " " + h3.replaceAll("(h3)", ""),
									new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC, BaseColor.BLACK));
							h3Paragraph.setAlignment(Element.ALIGN_LEFT);
							document.add(h3Paragraph);

							document.add(new VerticalPositionMark() {
								@Override
								public void draw(final PdfContentByte canvas, final float llx, final float lly,
										final float urx, final float ury, final float y) {
									final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
									String h3 = h3Title.replaceAll("(h3)", "");
									System.out.println("PlaceHolderH3" + "         " + tocPlaceHolderCount + "."
											+ tocPlaceHolderCountSub + "." + tocPlaceHolderCountH3 + " " + h3);
									tocPlaceholder.put("         " + tocPlaceHolderCount + "." + tocPlaceHolderCountSub
											+ "." + tocPlaceHolderCountH3 + " " + h3, createTemplate);
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

	private void addHeaderAndBorder(PdfWriter writer, Document document, int currentPage, int totalPages)
			throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();

		// Draw a rectangle border around the entire page
		PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(), PageSize.A4.getHeight());
		template.rectangle(36, 36, PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
		template.stroke();
		cb.addTemplate(template, 0, 0);

		// Create the main table with 6 columns
		PdfPTable table = new PdfPTable(6);
		float[] columnWidths = { 2, 1, 3, 1, 2, 1 }; // Adjust column widths as necessary
		table.setWidths(columnWidths);
		table.setTotalWidth(PageSize.A4.getWidth() - 72);
		table.setLockedWidth(true);

		// Add Image to the header table
		String imagePath = "src/main/java/Resources/Images/BELLOGO.png";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(40f, 40f);

		PdfPCell imageCell = new PdfPCell(img, true);
		imageCell.setRowspan(1);
		imageCell.setColspan(2);
		imageCell.setFixedHeight(40f);
		imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(imageCell);

		// Add cells to the header table
		Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font lightFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

		PdfPCell cell2 = new PdfPCell(new Phrase(reportType, boldFont));
		cell2.setRowspan(1);
		cell2.setColspan(2);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell2);

		String S = "PART NUMBER\n" + partNo;
		PdfPCell cell3 = new PdfPCell(new Phrase(S, lightFont));
		cell3.setRowspan(1);
		cell3.setColspan(2);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell(new Phrase(reportTitle, boldFont));
		cell4.setRowspan(3);
		cell4.setColspan(3);
		cell4.setFixedHeight(60f);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell4);

		PdfPCell cell5 = new PdfPCell(new Phrase("SHEET\n" + currentPage, lightFont));
		cell5.setRowspan(2);
		cell5.setColspan(1);
		cell5.setFixedHeight(30f);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell5);

		PdfPCell cell6 = new PdfPCell(new Phrase("PREPARED BY\n" + preparedBy, lightFont));
		cell6.setRowspan(2);
		cell6.setColspan(1);
		cell6.setFixedHeight(30f);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell6);

		PdfPCell cell7 = new PdfPCell(new Phrase("DATE\n" + preparedDate, lightFont));
		cell7.setRowspan(2);
		cell7.setColspan(1);
		cell7.setFixedHeight(30f);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell7);

		PdfPCell cell8 = new PdfPCell(new Phrase("TOTAL SHEETS\n" + totalPages, lightFont));
		cell8.setRowspan(3);
		cell8.setColspan(1);
		cell8.setFixedHeight(30f);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell8);

		PdfPCell cell9 = new PdfPCell(new Phrase("VERIFIED BY\n" + verifiedBy, lightFont));
		cell9.setRowspan(3);
		cell9.setColspan(1);
		cell9.setFixedHeight(30f);
		cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell9);

		PdfPCell lastColumnCell = new PdfPCell(new Phrase("DATE\n" + verifiedDate, lightFont));
		lastColumnCell.setFixedHeight(30f);
		lastColumnCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		lastColumnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		lastColumnCell.setRowspan(1);
		table.addCell(lastColumnCell);

		table.writeSelectedRows(0, -1, 36, 820, cb);

		// Footer watermark text
		cb.beginText();
		BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
		cb.setFontAndSize(baseFont, 8);

		cb.showTextAligned(Element.ALIGN_MIDDLE,
				"This Document is the Property of Bhart Electronics Ltd, Banglore India. Reproduction, Utilization",
				300, 30, 0);
		cb.showTextAligned(Element.ALIGN_MIDDLE,
				"or disclosure to third parties in any form whatsoever is not allowed without written consent of", 300,
				20, 0);
		cb.showTextAligned(Element.ALIGN_MIDDLE, "proprietors", 300, 10, 0);

		cb.endText();
		cb.restoreState();
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
					// System.out.println("Report Type" + reportType);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Part No")) {
					partNo = secondCell.toString();
					// System.out.println("Part No" + partNo);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Title")) {
					title = secondCell.toString();
					System.out.println("Title----->" + title);

				}

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Preface")) {
					preface = secondCell.toString();
					// System.out.println("Preface" + preface);

				}

				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Report Title")) {
					reportTitle = secondCell.toString();
					// System.out.println("Report Title" + reportTitle);

				}

				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Prepared Date")) {
					preparedDate = secondCell.toString();
					// System.out.println("Prepared Date" + preparedDate);

				}
				if (firstCell != null && !firstCell.toString().equals("")
						&& firstCell.toString().equals("Verified Date")) {
					verifiedDate = secondCell.toString();
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
			System.out.println("Summary headingPageNum---->" + headingPageNum);
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
						System.out.println("Summary subHeadingPageNum" + subHeadingPageNum);
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
								System.out.println("Summary h3PageNum" + h3PageNum);
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
								contentParagraph.setIndentationLeft(5); // Indent from the left margin
								contentParagraph.setIndentationRight(5); // Indent from the right margin
								// contentParagraph.setSpacingBefore(10); // Space before the paragraph
								// contentParagraph.setSpacingAfter(10); // Space after the paragraph
								document.add(contentParagraph);

								document.add(new Paragraph("            " + content,
										new Font(Font.FontFamily.TIMES_ROMAN, 10)));
							} else {

								// Now Added

								Paragraph h3Paragraph = new Paragraph(h3,
										new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL));
								h3Paragraph.setIndentationLeft(5); // Indent from the left margin
								h3Paragraph.setIndentationRight(5); // Indent from the right margin
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
						paragraph.setIndentationLeft(20); // Indent from the left margin
						paragraph.setIndentationRight(20); // Indent from the right margin
						paragraph.setSpacingBefore(10); // Space before the paragraph
						paragraph.setSpacingAfter(10); // Space after the paragraph
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
					System.out.println("----------------Preface---------");
					document.newPage();
				}
				document.add(new Paragraph("\n"));
			} else {
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

	public static void createSummary(Document document, Map<String, Map<String, Map<String, String>>> data)
			throws DocumentException, MalformedURLException, IOException {
		// Starting Page

		String imagePath = "src/main/java/Resources/Images/BellLogoRocket.png";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(50);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);

		Paragraph preface = new Paragraph();
		preface.setAlignment(Element.ALIGN_CENTER);

		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Paragraph titlePara = new Paragraph(title, titleFont);
		System.out.println(title + "Title Here");
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
			System.out.println("Table Heading" + heading);
			if (heading.contains("(TABLE)")) {

				Map<String, Map<String, String>> tablemap = new HashMap<String, Map<String, String>>();
				tablemap = data.get(heading);
				System.out.println("Table Map--->" + tablemap);

			} else {
				summaryPlaceHolderCountSub = 1;
				for (Map.Entry<String, Map<String, String>> subEntry : subheadings.entrySet()) {
					String subheading = subEntry.getKey();
					System.out.println("subheading ---->" + subheading);
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
								System.out.println("heading --->" + h3);
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
								System.out.println("content---->" + content);
								summaryPlaceHolderCountH3++;
							} else {
								System.out.println("content---->" + content);
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
							System.out.println("----------------Preface---------");
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
				addHeader(document, writer, currentPageNumber, totalPageCount);
				// addHeader(document, writer); // adding image
				// document.setMargins(document.leftMargin(), document.rightMargin(), 38,
				// document.bottomMargin());
				addborder(writer); // adding Margins
				currentPageNumber++;
			} catch (DocumentException | IOException e) {
				System.out.println(e.getLocalizedMessage());
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

		// Create Table Of Contents
		private void createTOC(final int count) throws DocumentException {
			// add a small introduction chapter the shouldn't be counted.
			final Chapter intro = new Chapter(new Paragraph("This is TOC ", boldFont), 0);
			intro.setNumberDepth(0);
			document.add(intro);

			for (int i = 1; i < count + 1; i++) {
				// Write "Chapter i"
				final String title = "Chapter " + i;
				final Chunk chunk = new Chunk(title).setLocalGoto(title);
				document.add(new Paragraph(chunk));

				// Add a placeholder for the page reference
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

		private static void createTOCByRead(final int count) throws DocumentException {
		}

		// NewAdd Header

		// This method will be called to add the header
		private void addHeader(Document document, PdfWriter writer, int currentPage, int totalPages)
				throws DocumentException, IOException {
			// Create the main table with 3 columns
			PdfPTable table = new PdfPTable(6);
			float[] columnWidths = { 2, 1, 3, 1, 2, 1 }; // Adjust column widths as necessary
			table.setWidths(columnWidths);
			// table.setWidthPercentage(100);
			// table.setSpacingAfter(0);
			// table.setLockedWidth(true);

			// \src\main\java\Resources\Images
			String imagePath = "src/Resources/Images/BELLOGO.png";
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

			// Add cells to the first row (three columns)
			/*
			 * PdfPCell cell1 = new PdfPCell(new Phrase("BELLL", boldFont));
			 * cell1.setRowspan(1); cell1.setColspan(2); cell1.setFixedHeight(40f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell1);
			 */

			// Add Cells
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

			// Add cells to the second row (first two columns)
			PdfPCell cell4 = new PdfPCell(new Phrase(reportTitle));
			cell4.setRowspan(3);
			cell4.setColspan(3);
			cell4.setFixedHeight(60f);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			// Cell 5
			PdfPCell cell5 = new PdfPCell(new Phrase("SHEET" + "\n" + currentPage));
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

			PdfPCell cell8 = new PdfPCell(new Phrase("TOTAL SHEETS" + "\n" + totalPages));
			cell8.setRowspan(3);
			cell8.setColspan(1);
			cell8.setFixedHeight(30f);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
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

			// Set other properties and add the main table to the document
			table.setSpacingAfter(10);
			table.setWidthPercentage(100);
			document.add(table);

			PdfContentByte cb = writer.getDirectContent();

			// Add normal header content
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

		private void addHeader(PdfWriter writer) throws DocumentException, IOException {
			PdfPTable table = new PdfPTable(6);
			float[] columnWidths = { 2, 1, 3, 1, 2, 1 }; // Adjust column widths as necessary
			table.setWidths(columnWidths);
			table.setTotalWidth(523); // Adjust the total width

			String imagePath = "src/main/java/Resources/Images/BELLOGO.png";
			Image img = Image.getInstance(imagePath);
			img.scaleAbsolute(40f, 40f); // Adjust image size

			PdfPCell imageCell = new PdfPCell(img, true);
			imageCell.setRowspan(1);
			imageCell.setColspan(2);
			imageCell.setFixedHeight(40f);
			imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			imageCell.setBorder(Rectangle.NO_BORDER); // No border for image cell
			table.addCell(imageCell);

			PdfPCell cell2 = new PdfPCell(new Phrase(reportType, boldFont));
			cell2.setRowspan(1);
			cell2.setColspan(2);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell2);

			String S = "PART NUMBER" + "\n" + partNo;
			PdfPCell cell3 = new PdfPCell(new Paragraph(S, LightFont));
			cell3.setRowspan(1);
			cell3.setColspan(2);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase(reportTitle));
			cell4.setRowspan(3);
			cell4.setColspan(3);
			cell4.setFixedHeight(60f);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell4);

			PdfPCell cell5 = new PdfPCell(new Phrase("SHEET" + "\n" + currentPageNumber));
			cell5.setRowspan(2);
			cell5.setColspan(1);
			cell5.setFixedHeight(30f);
			cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell5.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell5);

			PdfPCell cell6 = new PdfPCell(new Phrase("PREPARED BY" + "\n" + preparedBy));
			cell6.setRowspan(2);
			cell6.setColspan(1);
			cell6.setFixedHeight(30f);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell6.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Phrase("DATE" + "\n" + preparedDate));
			cell7.setRowspan(2);
			cell7.setColspan(1);
			cell7.setFixedHeight(30f);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell7);

			PdfPCell cell8 = new PdfPCell(new Phrase("TOTAL SHEETS" + "\n" + totalPageCount));
			cell8.setRowspan(3);
			cell8.setColspan(1);
			cell8.setFixedHeight(30f);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell8);

			PdfPCell cell9 = new PdfPCell(new Phrase("VERIFIED BY" + "\n" + verifiedBy));
			cell9.setRowspan(3);
			cell9.setColspan(1);
			cell9.setFixedHeight(30f);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(cell9);

			PdfPCell lastColumnCell = new PdfPCell(new Phrase("DATE" + "\n" + verifiedDate));
			lastColumnCell.setFixedHeight(30f);
			lastColumnCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			lastColumnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			lastColumnCell.setRowspan(1);
			lastColumnCell.setBorder(Rectangle.NO_BORDER); // No border for this cell
			table.addCell(lastColumnCell);

			table.writeSelectedRows(0, -1, 36, 806, writer.getDirectContent()); // Position the table
		}

		private void addHeader(Document document, PdfWriter writer)
				throws DocumentException, MalformedURLException, IOException {
			// Create the main table with 3 columns
			PdfPTable table = new PdfPTable(6);
			float[] columnWidths = { 2, 1, 3, 1, 2, 1 }; // Adjust column widths as necessary
			table.setWidths(columnWidths);
			// \src\main\java\Resources\Images
			String imagePath = "src/Resources/Images/BELLOGO.png";
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

			// Add cells to the first row (three columns)
			/*
			 * PdfPCell cell1 = new PdfPCell(new Phrase("BELLL", boldFont));
			 * cell1.setRowspan(1); cell1.setColspan(2); cell1.setFixedHeight(40f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cell1);
			 */

			// Add Cells
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

			// Add cells to the second row (first two columns)
			PdfPCell cell4 = new PdfPCell(new Phrase(reportTitle));
			cell4.setRowspan(3);
			cell4.setColspan(3);
			cell4.setFixedHeight(60f);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			// Cell 5
			PdfPCell cell5 = new PdfPCell(new Phrase("SHEET" + "\n" + "-"));
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

			PdfPCell cell8 = new PdfPCell(new Phrase("TOTAL SHEETS" + "\n" + "-"));
			cell8.setRowspan(3);
			cell8.setColspan(1);
			cell8.setFixedHeight(30f);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
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
			// PdfPCell lastColumnCell = new PdfPCell();
			PdfPCell lastColumnCell = new PdfPCell(new Phrase("DATE" + "\n" + verifiedDate));
			// lastColumnCell.addElement(nestedTable);
			lastColumnCell.setFixedHeight(30f);
			lastColumnCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			lastColumnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			lastColumnCell.setRowspan(1); // Spanning 1 row in the main table
			table.addCell(lastColumnCell);

			// Set other properties and add the main table to the document
			table.setSpacingAfter(10);
			table.setWidthPercentage(100);
			document.add(table);

			// Add a vertically rotated paragraph
			/*
			 * PdfPTable verticalTable = new PdfPTable(1); PdfPCell verticalCell = new
			 * PdfPCell(new Phrase("This is a vertically aligned paragraph"));
			 * verticalCell.setRotation(90); // Rotate the text 90 degrees
			 * verticalCell.setFixedHeight(document.getPageSize().getHeight() -
			 * document.topMargin() - document.bottomMargin());
			 * verticalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * verticalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * verticalTable.addCell(verticalCell);
			 * verticalTable.setTotalWidth(document.leftMargin() - 10); // Adjust the width
			 * as necessary verticalTable.writeSelectedRows(0, -1, 10,
			 * document.getPageSize().getHeight() - document.topMargin(),
			 * writer.getDirectContent());
			 * 
			 */
			PdfContentByte cb = writer.getDirectContent();

			// Add normal header content
			Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			/*
			 * ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new
			 * Phrase("Header Title", font), (document.right() - document.left()) / 2 +
			 * document.leftMargin(), document.top() + 10, 0);
			 */
			// Add a vertically rotated paragraph
			cb.saveState();
			cb.beginText();
			BaseFont baseFont = null;
			try {
				baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}

			/*
			 * cb.saveState(); cb.setLineWidth(1f); // Set the line width float lineX1 =
			 * document.left() - 25; float lineY1 = document.bottom(); float lineX2 =
			 * document.left() - 25; float lineY2 = document.top(); cb.moveTo(lineX1,
			 * lineY1); cb.lineTo(lineX2, lineY2); cb.stroke(); cb.restoreState();
			 */

			cb.setFontAndSize(baseFont, 8);

			cb.showTextAligned(Element.ALIGN_MIDDLE,
					"              This Doccument is the Property of Bhart Electronics Ltd, Banglore India. Reproduction, Utilization ",
					document.left() - 28, (document.top() + document.bottom()) / 2, 90);

			cb.showTextAligned(Element.ALIGN_MIDDLE,
					"              or disclosure to third parties in any form whatsover its not allowed with out written constent of",
					document.left() - 18, (document.top() + document.bottom()) / 2, 90);

			cb.showTextAligned(Element.ALIGN_MIDDLE, "              propertiers ", document.left() - 8,
					(document.top() + document.bottom()) / 2, 90);

			/*
			 * cb.showTextAligned(Element.ALIGN_MIDDLE,
			 * "      This Doccument is the Property of Bhart Electronics Ltd, Banglore India. Reproduction, Utilization or disclosure to third parties in any form "
			 * , document.left() - 27, (document.top() + document.bottom()) / 2, 90);
			 * cb.showTextAligned(Element.ALIGN_MIDDLE,
			 * "      whatsover its not allowed with out written constent of propertiers ",
			 * document.left() - 20, (document.top() + document.bottom()) / 2, 90);
			 */
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

		/*
		 * public void onEndPage(PdfWriter writer, Document document) { PdfPTable header
		 * = new PdfPTable(3);
		 * 
		 * float[] columnWidths = new float[] { 25f, 45f, 20f, 30f }; Font boldFont =
		 * new Font(FontFamily.HELVETICA, 17, Font.BOLD); try { // Set width of the
		 * table to be equal to the width of the document header.setWidths(new int[] {
		 * 1, 2, 1 }); header.setTotalWidth( document.getPageSize().getWidth() -
		 * document.leftMargin() - document.rightMargin()); header.setLockedWidth(true);
		 * header.getDefaultCell().setFixedHeight(40);
		 * header.getDefaultCell().setBorder(Rectangle.BOTTOM);
		 * 
		 * // Add content to the header // header.addCell(new
		 * Phrase("Header Column 1")); // header.addCell(new Phrase("Header Column 2"));
		 * // header.addCell(new Phrase("Header Column 3"));
		 * 
		 * // Set the position of the table header.writeSelectedRows(0, -1,
		 * document.leftMargin(), document.top() + document.topMargin(),
		 * writer.getDirectContent()); } catch (DocumentException de) { throw new
		 * ExceptionConverter(de); } }
		 */
	}

}
