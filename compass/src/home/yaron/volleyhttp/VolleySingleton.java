package home.yaron.volleyhttp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton
{
	final static String TAG = VolleySingleton.class.getSimpleName();
	private static VolleySingleton instance; // Singleton

	private RequestQueue requestQueue;	
	private Context context;

	private VolleySingleton(Context context)
	{
		this.context = context;
		requestQueue = getRequestQueue();
	}

	public static synchronized VolleySingleton getInstance(Context context)
	{
		if (instance == null)
			instance = new VolleySingleton(context);
		return instance;
	}	

	public RequestQueue getRequestQueue() 
	{
		if (requestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		}
		return requestQueue;
	}

	public <T> void addToRequestQueue(Request<T> request)
	{
		Log.d(TAG,"addToRequestQueue(..)");
		getRequestQueue().add(request);
	}
	
	public void dispose()
	{
		Log.d(TAG,"dispose(..)");
		getRequestQueue().stop();
		instance = null;
	}
}
