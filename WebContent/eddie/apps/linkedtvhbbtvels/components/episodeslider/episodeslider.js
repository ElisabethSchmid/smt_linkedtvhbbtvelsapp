function Episodeslider(options) { //TODO
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
				default:
					alert('unhandled msg in episodeslider.html : '+msg+' ('+command+','+content+')'); 
			}
		}

	return self;
}