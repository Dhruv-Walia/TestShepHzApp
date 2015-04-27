package com.example.testshephzapp;

import java.util.ArrayList;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.buddy.Buddy;
import com.shephertz.app42.paas.sdk.android.buddy.BuddyService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import adapter.SimpleArrayAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Logged extends Activity{
	
	private TextView user,session;
	private Button invite,logout,message,msgflt,unfriend;
	private ListView msglist;
	UserService userService;
	BuddyService buddyService;
	private ArrayList<String> data,data1,data2,data3;
	boolean sess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logged);	 
		user = (TextView) findViewById(R.id.textView1);
		session = (TextView) findViewById(R.id.textView2);
		msglist = (ListView) findViewById(R.id.messageList);
	    invite = (Button) findViewById(R.id.invite);
	    logout = (Button) findViewById(R.id.logout);
	    message = (Button) findViewById(R.id.message);
	    msgflt = (Button) findViewById(R.id.getmsg);
	    unfriend = (Button) findViewById(R.id.unfriend);
 		
	    sess=true;
	    user.setText("User : " + getIntent().getStringExtra("user"));
		session.setText("Session id : " + getIntent().getStringExtra("session"));
		
		//Building User-Service and Buddy-Service
		userService = App42API.buildUserService();
		buddyService = App42API.buildBuddyService();;
		
		Initailize();
	    
	    //Getting Friend Request to user as Notification 	   
	    buddyService.getFriendRequest(getIntent().getStringExtra("user"), new App42CallBack() {  
	    public void onSuccess(Object response)   
	    {  
	        @SuppressWarnings("unchecked")
			ArrayList<Buddy>  buddy  = (ArrayList<Buddy> )response;  
	        for(int i=0;i<buddy.size();i++)  
	        {  
	        	System.out.println("userName is : " + buddy.get(i).getUserName());   
	            System.out.println("buddyName is : " + buddy.get(i).getBuddyName());   
	            System.out.println("message is : " + buddy.get(i).getMessage());   
	            System.out.println("sendedOn is : " + buddy.get(i).getSendedOn());
	            final String username = buddy.get(i).getUserName().toString();
	            final String buddyname = buddy.get(i).getBuddyName().toString();
	            
	            runOnUiThread(new Runnable() {
		        	  public void run() {
		        		//Alert dialog for accepting/rejecting friend request 
		  	        	AlertDialog.Builder builder1 = new AlertDialog.Builder(Logged.this);
		  	            builder1.setMessage("Accept friend request from "+ buddyname);
		  	            builder1.setCancelable(true);
		  	            builder1.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		  	                public void onClick(DialogInterface dialog, int id) {
		  	                    buddyService.acceptFriendRequest(username, buddyname, new App42CallBack() {  
		  	                    	public void onSuccess(Object response)   
		  	                    	{  
		  	                    	    Buddy  buddy  = (Buddy )response;  
		  	                    	    System.out.println("userName is : " + buddy.getUserName());   
		  	                    	    System.out.println("buddyName is : " + buddy.getBuddyName());   
		  	                    	    System.out.println("sendedOn is : " + buddy.getSendedOn());  
		  	                    	    System.out.println("acceptedOn is : " + buddy.getAcceptedOn());  
		  	                    	}  
		  	                    	public void onException(Exception ex)  
		  	                    	{  
		  	                    	    System.out.println("Exception Message"+ex.getMessage());      
		  	                    	}  
		  	                    	});  
		  	                    dialog.cancel();
		  	                }
		  	            });
		  	            builder1.setNegativeButton("No",new DialogInterface.OnClickListener() {
		  	                public void onClick(DialogInterface dialog, int id) {
		  	                	buddyService.rejectFriendRequest(username, buddyname,new App42CallBack() {  
		  	                		public void onSuccess(Object response)   
		  	                		{  
		  	                		    Buddy  buddy  = (Buddy )response;  
		  	                		    System.out.println("userName is : " + buddy.getUserName());   
		  	                		    System.out.println("buddyName is : " + buddy.getBuddyName());   
		  	                		    System.out.println("sendedOn is : " + buddy.getSendedOn());  
		  	                		    System.out.println("acceptedOn is : " + buddy.getAcceptedOn());  
		  	                		}  
		  	                		public void onException(Exception ex)  
		  	                		{  
		  	                		    System.out.println("Exception Message"+ex.getMessage());      
		  	                		}  
		  	                		});  
		  	                    dialog.cancel();
		  	                }
		  	            });

		  	            AlertDialog alert11 = builder1.create();
		  	            alert11.show();
		        	  }
		        });
	            
	            
	            //Showing Notification for Friend Request
	    	    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	    	    PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

	    	    Notification not = new Notification.Builder(getApplicationContext())
	    	        .setContentTitle("Friend Request : " + buddy.get(i).getMessage())
	    	        .setContentText("Do you want to add " + buddy.get(i).getUserName()+ "as your Friend")
	    	        .setSmallIcon(R.drawable.ic_launcher)
	    	        .setContentIntent(pIntent)
	    	        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI).build();

	    	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    	    // hide the notification after its selected
	    	    not.flags |= Notification.FLAG_AUTO_CANCEL;

	    	    notificationManager.notify(0, not);
	    	     
	        }  
	    }  
	    public void onException(Exception ex)   
	    {  
	        System.out.println("Exception Message"+ex.getMessage()); 
	        runOnUiThread(new Runnable() {
	        	  public void run() {
	        		  Toast.makeText(getApplicationContext(), "There is no friend request", Toast.LENGTH_SHORT).show();
	        	  }
	        });
	    }  
	    });    
	    
	    // Sending Friend request to User list  
		invite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {  
				
				final Dialog dialog = new Dialog(Logged.this);
				dialog.setContentView(R.layout.dialogbox);
				dialog.setTitle("Pick friend to send Invite");
				
				// set the custom dialog components - text, image and button
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dialogview,R.id.textView1, data);
			    list.setAdapter(adapter);
			    
			    dialog.show();
			    list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						String buddyName = parent.getItemAtPosition(position).toString(); 
						String message = "Welcome to Shephertz";    
						
						buddyService = App42API.buildBuddyService();
						buddyService.sendFriendRequest(getIntent().getStringExtra("user"), buddyName, message, new App42CallBack() {  
						public void onSuccess(Object response)   
						{  
						    Buddy  buddy  = (Buddy )response;  
						    System.out.println("userName is : " + buddy.getUserName());   
						    System.out.println("buddyName is : " + buddy.getBuddyName());   
						    System.out.println("message is : " + buddy.getMessage());   
						    System.out.println("sendedOn is : " + buddy.getSendedOn());  
						    runOnUiThread(new Runnable() {
					        	  public void run() {
					        		  Toast.makeText(getApplicationContext(), "Friend Request Send", Toast.LENGTH_SHORT).show();
					        	  }
					        });
						}  
						public void onException(Exception ex)  
						{  
						    System.out.println("Exception Message"+ex.getMessage()); 
						    runOnUiThread(new Runnable() {
					        	  public void run() {
					        		  Toast.makeText(getApplicationContext(),"Error in sending request", Toast.LENGTH_SHORT).show();
					        	  }
					        });
						}  
						}); 
						dialog.dismiss();
					}
				});	
			}
		});
		
		// Sending Message Dialog Box
		message.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final Dialog dialog = new Dialog(Logged.this);
				dialog.setContentView(R.layout.message_dialogbox);
				dialog.setTitle("Send Message to friend");
				// set the custom dialog components - text, image and button
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				final EditText msgtxt = (EditText) dialog.findViewById(R.id.messagetext);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dialogview,R.id.textView1, data1);
			    list.setAdapter(adapter);
			    
			    dialog.show();
			    list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						String msg = msgtxt.getText().toString();
						String buddyName = parent.getItemAtPosition(position).toString();
						
						buddyService.sendMessageToFriend(getIntent().getStringExtra("user"),buddyName,msg, new App42CallBack() {  
							public void onSuccess(Object response)  
							{  
							    Buddy  buddy  = (Buddy )response;  
							    System.out.println("buddyName is : " + buddy.getBuddyName());  
							    System.out.println("userName is : " + buddy.getUserName());  
							    System.out.println("message is : " + buddy.getMessage());  
							    System.out.println("messageId is : " + buddy.getMessageId());  
							    System.out.println("SendedOn is : " + buddy.getSendedOn());  
							    runOnUiThread(new Runnable() {
						        	  public void run() {
						        		  Toast.makeText(getApplicationContext(), "Message Send", Toast.LENGTH_SHORT).show();
						        	  }
						        });
							}  
							public void onException(Exception ex)  
							{  
							    System.out.println("Exception Message"+ex.getMessage());
							    runOnUiThread(new Runnable() {
						        	  public void run() {
						        		  Toast.makeText(getApplicationContext(), "Error in sending message", Toast.LENGTH_SHORT).show();
						        	  }
						        });
							}  
							});  
						dialog.dismiss();
					}
				});
			}
		});
		
		// Getting messages from particular friend
		msgflt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(Logged.this);
				dialog.setContentView(R.layout.dialogbox);
				dialog.setTitle("Get messages from Friend");
				// set the custom dialog components - text, image and button
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dialogview,R.id.textView1, data1);
			    list.setAdapter(adapter);
			    
			    dialog.show();
			    list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						data2 = new ArrayList<String>();
						data3 = new ArrayList<String>();
 						String buddyName = parent.getItemAtPosition(position).toString();
						
						buddyService.getAllMessagesFromBuddy(getIntent().getStringExtra("user"), buddyName, new App42CallBack() {  
							public void onSuccess(Object response)   
							{  
							    @SuppressWarnings("unchecked")
								ArrayList<Buddy>  buddy  = (ArrayList<Buddy> )response;  
							    for(int i=0;i<buddy.size();i++)   
							    {  
							    	data2.add(buddy.get(i).getMessage());
							    	data3.add(buddy.get(i).getOwnerName());
							        System.out.println("Owner Name is : " + buddy.get(i).getOwnerName());  
							        System.out.println("userName is : " + buddy.get(i).getUserName());  
							        System.out.println("message is : " + buddy.get(i).getMessage());  
							        System.out.println("messageId is : " + buddy.get(i).getMessageId());  
							        System.out.println("SendedOn is : " + buddy.get(i).getSendedOn());
							        runOnUiThread(new Runnable() {
		        			        	  public void run() {
		        			        		  // Populating the ListView with messages  
		        			        		  SimpleArrayAdapter ld = new SimpleArrayAdapter(getApplicationContext(), data2,data3);
		  									  msglist.setAdapter(ld);
		        			        	  }
		        			        });  
							    }  
							}  
							public void onException(Exception ex)   
							{  
							    System.out.println("Exception Message"+ex.getMessage());  
							}  
							});
						dialog.dismiss();
						
					}
				});
			}
		});
		
		// Remove Friends from FriendList/UnFriend 
		unfriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(Logged.this);
				dialog.setContentView(R.layout.dialogbox);
				dialog.setTitle("Remove Friend from friendList");
				// set the custom dialog components - text, image and button
				ListView list = (ListView) dialog.findViewById(R.id.listView1);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dialogview,R.id.textView1, data1);
			    list.setAdapter(adapter);
			    
			    dialog.show();
			    list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
 						String buddyName = parent.getItemAtPosition(position).toString();
 						buddyService.unFriend(getIntent().getStringExtra("user"), buddyName, new App42CallBack() {  
 							public void onSuccess(Object response)   
 							{  
 							    App42Response app42response = (App42Response)response;        
 							    System.out.println("Response  is:  " + app42response);
 							    runOnUiThread(new Runnable() {
 		  			        	  public void run() {
 		  			        		Toast.makeText(getApplicationContext(), "Unfriend Successful", Toast.LENGTH_SHORT).show();
 		  			        	  }
 		  			            });
 							}  
 							public void onException(Exception ex)   
 							{  
 							    System.out.println("Exception Message"+ex.getMessage());  
 							    runOnUiThread(new Runnable() {
 		  			        	  public void run() {
 		  			        		Toast.makeText(getApplicationContext(), "Error in unfriending", Toast.LENGTH_SHORT).show();
 		  			        	  }
 		  			            });
 							}  
 							});
						dialog.dismiss();
						Initailize();
						
					}
				});
				
			}
		});
		
		// Performing Logout Operation
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				    
			    userService.logout(getIntent().getStringExtra("session"),new App42CallBack() {  
			    public void onSuccess(Object response)  
			    {  
			        App42Response app42response = (App42Response)response;        
			        System.out.println("response is " + app42response) ;
			        Intent in = new Intent(getApplicationContext(), MainActivity.class);
			        startActivity(in);
			        sess=false;
			        runOnUiThread(new Runnable() {
			        	  public void run() {
			        		  Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
			        	  }
			        });
			    }  
			    public void onException(Exception ex)  
			    {  
			        System.out.println("Exception Message " + ex.getMessage()); 
			        runOnUiThread(new Runnable() {
			        	  public void run() {
			        		  Toast.makeText(getApplicationContext(), "Logout Unsuccessful", Toast.LENGTH_LONG).show();
			        	  }
			        });
			        
			    }  
			    });  
			    
			}
		});
		
	}
	
	private void Initailize() {
		// Getting All User list for sending Friend Request
	    data = new ArrayList<String>(); 
	    userService.getAllUsers(new App42CallBack() {  
			public void onSuccess(Object response)   
			{  
			    @SuppressWarnings("unchecked")
				ArrayList<User> user = (ArrayList<User>)response;
			    for(int i = 0; i < user.size(); i++)     
			    {    
			        System.out.println("userName is " + user.get(i).getUserName());    
			        System.out.println("emailId is " + user.get(i).getEmail());
			        if(user.get(i).getUserName() == getIntent().getStringExtra("user")){
			             data.remove(i);
			        }else{
			        	data.add(user.get(i).getUserName());
			        }
			    }  
			       
			}  
			public void onException(Exception ex)  
			{  
			    System.out.println("Exception Message"+ex.getMessage()); 
			    runOnUiThread(new Runnable() {
		        	  public void run() {
		        		  Toast.makeText(getApplicationContext(), "Problem in getting Users", Toast.LENGTH_SHORT).show();
		        	  }
		        });
			}  
			});
	    
	    // Getting Friends for Sending Message from user
		data1 = new ArrayList<String>();
	    buddyService.getAllFriends(getIntent().getStringExtra("user"), new App42CallBack() {  
	    	public void onSuccess(Object response) {  
	    	    @SuppressWarnings("unchecked")
				ArrayList<Buddy>  buddy  = (ArrayList<Buddy> )response;  
	    	    for(int i=0;i<buddy.size();i++)  
	    	    {  
	    	    	data1.add(buddy.get(i).getBuddyName());
	    	        System.out.println("userName is : " + buddy.get(i).getUserName());   
	    	        System.out.println("acceptedOn is : " + buddy.get(i).getAcceptedOn());   
	    	        System.out.println("buddyName is : " + buddy.get(i).getBuddyName());   
	    	        System.out.println("message is : " + buddy.get(i).getMessage());   
	    	        System.out.println("sendedOn is : " + buddy.get(i).getSendedOn());  
	    	    }  
	    	}  
	    	public void onException(Exception ex)   
	    	{  
	    	    System.out.println("Exception Message"+ex.getMessage());      
	    	}  
	    	});
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(sess=true){
			//Alert dialog for accepting/rejecting friend request 
			AlertDialog.Builder builder1 = new AlertDialog.Builder(Logged.this);
            builder1.setMessage("You are Logged in:" +
            		"Do you really want to exit session ");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	userService.logout(getIntent().getStringExtra("session"),new App42CallBack() {  
        			    public void onSuccess(Object response)  
        			    {  
        			        App42Response app42response = (App42Response)response;        
        			        System.out.println("response is " + app42response) ;
        			        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        			        startActivity(in);
        			        sess=false;
        			        runOnUiThread(new Runnable() {
        			        	  public void run() {
        			        		  Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
        			        	  }
        			        });
        			    }  
        			    public void onException(Exception ex)  
        			    {  
        			        System.out.println("Exception Message " + ex.getMessage()); 
        			        runOnUiThread(new Runnable() {
        			        	  public void run() {
        			        		  Toast.makeText(getApplicationContext(), "Logout Unsuccessful", Toast.LENGTH_LONG).show();
        			        	  }
        			        });
        			        
        			    }  
        			    }); 
                    dialog.cancel();
                }
            });
            builder1.setNegativeButton("No",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {  
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();	
		}else{
			super.onBackPressed();
		}
		
	}
}
