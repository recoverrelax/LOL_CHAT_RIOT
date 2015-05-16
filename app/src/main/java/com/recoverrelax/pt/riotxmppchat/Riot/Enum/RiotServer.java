package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

public enum RiotServer {

   
    BR("Brazil", "chat.br.lol.riotgames.com"),
    EUNE("Europe Nordic and East", "chat.eun1.riotgames.com"),
    EUW("Europe West", "chat.euw1.lol.riotgames.com"),
    KR("Korea", "chat.kr.lol.riotgames.com"),
    LAN("Latin America North", "chat.la1.lol.riotgames.com"),
    LAS("Latin America South", "chat.la2.lol.riotgames.com"),
    NA("North America", "chat.na1.lol.riotgames.com"),
    OCE("Oceania", "chat.oc1.lol.riotgames.com"),
    PH("Phillipines", "chatph.lol.garenanow.com"),
    RU("Russia", "chat.ru.lol.riotgames.com"),
    TH("Thailand", "chatth.lol.garenanow.com"),
    TR("Turkey", "chat.tr.lol.riotgames.com"),
    TW("Taiwan", "chattw.lol.garenanow.com"),
    VN("Vietnam", "chatvn.lol.garenanow.com");

    public final String serverName;
    public final String serverHost;

    RiotServer(String serverName, String serverHost) {
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
}
