package com.teclever.dfcc.reportgeneration;




import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class l {

	private static String reportType = "";
	private static String partNo = "";
	private static String title = "";
	private static String preface = "";
	private static String reportTitle = "";
	private static String preparedBy = "";
	private static String verifiedBy = "";
	private static String preparedDate = "";
	private static String verifiedDate = "";

	private static List<TOCEntry> tocEntries = new ArrayList<>(); // List to store TOC entries
	static PdfWriter writer = null;
	public static void main(String[] args) {
		Document document = new Document(PageSize.A4);
		String pdfFilePath = "C:\\Users\\Teclever\\Downloads\\ReadExcelContentTOC.pdf";
		String excelPath = "C:\\Users\\Teclever\\Downloads\\REPORT_FIELDS.xlsx";
		try {
			Map<String, Map<String, String>> data = readExcelForHeadingSubHeadings(excelPath);
			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			document.open();
			HeaderFooter event = new HeaderFooter();
			writer.setPageEvent(event);
			document.open();

			// Create Summary
			createSummary(document, data);

			// Add Table of Contents
			addTableOfContents(document);

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		System.out.println("PDF saved to PdfMarginsExample " + pdfFilePath);
	}

	public static void addborder(PdfWriter writer) {
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate template = cb.createTemplate(PageSize.A4.getWidth(), PageSize.A4.getHeight());
		template.rectangle(36, 36, PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
		template.stroke();
		cb.addTemplate(template, 0, 0);
	}

	public static Map<String, Map<String, String>> readExcelForHeadingSubHeadings(String filePath) throws IOException {
		Map<String, Map<String, String>> data = new LinkedHashMap<>();
		FileInputStream fis = new FileInputStream(filePath);
		Workbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		String currentHeading = null;
		Map<String, String> subheadingMap = null;
		int rowCount = 0;
		for (Row row : sheet) {
			if (rowCount < 10) {
				Cell firstCell = row.getCell(0);
				Cell secondCell = row.getCell(1);

				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Report Type")) {
					reportType = secondCell.toString();
					System.out.println("Report Type: " + reportType);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Part No")) {
					partNo = secondCell.toString();
					System.out.println("Part No: " + partNo);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Title")) {
					title = secondCell.toString();
					System.out.println("Title: " + title);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Preface")) {
					preface = secondCell.toString();
					System.out.println("Preface: " + preface);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Report Title")) {
					reportTitle = secondCell.toString();
					System.out.println("Report Title: " + reportTitle);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Prepared Date")) {
					preparedDate = secondCell.toString();
					System.out.println("Prepared Date: " + preparedDate);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Verified Date")) {
					verifiedDate = secondCell.toString();
					System.out.println("Verified Date: " + verifiedDate);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Prepared By")) {
					preparedBy = secondCell.toString();
					System.out.println("Prepared By: " + preparedBy);
				}
				if (firstCell != null && !firstCell.toString().equals("") && firstCell.toString().equals("Verified By")) {
					verifiedBy = secondCell.toString();
					System.out.println("Verified By: " + verifiedBy);
				}
			} else {
				Cell firstCell = row.getCell(0);
				Cell secondCell = row.getCell(1);

				if (firstCell != null && !firstCell.toString().isEmpty()) {
					currentHeading = firstCell.toString();
					subheadingMap = new LinkedHashMap<>();
					data.put(currentHeading, subheadingMap);
				}

				if (secondCell != null && subheadingMap != null) {
					String subheading = secondCell.toString();
					StringBuilder content = new StringBuilder();
					for (int i = 2; i < row.getLastCellNum(); i++) {
						Cell cell = row.getCell(i);
						if (cell != null) {
							content.append(cell.toString()).append(" ");
						}
					}
					subheadingMap.put(subheading, content.toString().trim());
				}
			}
			rowCount++;
		}
		workbook.close();
		fis.close();
		return data;
	}

	public static void createSummary(Document document, Map<String, Map<String, String>> data)
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
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara);

		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));

		// Add Table
		BaseColor textColor = new BaseColor(22, 28, 99);
		BaseColor bcolor = new BaseColor(228, 239, 255);
		Font boldFont3 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
		PdfPTable table = new PdfPTable(3);
		float[] columnWidths = { 2, 2, 2 };
		table.setWidths(columnWidths);
		PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", boldFont3));
		cell1.setRowspan(1);
		cell1.setColspan(3);
		cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell1);

		PdfPCell cell2 = new PdfPCell(new Phrase("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "   BEL Testing –LCA-EWA", new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD)));
		cell2.setRowspan(1);
		cell2.setColspan(3);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_TOP);
		table.addCell(cell2);

		// Add more cells as needed...
		document.add(table);
		document.newPage();

		// Summary Content
		for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
			String heading = entry.getKey();
			Map<String, String> subheadingMap = entry.getValue();
			Font headingFont = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
			Font subheadingFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
			Font contentFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

			Paragraph headingParagraph = new Paragraph(heading, headingFont);
			headingParagraph.setAlignment(Element.ALIGN_LEFT);
			document.add(headingParagraph);

			// Record TOC entry
			tocEntries.add(new TOCEntry(heading, writer.getPageNumber()));

			document.add(new Paragraph("\n"));

			for (Map.Entry<String, String> subEntry : subheadingMap.entrySet()) {
				String subheading = subEntry.getKey();
				String content = subEntry.getValue();

				Paragraph subheadingParagraph = new Paragraph(subheading, subheadingFont);
				subheadingParagraph.setAlignment(Element.ALIGN_LEFT);
				document.add(subheadingParagraph);

				document.add(new Paragraph(content, contentFont));
				document.add(new Paragraph("\n"));
			}
		}
	}

	// Add Table of Contents Method
	public static void addTableOfContents(Document document) throws DocumentException {
		document.newPage();
		Paragraph tocTitle = new Paragraph("Table of Contents", new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK));
		tocTitle.setAlignment(Element.ALIGN_CENTER);
		document.add(tocTitle);

		for (TOCEntry entry : tocEntries) {
			Paragraph tocEntry = new Paragraph(entry.title + " ....... " + entry.page, new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK));
			tocEntry.setAlignment(Element.ALIGN_LEFT);
			document.add(tocEntry);
		}
	}

	// TOC Entry class
	static class TOCEntry {
		String title;
		int page;

		public TOCEntry(String title, int page) {
			this.title = title;
			this.page = page;
		}
	}

	static class HeaderFooter extends PdfPageEventHelper {
		Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase header = new Phrase("Header");
			Phrase footer = new Phrase("Footer");
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header, (document.right() - document.left()) / 2 + document.leftMargin(), document.top() + 10, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}
}
