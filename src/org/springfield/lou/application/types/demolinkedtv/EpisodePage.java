package org.springfield.lou.application.types.demolinkedtv;

import java.util.ArrayList;
import java.util.Iterator;

import org.springfield.fs.FsNode;
import org.springfield.fs.FsTimeLine;
import org.springfield.lou.application.types.LinkedtvhbbtvelsApplication;
import org.springfield.lou.screen.Screen;
import org.springfield.mojo.linkedtv.Episode;

public class EpisodePage {
	
	public static void setOnScreen(LinkedtvhbbtvelsApplication app,Screen s, ArrayList <Episode> episodes, String selpos) {
			
		
		int curEpId = 0; //TODO
		Object curO = s.getProperty("curEpId"); 
		if (curO!=null) {
    		curEpId = Integer.parseInt((String)curO);
    		//if (!forcedraw && oldepisode.equals(curid)) return; // nothing has changed don't send
    	} 
		app.log("slider update "+curEpId);
		
		 
		int selEpId = 0;
    	Object o = s.getProperty("selEpId"); //Important for 
    	if (o!=null) {
    		selEpId = Integer.parseInt((String)o);
    		//if (!forcedraw && oldepisode.equals(curid)) return; // nothing has changed don't send
    	} 
		app.log("slider update "+selEpId);
		String body = "";
		int i = 1;
		if(selEpId == curEpId){
			
		}
		for (int j =0; j > episodes.size();j++){
		//for(Iterator<HashMap> iter = episodes; iter.hasNext(); ) {
			//FsNode node = (FsNode)iter.next();
			Episode episode = episodes.get(j);
			String title = episode.getTitle();
			//String thisid = episode.getPresentationId();
			if (j == selEpId) {// no different 
				body += "<div class=\"episodesliderblockselected chapter\" data-referid=\"\" data-time=\"0:00\""; //data-referid=\""+node.getPath()+"\" ??
				body += "data-uid=\"\" data-entity=\""+title+"\" id=\"episode_block"+(i)+"\">"; //TODO data-uid=\""+node.getProperty("uid")+" ??
			} else {
				body += "<div class=\"episodesliderblock chapter\" data-referid=\"\" data-time=\"0:00\""; //DATA-time raus
				body += "data-uid=\"\" data-entity=\""+title+"\" id=\"episode_block"+(i)+"\">";				
			}
		
			body+="<img class=\"episodesliderimg\" src=\""+ episode.getStillsUri()+"\" />"; //Image
			app.log("Stills URI" + episode.getStillsUri()); //if empty take 1. chapter image of episode 
			body+="<div class=\"timecode\">"+app.getTimeCodeString(0)+"</div>";
			if (title == null) {
				
			} else if (title.length()>40) {
				body+="<div class=\"overlay\"><p>"+title.substring(37)+"...</p></div>";
			} else {
				body+="<div class=\"overlay\"><p>"+title+"</p></div>";
			}
			body+="</div>";	
			i++;
		}
		s.setContent("episodeslider",body);
	}
}
