package de.agiehl.bgg.lightspeedarena;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class PdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerator.class);

    // Layout constants
    private static final int MAX_COLUMNS = 4;
    private static final int ROWS_PER_PAGE = 2;
    private static final int ITEMS_PER_PAGE = MAX_COLUMNS * ROWS_PER_PAGE; // 8 items per page
    private static final float IMAGE_CONTAINER_HEIGHT = 130f; // Fixed height for the image container
    private static final float CELL_PADDING = 5f;

    // Controls whether section titles ("Comets", "Factions") are shown
    private final boolean showSectionTitles = false;

    private final GameData gameData;
    private final ResourceBundle messages;
    private final String language;

    public PdfGenerator(GameData gameData, ResourceBundle messages, String language) {
        this.gameData = gameData;
        this.messages = messages;
        this.language = language;
    }

    public void createPdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4.rotate()); // Force landscape
        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20); // Smaller margins for more space

        // Add Header and Footer
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterHandler(messages));

        // Add Comets
        if (gameData.getComets() != null && !gameData.getComets().isEmpty()) {
            if (showSectionTitles) {
                addSectionTitle(document, messages.getString("title.comets"));
            }
            addItemsToDocument(document, gameData.getComets());
        }

        // Add Factions
        if (gameData.getFactions() != null && !gameData.getFactions().isEmpty()) {
            // Add a page break if comets were added and didn't end exactly on a page boundary
            if (gameData.getComets() != null && !gameData.getComets().isEmpty() && gameData.getComets().size() % ITEMS_PER_PAGE != 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
            if (showSectionTitles) {
                addSectionTitle(document, messages.getString("title.factions"));
            }
            addItemsToDocument(document, gameData.getFactions());
        }

        document.close();
        logger.info("PDF created successfully for language '{}' at '{}'", language, dest);
    }

    private void addSectionTitle(Document document, String title) throws IOException {
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Paragraph titleParagraph = new Paragraph(title)
                .setFont(font)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10);
        document.add(titleParagraph);
    }

    private void addItemsToDocument(Document document, List<? extends GameItem> items) throws IOException {
        for (int i = 0; i < items.size(); i += ITEMS_PER_PAGE) {
            // Get the sublist for the current page
            List<? extends GameItem> pageItems = items.subList(i, Math.min(i + ITEMS_PER_PAGE, items.size()));

            Table table = new Table(UnitValue.createPercentArray(MAX_COLUMNS)).useAllAvailableWidth();
            table.setMarginBottom(20);

            for (GameItem item : pageItems) {
                Cell cell = createItemCell(item);
                table.addCell(cell);
            }

            // Add empty cells if the last row on the page is not full
            int remainingOnLastRow = pageItems.size() % MAX_COLUMNS;
            if (remainingOnLastRow > 0) {
                for (int j = 0; j < MAX_COLUMNS - remainingOnLastRow; j++) {
                    table.addCell(new Cell().setBorder(null));
                }
            }
            document.add(table);

            // Add a page break if there are more items to process
            if (i + ITEMS_PER_PAGE < items.size()) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }
    }

    private Cell createItemCell(GameItem item) throws IOException {
        Cell cell = new Cell().setBorder(null).setPadding(CELL_PADDING);

        // --- Image Container ---
        Div imageContainer = new Div()
                .setHeight(IMAGE_CONTAINER_HEIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        try (InputStream imageStream = Main.class.getResourceAsStream(item.getImage())) {
            if (imageStream != null) {
                byte[] imageBytes = imageStream.readAllBytes();
                Image img = new Image(ImageDataFactory.create(imageBytes));
                img.setAutoScale(true);
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                imageContainer.add(img);
            } else {
                logger.warn("Image not found: {}", item.getImage());
                imageContainer.add(new Paragraph("Image not found: " + item.getImage().substring(item.getImage().lastIndexOf('/') + 1)));
            }
        }
        cell.add(imageContainer);

        // --- Text Content ---
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont expansionFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        Paragraph name = new Paragraph(messages.getString(item.getNameKey()))
                .setFont(boldFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5)
                .setMarginBottom(0)
                .setMultipliedLeading(1.0f);
        cell.add(name);

        if (item.getExpansionKey() != null && !item.getExpansionKey().isBlank()) {
            Text star = new Text("★ ")
                    .setFontColor(ColorConstants.BLUE);
            Text expansionText = new Text(messages.getString(item.getExpansionKey()))
                    .setFont(expansionFont)
                    .setFontSize(9)
                    .setFontColor(ColorConstants.BLUE);
            Paragraph expansion = new Paragraph()
                    .add(star)
                    .add(expansionText)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5)
                    .setMultipliedLeading(1.0f);
            cell.add(expansion);
        }

        int fontsize = Integer.parseInt(messages.getString("fontsize"));
        Paragraph description = new Paragraph(messages.getString(item.getDescriptionKey()))
                .setFont(normalFont)
                .setFontSize(fontsize)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(1.1f);
        cell.add(description);

        return cell;
    }

    private static class HeaderFooterHandler implements IEventHandler {
        private final ResourceBundle messages;

        public HeaderFooterHandler(ResourceBundle messages) {
            this.messages = messages;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNum = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize(); // Corrected type to Rectangle
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);

            try {
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                float yFooter = pageSize.getBottom() + 20;
                float yHeader = pageSize.getTop() - 20;
                float xCenter = pageSize.getWidth() / 2;

                // We calculate total pages here to ensure it's up-to-date
                int totalPages = pdf.getNumberOfPages();

                pdfCanvas.beginText()
                        .setFontAndSize(font, 12)
                        .moveText(xCenter - (messages.getString("header.title").length() * 3), yHeader) // Simple centering logic
                        .showText(messages.getString("header.title"))
                        .endText();

                String pageText = MessageFormat.format(messages.getString("footer.page"), pageNum, totalPages);
                pdfCanvas.beginText()
                        .setFontAndSize(font, 8)
                        .moveText(pageSize.getLeft() + 36, yFooter)
                        .showText(messages.getString("footer.copyright"))
                        .endText()
                        .beginText()
                        .setFontAndSize(font, 8)
                        .moveText(pageSize.getRight() - 36 - 60, yFooter)
                        .showText(pageText)
                        .endText();

                pdfCanvas.release();
            } catch (IOException e) {
                logger.error("Error adding header/footer", e);
            }
        }
    }
}
