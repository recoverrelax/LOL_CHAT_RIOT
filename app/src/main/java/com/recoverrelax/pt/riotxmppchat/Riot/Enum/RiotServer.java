package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public enum RiotServer {


    BR(0, "Brazil", "chat.br.lol.riotgames.com", "BR1", true, "br"),
    EUNE(1, "Europe Nordic and East", "chat.eun1.riotgames.com", "EUN1", true, "eune"),
    EUW(2, "Europe West", "chat.euw1.lol.riotgames.com", "EUW1", true, "euw"),
    KR(3, "Korea", "chat.kr.lol.riotgames.com", "KR", true, "kr"),
    LAN(4, "Latin America North", "chat.la1.lol.riotgames.com", "LA1", true, "lan"),
    LAS(5, "Latin America South", "chat.la2.lol.riotgames.com", "LA2", true, "las"),
    NA(6, "North America", "chat.na1.lol.riotgames.com", "NA1", true, "na"),
    OCE(7, "Oceania", "chat.oc1.lol.riotgames.com", "OC1", true, "oce"),
    //    PH(8, "Phillipines", "chatph.lol.garenanow.com", null, false, null),
    RU(8, "Russia", "chat.ru.lol.riotgames.com", "RU1", true, "ru"),
    //    TH(10, "Thailand", "chatth.lol.garenanow.com", null, false, null),
    TR(9, "Turkey", "chat.tr.lol.riotgames.com", "TR1", true, "tr");
//    TW(12, "Taiwan", "chattw.lol.garenanow.com", null, false, null),
//    VN(13, "Vietnam", "chatvn.lol.garenanow.com", null, false, null);

    private final String serverName;
    private final String serverHost;
    private final String platformId;
    private final String region;
    private int position;
    private boolean apiAvailable;

    RiotServer(int position, String serverName, String serverHost, String platformId, boolean apiAvailable, String region) {
        this.position = position;
        this.serverName = serverName;
        this.serverHost = serverHost;
        this.region = region;
        this.platformId = platformId;
        this.apiAvailable = apiAvailable;
    }

    public static RiotServer getRiotServerByName(String serverName) {
        for (RiotServer server : RiotServer.values()) {
            if (server.serverName.equals(serverName))
                return server;
        }
        return null;
    }

    public static RiotServer getRiotServerHost(String serverHost) {
        for (RiotServer server : RiotServer.values()) {
            if (server.serverHost.equals(serverHost))
                return server;
        }
        return null;
    }

    public static List<String> getServerList() {
        ArrayList<String> serverList = new ArrayList<>();

        for (RiotServer server : RiotServer.values()) {
            serverList.add(server.getServerName());
        }
        return serverList;
    }

    public static List<String> getRegionList() {
        ArrayList<String> serverList = new ArrayList<>();

        for (RiotServer server : RiotServer.values()) {
            serverList.add(server.getServerRegion());
        }
        return serverList;
    }

    public static int getServerPositionByName(String serverName) {
        for (RiotServer server : RiotServer.values()) {
            if (server.serverName.equals(serverName))
                return server.getPosition();
        }
        return 0;
    }

    public static RiotServer getByServerPosition(int position) {
        for (RiotServer server : RiotServer.values()) {
            if (server.position == position)
                return server;
        }
        return RiotServer.EUW;
    }

    public int getPosition() {
        return position;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getServerRegion() {
        return this.region;
    }

    public String getServerPlatformId() {
        return platformId;
    }

}
