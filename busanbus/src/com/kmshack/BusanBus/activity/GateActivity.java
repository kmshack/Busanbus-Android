package com.kmshack.BusanBus.activity;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;


/**
 * 특정 노선의 정류소 *
 * busanbus://line/detail?nosun=xxxxx&uniqueid=xxxxx&ord=xxxxx&busstopname=xxxxx&updown=xxxxx
 * 
 * 정류소 *
 * busanbus://stop/detail?busstop=xxxxx
 * 
 * 홈 *
 * busanbus://home
 * 
 * @author KMSHACK
 *
 */

public class GateActivity extends BaseActivity {

	String mStrageDir = Environment.getExternalStorageDirectory().getPath();
	File mBusDataFile = new File(mStrageDir + "/Android/data/com.kmshack.BusanBus/databases/BusData.kms");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		if(!mBusDataFile.exists()){
			Intent i = new Intent(this, BusanBusActivity.class);
			startActivity(i);
		}
		
	     Intent intent = getIntent(); 
	     
	     if(Intent.ACTION_VIEW.equals(intent.getAction())) { 
	         Uri uri = intent.getData();
	         String scheme = uri.getScheme();
	         String host = uri.getHost();
	         String path = uri.getPath();
	         if(scheme.equals("busanbus")){
	        	 
	        	 tracker.trackPageView("/NosunDetail"); 
	        	 
	        	 if(host.equals("line")){ //특정 노선의 정류소
	        		 
	        		 if(path.equals("/detail")){
							Intent i = new Intent(this, BusArriveActivity.class);
							i.putExtra("NOSUN", uri.getQueryParameter("nosun"));
							i.putExtra("UNIQUEID", uri.getQueryParameter("uniqueid"));
							i.putExtra("ORD", uri.getQueryParameter("ord"));
							i.putExtra("BUSSTOPNAME", uri.getQueryParameter("busstopname"));
							i.putExtra("UPDOWN", uri.getQueryParameter("updown"));
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							
							tracker.trackEvent(			 
									"Gate",  // Category			 
									"Line",  // Action			 
									"노선 정류소", // Label			 
									0);	   // Value 
	        		 }
	        	 }
	        	 else if(host.equals("stop")){ //정류소
	        		 if(path.equals("/detail")){
						Intent i = new Intent(this, BusstopDetailActivity.class);
						i.putExtra("BusStop", uri.getQueryParameter("busstop"));
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						
						tracker.trackEvent(			 
								"Gate",  // Category			 
								"Stop",  // Action			 
								"정류소", // Label			 
								0);	   // Value 
	        		 }
	        	 }
	        	 else if(host.equals("home")){ // 홈
	        		 Intent i = new Intent(this, BusanBusActivity.class);
	        		 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		 startActivity(i);
	        		 
	        		 tracker.trackEvent(			 
								"Gate",  // Category			 
								"Home",  // Action			 
								"홈", // Label			 
								0);	   // Value 
	        	 }
	         }
	         
	     }
	     
	     
	     finish();
	}
}
