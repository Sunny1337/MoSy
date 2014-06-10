package com.example.streckengraphgoogle;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.drive.internal.DisconnectRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener{
	
	GooglePlayServiceLocation location1;
	Location deviceLocation,anfangsLocation,currentBestLocation;
	int resultCode,himmelsrichtungenIndex;
	boolean connected;
	float speed,locationBearing;
	LocationClient mLocationClient;
	TextView locationView, speedView, distanceView, anfangsLocationView,bearingView,addedDistanceView;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	float distance,addedDistance,newDistance;
	static final String Himmelsrichtungen[] = {"N","NE","E","SE","S","SW","W","NW","Fehler"};
	
	
	
	
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
    
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
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		locationView = (TextView)findViewById(R.id.textView1);
		distanceView = (TextView)findViewById(R.id.textView2);
		anfangsLocationView = (TextView)findViewById(R.id.textView3);
		speedView = (TextView)findViewById(R.id.textView4);
		bearingView = (TextView)findViewById(R.id.textView5);
		addedDistanceView = (TextView)findViewById(R.id.textView6);
		
		//CheckforGooglePlayServices check1 = new CheckforGooglePlayServices(this);
		//location1 = new GooglePlayServiceLocation(this);

		resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if(ConnectionResult.SUCCESS == resultCode){
			Toast.makeText(this, "GooglePlayService is available",Toast.LENGTH_LONG).show();
			
		}else{
			Toast.makeText(this, "GooglePlayService is NOT available",Toast.LENGTH_LONG).show();
		}
		/*connected = location1.checkConnection();
		if (connected){
			Toast.makeText(this, "GooglePlayService is available",Toast.LENGTH_LONG).show();
			
			
		}
		else{
			Toast.makeText(this, "GooglePlayService is NOT available",Toast.LENGTH_LONG).show();
		}*/
		mLocationClient =  new LocationClient(this, this, this);
		
		
		// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Start with updates turned off
        
		 
	
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
		 mLocationClient.connect();
		 //deviceLocation = mLocationClient.getLastLocation();
		
	//	location1.connectToGps();
		//deviceLocation = location1.getTheLocation();
		/*Toast.makeText(this, "Breite: "+ deviceLocation.getLatitude()+"  Länge: " + deviceLocation.getLongitude(), Toast.LENGTH_LONG).show();
		*/
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//location1.disconnectFromGps();
		 mLocationClient.disconnect();
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "ConnectionFailled", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		 Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		 mLocationClient.requestLocationUpdates(mLocationRequest,this);
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		 if(isBetterLocation(location,currentBestLocation)){
			 currentBestLocation = location;	 
			 
		if(anfangsLocation == null){
	    	 anfangsLocation = 	currentBestLocation;		    	
    	    }
		
		/*String msg = " " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());*/
		newDistance = getDistance(anfangsLocation,currentBestLocation);
		addedDistance += Math.abs(newDistance - distance);
		distance = newDistance;
		
		speed = (int)Math.round((currentBestLocation.getSpeed())*3.6F);
		locationBearing = anfangsLocation.bearingTo(currentBestLocation);
		himmelsrichtungenIndex = Himmelsrichtung(locationBearing);
		locationView.setText("Lat:"+currentBestLocation.getLatitude()+" , Long: "+currentBestLocation.getLongitude());
		anfangsLocationView.setText("Lat:"+anfangsLocation.getLatitude()+" , Long: "+anfangsLocation.getLongitude());
	    distanceView.setText("Entfernung: "+(int)Math.round(distance)+"m");
	    speedView.setText("Geschwindigkeit: "+speed+"km/h");
	    bearingView.setText("Richtung: "+Himmelsrichtungen[himmelsrichtungenIndex]);
	    addedDistanceView.setText("Zurückgelegte Strecke: "+(int)Math.round(addedDistance));
		
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        
		 }    
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
	
	public float getDistance(Location anfangslocation, Location aktuelleLocation){
		float [] dist = new float[1];  	
	    Location.distanceBetween(anfangsLocation.getLatitude(),anfangsLocation.getLongitude(), aktuelleLocation.getLatitude(),aktuelleLocation.getLongitude(),dist);
	    return dist[0];
		    }
	
}


