package org.springfield.lou.application.types.demolinkedtv;

import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.screen.Screen;

public class MainCurrent {
	
	public static void setOnScreen(LinkedtvhbbtvelsApplication app,Screen s,FsTimeLine timeline,int curtime) {
		
		// what
		FsNode whatnode = timeline.getCurrentFsNode("object", curtime);
		if (whatnode!=null) {
			s.setContent("mainScreenInfoWhat","What : "+whatnode.getProperty("title"));
			//app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWhat","What : "+whatnode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWhat","");
			//app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWhat", "");			
		}
		
		
		// where
		FsNode wherenode = timeline.getCurrentFsNode("location", curtime);
		if (wherenode!=null) {
			app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWhere","Where : "+wherenode.getProperty("title"));
		} else {
			app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWhere","");
		}
		
		// who
		FsNode whonode = timeline.getCurrentFsNode("person", curtime);
		if (whonode!=null) {
			app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWho","Who : "+whonode.getProperty("title"));
		} else {
			app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoWho","");
		}
		
		// chapter
		FsNode chapternode = timeline.getCurrentFsNode("chapter", curtime);
		if (chapternode!=null) {
			app.setContentAllScreensWithRole("mainscreen", "mainScreenInfoChapter","Chapter : "+chapternode.getProperty("title"));
		} else {
			app.setContentAllScreensWithRole("mainscreen","mainScreenInfoChapter", "");			
		}
		
	}
}
