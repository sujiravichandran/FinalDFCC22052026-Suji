package com.teclever.dfcc.buildconfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.BuildConfiguration;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.reportgeneration.ReportGeneration;

public class BuildConfigurationReport {

	private static final String COPYRIGHT_TEXT = "Powered By Teclever Solutions Pvt Ltd, Bangalore.";
	private String lastGeneratedFilePath;

	public String getLastGeneratedFilePath() {
	    return lastGeneratedFilePath;
	}
	static String currentDirectory = new File(
			ReportGeneration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	public Response generateBuildConfigurationReport(String uutType, String sNo, String date, String versionName, String unit, String lastUpDatedDate)
			throws DocumentException, MalformedURLException, IOException {

		Response res = new Response();
		Document document = new Document(PageSize.A4);

		// File name with timestamp
		String fileName = "Build_Configuration_" + unit + "_" + versionName
				+ new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";

		// File path
		String filePath;
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\sharn\\Desktop\\BelogoHindi\\" + fileName;
		} else {
			filePath = currentDirectory + File.separator + "Reports" + File.separator + fileName;
		}
		this.lastGeneratedFilePath = filePath;

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
			imagePath = "C:\\Users\\sharn\\Desktop\\BelogoHindi\\bel_logo_hindi.png";
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

		String title = "Build Configurations\nof\nDFCC High Level\nTesting";
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
		String imagePath1 = !DFCCConstant.isJarBuild ? "C:\\Users\\sharn\\Desktop\\BelogoHindi\\bel_logo_hindi.png"
				: currentDirectory + File.separator + "Images" + File.separator + "bel_logo_hindi.png";

		String imagePath3 = !DFCCConstant.isJarBuild ? "C:\\Users\\sharn\\Desktop\\BelogoHindi\\TECLEVER_logo.png"
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
		Font fontBoldUnderline = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD | Font.UNDERLINE, BaseColor.BLACK);
		String text = "QUALITY MANAGEMENT / EW&A";

