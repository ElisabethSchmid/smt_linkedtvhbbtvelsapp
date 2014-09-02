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
			s.setContent("mainScreenInfoWhat",whatnode.getProperty("title"));
			//s.setContent("mainScreenInfoWhat",AppLanguage.getWhatSliderName() + ": "+whatnode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWhat","");
		}
		
		
		// where
		FsNode wherenode = timeline.getCurrentFsNode("location", curtime);
		if (wherenode!=null) {
			s.setContent("mainScreenInfoWhere",wherenode.getProperty("title"));
			//s.setContent("mainScreenInfoWhere",AppLanguage.getWhereSliderName() + ": "+wherenode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWhere","");
		}
		
		// who
		FsNode whonode = timeline.getCurrentFsNode("person", curtime);
		if (whonode!=null) {
			s.setContent("mainScreenInfoWho",whonode.getProperty("title"));
			//s.setContent("mainScreenInfoWho",AppLanguage.getWhoSliderName() + ": "+whonode.getProperty("title"));
		} else {
			s.setContent("mainScreenInfoWho","");
		}
		
		// chapter
		//FsNode chapternode = timeline.getCurrentFsNode("chapter", curtime);
		//app.log("els chapternode: " + chapternode.asXML());
		
		
		//if (chapternode!=null) {
//			Object oCurChap = s.getProperty("currentChapterNr");
//			
//			int pos = 0;
//			app.log("chapternode: " + chapternode.getId() + "formerChapternode: " + s.getProperty("curId"));
//			if (oCurChap==null) {
//				pos = 1;
//		    	app.log("els pos: " + pos);
//		    	s.setProperty("currentChapterNr","" + pos);
//		    	s.setProperty("curId", chapternode.getId());
//		    }else {
//		    	pos = Integer.parseInt((String)oCurChap);
//		    	if((chapternode.getId() != s.getProperty("curId"))){
//		    		pos++;
//		    		app.log("els pos"+ pos);
//		    		s.setProperty("curId", chapternode.getId());
//		    		s.setProperty("currentChapterNr", ""+ pos);
//		    	} else {
//		    		pos = Integer.parseInt((String)oCurChap);
//		    		app.log("els pos"+ pos);
//		    		s.setProperty("curId", chapternode.getId());
//		    	}
//		    }
			//s.setContent("mainScreenInfoChapter",AppLanguage.getChapterSliderName() + ": "+chapternode.getProperty("title"));
			//s.setContent("mainScreenInfoChapter",AppLanguage.getChapterSliderName() + " "+ pos +": "+chapternode.getProperty("title"));
//		} else {
//			s.setContent("mainScreenInfoChapter", "");
//		}
		
	}
}
