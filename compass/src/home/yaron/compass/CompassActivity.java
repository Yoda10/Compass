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
	final static String TAG = CompassActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);	
	}	

	@Override
	public void onClick(View v)
	{
		Log.d(TAG,"onClick(..)");

		switch (v.getId()) {
		case R.id.dummy_button:
			registerDeviceOnServer();
			break;
		default:
			break;
		}		
	}

	private void registerDeviceOnServer()
	{
		//final String URL = "http://192.168.0.105:8089/api/scores/register";
		final String URL = "http://192.168.0.105:8089/api/scores/sendMessage";
		//final String URL = "http://192.168.0.105:8089/api/scores/test";

		// Formulate the request and handle the response.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
				new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d(TAG,"onResponse:\n"+response.toString());
			}
		},
		new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle error
				Log.d(TAG,"onErrorResponse:\n"+error.getMessage());
			}
		})
		{
			@Override
			protected Map<String,String> getParams() {
				Map<String,String> params = new HashMap<String, String>();
				params.put("RegistrationId","RegistrationId_55");
				params.put("DeviceName","DeviceName_55");
				params.put("Message","This is my Message.");

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
			@Override
			protected String doInBackground(Void... params) {
				String regId = null;
				try
				{
					final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					regId = gcm.register("PROJECT_NUMBER");					
					Log.d(TAG,"Device successfully registered to GCM service regId:"+regId);
					sendRegistrationIdToBackend(regId);
				}
				catch(Exception ex) {
					Log.e(TAG, "Problem registering to GCM service.", ex);
				}
				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {

			}
		}.execute(null, null, null);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app.
	 */
	private void sendRegistrationIdToBackend(String regId)
	{
		// Your implementation here.
	}
}
