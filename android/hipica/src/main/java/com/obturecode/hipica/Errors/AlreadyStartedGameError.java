package com.obturecode.hipica.Errors;


import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;

public class AlreadyStartedGameError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AlreadyStartedGameError(){
		super(MyApplication.getContext().getString(R.string.already_started_error_msg));
	}
}
