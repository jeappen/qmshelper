package org.shaastra.qmshelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class EditProfile_old extends FragmentActivity  {
	
EditText fnameET,lnameET,crollET,mobnumET ,cbranchET,cnameET,cityET,passwordET;
String gender, dobS,accomodation, fname ,lname ,croll,mobnum,cbranch,cname,city,password;
private Calendar calendar,dob;
private TextView dateView;
private int year, month, day,age;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_old);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		dateView = (TextView) findViewById(R.id.DOB);
	      calendar = Calendar.getInstance();
	      year = calendar.get(Calendar.YEAR);
	      month = calendar.get(Calendar.MONTH);
	      day = calendar.get(Calendar.DAY_OF_MONTH);
	      
	      dateView.setText("DOB : " + day+"/"+(month+1)+"/"+year);
	      gender="F";
	      accomodation="0";
	      //showDate(year, month, day);
		fnameET=(EditText) findViewById(R.id.fname);
				lnameET=(EditText) findViewById(R.id.lname);
				crollET =(EditText) findViewById(R.id.croll);
				mobnumET =(EditText) findViewById(R.id.mobnum);
				cbranchET =(EditText) findViewById(R.id.cbranch);
				cnameET =(EditText) findViewById(R.id.cname);
				cityET =(EditText) findViewById(R.id.city);
				passwordET =(EditText) findViewById(R.id.pw);
		boolean a = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		if (a) {
			try {
				new ExistingDetails().execute();
				
			} catch (Exception e) {
			}
		} else {
			Toast.makeText(
					this,
					"No internet connection. Check your connection and "
							+ "try again later", Toast.LENGTH_SHORT).show();
		}
		
		
		Button b=(Button) findViewById(R.id.update);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration
				
				try{
					if(passwordET.getText().toString().length()<6)
						Toast.makeText(getApplicationContext(), "The password should have at least 6 characters!", Toast.LENGTH_LONG).show();
					else
						new UpdateDetails().execute();	
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		RadioGroup genderRG = (RadioGroup)findViewById(R.id.radioGroup1);
		RadioGroup accomRG = (RadioGroup) findViewById(R.id.radioGroup2);
		genderRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			    boolean checked = ((RadioButton) findViewById(checkedId)).isChecked();
				switch(checkedId) {
				case R.id.Male:
		            if (checked)
		                gender="M";
		            break;
		        case R.id.Female:
		            if (checked)
		                gender="F";
		            break;
				}
				
			}
		});
accomRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

			    boolean checked = ((RadioButton) findViewById(checkedId)).isChecked();
				switch(checkedId) {
		        case R.id.want:
		            if (checked)
		                accomodation="1";
		            break;
		        case R.id.notwanted:
		            if (checked)
		                accomodation="0";
		            break;
				
				}
				
			}
		});
		
	}
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        
	    }
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        Log.w("DatePicker","Date = " + year);
        dob= Calendar.getInstance();
        dob.set(year, month, day);
        ((TextView) findViewById(R.id.DOB)).setText("DOB : " + day+"/"+(month+1)+"/"+year);
    }
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	public class DatePickerFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (OnDateSetListener)getActivity(), year, month, day);
        }

    }

	class UpdateDetails extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getApplicationContext());
			pDialog.setMessage("Posting");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			final List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			String fname =fnameET.getText().toString();
			String lname =lnameET.getText().toString();
			String croll =crollET.getText().toString();
			String mobnum =mobnumET.getText().toString();
			String cbranch =cbranchET.getText().toString();
			String cname =cnameET.getText().toString();
			String city =cityET.getText().toString();
			String password =passwordET.getText().toString();
			Calendar cur=Calendar.getInstance();
			cur.setTime(new Date());
			int age = cur.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		
		    if (dob.get(Calendar.MONTH) > cur.get(Calendar.MONTH) || 
		        (dob.get(Calendar.MONTH) == cur.get(Calendar.MONTH) && dob.get(Calendar.DATE) > cur.get(Calendar.DATE))) {
		        age--;
		    }
			int month = dob.get(Calendar.MONTH);
			int day = dob.get(Calendar.DATE);
			int year = dob.get(Calendar.YEAR);
			String DOBstring =Integer.toString(year)+"-"+String.format("%02d", month+1)+"-"+String.format("%02d", day);
			Log.d("G&A",gender+accomodation);
			/*if(password.length()<6){
				Toast.makeText(getActivity(), "The password should have at least 6 characters!", Toast.LENGTH_LONG).show();
				return null;}
				*/
			if(!fname.isEmpty())
			paramse.add(new BasicNameValuePair("first_name", fname));
			if(!lname.isEmpty())
			paramse.add(new BasicNameValuePair("last_name", lname));
			if(!croll.isEmpty())
			paramse.add(new BasicNameValuePair("college_roll", croll));
			if(!mobnum.isEmpty())
			paramse.add(new BasicNameValuePair("mobile_number", mobnum));
			if(!cbranch.isEmpty())
			paramse.add(new BasicNameValuePair("branch", cbranch));
			if(!cname.isEmpty())
			paramse.add(new BasicNameValuePair("college_text", cname));
			if(!city.isEmpty())
			paramse.add(new BasicNameValuePair("city", city));
			if(!password.isEmpty())
			paramse.add(new BasicNameValuePair("password", password));
			paramse.add(new BasicNameValuePair("want_accomodation", accomodation));
			paramse.add(new BasicNameValuePair("gender", gender));
			Log.d("date",DOBstring);
			Log.d("age",Integer.toString(age));
			if(age!=0){
			paramse.add(new BasicNameValuePair("dob", DOBstring));
			paramse.add(new BasicNameValuePair("age", Integer.toString(age)));
			}
			SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			final String token = uid.getString("uid", "Aaa");
			
					String url = "api/mobile/profile/";
					JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, token);
					Log.d("Update Results",json.toString());
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
			/*Intent i=new Intent(PostView.this,PostView.class);
            i.putExtra("post_id", post_id);
           i.putExtra("WallName", wall_name);
            i.putExtra("passed", "notif");
            startActivity(i);8*/
		}
	}
	

	class ExistingDetails extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(getApplicationContext());
			pDialog.setMessage("Getting Existing Details");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			final List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			
			SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			final String token = uid.getString("uid", "Aaa");
			String url = "api/mobile/profile/";
			JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, token);
			
			
			try {
				json=json.getJSONObject("data");
				fname =json.getString("first_name");
				lname =json.getString("last_name");
				croll =json.getString("college_roll");
				mobnum =json.getString("mobile_number");
				 cbranch =json.getString("branch");
				 cname =json.getString("college_text");
				 dobS=json.getString("dob");
				city =json.getString("city");
				password =json.getString("password");
				gender =json.getString("gender");
				accomodation=json.getString("want_accomodation");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Existing Results",json.toString());
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			fnameET.setText(fname, TextView.BufferType.EDITABLE);
			lnameET.setText(lname, TextView.BufferType.EDITABLE);
			if(!croll.equals("null"))
			crollET.setText(croll, TextView.BufferType.EDITABLE);
			if(!mobnum.equals("null"))
				mobnumET.setText(mobnum, TextView.BufferType.EDITABLE);
			if(!cbranch.equals("null"))
				cbranchET.setText(cbranch, TextView.BufferType.EDITABLE);
			if(!cname.equals("null"))
				cnameET.setText(cname, TextView.BufferType.EDITABLE);
			if(!city.equals("null"))
				cityET.setText(city, TextView.BufferType.EDITABLE);
			if(!dob.equals("null"))
				 dateView.setText("DOB : " +dobS.replace('-','/'));
			passwordET.setText(password, TextView.BufferType.EDITABLE);
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
			/*Intent i=new Intent(PostView.this,PostView.class);
            i.putExtra("post_id", post_id);
           i.putExtra("WallName", wall_name);
            i.putExtra("passed", "notif");
            startActivity(i);8*/
		}
	}

}
