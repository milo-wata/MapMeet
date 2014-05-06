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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.mapmtg);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					 .setText(mSectionsPagerAdapter.getPageTitle(i))
					 .setTabListener(this));
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() { // Don't use CPU time or battery when paused!
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Log.i("Swipe", "*****getItem " + position);
			switch(position) {
			case 0: return new MakeFrag();
			case 1: return new MapFrag();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_make).toUpperCase(l);
			case 1:
				return getString(R.string.title_map).toUpperCase(l);
			}
			return null;
		}
	}
}

class MakeFrag extends Fragment implements OnClickListener {
	private TextView address, title, date, time, attendees;
	private String addstr, titstr, datstr, timstr, attstr;
	private int resp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.make, container, false);
		address = (TextView) rootView.findViewById(R.id.location);
		title = (TextView) rootView.findViewById(R.id.mtgtitle);
		date = (TextView) rootView.findViewById(R.id.date);
		time = (TextView) rootView.findViewById(R.id.time);
		attendees = (TextView) rootView.findViewById(R.id.attendees);
		
		Button b = (Button) rootView.findViewById(R.id.newmtg);
        b.setOnClickListener(this);
		return rootView;
	}

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	
	public void onClick(View v) {
		if(v.getId() == R.id.newmtg) {
	        addstr = address.getText().toString();
	        titstr = title.getText().toString();
	        datstr = date.getText().toString();
	        timstr = time.getText().toString();
	        attstr = attendees.getText().toString();
	        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) { // this line and the two above are to find out if we're connected to the net
		        (new Thread(){ // network activity MUST occur on a separate thread
		        	@Override
			        public void run() {
			        	Socket socket = null;
			    		String source = "cscilab.bc.edu";
			    		PrintStream out = null;
			    		BufferedReader in = null;
			    		try {
			    			socket = new Socket(source, 10003);
			    			out = new PrintStream(socket.getOutputStream(), true);
			    			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			    			System.out.println("printing to the server...");
			    			out.println("newmeeting");
			    			out.println(titstr);
			    			out.println(datstr);
			    			out.println(timstr);
			    			out.println(attstr.replace('\n', '^'));
			    			out.println(addstr);
			    			String response = "";
			    			while(true) {
			    				response = in.readLine();
			    				if (response.equals("yes")){
			    					resp = 0;
			    			        break;
			    				}
			    				else if (response.equals("no")) {
			    			        resp = 1;
			    			        break;
			    				}
			    			}
			    		} catch (IOException e) {
			    			System.out.println("IOEx: "+e);
			    			resp = 2;
			    		} catch (Exception e) {
			    			System.out.println("Ex: "+e);
			    			resp = 3;
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
		        switch (resp) {
		        	case 0:
		        		address.setText("");
    			        title.setText("");
    			        date.setText("");
    			        time.setText("");
    			        attendees.setText("");
		        		(Toast.makeText(getActivity(), R.string.successmsg, Toast.LENGTH_SHORT)).show();
		        		break;
		        	case 1:
		        		(Toast.makeText(getActivity(), R.string.failmsg, Toast.LENGTH_SHORT)).show();
    			        break;
		        	case 2:
		        		(Toast.makeText(getActivity(), "Couldn't connect to the server", Toast.LENGTH_SHORT)).show();
		        		break;
		        	case 3:
		        		(Toast.makeText(getActivity(), "There was some problem: please try again.", Toast.LENGTH_SHORT)).show();
		        		break;
		        	default:
		        		(Toast.makeText(getActivity(), "There was some problem: please try again.", Toast.LENGTH_SHORT)).show();
		        		break;
		        }
		        
	        } else {
	        	System.out.println("Couldn't connect to the server.");
	        }

		}
    }
	
}

class MapFrag extends Fragment implements OnClickListener {
	private LocationListener listener;
	private LocationManager locManager;
	private WebView webView;
	private TextView nameField, titleField, dateField, timeField;
	private String urlocation = "";
	private int resp, i = 0;
	private List<String> mlist = new LinkedList<String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map, container, false);
		webView = (WebView) rootView.findViewById (R.id.curmap);
		webView.setWebViewClient(new WebViewClient());
		nameField = (TextView) rootView.findViewById (R.id.namefield);
		
		titleField = (TextView) rootView.findViewById(R.id.mtgt);
		dateField = (TextView) rootView.findViewById(R.id.mtgdate);
		timeField = (TextView) rootView.findViewById(R.id.mtgtime);
		locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		newlistener();
		if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			System.out.println("GPS_PROVIDER");
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        } else {
        	System.out.println("NO GPS_PROVIDER");
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        }
		
		Button b = (Button) rootView.findViewById(R.id.button);
		Button b1 = (Button) rootView.findViewById(R.id.button1);
		b1.setOnClickListener(this);
        b.setOnClickListener(this);
		return rootView;
	}
	
	private void newlistener() {
		System.out.println("newlistener");
		listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				System.out.println("Location changed.");
				if (location != null) {
					double latitude  = location.getLatitude();
					double longitude = location.getLongitude();
					String loc = Double.toString(latitude) + "," + Double.toString(longitude);
					System.out.println(loc);
					showMap(loc);
				} else {
					Log.d("CurrentLoc", "Location changed to null!!");
				}
			}
			@Override
			public void onProviderDisabled(String provider) {
				Log.d("GetLost", provider + " disabled");
			}
			@Override
			public void onProviderEnabled(String provider) {
				Log.d("FindMyLocation", provider + " enabled");
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.d("FindMyLocation", provider + " changed, see status/extras");
			}
		};
	}
	
	public void showMap(String location) {
		System.out.println("MAPPING YA BISH");
		String url = "http://maps.googleapis.com/maps/api/staticmap";
		url += "?center="+location;
		url += "&zoom=16";
		url += "&size=400x500";
		url += "&maptype=hybrid";
		url += "&sensor=true";
		url += "&markers=color:red%7Clabel:Y%7C"+location;
		Log.d("Maps", "URL:" + url);
		webView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button1) {
			ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) { // this line and the two above are to find out if we're connected to the net
		        new Thread(new Runnable() { // network activity MUST occur on a separate thread
			        public void run() {
			        	Socket socket = null;
			    		String source = "cscilab.bc.edu";
			    		PrintStream out = null;
			    		BufferedReader in = null;
			    		try {
			    			socket = new Socket(source, 10003);
			    			out = new PrintStream(socket.getOutputStream(), true);
			    			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			    			out.println("mapview");
			    			out.println(nameField.getText().toString());
			    			String meeting = in.readLine();
			    			while (!meeting.equals("no")) {
			    				mlist.add(meeting);
	        					meeting = in.readLine();
			    			}
			    		} catch (IOException e) {
			    			System.out.println("IOEx: "+e);
			    			resp = 1;
			    		} catch (Exception e) {
			    			System.out.println("Ex: "+e);
			    			resp = 0;
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
		    }
	        switch (resp) {
	        	case 1:
	        		(Toast.makeText(getActivity(), R.string.failmsg, Toast.LENGTH_SHORT)).show();
			        break;
	        	case 2:
	        		(Toast.makeText(getActivity(), "Couldn't connect to the server", Toast.LENGTH_SHORT)).show();
	        		break;
	        	default:
	        		(Toast.makeText(getActivity(), "Found all your meetings!", Toast.LENGTH_SHORT)).show();
	        		break;
	        }
		}
		
		else if(v.getId() == R.id.button) {
	    	showMeeting(i);
	    	i++;
		}
	
		else {
	    	System.out.println("Couldn't connect to the server.");
	    }
		
	}
	
	public void showMeeting(int j) {
		try {
    		String meet = mlist.get(j);
        	JSONObject jmeeting = new JSONObject(meet);
			urlocation = jmeeting.getString("Location").replace(" ", "+");
			titleField.setText(jmeeting.getString("Title"));
			dateField.setText(jmeeting.getString("Date"));
			timeField.setText(jmeeting.getString("Time"));
			nameField.setText(jmeeting.getString("Attendees").replace('^', '\n'));
			showMap(urlocation);
    	} catch (JSONException e) { 
    		(Toast.makeText(getActivity(), "Something bad happened. No meeting for you!", Toast.LENGTH_SHORT)).show();
    	} catch (IndexOutOfBoundsException e) {
			if (mlist.isEmpty()) (Toast.makeText(getActivity(), "You have no meetings. Go do something fun!", Toast.LENGTH_SHORT)).show();
			else {
				showMeeting(0);
				i = 0;
			}
		}
	}
}