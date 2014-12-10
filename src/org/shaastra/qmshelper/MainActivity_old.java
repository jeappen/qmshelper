package org.shaastra.qmshelper;

import java.security.spec.ECFieldF2m;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity_old extends FragmentActivity {

	TextView tv;
	int type = 0;
	String tx = null, error = null;
	String userName, password,first_name, last_name, email, user_id;
	EditText etuserName, etpassword;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences uid = getSharedPreferences("uid", MODE_PRIVATE);
		String token = uid.getString("uid", "text");
		if (token != "text") {
			Intent open = new Intent("org.shaastra.eventregistration.Menu");
			startActivity(open);
		}
		setContentView(R.layout.main_login);
		// tv = (TextView) findViewById(R.id.textView2);
		etuserName = (EditText) findViewById(R.id.editText1);
		etpassword = (EditText) findViewById(R.id.editText2);
		Button login = (Button) findViewById(R.id.Button1);
		Button register = (Button) findViewById(R.id.Button2);

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean a = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager
						.getActiveNetworkInfo();
				a = activeNetworkInfo != null
						&& activeNetworkInfo.isConnected();
				if (a) {
					try {
						new loadUser().execute();
					} catch (Exception e) {
					}
				} else {
					Toast.makeText(
							MainActivity_old.this,
							"No internet connection. Check your connection and "
									+ "try again later", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) { 
			//	Intent mainPage = new Intent(MainActivity.this, Register.class);
			//	startActivity(mainPage); 
				
			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	class loadUser extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity_old.this);
			pDialog.setMessage("Signing in...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			userName = etuserName.getText().toString();
			password = etpassword.getText().toString();
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", userName));
				params.add(new BasicNameValuePair("password", password));

				// Posting user data to script
				String LOGIN_URL = "participant_login/";
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						params, null);
				
				tx = null;
				try {
					tx = json.getString("token");
					first_name=json.getString("first_name");
					last_name=json.getString("last_name");
					email=json.getString("email");
					user_id=json.getString("user_id");
				} catch (Exception e) {
					error = json.getString("non_field_errors");
					type = 1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();

			if (tx != null) {
				SharedPreferences uid = getSharedPreferences("uid",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = uid.edit();
				editor.putString("uid", tx);
				editor.putString("user", userName);
				editor.commit();
				new GetWalls().execute();
			} else {
				Toast.makeText(MainActivity_old.this, "Wrong credentials.",
						Toast.LENGTH_SHORT).show();

			}

		}

	}

	class GetWalls extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// pDialog = new ProgressDialog(MainActivity.this);
			// pDialog.setMessage("Signing in...");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(true);
			// pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
/*
			try {

				String LOGIN_URL = "api/mobile/walls/";
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("usernam", "aqel"));
				SharedPreferences uid = getSharedPreferences("uid",
						MODE_PRIVATE);
				String token = uid.getString("uid", "Aaa");
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "GET",
						params1, token);
				int status = json.getInt("status");
				if (status == 1) {
					JSONArray MainObject = json.getJSONArray("data");
					SharedPreferences.Editor editor = uid.edit();
					editor.putInt("noWalls", MainObject.length());
					for (int i = 0; i < MainObject.length(); i++) {
						JSONObject jsonInside = MainObject.getJSONObject(i);
						String name = jsonInside.getString("name");
						int id = jsonInside.getInt("id");
						Log.d("wall", name);
						try {
							JSONObject person = jsonInside
									.getJSONObject("person");
							if (!person.equals(null)) {
								Log.d("pt", "Pattikkappette");
								Log.d("pt", person.toString());
								JSONObject user = person.getJSONObject("user");
								String userId = Integer.toString(user
										.getInt("id"));
								editor.putString("userId", userId);
								Log.d("userId", userId);
								Log.d("pt", "veendum Pattikkappette");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// editor.putString("userId", "userId");
						editor.putString("page_" + Integer.toString(i), name);
						editor.putInt("page_id_" + Integer.toString(i), id);
						editor.commit();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
*/
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			Intent openMenu = new Intent("org.shaastra.eventregistration.Menu");
			startActivity(openMenu);

		}

	}

}
