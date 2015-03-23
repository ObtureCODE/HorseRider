package com.obturecode.hipica.Errors;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;

public class GameLostError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GameLostError(){
		super(MyApplication.getContext().getString(R.string.game_lost_error_msg));
	}

}
