<?php
namespace Horserider;
use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

/*
	objeto	partida:{
		"IPPARTIDA":{ "tv":{"idcon":int,"nombre":String},
			 "mandos":[{"nombre":String,"color":String,"idcon":Int,"tipo":String}]
			}
		};

		$this->clientes:{"id"{"conn":$conn,"tipo":SIN_TIPO}}
*/

define('SIN_TIPO',-1);
define('TIPO_TV',0);
define('TIPO_MANDO',1);

define('REGISTER',0);
define('START',1);
define('MOVE',2);
define('END',3);
define('DOWN',4);
define('ERROR',5);

define('ESTADO_PARTIDA_PREPARANDOSE',0);
define('ESTADO_PARTIDA_EMPEZANDO',1);
define('ESTADO_PARTIDA_EMPEZADA',2);
define('ESTADO_PARTIDA_TERMINADA',3);
define('ESTADO_PARTIDA_BORRANDOSE',4);

define('DEBUG',true);
define('DEBUG_ERROR',0);
define('DEBUG_LOG',1);

define('CODE_GAME_LOST',400);
define('CODE_GAME_STARTED',401);

class Movimientos implements MessageComponentInterface {
    protected $clientes;
    protected $partidas;
    //protected $clients;
    public function __construct() {
        $this->clientes=array();
        $this->partidas=array();
        $this->clients = new \SplObjectStorage;
	//$this->clientes = new \SplObjectStorage;
    }
    public function onOpen(ConnectionInterface $conn) {
    	 //$this->clients->attach($conn);
    	 
		$this->clientes[$conn->resourceId]['conn']=$conn;
		$this->clientes[$conn->resourceId]['type']=SIN_TIPO;
		
		$this->log("New connection!",$conn,DEBUG_LOG);
	}

    public function onMessage(ConnectionInterface $from, $msg) {

    	$mensaje = json_decode($msg);
 
    	if($mensaje == null){
    		$this->log("Error en el JSON:{$msg}",$from,DEBUG_ERROR);
    		return;
    	}
		if($mensaje->a == REGISTER && $this->clientes[$from->resourceId]["type"] == SIN_TIPO){
			if(!isset($mensaje->t)){
				$this->log("Registro sin tipo:{$msg}",$from,DEBUG_ERROR);
    			return;
			}
			//El primero que se registra crea la partida si esta no ha sido creada.
			if(!isset($this->partidas[$this->ip($from)])){
				$this->partidas[$this->ip($from)]=array('tv'=>null,'mandos'=>array(),'estado'=>ESTADO_PARTIDA_PREPARANDOSE);
			}

			if($mensaje->t == TIPO_TV){
				$this->on_register_tv($mensaje);
					
			}else if($mensaje->t == TIPO_MANDO){
				 $this->on_register_mando($mensaje);
			}else{
				$this->log("Error en el tipo",$from,DEBUG_ERROR);	
			}	
		
		}elseif(isset($this->partidas[$this->ip($from)]) && $mensaje->a == START && $this->clientes[$from->resourceId]["type"]==TIPO_MANDO){
			$this->on_start($mensaje);
		
		}elseif($mensaje->a == MOVE && $this->clientes[$from->resourceId]["type"]==TIPO_MANDO){
			$this->on_move($mensaje);
		
		}elseif($mensaje->a == END && $this->clientes[$from->resourceId]["type"]==TIPO_TV){
			$this->on_end($mensaje);

		}else{
			$this->log("Accion no encontrada",$from,DEBUG_LOG);
		}
	
    }

    public function onClose(ConnectionInterface $conn) {
    	//$this->clients->detach($conn);
    	$this->log("OnClosed",$conn,DEBUG_LOG);
    	$this->deleteConn($conn->resourceId);
	}

    public function onError(ConnectionInterface $conn, \Exception $e) {
    	$this->log("An error has occurred: {$e->getMessage()}",$conn,DEBUG_ERROR);
        $conn->close();
        $this->deleteConn($conn->resourceId);
	}

	private function getPlayers($idPartida){
		$p=array();
		$mandos=array();
		if(isset($this->partidas[$idPartida]) && isset($this->partidas[$idPartida]['mandos']))
			$mandos = $this->partidas[$idPartida]['mandos'];

		for($i=0;$i<count($mandos);$i++){
			$p[]=array('id'=>$mandos[$i]['id'],
				'name'=>$mandos[$i]['name'],
				'color'=>$mandos[$i]['color']);
		}
		return $p;
	}

