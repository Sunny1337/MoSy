package com.example.streck_o_graph;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.Display;
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
		
		Location anfangsLocation,currentBestLocation,currentBestLocationOld;
		LocationClient mLocationClient;
		LocationRequest mLocationRequest; 
		TextView locationView;
		Context context;
		int resultCode,himmelsrichtungenIndex;
		boolean connected;
		float speed,locationBearing,newDistanceLine,alpha360,width,height,midX,midY,xNew,yNew,xOld,yOld,distance,addedDistance,newDistance,alpha180;	
		double xWert,yWert;
		static final String Himmelsrichtungen[] = {"N","NE","E","SE","S","SW","W","NW"};
		private static final int TWO_MINUTES = 1000 * 60 * 2;		
	    private static final int MILLISECONDS_PER_SECOND = 1000;
	    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;   
	    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	    
	    int y = 0;
	    List<Float> linien = new ArrayList<Float>(); 
		
		static float[] floatArray = new float[]{0.0f,0.0f,0.0f};
		public static short indices[] = new short[]{0};
		
		
	    
	    
	    
	    public LocationGPS(Context context) {
			// TODO Auto-generated constructor stub
	    	this.context = context;
	    	
	    	//Abfrage ob Google Play Service verfügbar ist
	    	checkForGoogleService();
	    	
			//Location Client Objekt erstellen		
			mLocationClient =  new LocationClient(context, this, this);
			
			//neuen LocationRequest erstellen
			createNewLocationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY,UPDATE_INTERVAL,FASTEST_INTERVAL);
			
			//Display Auflösung abfragen
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			
			Point size = new Point();
			display.getSize(size);
			 width = size.x/2;
			 height = size.y/2;
			 xOld = width;
			 yOld = height;
						 
		}
	    
	    //Methode um die 180°/-180° Werte der Location Klasse in 360° Werte umzurechnen
	    public float degree(float gradzahl){
	    	if(gradzahl < 0){
				gradzahl = 360F+gradzahl;			
			}
	    	return gradzahl;
	    }
	    //Gibt Index fürs Himmelsrichtung Array zum passenden Grad Wert aus
	    public int Himmelsrichtung(float gradzahl){
	    	
	    	 gradzahl = degree(gradzahl);
			
			float v = gradzahl/360F;
			float x = v * 8F;
			int y = (int)Math.round(x);
			
			if(y == 8){
				
				y = 0;
				return y;
				
			}else{
				
				return y;
				
			}
			
		}
	    
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

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
		    if (provider1 == null) {
		      return provider2 == null;
		    }
		    return provider1.equals(provider2);
		}
		
	

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	
		
				//Überprüpft ob die neue Location genauer ist als die alte
				if(isBetterLocation(location,currentBestLocation)){
					currentBestLocationOld = currentBestLocation;
					 currentBestLocation = location;	 
				//setzt die erste empfangene Location als anfangsLocation	 
				if(anfangsLocation == null){
			    	 anfangsLocation = 	currentBestLocation;		    	
		    	    }
			
				
				try{
					
				//Richtung vom alten Standort zum neuen	(von -180° bis +180°)
				alpha180 = currentBestLocationOld.bearingTo(currentBestLocation);
				//Abstand der beiden Standorte
				newDistanceLine = currentBestLocationOld.distanceTo(currentBestLocation);
				//Berechnung der Gesamtstrecke
				addedDistance = addedDistance + newDistance;
				//Berechnung der Geschwindigkeit in km/h
				speed = (int)Math.round((currentBestLocation.getSpeed())*3.6F);
				//Umwandlung der 180° Gradwertes ind 360° Gradwert
				alpha360 = degree(alpha180);
				
			
				
				 }
				catch(NullPointerException e){
					//Toast.makeText(context, "Fehler!:"+e, Toast.LENGTH_SHORT).show();
				}
				//Unwandlung des Gradwertes in Radiant
				double alpha360Rad =  Math.toRadians(alpha360);
				//Kosinus und Sinus der Khateten ausrechnen
				double cosA = Math.cos(alpha360Rad);
				double sinA = Math.sin(alpha360Rad);
				//Die neuen X und Y Werte berechnen | Die Entfernung des neuen Punktes und die Richtung sind bekannt. 
				// Mittels der Trigonometrie die Länger auf der X und der Y Achse berechnen
				float xValue = ((newDistanceLine * (float)sinA)*30);
				float yValue = ((newDistanceLine * (float)cosA)*30);
				//Richtung im Bezug auf den Startpunkt ermitteln
				locationBearing = anfangsLocation.bearingTo(currentBestLocation);
				himmelsrichtungenIndex = Himmelsrichtung(locationBearing);
				
				String text = "Speed: "+speed+" km/h   |  "+Himmelsrichtungen[himmelsrichtungenIndex]+"  |   Distance: "+addedDistance+" m";
						
											
				
				MainActivity.locationView.setText(text);
				
				
				xNew = (xOld + xValue);
			    yNew = (yOld + yValue);
				
				linien.add(xOld);
				linien.add(yOld);
				linien.add(0.0f);
				
				xOld = xNew;
				yOld = yNew;
				
				
				
				y = y +1;
				indices = new short[y];
				floatArray = new float[linien.size()];
				
				for(int x = 0; x <indices.length;x++){
					indices[x] = (short) x;
					
				}
				
				
				int p = 0;
				
				for (Float f : linien) {
				    floatArray[p++] = f; // Or whatever default you want.
				}
				
				
				
				
				
				MainActivity.glSurfaceView.requestRender();

				
				 }
	}
	
	public static float[] getVertices(){
		return floatArray;
	}
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
