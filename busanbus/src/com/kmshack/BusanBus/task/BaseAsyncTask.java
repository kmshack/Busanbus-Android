package com.kmshack.BusanBus.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class BaseAsyncTask extends AsyncTask<String, String, String>{

	public PostListener onPost = null;
	
	@Override
	protected void onPostExecute(String result) {
		
		onPost.onPost(result);
		super.onPostExecute(result);
	}
	
	public void setOnTapUpListener(PostListener listener) {   
		onPost = listener;   
	}   
	
   public interface PostListener {   
		void onPost(String result);   
	}

	@Override
	protected String doInBackground(String... params) {
		return null;
	}  
	
	public String DownloadHtml(String addr) {
		HttpGet httpget = new HttpGet(addr);
		DefaultHttpClient client = new DefaultHttpClient();
		StringBuilder html = new StringBuilder(); 
		try {
			HttpResponse response = client.execute(httpget);
			BufferedReader br = new BufferedReader(new 
					InputStreamReader(response.getEntity().getContent(), "euc-kr")); //"euc-kr" ¾È³ÖÀ¸¸é ÇÑ±Û ±úÁü!!!!!!!!!
			for (;;) {
				String line = br.readLine();
				if (line == null) break;
				html.append(line + '\n'); 
			}
			br.close();
		} catch (Exception e) {;}
	
		return html.toString();
	}
	

}