	private function deleteConn($id){
		if(!isset($this->clientes[$id]))
			return;

		$conn = $this->clientes[$id]['conn'];

		//miramos si esta en una partida
		if(isset($this->partidas[$this->ip($conn)])){
			if($this->clientes[$id]['type']==TIPO_MANDO){
				$mandos = $this->partidas[$this->ip($conn)]['mandos'];
				for($i=0;$i<count($mandos);$i++){
					if($mandos[$i]['id']==$id){
						if($this->partidas[$this->ip($conn)]['tv']!=null){
							//igual hay que mirar si la conexion con la tv esta online
							$this->clientes[$this->partidas[$this->ip($conn)]['tv']['id']]['conn']->send(json_encode(array('a'=>DOWN,'p'=>array('id'=>$id))));
						}
						//Borramos de la lista de mandos de una partida
						unset($this->partidas[$this->ip($conn)]['mandos'][$i]);
						break;
					}
				}

				//Si borramos el mando y no hay ningun mando mas ni TV, borramos la partida
				if(count($this->partidas[$this->ip($conn)]['mandos'])==0 && $this->partidas[$this->ip($conn)]['tv']==null){
					unset($this->partidas[$this->ip($conn)]);
				}

			}elseif($this->clientes[$id]['type']==TIPO_TV){
				for($i=0;$i<count($this->partidas[$this->ip($conn)]['mandos']);$i++){
					if($this->partidas[$this->ip($conn)]['estado']!=ESTADO_PARTIDA_BORRANDOSE){
						$this->clientes[$this->partidas[$this->ip($conn)]['mandos'][$i]['id']]['conn']->send(json_encode(array('a'=>ERROR,'e'=>'La carrera se ha perdido','code'=>CODE_GAME_LOST)));
					}
					$this->clientes[$this->partidas[$this->ip($conn)]['mandos'][$i]['id']]['type']=SIN_TIPO;
				}
				//si se borra la TV se borra la partida.
				unset($this->partidas[$this->ip($conn)]);
			}

		}
		unset($this->clientes[$id]);
	}

	private function log($msg,$from,$type){
		if($type==DEBUG_LOG && !DEBUG)	return;

		print("{$this->ip($from)} ({$from->resourceId}) ::{$msg}\n");
	}

	private function ip($conn){
		return $conn->WebSocket->request->getHeader('X-Forwarded-For', true)->__toString();
	}

	private function on_register_tv($mensaje){
		//Inicio la tv
		if($this->partidas[$this->ip($from)]["tv"]==null){
			$this->clientes[$from->resourceId]["type"]=TIPO_TV;
			$this->partidas[$this->ip($from)]["tv"]=array('id'=>$from->resourceId,"name"=>$from->resourceId);
			
			//Le respondo con los jugadores que hay actualmente
			$p = $this->getPlayers($this->ip($from));

			$respuesta = array('a'=>REGISTER,'p'=>$p);
			$from->send(json_encode($respuesta));

			//Le decimos a los mandos que ya tienen TV
			$mandos = $this->partidas[$this->ip($from)]['mandos'];
			for($i=0;$i<count($mandos);$i++){
				$this->clientes[$mandos[$i]['id']]['conn']->send(json_encode(array('a'=>REGISTER,'tv'=>1,'m'=>$i==0?1:0)));
			}
		}else{
			//TODO:ERROR, tv en la misma ip
			$from->close();
			$this->deleteConn($from->resourceId);
		}
	}

