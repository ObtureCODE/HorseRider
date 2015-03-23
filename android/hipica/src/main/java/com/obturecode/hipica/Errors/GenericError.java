package com.obturecode.hipica.Errors;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;


public class GenericError extends Error {
	

	private static final long serialVersionUID = 1L;
	
	private int code;

	public GenericError(){
		super(MyApplication.getContext().getString(R.string.generic_error_msg));
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
