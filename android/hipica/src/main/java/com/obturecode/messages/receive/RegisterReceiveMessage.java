package com.obturecode.messages.receive;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterReceiveMessage extends BaseReceiveMessage {
	
	boolean tv;
	boolean master;

	public RegisterReceiveMessage(JSONObject result) {
		super(result);
	}

	@Override
	public void parseMessage(JSONObject result) {
		try {
			master = result.getInt("m") == 0 ? false:true;
			tv = result.getInt("tv") == 0 ? false:true;
		} catch (JSONException e) {
			//TODO handle error
		}
		
	}

	public boolean hasTv() {
		return tv;
	}

	public boolean isMaster() {
		return master;
	}
	
	
	
}