		Font font12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE, BaseColor.BLACK);
		Font font12D = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLDITALIC, BaseColor.BLACK);
		String unitTitle = "";
		String bELpARTnO = "";
		String configuration = "";
		String cpudigi = "";
		String partNumberHeading = "";
		int configCount = 2;
		String chassisName = "";
		String assemblyName = "";
		String bot2 = "";
		String bot3 = "";

		if (unit.equalsIgnoreCase("uut1")) {
			unitTitle = "BUILD CONFIGURATION OF DFCC -  MK1";
			bELpARTnO = "BEL PART No: 116000039575";
			configuration = "CONFIGURATION MANAGEMENT DOCUMENT ISSUE LEVEL -10";
			cpudigi = "CPU Assembly";
			partNumberHeading = "Part Number / Key Sheet Issue Level";
			chassisName = "Chassis";
			bot2 = "DM, AI/EW&A:";
			bot3 = "OAQA Rep:";
		} else if (unit.equalsIgnoreCase("uut2")) {
			unitTitle = "BUILD CONFIGURATION OF DFCC - MK1A";
			bELpARTnO = "BEL PART No: 110400133385";
			configuration = "CONFIGURATION MANAGEMENT DOCUMENT ISSUE LEVEL -01";
			cpudigi = "Digital Card";
			partNumberHeading = "Part Number / GA Issue ";
			configCount = 3;
			chassisName = "Dip brazed Chassis";
			assemblyName = "Electrical Chassis Assy";
			bot2 = "Verified By";
			bot3 = "ORDAQA";
		} else {
			unitTitle = "BUILD CONFIGURATION OF DFCC -  MK2";
			bELpARTnO = "BEL PART No: 110002595472";
			configuration = "CONFIGURATION CONTROL(MAIN UNIT)/CCU ISSUE LEVEL-10";
			cpudigi = "CPU Assembly";
			partNumberHeading = "Part Number / GA Issue ";
			configCount = 3;
			chassisName = "Chassis";
			assemblyName = "Aircraft Tray Assembly";
			bot2 = "SE, AI/EW&A:";
			bot3 = "OAQA Rep:";
		}

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font), x + (width / 2),
				imgY + (imgHeight / 2) + 15, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(unitTitle, fontBoldUnderline), x + (width / 2),
				imgY + (imgHeight / 2) + 2, 0);

		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(bELpARTnO, font12D), x + (width / 2),
				imgY + (imgHeight / 2) - 12, 0);

		Font fontMin = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
		document.add(new Paragraph("\n" + "\n" + "\n"));

		Paragraph configurationDetails = new Paragraph(configuration, font);
		configurationDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
		document.add(configurationDetails);

		document.add(new Paragraph("\n"));

		PdfPTable infoTable = new PdfPTable(3);
		infoTable.setWidthPercentage(100);
		infoTable.setWidths(new float[] { 1f, 1f, 1f }); // Equal widths for left, center, right

		PdfPCell cell1 = new PdfPCell(new Phrase("Serial No : " + sNo, fontMin));
		PdfPCell cell2 = new PdfPCell(new Phrase("Version : " + versionName, fontMin));
		PdfPCell cell3 = new PdfPCell(new Phrase("Date : " + lastUpDatedDate, fontMin));

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

		// --- Build Configuration Table ---
		PdfPTable dataTable = new PdfPTable(4);
		dataTable.setWidthPercentage(100);
		dataTable.setSpacingBefore(10f);
		dataTable.setWidths(new float[] { 1f, 2f, 3.5f, 2f });

		Font tableheaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.WHITE);
		Font dataFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL, BaseColor.BLACK);

		// Header cells
		PdfPCell h1 = new PdfPCell(new Phrase("Channel", tableheaderFont));
		PdfPCell h2 = new PdfPCell(new Phrase("Description", tableheaderFont));
		PdfPCell h3 = new PdfPCell(new Phrase("Part Number / Key Sheet Issue Level", tableheaderFont));
		PdfPCell h4 = new PdfPCell(new Phrase("Serial Number", tableheaderFont));

		// Header background color
		BaseColor headerColor = new BaseColor(0, 102, 204); // BEL blue
		h1.setBackgroundColor(headerColor);
		h2.setBackgroundColor(headerColor);
		h3.setBackgroundColor(headerColor);
		h4.setBackgroundColor(headerColor);

		// Center align headers
		h1.setHorizontalAlignment(Element.ALIGN_CENTER);
		h2.setHorizontalAlignment(Element.ALIGN_CENTER);
		h3.setHorizontalAlignment(Element.ALIGN_CENTER);
		h4.setHorizontalAlignment(Element.ALIGN_CENTER);

		// Add headers to table
		dataTable.addCell(h1);
		dataTable.addCell(h2);
		dataTable.addCell(h3);
		dataTable.addCell(h4);

		BuildConfigurationManagement bcm = new BuildConfigurationManagement();
		BuildConfiguration buildConfig = new BuildConfiguration();
		buildConfig = bcm.getBuildConfiguration(uutType, sNo, versionName);

		// Example rows — replace with your actual data loop
		String[][] buildConfigurationdata = {
				// Channel -01
				{ "CH#1", "Analog-1-Left", buildConfig.getCh1Analog1LeftPartNumber(),
						buildConfig.getCh1Analog1LeftSerialNo() },
				{ "", "Analog-1-Right", buildConfig.getCh1Analog1RightPartNumber(),
						buildConfig.getCh1Analog1RightSerialNo() },
				{ "", "Analog-2", buildConfig.getCh1Analog2PartNumber(), buildConfig.getCh1Analog2SerialNo() },
				{ "", "Power Supply", buildConfig.getCh1poweSupplyPartNumber(),
						buildConfig.getCh1poweSupplySerialNo() },
				{ "", cpudigi, buildConfig.getCh1cpuAssemblyPartNumber(), buildConfig.getCh1cpuAssemblySerialNo() },
				{ "", "Flex Assembly CH-1", buildConfig.getCh1flexAssemblyPartNumber(),
						buildConfig.getCh1flexAssemblySerialNo() },
				//{ "   ", "    ", "    ", "   " },

				// Channel -02
				{ "CH#2", "Analog-1-Left", buildConfig.getCh2Analog1LeftPartNumber(),
						buildConfig.getCh2Analog1LeftSerialNo() },
				{ "", "Analog-1-Right", buildConfig.getCh2Analog1RightPartNumber(),
						buildConfig.getCh2Analog1RightSerialNo() },
				{ "", "Analog-2", buildConfig.getCh2Analog2PartNumber(), buildConfig.getCh2Analog2SerialNo() },
				{ "", "Power Supply", buildConfig.getCh2poweSupplyPartNumber(),
						buildConfig.getCh2poweSupplySerialNo() },
				{ "", cpudigi, buildConfig.getCh2cpuAssemblyPartNumber(), buildConfig.getCh2cpuAssemblySerialNo() },
				{ "", "Flex Assembly CH-2", buildConfig.getCh2flexAssemblyPartNumber(),
						buildConfig.getCh2flexAssemblySerialNo() },
				//{ "     ", "   ", "    ", "    " },

				// Channel -03
				{ "CH#3", "Analog-1-Left", buildConfig.getCh3Analog1LeftPartNumber(),
						buildConfig.getCh3Analog1LeftSerialNo() },
				{ "", "Analog-1-Right", buildConfig.getCh3Analog1RightPartNumber(),
						buildConfig.getCh3Analog1RightSerialNo() },
				{ "", "Analog-2", buildConfig.getCh3Analog2PartNumber(), buildConfig.getCh3Analog2SerialNo() },
				{ "", "Power Supply", buildConfig.getCh3poweSupplyPartNumber(),
						buildConfig.getCh3poweSupplySerialNo() },
				{ "", cpudigi, buildConfig.getCh3cpuAssemblyPartNumber(), buildConfig.getCh3cpuAssemblySerialNo() },
				{ "", "Flex Assembly CH-3", buildConfig.getCh3flexAssemblyPartNumber(),
						buildConfig.getCh3flexAssemblySerialNo() },
				//{ "    ", "   ", "    ", "   " },

				// Channel -04
				{ "CH#4", "Analog-1-Left", buildConfig.getCh4Analog1LeftPartNumber(),
						buildConfig.getCh4Analog1LeftSerialNo() },
				{ "", "Analog-1-Right", buildConfig.getCh4Analog1RightPartNumber(),
						buildConfig.getCh4Analog1RightSerialNo() },
				{ "", "Analog-2", buildConfig.getCh4Analog2PartNumber(), buildConfig.getCh4Analog2SerialNo() },
				{ "", "Power Supply", buildConfig.getCh4poweSupplyPartNumber(),
						buildConfig.getCh4poweSupplySerialNo() },
				{ "", cpudigi, buildConfig.getCh4cpuAssemblyPartNumber(), buildConfig.getCh4cpuAssemblySerialNo() },
				{ "", "Flex Assembly CH-4", buildConfig.getCh4flexAssemblyPartNumber(),
						buildConfig.getCh4flexAssemblySerialNo() },
				//{ "    ", "   ", "   ", "    " },

		};

