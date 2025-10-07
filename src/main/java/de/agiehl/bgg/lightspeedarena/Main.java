package de.agiehl.bgg.lightspeedarena;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Defines which languages should be generated.
        // Add a new Locale here to support a new language.
        List<Locale> supportedLocales = List.of(
                Locale.GERMAN,
                Locale.ENGLISH,
                Locale.ITALIAN
        );

        try {
            // Load the central game data from JSON
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = Main.class.getResourceAsStream("/game_data.json");
            if (is == null) {
                logger.error("game_data.json not found in resources!");
                return;
            }
            GameData gameData = mapper.readValue(is, GameData.class);
            logger.info("Successfully loaded game data from JSON.");

            // Generate a PDF for each supported language
            for (Locale locale : supportedLocales) {
                logger.info("Generating PDF for language: {}", locale.getLanguage());

                String messagesFile = "/messages_" + locale.getLanguage() + ".json";
                InputStream messagesIs = Main.class.getResourceAsStream(messagesFile);
                if (messagesIs == null) {
                    logger.error("{} not found in resources!", messagesFile);
                    continue; // Skip to next language
                }
                LocalizedData messages = mapper.readValue(messagesIs, LocalizedData.class);

                String dest = "lightspeed_arena_" + locale.getLanguage() + ".pdf";
                PdfGenerator pdfGenerator = new PdfGenerator(gameData, messages, locale.getLanguage());
                pdfGenerator.createPdf(dest);
            }

        } catch (IOException e) {
            logger.error("An error occurred during PDF generation.", e);
        }
    }
}
