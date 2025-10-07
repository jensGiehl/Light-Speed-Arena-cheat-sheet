package de.agiehl.bgg.lightspeedarena.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import de.agiehl.bgg.lightspeedarena.model.LocalizedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class HeaderFooterHandler implements IEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(HeaderFooterHandler.class);

    private final LocalizedData messages;
    private final Map<Integer, PdfFormXObject> totalPagePlaceholders = new HashMap<>();

    public HeaderFooterHandler(LocalizedData messages) {
        this.messages = messages;
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getType().equals(PdfDocumentEvent.START_PAGE)) {
            addHeaderAndFooterPlaceholders((PdfDocumentEvent) event);
        }
    }

    private void addHeaderAndFooterPlaceholders(PdfDocumentEvent docEvent) {
        try {
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNum = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            float yFooter = pageSize.getBottom() + 10;
            float yHeader = pageSize.getTop() - 15;
            float xCenter = pageSize.getWidth() / 2;

            pdfCanvas.beginText()
                    .setFontAndSize(font, 12)
                    .moveText(xCenter - (messages.getHeader().getTitle().length() * 3), yHeader)
                    .showText(messages.getHeader().getTitle())
                    .endText();

            pdfCanvas.beginText()
                    .setFontAndSize(font, 8)
                    .moveText(pageSize.getLeft() + 20, yFooter)
                    .showText(messages.getFooter().getCopyright())
                    .endText();

            String pageOfText = MessageFormat.format(messages.getFooter().getPage(), pageNum, " ");
            pageOfText = pageOfText.substring(0, pageOfText.length() - 1);
            float pageTextWidth = font.getWidth(pageOfText, 8);
            float totalPlaceholderWidth = font.getWidth("000", 8);

            float xPageNum = pageSize.getRight() - 20 - totalPlaceholderWidth;
            pdfCanvas.beginText().setFontAndSize(font, 8).moveText(xPageNum - pageTextWidth, yFooter).showText(pageOfText).endText();

            PdfFormXObject placeholder = new PdfFormXObject(new Rectangle(0, 0, totalPlaceholderWidth, 10));
            pdfCanvas.addXObjectAt(placeholder, xPageNum, yFooter - 1);
            totalPagePlaceholders.put(pageNum, placeholder);

            pdfCanvas.release();
        } catch (IOException e) {
            logger.error("Error adding header/footer placeholder", e);
        }
    }

    public void writeTotal(PdfDocument pdf) {
        int totalPages = pdf.getNumberOfPages();
        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            for (PdfFormXObject xObject : totalPagePlaceholders.values()) {
                PdfCanvas canvas = new PdfCanvas(xObject, pdf);
                canvas.beginText().setFontAndSize(font, 8).moveText(0, 1).showText(String.valueOf(totalPages)).endText();
            }
        } catch (IOException e) {
            logger.error("Error writing total page count", e);
        }
    }
}
