package org.shaastra.qmshelper.reused;

import java.io.BufferedReader;
import java.io.File;
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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Choose extends ListActivity {
	int pos, count = 0;
	Thread networkThread;
	Boolean httpRes = false;
	String result, resPage, user, pass;
	EventDatabase db;
	Database info;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = getIntent().getStringExtra("user");
		pass = getIntent().getStringExtra("pass");
		Log.i("chooseUser", user);
		Log.i("choosePass", pass);

		String[] choices = { "Scan and Register", "Get list from server",
				"Refresh Event List", "Show scanned data", "Export to csv",
				"Send exported csv" };
		setListAdapter(new ArrayAdapter<String>(Choose.this,
				android.R.layout.simple_list_item_1, choices));
		setContentView(R.layout.choose_re);
	}

	public ProgressDialog mDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				mDialog.dismiss();
			else if (msg.what == 1)
				mDialog = ProgressDialog.show(Choose.this, "Fetching Data",
						"Loading Please Wait", true);
			else if (msg.what == 2)
				mDialog = ProgressDialog.show(Choose.this, "Connection Error",
						"Cannot connect to server", true);
			else if (msg.what == 3)
				mDialog = ProgressDialog.show(Choose.this,
						"Refreshing Event List", "Loading Please Wait", true);
		}
	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		pos = position + 1;
		db = new EventDatabase(this);
		info = new Database(this);
		context = getApplicationContext();
		networkThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					switch (pos) {
					case 1:
						Intent reg = new Intent(Choose.this, Register.class);
						reg.putExtra("position", (long) (pos));
						reg.putExtra("user", user);
						reg.putExtra("pass", pass);
						startActivity(reg);
						break;
//					case 2:
//						Log.v("in choose","case 2");
//						// TODO fix app crash on no internet connection
//						// Intent list = new Intent(Choose.this, GetList.class);
//						// list.putExtra("user", user);
//						// list.putExtra("pass", pass);
//						Toast.makeText(context,
//								"Disabled,  wait for it!", Toast.LENGTH_SHORT)
//								.show();
//						// startActivity(list);
//						break;
					case 3:
						handler.sendEmptyMessage(3);
						postData();
						if (httpRes) {
							String[] myStrings = result.split(":");
							db.open();
							db.update();
							for (int i = 1; i < myStrings.length; i++) {
								// Log.e(""+i, ""+myStrings[i]);
								if (i % 3 == 0)
									db.createEntry(myStrings[i - 1],
											myStrings[i - 2], myStrings[i]);
							}
							db.close();
							handler.sendEmptyMessage(0);
						} else {
							handler.sendEmptyMessage(0);
							handler.sendEmptyMessage(2);
							Thread.sleep(2500);
							handler.sendEmptyMessage(0);
						}
						break;
					case 4:
						Intent table = new Intent(Choose.this, Display.class);
						table.putExtra("user", user);
						table.putExtra("pass", pass);
						startActivity(table);
						break;
					case 5:
						info.open();
						try {
							info.dbToCsv();
						} catch (IOException e) {
							e.printStackTrace();
						}
						info.close();
						break;
					case 6:
						File file = new File("/sdcard/data.csv");
						String filelocation = "/mnt/sdcard/data.csv";
						Intent sharingIntent = new Intent(Intent.ACTION_SEND);
						//sharingIntent.setType("vnd.android.cursor.dir/email");
						sharingIntent.setType("text/html");
						String[] to = {"saarang-webops-2014@googlegroups.com"};
						sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
						sharingIntent.putExtra(Intent.EXTRA_STREAM,
								Uri.fromFile(file));
						sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Data form barcode scanner app");
						startActivity(Intent.createChooser(
								sharingIntent, "Send email"));
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		networkThread.start();
	}

	public void postData() throws InterruptedException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://erp.saarang.org/mobile/barcode/");
		try {
			Log.i("Inside postData", "reached");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("user", user));
			nameValuePairs.add(new BasicNameValuePair("pass", pass));
			nameValuePairs.add(new BasicNameValuePair("actionType", String
					.valueOf(4)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			result = convertStreamToString(is);
			httpRes = true;
			resPage = response.getStatusLine().getReasonPhrase();
			Log.i("httpsRes true", "reached");
			Log.i("RESULT", result);
			Log.i("Page", response.getStatusLine().getReasonPhrase());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
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
}
