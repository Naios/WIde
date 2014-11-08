package wide.session.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import wide.session.WIde;
import wide.session.hooks.Hook;
import wide.session.hooks.HookListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Config
{
	private Properties storage;

	private String file;

	protected HashMap<String, StringProperty> properties = new HashMap<>();

	public Config()
	{
	    // After Arg parser has finished, load config with args
        WIde.getHooks().addListener(new HookListener(Hook.ON_ARGS_FINISHED, this)
        {
            @Override
            public void informed()
            {
                load();
            }
        });
	    
	    // Saves Config on Close
	    WIde.getHooks().addListener(new HookListener(Hook.ON_APPLICATION_STOP, this)
        {
            @Override
            public void informed()
            {
                save();
            }
        });

	    // Config Loaded means always Config Changed
	    WIde.getHooks().addListener(new HookListener(Hook.ON_CONFIG_LOADED, this)
        {
            @Override
            public void informed()
            {
                // Hook.ON_CONFIG_CHANGED
                WIde.getHooks().fire(Hook.ON_CONFIG_CHANGED);
            }
        });
	}

	protected String[][] getDefaultProperties()
	{
	    return new String[][] {};
	}
	
	private void load()
	{
	    this.file = WIde.getArgs().getConfigName();

        final String[][] defaultProperties = getDefaultProperties();
        final Properties defaults = new Properties();

        // Array size must be [?][2]
        assert (defaultProperties[0].length == 2);

        for (String[] property : defaultProperties)
            defaults.put(property[0], property[1]);

        storage = new Properties(defaults);

        // Try to load an existing config file
        try
        {
            storage.loadFromXML(new FileInputStream(file));
        } catch (IOException e)
        {
        }

        // Hooks.ON_CONFIG_LOADED
        WIde.getHooks().fire(Hook.ON_CONFIG_LOADED);
	}

	public void save()
	{
	    synchronized (properties)
        {
    		try
    		{
    			final FileOutputStream out = new FileOutputStream(file);
    			storage.storeToXML(out, "WIde Config");
    			out.close();
    
    		} catch (IOException e)
    		{
    			e.printStackTrace();
    		}
        }
	}

	public StringProperty getProperty(final String key)
	{
		synchronized (properties)
		{
			StringProperty property = properties.get(key);

			if (property == null)
			{
			    property = new SimpleStringProperty(storage.getProperty(key));
			    property.addListener(new ChangeListener<String>()
				{
					@Override
					public void changed(
							ObservableValue<? extends String> observable,
							String oldValue, String newValue)
					{
						storage.setProperty(key, newValue);
						
						// Hook.ON_CONFIG_CHANGED
						WIde.getHooks().fire(Hook.ON_CONFIG_CHANGED);
					}
				});

				properties.put(key, property);
			}
			return property;
		}
	}
}
