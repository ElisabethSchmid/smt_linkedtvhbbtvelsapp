/* 
* LinkedtvhbbtvApplication.java
* 
* Copyright (c) 2013 Noterik B.V.
* 
* This file is part of smt_demolinkedtvapp, an app for the multiscreen toolkit 
* related to the Noterik Springfield project.
*
* smt_demolinkedtvapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_demolinkedtvapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_demolinkedtvapp.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.lou.application.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.application.components.BasicComponent;
import org.springfield.lou.application.components.ComponentInterface;
import org.springfield.lou.application.types.demolinkedtv.AppLanguage;
import org.springfield.lou.application.types.demolinkedtv.MainCurrent;
import org.springfield.lou.application.types.demolinkedtv.MainSlider;
import org.springfield.lou.application.types.demolinkedtv.EpisodePage;
import org.springfield.lou.application.types.demolinkedtv.Slider;
import org.springfield.fs.*;
import org.springfield.hbbtvsupport.RemoteControl;
import org.springfield.lou.screen.Capabilities;
import org.springfield.lou.screen.Screen;
import org.springfield.lou.tools.FsFileReader;
import org.springfield.lou.user.User;
import org.springfield.mojo.linkedtv.Episode;
import org.springfield.mojo.linkedtv.GAIN;
import org.springfield.mojo.linkedtv.GAINObjectEntity;

/**
 * LinkedTV HbbTV application based on LinkedTV Demo Application.
 * The multiscreen application shows the video on the main TV screen 
 * while the second screen shows related information about the current 
 * moment in video divided into four slider layers, "who", "what", 
 * "where" and chapters. Users can select items from the seconds screen 
 * to read a short abstract about it or rotate their device to go to 
 * a website with more information.
 * Users can also push the entities to the screen, the video on the 
 * mainscreen then jumps to the attached moment in time.
 * Further more it's possible to login and share or bookmark entities
 * from the different layers.
 * 
 * @author Daniel Ockeloen, Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2013
 * @package org.springfield.lou.application.types
 * 
 */
public class LinkedtvhbbtvelsApplication extends Html5Application {
	private static String GAIN_ACCOUNT = "LINKEDTV-TEST";
	
	
	private Episode episode;
	private GAIN gain;
	
	public FsTimeLine timeline = null;
	private enum sliders { whoslider,whatslider,whereslider,chapterslider,bookmarkslider,sharedslider, joinedslider; }
	private enum blocks { who,what,where,chapter; }
	private int currentChapter = -1;
	private long currentTime = 0l;
	private boolean hbbtvMode = false; 
	private boolean episodeSelected = false; //TODO els
	
	private String language = "de"; //change language to something else then de to load default

	
	public LinkedtvhbbtvelsApplication(String id) {
		super(id); 
		//gain = new GAIN(GAIN_ACCOUNT, id);
		if (gain!=null) gain.application_new();
		
		System.out.println("LINKEDTVHBBTV APPLICATION STARTED");
	}
	
	public LinkedtvhbbtvelsApplication(String id, String remoteReceiver) {
		super(id, remoteReceiver); 
		
		// gain = new GAIN(GAIN_ACCOUNT, id);
		if (gain!=null) gain.application_new();
		System.out.println("LINKEDTVHBBTV APPLICATION STARTED 2");
	}
	
	/**
	 * Handling new screen
	 * 
	 * @param s - screen
	 */
	public void onNewScreen(Screen s) {
		
		String fixedrole = s.getParameter("role");
		
		if (episode == null) {
			System.out.println("EPISODE="+s.getParameter("id"));
			episode = new Episode(s.getParameter("id"));
		}
		
		// so we want to load based on device type
		Capabilities caps = s.getCapabilities();
		String dstyle = caps.getDeviceModeName();

		if (gain!=null) gain.screen_new(s.getId());
		
		// try to load special style first if not fallback.
		loadStyleSheet(s,"animate");
		loadStyleSheet(s, dstyle);
		
		
//		if(episodeSelected == false){//needs to be changed to true when episode is selected
//		ArrayList <Episode> episodes = new ArrayList <Episode> ();
//		episodes.add(0,episode);
//		episodes.add(1,episode);
//		episodes.add(2,episode);
//		episodes.add(3,episode);
//		episodes.add(4,episode);
//		episodes.add(5,episode);
//		episodes.add(6,episode);
//		
//		loadContent(s,"episodeslider");
//		
//		
//		Object oEp = s.getProperty("selEpId");
//		if (oEp==null) {
//			s.setProperty("selEpId", 0);
//			oEp = s.getProperty("selEpId");
//		}
//		EpisodePage.setOnScreen(this,s,episodes,(String)oEp);	
//	
////		if (hbbtvMode == false) {
////			loadContent(s, "desktopmanager");
////		}
//	} else {
	//}
		
		if (caps.getDeviceMode() == caps.MODE_HBBTV) {
			// this is the HbbTV main screen
			this.hbbtvMode = true;
			
			// reset for screens that might have run in the past?
			currentTime = 0l;	
			
			s.setRole("mainscreen");
			

			loadMainScreen(s);	
		} else if (screenmanager.hasRole("mainscreen") && (fixedrole == null || !fixedrole.equals("mainscreen"))) {
			// Do we already have a screen in the application that claims to be a mainscreen ?
			System.out.println("Second screen");
			loadSecondScreen(s);
			
			if (timeline == null) {
				initTimeLine();				
			}										
		} else {
			// this is the a non HbbTV main screen
			// reset for screens that might have run in the past?
			currentTime = 0l;			
			
			//System.out.println("Main screen presentation uri = "+episode.getPresentationId());
			s.setRole("mainscreen");
			loadMainScreen(s);			
		}

		loadContent(s, "notification");
	}
	
