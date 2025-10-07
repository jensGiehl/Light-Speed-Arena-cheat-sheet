package de.agiehl.bgg.lightspeedarena.pdf;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.UnitValue;
import de.agiehl.bgg.lightspeedarena.model.GameData;
import de.agiehl.bgg.lightspeedarena.model.GameItem;
import de.agiehl.bgg.lightspeedarena.model.LocalizedData;
import de.agiehl.bgg.lightspeedarena.model.LocalizedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerator.class);

    private static final int MAX_COLUMNS = 4;
    private static final int ROWS_PER_PAGE = 2;
    private static final int ITEMS_PER_PAGE = MAX_COLUMNS * ROWS_PER_PAGE;

    private final GameData gameData;
    private final LocalizedData messages;
    private final String language;
    private final ItemCellFactory cellFactory;

    public PdfGenerator(GameData gameData, LocalizedData messages, String language) {
        this.gameData = gameData;
        this.messages = messages;
        this.language = language;
        this.cellFactory = new ItemCellFactory(messages);
    }

    public void createPdf(String dest) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4.rotate());
        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        HeaderFooterHandler handler = new HeaderFooterHandler(messages);
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, handler);

        if (gameData.getComets() != null && !gameData.getComets().isEmpty()) {
            addItemsToDocument(document, gameData.getComets(), messages.getComets());
        }

        if (gameData.getFactions() != null && !gameData.getFactions().isEmpty()) {
            if (gameData.getComets() != null && !gameData.getComets().isEmpty() && gameData.getComets().size() % ITEMS_PER_PAGE != 0) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
            addItemsToDocument(document, gameData.getFactions(), messages.getFactions());
        }

        handler.writeTotal(pdf);

        document.close();
        logger.info("PDF created successfully for language '{}' at '{}'", language, dest);
    }

    private void addItemsToDocument(Document document, List<? extends GameItem> items, List<LocalizedItem> localizedItems) throws IOException {
        for (int i = 0; i < items.size(); i += ITEMS_PER_PAGE) {
            List<? extends GameItem> pageItems = items.subList(i, Math.min(i + ITEMS_PER_PAGE, items.size()));

            Table table = new Table(UnitValue.createPercentArray(MAX_COLUMNS)).useAllAvailableWidth();
            table.setMarginBottom(20);

            for (GameItem item : pageItems) {
                Cell cell = cellFactory.createItemCell(item, localizedItems);
                table.addCell(cell);
            }

            int remainingOnLastRow = pageItems.size() % MAX_COLUMNS;
            if (remainingOnLastRow > 0) {
                for (int j = 0; j < MAX_COLUMNS - remainingOnLastRow; j++) {
                    table.addCell(new Cell().setBorder(null));
                }
            }
            document.add(table);

            if (i + ITEMS_PER_PAGE < items.size()) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }
    }
}
