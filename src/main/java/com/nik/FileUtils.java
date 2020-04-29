package com.nik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtils {

	public static String getSegment(String lineStr) {
		if(-1 == lineStr.indexOf('*')) {
			return "";
		}
		return lineStr.substring(0, lineStr.indexOf('*'));
	}

	public static List<String> getListFromFile(File file) throws FileNotFoundException{
	    List<String> resultList = new ArrayList<>();
	    String sCurrentLine;

	    BufferedReader br = new BufferedReader(new FileReader(file));

		try {
			while ((sCurrentLine = br.readLine()) != null) {
				resultList.add(sCurrentLine);
			}
		} catch (IOException e) {
		}
		
		return resultList;
	}

	public static boolean isFileContentSame(File expectedFile, File actualFile) throws FileNotFoundException{
	    String expectedCurrLine;
	    String actualCurrLine;
	    
	    try (BufferedReader expectedBR = new BufferedReader(new FileReader(expectedFile));
	    		BufferedReader actualBR = new BufferedReader(new FileReader(actualFile));) {
			
			while ((expectedCurrLine = expectedBR.readLine()) != null 
					&& (actualCurrLine = actualBR.readLine()) != null) {
				if(!expectedCurrLine.equals(actualCurrLine)) {
					return false;
				}
			}
		} catch (IOException e) {
		}
	    return true;
	}

	public static List<Map<String, String>> findDiffInLine(String expectedLine, String actualLine) {
		List<Map<String, String>> list = new ArrayList<>();
		if(expectedLine.split("\\*").length != actualLine.split("\\*").length) {
			Map<String, String> map = new HashMap<>();
			map.put(expectedLine.substring(expectedLine.indexOf('*'), expectedLine.length()),
					actualLine.substring(actualLine.indexOf('*'), actualLine.length()));
			list.add(map);
			return list;
		
		} else {
			String[] expectedSubsegList = expectedLine.split("\\*"); 
			String[] actualSubsegList = actualLine.split("\\*"); 
			for(int cnt=1; cnt<expectedSubsegList.length; cnt++) {
				if(!expectedSubsegList[cnt].equals(actualSubsegList[cnt])) {
					Map<String, String> map = new HashMap<>();
					map.put(expectedSubsegList[cnt], actualSubsegList[cnt]);
					list.add(map);					
				}
			}
			return list;
		}
		
	}
}
