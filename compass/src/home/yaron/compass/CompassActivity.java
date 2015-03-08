package home.yaron.compass;

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
	final static String API_SERVER_KEY = "AIzaSyAd7A0j7NOqoAyQRu3R-xBaVIDODoe-_aM";
	
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
		case R.id.dummy_button:
			//registerDeviceOnServer();
			gcmRegistration();
			break;
		default:
			break;
		}		
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app.
	 */
	private void registerDeviceOnServer(final String deviceName, final String regId)
	{
		final String URL = "http://192.168.0.104:8089/api/scores/register";
		//final String URL = "http://192.168.0.104:8089/api/scores/sendMessage";
		//final String URL = "http://192.168.0.105:8089/api/scores/test";

		// Formulate the request and handle the response.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
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
				params.put("RegistrationId",regId);
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
	}	

	public void gcmRegistration()
	{
		new AsyncTask<Void, Void, String>() {
			String message = "empty";
			@Override
			protected String doInBackground(Void... params) {
				String regId = null;				 
				try
				{
					final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					regId = gcm.register(API_PROJECT_NUMBER);
					message = "Device successfully registered to GCM service."+"\nregId:"+regId;
					Log.d(TAG,message);
					registerDeviceOnServer("Sony_tablet",regId);
				}
				catch(Exception ex) {
					message = "Problem registering to GCM service.";
					Log.e(TAG, message, ex);					
				}
				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {
				if( mainText != null )
					mainText.setText(message);
			}
		}.execute(null, null, null);
	}	
}
