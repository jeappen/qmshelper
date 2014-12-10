package org.shaastra.qmshelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Register_old extends Activity{
	
EditText fnameET,lnameET,emailET,passwordET;
String fname,lname,email,password;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.register_old);
		fnameET=(EditText) findViewById(R.id.fname);
		lnameET=(EditText) findViewById(R.id.lname);
		emailET =(EditText) findViewById(R.id.emailid_reg);
		passwordET =(EditText) findViewById(R.id.pw);
		setTitle("Register");
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		if (a) {
			try {
				//new UpdateDetails().execute();
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(
					this,
					"No internet connection. Check your connection and "
							+ "try again later", Toast.LENGTH_SHORT).show();
		}
		Button b=(Button) findViewById(R.id.confirm_reg);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration
				
				try{
						
					fname =fnameET.getText().toString();
					lname =lnameET.getText().toString();
					email =emailET.getText().toString();
					password =passwordET.getText().toString();
					if(fname.isEmpty()||lname.isEmpty()||email.isEmpty())
						Toast.makeText(getApplicationContext(), "Please fill in all the fields.", Toast.LENGTH_LONG).show();
					else if(!isValidEmailAddress(email))
						Toast.makeText(getApplicationContext(), "Please enter a valid email id.", Toast.LENGTH_LONG).show();
					else if(password.length()<6){
						Toast.makeText(getApplicationContext(), "The password should have at least 6 characters!", Toast.LENGTH_LONG).show();
						}
					else
						new SubmitRegistration().execute();
				}catch(Exception e){
					//Toast.makeText(getApplicationContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		
	}

   
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
 }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

	class SubmitRegistration extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		int error;
		String errmsg;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error=0; errmsg="Generic Error";
			pDialog = new ProgressDialog(Register_old.this);
			pDialog.setMessage("Registering");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("milestone02","got here");
			final List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			
				
			paramse.add(new BasicNameValuePair("first_name", fname));
			paramse.add(new BasicNameValuePair("last_name", lname));
			paramse.add(new BasicNameValuePair("email", email));
			paramse.add(new BasicNameValuePair("password", password));
			SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			Log.d("milestone","got here");
			
					String url = "participant_registration/";
					JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, null);
					
					Log.d("Registration Results",json.toString());
					String tx = null;
					
						tx = json.optString("token");
						if(tx.isEmpty()){
						error=1;
							errmsg=json.optJSONArray("email").optString(0);
						// TODO Auto-generated catch block
						}
						else{
					
					SharedPreferences.Editor editor = uid.edit();
					editor.putString("uid", tx);
					editor.putString("user", email);
					editor.commit();
						}
					
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if(error==1){
				TextView msg = new TextView(Register_old.this);
				msg.setText(Html.fromHtml(errmsg));
				msg.setTextSize(16);
				msg.setTextColor(Color.GRAY);
				msg.setMovementMethod(LinkMovementMethod.getInstance());
				AlertDialog.Builder builder1 = new AlertDialog.Builder(Register_old.this);
	            builder1.setView(msg);
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("OK",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
				Intent open = new Intent("org.shaastra.eventregistration.Menu");
				startActivity(open);
			}
			/*Intent i=new Intent(PostView.this,PostView.class);
            i.putExtra("post_id", post_id);
           i.putExtra("WallName", wall_name);
            i.putExtra("passed", "notif");
            startActivity(i);8*/
		}
	}

}
