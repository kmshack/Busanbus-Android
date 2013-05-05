package com.kmshack.BusanBus.task;

public class HtmlAsync extends BaseAsyncTask {

	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		if (url == null)
			return null;

		return DownloadHtml(url);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

}
