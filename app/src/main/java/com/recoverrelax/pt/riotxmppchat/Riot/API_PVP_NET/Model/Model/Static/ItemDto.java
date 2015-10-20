package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class ItemDto {

    private String colloq;
    private boolean consumeOnFull;
    private boolean consumed;
    private int depth;
    private String description;
    private Map<String, String> effect;
    private List<String> from;
    private GoldDto gold;
    private String group;
    private boolean hideFromAll;
    private int id;
    private ImageDto image;
    private boolean inStore;
    private List<String> into;
    private Map<String, Boolean> maps;
    private String name;
    private String plaintext;
    private String requiredChampion;
    private MetaDataDto meta;
    private String sanitizedDescription;
    private int specialRecipe;
    private int stacks;
    private BasicDataStatsDto stats;
    private List<String> tags;

    public ItemDto() {
    }

    public String getColloq() {
        return colloq;
    }

    public boolean isConsumeOnFull() {
        return consumeOnFull;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public int getDepth() {
        return depth;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getEffect() {
        return effect;
    }

    public List<String> getFrom() {
        return from;
    }

    public GoldDto getGold() {
        return gold;
    }

    public String getGroup() {
        return group;
    }

    public boolean isHideFromAll() {
        return hideFromAll;
    }

    public int getId() {
        return id;
    }

    public ImageDto getImage() {
        return image;
    }

    public boolean isInStore() {
        return inStore;
    }

    public List<String> getInto() {
        return into;
    }

    public Map<String, Boolean> getMaps() {
        return maps;
    }

    public String getName() {
        return name;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public String getRequiredChampion() {
        return requiredChampion;
    }

    public MetaDataDto getMeta() {
        return meta;
    }

    public String getSanitizedDescription() {
        return sanitizedDescription;
    }

    public int getSpecialRecipe() {
        return specialRecipe;
    }

    public int getStacks() {
        return stacks;
    }

    public BasicDataStatsDto getStats() {
        return stats;
    }

    public List<String> getTags() {
        return tags;
    }
}
