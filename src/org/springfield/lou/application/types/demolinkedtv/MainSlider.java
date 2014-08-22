package org.springfield.lou.application.types.demolinkedtv;

import java.util.Iterator;

import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.screen.Screen;

public class MainSlider {

	public static void setOnScreen(LinkedtvhbbtvelsApplication app,Screen s,FsTimeLine timeline,int curtime) {
		String curid = null;
		FsNode chapternode = timeline.getCurrentFsNode("chapter", curtime);
		if (chapternode!=null) {
			curid = chapternode.getId();
		}
    	Object o = s.getProperty("chapteractive");
    	if (o!=null) {
    		String oldchapter = (String)o;
    		if (oldchapter.equals(curid)) return; // nothing has changed don't send
    	} 
		s.setProperty("chapteractive",curid);
		app.log("slider update "+curid);
		String body = "";
		int i = 1;
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("chapter"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			String title = node.getProperty("title");
			String thisid = node.getId();
			if (curid!=null && curid.equals(thisid)) {
				body += "<div class=\"mainsliderblockactive chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
			} else {
				body += "<div class=\"mainsliderblock chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";				
			}
		
			body+="<img class=\"mainsliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
			body+="<div class=\"timecode\">"+app.getTimeCodeString(node.getStarttime()/1000)+"</div>";
			if (title == null) {
				
			} else if (title.length()>40) {
				body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
			} else {
				body+="<div class=\"overlay\"><p>"+title+"</p></div>";
			}
			body+="</div>";	
			i++;
		}
		s.setContent("mainscreenslider",body);
		//app.setContentAllScreensWithRole("mainscreen", "mainscreenslider",body);

	}

}
