package org.shaastra.qmshelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class RegisteredUser extends FragmentActivity implements OnDateSetListener   {
	
EditText fnameET,lnameET,emailET,crollET,mobnumET ,bcodeET,cbranchET,cnameET,cityET,passwordET;
Spinner branch_spinner;
RadioGroup genderRG,accomRG;
TextView dateView,idTV,faq;
Button updatebut,regbut;

String gender, dobS,email,accomodation, fname ,lname ,croll,mobnum,cbranch,cname,city,password,userid,token,bcode;

String[] branches="School\nArts\nAccounting\nApplied Mechanics\nMechatronics\nAerospace Engineering\nAutomobile Engineering\nBiotech / Biochemical / Biomedical\nBiology\nCeramic Engineering\nChemical Engineering\nChemistry\nDesign\nEngineering Design\nCivil Engineering\nComputer Science and Engineering\nElectronics and Communications Engineering\nElectrical and Electronics Engineering\nElectrical Engineering\nElectronics and Instrumentation Engineering\nEngineering Physics\nEconomics\nFashion Technology\nHumanities and Social Sciences\nIndustrial Production\nProduction\nInformation Technology and Information Science\nManagement\nManufacturing\nMathematics\nMetallurgy and Material Science\nMechanical Engineering\nOcean Engineering and Naval Architecture\nPhysics\nTelecom\nTextile Engineering\nOthers".split("\n");
ArrayAdapter<String> branch_data;

private Calendar calendar,dob;

private int year, month, day,age;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newuser);
		

		dateView = (TextView) findViewById(R.id.DOB);
	      calendar = Calendar.getInstance();
	      year = calendar.get(Calendar.YEAR);
	      month = calendar.get(Calendar.MONTH);
	      day = calendar.get(Calendar.DAY_OF_MONTH);
	      dob= Calendar.getInstance();
	      branch_data= new ArrayAdapter<String>(RegisteredUser.this,android.R.layout.simple_spinner_item, branches);
	      //dateView.setText("DOB : " + day+"/"+(month+1)+"/"+year);
	      dateView.setText("DOB : --/--/----"); 
	      gender="F";
	      accomodation="0";
	      setTitle("Registered User");
	      //showDate(year, month, day);
	      branch_spinner=(Spinner)findViewById(R.id.branch_spinner);
	      branch_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      branch_spinner.setAdapter(branch_data); 
	      
	      idTV=(TextView) findViewById(R.id.idTV);
	      faq=(TextView) findViewById(R.id.faq);
	      
	      		fnameET=(EditText) findViewById(R.id.fname);
				lnameET=(EditText) findViewById(R.id.lname);
				emailET =(EditText) findViewById(R.id.emailid_reg);
				crollET =(EditText) findViewById(R.id.croll);
				mobnumET =(EditText) findViewById(R.id.mobnum);
				cbranchET =(EditText) findViewById(R.id.cbranch);
				cnameET =(EditText) findViewById(R.id.cname);
				cityET =(EditText) findViewById(R.id.city);
				passwordET =(EditText) findViewById(R.id.pw);
				bcodeET=(EditText) findViewById(R.id.bcodeET);
				
				idTV.setText(getResources().getString(R.string.id_name)+" : "+getResources().getString(R.string.id_default));
				faq.setText(getResources().getString(R.string.registeredfaq));
				token="empty";
				token=getIntent().getStringExtra("token");
				Log.d("received token",token);
				email=getIntent().getStringExtra("email");
				emailET.setText(email);
				emailET.setEnabled(false);
				
				
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
		
		
		updatebut=(Button) findViewById(R.id.update);
		updatebut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubduration
				
				try{
					/*if(passwordET.getText().toString().length()<6)
						Toast.makeText(getApplicationContext(), "The password should have at least 6 characters!", Toast.LENGTH_LONG).show();
					else*/
						new UpdateDetails().execute();	
				}catch(Exception e){
					Toast.makeText(getApplicationContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});
		updatebut.setVisibility(View.VISIBLE);
		genderRG = (RadioGroup)findViewById(R.id.radioGroup1);
		accomRG = (RadioGroup) findViewById(R.id.radioGroup2);
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
		regbut=(Button) findViewById(R.id.confirm_reg);
	regbut.setVisibility(View.GONE);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}
	 public boolean isValidEmailAddress(String email) {
	        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
	        java.util.regex.Matcher m = p.matcher(email);
	        return m.matches();
	 }
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        
	    }
	}
	
	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub

		SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			uid.edit().clear().commit();
			super.onDestroy();
			
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
        Calendar dob= Calendar.getInstance();
        dob.set(year, month, day);
        Calendar c = Calendar.getInstance();
        if(c.after(dob)){
        	this.dob=dob;
        	this.year=year;
        	this.month=month;
        	this.day=day;
        dateView.setText("DOB : " + day+"/"+(month+1)+"/"+year);
        }
        else
        	Toast.makeText(getApplicationContext(), "Please enter a valid DOB", Toast.LENGTH_LONG).show();
        	
    }
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	public class DatePickerFragment extends DialogFragment{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
        	/*final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);*/

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (OnDateSetListener)getActivity(), year, month, day);
        }

    }
	public void Clear(View v){
		//To Clear all fields for next new user
		SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
		ViewGroup group = (ViewGroup)findViewById(R.id.submainlayout);
		for (int i = 0, count = group.getChildCount(); i < count; ++i) {
		    View view = group.getChildAt(i);
		    if (view instanceof EditText && (EditText)view!=emailET) {
		        ((EditText)view).setText("");
		    }
		}
		bcodeET.setText("");
		branch_spinner.setSelection(0);
		genderRG.check(R.id.Female);
		accomRG.check(R.id.notwanted);
		//emailET.setEnabled(true);
		//idTV.setText(getResources().getString(R.string.id_name)+" : "+getResources().getString(R.string.id_default));
		uid.edit().clear().commit();
		
		//updatebut.setVisibility(View.GONE);
		//regbut.setVisibility(View.VISIBLE);
		
		Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
		dob.set(year, month, day);
		dateView.setText("DOB : --/--/----"); 
		
        //dateView.setText("DOB : " + day+"/"+(month+1)+"/"+year);
        fnameET.requestFocus();
	}
	
	public void Scan(View v){
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		// start scanning
		try {
			scanIntegrator.initiateScan();
		} catch (Exception e) {
			Log.e("<---Scan Error 1--->", ">>" + e.getMessage());
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
			String scanContent;
			String scanFormat;
			
				//tvr1.setText("Sending...");
				// gcontent from Intent Result
				scanContent = scanningResult.getContents();
				// get format name of data scanned
				scanFormat = scanningResult.getFormatName();
				bcodeET.setText(scanContent);
				// output to UI
				Log.i("Inside onActivityResult", "reached");
				boolean scanDone = true;
				if (scanContent != null) {
					//sendData();
				} else {
					//tvr1.setText("No data scanned");
					Log.e("<---Error Scan--->", ">>No Data Scanned");
				}
			
		} else {
			// invalid scan data or scan canceled
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	class UpdateDetails extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisteredUser.this);
			pDialog.setMessage("Updating Details");
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
			String cbranch =branch_spinner.getSelectedItem().toString();//cbranchET.getText().toString();
			String cname =cnameET.getText().toString();
			String city =cityET.getText().toString();
			//String password =passwordET.getText().toString();
			String bcode =bcodeET.getText().toString();
			
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
			Log.d("G&A",gender+" "+accomodation);
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
			if(!bcode.isEmpty())
				paramse.add(new BasicNameValuePair("barcode", bcode));
			/*if(!password.isEmpty())
			 *paramse.add(new BasicNameValuePair("password", password));
			 */
			paramse.add(new BasicNameValuePair("want_accomodation", accomodation));
			paramse.add(new BasicNameValuePair("gender", gender));
			Log.d("date",DOBstring);
			Log.d("age",Integer.toString(age));
			Log.d("bcode",bcode);
			if(age>0){
			paramse.add(new BasicNameValuePair("dob", DOBstring));
			paramse.add(new BasicNameValuePair("age", Integer.toString(age)));
			}
			//SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			//token = uid.getString("uid", "nothing");
			
					String url = "api/mobile/profile/";
					JSONObject json = jsonParser.makeHttpRequest(url, "POST", paramse, token);
					Log.d("Update Results",json.toString());
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if(token.equals("nothing")){
				Toast.makeText(getApplicationContext(), "ERR: Can't update Details", Toast.LENGTH_LONG).show();
				emailET.setEnabled(true);

				regbut.setVisibility(View.VISIBLE);
				updatebut.setVisibility(View.GONE);
			}
			else{
				Toast.makeText(getApplicationContext(), "Details Updated Successfully", Toast.LENGTH_SHORT).show();
				regbut.setVisibility(View.GONE);
				updatebut.setVisibility(View.VISIBLE);
			}
		}
	}
	
	class ExistingDetails extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		JSONParser jsonParser = new JSONParser();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisteredUser.this);
			pDialog.setMessage("Getting Existing Details");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			final List<NameValuePair> paramse = new ArrayList<NameValuePair>();
			
			SharedPreferences uid = getApplicationContext().getSharedPreferences("uid",0);
			//final String token = uid.getString("uid", "Aaa");
			String url = "api/mobile/profile/";
			JSONObject json = jsonParser.makeHttpRequest(url, "GET", paramse, token);
			
			
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
				//password =json.getString("password");
				gender =json.getString("gender");
				accomodation=json.getString("want_accomodation");
				userid=json.getString("user_id");
				bcode=json.getString("barcode");
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
			if(!cbranch.equals("null")){
				int spinnerPosition = branch_data.getPosition(cbranch);
				branch_spinner.setSelection(spinnerPosition);
				//cbranchET.setText(cbranch, TextView.BufferType.EDITABLE);
				}
			if(!cname.equals("null"))
				cnameET.setText(cname, TextView.BufferType.EDITABLE);
			if(!city.equals("null"))
				cityET.setText(city, TextView.BufferType.EDITABLE);
			if(!dobS.equals("null")){
				String[] dob_split=dobS.split("-");
				RegisteredUser.this.day=Integer.valueOf(dob_split[2]);
				RegisteredUser.this.month=Integer.valueOf(dob_split[1])-1;
				RegisteredUser.this.year=Integer.valueOf(dob_split[0]);
				dateView.setText("DOB : " + day+"/"+(month+1)+"/"+year);
				 }
			if(!bcode.equals("-1") && !bcode.equals("null"))
				bcodeET.setText(bcode, TextView.BufferType.EDITABLE);
			if(gender.equals("M"))
				genderRG.check(R.id.Male);
			if(accomodation.equals("true"))
				accomRG.check(R.id.want);
			
			idTV.setText(getResources().getString(R.string.id_name)+" : "+getResources().getString(R.string.id_prefix)+userid);
			
			
			//passwordET.setText(password, TextView.BufferType.EDITABLE);
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
			
		}
	}


}
