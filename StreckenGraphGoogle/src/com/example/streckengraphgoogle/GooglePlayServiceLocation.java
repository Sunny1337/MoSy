package com.example.streckengraphgoogle;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class GooglePlayServiceLocation implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
Context context;
int resultCode;

LocationClient mLocationClient;
Location mCurrentLocation;

	public GooglePlayServiceLocation(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		 mLocationClient =  new LocationClient(context, this, this);
		 mLocationClient.connect();

		/*?*/ //mCurrentLocation = mLocationClient.getLastLocation();


	}
	
	public boolean checkConnection(){
		resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	
		if(ConnectionResult.SUCCESS == resultCode){
			 return true;
		}
		else{
			 return false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "ConnectionFailled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		 // Display the connection status
        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(context, "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();
	}
	
	public void connectToGps(){
		//mLocationClient.connect();
		
	}
	public void disconnectFromGps(){
		// mLocationClient.disconnect();
	}
	public Location getTheLocation(){
		return mLocationClient.getLastLocation();
		
	}
	
}
