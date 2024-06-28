package com.teclever.dfcc.reportgeneration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
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

public class ReportGeneration {

	public void generateBriefReport(Map<String, String> data1, Map<String, Map<String, String>> data2)
			throws DocumentException, MalformedURLException, IOException {

		Document document = new Document(PageSize.A4);
		String fileName = "BriefReport_"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;

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
		
		//To Create Header 
		
		//Add The BEL Logo
		String imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
		Image img = Image.getInstance(imagePath);
		img.scaleAbsolute(2, 1);
		img.scalePercent(100);
		img.setAlignment(Element.ALIGN_CENTER);
		document.add(img);
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		
		Font lineFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
		Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN,24, Font.BOLD, BaseColor.BLACK);
		
		
		String line = "____________________________________________________________";;
		Paragraph linePara = new Paragraph(line, lineFont);
		linePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(linePara);
	
		
		String title = "Detailed Report"  + "\n" + "of" +"\n"+"DFCC High Level"+"\n"+"Testing";;
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(titlePara);
		
		document.add(new Paragraph("\n"));
		document.add(new Paragraph("\n"));
		
		document.newPage();
		
		    PdfContentByte canvas = writer.getDirectContent();
	        float x = document.leftMargin();
	        float y = document.bottomMargin()+120;
	        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
	        float height = 60f; // height of the oval box

	        // Draw the oval rectangle
	        canvas.setColorStroke(BaseColor.BLACK);
	        canvas.setColorFill(BaseColor.WHITE);
	        canvas.roundRectangle(x+5, y, width-10f, height, 30);
	        canvas.fill();
	        canvas.stroke();

	        // Add images side by side
	        String imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
	        String imagePath2 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
	        String imagePath3 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";

	        Image img1 = Image.getInstance(imagePath1);
	        Image img2 = Image.getInstance(imagePath2);
	        Image img3 = Image.getInstance(imagePath3);

	        float imgWidth = (width - 20) / 3; // calculate the width for each image
	        float imgHeight = height - 20; // calculate the height for each image

	        img1.scaleToFit(imgWidth, imgHeight);
	        img2.scaleToFit(imgWidth, imgHeight);
	        img3.scaleToFit(imgWidth, imgHeight);

	        float imgY = y + 10;
	        float imgX1 = x + 10;
	        float imgX2 = imgX1 + imgWidth + 5;
	        float imgX3 = imgX2 + imgWidth + 5;

	        img1.setAbsolutePosition(imgX1, imgY);
	        img2.setAbsolutePosition(imgX2, imgY);
	        img3.setAbsolutePosition(imgX3, imgY);

	        canvas.addImage(img1);
	        canvas.addImage(img2);
	        canvas.addImage(img3);
	        
	       
		// Close the document
		document.close();

		System.out.println("PDF saved to  PdfMarginsExample " + filePath);

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
			template.rectangle(36, 36, PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() - 72);
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
