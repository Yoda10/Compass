package home.yaron.compass;

import home.yaron.config.Config;
import home.yaron.volleyhttp.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/** 
 * 
 */
public class CompassActivity extends Activity implements OnClickListener
{	
	// Constants
	final static String TAG = CompassActivity.class.getSimpleName();
	final static String API_PROJECT_NUMBER = "231729049897";	
	final static String DEVICE_NAME = "Sony_tablet";
	final String Server_URL = "http://192.168.0.104:8089/api/scores/";

	// Members
	private TextView mainText = null;	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);

		mainText = (TextView)findViewById(R.id.main_text);
	}	

	@Override
	public void onClick(View v)
	{
		Log.d(TAG,"onClick(..)");

		switch (v.getId()) {
		case R.id.button_register:			
			gcmRegistration();
			break;
		case R.id.button_send:			
			sendMessage(DEVICE_NAME,"My Android message.");
			break;
		default:
			break;
		}		
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your application.
	 */
	private void registerDeviceOnServer(final String deviceName, final String registrationId)
	{
		final String URLx = "http://192.168.0.104:8089/api/scores/register";
		//final String URL = "http://192.168.0.104:8089/api/scores/sendMessage";
		//final String URL = "http://192.168.0.105:8089/api/scores/test";
		
		final String serverUrl = Config.getInstance(this).getProperty("ServerUrl");
		if( serverUrl == null )
		{
			Toast.makeText(this, "Server URL not found on config file.", Toast.LENGTH_LONG).show();
			return;
		}

		// Formulate the request and handle the response.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl+"register",
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				Log.d(TAG,"onResponse:\n"+response.toString());
				mainText.append("\nServer response:"+response.toString());
			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle error
				Log.d(TAG,"onErrorResponse:\n"+error.getMessage());
				mainText.append("\nServer error response:"+error.getMessage());
			}
		})
		{
			@Override
			protected Map<String,String> getParams() {
				Map<String,String> params = new HashMap<String, String>();				
				params.put("DeviceName",deviceName);
				params.put("RegistrationId",registrationId);
				//params.put("Message","This is my Message.");

				return params;
			}
		};

		// Add the request to the RequestQueue.
		VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
	}

	@Override
	public void finish()
	{
		Log.d(TAG,"finish(..)");		
		super.finish();

		VolleySingleton.getInstance(this).dispose();
		Config.getInstance(this).dispose();
	}	

	/***
	 * Register this mobile device on the GCM service.
	 */
	private void gcmRegistration()
	{
		new AsyncTask<Void, Void, String>() {
			String message = "empty";
			@Override
			protected String doInBackground(Void... params) {
				String registrationId = null;				 
				try
				{
					final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					registrationId = gcm.register(API_PROJECT_NUMBER);
					message = "Device successfully registered to GCM service."+"\nregId:"+registrationId;
					Log.d(TAG,message);
					registerDeviceOnServer(DEVICE_NAME,registrationId);
				}
				catch(Exception ex) {
					message = "Problem registering to GCM service.";
					Log.e(TAG, message, ex);					
				}
				return registrationId;
			}

			@Override
			protected void onPostExecute(String regId) {
				if( mainText != null )
					mainText.setText(message);
			}
		}.execute(null, null, null);
	}

	private void sendMessage(final String deviceName, final String message)
	{
		final String URLx = "http://192.168.0.104:8089/api/scores/sendMessage";
		//Config.getInstance(this).setProperty("URL", "http://192.168.0.104:8089/api/scores/sendMessage");
		//String x = Config.getProperty(this, "URL");
		
		final String serverUrl = Config.getInstance(this).getProperty("ServerUrl");
		if( serverUrl == null )
		{
			Toast.makeText(this, "Server URL not found on config file.", Toast.LENGTH_LONG).show();
			return;
		}

		// Formulate the request and handle the response.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl+"sendMessage", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {				
				Log.d(TAG,"onResponse:\n"+response.toString());
				mainText.setText("\nServer response:"+response.toString());
			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle error
				Log.d(TAG,"onErrorResponse:\n"+error.getMessage());
				mainText.setText("\nServer error response:"+error.getMessage());
			}
		})
		{
			@Override
			protected Map<String,String> getParams() {
				Map<String,String> params = new HashMap<String, String>();			
				params.put("DeviceName",deviceName);
				params.put("Message",message);				

				return params;
			}
		};

		// Add the request to the RequestQueue.
		VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
	}	
}
