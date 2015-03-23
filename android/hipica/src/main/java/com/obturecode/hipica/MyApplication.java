package com.obturecode.hipica;


import android.app.Application;
import android.content.Context;

import junit.framework.Assert;

public class MyApplication extends Application {
	
	private static MyApplication instance;
	
	public MyApplication() {
    	instance = this;
    }

    public static Context getContext() {
    	return instance;
    }
    
    public static String getHost(){
        String host = "ws://YOURHOST";
        Assert.assertNotSame("Host not defined", host, "ws://YOURHOST");
        return host;
    }
    
    public static String getPort(){
    	return "80";
    }
    
}
