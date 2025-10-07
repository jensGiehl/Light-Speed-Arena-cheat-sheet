package de.agiehl.bgg.lightspeedarena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TitleData {
    private String comets;
    private String factions;

    public String getComets() {
        return comets;
    }

    public void setComets(String comets) {
        this.comets = comets;
    }

    public String getFactions() {
        return factions;
    }

    public void setFactions(String factions) {
        this.factions = factions;
    }
}
