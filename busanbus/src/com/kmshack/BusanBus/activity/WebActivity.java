package com.kmshack.BusanBus.activity;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kmshack.BusanBus.R;

public class WebActivity extends BaseActivity {

	WebView webview;
	ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		tracker.trackPageView("/Notice"); 
		
		setTitle("공지사항");
		
		webview = (WebView)findViewById(R.id.webview);
		webview.loadUrl("http://busanbus.tistory.com/m/post/list");
		progress = (ProgressBar)findViewById(R.id.progress);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient(){
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
			  if(newProgress >= 100){
				  progress.setVisibility(View.GONE);
				  webview.setVisibility(View.VISIBLE);
			  }
			  else{
				  webview.setVisibility(View.INVISIBLE);
				  progress.setVisibility(View.VISIBLE);
			  }
			  
		}});
		webview.setWebViewClient(new EventWebViewClient());
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	    	webview.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	private class EventWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			
			try {
				URI uri= new URI(url);
				
				if(uri.getScheme().equals("http") || uri.getScheme().equals("https")){
					view.loadUrl(url);
				}
				else{
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(i);
				}
			} catch (URISyntaxException e) {}
			
			return true;
		}
	}
	
	




}
