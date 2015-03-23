package com.obturecode.hipica.Errors;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;


public class InternetError extends Error {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternetError() {
		super(MyApplication.getContext().getString(R.string.internet_error_msg));
	}
}
