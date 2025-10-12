package de.agiehl.bgg.lightspeedarena.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import de.agiehl.bgg.lightspeedarena.Main;
import de.agiehl.bgg.lightspeedarena.model.GameItem;
import de.agiehl.bgg.lightspeedarena.model.LocalizedData;
import de.agiehl.bgg.lightspeedarena.model.LocalizedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ItemCellFactory {

    private static final Logger logger = LoggerFactory.getLogger(ItemCellFactory.class);

    private static final float IMAGE_CONTAINER_HEIGHT = 130f;
    private static final float CELL_PADDING = 5f;

    private final LocalizedData messages;

    public ItemCellFactory(LocalizedData messages) {
        this.messages = messages;
    }

    public Cell createItemCell(GameItem item, List<LocalizedItem> localizedItems) throws IOException {
        Cell cell = new Cell().setBorder(null).setPadding(CELL_PADDING);

        Optional<LocalizedItem> localizedItemOpt = localizedItems.stream()
                .filter(li -> li.getId().equalsIgnoreCase(item.getId()))
                .findFirst();

        if (localizedItemOpt.isEmpty()) {
            logger.warn("No localized text found for item ID: {}", item.getId());
            cell.add(new Paragraph("Missing text for " + item.getId()));
            return cell;
        }
        LocalizedItem localizedItem = localizedItemOpt.get();

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

        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont expansionFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

        Paragraph name = new Paragraph(localizedItem.getName())
                .setFont(boldFont)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5)
                .setMarginBottom(0)
                .setMultipliedLeading(1.0f);
        cell.add(name);

        if (item.getExpansionKey() != null && !item.getExpansionKey().isBlank()) {
            String expansionKey = item.getExpansionKey();
            String expansionName = messages.getExpansions().get(expansionKey);
            if (expansionName != null) {
                Text star = new Text("★ ")
                        .setFontColor(ColorConstants.BLUE);
                Text expansionText = new Text(expansionName)
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
        }

        int fontsize = Integer.parseInt(messages.getFontsize());
        Paragraph description = new Paragraph(localizedItem.getDescription())
                .setFont(normalFont)
                .setFontSize(fontsize)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(1.1f);
        cell.add(description);

        return cell;
    }
}
