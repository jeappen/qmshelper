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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * 
 * @author aditya.polymath & snugghash 
 */
public class Register extends Activity implements OnClickListener {
	String scanContent, scanFormat, result, resPage, discount, user, pass;
	String[][] data = new String[3][];
	Spinner spinner;
	Button buttonScan, buttonEntry, sendTeam, buttonAbort,buttonFlush;
	TextView formatTxt, contentTxt, tv1, tvr1, tvr2;
	EditText et, etAdd;
	int pos;
	long l;
	String[] myStrings;
	boolean scanDone = false, httpRes = false, Is_Team;
	Database db;
	EventDatabase eveDb;
	StringBuilder team = new StringBuilder();
	public ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pos = 0;
		l = getIntent().getLongExtra("position", 0);
		setContentView(R.layout.register_re);
		et = (EditText) findViewById(R.id.etEntry);
		buttonEntry = (Button) findViewById(R.id.bEntry);
		sendTeam = (Button) findViewById(R.id.sendTeam);
		buttonEntry.setOnClickListener(this);
		buttonEntry.setText("Add Entry");
		user = getIntent().getStringExtra("user");
		pass = getIntent().getStringExtra("pass");
		Log.i("regUser", user);
		Log.i("regPass", pass);
		spinner = (Spinner) findViewById(R.id.s1);
		buttonScan = (Button) findViewById(R.id.button1);
		formatTxt = (TextView) findViewById(R.id.tvFor);
		contentTxt = (TextView) findViewById(R.id.tvCon);
		tv1 = (TextView) findViewById(R.id.textView01);
		tvr1 = (TextView) findViewById(R.id.tvr1);
		tvr2 = (TextView) findViewById(R.id.tvr2);
		buttonAbort = (Button) findViewById(R.id.action_abort);
		buttonFlush = (Button) findViewById(R.id.action_flush);
		db = new Database(this);
		buttonScan.setOnClickListener(this);
		buttonAbort.setOnClickListener(this);
		buttonFlush.setOnClickListener(this);
		sendTeam.setOnClickListener(this);
		eveDb = new EventDatabase(this);
		eveDb.open();
		data = eveDb.getData();
		List<String> categories = new ArrayList<String>();
		for (int i = 0; i < data[0].length; i++) {
			categories.add(data[1][i]);
		}
		eveDb.close();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.e("OnItemSelected", ">>" + arg2);
				if (pos != arg2) {
					team.setLength(0);
				}
				pos = arg2;
				Is_Team = data[2][arg2].equals("1");
				// Log.e("Team", ""+data[2][arg2]+" "+is_team);
				if (Is_Team) {
					sendTeam.setVisibility(View.VISIBLE);
					sendTeam.setText("Send/save all members as a team");
					buttonAbort.setVisibility(View.VISIBLE);
					
					//Only one/zero members, hence:
					//sendTeam.setClickable(false);

				} else {
					sendTeam.setVisibility(View.GONE);
				}
				// Clear tvr1 and tvr2 scanFormat scanContent
				tvr1.setText("");
				tvr2.setText("");
				formatTxt.setText("");
				contentTxt.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// change duh!
				Log.e("OnNothingSelected", ">> nothing selected");
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			// start scanning
			try {
				scanIntegrator.initiateScan();
			} catch (Exception e) {
				Log.e("<---Scan Error 1--->", ">>" + e.getMessage());
			}
			tvr1.setText("Turning Camera On...");
			tvr2.setText("");
		} else if (v.getId() == R.id.bEntry) {
			// Log.i("hjh", "hsdgchjsa");
			scanContent = et.getText().toString();
			if (Is_Team) {
				tvr1.setText("Add members and then press 'Send as Team!'");
				if (scanContent != null && !scanContent.isEmpty()) {
					team.append(scanContent);
					if(team.charAt(team.length()-1)!='/')team.append("/");
				} else {
					tvr1.setText("Empty Field");
					Log.e("<--Emplty Field-->", "empty edit text");
				}
				formatTxt.setText("FORMAT: " + scanFormat);
				contentTxt.setText("TEAM MEMBERS: " + team);
				tvr2.setText("");
				//Should this be here?
				//et.setText("");

				//sendTeam.setClickable(true);

			} else {
				if (scanContent != null && !scanContent.isEmpty()) {
					Log.e("test", ">>" + scanContent);
					scanDone = true;
					scanFormat = "1";
					tvr1.setText("Sending...");
					contentTxt.setText("CONTENT: " + scanContent);
					formatTxt.setText("");
					sendData();
				} else {
					tvr1.setText("Empty Field");
					Log.e("<--Emplty Field-->", "empty edit text");	
				}
			}
		} else if (v.getId() == R.id.sendTeam) {
			Log.i("SEND BUTTON", "PRESSED");
			scanContent = team.toString();
			Log.i("TEAM MEMBERS: ", scanContent);
			scanDone = true;
			scanFormat = "2";
			if (scanContent != null && !scanContent.isEmpty() && scanContent.length()!=0) {
				tvr1.setText("Sending...");
				sendData();
				Log.v("scanContent is not empty", scanContent);
			}
			else {
				tvr1.setText("Empty Field");
				tvr2.setText("");
				Log.e("<--Emplty Field-->", "empty edit text");
			}
			team.setLength(0);
			contentTxt.setText("");
			formatTxt.setText("");
		}
		// Abort everything, clear everything except spinner and edit ID on pressing
		// abort.
		else if (v.getId() == R.id.action_abort) {
			Log.i("ABORT BUTTON", "PRESSED");
			tvr1.setText("");
			tvr2.setText("");
			//et.setText(et.getText().append(team));
			team.setLength(0);
			sendTeam.setClickable(true);
			spinner.setClickable(true);
			formatTxt.setText("");
			contentTxt.setText(team);
		}
		else if (v.getId() == R.id.action_flush) {
			et.setText("");
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		// Check for resultCode showing un-executed activity, fixes appcrash.
		if (resultCode == RESULT_CANCELED) {
			Log.v("debug back crash", resultCode + "");
			// invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		// check we have a valid result
		if (scanningResult != null) {
			if (Is_Team) {
				tvr1.setText("Add members and then press 'Send as Team!'");
				scanContent = scanningResult.getContents();
				scanFormat = scanningResult.getFormatName();
				
				//If team length is zero(now it'll be one) disable sendTeam
				//if(team.length()==0)
					//sendTeam.setClickable(false);
				
				if (scanContent != null/* &&!scanContent.isEmpty() */) {
					team.append(scanContent);
					team.append("/");
				}
				formatTxt.setText("FORMAT: " + scanFormat);
				contentTxt.setText("TEAM MEMBERS: " + team);

				et.setText(team);
				// contentTxt.setText(team);

				// Disable spinner before trying to send on team events
				//spinner.setClickable(false);
				//spinner.setActivated(false);
				
				// Enable send team on more than 1 member
				if(sendTeam.isClickable()==false)sendTeam.setClickable(true);

				Log.i("Inside onActivityResult", "reached");
			} else {
				tvr1.setText("Sending...");
				// gcontent from Intent Result
				scanContent = scanningResult.getContents();
				// get format name of data scanned
				scanFormat = scanningResult.getFormatName();
				// output to UI
				formatTxt.setText("FORMAT: " + scanFormat);
				contentTxt.setText("CONTENT: " + scanContent);
				Log.i("Inside onActivityResult", "reached");
				scanDone = true;
				if (scanContent != null) {
					sendData();
				} else {
					tvr1.setText("No data scanned");
					Log.e("<---Error Scan--->", ">>No Data Scanned");
				}
			}
		} else {
			// invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void postData() throws InterruptedException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://erp.saarang.org/mobile/barcode/");
		while (true) {
			if (scanDone == true && scanFormat != null && scanContent != null) {
				try {
					Log.i("Inside postData", "reached");
					Log.i("scan content", scanContent);
										
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					// nameValuePairs.add(new BasicNameValuePair("user", user));
					// nameValuePairs.add(new BasicNameValuePair("pass", pass));
					nameValuePairs.add(new BasicNameValuePair("userid",
							scanContent));
					nameValuePairs.add(new BasicNameValuePair("actionType",
							String.valueOf(l)));
					nameValuePairs.add(new BasicNameValuePair("eventid",
							data[0][pos]));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();
					InputStream is = entity.getContent();
					result = convertStreamToString(is);
					httpRes = true;
					Log.i("RESULT", result);
					resPage = response.getStatusLine().getReasonPhrase();
					Log.i("httpsRes true", "reached");
					Log.i("Page", response.getStatusLine().getReasonPhrase());
				} catch (ClientProtocolException e) {
					Log.e("<---Client Protocol Exception--->", e.getMessage());
				} catch (IOException e) {
					Log.e("<---IO Exception--->", e.getMessage());
					httpRes = false;
				} catch (Exception e) {
					Log.e("<---Inside PostData Other Error--->", e.getMessage());
				}
				break;
			} else {
				Thread.sleep(10);
			}
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

	private void sendData() {
		Thread networkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//TODO Order of things, Async task.
					//TODO Check whether the next two lines screw up flags; if so set them up to run after request in Asynctask
					httpRes = false;
					resPage = "Bad";
					postData();
					// If sending to server works
					if (l == 1 && httpRes && resPage.equals("OK")
							&& (result.charAt(0) == 'S' || result.charAt(0)== 'A')) {
						db.open();
						db.createEntry(scanContent, data[1][pos], data[0][pos],
								1);
						Log.i("SEND", "DONE");
						db.close();
					}
					// If sending to server fails
					else if (l == 1 && (!httpRes || !resPage.equals("OK"))) {
						db.open();
						boolean found = db.findEntry(scanContent, data[0][pos]);
						Log.i("FOUND", "" + found);
						if (!found) {
							db.createEntry(scanContent, data[1][pos],
									data[0][pos], 0);
							Log.i("STORE", "DONE");
						}
						db.close();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					Log.i("Test", e.getMessage());
				}

				// Show different things on failure and otherwise
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						tvr1.setText("Stored internally");
						if (l == 1 && httpRes && resPage.equals("OK"))
							tvr2.setText("Sending Succesful!");
						else if (l == 1 && (!httpRes || !resPage.equals("OK")))
							tvr2.setText("Sending Unsuccesful!");
					}
				});
			}
		});
		networkThread.start();
	}
}
