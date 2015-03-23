package com.obturecode.messages.send;

import org.json.JSONException;

public class RegisterMessage extends BaseMessage  {
	private static String system = "android";
	private static int type = 1;
	
	private String name;
	
	public RegisterMessage(String name){
		super();
		this.name = name;
	}

	@Override
	protected String getMethod() {
		return "0";
	}

	@Override
	protected void fillParams() {
		try {
			params.put("t", type);
			params.put("name", name);
			params.put("system", system);
		} catch (JSONException e) {
		}
	}
	
}
