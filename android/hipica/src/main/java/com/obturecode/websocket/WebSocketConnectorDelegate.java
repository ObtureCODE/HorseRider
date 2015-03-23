package com.obturecode.websocket;

public interface WebSocketConnectorDelegate {
	public void connectorOpened();
	public void connectorMessage(String message);
	public void connectorError(Error error);
	public void connectorClosed();
}
