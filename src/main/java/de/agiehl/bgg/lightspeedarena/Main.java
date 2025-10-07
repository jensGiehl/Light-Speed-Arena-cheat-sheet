package de.agiehl.bgg.lightspeedarena;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.agiehl.bgg.lightspeedarena.model.GameData;
import de.agiehl.bgg.lightspeedarena.model.LocalizedData;
import de.agiehl.bgg.lightspeedarena.pdf.PdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        List<Locale> supportedLocales = List.of(
                Locale.GERMAN,
                Locale.ENGLISH,
                Locale.ITALIAN
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream gameDataStream = Main.class.getResourceAsStream("/game_data.json");
            if (gameDataStream == null) {
                logger.error("game_data.json not found in resources!");
                return;
            }
            GameData gameData = mapper.readValue(gameDataStream, GameData.class);
            logger.info("Successfully loaded game data from JSON.");

            for (Locale locale : supportedLocales) {
                logger.info("Generating PDF for language: {}", locale.getLanguage());

                String messagesFile = "/messages_" + locale.getLanguage() + ".json";
                InputStream messagesStream = Main.class.getResourceAsStream(messagesFile);
                if (messagesStream == null) {
                    logger.error("{} not found in resources!", messagesFile);
                    continue;
                }
                LocalizedData messages = mapper.readValue(messagesStream, LocalizedData.class);

                String dest = "lightspeed_arena_" + locale.getLanguage() + ".pdf";
                PdfGenerator pdfGenerator = new PdfGenerator(gameData, messages, locale.getLanguage());
                pdfGenerator.createPdf(dest);
            }

        } catch (IOException e) {
            logger.error("An error occurred during PDF generation.", e);
        }
    }
}
