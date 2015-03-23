package Models;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.Errors.GenericError;
import com.obturecode.messages.receive.BaseReceiveMessage;
import com.obturecode.messages.receive.EndReceiveMessage;
import com.obturecode.messages.receive.ErrorReceiveMessage;
import com.obturecode.messages.receive.RegisterReceiveMessage;
import com.obturecode.messages.receive.StartReceiveMessage;
import com.obturecode.messages.send.MoveMessage;
import com.obturecode.messages.send.RegisterMessage;
import com.obturecode.messages.send.StartMessage;
import com.obturecode.websocket.WebSocketConnector;
import com.obturecode.websocket.WebSocketConnectorDelegate;


public class GameModel implements WebSocketConnectorDelegate {
	
	private static GameModel INSTANCE;
	
	
	private String nameToRegister;
	private int TIME_BTW_STATUS = 200;
	
	private GameModelDelegate delegate;
	private Handler gameHandler;
	private Runnable gameRunnable;
	
	private ErrorReceiveMessage lastError;
	
	private enum GameState{
		REGISTER,
		PLAY
	}
	
	private GameState state = GameState.REGISTER;
	
	public static GameModel shared(){
		if(INSTANCE == null){
			INSTANCE = new GameModel();
		}
		return INSTANCE;
	}
	
	
	private GameModel(){
		
	}
	
	public void register(String name){
		nameToRegister = name;
		state = GameState.REGISTER;
		if(!WebSocketConnector.shared().isConnected()){
			WebSocketConnector.shared().setDelegate(this);
			WebSocketConnector.shared().activeDebugMode();
			WebSocketConnector.shared().openConnection(MyApplication.getHost(), MyApplication.getPort(), MyApplication.getContext());
		}else{
			sendRegister();
		}
	}
	
	public void startGame(){
		StartMessage message = new StartMessage();
		WebSocketConnector.shared().sendMessage(message);
	}
	
	public void setDelegate(GameModelDelegate delegate){
		this.delegate = delegate;
		//TODO guardar œltimo error y enviarlo cuando se setee el delegate
	}
	
	private void sendRegister(){
		RegisterMessage message = new RegisterMessage(nameToRegister);
		WebSocketConnector.shared().sendMessage(message);
		nameToRegister = null;
	}
	
	private void sendMove(){
		int moves = delegate.avancesTillLastTime();
		if(moves > 0){
			MoveMessage message = new MoveMessage();
			message.setAvances(moves);
			WebSocketConnector.shared().sendMessage(message);
		}
		executeGameHandler();
	}
	
	private void executeGameHandler(){
		gameHandler = new Handler(Looper.getMainLooper());
		gameRunnable = new Runnable() {
			
			@Override
			public void run() {
				sendMove();
				
			}
		}; 
		gameHandler.postDelayed(gameRunnable, TIME_BTW_STATUS);
	}
	

	@Override
	public void connectorOpened() {
		if(nameToRegister != null){
			sendRegister();
		}
	}
	
	
	private void resetGame(){
		gameHandler.removeCallbacks(gameRunnable);
		state = GameState.REGISTER;
	}
	
	@Override
	public void connectorMessage(String m) {
		BaseReceiveMessage message = MessageReceiveBuilder.buildMessage(m);
		if(message instanceof RegisterReceiveMessage){
			RegisterReceiveMessage mess = (RegisterReceiveMessage) message;
			if(mess.hasTv()){
				delegate.registerComplete(mess.isMaster());
			}
		}else if(message instanceof StartReceiveMessage){
			state = GameState.PLAY;
			delegate.gameStarted();
			executeGameHandler();
		}else if(message instanceof EndReceiveMessage){
			resetGame();
			Log.e("e", "game finished model");
			delegate.gameFinished();
		}else if(message instanceof ErrorReceiveMessage){
			lastError = (ErrorReceiveMessage)message;
			resetGame();
			delegate.gameError(((ErrorReceiveMessage) message).getError());
		}else{
			resetGame();
			delegate.gameError(new GenericError());
		}
	}
	

	@Override
	public void connectorError(Error error) {
		if(state == GameState.REGISTER){
			if(lastError != null){
				delegate.registerError(lastError.getError());
				lastError = null;
			}else{
				delegate.registerError(error);
			}
		}else{
			if(lastError != null){
				delegate.gameError(lastError.getError());
				lastError = null;
			}else{
				delegate.gameError(error);
			}
		}
	}
	

	@Override
	public void connectorClosed() {
		resetGame();
		if(state == GameState.REGISTER){
			if(lastError != null){
				delegate.registerError(lastError.getError());
				lastError = null;
			}else{
				delegate.registerError(new GenericError());
			}
		}else{
			if(lastError != null){
				delegate.gameError(lastError.getError());
				lastError = null;
			}else{
				delegate.gameError(new GenericError());
			}
		}
	}
	
}
