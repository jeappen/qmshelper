package org.shaastra.qmshelper.reused;



import org.shaastra.qmshelper.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ResponseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response_re);
		
		Bundle extras = getIntent().getExtras();
		String data;
		if (savedInstanceState == null) {
		    extras = getIntent().getExtras();
		    if(extras == null) {
		        data= null;
		    } else {
		        data= extras.getString("response");
		    }
		} else {
		    data= (String) savedInstanceState.getSerializable("response");
		}
		
		String mime = "text/html";
		String encoding = "utf-8";
		WebView myWebView = (WebView)this.findViewById(R.id.webView);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setBuiltInZoomControls(true);
		myWebView.setWebViewClient(new WebViewClient());
		
		myWebView.loadData(data, mime, encoding);
		//myWebView.loadUrl("http://erp.saarang.org/main/register/35");
		//myWebView.loadDataWithBaseURL(null, html, mime, encoding, null);
	}

}
