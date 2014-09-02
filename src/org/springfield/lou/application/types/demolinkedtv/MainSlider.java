package org.springfield.lou.application.types.demolinkedtv;

import java.util.HashMap;
import java.util.Iterator;

import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.screen.Screen;

public class MainSlider {

	public static void setOnScreen(LinkedtvhbbtvelsApplication app,Screen s,FsTimeLine timeline,int curtime,String selpos,boolean forcedraw) {
	
		String curid = null;
		FsNode chapternode = timeline.getCurrentFsNode("chapter", curtime);
		if (chapternode!=null) {
			curid = chapternode.getId();
		}
    	Object o = s.getProperty("chapteractive");
    	if (o!=null) {
    		String oldchapter = (String)o;
    		if (!forcedraw && oldchapter.equals(curid)) return; // nothing has changed don't send
    	} 
		s.setProperty("chapteractive",curid);
		app.log("slider update "+curid);
		String body = "";
		int i = 1;
		String selid = null;
		try {
			FsNode n = timeline.getFsNodeById("chapter",Integer.parseInt(selpos));
			if (n!=null) selid = n.getId();
		} catch(Exception e) {
			
		}
		String titleToDisplay = ""; 
		
		for(Iterator<FsNode> iter = timeline.getFsNodesByType("chapter"); iter.hasNext(); ) {
			FsNode node = (FsNode)iter.next();
			String title = node.getProperty("title");
			String thisid = node.getId();
			if (selid!=null && selid.equals(thisid)) {
				body += "<div class=\"mainsliderblockselected chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
			} else if (curid!=null && curid.equals(thisid)) {
				body += "<div class=\"mainsliderblockactive chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";
			} else {
				body += "<div class=\"mainsliderblock chapter\" data-referid=\""+node.getPath()+"\" data-time=\""+node.getStarttime()/1000+"\"";
				body += "data-uid=\""+node.getProperty("uid")+"\" data-entity=\""+title+"\" id=\"chapter_block"+(i)+"\">";				
			}
		
			body+="<img class=\"mainsliderimg\" src=\""+node.getScreenShotUrl()+"\" />";
			//body+="<div class=\"timecode\">"+app.getTimeCodeString(node.getStarttime()/1000)+"</div>"; //TODO proof if to keep
			if (curid!=null && curid.equals(thisid)){//TODO els
				titleToDisplay = title; //TODO els
			//body+="<div class=\"currentChaptitle\">"+title+"</div>";//TODO els
			}//TODO els
//			if (title == null) {
//			} else if (title.length()>40) {
//				body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
//			} else {
//				body+="<div class=\"overlay\"><p>"+title+"</p></div>";
//			}//TODO els
			body+="</div>";	
			i++;
		}
		body+="<div id=\"currentChapTitle\">"+titleToDisplay+"</div>";//TODO els
		s.setContent("mainscreenslider",body);
	}

}
