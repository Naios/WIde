package wide.core.session.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import wide.core.WIde;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;

public class Config
{
	private final Properties storage = new Properties();
	
	private final HashMap<String, StringProperty> properties = new HashMap<>();
	
	private boolean hasChanged = false;

	public Config()
	{
	    // After Arg parser has finished, load config with args
        WIde.getHooks().addListener(new HookListener(Hook.ON_ARGUMENTS_LOADED, this)
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
	}

	protected String[][] getDefaultProperties()
	{
	    return new String[][] {};
	}
	
	private void load()
	{
        final String[][] defaultProperties = getDefaultProperties();

        // Array size must be [?][2]
        assert (defaultProperties[0].length == 2);

        // Try to load an existing config file
        try
        {
            storage.loadFromXML(new FileInputStream(WIde.getArgs().getConfigName()));
        } catch (IOException e)
        {
            hasChanged = true;
        }
        
        for (String[] property : defaultProperties)
            if (!storage.containsKey(property[0]))
            {
                storage.put(property[0], property[1]);
                hasChanged = true;
            }

        // Hooks.ON_CONFIG_LOADED
        WIde.getHooks().fire(Hook.ON_CONFIG_LOADED);
	}

	public void save()
	{
	    if (!hasChanged)
	        return;
	    
	    synchronized (properties)
        {
    		try
    		{
    			final FileOutputStream out = new FileOutputStream(WIde.getArgs().getConfigName());
    			storage.storeToXML(out, "WIde Config");
    			out.close();
    			
    			hasChanged = false;
    
    		} catch (IOException e)
    		{
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
						
						if (!hasChanged)
						    hasChanged = true;
						
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
