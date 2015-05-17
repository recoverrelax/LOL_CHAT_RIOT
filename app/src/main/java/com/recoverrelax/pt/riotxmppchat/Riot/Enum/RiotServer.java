package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import java.util.ArrayList;

public enum RiotServer {

   
    BR(0, "Brazil", "chat.br.lol.riotgames.com"),
    EUNE(1, "Europe Nordic and East", "chat.eun1.riotgames.com"),
    EUW(2, "Europe West", "chat.euw1.lol.riotgames.com"),
    KR(3, "Korea", "chat.kr.lol.riotgames.com"),
    LAN(4, "Latin America North", "chat.la1.lol.riotgames.com"),
    LAS(5, "Latin America South", "chat.la2.lol.riotgames.com"),
    NA(6, "North America", "chat.na1.lol.riotgames.com"),
    OCE(7, "Oceania", "chat.oc1.lol.riotgames.com"),
    PH(8, "Phillipines", "chatph.lol.garenanow.com"),
    RU(9, "Russia", "chat.ru.lol.riotgames.com"),
    TH(10, "Thailand", "chatth.lol.garenanow.com"),
    TR(11, "Turkey", "chat.tr.lol.riotgames.com"),
    TW(12, "Taiwan", "chattw.lol.garenanow.com"),
    VN(13, "Vietnam", "chatvn.lol.garenanow.com");

    public final String serverName;
    public final String serverHost;
    public int position;

    RiotServer(int position, String serverName, String serverHost) {
        this.position = position;
        this.serverName = serverName;
        this.serverHost = serverHost;
    }

    public static RiotServer getRiotServerByName(String serverName)
    {
        for(RiotServer server: RiotServer.values())
        {
            if(server.serverName.equals(serverName))
                return server;
        }
        return null;
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
