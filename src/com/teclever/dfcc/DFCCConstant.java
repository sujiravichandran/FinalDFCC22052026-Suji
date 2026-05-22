package com.teclever.dfcc;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.teclever.dfcc.datastore.dto.CopyFileDTO;
import com.teclever.dfcc.datastore.dto.StageFilesStateDTO;
import com.teclever.dfcc.stateMachine.AdvancedTestStateObject;
import com.teclever.dfcc.stateMachine.LRUTestStateObject;
import com.teclever.dfcc.stateMachine.SelfTestStateObject;
import com.teclever.dfcc.stateMachine.SessionTestStateObject;
import com.teclever.dfcc.stateMachine.StateMachine;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DFCCConstant {
    private static Map uutIdNameMap;
    private static Map uutNameIdMap;
    public static boolean isJarBuild = false ;
 
    
    
    public static String JARSTRING = "";
    public static String testTypeId = "";
    public static String fileNameRdf = "";
    public static String tpfFileName = "";
    public static boolean moveException = false;
    public static boolean dashBoardLoading = false;
    public static boolean testFilesEnableChecking = false;
    
    public static String roleId ="";
    public static boolean completeFlag = false;
    public static boolean runnedCompleteTest = false;
    public static boolean mandatoryOfpLoading = false;

    public static String filePath = "";
    public static boolean isDebug = false;
    public static String testFileResultId = "";	
    public static String testStarttime = "";
    public static String testEndTime = "";
    public static int totalFilesCount = 0;
    public static int failedFilesCount = 0;
    
    public static String selectedSNoBuildConfig = "";
    public static String selectedUutBuildConfig = "";
    public static String selectedBuildConfig = "";
    public static String selectedVersionNoBuildConfig = "";
    public static String selectedFetchBuildConfig = "";
    public static String selectedBuildConfigDate;
    
  //for Manual Testing 153::
  	public static String selectedSessionId1553 = "";
  	public static String selectedStageId1553 = "";
  	
  	public static String showUut="";
  	public static String showSerialNo="";
  	public static String showSession="";
  	public static String showStage="";
  	
    public static boolean sessionTestStagesCompleted = false;
  	
//  	Build Config::
  	 public static boolean buildFetched = false;
  	 public static boolean buildDashboard = false;
  	 
//  	 Stopping flag::
  	 public static boolean pauseStopping = false;
  	 
//  	 Picheck:
  	public static boolean picheckStop = false;
  	 
  	public static boolean testPauseStopping = false;
  	 
//  	file move if closed
//	 Stopping flag::
	 public static boolean closedFileMove = false;
	 public static boolean continueAfterPopupAction =false;
  	
  	
//  	For Last Test Stop Case Count::
  	public static int totalTestFileCount =0 ;
  	public static int runnedTestFileCount =0 ;
  	
  	public static String macroCommandName="";
  	
//  	Custom1 SymbolCommand::
	public static String symbolCommand = "";
	public static boolean custom1TestCompleteFlag = false;
    
    
//    For Dashboard:
    public static boolean linkTestType = true;
    
    public static boolean lastFileCountFlag = false;
    
//    Cumulative result:
    public static String selectedUut = "";
    public static String selectedSNo = "";
    
//    Deviation:
    public static String selectedSession = "";
    
//    RDFMoveCancel:
    public static boolean rdfMoveCanceled = false;
    
    //AnujK : 05/08/2025 -- Changed to 3mins
    public static long tempDelayTime = 180000; //3mins
    public static boolean entered = true;
    public static boolean colourFlag = false;
    public static boolean stopColourFlag = false;
    public static boolean continueWithErrorFlag = false;
    public static boolean macroHandled = false;
    public static boolean LoadDriverHandled = false;
   
    public static Map <String,String>stageIdStatus = new HashMap<String,String>();
    
    public static Map<String,String> sessionIdDfccSlNo = new HashMap<String,String>();
    
    public static String resultStartTime = "";
    public static String resultEndTime = "";
    public static String resultStageType = "";
    
    //For Date Time Issue
    public static String endTime = "";
    
    //For Enabling Disabling Issue
    public static int repeatCount = 0;
    public static String stageId= "";
    public static String sessionStageMapId = "";
    
    
    //Mani Changes Added
    public static int cardCount = 0;
    public static int currentCount = 0;
    
    public static String stageIdForRdf="";
   
    public static String temDataSelection="";
    
//    Build Config saved Date::
    
    public static String savedDate="";
    
    
    
//    Aitess switchh delay::
    public static boolean aitessSwitched = false;
    
//    Custom1Path::
    public static String customOnePath = "";
    public static String currentTestStageId;
    public static void continueAfterPopupAction() {
        // Update closedFileMove based on user action
        // Reset all test results and flags
        DFCCConstant.FailedStagesRdfPaths.clear();
        SessionTestStateObject.clearSessionTestResults();
        SelfTestStateObject.clearselfTestResults();
        AdvancedTestStateObject.clearAdvancedTestResultsList();
        LRUTestStateObject.clearlruTestResultsList();
        StateMachine.setResettingProgressBar(true);

        DFCCConstant.closedFileMove = false;
        continueAfterPopupAction = true;
       
    }
    
    public static Map<String,List<String>> stageCompletedFiles = new HashMap<String,List<String>>();
    public static Map<String,List<String>> stageActualFiles = new HashMap<String,List<String>>();

    //Move files And LogOut Move Files
    public static Map<String,List<CopyFileDTO>>logOutmoveFiles = new HashMap<String,List<CopyFileDTO>>();
    public static List<Path>rdfsPaths = new ArrayList<Path>();
    public static List<CopyFileDTO> FailedStagesRdfPaths = new ArrayList<CopyFileDTO>();
    public static Path  outPut ;
    public static Map<String,StageFilesStateDTO> sessionStagesResultsState = new HashMap<String,StageFilesStateDTO>();
    
    
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
