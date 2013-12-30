package com.kmshack.BusanBus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.kmshack.BusanBus.App;

public class SizeableTextView extends TextView {

	public SizeableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		for(int i = 0 ; i < attrs.getAttributeCount() ; i++){
	        String attributeName = attrs.getAttributeName(i);
	        
	        if(attributeName.equals("textSize")){
	        	int size = Integer.parseInt(attrs.getAttributeValue(i).replace(".0sp", ""));
	        	super.setTextSize(TypedValue.COMPLEX_UNIT_SP, size + App.FONT_SIZE);
	        	
	        }
	    }
	    
	}
}
