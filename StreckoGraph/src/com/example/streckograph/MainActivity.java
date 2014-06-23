package com.example.streckograph;







import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class MainActivity extends Activity {
	
	//Variablen
	
	static TextView locationView;
	public static GLSurfaceView glSurfaceView;   
	LocationGPS myLocation;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		//Objekt der Klasse LocationGPS erzeugen
		myLocation = new LocationGPS(this);
		
        
        
        // Surface View für OpenGl .
        glSurfaceView = new GlSurfaceV(this);
        
        
		
		
		
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        RelativeLayout.LayoutParams glParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        
        layout.addView(glSurfaceView, glParams);
		
        //neues TextView für die Anzeige der Geschwindigkeit,Richtung und Entfernung erstellen
		locationView = new TextView(this);
		locationView.setText("Herzlich Wilkommen bei StreckoGraph");
		locationView.setTextColor(Color.parseColor("#FFFFFF"));
		locationView.setGravity(0x01);
		layout.addView(locationView);
		
				
       
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//wenn die Activity gestarted wird, mit dem Location Client verbinden 
		myLocation.connect();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//wenn die Activity gestopt wird, Verbindung zum Client unterbrechen
		myLocation.disconnect();
	}
	
	
	
}


