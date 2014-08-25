function Mainscreenslider(options) {
	var self = {};
	var settings = {};
	$.extend(settings, options);
	
	self.putMsg = function(msg){
	
		try{
			var command = [msg.target[0].class];
		}catch(e){
			command = $(msg.currentTarget).attr('class').split(" ");
		}
		var content = msg.content;
		for(var i=0; i<command.length; i++) {
			switch(command[i]) { 
				case 'show':
					$('#mainscreenslider').animate({bottom:'0px'},250,function() { self.animDone('in'); });
					break;
				case 'hide':
					$('#mainscreenslider').animate({bottom:'-120px'},250,function() { self.animDone('out'); });
					break;
				default:
					alert('unhandled msg in mainscreenslider.html : '+msg+' ('+command+','+content+')'); 
			}
		}
	}
	
	self.animDone = function(step) {
		if (step=='in') {
   		} else if (step=='out') {
  		}
	}
	
	return self;
}
