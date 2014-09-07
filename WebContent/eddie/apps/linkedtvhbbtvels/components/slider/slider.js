/* 
* slider.js
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


function /Slider(options){
	var self = {};
	var queue = [];
	var queueRunning = false;
	var settings = {
		'startdragx': 0
	};
	var language;
	$.extend(settings, options);


	
	self.putMsg = function(msg){

		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(i=0;i<command.length;i++){
			switch(command[i]) { 
				case 'highlightblock':
					self.handleHighBlock(content);
	  				break;
	  			case 'html':
	  				self.putContent(content);
	  				break;
	  			case 'setlanguage':
	  				self.setlanguage(content);
	  				break;
				default:
					console.log('unhandled msg in /slider.js : '+command); 
			}
		}
	}

	self.animDone = function(){
	}

	self.handleHighBlock = function(content){
		var sc = content.split(",");
        var block = sc[0];
        var colorclass = sc[1];
        var color;

        switch (colorclass) {
                case "triangleblue":
                        color = "#1EC9F3";
                        break;
                case "trianglegreen":
                        color = "#5FD07B";
                        break;
                case "trianglepink":
                        color = "#B392B7";
                        break;
                case "trianglebrown":
                		color = "#D2A732";
                		break;
        }
		
		$('div[id^="/sliderid_block"]').each(function(index) {
			var blockid = $(this).attr("id").match(/\d+$/);
			$(this).css({background: (block == blockid) ? color : "#222222"});			
		});
	}

	self.putContent = function(content){
		$('#/slider').html(content);
		components.sliderevents.addDragEvents('/sliderid');
	}
	
	self.setlanguage = function(line) {
		language = line;
	}
	
	
	eddie.putLou("","loaddata(/slider)");
	return self;
}


if(!window['components'])
	window['components'] = {};

components['/slider'] = /Slider();

function /slider_putMsg(msg) {
	components./slider.putMsg(msg);
}