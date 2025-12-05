package com.teclever.dfcc.reportgeneration;

import java.util.LinkedHashMap;
import java.util.Map;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFRenderer {

    private final Document document;
    private final PdfWriter writer;
    private final Map<String, PdfTemplate> tocMap = new LinkedHashMap<>();
    private final BaseFont baseFont;

    public PDFRenderer(Document doc, PdfWriter writer) throws Exception {
        this.document = doc;
        this.writer = writer;
        this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
    }

    // ================================================================
    //  TOC ENTRY CREATION
    // ================================================================
    public void registerTOCEntry(String key) {
        PdfTemplate template = writer.getDirectContent().createTemplate(50, 12);
        tocMap.put(key, template);
    }

    public Map<String, PdfTemplate> getTocMap() {
        return tocMap;
    }

    // ================================================================
    //  PAGE-BREAK SAFE PARAGRAPH ADDER
    // ================================================================
    public void addParagraphWithKeepTogether(Paragraph heading, Paragraph content) throws Exception {

        float requiredHeight = simulateHeight(heading, content);
        float remaining = writer.getVerticalPosition(true) - document.bottomMargin();

        if (requiredHeight > remaining) {
            document.newPage();
        }

        document.add(heading);
        document.add(content);
    }

    // ================================================================
    //  SIMPLE PARAGRAPH (NO HEADING)
    // ================================================================
    public void addParagraph(Paragraph p) throws Exception {
        float h = simulateHeight(p);
        float remaining = writer.getVerticalPosition(true) - document.bottomMargin();
        if (h > remaining) document.newPage();
        document.add(p);
    }

    // ================================================================
    //  HEIGHT SIMULATION
    // ================================================================
    private float simulateHeight(Element... elems) throws Exception {
        ColumnText ct = new ColumnText(writer.getDirectContent());
        ct.setSimpleColumn(
                document.left(),
                document.bottom(),
                document.right(),
                document.top()
        );

        for (Element e : elems) {
            ct.addElement(e);
        }

        ct.go(true);
        return document.top() - ct.getYLine();
    }

    // ================================================================
    //  HEADING HELPERS
    // ================================================================
    public Paragraph heading1(String text) {
        return new Paragraph(text, new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD));
    }

    public Paragraph heading2(String text) {
        return new Paragraph(text, new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD));
    }

    public Paragraph heading3(String text) {
        return new Paragraph(text, new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
    }

    public Paragraph body(String text) {
        Paragraph p = new Paragraph(text, new Font(Font.FontFamily.TIMES_ROMAN, 10));
        p.setSpacingBefore(4);
        p.setSpacingAfter(8);
        p.setIndentationLeft(20);
        return p;
    }

    // ================================================================
    //  FINAL TOC DRAWER
    // ================================================================
    public void drawTocEntry(String label, PdfTemplate template) {
        PdfContentByte cb = writer.getDirectContent();
        cb.addTemplate(template, document.right() - 50, writer.getVerticalPosition(true));
    }

    public void finalizeTOC() {
        for (Map.Entry<String, PdfTemplate> entry : tocMap.entrySet()) {
            PdfTemplate template = entry.getValue();
            template.beginText();
            template.setFontAndSize(baseFont, 10);
            template.showText(String.valueOf(writer.getPageNumber()));
            template.endText();
        }
    }
}
