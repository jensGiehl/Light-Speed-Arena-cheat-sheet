# Light Speed: Arena - Cheat Sheet Generator

This project helps you to quickly and easily create an overview (of the asteroids & factions) for the board game "Light Speed: Arena" for different languages. It's a cheat sheet so you don't have to have the instructions for every expansion at hand.

## How to add a new language

1.  **Copy an existing language file:**
    *   In the `src/main/resources` directory you will find files like `messages_de.json` or `messages_en.json`.
    *   Copy one of these files and rename it with the appropriate language code (e.g., `messages_fr.json` for French).

2.  **Translate the content:**
    *   Open the new file.
    *   Translate the values of the JSON entries into the new language. Please make sure that the keys (e.g., `"header"`, `"title"`, `"comets"`) remain unchanged.

3.  **Build the project:**
    *   Run the application to generate the PDF with the new language. The language is selected based on your system's locale.
