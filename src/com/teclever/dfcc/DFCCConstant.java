package com.teclever.dfcc;


import java.util.Map;

public class DFCCConstant {
    private static Map uutIdNameMap;
    private static Map uutNameIdMap;
    public static boolean isJarBuild = false;
    public static String jarString = "";
    public static Map getUutIdNameMap() {
        return uutIdNameMap;
    }

    public static void setUutIdNameMap(Map uutIdNameMap) {
        DFCCConstant.uutIdNameMap = uutIdNameMap;
    }

    public static Map getUutNameIdMap() {
        return uutNameIdMap;
    }

    public static void setUutNameIdMap(Map uutNameIdMap) {
        DFCCConstant.uutNameIdMap = uutNameIdMap;
    }
}