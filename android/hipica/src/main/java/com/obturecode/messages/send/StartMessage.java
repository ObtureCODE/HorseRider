package com.obturecode.messages.send;

public class StartMessage extends BaseMessage {

	@Override
	protected String getMethod() {
		return "1";
	}

	@Override
	protected void fillParams() {
		
	}

}
