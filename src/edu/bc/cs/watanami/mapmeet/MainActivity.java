package edu.bc.cs.watanami.mapmeet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void showMap(View v) {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
	        new Thread(new Runnable() {
		        public void run() {
		        	Socket socket = null;
		    		String source = "cscilab.bc.edu";
		    		BufferedReader in = null;
		    		PrintStream out = null;
		    		
		    		JSONObject json_meeting = new JSONObject();
		    		try {
						json_meeting.put("Title", "First meetings");
						json_meeting.put("Date", "10/20/1991");
						json_meeting.put("Time", "4:20PM");
						json_meeting.put("Att_list", "Karen, John, Bill, Freddy");
						json_meeting.put("Latitude", 71.2345);
						json_meeting.put("Longitude", 86.6789);
					} catch (JSONException e1) {
						System.out.println("Couldn't make json object: "+e1);
					}
		    		String meeting = json_meeting.toString();
		    		
		    		try {
		    			socket = new Socket(source, 10003);
		    			out = new PrintStream(socket.getOutputStream(), true);
		    			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    				
		    			out.println("newmeeting");
	    				out.println(meeting);

		    			String nextLine;
		    			while ((nextLine = in.readLine()) != null) // show everything that comes to us from the socket
		    				System.out.println(nextLine);
		    		} catch (IOException e) {
		    			System.out.println("Error: "+e);
		    		} catch (Exception e) {
		    			System.out.println("Error: "+e);
		    		} finally {
		    			if (socket != null)
		    				try {
		    					socket.close();
		    				} catch (IOException e) {
		    					// we tried
		    				}
		    		}
		        }
		    }).start();
        } else {
        	System.out.println("Couldn't connect to the server.");
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