	private function on_register_mando(){
		$this->clientes[$from->resourceId]["type"]=TIPO_MANDO;
		$mando = array("name"=>$mensaje->name,'color'=>0,'id'=>$from->resourceId,'so'=>$mensaje->system);
		if($this->partidas[$this->ip($from)]['estado']==ESTADO_PARTIDA_PREPARANDOSE){
			if(count($this->partidas[$this->ip($from)]['mandos'])<4){
			//Comprobamos si hay espacio para un jugador más y lo agregamos a la partida
				$this->partidas[$this->ip($from)]['mandos'][]=$mando;

				//Si ya hay asociada una TV le indicamos que hay un nuevo jugador
				$tv=0;
				$m=0;
				if($this->partidas[$this->ip($from)]['tv']!=null){
					$mensaje = array('a'=>REGISTER,'p'=>array($mando));
					$this->clientes[($this->partidas[$this->ip($from)]['tv']['id'])]['conn']->send(json_encode($mensaje));
					$tv=1;//indica que hay TV asociada a la partida
					if(count($this->partidas[$this->ip($from)]['mandos'])==1)
						$m=1;//Indica que es el mando master
				}
				$this->log("Registrado Movil",$from,DEBUG_LOG);
				$from->send(json_encode(array('a'=>REGISTER,'m'=>$m,'tv'=>$tv)));
			}else{
		       	//ERROR MUCHOS MANDOS, habra que dar un error y borrarlo de los clientes
				$this->log("Send: Demasiados jugadores",$from,DEBUG_LOG);
				$from->send(json_encode(array('a'=>ERROR,'e'=>'Demasiados jugadores')));
				$from->close();
				unset($this->clientes[$from->resourceId]);
			}
		}else{
			//ERROR LA PARTIDA YA HA COMENZADO, habra que dar un error y borrarlo de los clientes
			$this->log("Send: No te puedes unir a una partida ya empezada",$from,DEBUG_LOG);
			$from->send(json_encode(array('a'=>ERROR,'e'=>'No te puedes unir a una partida ya empezada')));
			$from->close();
			unset($this->clientes[$from->resourceId]);
		}
	}

	private function on_start($mensaje){
		if($this->partidas[$this->ip($from)]['estado']==ESTADO_PARTIDA_PREPARANDOSE){
			$this->partidas[$this->ip($from)]['estado']=ESTADO_PARTIDA_EMPEZANDO;
			$p = $this->getPlayers($this->ip($from));
			$respuesta = array('a'=>START,'p'=>$p);

				//Indicamos a la TV que la patida empieza
			$this->clientes[$this->partidas[$this->ip($from)]['tv']['id']]['conn']->send(json_encode($respuesta));

				//indicamos a todos los mandos que empieza la partida
			$mandos = $this->partidas[$this->ip($from)]['mandos'];
			for($i=0;$i<count($mandos);$i++){
				$this->clientes[$mandos[$i]['id']]['conn']->send(json_encode(array('a'=>START)));
			}
		}
	}

	private function on_move($mensaje){
		//Si entramos por aqui y la partida esta en estado EMPEZANDO, la ponemos en estado EMPEZADA
		if($this->partidas[$this->ip($from)]['estado']==ESTADO_PARTIDA_EMPEZANDO)
			$this->partidas[$this->ip($from)]['estado']=ESTADO_PARTIDA_EMPEZADA;

		if($this->partidas[$this->ip($from)]['estado']== ESTADO_PARTIDA_EMPEZADA && isset($mensaje->avance)){
				
			$respuesta = array('a'=>MOVE,'p'=>array('id'=>$from->resourceId,'avance'=>$mensaje->avance));
				
			//Indicamos a la TV el movimiento recibido
			$this->clientes[$this->partidas[$this->ip($from)]['tv']['id']]['conn']->send(json_encode($respuesta));
		}
	}

	private function on_end($mensaje){
		$this->log("Recibo un END",$from,DEBUG_LOG);
		if(($this->partidas[$this->ip($from)]['estado']== ESTADO_PARTIDA_EMPEZADA || $this->partidas[$this->ip($from)]['estado']== ESTADO_PARTIDA_EMPEZANDO) && isset($mensaje->p)){
			$this->partidas[$this->ip($from)]['estado']=ESTADO_PARTIDA_TERMINADA;
			$players = $mensaje->p;
			$this->log("Proceso un END",$from,DEBUG_LOG);
			
			//Comunicamos a todos los mandos que la partida ha terminado y las posiciones en las que han quedado
			for($i=0;$i<count($players);$i++){
				if(isset($this->clientes[$players[$i]->id]) && $this->ip($from)==$this->ip($this->clientes[$players[$i]->id]['conn'])){
					$this->log("Envio un END",$this->clientes[$players[$i]->id]['conn'],DEBUG_LOG);
					$this->clientes[$players[$i]->id]['conn']->send(json_encode(array('a'=>END,'pos'=>$i)));
				}
			}
			$this->partidas[$this->ip($from)]['estado']=ESTADO_PARTIDA_BORRANDOSE;
			
			//cerramos la TV y provocamos un cierre en cascada a todos los mandos
			$from->close();
			$this->deleteConn($from->resourceId);
		}
	}

}

