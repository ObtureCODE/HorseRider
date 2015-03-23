		var FPS = 40;
		var FPS_SUN = 5;
		var FPS_CLOUDS = 30;
		var SPEED_PANNING = 15;
		var TIME_HITO_1 = 10;
		var TIME_HITO_2 = TIME_HITO_1+5;
		var STEP_CLOCK = 2000;
		var TIME_CELEBRATION = 5000
		var clock = 0;
		var timer_clock;
    	var MARGIN_FIRTS = 100;
		var TIMEANIMATION = STEP_CLOCK;
		var TIMEANIMATIONFAST = 700;
		
		var kRedPlayer = 0;
		var kGreenPlayer = 1;
		var kBluePlayer = 2;
		var kYellowPlayer = 3;
		var arrayColors = new Array(kRedPlayer,kGreenPlayer, kBluePlayer, kYellowPlayer);
		
    	var arrayplayers = new Array();
		var haswinner = false;
    	var primerJugadorLlego = true;


		var SOCKETENDPOINT = "ws://horserider.obturecode.com/ws/";
		

    	
    	function SortByAvance(a, b){
  			var aAvance = a.avance;
			var bAvance = b.avance; 
			return ((aAvance > bAvance) ? -1 : ((aAvance < bAvance) ? 1 : 0));
		}
    	
    
    	
    	function mostrarGanador(){
    		haswinner = true;
    		ganador = arrayplayers[0];
    		parent_width = parseInt($("#scene").css("width")) ;
	   		margin = (parent_width/2.0)-(parseInt($("#item"+ganador.id).css("width"))/2);
   			$("#item"+ganador.id).animate({
			    marginLeft: margin,
			  	}, TIMEANIMATION, function() {
    	
			});

    		for(i=1;i<arrayplayers.length;i++){
    			$("#item"+arrayplayers[i].id).animate({
					    marginLeft: 0-parseInt($("#item"+arrayplayers[i].id).css("width")),
				  	}, TIMEANIMATIONFAST, function() {
				});
    		}
  		
  			var clase;
  			var clasetexto;
  			switch (ganador.color){
  				case kRedPlayer:
  					clase = "cuadraRed";
  					clasetexto = "color_rojo";
  					break;
  				case kGreenPlayer:
  					clase = "cuadraGreen";
  					clasetexto = "color_verde";
  					break;
  				case kBluePlayer:
  					clase = "cuadraBlue";
  					clasetexto = "color_azul";
  					break;
  				case kYellowPlayer:
  					clase = "cuadraYellow";
  					clasetexto = "color_amarillo";
  					break;
  			}
  			$("#endmark").css("display","block");
  			$("#endmark").animate({
			    left: "-10%",
			  	}, 2500, function() {
			    	$("#nameplayerWinner").parent().addClass(clase).addClass(clasetexto);
			  		$("#nameplayerWinner").html(ganador.name);
			  		$('#winnerBox').animate({ marginTop: 30 }, // what we are animating
						    {
						        duration: 1000, // how fast we are animating
						        easing: 'easeOutBounce', // the type of easing
						        complete: function() { // the callback
						        }
						    });
					$("#playersAdded").animate({ marginTop: -40 }, // what we are animating
						    {
						        duration: 300, // how fast we are animating
						        easing: 'easeInQuad', // the type of easing
						        complete: function() {}
					});

			});
  			
    	}
    	
    	function deployScoreboard(){
    		//playersAdded
    		$('#playersAdded').animate({ top: 0 }, // what we are animating
			    {
			        duration: 1000, // how fast we are animating
			        easing: 'easeOutBounce', // the type of easing
			        complete: function() { // the callback
			        }
			});
    	}
    	
		function desplazaSalida(){
			$("#startmark").animate({
			    left: "-10%",
			  	}, 1000, function() {
    	
			});
	
		}
		
		function stepClock(){
			// En cada paso de reloj ordenamos el array y mostramos las nuevas posiciones en pantalla
			clock +=STEP_CLOCK/1000.0;


			// Ordenamos el array por avance
		    arrayplayers.sort(SortByAvance);

		    first = arrayplayers[0];
		    first_position = parseInt($("#scene").css("width"))-parseInt($("#item"+arrayplayers[0].id).css("width"))-MARGIN_FIRTS;
		    first_avance = arrayplayers[0].avance;
		    
			var totalMovement = arrayplayers.length;

		    for(i=0;i<arrayplayers.length;i++){
		    	player_avance = arrayplayers[i].avance;
		    	player_position = (player_avance*first_position)/first_avance;

		    	console.log("Jugador "+ arrayplayers[i].name + " acumulado " + player_avance + " posición " + player_position);
		   			 
		   		$("#item"+arrayplayers[i].id).animate({
				    marginLeft: player_position,
			  	}, TIMEANIMATION, function() {
			    	// Animation complete.
			    	totalMovement--;
			    	if(totalMovement==0){
			    		// comprobamos condición de victoria una vez animado todo
			    		if(clock > TIME_HITO_2){
			    			// terminó la partida
			    			clearInterval(timer_clock); //detenemos el reloj de la competición
			    			clock = 0;
			    			mostrarGanador();
			    			// enviamos mensaje al server indicando quién ha ganado
			    			sendEndMessage();
			    		}
			    	}
				});
			}
		}
		
		function start(){
    		doRequestStart();
		}
		
		function startSpritesBg(){
			$('#bgtrackred, #bgtrackblue, #bgtrackyellow, #bgtrackgreen, #bgsun,#bgclouds').spStart(false);
		}
		function stopSpritesBg(){
			$('#bgtrackred, #bgtrackblue, #bgtrackyellow, #bgtrackgreen, #bgsun, #bgclouds').spStop(false);
		}
		
		function startSpritesPlayers(){
		    $('.itemblue').jsMovie('playClips');
			$('.itemred').jsMovie('playClips');
			$('.itemgreen').jsMovie('playClips');
			$('.itemyellow').jsMovie('playClips');
		}
		
		function stopSpritesPlayers(){
		    $('.itemblue').jsMovie("gotoFrame",1);
			$('.itemblue').jsMovie('stop');
		    $('.itemred').jsMovie("gotoFrame",1); 
			$('.itemred').jsMovie('stop');
		    $('.itemgreen').jsMovie("gotoFrame",1); 
			$('.itemgreen').jsMovie('stop');
		    $('.itemyellow').jsMovie("gotoFrame",1);
			$('.itemyellow').jsMovie('stop');

		
		}

		
		function prepareSprites(){
			$('#bgsun').pan({fps: FPS_SUN, speed: SPEED_PANNING, dir: 'left',depth: 0});
			$('#bgclouds').pan({fps: FPS_CLOUDS, speed: SPEED_PANNING, dir: 'left',depth: 10});

	    	$('#bgtrackred').pan({fps: FPS, speed: SPEED_PANNING, dir: 'left',depth: 20});
			$('#bgtrackgreen').pan({fps: FPS, speed: SPEED_PANNING, dir: 'left',depth: 30});
			$('#bgtrackblue').pan({fps: FPS, speed: SPEED_PANNING, dir: 'left',depth: 40});
			$('#bgtrackyellow').pan({fps: FPS, speed: SPEED_PANNING, dir: 'left',depth: 50});
			$('#bgtrackred, #bgtrackblue, #bgtrackgreen, #bgtrackyellow, #bgsun,#bgclouds').spRelSpeed(20);
			$('#bgtrackred, #bgtrackblue, #bgtrackyellow, #bgtrackgreen, #bgsun,#bgclouds').spStop();
			
			// Sprites items
			$('.itemblue').jsMovie({sequence : "blue_##.png",folder   : "img/caballospq/blue/",from     : 1,to       : 15,width    : 160,height   : 119,fps:FPS,repeat: true});
			$('.itemblue').jsMovie("addClip","myclip1",1,15,0);
	
	    	$('.itemred').jsMovie({sequence : "red_##.png",folder   : "img/caballospq/red/",from     : 1,to       : 15,width    : 160,height   : 119,fps:FPS,repeat: true});
			$('.itemred').jsMovie("addClip","myclip1",1,15,0);
			
		   	$('.itemgreen').jsMovie({sequence : "green_##.png",folder   : "img/caballospq/green/",from     : 1,to       : 15,width    : 160,height   : 119,fps:FPS,repeat: true});
			$('.itemgreen').jsMovie("addClip","myclip1",1,15,0);
	
		   	$('.itemyellow').jsMovie({sequence : "yellow_##.png",folder   : "img/caballospq/yellow/",from     : 1,to       : 15,width    : 160,height   : 119,fps:FPS,repeat: true});
			$('.itemyellow').jsMovie("addClip","myclip1",1,15,0);
		}
		
		function startGame(){
			start();
		}


		
		/**** comunicación websockets ****/


		var kREGISTER = 0;
		var kSTART = 1;
		var kMOVE = 2;
		var kEND = 3;
		var kDOWNPLAYER = 4;


		function processRegister(jugadores){
			console.log("Procesamos Registro");

			clients = jugadores;
		    for(i=0;i<clients.length;i++){
		    	player = clients[i];
		    	var j=0;
		    	for(j=0;j<arrayplayers.length;j++){
		    		playeradded = arrayplayers[j];
		    		if(playeradded.id == player.id)
		    			break;
		    	}
		    	if(j==arrayplayers.length){// si no está el jugador añadido se añade

		    		color = arrayColors[arrayplayers.length];
					player.color = color;   	
					player.avance = 0;	
		    		arrayplayers[j] = player;
		    		$('.sinjinete').first().removeClass("sinjinete").addClass("conjinete").attr("id","item"+player.id);

					//cuando llegue el primer jugador muestro la pantalla de ficha de jugadores
					playerNameDom = $(".sinnombre").first();
					playerNameDomScoreboard = $(".sinnombreScoreboard").first();
					playerDom = playerNameDom.parent();
		    		if(primerJugadorLlego){
		    			passPage("#instructionsbox");
		    			primerJugadorLlego = false;
		    		}else{
		    			playerDom.animate({ top: 29 }, 
						    {
			    		    duration: 200, 
					        easing: 'easeInQuad', 
					        complete: function() {}
					    });
		    			
		    		}
	    			playerNameDom.removeClass("sinnombre").addClass("connombre").html(player.name);
	    			playerNameDomScoreboard.removeClass("sinnombreScoreboard").addClass("connombreScoreboard").html(player.name);

		    		//centrado respecto de su padre
		    		parent_width = parseInt($("#scene").css("width")) ;
		    		margin = (parent_width/2.0)-(parseInt($("#item"+player.id).css("width"))/2);
		    		$("#item"+player.id).css("visibility","visible");
		    		$("#item"+player.id).css("margin-left",margin);
		    	} 
		    	
		    }
		}

		function processStart(jugadores){
			console.log("Procesamos comienzo");

			// arrancamos el timer del juego con cuenta atrás de tres segundos (así coordinamos los clientes móviles), quitamos la pantalla de jugadores y colocamos el marcador
			setTimeout(function(){
				timer_clock = setInterval(function(){stepClock();},STEP_CLOCK)
				startSpritesPlayers();
				startSpritesBg();
	    		passPage("#splash");
	    		desplazaSalida();
	    		deployScoreboard();	
			},3000);
	   		

		}

		function processMove(jugador){
			console.log("Procesamos movimiento");


	   	    if(clock<=TIME_HITO_1){
	   	    	MARGIN_FIRST = 400;
	   	    }else{
		   	   	if(clock>TIME_HITO_1 && clock < TIME_HITO_2){
		   	   		MARGIN_FIRST = 50;
	   	    	
		   	   	}else{
		   	   		if(clock > TIME_HITO_2){
		   	   			MARGIN_FIRTS = 400
		   	   		}
		   	   	}
		   	}
			var i;
		   	for(i=0;i<arrayplayers.length;i++){
	   			if(jugador.id == arrayplayers[i].id){
	   				arrayplayers[i].avance += parseInt(jugador.avance);
	   			}
		   	}




		}

		function processDown(jugador){
			console.log("Procesamos una caída");

			var i;
			for(i=0;i<arrayplayers.length;i++){
				if(jugador.id==arrayplayers[i].id){
					console.log("El jugador "+i +" se ha caído. Ahora vendrá un ovni y se lo llevará");
					//diferenciar si estamos en pantalla de espera o ya en la carrera
				}
			}
		}

		function sendEndMessage(){
			var message = "{\"a\":3,\"p\":[";
			//ya están ordenadas por avance
			var i;
			var sep="";
			for(i=0;i<arrayplayers.length;i++){
				if(arrayplayers.length-1 == i)
					sep="";
				else
					sep=",";

				message += "{\"id\":"+arrayplayers[i].id+"}"+sep;
			}
			message += "]}";
			doSendWSMessage(message);
		}

		function processError(){
			if(!haswinner){
				alert('Upps... parece que algo ha ido mal');				
				location.reload();
			}
		}

		function processMessage(message){
    		var obj = JSON.parse(message);
    		var accion = parseInt(obj.a,10);
    		
    		switch(accion){
    			case kREGISTER:
    				processRegister(obj.p);
    				break;
    			case kSTART:
    				// también viene el array de caballos, se lo pasamos por si acaso nos hiciera falta
    				processStart(obj.p);
    				break;
    			case kMOVE:
    				processMove(obj.p);
    				break;
    			case kEND:
    				break;
    			case kDOWNPLAYER:
    				processDown(obj.p);
    				break;
    			default:
    				break;
    		}
		}


		
		function initWS(){
			websocket = new WebSocket(SOCKETENDPOINT); 
			websocket.onopen = function(evt) { onOpen(evt) }; 
			websocket.onclose = function(evt) { onClose(evt) }; 
			websocket.onmessage = function(evt) { onMessage(evt) }; 
			websocket.onerror = function(evt) { onError(evt) };
		}
		
   
		function onOpen(evt) { 
			console.log("onOpen " + evt.data);	//mensaje para registrar la tv en el server
			doSendWSMessage("{\"a\":0,\"t\":0}"); 
		}  

		function onClose(evt) {
			console.log("onClose " + evt.data);
			processError();

		}

		function onMessage(evt) {processMessage(evt.data);}  

		function onError(evt) {
			console.log("onError " + evt.data);
			processError();
		}
		
		function doSendWSMessage(message) {websocket.send(message);}
		
		
		/**** end comunicación websockets ****/
		
		// hacia arriba funciones javascript del juego
		
    	$(document).ready(function() {
    		// preparamos los sprites de decoración
			prepareSprites();
			prepareScreen(); // centrar cajas y demás
			$("#btnRepetir").click(function(){
					location.reload();
			})
		});
		
		// hacia abajo funciones del contexto del juego

		function prepareScreen(){
			$('#splash').animate({ marginTop: 30 }, // what we are animating
			    {
			        duration: 1000, // how fast we are animating
			        easing: 'easeOutBounce', // the type of easing
			        complete: function() { // the callback
			        }
			    });
			
			setTimeout(function(){passPage("#presentation");},2000);

		}
		
		function passPage(id){
			$(id).animate({ marginTop: -630,rotate: '+=90deg' }, // what we are animating
			    {
			        duration: 300, // how fast we are animating
			        easing: 'easeInQuad', // the type of easing
			        complete: function() { // the callback
        				//setTimeout(function(){passPage("#instructionsbox");},2000);
        				if(id == "#presentation"){
	        				console.log(id);
							//startGame();    		 
							initWS();
						}
			        }
		    });
		}


