package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ChampionSpellDto {

    public List<ImageDto> allimages;
    public List<Double> cooldown;
    public String cooldownBurn;
    public List<Integer> cost;
    public String costBurn;
    public String costType;
    public String description;
    public List<List<Double>> effect;
    public List<String> effectBurn;
    public ImageDto image;
    public String key;
    public LevelTipDto leveltip;
    public int maxrank;
    public String name;
    public Object range;
    public String rangeBurn;
    public String resource;
    public String sanitizedDescription;
    public String sanitizedTooltip;
    public String tooltip;
    public List<SpellVarsDto> vars;

    public ChampionSpellDto() {
    }

}
