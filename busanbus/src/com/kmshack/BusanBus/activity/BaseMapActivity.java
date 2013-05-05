package com.kmshack.BusanBus.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.kmshack.BusanBus.R;

/**
 * ¸ðµç MapActivityÀÇ Base 
 * @author kmshack
 *
 */
public class BaseMapActivity extends MapActivity {

	private TextView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
	}

	@Override
	public void finish() {
		super.finish();
		this.overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
	}

	public void onAction(View view) {
		switch (view.getId()) {
		case R.id.btn_back_layout:
			this.finish();
			break;
		case R.id.btn_search_layout:
			Intent mainview = new Intent(this, SearchMainActivity.class);
			mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainview);
			break;
		}
	}

	public void disableBack() {
		findViewById(R.id.btn_back).setVisibility(View.GONE);
		findViewById(R.id.btn_back_empty).setVisibility(View.VISIBLE);
	}

	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View parent = (View) inflater.inflate(R.layout.common_layout, null);
		LinearLayout mContentsLayout = (LinearLayout) parent
				.findViewById(R.id.layout_contents);

		mTitle = (TextView) parent.findViewById(R.id.text_top_name);

		inflater.inflate(layoutResID, mContentsLayout);
		super.setContentView(parent);
	}

	public void setTitleLeft(String title) {
		mTitle.setText(title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main:
			Intent mainview = new Intent(this, SearchMainActivity.class);
			mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainview);
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		case R.id.menu_noti:
			Intent webview = new Intent(this, WebActivity.class);
			webview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(webview);
			return true;
		}
		return false;
	}

	@Override
	public boolean onSearchRequested() {

		Intent mainview = new Intent(this, SearchMainActivity.class);
		mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mainview);

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case 0:
			return getProgressDialog();
		}

		return super.onCreateDialog(id);
	}

	public void showDialog() {
		try {
			showDialog(0);
		} catch (Exception e) {
		}
	}

	public void dismissDialog() {
		try {
			dismissDialog(0);
		} catch (Exception e) {
		}
	}

	public Dialog getProgressDialog() {

		Dialog dialog = new Dialog(this, R.style.dialog);
		dialog.setContentView(R.layout.dialog_progress);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				onProgressDialogCancel();
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_SEARCH
						|| keyCode == KeyEvent.KEYCODE_MENU) {

					return true;
				}
				return false;

			}
		});
		return dialog;
	}

	protected void onProgressDialogCancel() {
		finish();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
