package org.springfield.lou.application.types.demolinkedtv;

import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.screen.Screen;

public class MainCurrent {
	
	public static void setOnScreen(LinkedtvhbbtvelsApplication app,Screen s,FsTimeLine timeline,int curtime) {
		
		//String annotationTitle = "TODO";
		// what
		FsNode whatnode = timeline.getCurrentFsNode("object", curtime);
		if (whatnode!=null) {
			s.setContent("mainScreenInfoWhat",AppLanguage.getWhatSliderName() + ": "+whatnode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWhat","");
		}
		
		
		// where
		FsNode wherenode = timeline.getCurrentFsNode("location", curtime);
		if (wherenode!=null) {
			s.setContent("mainScreenInfoWhere",AppLanguage.getWhereSliderName() + ": "+wherenode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWhere","");
		}
		
		// who
		FsNode whonode = timeline.getCurrentFsNode("person", curtime);
		if (whonode!=null) {
			s.setContent("mainScreenInfoWho",AppLanguage.getWhoSliderName() + ": "+whonode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWho","");
		}
		
		// chapter
		FsNode chapternode = timeline.getCurrentFsNode("chapter", curtime);
		if (chapternode!=null) {
			s.setContent("mainScreenInfoChapter",AppLanguage.getChapterSliderName() + ": "+chapternode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoChapter", "");
		}
		
	}
}