	/**
	 * Loading the main screen
	 * 
	 * @param s - the main screen
	 */
	private void loadMainScreen(Screen s) {
		System.out.println("EPISODE="+episode);
		Boolean hbbtvMode = false;
		// switch between HTML5 version and HbbTV version
		Capabilities caps = s.getCapabilities();
		if (caps.getDeviceMode() == caps.MODE_HBBTV) {
			hbbtvMode = true;
		}
		System.out.println("HBBTVMODE="+hbbtvMode);
		if (hbbtvMode == false) {
			loadContent(s, "video");
			loadContent(s, "desktopmanager");
			loadContent(s, "mainscreeninfo");
			loadContent(s, "mainscreencard");
			loadContent(s, "copyrightbar");
			loadContent(s, "mainscreenslider");
    		//TODO els  s.setContent("video","setVideo("+ episode.getStreamUri() +")");??
			System.out.println("STREAM="+episode.getStreamuri(3));
			//this.componentmanager.getComponent("video").put("app", "setVideo("+ episode.getStreamUri() +")");
			this.componentmanager.getComponent("video").put("app", "setVideo("+ getLocalVideoUrl() +")");
			
			//this.componentmanager.getComponent("video").put("app", "setPoster("+ episode.getStillsUri() +"/h/0/m/0/sec1.jpg)");
		} else {
			
			loadContent(s, "hbbtvvideo");
			String vurl = "http://images3.noterik.com/linkedtv/raw2.mp4";
			this.componentmanager.getComponent("hbbtvvideo").put("app", "setVideo("+ getLocalVideoUrl() +")");

			//this.componentmanager.getComponent("hbbtvvideo").put("app", "setVideo("+ episode.getStreamuri(3) +")");
			//this.componentmanager.getComponent("hbbtvvideo").put("app", "setPoster("+ episode.getStillsUri() +"/h/0/m/0/sec1.jpg)");
			this.componentmanager.getComponent("hbbtvvideo").put("app", "play()");
		}
		
			//TODO els
			//loadContent(s, "overlay"); // this is just for testing, remove it later!
			//this.componentmanager.getComponent("overlay").put("app", "showQRCode()");
			AppLanguage.loadLanguage(this, language);
				
	}
	
	/**
	 * Loading secondary screens
	 * 
	 * @param s - the secondary screen
	 */
	private void loadSecondScreen(Screen s) {		
		s.setRole("secondaryscreen");
//		loadContent(s, "login");
		loadContent(s, "tablet");
		loadContent(s, "droparea");		
		loadContent(s, "signal");
		loadContent(s, "screens", "menu");
	}
	
	/**
	 * Handling new user join
	 * 
	 * @param s - screen
	 * @param name - name of the user
	 */
	public void onNewUser(Screen s,String name) {
		super.onNewUser(s, name);
		
		if (gain!=null) gain.user_login(name, s.getId());
		
		String body = Slider.loadDataJoined(this,timeline);
		ComponentInterface comp = getComponentManager().getComponent("joinedslider");
		if (comp!=null) {
			comp.put("app", "html("+body+")");
		}
		
		comp = getComponentManager().getComponent("notification");
		comp.put("app", "login("+name+")");
	}
	
	/** 
	 * Handling log out of user
	 * 
	 * @param s - screen
	 * @param name - name of the user
	 */
	public void onLogoutUser(Screen s,String name) {
		super.onLogoutUser(s, name);
		
		if (gain!=null) gain.user_logout(name, s.getId());
		
		String body = Slider.loadDataJoined(this,timeline);
		ComponentInterface comp = getComponentManager().getComponent("joinedslider");
		if (comp!=null) {
			comp.put("app", "html("+body+")");
		}
		comp = getComponentManager().getComponent("notification");
		if (name!=null) {
			comp.put("app", "logout("+name+")");
		}
	}
	
	/**
	 * Handling request from screen
	 * 
	 * @param s - screen
	 * @param from - 
	 * @param msg - the message send
	 */
	public void putOnScreen(Screen s,String from,String msg) {
        int pos = msg.indexOf("(");
        if (pos!=-1) {
        	String command = msg.substring(0,pos);
            String content = msg.substring(pos+1,msg.length()-1);
            if (command.equals("orientationchange")) {
                handleOrientationChange(s,content);
            } else if (command.equals("timeupdate")) {
                handleTimeupdate(s,content);
            } else if (command.equals("gesture")) {
                handleGesture(s,content);
            } else if (command.equals("loaddata")) {
                handleLoadData(s,content);
            } else if (command.equals("loadscreen")) {
                handleLoadScreen(s,content);
            } else if (command.equals("loadblockdata")) {
                handleLoadBlockData(s,content);
            } else if(command.equals("started")){
				started(s, content);
			}else if (command.equals("paused")) {
				paused(s, content);
			}else if(command.equals("stopped")){
				stopped(s, content);
			} else if (command.equals("loadfakeusers")) {
				handleLoadFakeUsers(s);
            } else if (command.equals("bookmark")) {
            	handleBookmark(s,content);
            } else if (command.equals("share")) {
            	handleShare(s,content);
            } else if (command.equals("infoblockfinished")) {
            	handleInfoBlockFinished(s, content);
            } else {
            	super.putOnScreen(s, from, msg);
            }
        }
	}
	
	/**
	 * Handling gestures
	 * 
	 * @param s - screen
	 * @param content - source and type of the gesture
	 */
	private void handleGesture(Screen s,String content) {
		System.out.println("GESTURE FROM SCREEN ="+s.getId()+" "+content);
		String[] params = content.split(",");
		String source = params[0];
		String type = params[1];
		if (source.equals("screens")) {
			if (type.equals("swipeup")) {
				System.out.println("SEND OPEN APPS");
				// we need to start the animation and load the content
				s.putMsg("tablet","app", "openapps()");
			}
		} else if (source.equals("content")) {
			if (type.equals("swipedown")) {
				s.putMsg("tablet","app", "closeapps()");	
			}
		}
	}
	
