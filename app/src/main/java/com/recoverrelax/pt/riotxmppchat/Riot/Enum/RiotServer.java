package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import java.util.ArrayList;

public enum RiotServer {

   
    BR(0, "Brazil", "chat.br.lol.riotgames.com", true),
    EUNE(1, "Europe Nordic and East", "chat.eun1.riotgames.com", true),
    EUW(2, "Europe West", "chat.euw1.lol.riotgames.com", true),
    KR(3, "Korea", "chat.kr.lol.riotgames.com", true),
    LAN(4, "Latin America North", "chat.la1.lol.riotgames.com", true),
    LAS(5, "Latin America South", "chat.la2.lol.riotgames.com", true),
    NA(6, "North America", "chat.na1.lol.riotgames.com", true),
    OCE(7, "Oceania", "chat.oc1.lol.riotgames.com", true),
    PH(8, "Phillipines", "chatph.lol.garenanow.com", false),
    RU(9, "Russia", "chat.ru.lol.riotgames.com", true),
    TH(10, "Thailand", "chatth.lol.garenanow.com", false),
    TR(11, "Turkey", "chat.tr.lol.riotgames.com", true),
    TW(12, "Taiwan", "chattw.lol.garenanow.com", false),
    VN(13, "Vietnam", "chatvn.lol.garenanow.com", false);

    private final String serverName;
    private final String serverHost;
    private int position;
    private boolean apiAvailable;

    RiotServer(int position, String serverName, String serverHost, boolean apiAvailable) {
        this.position = position;
        this.serverName = serverName;
        this.serverHost = serverHost;
        this.apiAvailable = apiAvailable;
    }

    public static RiotServer getRiotServerByName(String serverName)
    {
        for(RiotServer server: RiotServer.values())
        {
            if(server.serverName.equals(serverName))
                return server;
        }
        throw new IllegalArgumentException("No RiotServer Enum for serverName: " + serverName);
    }

    public static RiotServer getRiotServerHost(String serverHost)
    {
        for(RiotServer server: RiotServer.values())
        {
            if(server.serverHost.equals(serverHost))
                return server;
        }
        throw new IllegalArgumentException("No RiotServer Enum for serverHost: " + serverHost);
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

    public static String [] getServerList(){
        ArrayList<String> serverList = new ArrayList<>();

        for(RiotServer server: RiotServer.values()){
            serverList.add(server.getServerName());
        }
        return serverList.toArray(new String[serverList.size()]);
    }

    public static int getServerPositionByName(String serverName)
    {
        for(RiotServer server: RiotServer.values())
        {
            if(server.serverName.equals(serverName))
                return server.getPosition();
        }
        return 0;
    }

}