//		// Add rows dynamically
//		for (String[] row : buildConfigurationdata) {
//			for (String value : row) {
//				PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
//				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//				cell.setPadding(5f);
//				dataTable.addCell(cell);
//			}
//		}
		
		
		
		
		int rowCount = 0;

		for (String[] row : buildConfigurationdata) {
		    for (String value : row) {
		        PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
		        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		        cell.setPadding(5f);
		        dataTable.addCell(cell);
		    }

		    rowCount++;

		  
			if (rowCount == 6 || rowCount == 12 || rowCount == 18 || rowCount == 24) {
				PdfPCell separator = new PdfPCell(new Phrase(""));
				separator.setColspan(4); // Span across all columns
				separator.setFixedHeight(15f); // Enough height to show all borders
				separator.setBorder(Rectangle.BOX); // All 4 sides
				separator.setBorderWidth(1f); // Uniform border thickness
				separator.setBorderColor(BaseColor.BLACK); // Black border
				separator.setBackgroundColor(new BaseColor(240, 240, 240)); // (Optional) light gray background for
																	// clarity
				dataTable.addCell(separator);
			}
		}


		if (configCount == 2) {

			String[][] buildConfigurationdata1 = {
					// Channel -01
					{ "", "Mother Board", buildConfig.getMotherboardPartNumber(),
							buildConfig.getMotherboardSerialNumber() },
					{ "", chassisName, buildConfig.getChasisPartNumber(), buildConfig.getChasisSerialNumber() }

			};

			for (String[] row : buildConfigurationdata1) {
				for (String value : row) {
					PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(5f);
					dataTable.addCell(cell);
				}
			}

		} else {

			String[][] buildConfigurationdata1 = {
					// Channel -01
					{ "", "Mother Board", buildConfig.getMotherboardPartNumber(),
							buildConfig.getMotherboardSerialNumber() },
					{ "", chassisName, buildConfig.getChasisPartNumber(), buildConfig.getChasisSerialNumber() },
					{ "", assemblyName, buildConfig.getAssemblyPartNumber(), buildConfig.getAssemblySerialNumber() },

			};

			for (String[] row : buildConfigurationdata1) {
				for (String value : row) {
					PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(5f);
					dataTable.addCell(cell);
				}
			}

		}

		// Add table to document
		document.add(dataTable);
		document.add(new Paragraph("\n" + "\n" + "\n"+"\n"));
	
		
		
		
		PdfPTable bottomContent = new PdfPTable(3);
		bottomContent.setWidthPercentage(100);
		bottomContent.setWidths(new float[] { 1f, 1f, 1f }); // Equal widths for left, center, right

		PdfPCell cell5 = new PdfPCell(new Phrase("Inspected By: ", fontMin));
		PdfPCell cell6 = new PdfPCell(new Phrase(bot2, fontMin));
		PdfPCell cell7 = new PdfPCell(new Phrase(bot3, fontMin));

		// Remove borders
		cell5.setBorder(Rectangle.NO_BORDER);
		cell6.setBorder(Rectangle.NO_BORDER);
		cell7.setBorder(Rectangle.NO_BORDER);

		// Alignment
		cell5.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setHorizontalAlignment(Element.ALIGN_RIGHT);

		// Add to table
		bottomContent.addCell(cell5);
		bottomContent.addCell(cell6);
		bottomContent.addCell(cell7);

		// Add to document
		document.add(bottomContent);
		
		

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