	/**
	 * Loading new slider data
	 * 
	 * @param s - screen
	 * @param content - slider to load
	 */
	private void handleLoadData(Screen s,String content) {
		if (timeline == null) {
			initTimeLine();	// initialize timeline with all Data	 		
		}	

		float chapterStart = 0f;
		float chapterDuration = 0f;
		
		FsNode chapter = timeline.getCurrentFsNode("chapter", currentTime);
		if (chapter != null) {
			chapterStart = chapter.getStarttime();
			chapterDuration = chapter.getDuration();
		}
		
		switch (sliders.valueOf(content)) {
			case whoslider:
				String body = Slider.loadDataWho(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whoslider","app", "html("+body+")");
				break;
			case whatslider:
				body = Slider.loadDataWhat(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whatslider","app", "html("+body+")");
				break;
			case whereslider:
				body = Slider.loadDataWhere(this,timeline, chapterStart, chapterDuration);
				s.putMsg("whereslider","app", "html("+body+")");
				break;
			case chapterslider:
				body = Slider.loadDataChapter(this,timeline);
				s.putMsg("chapterslider","app", "html("+body+")");
				break;
			case bookmarkslider:
				body = Slider.loadDataBookmark(s,this,timeline);
				s.putMsg("bookmarkslider","app", "html("+body+")");
				break;
			case sharedslider:
				body = Slider.loadDataShared(s,this,timeline);
				s.putMsg("sharedslider","app", "html("+body+")");
				break;
			case joinedslider:
				body = Slider.loadDataJoined(this,timeline);
				s.putMsg("joinedslider","app", "html("+body+")");
				break;
		}
	}
	
	
	
	/**
	 * Loading data for the specified entity
	 * 
	 * @param s - screen
	 * @param content - wrapped content contains type, uid, screen orientation, entity, image
	 */
	private void handleLoadBlockData(Screen s,String content) {
		String params[] = content.split(",");		
		
		if (gain!=null) gain.user_select(s.getUserName(), params[1], s.getId());
		
		String type = params[0].substring(0,params[0].indexOf("_"));
		String id = params[0].substring(params[0].indexOf("_")+6); //compensate for '_block'
		String uid = params[1].substring(params[1].lastIndexOf("/")+1);
		String orientation = params[2];
		String entity = params[3];
		//String image = params[4];
		//String description = content.substring(content.indexOf(",", content.indexOf(image+",")+image.length())+1);
		String description = null;
		String image = null;
		System.out.println("LOAD BLOCKDATA="+content+" TYPE="+type);
		String body = "";
		String color = "";
		//System.out.println("ENTITY = "+entity+" DESCRIPTION = "+description);

		
		String fsType = type;
		if (type.equals("what")) {
			fsType = "object";
		} else if (type.equals("who")) {
			fsType = "person";
		} else if (type.equals("where")) {
			fsType = "location";
		}
		
		FsNode annotation = timeline.getFsNodeById(fsType, Integer.parseInt(id));
		FsNode proxynode  = episode.getEntityFromProxy(annotation.getProperty("locator"));
		if (proxynode!=null) {
			System.out.println("PROXY VALUE DESCRIPTION = "+proxynode.getProperty("description"));
			description = proxynode.getProperty("description");
			image = proxynode.getProperty("thumb");
			if (image!=null && !image.equals("")) {
				// remap to edna
				image = getLocalUrl()+"/edna/external/"+image.substring(7)+"?script=medium";
			}
			System.out.println("PROXY VALUE IMAGE2 = "+image);
		}

		System.out.println("DAN1");
		//FSList enrichmentsList = episode.getEnrichmentsFromAnnotation(annotation);
		FSList enrichmentsList = new FSList();
		System.out.println("DAN2");
		List<FsNode> enrichments = enrichmentsList.getNodes();
		System.out.println("DAN3");
		try {
			switch (blocks.valueOf(type)) {
				case who: color = Slider.colorClasses.get("whoslider");break;
				case what: color = Slider.colorClasses.get("whatslider");break;
				case where: color = Slider.colorClasses.get("whereslider");break;
				case chapter: color = Slider.colorClasses.get("chapterslider");break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}		
		System.out.println("DAN4");
		System.out.println("Orientation = "+orientation);
		
		if (orientation.equals("portrait")) {
			if (enrichments.size() == 1) {
				body += "<iframe id=\"ext_inf\" src=\""+enrichments.get(0).getProperty("locator")+"\"></iframe>";
			} else {			
				//String lang = presentation.getLanguage();
				String lang = "de";
				body += "<iframe id=\"ext_inf\" src=\"http://"+lang+".wikipedia.org/wiki/"+entity+"\"></iframe>";
			}
		} else {
			System.out.println("DAN5");
			body += "<div class=\"triangleshift\"><div class=\""+color+"_large\"></div></div>";
			body += "<div class=\"infoscreen_div_centered\">";
			body += "<p id=\"infoscreen_title\" class=\"info_text_1\">"+entity+"</p>";
			body += "<div>";
			body += "<center>";
			if (description!=null) {
				body += "<p id=\"info_description\">"+description+"</p>";
			} else {
				body += "<p id=\"info_description\"></p>";
			}
			body += "<p class=\"info_text_1\">"+ AppLanguage.getFindOutMore() +"</p>";
			body += "<div>";
			/*
			for (FsNode enrichment : enrichments) {
				body += "<a href=\""+enrichment.getProperty("locator")+"\">";
				body += enrichment.getProperty("type");
				if (!enrichment.getProperty("source").equals("")) {
					body += "-"+enrichment.getProperty("source");
				}
				body += "</a><br/>";
			}
			*/
			body += "<p class=\"info_text_2\">"+ AppLanguage.getRotateScreen()+"</p>";
			body += "</div>";
			body += "</center>";
			body += "</div>";
			body += "</div>";
			body += "<div class=\"infoscreen_div_centered\">";
			System.out.println("DAN6");
			if (image!=null) {
				body += "<img id=\"infimg\" src=\""+image+"\"/>";
			}
			body += "</div>";
			System.out.println("DAN7");
		}

		System.out.println("DATA="+body);
		s.setContent("infoscreen",body);
	}
	
	/**
	 * Highlighting blocks in the slider when in play range
	 * 
	 * @param s - screen
	 * @param content - video time
	 */
	private void handleTimeupdate(Screen s,String content) {		
		if (timeline == null) {
			initTimeLine();				
		}	
		
		// timeout system for menu
		checkAutoHideMenu(s);
		
		
		String[] t = content.split(":");
		long ms = Long.parseLong(t[0])*1000;
		int newChapter = currentChapter;
		float chapterStart = 0f;
		float chapterDuration = 0f;
		currentTime = ms;
		
		
		//TODO els
		/*
		int qrCodeDisplayTime = 20000;
		if(currentTime >= qrCodeDisplayTime){
			ComponentInterface compMainScreenOverlay = getComponentManager().getComponent("overlay");
			if (compMainScreenOverlay!=null) {
				this.componentmanager.getComponent("overlay").put("app", "hideQRCode()");
			}
		}
		*/
		//end els TODO
		
		
		MainCurrent.setOnScreen(this,s,timeline,(int)currentTime);
		Object o = s.getProperty("selid");
		if (o==null) {
			MainSlider.setOnScreen(this,s,timeline,(int)currentTime,null,false);
		} else {
			MainSlider.setOnScreen(this,s,timeline,(int)currentTime,(String)o,false);	
		}
		
		
		
		ComponentInterface comp = getComponentManager().getComponent("chapterslider");
		if (comp!=null) {
			int blocknumber = timeline.getCurrentFsNodeNumber("chapter", ms);
			if (blocknumber!=-1) {
				newChapter = blocknumber;
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("chapterslider"))+")");
			}
			chapterStart = timeline.getCurrentFsNode("chapter", ms).getStarttime();
			chapterDuration = timeline.getCurrentFsNode("chapter", ms).getDuration();
		}

		comp = getComponentManager().getComponent("whoslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWho(this,timeline, chapterStart, chapterDuration);
				//System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("person", ms);
			if (blocknumber!=-1) {				
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whoslider"))+")");
			}
		}
		
		comp = getComponentManager().getComponent("whatslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWhat(this,timeline, chapterStart, chapterDuration);
				//System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("object", ms);
			if (blocknumber!=-1) {
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whatslider"))+")");
			}
		}
		
