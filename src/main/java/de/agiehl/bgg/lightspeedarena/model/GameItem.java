package de.agiehl.bgg.lightspeedarena.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameItem {

    private String image;
    private String id;
    private String expansionKey;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpansionKey() {
        return expansionKey;
    }

    public void setExpansionKey(String expansionKey) {
        this.expansionKey = expansionKey;
    }
}
