package com.kmshack.BusanBus.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.BusanBusPrefrence;

public class TextSizeSettingActivity extends BaseActivity {
	
	private BusanBusPrefrence mBusanBusPrefrence;
	
	
	private LinearLayout mLayoutSettingArrive;
	private LinearLayout mLayoutSettingFavorite;
	private LinearLayout mLayoutSettingFavoriteLocation;
	
	private ImageView mCheckSettingArrive;
	private ImageView mCheckSettingFavorite;
	private ImageView mCheckSettingFavoriteLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_textsize);
		
		setTitle("글자크기 선택");
		
		mBusanBusPrefrence = BusanBusPrefrence.getInstance(getApplicationContext());
		
		mLayoutSettingArrive = (LinearLayout)findViewById(R.id.setting_arrive_sun);
		mLayoutSettingFavorite = (LinearLayout)findViewById(R.id.setting_favorite_start);
		mLayoutSettingFavoriteLocation = (LinearLayout)findViewById(R.id.setting_favorite_location);
		
		mCheckSettingArrive = (ImageView)findViewById(R.id.setting_check_arrive_sun);
		mCheckSettingFavorite = (ImageView)findViewById(R.id.setting_check_favorite_start);
		mCheckSettingFavoriteLocation = (ImageView)findViewById(R.id.setting_check_favorite_location);
		
		mLayoutSettingArrive.setOnClickListener(mSettingClick);
		mLayoutSettingFavorite.setOnClickListener(mSettingClick);
		mLayoutSettingFavoriteLocation.setOnClickListener(mSettingClick);
		
		loadSelect();
		
	}

	View.OnClickListener mSettingClick = new OnClickListener() {
		
		public void onClick(View v) {
			
			switch (v.getId()) {
				case R.id.setting_arrive_sun:
					mBusanBusPrefrence.setTextSize(0);
					loadSelect();
					
					break;
					
				case R.id.setting_favorite_start:
					mBusanBusPrefrence.setTextSize(2);
					loadSelect();
					
					break;
					
				case R.id.setting_favorite_location:
					mBusanBusPrefrence.setTextSize(4);
					loadSelect();
					
					break;
				
			}
			
		}
	};
	
	private void loadSelect(){
		
		switch (mBusanBusPrefrence.getTextSize()) {
		case 0:
			mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_check);
			mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			
			break;
		case 2:
			mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_check);
			mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			
			break;
		case 4:
			mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_uncheck);
			mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_check);
			
			break;

		}
		
	}
}
