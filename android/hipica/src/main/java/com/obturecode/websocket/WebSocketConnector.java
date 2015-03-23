package com.obturecode.websocket;

import java.net.URI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.codebutler.android_websockets.WebSocketClient;
import com.obturecode.hipica.Errors.InternetError;
import com.obturecode.websocket.error.GenericError;

public class WebSocketConnector implements  WebSocketClient.Listener{
	
	private static WebSocketConnector INSTANCE;
	private WebSocketConnectorDelegate delegate;
	private Context context;
	
	private boolean debugMode = false;
	
	WebSocketClient client;
	
	public static  WebSocketConnector shared(){
		if(INSTANCE == null){
			INSTANCE = new WebSocketConnector();
		}
		return INSTANCE;
	}
	
	private WebSocketConnector(){
		
	}
	
	public void setDelegate(WebSocketConnectorDelegate delegate){
		this.delegate = delegate;
	}
	
	public void openConnection(String host, String port, Context context){
		this.context = context;
		client = new WebSocketClient(URI.create(host+":"+port),this, null);
		client.connect();
	}
	
	public void disconnect(){
		client.disconnect();
	}

	@Override
	public void onConnect() {
		delegate.connectorOpened();
	}
	
	public void activeDebugMode(){
		debugMode = true;
	}

	@Override
	public void onMessage(String message) {
		if(debugMode)
			Log.e("websocket", "message recieved "+message);
		delegate.connectorMessage(message);
	}

	@Override
	public void onMessage(byte[] data) {
		// TODO por aqu’ de momento nada
	}

	@Override
	public void onDisconnect(int code, String reason) {
		if(debugMode)
			Log.e("websocket", "disconnect "+reason+" code");
		delegate.connectorClosed();
	}

	@Override
	public void onError(Exception error) {
		if(debugMode)
			Log.e("websocket", "error "+error.getLocalizedMessage());
		if(internetAvaliable()){
			delegate.connectorError(new GenericError());
		}else{
			delegate.connectorError(new InternetError());
		}
	}
	
	public void sendMessage(BaseMessageInterface message){
		String m = message.messageToString();
		if(debugMode)
			Log.e("websocket", "message send "+m);
		client.send(m);
	}
	
	public boolean isConnected(){
		if(client != null)
			return client.isConnected();
		else 
			return false;
	}
	
	private Boolean internetAvaliable(){
		ConnectivityManager conMgr =  (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
		   return false;
		 if (!i.isConnected())
		   return false;
		 if (!i.isAvailable())
		   return false;
		 return true;

	}
	
}
