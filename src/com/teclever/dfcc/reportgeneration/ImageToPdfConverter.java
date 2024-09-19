package com.teclever.dfcc.reportgeneration;
import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.Response;

public class ImageToPdfConverter {
	public String pdfConvertor(String imagePath) {
		Response res = new Response();

		File file = new File(imagePath);
		String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
		String filePath = file.getParent()+File.separatorChar;
		String outputPdf = filePath+fileNameWithoutExtension + ".pdf";
		try {
			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(outputPdf));
			document.open();
			Image img = Image.getInstance(imagePath);
			img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
			img.setAlignment(Image.ALIGN_CENTER);
			document.add(img);
			// document.newPage();
			document.close();
			System.out.println("PDF with images created successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputPdf;
	}
}
