package com.recoverrelax.pt.riotxmppchat.MyUtil;

public class HttpUtils {
    private static final String ERROR_404_NOT_FOUND = "404 Not Found";

    public static boolean is404NotFound(Throwable e){
        return e.getMessage().equals(ERROR_404_NOT_FOUND);
    }
}
