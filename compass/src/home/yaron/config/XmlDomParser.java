package home.yaron.config;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Environment;
import android.util.Log;

public class XmlDomParser
{
	final static String TAG = XmlDomParser.class.getSimpleName();

	public static Map readXmlConfigFile()
	{
		final Hashtable<String, String> map = new Hashtable<String, String>();

		try 
		{
			final File xmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Compass/compass_config.xml");
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document document = dBuilder.parse(xmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();

			final NodeList nodeList = document.getElementsByTagName("Config").item(0).getChildNodes(); // The root		
			for(int i = 0; i < nodeList.getLength(); i++)
			{
				final Node node = nodeList.item(i);	
				if(node.getNodeType() == Node.ELEMENT_NODE)
				{					
					final Element element = (Element)node;
					final String nodeName = element.getNodeName();					
					final String value = element.getFirstChild().getNodeValue();

					// Add to the key value map.
					map.put(nodeName, value);
					Log.d(TAG,"\nConfig key:key"+nodeName+" value:"+value);
				}
			}
		} 
		catch(Exception ex) {
			Log.e(TAG,"Problem loading configuration file.",ex);
		}

		Log.d(TAG,"Configuration file loaded successfully.");

		return map;
	}
}