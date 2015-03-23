package com.obturecode.messages.send;

import org.json.JSONException;

public class MoveMessage extends BaseMessage {

	int avances;
	
	@Override
	protected String getMethod() {
		return "2";
	}

	@Override
	protected void fillParams() {
		try {
			params.put("avance", avances);
		} catch (JSONException e) {
		}
	}

	public void setAvances(int avances) {
		this.avances = avances;
	}
	
	

}
