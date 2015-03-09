package home.yaron.gcm;

import home.chaim.compass.Chaim;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService
{
	// Constants
	final static String TAG = GcmIntentService.class.getSimpleName();

	public GcmIntentService()
	{
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.d(TAG,"onHandleIntent(..)");

		final Bundle extras = intent.getExtras();
		final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		final String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{ 
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
			{
				Log.d(TAG,"GCM error message type:"+messageType);				
			}			 
			else 
			{				
				Log.d(TAG, "GCM push message received.\nextras:" + extras.toString());							
				
				// Do some work.
				final Chaim chaim = new Chaim();
				chaim.doThePushWork(extras);
			}
		}

		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}	
}
