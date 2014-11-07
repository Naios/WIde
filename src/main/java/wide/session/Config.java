package wide.session;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Config
{
	private final static String NAME = "WIde.xml";

	private final static Properties storage = create();

	private final static HashMap<String, StringProperty> properties = new HashMap<>();

	private final static Object monitor = new Object();

	private static Properties create()
	{
		Properties properties = new Properties();

		try
		{
			properties.loadFromXML(new FileInputStream(NAME));
		} catch (IOException e)
		{
			// Default properties
			properties.put("DB:User", "root");
			properties.put("DB:Host", "localhost");
			properties.put("DB:Port", "3306");
			properties.put("DB:World", "world");
			properties.put("DB:Password", "");
			properties.put("DB:Characters", "characters");
			properties.put("DB:Autologin", "false");
		}

		return properties;
	}

	public static void Save()
	{
		try
		{
			final FileOutputStream out = new FileOutputStream(NAME);
			storage.storeToXML(new FileOutputStream(NAME), "WIde config file");
			out.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static StringProperty Property(final String key)
	{
		synchronized (monitor)
		{
			StringProperty ppt = properties.get(key);

			if (ppt == null)
			{
				ppt = new SimpleStringProperty(storage.getProperty(key));
				ppt.addListener(new ChangeListener<String>()
				{
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue)
					{
						storage.setProperty(key, newValue);
					}
				});

				properties.put(key, ppt);
			}
			return ppt;
		}
	}
}
