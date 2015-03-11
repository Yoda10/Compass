package home.yaron.config;

import java.util.Hashtable;

import android.content.Context;
import android.util.Log;

public class Config
{
	final static String TAG = Config.class.getSimpleName();
	private static Config instance; // Singleton

	private Hashtable<String,String> configMap = null; // Key value pairs.

	private Config(Context context)
	{		
		configMap = (Hashtable<String,String>)XmlDomParser.readXmlConfigFile();
	}

	public static synchronized Config getInstance(Context context)
	{
		if (instance == null)
			instance = new Config(context.getApplicationContext());
		return instance;
	}

	public void dispose()
	{
		Log.d(TAG,"dispose(..)");		
		instance = null;
	}

	public static String getProperty(Context context, String key)
	{
		return getInstance(context).getProperty(key);			
	}

	public String getProperty(String key)
	{		
		final String property = configMap.get(key);
		if( property == null )
			Log.w(TAG,"Property key:"+key+" not found on configuration file.");

		return property;
	}
}
