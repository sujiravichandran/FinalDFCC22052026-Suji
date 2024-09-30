package com.teclever.dfcc.reportgeneration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.teclever.datastore.dto.Response;
import com.teclever.dfcc.DFCCConstant;
import com.teclever.dfcc.datastore.dto.ResultExecutionDTO;
import com.teclever.dfcc.datastore.dto.ResultExecutionResponse;
import com.teclever.dfcc.resultmanagement.ResultExecutionManagement;
import com.teclever.dfcc.resultstore.dto.ResultDetailedDTO;
import com.teclever.dfcc.resultstore.dto.ResultDetailedResponse;

public class ReportGeneration {

    public Response generateBreifReportForCurrentExecution(String sessionId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
    System.out.println("ENTERED INTO Report generateBreifReportForCurrentExecution");
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        String fileName = "BriefReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
        String filePath ="";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\manik\\Downloads\\" + fileName;
		} else {
			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}
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

        // To Create Header

        // Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
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

        String title = "Brief Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        // Add images and text inside the rectangular box
        String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath1 = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		}
        
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\manik\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/TECLEVER_logo.png";
		}

        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);
        
        //To Fetch....
        ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);    
        List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
        resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
        System.out.println("PDF GENERATE LIST SIZE"+resultExecutionDTOList.size());
        
        Map<String,String> sessionDetailsMap =  resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

        Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     "+sessionDetailsMap.get("sessionName"), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(SessionNameDetails);

        Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     "+resultExecutionResponse.getStageName(), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(StageNameDetails);

        Paragraph userNameDetails = new Paragraph(" User Name           :" + "      "+sessionDetailsMap.get("userName"), headerFont);
        userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
        document.add(userNameDetails);

        Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No     :" + "     "+sessionDetailsMap.get("dfccPartNo"), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(dfccPartNoDetails);

       
       
        // Create table
        PdfPTable table = new PdfPTable(5); // 10 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table


        // Set Column widths
        float[] columnWidths = {1.5f, 2.5f, 0.5f, 2f ,1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "Rdf File Detials", "D*Count", "End At", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);

        
 

        // Add rows from list
		if (resultExecutionDTOList != null) {
			for (ResultExecutionDTO dto : resultExecutionDTOList) {
				Font greenFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN);
				Font redFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED);

				table.addCell(new Phrase(dto.getTestFileName()));
				table.addCell(new Phrase(dto.getRdfFilePath() + dto.getRdfFile()));
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
		}
		// Add table to document
		document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
    }
    
    public Response generateDetailedReportForCurrentExecution(String sessionId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        String fileName = "DetailedReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
     //   String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
          String filePath ="";
          
          if(!DFCCConstant.isJarBuild)
          {
        	  filePath = "C:\\Users\\manik\\Downloads\\"+fileName;
          }
          else
          {
        	  filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
          }
          

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

        // To Create Header

        // Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
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

        String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        // Add images and text inside the rectangular box
		String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath1 = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {

			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		}
        
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\manik\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		}
     //   String imagePath3 =  "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
        
        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);

        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

        Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     Session Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(SessionNameDetails);

        Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     Stage Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(StageNameDetails);

        Paragraph userNameDetails = new Paragraph(" User Name          :" + "      User Name", headerFont);
        userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
        document.add(userNameDetails);
        
        
        
        
        ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultDetailedResponse = resultExecutionManagement.getResultExecutionDetailedListForStages(sessionId);


        List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
        resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();
        
     /*   for (int i = 0; i < 100; i++) {
            ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();

            resultDetailedDTO.setExpectedValue(i + ".00");
            resultDetailedDTO.setFaultyChannel("CH" + i);
            resultDetailedDTO.setMeasuredValue(i + "80");
            resultDetailedDTO.setRdfName("rdf" + i);
            resultDetailedDTO.setStepName("" + i);
            resultDetailedDTO.setSignalName("SN_" + i);
            resultDetailedDTO.setTpfFileName("TPF_" + i);
            resultDetailedDTO.setTpgph("TPGH" + i);
            resultDetailedDTO.setUnit("UN-" + i);
            resultDetailedDTO.setTestName("TN-" + i);
            resultDetailedDTOList.add(resultDetailedDTO);
        }*/

        // Create table
        PdfPTable table = new PdfPTable(10); // 10 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table


        // Set Column widths
        float[] columnWidths = {1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit", "TPF File Name", "Signal Name", "Faulty Channel", "RDF Name"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);

        // Add rows from list
		if (resultDetailedDTOList != null) {
			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				table.addCell(new Phrase(dto.getTestName()));
				table.addCell(new Phrase(dto.getTpgph()));
				table.addCell(new Phrase(dto.getStepName()));
				table.addCell(new Phrase(dto.getExpectedValue()));
				table.addCell(new Phrase(dto.getMeasuredValue()));
				table.addCell(new Phrase(dto.getUnit()));
				table.addCell(new Phrase(dto.getTpfFileName()));
				table.addCell(new Phrase(dto.getSignalName()));
				table.addCell(new Phrase(dto.getFaultyChannel()));
				table.addCell(new Phrase(dto.getRdfName()));
			}
		}

        // Add table to document
        document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
    }
    
    //For Session Id
    public Response generateBreifReportForCurrentSession(String sessionId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        
        String fileName = "BriefReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
    
        String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\manik\\Downloads\\" + fileName;
		} else {
			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}
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

        // To Create Header

        // Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
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

        String title = "Breif Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        // Add images and text inside the rectangular box
		String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath1 = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		}
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

		String imagePath3 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath3 = "C:\\Users\\manik\\Downloads\\TECLEVER_logo.png";
		} else {
			imagePath3 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/TECLEVER_logo.png";
		}
        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);
        
        //To Fetch....
        ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages(sessionId);    
        List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
        resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
        
        Map<String,String> sessionDetailsMap =  resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

        Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     "+sessionDetailsMap.get("sessionName"), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(SessionNameDetails);

      /*  Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     "+resultExecutionResponse.getStageName(), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(StageNameDetails);*/

        Paragraph userNameDetails = new Paragraph(" User Name           :" + "      "+sessionDetailsMap.get("userName"), headerFont);
        userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
        document.add(userNameDetails);

        Paragraph dfccPartNoDetails = new Paragraph(" DFCC Part No     :" + "     "+sessionDetailsMap.get("dfccPartNo"), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(dfccPartNoDetails);

       
       
        // Create table
        PdfPTable table = new PdfPTable(5); 
        table.setWidthPercentage(100); 
        table.setSpacingBefore(10f); 
        table.setSpacingAfter(10f); 


        // Set Column widths
        float[] columnWidths = {1.5f, 2.5f, 0.5f, 2f ,1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "Rdf File Detials", "D*Count", "End At", "Stage Name" ,"Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);
        // Add rows from list
		if (resultExecutionDTOList != null) {
			for (ResultExecutionDTO dto : resultExecutionDTOList) {
				table.addCell(new Phrase(dto.getTestFileName()));
				table.addCell(new Phrase(dto.getRdfFilePath() + dto.getRdfFile()));
				table.addCell(new Phrase(dto.getDStarCount()));
				table.addCell(new Phrase(dto.getEndTime()));
				table.addCell(new Phrase(dto.getStageId()));
				table.addCell(new Phrase(dto.getStatus()));
			}
		}
        // Add table to document
        document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
    }
    
    public Response generateDetailedReportForCurrentSession(String sessionId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        String fileName = "DetailedReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
		String filePath = "";
		if (!DFCCConstant.isJarBuild) {
			filePath = "C:\\Users\\manik\\Downloads\\" + fileName;
		} else {
			filePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/" + fileName;
		}

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

        // To Create Header

        // Add The BEL Logo
		String imagePath = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
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

        String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        
        // Add images and text inside the rectangular box
		String imagePath1 = "";
		if (!DFCCConstant.isJarBuild) {
			imagePath1 = "C:\\Users\\manik\\Downloads\\BEL.jpeg";
		} else {
			imagePath1 = "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
		}
        
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";
		  String imagePath3 = "";
			if (!DFCCConstant.isJarBuild) {
				imagePath3 = "C:\\Users\\manik\\Downloads\\TECLEVER_logo.png";
			}else
			{
				   imagePath3 =  "/home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
				   
			}
        
        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);

        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

        Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     Session Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(SessionNameDetails);

        Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     Stage Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(StageNameDetails);

        Paragraph userNameDetails = new Paragraph(" User Name          :" + "      User Name", headerFont);
        userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
        document.add(userNameDetails);

        ResultDetailedResponse resultDetailedResponse = new ResultDetailedResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultDetailedResponse = resultExecutionManagement.getResultExecutionListDetailedListForSession(sessionId);


        List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
        resultDetailedDTOList = resultDetailedResponse.getResultDetailedList();
      /*  for (int i = 0; i < 100; i++) {
            ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();

            resultDetailedDTO.setExpectedValue(i + ".00");
            resultDetailedDTO.setFaultyChannel("CH" + i);
            resultDetailedDTO.setMeasuredValue(i + "80");
            resultDetailedDTO.setRdfName("rdf" + i);
            resultDetailedDTO.setStepName("" + i);
            resultDetailedDTO.setSignalName("SN_" + i);
            resultDetailedDTO.setTpfFileName("TPF_" + i);
            resultDetailedDTO.setTpgph("TPGH" + i);
            resultDetailedDTO.setUnit("UN-" + i);
            resultDetailedDTO.setTestName("TN-" + i);
            resultDetailedDTOList.add(resultDetailedDTO);
        }*/

        // Create table
        PdfPTable table = new PdfPTable(10); // 10 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table


        // Set Column widths
        float[] columnWidths = {1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit", "TPF File Name", "Signal Name", "Faulty Channel", "RDF Name"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);

        // Add rows from list
		if (resultDetailedDTOList != null) {
			for (ResultDetailedDTO dto : resultDetailedDTOList) {
				table.addCell(new Phrase(dto.getTestName()));
				table.addCell(new Phrase(dto.getTpgph()));
				table.addCell(new Phrase(dto.getStepName()));
				table.addCell(new Phrase(dto.getExpectedValue()));
				table.addCell(new Phrase(dto.getMeasuredValue()));
				table.addCell(new Phrase(dto.getUnit()));
				table.addCell(new Phrase(dto.getTpfFileName()));
				table.addCell(new Phrase(dto.getSignalName()));
				table.addCell(new Phrase(dto.getFaultyChannel()));
				table.addCell(new Phrase(dto.getRdfName()));
			}
		}
        // Add table to document
        document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
    }
    
    //For UutType
    public Response generateBreifReportForCurrentUutType(String uutTypeId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        String fileName = "BriefReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance()) + ".pdf";
        String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
//      String filePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/Reports/"+fileName;

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

        // To Create Header

        // Add The BEL Logo
        String imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
     //   String imagePath = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
       
        
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

        String title = "Brief Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        // Add images and text inside the rectangular box
        String imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
      //  String imagePath1 = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
        
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

        String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
       // String imagePath3 = "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/TECLEVER_logo.png";

        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);
        
        //To Fetch....
        ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForUnit(uutTypeId);    
        List<ResultExecutionDTO> resultExecutionDTOList = new ArrayList<ResultExecutionDTO>();
        resultExecutionDTOList = resultExecutionResponse.getResultDTOList();
        
       // Map<String,String> sessionDetailsMap =  resultExecutionManagement.getSessionDetailsBySessionId(sessionId);
        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

       
        Paragraph uutTypeDetails = new Paragraph(" UUT Name         :" + "     "+resultExecutionResponse.getStageName(), headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(uutTypeDetails);

 
       
       
        // Create table
        PdfPTable table = new PdfPTable(7); // 10 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table


        // Set Column widths
        float[] columnWidths = {1.5f, 2.5f, 0.5f, 2f ,1f,1f,1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "Rdf File Detials", "D*Count", "End At","Stage Name","Session Name", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);

        
 

        // Add rows from list
        for (ResultExecutionDTO dto : resultExecutionDTOList) {
            table.addCell(new Phrase(dto.getTestFileName()));
            table.addCell(new Phrase(dto.getRdfFilePath()+dto.getRdfFile()));
            table.addCell(new Phrase(dto.getDStarCount()));
            table.addCell(new Phrase(dto.getEndTime()));
            table.addCell(new Phrase(dto.getStageName()));
            table.addCell(new Phrase(dto.getSessionName()));
            table.addCell(new Phrase(dto.getStatus()));
        }

        // Add table to document
        document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
    }
    
    public Response generateDetailedReportForCurrentUutType(String sessionId)
            throws DocumentException, MalformedURLException, IOException {
    	//yyyyMMdd_HHmmss
    	//dd-MM-yyyy
        Response res = new Response();
        Document document = new Document(PageSize.A4);
        String fileName = "DetailedReport_" 
                + new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(Calendar.getInstance().getTime()) + ".pdf";
     //   String filePath = "C:\\Users\\Teclever\\Downloads\\" + fileName;
          String filePath = "/home/teclever_java_app/aitessreport/"+fileName;

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

        // To Create Header

        // Add The BEL Logo
        String imagePath = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
      //  String imagePath =  "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
        
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

        String title = "Detailed Report" + "\n" + "of" + "\n" + "DFCC High Level" + "\n" + "Testing";
        ;
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(titlePara);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));

        document.newPage();


        // Correct One

        PdfContentByte canvas = writer.getDirectContent();
        float x = document.leftMargin();
        float y = document.getPageSize().getHeight() - document.topMargin() - 100; // Adjust this value to position at
        // the top
        float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float height = 100f; // Height of the rectangular box

        // Draw the rectangular box
        canvas.setColorStroke(BaseColor.BLACK);
        canvas.rectangle(x, y, width, height);
        canvas.stroke();

        // Add images and text inside the rectangular box
        String imagePath1 = "C:\\Users\\Teclever\\Downloads\\BEL.jpeg";
      //  String imagePath1 =  "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
        
        
        // String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_LOGO1.png";

        String imagePath3 = "C:\\Users\\Teclever\\Downloads\\TECLEVER_logo.png";
     //   String imagePath3 =  "home/teclever_java_app/Desktop/DEPLOYMENT/Deployment/BEL.jpeg";
        
        Image img1 = Image.getInstance(imagePath1);
        Image img3 = Image.getInstance(imagePath3);

        float imgWidth = (width - 20) / 3; // Calculate the width for each image
        float imgHeight = height - 20; // Calculate the height for each image

        img1.scaleToFit(imgWidth, imgHeight + 10);
        img3.scaleToFit(imgWidth, imgHeight);

        float imgY = y + 10;
        float imgX1 = x + 10;
        float imgX3 = x + 2 * (imgWidth + 10); // Adjusted to skip the middle section

        img1.setAbsolutePosition(imgX1, imgY + 30);
        img3.setAbsolutePosition(imgX3, imgY + 10);

        document.add(img1);
        document.add(img3);

        // Add text in place of the second image
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
        Font headerFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);

        String text = "DFCC High Level Testing";
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(text, font),
                x + imgWidth + 10 + imgWidth / 2, imgY + imgHeight / 2, 0);

        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
        Paragraph SessionDetails = new Paragraph("Stage Details", headerFont1);
        SessionDetails.setAlignment(Element.ALIGN_CENTER); // Center align the heading
        document.add(SessionDetails);

        Paragraph SessionNameDetails = new Paragraph(" Session Name      :" + "     Session Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(SessionNameDetails);

        Paragraph StageNameDetails = new Paragraph(" Stage Name         :" + "     Stage Name", headerFont);
        SessionDetails.setAlignment(Element.ALIGN_RIGHT); // Center align the heading
        document.add(StageNameDetails);

        Paragraph userNameDetails = new Paragraph(" User Name          :" + "      User Name", headerFont);
        userNameDetails.setAlignment(Element.ALIGN_LEFT); // Center align the heading
        document.add(userNameDetails);

        ResultExecutionResponse resultExecutionResponse = new ResultExecutionResponse();
        ResultExecutionManagement resultExecutionManagement = new ResultExecutionManagement();
        resultExecutionResponse = resultExecutionManagement.getResultExecutionListBriefListForStages("");


        List<ResultDetailedDTO> resultDetailedDTOList = new ArrayList<ResultDetailedDTO>();
        for (int i = 0; i < 100; i++) {
            ResultDetailedDTO resultDetailedDTO = new ResultDetailedDTO();

            resultDetailedDTO.setExpectedValue(i + ".00");
            resultDetailedDTO.setFaultyChannel("CH" + i);
            resultDetailedDTO.setMeasuredValue(i + "80");
            resultDetailedDTO.setRdfName("rdf" + i);
            resultDetailedDTO.setStepName("" + i);
            resultDetailedDTO.setSignalName("SN_" + i);
            resultDetailedDTO.setTpfFileName("TPF_" + i);
            resultDetailedDTO.setTpgph("TPGH" + i);
            resultDetailedDTO.setUnit("UN-" + i);
            resultDetailedDTO.setTestName("TN-" + i);
            resultDetailedDTOList.add(resultDetailedDTO);
        }

        // Create table
        PdfPTable table = new PdfPTable(10); // 10 columns
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table


        // Set Column widths
        float[] columnWidths = {1f, 1f, 0.8f, 1.1f, 1.2f, 0.9f, 1f, 1f, 1f, 1f};
        table.setWidths(columnWidths);


        // Add table header
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"Test Name", "TPGPH", "Step Name", "Expected Value", "Measured Value", "Unit", "TPF File Name", "Signal Name", "Faulty Channel", "RDF Name"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Set the number of header rows
        table.setHeaderRows(1);

        // Add rows from list
        for (ResultDetailedDTO dto : resultDetailedDTOList) {
            table.addCell(new Phrase(dto.getTestName()));
            table.addCell(new Phrase(dto.getTpgph()));
            table.addCell(new Phrase(dto.getStepName()));
            table.addCell(new Phrase(dto.getExpectedValue()));
            table.addCell(new Phrase(dto.getMeasuredValue()));
            table.addCell(new Phrase(dto.getUnit()));
            table.addCell(new Phrase(dto.getTpfFileName()));
            table.addCell(new Phrase(dto.getSignalName()));
            table.addCell(new Phrase(dto.getFaultyChannel()));
            table.addCell(new Phrase(dto.getRdfName()));
        }

        // Add table to document
        document.add(table);


        // Close the document
        document.close();

        System.out.println("PDF saved to  PdfMarginsExample " + filePath);
        return res;
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
