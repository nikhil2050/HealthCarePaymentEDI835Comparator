package com.nik;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class Main {
	final static String DIR_PATH = "D:\\NK\\OneDrive - LTI\\Other\\fvr\\200423_frlncr_vasu\\200425_835sample";
	final static String ACTUAL_DIR_PATH = DIR_PATH + "\\actual\\" ;
	final static String EXPECTED_DIR_PATH = DIR_PATH + "\\exp\\";
//	final static String FILE_NAME = "1003853136.txt";
//	final static String FILE_NAME = "1073675583.txt";
	final static String FILE_NAME = "1114922341.txt";
	
	final static String CONST_FILENAME = "FileName";
	final static String CONST_SEGMENTNAME = "Segment Name";
	final static String CONST_EXPECTED_LINE = "Expected Line";
	final static String CONST_ACTUAL_LINE = "Actual Line";
	final static String CONST_EXPECTED_VAL = "Expected";
	final static String CONST_ACTUAL_VAL = "Actual";

	public static ExtentTest LOGGER = null;
	public static ExtentReports EXTENT = null;
	
	static {
		ExtentHtmlReporter reporter = new ExtentHtmlReporter("./Reports/testFile.html");
		EXTENT = new ExtentReports();
		EXTENT.attachReporter(reporter);
		LOGGER = EXTENT.createTest("LoginTest");
	}
	
	public static void main(String[] args) throws FileNotFoundException {
//		System.out.println(FileUtils.isFileContentSame(EXPECTED_DIR_PATH + FILE_NAME, ACTUAL_DIR_PATH + FILE_NAME));
//		executeComparison();
//		System.out.println(executeStep2Comparison(FILE_NAME));
//		System.out.println(FileUtils.findDiffInLine("CAS*PR*1*162.30~", "CAS*PR*2*162.3~"));
		iterateFiles();
		
//		LOGGER.log(Status.INFO, "Login something 2");
		if(null!=EXTENT)
			EXTENT.flush();
	}
	
	public static void iterateFiles() {
		File directory = new File(EXPECTED_DIR_PATH);
		File[] directoryListing = directory.listFiles();
		for(File expectedFile: directoryListing) {
			File actualFile = new File(ACTUAL_DIR_PATH+expectedFile.getName());
			if(null != actualFile) {
				executeFilesComparison(expectedFile, actualFile);
//				System.out.println(expectedFile.getName()+"\t"+ actualFile.getName());
			}
		}
		EXTENT.flush();
	}
	public static void executeFilesComparison(File expectedFile, File actualFile) {
		
		try {
			if(!executeStep1Comparison(expectedFile, actualFile))
				System.out.println(executeStep2Comparison(expectedFile, actualFile));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean executeStep1Comparison(File expectedFile, File actualFile) throws FileNotFoundException {
		String logStr = "\n\n"+expectedFile.getName();
		System.out.println(logStr);
//		LOGGER.log(Status.INFO, logStr);
		return FileUtils.isFileContentSame(expectedFile, actualFile);
	}

	private static List<Map<String,String>> executeStep2Comparison(File expectedFile, File actualFile) throws FileNotFoundException {
		List<String> expectedTextList = FileUtils.getListFromFile(expectedFile);
		List<String> actualTextList = FileUtils.getListFromFile(actualFile);
		List<Map<String,String>> resultList = new ArrayList<>();
		
		for(int expCount=0, actCount=0; expCount<expectedTextList.size() && actCount<actualTextList.size(); expCount++) {
//			System.out.println("expCount:"+expCount+"\tactCount"+actCount
//					+"\n\tExp: "+expectedTextList.get(expCount)+"\n\tExp: "+actualTextList.get(actCount));
			
			// If expectedLine = actualLine
			if( expectedTextList.get(expCount).equals(actualTextList.get(actCount)) ) {
				actCount++;

			} else {
				// Error in expected line (segments matching)
				if(FileUtils.getSegment(expectedTextList.get(expCount)).equals(FileUtils.getSegment(actualTextList.get(actCount)))) {
					Map<String, String> map = createMap(expectedFile.getName(), FileUtils.getSegment(expectedTextList.get(expCount)),
							expectedTextList.get(expCount), actualTextList.get(actCount), 
							String.valueOf(expCount+1), String.valueOf(actCount+1));
					resultList.add(map);

//					String logStr = "\nExpected line number:-"+expCount+"-"+expectedTextList.get(expCount);
//					logStr = "\nActual line number:-"+actCount+"-"+actualTextList.get(actCount);
//					System.out.println(logStr);
//					LOGGER.log(Status.ERROR, expectedFile.getName()+"\t"+FileUtils.getSegment(expectedTextList.get(expCount))+"\t"
//							+expectedTextList.get(expCount)+"\t"+actualTextList.get(actCount));

					actCount++;
				}
				
				// Line missing in actual (Alternative #2)
				else {
					List<Integer> missedExpectedLineNos = new ArrayList<>();
					for(int tmpExpCount=expCount+1; tmpExpCount<expectedTextList.size() ; tmpExpCount++) {
						missedExpectedLineNos.add(tmpExpCount-1);
						// Check if next Expected line matches current Actual line 
						if(expectedTextList.get(tmpExpCount).equals(actualTextList.get(actCount))){

							// Log all missed Expected lines in Actual 
							for(int missedExpectedLineNo: missedExpectedLineNos) {
								Map<String, String> map = createMap(expectedFile.getName(), FileUtils.getSegment(expectedTextList.get(missedExpectedLineNo)),
										expectedTextList.get(missedExpectedLineNo), "Absent", 
										String.valueOf(missedExpectedLineNo+1), null);
								resultList.add(map);
								
//								String logStr = "\nExpected line number:-"+missedExpectedLineNo+"-"+expectedTextList.get(missedExpectedLineNo);
//								logStr = "\nActual line number:-N/A-N/A";
//								System.out.println(logStr);
							}
							expCount = tmpExpCount;
//							continue;
							break;
						}
					}
					actCount++;
				}
//				/*DELETE THIS*/else {
//					actCount++;					
//				}

//				// Line missing in actual (Alternative #1)
//				else if(expectedTextList.get(expCount+1).equals(actualTextList.get(actCount))) {
//					Map<String, String> map = createMap(fileName, getSegment(expectedTextList.get(expCount)), 
//							expectedTextList.get(expCount), "");
//					resultList.add(map);					
//					expCount--;
//				}
//				
//				// Line xtra in actual (Alternative #1)
//				else if(expectedTextList.get(expCount).equals(actualTextList.get(actCount+1))) {
//					Map<String, String> map = createMap(fileName, getSegment(expectedTextList.get(expCount)), 
//							expectedTextList.get(expCount), "");
//					resultList.add(map);
//					actCount++;
//				}
				
			}
		}
		return resultList;
	}

	public static Map<String, String> createMap(String fileName, String segmentName, 
			String expectedVal, String actualVal, String expLineNo, String actLineNo) {
		expectedVal = expectedVal.replace("~", "");
		actualVal = actualVal.replace("~", "");
		Map<String, String> map = new HashMap<>();
		map.put(CONST_FILENAME, fileName);
		map.put(CONST_SEGMENTNAME, segmentName);
		map.put("Expected Line", expLineNo);
//		map.put(CONST_EXPECTED_LINE, expectedVal);
//		map.put(CONST_ACTUAL_LINE, actualVal);	
		if(actLineNo!=null) {
			Map<String,String> subsegMap = FileUtils.findDiffInLine(expectedVal, actualVal).get(0);
			for(Map.Entry subseg: subsegMap.entrySet()) {
				map.put(CONST_EXPECTED_VAL, String.valueOf(subseg.getKey()));
				map.put(CONST_ACTUAL_VAL, String.valueOf(subseg.getValue()));
			}
		}
		StringBuffer logStr = new StringBuffer("\nExpected line number:-").append(expLineNo).append("-").append(expectedVal)
				.append("\nActual line number:-").append(null!=actLineNo?actLineNo:"N/A").append("-").append(actualVal);
		System.out.println(logStr.toString());
		LOGGER.log(Status.ERROR, fileName+" | "+segmentName+" | "+map.get(CONST_EXPECTED_VAL)+" | "+map.get(CONST_ACTUAL_VAL));
		return map;
	}
	
}
