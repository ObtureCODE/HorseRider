package Models;
/*
import android.os.Handler;
import android.util.Log;

import com.obturecode.hipica.MyApplication;
import com.obturecode.hipica.R;
import com.obturecode.hipica.BaseProxy.BaseProxyDelegate;
import com.obturecode.hipica.Errors.GenericError;
import com.obturecode.singleton.GlobalVariable;

import Proxys.StatusProxy;
import VO.StatusVO;


public class OldGameModel {
	
	private static Integer TAG_REGISTER = 20;
	private static Integer TAG_STATUS = 21;
	
	private static Integer TIME_BTW_STATUS = 1000;
	private static Integer TIME_BTW_REGISTER = 5000;
	
	private static OldGameModel INSTANCE = new OldGameModel();
	
	private String idDevice;
	private String idTV;
	private String name;
	private Integer player;
	private Integer currentStatus;
	
	public GameModelDelegate delegate;
	Boolean isPlayerIdentified = false;
	Boolean abort = false;
	
	private StatusProxy proxy;
	
	
	private OldGameModel(){
		super();
	}
	
	public static OldGameModel getInstance(){
		return INSTANCE;
	}
	
	private void doRegisterRequest(){
		if(proxy != null){
			Log.e("error model", "operaci—n en curso");
			return;
		}
		proxy = new StatusProxy(this, MyApplication.getContext().getString(R.string.BASE_URL));
		proxy.setName(this.name);
		proxy.delegateTag = TAG_REGISTER;
		proxy.execute();
		
	}
	
	private void doStatusRequest(){
		if(proxy != null)
		{
			Log.e("error model", "operaci—n en curso");
			return;
		}
		proxy = new StatusProxy(this, MyApplication.getContext().getString(R.string.BASE_URL));
		if(currentStatus == StatusVO.GAME_STATUS_START)
		{
			proxy.setAvances(delegate.avancesTillLastTime());
		}
		proxy.setIdDevice(idDevice);
		proxy.setIdTV(idTV);
		proxy.delegateTag = TAG_STATUS;
		proxy.execute();
	}
	
	private void executeStatusHandler()
	{
		final Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() 
		{
			@Override
			public void run() {
				doStatusRequest();
			}
		}, TIME_BTW_STATUS);
	}
	
	private void executeRegisterHandler(){
		final Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				doRegisterRequest();
			}
		}, TIME_BTW_REGISTER);
	}
	
	private void resetGame(){
		name = null;
		idDevice = null;
		idTV = null;
		player = -1;
		isPlayerIdentified = false;
		currentStatus = StatusVO.GAME_STATUS_WAIT;
		GlobalVariable.getInstance().setCookieStore(null);
	}
	
	public void registerPlayer(String name){
		this.name = name;
		this.doRegisterRequest();
	}
	
	public void startGame(){
		if(proxy != null){
			Log.e("error model", "operaci—n en curso");
			return;
		}
		proxy = new StatusProxy(this, MyApplication.getContext().getString(R.string.BASE_URL));
		proxy.setIdDevice(idDevice);
		proxy.setIdTV(idTV);
		proxy.setStart(1);
		proxy.delegateTag = TAG_STATUS;
		proxy.execute();
	}
	
	
	@Override
	public void requestFinished(Object result) 
	{
		StatusVO status = (StatusVO) result;
		
		if(proxy.delegateTag == TAG_REGISTER)
		{
			//Recibimos el primer status
			idTV = status.getIdTV();
			idDevice = status.getIdDevice();
			currentStatus = status.getStatus();
			if(currentStatus == StatusVO.GAME_STATUS_ERROR)
			{
				delegate.registerError(new GenericError());
			}
			else
			{
				this.executeStatusHandler();
			}
		}
		
		if(proxy.delegateTag == TAG_STATUS)
		{
			if(!isPlayerIdentified)
			{
				if(status.getStatus() != StatusVO.GAME_STATUS_ERROR){
					if(status.getPlayer() != -1){
						player = status.getPlayer();
						isPlayerIdentified = true;
						if(player == 0){
							//Soy el player 1, yo elijo cuando empieza la partida
							delegate.registerComplete(true);
						}else{
							delegate.registerComplete(false);
							this.executeStatusHandler();
						}
					}else{
						//Si no estoy identificado, sigo pidiendo status
						this.executeStatusHandler();
					}
				}else{
					delegate.gameError(new GenericError());
				}
			}else{
				if(status.getStatus() != currentStatus){
					currentStatus = status.getStatus();
					if(currentStatus == StatusVO.GAME_STATUS_START){
						//El juego ha empezado
						delegate.gameStarted();
						this.executeStatusHandler();
					}else if(currentStatus == StatusVO.GAME_STATUS_FINISHED){
						//El juego ha terminado
						this.resetGame();
						delegate.gameFinished();
					}else{
						//Error en el juego, volvemos a empezar
						this.resetGame();
						delegate.gameFinished();
					}
				}else{
					//Como nada ha cambiado, sigo haciendo peticiones
					this.executeStatusHandler();
				}
			}
		}
		
		proxy = null;
		
	}

	@Override
	public void requestFault(Error error) 
	{
		
		if(proxy.delegateTag == TAG_REGISTER)
		{
			if(error instanceof GenericError)
			{
				GenericError e = (GenericError) error;
				if(e.getCode() == 404)
				{
					//No hemos encontrado la tele, seguimos intentando registrarnos
					this.executeRegisterHandler();
					return;
				}
			}
			delegate.registerError(error);
		}
		
		if(proxy.delegateTag == TAG_STATUS)
		{
			this.resetGame();
			delegate.gameError(error);
		}
		
		proxy = null;
	}
}*/
