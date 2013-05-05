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

/**
 * 공지 사항
 * @author kmshack
 *
 */
public class WebActivity extends BaseActivity {

	private WebView mWebView;
	private ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		setTitleLeft("공지사항");

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.scrollTo(0, 170);
		mWebView.loadUrl("http://kmshack.tistory.com/m/post/list?categoryId=416088");
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress >= 100) {
					mProgress.setVisibility(View.GONE);
					mWebView.setVisibility(View.VISIBLE);
					mWebView.scrollTo(0, 170);
				} else {
					mWebView.setVisibility(View.INVISIBLE);
					mProgress.setVisibility(View.VISIBLE);
				}

			}
		});
		mWebView.setWebViewClient(new EventWebViewClient());

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class EventWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			try {
				URI uri = new URI(url);

				if (uri.getScheme().equals("http")
						|| uri.getScheme().equals("https")) {
					view.loadUrl(url);
				} else {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(i);
				}
			} catch (URISyntaxException e) {
			}

			return true;
		}
	}

}
