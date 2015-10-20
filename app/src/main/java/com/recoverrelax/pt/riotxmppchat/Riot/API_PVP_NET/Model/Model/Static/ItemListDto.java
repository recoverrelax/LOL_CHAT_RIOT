package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class ItemListDto {

    private BasicDataDto basic;
    private Map<String, ItemDto> data;
    private List<GroupDto> groups;
    private List<ItemTreeDto> tree;
    private String type;
    private String version;

    public ItemListDto() {
    }

    public BasicDataDto getBasic() {
        return basic;
    }

    public Map<String, ItemDto> getData() {
        return data;
    }

    public List<GroupDto> getGroups() {
        return groups;
    }

    public List<ItemTreeDto> getTree() {
        return tree;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }
}
