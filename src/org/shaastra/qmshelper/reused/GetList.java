package org.shaastra.qmshelper.reused;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.shaastra.qmshelper.R;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

@SuppressLint("HandlerLeak")
public class GetList extends ListActivity {
	String result, resPage;
	EditText et;
	String[] List;
	String[] toFill;
	boolean httpRes;
	private ProgressDialog mDialog;
	ArrayAdapter<String> adapter;
	TextWatcher tw;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				adapter = new ArrayAdapter<String>(GetList.this,
						android.R.layout.simple_list_item_1, toFill);
				setListAdapter(adapter);
				setContentView(R.layout.get_list_re);
				et = (EditText) findViewById(R.id.et);
				// et.addTextChangedListener(tw);
				et.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence cs, int arg1,
							int arg2, int arg3) {
						// When user changed the Text
						GetList.this.adapter.getFilter().filter(cs);
					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}
				});
			} else if (msg.what == 0)
				mDialog.dismiss();
			else if (msg.what == 2)
				mDialog = ProgressDialog.show(GetList.this, "Connection Error",
						"Cannot connect to server", true);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mDialog = ProgressDialog.show(GetList.this, "Fetching Data",
				"Loading Please Wait", true);
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					postData();
					if (httpRes) {
						List = result.split(":");
						// Log.i("Test", "******** " + rollNum.length + "***");
						toFill = new String[(List.length) / 4 + 1];
						toFill[0] = "Userid		Event";
						for (int i = 0; i < (List.length / 4); i++) {
							String s = "";
							s = s + List[4 * i] + "	 ";
							s += List[4 * i + 1];
							toFill[i + 1] = s;
						}
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(0);
						handler.sendEmptyMessage(2);
						Thread.sleep(3000);
						handler.sendEmptyMessage(0);
						startActivity(new Intent(GetList.this, Choose.class));
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * runOnUiThread(new Runnable() {
				 * 
				 * @Override public void run() { if(httpRes){
				 * //et.addTextChangedListener(tw); } } });
				 */
			}
		});
		networkThread.start();
	}

	public void postData() throws InterruptedException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://www.erp.saarang.org/mobile/barcode/");
		try {
			Log.i("Inside postData", "reached");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			//TODO verify the correct flow
			//nameValuePairs.add(new BasicNameValuePair("user", user));
			//nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("eventid", "" + 1));
			nameValuePairs.add(new BasicNameValuePair("actionType", String
					.valueOf(2)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response;
			if((response = httpclient.execute(httppost))==null);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			result = convertStreamToString(is);
			httpRes = true;
			resPage = response.getStatusLine().getReasonPhrase();
			Log.i("httpsRes true", "reached");
			Log.i("RESULT", result);
			Log.i("Page", response.getStatusLine().getReasonPhrase());
			
			//Send intent to WebView to show the response
//			Intent intent = new Intent(this,
//			ResponseActivity.class);
//			intent.putExtra("response", result);
//			startActivity(intent);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "::"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}