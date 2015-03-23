package com.obturecode.messages.receive;

import org.json.JSONException;
import org.json.JSONObject;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;
import com.obturecode.hipica.Errors.AlreadyStartedGameError;
import com.obturecode.hipica.Errors.GameLostError;
import com.obturecode.hipica.Errors.InternetError;
import com.obturecode.websocket.error.GenericError;

public class ErrorReceiveMessage extends BaseReceiveMessage  {
	
	String errorMessage;
	int errorCode;
	Error error;
	
	public enum ErrorType{
		GENERIC,
		INTERNET
	}
	
	private static final int CODE_GAME_LOST = 400;
	private static final int CODE_GAME_STARTED = 401;
	
	public ErrorReceiveMessage(ErrorType type){
		super(null);
		if(type == ErrorType.INTERNET){
			errorMessage = MyApplication.getContext().getString(R.string.internet_error_msg);
			error = new InternetError();
		}else{
			errorMessage = MyApplication.getContext().getString(R.string.generic_error_msg);
			error = new GenericError();
		}
	}

	public ErrorReceiveMessage(JSONObject result) {
		super(result);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseMessage(JSONObject result) {
		try {
			errorMessage = result.getString("e");
			this.setErrorCode(result.getInt("code"));
		} catch (JSONException e) {
			error = new GenericError();
		}
	}
	
	private void setErrorCode(int errorCode){
		this.errorCode = errorCode;
		switch (errorCode) {
		case CODE_GAME_LOST:
			error = new GameLostError();
			break;
		
		case CODE_GAME_STARTED:
			error = new AlreadyStartedGameError();
			break;

		default:
			error = new GenericError();
			break;
		}
	}
	
	public Error getError(){
		return error;
	}

}
