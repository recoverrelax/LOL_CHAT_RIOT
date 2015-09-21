package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum MapNames {
    SUMMONERS_RIFT_CV(11, "Summoner's Rift", "Current Version"),
    SUMMONERS_RIFT_SM(1, "Summoner's Rift", "Original Summer Variant"),
    SUMMONERS_RIFT_AV(2, "Summoner's Rift", "Original Autumn Variant"),
    PROVIDING_GROUNDS(3, "The Proving Grounds", "Tutorial Map"),
    TWISTED_THREELINE_OV(4, "Twisted Treeline", "Original Version"),
    TWISTED_THREELINE_CV(10, "Twisted Treeline", "Current Version"),
    CRYSTAL_SCAR(8, "The Crystal Scar", "Dominion Map"),
    HOWLING_ABYSS(12, "Howling Abyss", "ARAM Map"),
    BUTCHERS_BRIDGE(14, "Butcher's Bridge", "ARAM Map");

    private String mapName;
    private int mapId;
    private String mapNotes;

    MapNames(int mapId, String mapName, String mapNotes){
        this.mapId = mapId;
        this.mapName = mapName;
        this.mapNotes = mapNotes;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getMapNotes() {
        return mapNotes;
    }

    public void setMapNotes(String mapNotes) {
        this.mapNotes = mapNotes;
    }
}
