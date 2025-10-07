package de.agiehl.bgg.lightspeedarena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalizedData {
    private HeaderData header;
    private FooterData footer;
    private String fontsize;
    private TitleData title;
    private Map<String, String> expansions;
    private List<LocalizedItem> comets;
    private List<LocalizedItem> factions;

    // Getters and setters

    public HeaderData getHeader() {
        return header;
    }

    public void setHeader(HeaderData header) {
        this.header = header;
    }

    public FooterData getFooter() {
        return footer;
    }

    public void setFooter(FooterData footer) {
        this.footer = footer;
    }

    public String getFontsize() {
        return fontsize;
    }

    public void setFontsize(String fontsize) {
        this.fontsize = fontsize;
    }

    public TitleData getTitle() {
        return title;
    }

    public void setTitle(TitleData title) {
        this.title = title;
    }

    public Map<String, String> getExpansions() {
        return expansions;
    }

    public void setExpansions(Map<String, String> expansions) {
        this.expansions = expansions;
    }

    public List<LocalizedItem> getComets() {
        return comets;
    }

    public void setComets(List<LocalizedItem> comets) {
        this.comets = comets;
    }

    public List<LocalizedItem> getFactions() {
        return factions;
    }

    public void setFactions(List<LocalizedItem> factions) {
        this.factions = factions;
    }
}
