package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ChampionDto {

    public List<String> allytips;
    public String blurb;
    public List<String> enemytips;
    public int id;
    public ImageDto image;
    public InfoDto info;
    public String key;
    public String lore;
    public String name;
    public String partype;
    public PassiveDto passive;
    public List<RecommendedDto> recommended;
    public List<SkinDto> skins;
    public List<ChampionSpellDto> spells;
    public StatsDto stats;
    public List<String> tags;
    public String title;

    ChampionDto(){}

    @Override
    public String toString() {
        return "ChampionDto{" +
                "allytips=" + allytips +
                ", blurb='" + blurb + '\'' +
                ", enemytips=" + enemytips +
                ", id=" + id +
                ", image=" + image +
                ", info=" + info +
                ", key='" + key + '\'' +
                ", lore='" + lore + '\'' +
                ", name='" + name + '\'' +
                ", partype='" + partype + '\'' +
                ", passive=" + passive +
                ", recommended=" + recommended +
                ", skins=" + skins +
                ", spells=" + spells +
                ", stats=" + stats +
                ", tags=" + tags +
                ", title='" + title + '\'' +
                '}';
    }

    public ImageDto getImage() {
        return image;
    }

    public List<String> getSkinnImageList(String championName){
        List<String> finalSkinList = new ArrayList<>();
        List<SkinDto> skins = getSkins();

        for(SkinDto skin: skins){
            finalSkinList.add(championName + "_" + skin.num);
        }
        return finalSkinList;
    }


    public List<SkinDto> getSkins() {
        return skins;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
