package com.teclever.dfcc;


import java.util.Map;

public class DFCCConstant {
    private static Map uutIdNameMap;
    private static Map uutNameIdMap;
    public static boolean isJarBuild = false;
    public static String JARSTRING = "";
    public static String filePath = "";
    public static boolean isDebug = false;
    public static long tempDelayTime = 120000; //2mins
    public static boolean entered = true;
    
    public static class UutTypeConstants {
        public static final String MARK1 = "DFCC-MK1";
        public static final String MARK1A = "DFCC-MK1A";
        public static final String MARK2 = "DFCC-MK2";
    }
    

	public static boolean isEntered() {
		return entered;
	}

	public static void setEntered(boolean entered) {
		DFCCConstant.entered = entered;
	}

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

	public static void setDebug(boolean isDebug) {
		DFCCConstant.isDebug = isDebug;
	}
    
}
