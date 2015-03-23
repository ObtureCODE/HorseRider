package com.obturecode.messages.receive;

import org.json.JSONObject;

public class StartReceiveMessage extends BaseReceiveMessage {

	public StartReceiveMessage(JSONObject result) {
		super(result);
	}

	@Override
	public void parseMessage(JSONObject result) {
		
	}

}
