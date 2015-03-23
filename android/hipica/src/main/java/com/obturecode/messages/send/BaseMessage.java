package com.obturecode.messages.send;

import org.json.JSONException;
import org.json.JSONObject;

import com.obturecode.websocket.BaseMessageInterface;

public abstract class BaseMessage implements BaseMessageInterface{
	protected JSONObject params = new JSONObject();
	
	protected abstract String getMethod();
	
	public BaseMessage(){
		try {
			params.put("a", Integer.parseInt(getMethod()));
		} catch (JSONException e) {
		}
	}
	
	
	@Override
	public String messageToString(){
		fillParams();
		return params.toString();
	}
	
	protected abstract void fillParams();
	
}
