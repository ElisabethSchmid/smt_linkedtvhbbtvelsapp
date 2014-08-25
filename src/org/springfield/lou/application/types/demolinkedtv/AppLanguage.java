package org.springfield.lou.application.types.demolinkedtv;

import java.io.File;
import java.util.HashMap;

import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.tools.FsFileReader;

public class AppLanguage {
	
	private static String whoSliderName = "WHO";
	private static String whatSliderName = "WHAT";
	private static String whereSliderName = "WHERE";
	private static String chapterSliderName = "CHAPTER";
	private static String bookmarkSliderName = "BOOKMARK";
	private static String sharedSliderName = "SHARED";
	private static String joinedSliderName = "JOINED";
	private static String time = "TIME";
	private static String findOutMore = "FIND OUT MORE";
	private static String rotateScreen = "Rotate your screen to read more";
	
	public static void loadLanguage(LinkedtvhbbtvelsApplication app, String language){
		String fileContent = "";
		
		if(language.equals("de")){
			fileContent = FsFileReader.getFileContent(app, "displayText", "displayText"+File.separator+"text_de.js");
		} else if(language.equals("en")){
			fileContent = FsFileReader.getFileContent(app, "displayText", "displayText")+File.separator+"text_en.js";
		} else {
			// TODO els
			fileContent = FsFileReader.getFileContent(app, "displayText", "displayText")+File.separator+"defaultText.js";
			// end els TODO
		}
		if(fileContent.length() > 0){
			String[] defaultTextParameterRows = fileContent.split("\n");
			HashMap <String,String> defaultTextParameters = new HashMap<String,String>();
			for (String row : defaultTextParameterRows) {
				System.out.println(row);
				String [] defaultTextParameterLine = row.split("=");
				defaultTextParameters.put(defaultTextParameterLine[0],defaultTextParameterLine[1]);
			}
			System.out.println("map:" + defaultTextParameters.toString());
			
			whoSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.who");
			whatSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.what");
			whereSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.where");
			chapterSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.chapter");
			joinedSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.bookmark");
			bookmarkSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.shared");
			sharedSliderName = defaultTextParameters.get("LinkedtvhbbtvApplication.joined");
			time = defaultTextParameters.get("LinkedtvhbbtvApplication.time");
			findOutMore = defaultTextParameters.get("LinkedtvhbbtvApplication.findOutMore");
			rotateScreen = defaultTextParameters.get("LinkedtvhbbtvApplication.rotateScreen");
			
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.who"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.what"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.where"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.chapter"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.bookmark"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.shared"));
			System.out.println("els: " + defaultTextParameters.get("LinkedtvhbbtvApplication.joined"));
		} 
	}

	public static String getRotateScreen() {
		return time;
	}

	
	public static String getTime() {
		return time;
	}

	public static String getFindOutMore() {
		return findOutMore;
	}


	public static String getWhoSliderName() {
		return whoSliderName;
	}

	public static String getWhatSliderName() {
		return whatSliderName;
	}

	public static String getWhereSliderName() {
		return whereSliderName;
	}


	public static String getChapterSliderName() {
		return chapterSliderName;
	}


	public static String getBookmarkSliderName() {
		return bookmarkSliderName;
	}

	public static String getSharedSliderName() {
		return sharedSliderName;
	}


	public static String getJoinedSliderName() {
		return joinedSliderName;
	}
}
