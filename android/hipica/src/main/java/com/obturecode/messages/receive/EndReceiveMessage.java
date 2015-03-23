package com.obturecode.messages.receive;

import org.json.JSONException;
import org.json.JSONObject;

public class EndReceiveMessage extends BaseReceiveMessage  {

	private int pos;
	
	public EndReceiveMessage(JSONObject result) {
		super(result);
	}

	@Override
	public void parseMessage(JSONObject result) {
		try {
			pos = result.getInt("pos");
		} catch (JSONException e) {
		}
	}
	
}
