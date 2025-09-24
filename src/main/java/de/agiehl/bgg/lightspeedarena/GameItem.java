package de.agiehl.bgg.lightspeedarena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameItem {
    private String image;
    private String nameKey;
    private String subtitleKey;
    private String expansionKey;
    private String descriptionKey;

    // Getters and Setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getSubtitleKey() {
        return subtitleKey;
    }

    public void setSubtitleKey(String subtitleKey) {
        this.subtitleKey = subtitleKey;
    }

    public String getExpansionKey() {
        return expansionKey;
    }

    public void setExpansionKey(String expansionKey) {
        this.expansionKey = expansionKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }
}

