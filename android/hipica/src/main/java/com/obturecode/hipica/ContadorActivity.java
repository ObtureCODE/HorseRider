package com.obturecode.hipica;

import java.util.List;

import Models.GameModel;
import Models.GameModelDelegate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ContadorActivity extends Activity implements GameModelDelegate, SensorEventListener
{
	float prevZ, curZ;
	int isRising = -1;
	int cuentaAtras = 4;
	int contador = 0;
	private boolean gameEnded = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.contador);
    	
    	GameModel.shared().setDelegate(this);
    	
    	MyCount counter = new MyCount(5000,1000);
    	counter.start();
    }
    
    
    public class MyCount extends CountDownTimer
    {
    	public MyCount(long millisInFuture, long countDownInterval) 
    	{
    		super(millisInFuture, countDownInterval);
    	}
    	
    	
    	@Override
    	public void onFinish() 
    	{
    	}
    	
    	@Override
    	public void onTick(long millisUntilFinished) 
    	{
    		ImageView imagen;
        	
        	switch (cuentaAtras) 
        	{
        		case 4:
        			cuentaAtras--;
        			break;
        		case 3:
        			imagen = (ImageView)findViewById(R.id.three);
        			imagen.setVisibility(View.INVISIBLE);
        			cuentaAtras--;
        			break;
    			
        		case 2:
        			imagen = (ImageView)findViewById(R.id.two);
        			imagen.setVisibility(View.INVISIBLE);
        			cuentaAtras--;
        			break;
        		
        		case 1:
        			imagen = (ImageView)findViewById(R.id.one);
        			imagen.setVisibility(View.INVISIBLE);
        			imagen = (ImageView)findViewById(R.id.riding);
        			imagen.setVisibility(View.VISIBLE);
        			cuentaAtras--;
        	        contador = 0;
        			break;

        		default:
        			break;
    		}
    	}
    }

	@Override
	public int avancesTillLastTime() 
	{
		if(cuentaAtras == 0){
			int c = contador;
			contador = 0;
			return c;
		}else{
			return 0;
		}
		
	}

	@Override
	public void registerComplete(Boolean master) 
	{	
	}

	@Override
	public void registerError(Error e) 
	{	
	}

	@Override
	public void gameStarted() 
	{
	}

	@Override
	public void gameFinished() 
	{	
		gameEnded = true;
		Log.e("e", "game finished activity");
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog alertDialog = new AlertDialog.Builder(ContadorActivity.this).create();
				alertDialog.setTitle(getString(R.string.partida_terminada));
				alertDialog.setMessage(getString(R.string.la_partida_se_ha_acabado));
				alertDialog.setCancelable(false);
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   dialog.dismiss();
					   finish();
				   }
				});
				alertDialog.show();
			}
		});
	}

	
	
	@Override
	public void gameError(final Error e)
	{
		gameEnded = true;
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog alertDialog = new AlertDialog.Builder(ContadorActivity.this).create();
				alertDialog.setTitle(R.string.error);
				alertDialog.setMessage(e.getMessage());
				alertDialog.setCancelable(false);
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.setButton(getString(R.string.aceptar), new DialogInterface.OnClickListener()
				{
				   public void onClick(DialogInterface dialog, int which) 
				   {
					   	finish();
				   }
				});
				alertDialog.show();				
			}
		});
		
	}
	
	@Override
    protected void onResume() 
	{
        super.onResume();
        if(!gameEnded){
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

            if (sensors.size() > 0)
            {
                sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
            }
        }else{
            finish();
        }

    }
    
    @Override
    protected void onPause()
    {
    	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);    	
        sm.unregisterListener(this);
        super.onStop();
    }
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
        synchronized (this) 
        {   
            curZ = event.values[2];
            if (prevZ == 0)
                prevZ = curZ;

            float variacion = prevZ - curZ;
            if (variacion > 6 || variacion < -6){
            	if (curZ < prevZ){
            		if(isRising == -1){
                        //Empezamos hacia abajo
                        isRising = 0;
                    }else{
            			if(isRising == 1){
            				isRising = 0;
            				contador++;
            			}
            		}
            	}else{
                    if(isRising == -1){
                        //Empezamos hacia arriba
                        isRising = 1;
                    }else{
                    	if(isRising == 0){
                    		isRising = 1;
                    	}
                    }
            	}
            }
            prevZ = curZ;
        }
	}
	
	@Override
	public void onBackPressed() 
	{
		if(gameEnded){
			super.onBackPressed();
		}
	}

}