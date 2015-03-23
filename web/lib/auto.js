$(document).ready(function() {
			//TV Samsung
    		if(typeof Common === 'object'){
    			$.getScript("lib/samsung/Main.js",function(data,textStatus,jqxhr){Main.onLoad();});
    		}
		});