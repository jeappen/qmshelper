package org.shaastra.qmshelper;
import org.shaastra.qmshelper.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;

public class Login extends Activity {

	EditText et1, et2;
	Button b;
	String user, pass;
	String[] userpass = "qmsapp01 scaryhog\nqmsapp02 badgrass\nqmsapp03 olivetooth\nqmsapp04 cooldino\nqmsapp05 redtown\nqmsapp06 longstick\nqmsapp07 funnymusk\nqmsapp08 sillycave\nqmsapp09 luckyglass\nqmsapp10 hotsystem\nqmsapp11 cutejaguar\nqmsapp12 hugemetal\nqmsapp13 goldpencil\nqmsapp14 jollyname\nqmsapp15 freshsugar\nqmsapp16 mistyjam\nqmsapp17 lushcopper\nqmsapp18 funnyfork\nqmsapp19 spicypink\nqmsapp20 dampshow\nqmsapp21 realfish\nqmsapp22 gardenline\nqmsapp23 cowboyrule\nqmsapp24 jungleman\nqmsapp25 deepshallow\nqmsapp26 greenvalley\nqmsapp27 sheetball\nqmsapp28 verysign\nqmsapp29 coverdull\nqmsapp30 missionkill"
			.split("\n");
	String[][] userpass2 = new String[30][2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		et1 = (EditText) findViewById(R.id.editText1);
		et2 = (EditText) findViewById(R.id.editText2);
		b = (Button) findViewById(R.id.button1);
		/*
		 * String test = ":1:e1:2:2e:3:e3"; String[] myStrings=test.split(":");
		 * for(int i =0 ;i <myStrings.length; i++) Log.e("TEST"+ i,
		 * myStrings[i]);
		 */
		for (int i = 0; i < userpass.length; i++) {
			userpass2[i] = userpass[i].split(" ");
		}
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean flag = false;
				user = et1.getText().toString();
				pass = et2.getText().toString();
				for (int i = 0; i < userpass.length; i++) {
					if (user.compareTo(userpass2[i][0]) == 0
							&& pass.compareTo(userpass2[i][1]) == 0) {
						Intent intent = new Intent(Login.this, Main.class);
						intent.putExtra("user", user);
						intent.putExtra("pass", pass);
						flag = true;
						// i.putExtra("myStrings",myStrings);
						startActivity(intent);
					}
				}
				if (flag == false) {
					AlertDialog alertDialog = new AlertDialog.Builder(
							Login.this).create();
					alertDialog.setTitle("Wrong Username or Password");
					alertDialog.setMessage("Contact Shaastra Team");
					alertDialog.show();
				}
			}
		});
	}
}