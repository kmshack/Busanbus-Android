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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.utils.Utils;

public class BusanBusActivity extends Activity {

	private TextView mMsg;

	//데이터 최신 버전 날짜
	private int mYear = 2013;
	private int mMonth = 11;
	private int mDay = 25;
	private int mHour = 13; // 24
	private int mMin = 40;

	private File mDir;
	private File mFile;

	public Handler startHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent mainview = new Intent(getApplicationContext(), SearchMainActivity.class);
			mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainview);

			finish();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mDir = Utils.getDbDirectory(getApplicationContext());
		mFile = Utils.getDbFile(getApplicationContext());

		mMsg = (TextView) findViewById(R.id.msg);

		Calendar cal = Calendar.getInstance();
		cal.set(mYear, (mMonth - 1), mDay, mHour, mMin, 0);

		if (mFile.exists() && mFile.lastModified() < cal.getTimeInMillis()) {
			mFile.delete();
			mMsg.setText(getString(R.string.loading_msg));
		}
		
		else if(getIntent()!=null){
			Bundle bundle = getIntent().getExtras();
			if(bundle!=null){
				if(bundle.getBoolean("reload", false)){
					mMsg.setText(getString(R.string.reloading_msg));
				}
			}
		}
		
		
		

		new DataTask().execute();
	}

	class DataTask extends AsyncTask<Boolean, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... arg0) {
			
			if (!mFile.exists()) {
				mDir.mkdirs();

				AssetManager am = null;
				InputStream[] arrIs = new InputStream[4];
				BufferedInputStream[] arrBis = new BufferedInputStream[4];
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;

				try {
					am = getResources().getAssets();

					for (int i = 0; i < arrIs.length; i++) {
						arrIs[i] = am.open("BusDataCut" + (i + 1) + ".kms");
						arrBis[i] = new BufferedInputStream(arrIs[i]);
					}

					fos = new FileOutputStream(mFile);
					bos = new BufferedOutputStream(fos);
					int read = -1;
					byte[] buffer = new byte[1024];

					for (int i = 0; i < arrIs.length; i++) {
						while ((read = arrBis[i].read(buffer, 0, 1024)) != -1) {
							bos.write(buffer, 0, read);
						}
						bos.flush();
					}

				} catch (Exception e) {
				} finally {
					for (int i = 0; i < arrIs.length; i++) {
						try {
							if (arrIs[i] != null)
								arrIs[i].close();
						} catch (Exception e) {
						}
						try {
							if (arrBis[i] != null)
								arrBis[i].close();
						} catch (Exception e) {
						}
					}
					try {
						if (fos != null)
							fos.close();
					} catch (Exception e) {
					}
					try {
						if (bos != null)
							bos.close();
					} catch (Exception e) {
					}
					arrIs = null;
					arrBis = null;
				}
			}

			return mFile.exists();

		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) {
				startHandler.sendEmptyMessageDelayed(0, 800);
			} else {
				Toast.makeText(getApplicationContext(), "데이터에 문제가 있어 실행 할 수 없습니다.", Toast.LENGTH_LONG).show();
				finish();
			}
		}

	}

}
