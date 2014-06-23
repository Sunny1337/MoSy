package com.example.streckograph;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class LocationGPS implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener {
	
	
	//Variablen
		
		Location startLocation,currentBestLocation,currentBestLocationOld;
		LocationClient mLocationClient;
		LocationRequest mLocationRequest; 
		TextView locationView, speedView, distanceView, startLocationView,bearingView,addedDistanceView;
		Context context;
		List<Float> pathArray = new ArrayList<Float>(); 
		int resultCode;
		float speed,locationBearing,newDistancePath,alpha360, oldDistancePath,addedDistance,newDistance,alpha180,width, height,degree360,xValue,yValue;
		boolean connected;
		int y = 0;
		double opposite = 0;
		double adjacent = 0;
		double cosA,sinA;
		static float[] floatArray = new float[]{0.0f,0.0f,0.0f};
		float [] dist = new float[1];
		double alpha360Rad;
		public static short indices[] = new short[]{0};
		private static final int TWO_MINUTES = 1000 * 60 * 2;
		static final String DIRECTIONS[] = {"N","NE","E","SE","S","SW","W","NW"};
	    private static final int MILLISECONDS_PER_SECOND = 1000;// Milliseconds per second
	    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;    // Update frequency in seconds
	    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;// Update frequency in milliseconds
	    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;// The fastest update frequency, in seconds
	    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;// A fast frequency ceiling in milliseconds
	   
	    
	   float i = 0;
	    
	    public LocationGPS(Context context) {
			// TODO Auto-generated constructor stub
	    	//Context übergeben
	    	this.context = context;
	    	checkForGoogleService();
	    	//set Location Client		
			mLocationClient =  new LocationClient(context, this, this);	
			//neue Location Abfrage erstellen
			createNewLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY,UPDATE_INTERVAL,FASTEST_INTERVAL);
			
			//Abfragenden der BildschirmBreite und Höhe
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics metrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(metrics);
			 			
			 width = metrics.heightPixels;
			 height =  metrics.widthPixels;
			 //der Wegpfad beginnt in der mitte des Bildschirms
			 yValue = height/2;
			 xValue = width/2;
			
		}
	    
	    // Methode zum umrechnen des +/- 180° Wertes der Location Klasse
	    public float toDegree360(float degree180){
	    	if(degree180 < 0){
				degree360 = 360F+degree180;			
			}
	    	return degree360;
	    }
	    
	    //Methode zur Umwandlung der Gradzahl in eine Himmelsrichtung
	    public String CompassDirection(float degree180){
	    	
	    	degree360 = toDegree360(degree180);
			
			float i = degree180/360F;
			float x = i * 8F;
			int y = (int)Math.round(x);
			
			if(y == 8){
				
				y = 0;
				
				return DIRECTIONS[y] ;
				
			}else{
				
				return DIRECTIONS[y] ;
				
			}
			
		}
	    //Fragt die Verfügbarkeit des Google Play Services ab
	    public void checkForGoogleService(){
	    	resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	    	if(ConnectionResult.SUCCESS == resultCode){
				Toast.makeText(context, "GooglePlayService is available",Toast.LENGTH_LONG).show();
				
			}else{
				Toast.makeText(context, "GooglePlayService is NOT available",Toast.LENGTH_LONG).show();
			}
	    }
	    
	    public void createNewLocationRequest(int priority,Long interval,long fastestInterval){
	    	
	    	// Create the LocationRequest object
	        mLocationRequest = LocationRequest.create();
	        // Use high accuracy
	        mLocationRequest.setPriority(priority);
	        // Set the update interval to 5 seconds
	        mLocationRequest.setInterval(interval);
	        // Set the fastest update interval to 1 second
	        mLocationRequest.setFastestInterval(fastestInterval);
	       //create a new location Request
			
	      
	    }
	    
	    // vergleicht bisherige Location mit neuer Location auf Genauigkeit,Aktualität und den Provider
	    
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		    if (currentBestLocation == null) {
		        // A new location is always better than no location
		        return true;
		    }

		    // Check whether the new location fix is newer or older
		    long timeDelta = location.getTime() - currentBestLocation.getTime();
		    boolean isSignificantlyNewer = timeDelta >  TWO_MINUTES;
		    boolean isSignificantlyOlder = timeDelta < - TWO_MINUTES;
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
		    boolean isSignificantlyLessAccurate = accuracyDelta > 5;

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

		
		private boolean isSameProvider(String provider1, String provider2) {
		    if (provider1 == null) {
		      return provider2 == null;
		    }
		    return provider1.equals(provider2);
		}
		
		
	
		//wird aufgerufen wenn sich der Standort des Smartphone ändert
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		//Überprüpft ob die neue Location genauer ist als die alte
			if(isBetterLocation(location,currentBestLocation)){
				currentBestLocationOld = currentBestLocation;
				currentBestLocation = location;	 
				//setzt die erste empfangene Location als startLocation	 
				if(startLocation == null){
			    	 startLocation = 	currentBestLocation;		    	
		    	    }
				
				
				try{
					//Richtung von der altenLocation zur neuen Location	
					alpha180 = currentBestLocationOld.bearingTo(currentBestLocation);
					//Richtungswert in 360° Wert umwandeln
					alpha360 = toDegree360(alpha180);
					//Umwandlung in Radiand
					alpha360Rad =  Math.toRadians(alpha360);
					//Ausrechnen der Cosinus/Sinus von alpha
					
					cosA = Math.cos(alpha360Rad);
					sinA = Math.sin(alpha360Rad);
					
					
					//Entfernung der alten Location zur aktuellen Location in meter
					newDistancePath = currentBestLocationOld.distanceTo(currentBestLocation);
					
					//Location.distanceBetween(currentBestLocationOld.getLatitude(),currentBestLocationOld.getLongitude(),currentBestLocation.getLatitude(),currentBestLocation.getLongitude(),dist);
					//newDistance = dist[0];
					
					try{
						//Berechnung der Gesamtstrecke
						addedDistance += (int) Math.round(Math.abs(newDistancePath));
						
						//ldDistancePath = newDistancePath;
						
						//Geschwindigkeit in km/h
						speed = (int)Math.round((currentBestLocation.getSpeed())*3.6F);
								 
							
						//Berechnen der Richtung zum Startpunkt		
						locationBearing = startLocation.bearingTo(currentBestLocation);
						String text = "Speed: "+speed+" km/h   |  "+CompassDirection(locationBearing)+"  |   Distance: "+addedDistance+" m";
						//TextView ind der MainActivity setzen
						MainActivity.locationView.setText(text);
						
						}catch(IndexOutOfBoundsException e){
						
					}
					
				}
				catch(NullPointerException e){
					
				}
				
						
				
				
								
				//Array in dem die Koordinaten für den Wegpfad gespeichert werden
				
				//Y-Wert
				pathArray.add(yValue);
				//X-Wert
				pathArray.add(xValue);
				
				//Z-Wert
				pathArray.add(0.0f);
				//setzen der neuen x/y Werte mit richtiger Richtung 
				xValue = xValue+((newDistancePath * (float)sinA));
				yValue = yValue+((newDistancePath * (float)cosA));
				
				
				
				
				y = y +1;
				
				indices = new short[y];
				
				floatArray = new float[pathArray.size()];
				
				for(int x = 0; x <indices.length;x++){
					indices[x] = (short) x;
					
				}
				
				
				
				
				//Array Liste mit den Pfad werten in ein float Array umwandeln
				int p = 0;
				for (Float f : pathArray) {
				    floatArray[p++] = f; 
				}
				
				
				
				
				//Render auforderung
				MainActivity.glSurfaceView.requestRender();
				
			}
	}
	//Zum übergeben der Pfadpunkte
	public static float[] getVertices(){
		return floatArray;
	}
	//Zum übergeben der Indizes
	public static short[] getIndices(){
		return indices;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		 Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
		 mLocationClient.requestLocationUpdates(mLocationRequest,this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();
	}

	
	public void connect(){
		mLocationClient.connect();		
	}
	public void disconnect(){
		mLocationClient.disconnect();
	}
}
