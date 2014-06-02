package com.example.streckengraph;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView locationString,distanceView,locationStart,bearing,speedView; 
	Location anfangsLocation,currentBestLocation;
	int dritteLocation = 0;
	
	int distance;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	static final String Himmelsrichtungen[] = {"N","NE","E","SE","S","SW","W","NW","Fehler"};
	
	public int Himmelsrichtung(float gradzahl){
		if(gradzahl < 0){
			gradzahl = 360F+gradzahl;			
		}
		float i = gradzahl/360F;
		float x = i * 8F;
		int y = (int)Math.round(x);
		
		if(y == 8){
			
			y = 0;
			return y;
			
		}else{
			
			return y;
			
		}
		
	}
	public float getDistance(Location anfangslocation, Location aktuelleLocation){
		float [] dist = new float[1];  	
	    Location.distanceBetween(anfangsLocation.getLatitude(),anfangsLocation.getLongitude(), aktuelleLocation.getLatitude(),aktuelleLocation.getLongitude(),dist);
	    return dist[0];
		    }
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		locationString = (TextView)findViewById(R.id.locationString);
		distanceView = (TextView)findViewById(R.id.distanceView);
		locationStart = (TextView)findViewById(R.id.locationStart);
		bearing = (TextView)findViewById(R.id.bearing);
		speedView = (TextView)findViewById(R.id.speedView);
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		
		
		
		
		
		
		//Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager. NETWORK_PROVIDER);
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	dritteLocation = dritteLocation +1;
		    if(isBetterLocation(location,currentBestLocation)){
		    	
		    	    if(anfangsLocation == null || dritteLocation == 3){
			    	 anfangsLocation = location;		    	
		    	    }
			    	
			    Double latitude = location.getLatitude();
			    Double longitude = location.getLongitude();
			    Double startLatitude = anfangsLocation.getLatitude();
			    Double startLongitude = anfangsLocation.getLongitude();
			    float locationBearing = anfangsLocation.bearingTo(location);
			    int himmelsrichtungenIndex = Himmelsrichtung(locationBearing);
			    distance = (int)Math.round(getDistance(anfangsLocation,location));
			    float speed = (location.getSpeed())*3.6F;
			    
			    String speedString =("Geschwindigkeit: "+speed+" km/h");
			    String distanceString = ("Entfernung: "+distance+" m");
			    String locationBearingString = ("Richtung: "+Himmelsrichtungen[himmelsrichtungenIndex]);
			    String startLocation = ("Anfangs Location = Breite: "+startLatitude+ "  Länge: "+ startLongitude);
			    String test = ("Aktuelle Location = Breite: "+latitude+ "  Länge: "+ longitude);
			    CharSequence charsequence =  test;
			    
			    locationString.setText(charsequence);
			    distanceView.setText(distanceString);
			    locationStart.setText(startLocation);
			    bearing.setText(locationBearingString);
			    speedView.setText(speedString);
		    	    
		    }

		    
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };
		
		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager. GPS_PROVIDER, 0, 1, locationListener);
		locationManager.requestLocationUpdates(LocationManager. NETWORK_PROVIDER, 0, 1, locationListener);
		
		// Or use LocationManager.GPS_PROVIDER

		
		
		
		
	

		
		
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
