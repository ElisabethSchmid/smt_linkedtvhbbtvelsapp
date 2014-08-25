var Desktopmanager = function(options){
	var self = {};
	var settings = {};

	$.extend(settings, options);
	eddie.log('keyboard started');
    
	document.addEventListener("keydown", keyPressed, false);
	
	return self;
}

function keyPressed(e) {
	eddie.log('keypressed='+e.keyCode);
	eddie.putLou('', 'keypressed('+e.keyCode+')');
}