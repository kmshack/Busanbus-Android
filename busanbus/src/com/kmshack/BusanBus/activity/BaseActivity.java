package com.kmshack.BusanBus.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.kmshack.BusanBus.R;

public class BaseActivity extends SherlockFragmentActivity {

	public ActionBarHelper mActionBar;

	public GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-YOUR-TRACKING-ID", 60, this);

		super.onCreate(savedInstanceState);
		setOnStartingAnimation();

		mActionBar = createActionBarHelper();
		mActionBar.init();

	}

	@Override
	public void finish() {
		super.finish();
		setOnEndingAnimation();
	}

	protected void setOnStartingAnimation() {
		this.overridePendingTransition(R.anim.start_enter_right, R.anim.start_exit_left);
	}

	protected void setOnEndingAnimation() {
		this.overridePendingTransition(R.anim.end_enter_right, R.anim.end_exit_left);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();

	}

	public ActionBarHelper getActionBarHelper() {
		return mActionBar;
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

	
	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if(item.getItemId() == R.id.search){
			Intent mainview = new Intent(this, SearchMainActivity.class);
			mainview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainview);
		}

		return super.onOptionsItemSelected(item);
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
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				onProgressDialogCancel();
			}
		});
		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_MENU) {

					return true;
				}
				return false;

			}
		});
		return dialog;
	}

	protected void onProgressDialogCancel() {
	}

	public ActionBarHelper createActionBarHelper() {
		return new ActionBarHelper();
	}

	class ActionBarHelper {
		private final ActionBar mActionBar;
		private CharSequence mDrawerTitle;
		private CharSequence mTitle;

		private ActionBarHelper() {
			mActionBar = getSupportActionBar();
		}

		public void init() {
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setHomeButtonEnabled(true);
			mTitle = mDrawerTitle = getTitle();
			mActionBar.setIcon(R.drawable.actionbar_icon);
			mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#42609C")));
			mActionBar.setLogo(R.drawable.actionbar_icon);
		}

		/**
		 * When the drawer is closed we restore the action bar state reflecting
		 * the specific contents in view.
		 */
		public void onDrawerClosed() {
			mActionBar.setTitle(mTitle);
		}

		/**
		 * When the drawer is open we set the action bar to a generic title. The
		 * action bar should only contain data relevant at the top level of the
		 * nav hierarchy represented by the drawer, as the rest of your content
		 * will be dimmed down and non-interactive.
		 */
		public void onDrawerOpened() {
			mActionBar.setTitle(mDrawerTitle);
		}

		public void setTitle(CharSequence title) {
			mTitle = title;
		}
	}

}
