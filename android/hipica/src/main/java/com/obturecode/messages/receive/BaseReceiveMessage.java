package com.obturecode.messages.receive;

import org.json.JSONObject;

public abstract class BaseReceiveMessage {
	
	public BaseReceiveMessage(JSONObject result){
		parseMessage(result);
	}
	
	public abstract void  parseMessage(JSONObject result);
}
