package Models;

import org.json.JSONException;
import org.json.JSONObject;

import com.obturecode.messages.receive.BaseReceiveMessage;
import com.obturecode.messages.receive.EndReceiveMessage;
import com.obturecode.messages.receive.ErrorReceiveMessage;
import com.obturecode.messages.receive.ErrorReceiveMessage.ErrorType;
import com.obturecode.messages.receive.RegisterReceiveMessage;
import com.obturecode.messages.receive.StartReceiveMessage;

public class MessageReceiveBuilder {
	
	private final static int REGISTER = 0;
	private final static int START = 1;
	private final static int END = 3;
	private final static int ERROR = 5;
	
	public static BaseReceiveMessage buildMessage(String response){
		BaseReceiveMessage message = null;
		try {
			JSONObject result = new JSONObject(response);
			int action = result.getInt("a");
			switch (action) {
			case REGISTER:
				message = new RegisterReceiveMessage(result);
				break;
			
			case START:
				message = new StartReceiveMessage(result);
				break;
				
			case END:
				message = new EndReceiveMessage(result);
				break;
			
			case ERROR:
				message = new ErrorReceiveMessage(result);
				break;
			default:
				message = new ErrorReceiveMessage(ErrorType.GENERIC);
				break;
			}
			
		} catch (JSONException e) {
			message = new ErrorReceiveMessage(ErrorType.GENERIC);
		}
		
		return message;
		
		
	}
	
}
