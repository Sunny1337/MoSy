package com.example.streckengraphgoogle;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CheckforGooglePlayServices {
Context context;
int resultCode;
	public CheckforGooglePlayServices(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
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
	
}
