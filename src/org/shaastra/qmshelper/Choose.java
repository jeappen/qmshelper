package org.shaastra.qmshelper;

import org.shaastra.qmshelper.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Choose extends Activity {
	Button pbut,ebut;
	String user,pass;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		user = null;//getIntent().getStringExtra("user");
		pass = null;//getIntent().getStringExtra("pass");
		user = "test";//getIntent().getStringExtra("user");
		pass = "test";
		Log.i("chooseUser", user);
		Log.i("choosePass", pass);
		setContentView(R.layout.choose);

		pbut = (Button) findViewById(R.id.button1);
		ebut= (Button) findViewById(R.id.button2);
		pbut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
						Intent intent = new Intent(Choose.this, Passport.class);
						intent.putExtra("user", user);
						intent.putExtra("pass", pass);
						// i.putExtra("myStrings",myStrings);
						startActivity(intent);
					}
				});
		/*ebut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
						Intent intent = new Intent(Choose.this, Event.class);
						intent.putExtra("user", user);
						intent.putExtra("pass", pass);
						// i.putExtra("myStrings",myStrings);
						startActivity(intent);
					}
				});
	*/
		}

}
