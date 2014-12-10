package org.shaastra.qmshelper;

import org.shaastra.qmshelper.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Passport extends Activity {
	Button pbut,ebut;
	String user,pass;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		user = getIntent().getStringExtra("user");
		pass = getIntent().getStringExtra("pass");
		Log.i("chooseUser", user);
		Log.i("choosePass", pass);
		setContentView(R.layout.passport);

		pbut = (Button) findViewById(R.id.button1);
		ebut= (Button) findViewById(R.id.button2);
		pbut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean a = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = connectivityManager
						.getActiveNetworkInfo();
				a = activeNetworkInfo != null && activeNetworkInfo.isConnected();
				if (a) {Intent intent = new Intent(Passport.this, NewUser.class);
				intent.putExtra("user", user);
				intent.putExtra("pass", pass);
				// i.putExtra("myStrings",myStrings);
				startActivity(intent);
				} else {
					Toast.makeText(
							getApplicationContext(),
							"No internet connection. Check your connection and "
									+ "try again later", Toast.LENGTH_SHORT).show();
				}
						
					}
				});
		ebut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
						Intent intent = new Intent(Passport.this, SearchUser.class);
						intent.putExtra("user", user);
						intent.putExtra("pass", pass);
						// i.putExtra("myStrings",myStrings);
						startActivity(intent);
					}
				});
	
		}

}
