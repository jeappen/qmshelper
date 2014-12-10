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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
 public class Register2 extends Activity implements OnClickListener{
	String scanContent, scanFormat, result, resPage;
	String[][] data = new String[3][];
    Spinner spinner;
	Button b;
	TextView formatTxt, contentTxt, tv1, tv2;
	int pos;
	long l;
	String[] myStrings;
	boolean scanDone = false, httpRes = false;
	Database db;
	public ProgressDialog mDialog;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1_re);
        l = getIntent().getLongExtra("position", 0);
        Log.i("check l", ""+l);
        spinner = (Spinner) findViewById(R.id.s1);
        b=(Button) findViewById(R.id.button1);
        formatTxt = (TextView) findViewById(R.id.textView1);
        contentTxt = (TextView) findViewById(R.id.textView2);
        tv1 = (TextView) findViewById(R.id.textView03);
        tv2 = (TextView) findViewById(R.id.textView04);
        db = new Database(this);
        EventDatabase eveDb = new EventDatabase(this);
        eveDb.open();
        data=eveDb.getData();
        List<String> categories = new ArrayList<String>();
        for(int i=0;i<data[0].length;i++){
        		categories.add(data[2][i]);
        		Log.i("evedb0", data[0][i]);
        		Log.i("evedb1", data[1][i]);
        }
        //categories.add("test");
        eveDb.close();
        b.setOnClickListener(this);
        ArrayAdapter<String>dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);        
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.button1){
			pos=spinner.getSelectedItemPosition();
			//Toast.makeText(Register.this,"Event selected id :"+ myStrings[2*pos],Toast.LENGTH_SHORT).show();
			//instantiate ZXing integration class
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			//start scanning
			scanIntegrator.initiateScan();
			tv1.setText("Turnig Camera On...");
			tv2.setText("");
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve result of scanning - instantiate ZXing object
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		//check we have a valid result
		if (scanningResult != null) {
			tv1.setText("Sending...");
			//get content from Intent Result
			scanContent = scanningResult.getContents();
			//get format name of data scanned
			scanFormat = scanningResult.getFormatName();
			//output to UI
			formatTxt.setText("FORMAT: "+scanFormat);
			contentTxt.setText("CONTENT: "+scanContent);
			Log.i("Inside onActivityResult", "reached");
			scanDone = true;
			
			
			Thread networkThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						postData();
						//Log.i("l", ""+l);
						//Log.i("httpRes", ""+httpRes);
						//Log.i("resPage", ""+resPage.equals("OK"));
						//Log.i("result", ""+result.charAt(0));
						if(l==1&&httpRes&&resPage.equals("OK")&&result.charAt(0)=='R'){
									db.open();
									//db.createEntry(scanContent, data[1][pos], Integer.parseInt(data[0][pos]), 1);
									Log.i("STORE", "DONE");
									db.close();
						}
						else if(l==1&&(!httpRes||!resPage.equals("OK"))){
							db.open();
							//db.createEntry(scanContent, data[1][pos], Integer.parseInt(data[0][pos]), 0);
							Log.i("STORE", "DONE");
							db.close();
						}
						else if(l==3&&httpRes&&resPage.equals("OK")){
									db.open();
									Log.i("deleteUSER", scanContent);
									Log.i("deleteEvent", data[1][pos]);
									db.deleteEntry(scanContent,data[0][pos]);
									db.close();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                    	if(l==1){
		                    	if(!httpRes){
		                    		result="Unsuccesful!!!";
		                    		resPage="Stored internally";
		                    	}
	                    	}
	                    	else if(l==3){
	                    		if(!httpRes){
	                    			resPage="";
	                    			result="Connection Error";
	                    		}
	                    	}
	                    	tv1.setText(resPage);
	                    	tv2.setText(result);
	                    }
	                });
				}
			});
				networkThread.start();
		}
		else{
			//invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	public void postData() throws InterruptedException {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://www.saarang.iitm.ac.in/android/register.php");
	    while(true){
		    if(scanDone == true&&!scanFormat.isEmpty()&&!scanContent.isEmpty()){
		    	try {
		    		Log.i("Inside postData", "reached");
		    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    		nameValuePairs.add(new BasicNameValuePair("user", "EE12B070"));
			        nameValuePairs.add(new BasicNameValuePair("pass", "webopscoord"));
			        nameValuePairs.add(new BasicNameValuePair("userid", scanContent));
			        nameValuePairs.add(new BasicNameValuePair("actionType", String.valueOf(l)));
			        nameValuePairs.add(new BasicNameValuePair("eventid", data[0][pos]));
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(httppost);
			        HttpEntity entity = response.getEntity();
			        InputStream is = entity.getContent();
			        result = convertStreamToString(is);
			        httpRes = true;
			        Log.i("RESULT", result);
			        resPage= response.getStatusLine().getReasonPhrase();
			        Log.i("httpsRes true", "reached");
			        Log.i("Page", response.getStatusLine().getReasonPhrase());
			    } catch (ClientProtocolException e) {
			        // TODO Auto-generated catch block
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
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
 }