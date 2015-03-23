package com.obturecode.hipica;

import Models.GameModel;
import Models.GameModelDelegate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NameActivity extends Activity implements OnClickListener, GameModelDelegate {
	
	EditText textName;
	private Button buttonListo;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.name);
		
		TextView textView = (TextView)findViewById(R.id.TextView);

        
        textName = (EditText)findViewById(R.id.EditText);

        buttonListo = (Button)findViewById(R.id.ButtonListo);
        
        Button buttonListo = (Button)findViewById(R.id.ButtonListo);
        buttonListo.setOnClickListener(this);
    
        textView = (TextView)findViewById(R.id.TextView2);

        textView = (TextView)findViewById(R.id.TextView3);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		buttonListo.setEnabled(true);
	}

	@Override
	public void onClick(View v) 
	{
		RelativeLayout re = (RelativeLayout)findViewById(R.id.RelativeLayout);
		re.setVisibility(View.VISIBLE);
		
		if(textName != null && !textName.getText().toString().equals(""))
		{
			GameModel.shared().setDelegate(this);
			GameModel.shared().register(textName.getText().toString());
		
	        buttonListo.setEnabled(false);
	        
	        EditText editText = (EditText)findViewById(R.id.EditText);
	        editText.setEnabled(false);
		}
		else
		{
			re.setVisibility(View.INVISIBLE);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			builder.setTitle("Error!");
			builder.setMessage(getString(R.string.name_empty));
			builder.setPositiveButton(getString(R.string.aceptar), null);
			AlertDialog ad = builder.create();
			
			ad.show();
		}
	}

	@Override
	public int avancesTillLastTime() 
	{
		return 0;
	}

	@Override
	public void registerComplete(final Boolean master) 
	{
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (master)
				{
					AlertDialog alertDialog = new AlertDialog.Builder(NameActivity.this).create();
					alertDialog.setTitle(getString(R.string.empezar_partida));
					alertDialog.setMessage(getString(R.string.cuando_todos_esten_listos));
					alertDialog.setButton(getString(R.string.empezar), new DialogInterface.OnClickListener()
					{
					   public void onClick(DialogInterface dialog, int which) 
					   {
						   GameModel.shared().startGame();
						   RelativeLayout re = (RelativeLayout)findViewById(R.id.RelativeLayout);
						   re.setVisibility(View.INVISIBLE);
						   re = (RelativeLayout)findViewById(R.id.RelativeLayout2);
						   re.setVisibility(View.VISIBLE);
					   }
					});
					alertDialog.show();
				}
				else
				{
					RelativeLayout re = (RelativeLayout)findViewById(R.id.RelativeLayout);
					re.setVisibility(View.INVISIBLE);
					re = (RelativeLayout)findViewById(R.id.RelativeLayout2);
					re.setVisibility(View.VISIBLE);
				}
			}
		});
		
	}

	@Override
	public void registerError(final Error e) 
	{
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				buttonListo.setEnabled(true);
				RelativeLayout re = (RelativeLayout)findViewById(R.id.RelativeLayout);
				re.setVisibility(View.INVISIBLE);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NameActivity.this); 
				builder.setTitle(R.string.error);
				builder.setMessage(e.getMessage());
				builder.setPositiveButton(getString(R.string.aceptar), null);
				AlertDialog ad = builder.create();
				
				ad.show();
				
			}
		});
		
	}

	@Override
	public void gameStarted() 
	{
		
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Button buttonListo = (Button)findViewById(R.id.ButtonListo);
		        buttonListo.setEnabled(true);
		        
		        EditText editText = (EditText)findViewById(R.id.EditText);
		        editText.setEnabled(true);
		        
				RelativeLayout re = (RelativeLayout)findViewById(R.id.RelativeLayout2);
				re.setVisibility(View.INVISIBLE);
				   
				Intent intent = new Intent(NameActivity.this, ContadorActivity.class);
		    	startActivity(intent);
			}
		});
	
	}

	@Override
	public void gameFinished() 
	{
		
	}

	@Override
	public void gameError(Error e) 
	{
	}
}
