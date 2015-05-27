package com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils;

public class XmppUtils {

    public static String parseXmppAddress(String jid) {
        int slashIndex = jid.indexOf('/');
        if (slashIndex < 0) {
            return jid;
        } else if (slashIndex == 0) {
            return "";
        } else {
            return jid.substring(0, slashIndex);
        }
    }
}
