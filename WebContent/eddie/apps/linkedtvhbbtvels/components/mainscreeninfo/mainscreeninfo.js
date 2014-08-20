/* 
* overlay.js
* 
* Copyright (c) 2013 Noterik B.V.
* 
* This file is part of smt_trafficlightoneapp, an app for the multiscreen toolkit 
* related to the Noterik Springfield project.
*
* smt_trafficlightoneapp is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* smt_trafficlightoneapp is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with smt_trafficlightoneapp.  If not, see <http://www.gnu.org/licenses/>.
*/


function Mainscreeninfo(options) {
	var self = {};
	var settings = {};
	$.extend(settings, options);
	var qrCode = document.getElementById("mainScreenInfo");
	
	setInterval(function() { eddie.putLou('','displaymainscreentext()'); }, 1000);
	
	self.putMsg = function(msg){
	
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(var i=0; i<command.length; i++) {
			switch(command[i]) { 
				case 'showMainScreenInfo':
					self.showMainScreenInfo();
					break;
				case 'hideMainScreenInfo':
					self.hideMainScreenInfo();
					break;
				case 'html':
	  				self.putContent(content);
	  				break; 
				default:
					alert('unhandled msg in hbbtvvideo.html : '+msg+' ('+command+','+content+')'); 
			}
		}
	}
	
	self.showMainScreenInfo = function() {
		$("#mainScreenInfo").show();
		//eddie.putLou('notification','show(showMainScreenInfo)');
	}
	
	self.hideMainScreenInfo = function() {
		$("#mainScreenInfo").hide();
		//eddie.putLou('notification','show(hideMainScreenInfo)');
	}
	
	self.putContent = function(content){
		var contentId = $(content).attr('id')
		$('#contentId').html(content);
		//eddie.putLou('notification','show(hideMainScreenInfo)');
	}
	
	//eddie.putLou('notification','show(displaymainscreentext)');
	eddie.putLou("","displaymainscreentext()");
	return self;
}
