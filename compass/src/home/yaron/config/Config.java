package home.yaron.config;

import home.yaron.compass.R;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Config
{
	final static String TAG = Config.class.getSimpleName();
	private static Config instance; // Singleton

	private SharedPreferences sharedPref;
	private Map configMap = null;

	private Config(Context context)
	{		
		this.sharedPref = context.getSharedPreferences(context.getString(R.string.configuration_file_name), Context.MODE_PRIVATE);
		configMap = XmlDomParser.readXmlConfigFile();
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
		final String property = sharedPref.getString(key,null);
		if( property == null )
			Log.w(TAG,"Property key:"+key+" not found on configuration file.");

		return property;
	}

	public synchronized void setProperty(String key, String value)
	{		
		final SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key,value);
		editor.commit();
	}
}