		comp = getComponentManager().getComponent("whereslider");
		if (comp!=null) {
			if (currentChapter != newChapter) {
				String body = Slider.loadDataWhere(this,timeline, chapterStart, chapterDuration);
				//System.out.println("Sending new layer data: "+body);
				comp.put("app", "html("+body+")");
			}
			
			int blocknumber = timeline.getCurrentFsNodeNumber("location", ms);
			if (blocknumber!=-1) {
				comp.put("app", "highlightblock("+(blocknumber+1+","+Slider.colorClasses.get("whereslider"))+")");
			}
		}
		currentChapter = newChapter;
		
		FsNode chapter = timeline.getCurrentFsNode("chapter", ms);
		FSList annotationsList = episode.getAnnotationsFromChapter(chapter);
		List<FsNode> annotations = annotationsList.getNodes();
		List<GAINObjectEntity> entityList = new ArrayList<GAINObjectEntity>();
		for (FsNode annotation : annotations) {
			GAINObjectEntity entity = new GAINObjectEntity(annotation);
			entityList.add(entity);
		}
		
		
		//log("epDuration: " + episode.getDuration() +" = milisec"+ ms);
		
//		if(episode.getDuration() <= ms){
//			log("epDuration: " + episode.getDuration() +" = milisec"+ ms);
//			if (hbbtvMode == false) {
//				//s.putMsg("hbbtvvideo","app","seek("+ 0 +")");
//				this.componentmanager.getComponent("hbbtvvideo").put("app", "play()");
//				//s.setProperty("selid", 1);
//			} 
//			//else {
//			//	s.putMsg("video","app","seek("+ 0 +")");
//			//}
//			
//			
//		}
		
		
		if (gain!=null) {
			gain.updateEntities(entityList);
		//	gain.sendKeepAliveRequest();
		}
	}
	
	/**
	 * Loading screen
	 * 
	 * @param s - screen
	 * @param content - screen name
	 */
	private void handleLoadScreen(Screen s,String content) {
	removeCurrentSliders(s);
	System.out.println("LOAD SCREEN="+content);
	
	if (content.equals("screens_episode")) { 
		//better Solution eg. read names from properties-File?? Source/externalize Strings
		s.setContent("content","");
		addSlider(s, "content", "whoslider", AppLanguage.getWhoSliderName());//Name is being used
		addSlider(s, "content", "whatslider", AppLanguage.getWhatSliderName());
		addSlider(s, "content", "whereslider", AppLanguage.getWhereSliderName());
		addSlider(s, "content", "chapterslider", AppLanguage.getChapterSliderName());
		loadContent(s, "sliderevents");
	} else if (content.equals("screens_overview")) {
		s.setContent("content","");
	} else if (content.equals("screens_bookmarks")) {
		s.setContent("content","");
		addSlider(s, "content", "bookmarkslider", AppLanguage.getBookmarkSliderName());
		addSlider(s, "content", "sharedslider", AppLanguage.getSharedSliderName());	
		loadContent(s, "sliderevents");
	} else if (content.equals("screens_social")) {
		s.setContent("content","");
		addSlider(s, "content", "joinedslider", AppLanguage.getJoinedSliderName());	
		loadContent(s, "sliderevents");
	}
}

