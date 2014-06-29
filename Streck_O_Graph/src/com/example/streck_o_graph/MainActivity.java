package com.example.streck_o_graph;






import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class MainActivity extends Activity {
	
	//Variablen
	
	public static TextView locationView;
	public static GLSurfaceView glSurfaceView;
   	public LocationGPS locationGPS;
		
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    //Object der Klasse LocationGPS erstellen
		locationGPS = new LocationGPS(this);
		       
			
        // erstellen eines Surface Views
        glSurfaceView = new GLSurfaceV(this);
        
        
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        RelativeLayout.LayoutParams glParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.addView(glSurfaceView, glParams);
		
		locationView = new TextView(this);
		locationView.setText("Herzlich Wilkommen bei Streck O Graph ");
		locationView.setTextColor(Color.parseColor("#00FF00"));
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
		//zum Location Client verbinden
		locationGPS.connect();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//Verbindung zum Client unterbrechen
		locationGPS.disconnect();
	}
	
	
	
}


