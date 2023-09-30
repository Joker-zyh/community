package com.henu.community.util;

import java.util.UUID;

public class GenerateUUID {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
