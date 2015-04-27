package com.example.testshephzapp;

import java.util.HashMap;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText username,email,password;
	private Button login,logup;
	private UserService userService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button)findViewById(R.id.login);
        logup = (Button)findViewById(R.id.logup);
        password = (EditText)findViewById(R.id.password);
        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        App42API.initialize(getApplicationContext(),"28a359e132f9f63a97a47ce0732c3a1a58ed842c9b69d96dc1e46f3cc089f4e5",
        		"7c4d1b4c0a97b0b8c3a28d1c9ea743ef25efff27bca5c051e44f7b9e7cb14a92");
        //Build User Service  
        userService = App42API.buildUserService();  
        // Using userService reference, you should be able to call all its methods like create user/update user/authenticate etc.   
         
        logup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String userName = username.getText().toString();
				String emailId = email.getText().toString();
				String pwd = password.getText().toString();
				
				/* This will create user in App42 cloud and will return created User  
		        object in onSuccess callback method */
		        userService.createUser(userName, pwd, emailId, new App42CallBack() {  
		        public void onSuccess(Object response)   
		        {  
		            User user = (User)response; 
		            System.out.println("userName is " + user.getUserName());  
		            System.out.println("emailId is " + user.getEmail()); 
			        runOnUiThread(new Runnable() {
			        	  public void run() {
			        		  Toast.makeText(getApplicationContext(), "You are registered", Toast.LENGTH_SHORT).show();
			        	  }
			        });
		            
		        }  
		        public void onException(Exception ex)   
		        {  
		            System.out.println("Exception Message"+ex.getMessage()); 
			        runOnUiThread(new Runnable() {
			        	  public void run() {
			        		  if(username == null||email == null||password == null){
					            	Toast.makeText(getApplicationContext(), "Enter all the fields", Toast.LENGTH_LONG).show();
					            }else if(password.getTextSize()<8){
					            	Toast.makeText(getApplicationContext(), "Password is incorrect" +
					            			"Should contain 8 letters and one numeric Char", Toast.LENGTH_LONG).show();
					            }else{
					            	Toast.makeText(getApplicationContext(), "Problem with the server", Toast.LENGTH_LONG).show();
					            } 
			        	  }
			        });
		             
		        }  
		        });
				
			}
		});
        
        login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final String userName = username.getText().toString();
				final String pwd = password.getText().toString();
				   
			    HashMap<String, String> otherMetaHeaders = new HashMap<String, String>();  
			    otherMetaHeaders.put("userProfile", "true");
			       
			    userService.setOtherMetaHeaders(otherMetaHeaders);   
			    userService.authenticate(userName , pwd, new App42CallBack() {  
			    public void onSuccess(Object response)  
			    {  
			        User user = (User)response;  
			        System.out.println("userName is " + user.getUserName());    
			        System.out.println("sessionId is " + user.getSessionId());
			        Intent in = new Intent(getApplicationContext(), Logged.class);
			        in.putExtra("user", userName);
			        in.putExtra("session", user.getSessionId());
			        startActivity(in);
			    }  
			    public void onException(Exception ex)   
			    {  
			        System.out.println("Exception Message : "+ex.getMessage());  
			        runOnUiThread(new Runnable() {
			        	  public void run() {
			        		  Toast.makeText(getApplicationContext(), "username or password is incorrect", Toast.LENGTH_LONG).show();
			        	  }
			        });
			    }  
			    });  
			}
		});
          
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