/**
 * Add slider, replaces some names in javascript files to match the slider type
 * 
 * @param s - screen
 * @param target - target to add slider
 * @param slider - type of slider
 * @param sliderName - type of sliderName
 * 
 */
private void addSlider(Screen s, String target, String slider, String sliderName){
	String body = FsFileReader.getFileContent(this, slider, getComponentManager().getComponentPath("slider"));
			
	body = body.replaceAll("/slider", slider);
	body = body.replaceAll("/NAME", sliderName.toUpperCase());
	//Name of the Slider 
	//slider.substring(0, slider.indexOf("slider")).toUpperCase()
	body = body.replaceAll("/class", Slider.colorClasses.get(slider));
	body = body.replaceAll("/position", Slider.positions.get(slider));
	
	s.addContent(target, body);
	
	body = FsFileReader.getFileContent(this, slider, componentmanager.getComponentJS("slider"));
	body = body.replaceAll("/sliderid", slider.substring(0, slider.indexOf("slider")));
	body = body.replaceAll("/slider", slider);
	body = body.replaceAll("/Slider", slider.substring(0, 1).toUpperCase() + slider.substring(1));
	
	s.setScript(target, body);
	BasicComponent sliderComponent = (BasicComponent)this.componentmanager.getComponent(slider);
	if(sliderComponent==null){
		sliderComponent = new BasicComponent(); 
		sliderComponent.setId(slider);
		sliderComponent.setApplication(this);
	}
	this.addComponentToScreen(sliderComponent, s);

	//sliderComponent.put("app", "setlanguage("+presentation.getLanguage()+")");
	sliderComponent.put("app", "setlanguage(de)");
}

	
	
	/**
	 * 
	 * 
	 * @param s - screen
	 */
	public void removeCurrentSliders(Screen s){
		Iterator<String> it = s.getComponentManager().getComponents().keySet().iterator();
		while(it.hasNext()){
			String comp = it.next();
			if(isSlider(comp)) this.removeComponentFromScreen(comp, s);
		}
	}
	
	/**
	 * Find out if name is a slider or not
	 * 
	 * @param name - the name of the component
	 * @return true if name is a slider, otherwise false
	 */
	private boolean isSlider(String name){
		try{
			sliders.valueOf(name);
			System.out.println(name + ": is a slider");
			return true;
		}catch(IllegalArgumentException e){
			System.out.println(name + ": is not a slider");
			return false;
		}
	}

	/**
	 * Update style sheet according to second screen orientation
	 * 
	 * @param s - screen
	 * @param o - the orientation
	 */
	private void handleOrientationChange(Screen s,String o) {
		// set the changed capabilities
		Capabilities caps = s.getCapabilities();
		caps.addCapability("orientation", o); // set the new orientation
		if (gain!=null) gain.screen_orientation(s.getId(), o);
		
		// reload the style sheet (should we not remove the old?)
		String dstyle = caps.getDeviceModeName();
		loadStyleSheet(s,dstyle,appname);
	}
	
	/**
	 * Handling bookmarks
	 * 
	 * @param s - screen
	 * @param content - the block to bookmark
	 */
	private void handleBookmark(Screen s,String content) {
		// get the user of this screen
		String username = s.getUserName();
		System.out.println("BOOKMARK="+content+" USER="+username);
		if (username==null) {
			s.putMsg("notification","app","show(to bookmark please login)");
		}
		User u = getUserManager().getUser(username);
		if (u!=null) {
			if (gain!=null) gain.user_bookmark(username, content, s.getId());
			u.addBookmark(content);
			s.putMsg("notification","app","show(bookmarked "+u.getBookmarks().size()+")");
		}		
	}
	
	/**
	 * Handle sharing
	 * 
	 * @param s - screen
	 * @param content - the block to share
	 */
	private void handleShare(Screen s,String content) {
		// get the user of this screen
		String username = s.getUserName();
		System.out.println("SHARE="+content+" USER="+username);
		if (username==null) {
			s.putMsg("notification","app","show(to share please login)");
		}
		
		for(Iterator<String> iter = getUserManager().getUsers(); iter.hasNext(); ) {
			String uname = (String)iter.next();
			if (uname.equals(username)) {
				s.putMsg("notification","app","show(share send out)");
			} else {
				User u = getUserManager().getUser(uname);
				if (u!=null) {
					u.addShared(content);
					Iterator<String> it = this.getScreenManager().getScreens().keySet().iterator();
					while(it.hasNext()){
						String next = (String) it.next();
						Screen nscreen = getScreenManager().get(next);
						String nname = nscreen.getUserName();
						if (nname!=null && !nname.equals(username)) {
							nscreen.putMsg("notification","app","show("+username+" shared with you)");
							String body = Slider.loadDataShared(nscreen,this,timeline);
							nscreen.putMsg("sharedslider","app", "html("+body+")");
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get a nicely formatted time string
	 * 
	 * @param seconds - number of seconds
	 * @return a formatted time string
	 */
	public String getTimeCodeString(double seconds) {
		String result = null;
		int sec = 0;
		int hourSecs = 3600;
		int minSecs = 60;
		int hours = 0;
		int minutes = 0;
		while (seconds >= hourSecs) {
			hours++;
			seconds -= hourSecs;
		}
		while (seconds >= minSecs) {
			minutes++;
			seconds -= minSecs;
		}
		sec = new Double(seconds).intValue();
		result = minutes+":";
		if (sec<10) {
			result += "0"+sec;
		} else {
			result += sec;
		}
		return result;
	}
	
	/**
	 * 
	 */
	private void started(Screen s, String videoTime){
		if (hbbtvMode == false) {
			this.componentmanager.getComponent("video").put("app", "started()");
		} else {
			log("HBBTV");
			this.componentmanager.getComponent("hbbtvvideo").put("app", "started()");
			
			//TODO els
			loadContent(s, "hbbtvmanager");
			loadContent(s, "mainscreeninfo");
			loadContent(s, "mainscreencard");
			loadContent(s, "copyrightbar");
			loadContent(s, "mainscreenslider");
			//this.componentmanager.getComponent("mainscreeninfo").put("app", "showMainScreenInfo()");
		}
		if (gain!=null) gain.player_play(s.getId(), episode.getMediaResourceId(), videoTime);
	}
	
	/**
	 * 
	 */
	private void paused(Screen s, String videoTime){
		if (hbbtvMode == false) {
			this.componentmanager.getComponent("video").put("app", "started()");
		} else {
			this.componentmanager.getComponent("hbbtvvideo").put("app", "started()");
		}
		if (gain!=null) gain.player_pause(s.getId(), episode.getMediaResourceId(), videoTime);
	}
	
	/**
	 * 
	 */
	private void stopped(Screen s, String videoTime){
		if (hbbtvMode == false) {
			this.componentmanager.getComponent("video").put("app", "stopped()");
		} else {
			this.componentmanager.getComponent("hbbtvvideo").put("app", "stopped()");
		}
		if (gain!=null) gain.player_stop(s.getId(), episode.getMediaResourceId(), videoTime);
	}
	
	
	/**
	 * Loading fake users for now
	 * 
	 * @param s - screen
	 */
	private void handleLoadFakeUsers(Screen s) {
		String body = "<table><tr><td><div class=\"fakeuser\" id=\"user_bert\"><p>Bert</p><img src=\"/eddie/apps/linkedtvhbbtv/img/people/bert.png\"></div>";
		body += "</td><td><div class=\"fakeuser\" id=\"user_anne\"><p>Anne</p><img src=\"/eddie/apps/linkedtvhbbtv/img/people/anne.png\"></div>";
		body += "</td><td><div class=\"fakeuser\" id=\"user_ralph\"><p>Ralph</p><img src=\"/eddie/apps/linkedtvhbbtv/img/people/ralph.png\"></div>";
		body += "</td></tr><tr><tr><td><div class=\"fakeuser\" id=\"user_nina\"><p>Nina</p><img src=\"/eddie/apps/linkedtvhbbtv/img/people/nina.png\"></div></td></tr></table>";
		s.putMsg("tablet","app", "fakeusershtml("+body+")");
		s.loadScript("tablet", "tablet/fakeuserevents.js", this);
	}
	
	/**
	 * Initialize timeline with annotations and chapters
	 */
	private void initTimeLine() {
		timeline = new FsTimeLine();
		
		timeline.removeNodes();
		
		FsTimeTagNodes results = new FsTimeTagNodes();
		
		FSList annotationsList = episode.getAnnotations();
		List<FsNode> annotations = annotationsList.getNodes();	
		for (FsNode annotation : annotations) {
			results.addNode(annotation);
		}
		
		FSList chaptersList = episode.getChapters();
		List<FsNode> chapters = chaptersList.getNodes();	
		for (FsNode chapter : chapters) {
			results.addNode(chapter);
		}
		
		timeline.addNodes(results.getAllNodes());
		
	}
	

	
	private void handleInfoBlockFinished(Screen s, String params) {
		System.out.println("Received info block finished with following params "+params);
		String[] parameters = params.split(",");
		if (parameters != null && parameters.length == 2) {
			if (gain!=null) gain.user_viewtime(s.getUserName(), parameters[1], s.getId(), parameters[0]);
		}
	}
	
	 public void keypressed(Screen s,String content) {
		 	// reset shutdown timer
		 	setScreenPropertyInt(s,"mainmenulastuse", (int)(new Date().getTime()/1000));
		 
	    	try {
	    		int keycode = Integer.parseInt(content);
	    		switch (keycode) {
					case RemoteControl.REMOTEKEY_ENTER :
						Object onscreen = s.getProperty("cardonscreen");
				    	if (onscreen!=null) {
				    		hideMainCard(s);
				    		vcontinue(s);
				    	}
						gotoChapter(s);
					break;
					case RemoteControl.REMOTEKEY_RED :
						break;
					case RemoteControl.REMOTEKEY_GREEN :
						break;
					case RemoteControl.REMOTEKEY_YELLOW :
						break;
					case RemoteControl.REMOTEKEY_BLUE :
						toggleMainScreenInfo(s);
						break;
					case RemoteControl.REMOTEKEY_RIGHT :
						onscreen = s.getProperty("cardonscreen");
				    	if (onscreen==null) {
				    		selectNextChapter(s);
				    	} else {
							int pos = getScreenPropertyInt(s, "cardonscreenpos");
				    		s.setProperty("cardonscreenpos",""+(pos+1));
				    		fillMainCard(s);
				    	}
						break;
					case RemoteControl.REMOTEKEY_LEFT :
						onscreen = s.getProperty("cardonscreen");
				    	if (onscreen==null) {
				    		selectPrevChapter(s);
				    	} else {
							int pos = getScreenPropertyInt(s, "cardonscreenpos");
							if (pos>0) { 
								s.setProperty("cardonscreenpos",""+(pos-1));
								fillMainCard(s);
							}
				    	}
						break;
					case RemoteControl.REMOTEKEY_UP :
				    	onscreen = s.getProperty("slideronscreen");
				    	if (onscreen==null) {
				    		showMainSlider(s);
				    	} else {	    	
				    		s.setProperty("cardonscreenpos","0");
				    		fillMainCard(s);
				    		showMainCard(s);
				    		pause(s);
				    	}
						break;
					case RemoteControl.REMOTEKEY_DOWN :
				    	onscreen = s.getProperty("cardonscreen");
				    	if (onscreen==null) {
				    		hideMainSlider(s);
				    	} else {
				    		hideMainCard(s);
				    		vcontinue(s);
				    	}
						break;
	    		}
	    	} catch(Exception e) {
	    		log("illigal keyPressed");
	    	}
	    }
	 
	 
	    private void selectNextChapter(Screen s) {
	    	int nrOfDispChapters = episode.getChapters().size();
	    	log("els nrOfDispChapters: "+ nrOfDispChapters);
	    	Object onscreen = s.getProperty("slideronscreen");
	    	if (onscreen==null) {
	    		s.putMsg("mainscreenslider","app","show()");
	    		s.setProperty("slideronscreen","true");
	    	}
	    	
	    	
	    	Object o = s.getProperty("selid");
	    	if (o==null) {	
	    		log("1");
	    		s.setProperty("selid", "1");
				MainSlider.setOnScreen(this,s,timeline,(int)currentTime,"1",true);
	    	} else {
	    		int pos = Integer.parseInt((String)o);
	    		if (pos>=nrOfDispChapters) {
	    			pos = 12; //TODO
	    			s.setProperty("selid", ""+pos);
	    			MainSlider.setOnScreen(this,s,timeline,(int)currentTime,""+pos,true);
	    		}else{
	    			s.setProperty("selid", ""+(pos+1));
	    			log(""+(pos+1));
	    			MainSlider.setOnScreen(this,s,timeline,(int)currentTime,""+(pos+1),true);
	    		}
	    		//if((pos+1)%nrOfDispChapters){
		    	
		    	
		    	
	    	}
	    	
	    	
	    }
	    
	    private void selectPrevChapter(Screen s) {
	    	Object onscreen = s.getProperty("slideronscreen");
	    	if (onscreen==null) {
	    		s.putMsg("mainscreenslider","app","show()");
	    		s.setProperty("slideronscreen","true");
	    	}
	    	
	    	
	    	Object o = s.getProperty("selid");
	    	if (o==null) {	
	    		log("1");
	    		s.setProperty("selid", "1");
				MainSlider.setOnScreen(this,s,timeline,(int)currentTime,"1",true);
	    	} else {
	    		int pos = Integer.parseInt((String)o);
	    		if (pos<2) return;
	    		s.setProperty("selid", ""+(pos-1));
	    		log(""+(pos-1));
				MainSlider.setOnScreen(this,s,timeline,(int)currentTime,""+(pos-1),true);
	    	}
	    }
	    
	 
	    private void gotoChapter(Screen s) {
	    	Object o = s.getProperty("selid");
	    	if (o==null) return;
	    	
	    	s.setProperty("currentChapterNr",o);
    		hideMainSlider(s);
	    
	    	FsNode chapternode = null;
			try {
				chapternode = timeline.getFsNodeById("chapter",Integer.parseInt((String)o));
			} catch(Exception e) {} 

			if (chapternode!=null) {
				// lets seek to its end time
				float st = (chapternode.getStarttime()/1000)+1;
				if (s.getCapabilities().getDeviceMode()==s.getCapabilities().MODE_HBBTV) {
					s.putMsg("hbbtvvideo","app","seek("+ st +")");
					//this.componentmanager.getComponent("hbbtvvideo").put("app", "seek("+ st +")");
				} else {
					s.putMsg("video","app","seek("+ st +")");
					//this.componentmanager.getComponent("video").put("app", "seek("+ st +")");
				}
			}
	    }
	    
	 
	    private void toggleMainScreenInfo(Screen s) {
	    	Object o = s.getProperty("infoactive");
	    	if (o!=null) {
	    		if (((String)o).equals("true")) {
	    			s.setProperty("infoactive","false");	
	    			this.componentmanager.getComponent("mainscreeninfo").put("app", "hide()");
	    		} else {
	    			s.setProperty("infoactive","true");	
	    			this.componentmanager.getComponent("mainscreeninfo").put("app", "show()");	
	    		}
	    	} else {
    			s.setProperty("infoactive","false");	
    			this.componentmanager.getComponent("mainscreeninfo").put("app", "hide()");
	    	}
	    }
	    
	    
	    private void checkAutoHideMenu(Screen s) {
	    	int now = (int)(new Date().getTime()/1000);
	    	int old = getScreenPropertyInt(s,"mainmenulastuse");
	    	if ((now-old)>10) {
				Object onscreen = s.getProperty("cardonscreen");
		    	if (onscreen==null) {
		    		hideMainSlider(s);
		    	}
	    	}
	
	    }
	    
	    public void setScreenPropertyInt(Screen s,String name,int value) {
	    	s.setProperty(name, ""+value);
	    }
	    
	    public int getScreenPropertyInt(Screen s,String name) {
	    	try {
	    		return Integer.parseInt((String)s.getProperty(name));
	    	} catch(Exception e) {}
	    	return -1;
	    }
	    
	    private void hideMainSlider(Screen s) {
	    	s.putMsg("mainscreenslider","app","hide()");
	    	s.setProperty("slideronscreen",null);
			Object onscreen = s.getProperty("cardonscreen");
	    	if (onscreen!=null) {
	    		hideMainCard(s);
	    	}
	    }
	    
	    private void showMainSlider(Screen s) {
	    	s.putMsg("mainscreenslider","app","show()");
	    	s.setProperty("slideronscreen","true");
	    }
	    

	    private void showMainCard(Screen s) {
	    	s.putMsg("mainscreencard","app","show()");
	    	s.setProperty("cardonscreen","true");
	    }
	    
	    private void hideMainCard(Screen s) {
	    	s.putMsg("mainscreencard","app","hide()");
	    	s.setProperty("cardonscreen",null);
	    }
	    
	    private void fillMainCard(Screen s) {
	    	
			FsNode node = null;
			Object o = s.getProperty("selid");
			if(o == null){
				s.setProperty("selid", "1");
			}
			int nodeId = getScreenPropertyInt(s, "selid");
			node = timeline.getFsNodeById("chapter", nodeId);
			
			FSList annotationsList = episode.getAnnotationsFromChapter(node);
			//List<FsNode> annotations = annotationsList.getNodes();
			
			List<FsNode> annotations = FilterHbbtvAnnotations(annotationsList);
			/*
			for (FsNode annotation : annotations) {
				body+=annotation.getProperty("locator")+"\n";
			}
			*/
			int pos = getScreenPropertyInt(s, "cardonscreenpos");
			FsNode annotation = annotations.get(pos);
			
			String title = "( "+(pos+1)+" / "+annotations.size()+" )<br />";
			String description = "";
			String thumb = "";
			String eurl = annotation.getProperty("locator"); // we want stuff from the proxy
			
			FsNode proxynode = episode.getEntityFromProxy(eurl);
			if (proxynode!=null) {
				title  = "( "+(pos+1)+" / "+annotations.size()+" ) "+proxynode.getProperty("type")+" : "+proxynode.getProperty("label");
				description = proxynode.getProperty("description");
				String img = proxynode.getProperty("thumb");
				if (img!=null && !img.equals("")) {
					// remap to edna
					img = getLocalUrl()+"/edna/external/"+img.substring(7)+"?script=medium";
					thumb = "<img id=\"cardthumb\" src=\""+img+"\" />";
				}
				System.out.println("ENT="+eurl);			
				System.out.println("THUMB="+thumb);
			}
		    s.setContent("cardpicture",thumb);
	    	s.setContent("cardtitle",title);
	    	s.setContent("carddescription",description);
	    }
	    
	    private void fillMainCard_old(Screen s) {
	    	
			FsNode node = null;
			Object o = s.getProperty("selid");
			if(o == null){
				s.setProperty("selid", "1");
			}
			int nodeId = getScreenPropertyInt(s, "selid");
			node = timeline.getFsNodeById("chapter", nodeId);
			
			FSList enrichmentsList = episode.getEnrichmentsFromAnnotation(node); // are chapterannotation treated the same ??
			List<FsNode> enrichments = enrichmentsList.getNodes();
	    	
			FSList annotationsList = episode.getAnnotationsFromChapter(node);
			List<FsNode> annotations = annotationsList.getNodes();
			
	    	//int pos = Integer.parseInt((String)o);
			//body += "<p id=\"infoscreen_title\" class=\"info_text_1\">"+entity+"</p>";
			String body = "";
		
			body += "<div class=\"infoscreencard_div_centered\">";
			body += "<p id=\"maincard_title\" class=\"maincard_text_1\">";
			body += AppLanguage.getChapterSliderName();
	    	body += " ";
	    	body += nodeId;
			body += ": "+node.getProperty("title");
			body += "</p>";
			body+="<p class=\"maincard_timecode\">" + AppLanguage.getTime() +": " +getTimeCodeString(node.getStarttime()/1000) + "-"+ getTimeCodeString((node.getStarttime()+node.getDuration())/1000) +"</p>";
			body += "<p id=\"maincard_description\">";
			if(node.getProperty("description") != null){//TODO
				body += node.getProperty("description");//TODO
				log("Desc: " + node.getProperty("description"));//TODO
			}else {
				int annoCount = 0;
				int maxAnnoToDisplas = 7; //TODO
				for (FsNode annotation : annotations) {
					if(annoCount < maxAnnoToDisplas){
					body += "<p>";
					body +=  annotation.getProperty("title");
					body += "</p>";
					annoCount++;
					}
				}
				
			}
			body += "</p><div>";
			for (FsNode enrichment : enrichments) {
				body += "<a href=\""+enrichment.getProperty("locator")+"\">";
				body += enrichment.getProperty("type");
				if (!enrichment.getProperty("source").equals("")) {
					body += "-"+enrichment.getProperty("source");
				}
				body += "</a><br/>";
			}
			//body += "<p class=\"info_text_2\">Rotate your screen to read more</p>";
			body += "</div>";
			body += "</div>";
			body += "</div>";
			
			//body += "<div class=\"infoscreen_div_centered\">";
			//body += "<img id=\"infimg\" src=\""+image+"\"/>";
			//body += "</div>";
	    	
		
	    	s.setContent("mainscreencard",body);
	    }
	    
	    public void pause(Screen s) {
			if (s.getCapabilities().getDeviceMode()==s.getCapabilities().MODE_HBBTV) {
				s.putMsg("hbbtvvideo","app","pause()");
			} else {
				s.putMsg("video","app","pause()");
			}
	    }
	    
	    public void vcontinue(Screen s) {
			if (s.getCapabilities().getDeviceMode()==s.getCapabilities().MODE_HBBTV) {
				s.putMsg("hbbtvvideo","app","continue()");
			} else {
				s.putMsg("video","app","continue()");
			}
	    }

		
		private List<FsNode> FilterHbbtvAnnotations(FSList annotations) {
			List<FsNode> nodes = annotations.getNodes();
			List<FsNode> results = new ArrayList<FsNode>();
			for (FsNode node : nodes) {
				if (node != null) {
					String eurl = node.getProperty("locator"); // we want stuff from the proxy
					System.out.println("FILTER URL="+eurl);
					FsNode proxynode = episode.getEntityFromProxy(eurl);
					System.out.println("FILTER NODE="+proxynode);
					if (proxynode!=null) {
						String label = proxynode.getProperty("label");
						System.out.println("FILTER LABEL="+label);
						if (label!=null) {
							results.add(node);
						}
					}
				}
			}
			return results;
		}
		
		public String getLocalUrl() {
			//return "http://a1.noterik.com";
			return "http://192.168.1.98:8080";
		}
		
		public String getLocalScreenshotUrl() {
		//	return "http://a1.noterik.com/eddie/images/";
			return "http://192.168.1.98:8080/eddie/images/";
		}
		
		public String getLocalVideoUrl() {
			//return "http://images3.noterik.com/linkedtv/raw2.mp4";
			return "http://192.168.1.98:8080/eddie/raws/raw2.mp4";
		}

	    

}
