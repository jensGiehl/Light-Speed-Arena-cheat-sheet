package de.agiehl.bgg.lightspeedarena;

import java.util.List;

public class GameData {
    private List<Comet> comets;
    private List<Faction> factions;

    // Getters and Setters
    public List<Comet> getComets() {
        return comets;
    }

    public void setComets(List<Comet> comets) {
        this.comets = comets;
    }

    public List<Faction> getFactions() {
        return factions;
    }

    public void setFactions(List<Faction> factions) {
        this.factions = factions;
    }
}
