package com.kmshack.BusanBus.task;

public class HtmlsAsync extends BaseAsyncTask{

	private String Title;
	private String Next;
	
	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		if(url == null)
			return null;
		
		return DownloadHtml(url);
	}
	
	public void setTitle(String s){
		Title = s;
	}
	
	public String getTitle(){
		return Title;
	}
	
	public void setNext(String s){
		Next = s;
	}
	
	public String getNext(){
		return Next;
	}
	
}
