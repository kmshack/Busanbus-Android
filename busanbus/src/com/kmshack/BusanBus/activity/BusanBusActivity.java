package com.kmshack.BusanBus.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.kmshack.BusanBus.R;

/**
 *  Loading  Activity 
 *  DB업데이트 작업
 * @author kmshack
 *
 */
public class BusanBusActivity extends Activity {
	
	private TextView mMsg;
	
	//최종 업데이트일
	private int mYear = 2012;
	private int mMonth = 12;
	private int mDay = 4;
	private int mHour = 2; //24Hour
	private int mMin = 0; 
	
	private String mStrageDir = Environment.getExternalStorageDirectory().getPath();
	private File mFileDir = new File(mStrageDir + "/Android/data/com.kmshack.BusanBus/databases");
	private File mBusDataFile = new File(mStrageDir + "/Android/data/com.kmshack.BusanBus/databases/BusData.kms");
	
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			Intent mainview = new Intent(getApplicationContext(), SearchMainActivity.class);
			mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainview);
			
			finish();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mMsg = (TextView)findViewById(R.id.msg);
		
		Calendar cal = Calendar.getInstance();
		cal.set(mYear, (mMonth-1), mDay, mHour, mMin, 0); 
		
		if(mBusDataFile.exists() && mBusDataFile.lastModified() < cal.getTimeInMillis()){
			mBusDataFile.delete();
			mMsg.setText(getString(R.string.loading_msg));
		}
		
		new DataTask().execute();
	}
	
	/**
	 *  분할된 파일을 합치는 작업
	 * @author kmshack
	 *
	 */
	class DataTask extends AsyncTask<Boolean, Boolean, Boolean>{

		@Override
		protected Boolean doInBackground(Boolean... arg0) {
			
			if(!mBusDataFile.exists()){
				mFileDir.mkdirs();
			
				AssetManager am = null;
				InputStream[] arrIs = new InputStream[4];
				BufferedInputStream[] arrBis = new BufferedInputStream[4];
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				
				try{
					am = getResources().getAssets();
					
					for(int i = 0; i < arrIs.length; i++){
						arrIs[i] = am.open("BusDataCut" + (i + 1) + ".kms");
						arrBis[i] = new BufferedInputStream(arrIs[i]);
					}
					
					fos = new FileOutputStream(mBusDataFile);
					bos = new BufferedOutputStream(fos);
					int read = -1;
					byte[] buffer = new byte[1024];

					for(int i = 0; i < arrIs.length; i++){
						while((read = arrBis[i].read(buffer, 0, 1024)) != -1){
							bos.write(buffer, 0, read);
						}
						bos.flush();
					}

				}
				catch(Exception e){}
				finally{
					for(int i = 0; i < arrIs.length; i++){
						try{if(arrIs[i] != null) arrIs[i].close();}catch(Exception e){}
						try{if(arrBis[i] != null) arrBis[i].close();}catch(Exception e){}
					}
					try{if(fos != null) fos.close();}catch(Exception e){}
					try{if(bos != null) bos.close();}catch(Exception e){}
					arrIs = null;
					arrBis = null;
				}
			}
			
			return mBusDataFile.exists();
			
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			
			if(result){
				mHandler.sendEmptyMessageDelayed(0, 700);
			}else{
				Toast.makeText(getApplicationContext(), "노선 데이터가 올바르지 않아 실행 할 수 없습니다.", Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}
	
}


