package org.shaastra.qmshelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaastra.qmshelper.R;
import org.shaastra.qmshelper.RegisteredUser.ExistingDetails;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchUser extends Activity {
	Button search_email_but,search_sid_but;
	EditText emailET,sidET;
	String user,pass,email,sid,token;
	Singleton app;
	
	String tx;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (Singleton) getApplicationContext();
		user = null;//getIntent().getStringExtra("user");
		pass = null;//getIntent().getStringExtra("pass");
		token=null;
		tx=null;
		tx="empty";
		user = "test";//getIntent().getStringExtra("user");
		pass = "test";
		Log.i("chooseUser", user);
		Log.i("choosePass", pass);
		setContentView(R.layout.search_user);

		search_email_but = (Button) findViewById(R.id.search_email);
		search_sid_but= (Button) findViewById(R.id.search_sid);
		emailET=(EditText)findViewById(R.id.emailET);
		sidET=(EditText)findViewById(R.id.sidET);
		
		
	
		
		
		search_email_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager
						.getActiveNetworkInfo();
				final boolean a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
				
					email=emailET.getText().toString();
					sid="";
					//new loadUser().execute();
					if (a) {
						if(email.isEmpty())
							Toast.makeText(getApplicationContext(), "Please enter an email ID", Toast.LENGTH_SHORT).show();
						else{
							try{
								new getAuth().execute();
							}
							catch(Exception e){
								Toast.makeText(getApplicationContext(), "ERR: Connection Problem", Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						Toast.makeText(
								getApplicationContext(),
								"No internet connection. Check your connection and "
										+ "try again later", Toast.LENGTH_SHORT).show();
					}
					}
				});
		search_sid_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager
						.getActiveNetworkInfo();
				final boolean a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
				
					email="";
					sid=sidET.getText().toString();
					Integer i=sid.indexOf("15"),sizeofuserid=5;
					if (a) {
						if(i==-1 || sid.length()-i!=sizeofuserid+2 ) //check if shaastra id is 5 digits
							Toast.makeText(getApplicationContext(), "Please enter a valid shaastra ID", Toast.LENGTH_LONG).show();
						else{	
						sid=sid.substring(i+2);
						sid=sid.replaceFirst("^0+(?!$)", "");
						Log.d("sid",sid);
							try{
								new getAuth().execute();
							}
							catch(Exception e){
								Toast.makeText(getApplicationContext(), "ERR: Connection Problem", Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						Toast.makeText(
								getApplicationContext(),
								"No internet connection. Check your connection and "
										+ "try again later", Toast.LENGTH_SHORT).show();
					}
					}
					
				
				
				});
	
		}
	class getAuth extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		boolean error=false;
		JSONParser jsonParser = new JSONParser();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchUser.this);
			pDialog.setMessage("Searching...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			final List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			
			final String admintoken = app.getadmintoken();
			String url = "api/mobile/users/";
			
			if(!email.isEmpty())
				paramse.add(new BasicNameValuePair("email", email));
			else if(!sid.isEmpty())
				paramse.add(new BasicNameValuePair("id", sid));
			JSONObject json;
			json = jsonParser.makeHttpRequest(url, "GET", paramse, admintoken);
			
			try {
				json=json.getJSONObject("data");
				token = json.getString("token");
				email=json.getString("email");

				Log.d("GetAuth Results",json.toString());
			} catch (JSONException e) {
				error=true;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if(error)
				Toast.makeText(getApplicationContext(), "ERR: User not found", Toast.LENGTH_LONG).show();
			else{
				Toast.makeText(getApplicationContext(), "User found", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(SearchUser.this, RegisteredUser.class);
				intent.putExtra("email", email);
				intent.putExtra("token", token);
				Log.d("sending",token);
				startActivity(intent);
			}
		}
	}
	

}
