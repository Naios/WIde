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
import wide.core.Constants;
import wide.core.WIde;
import wide.core.session.hooks.Hook;
import wide.core.session.hooks.HookListener;

public class Config
{
	private Properties storage = new Properties();

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

	private void load()
	{
	    properties.clear();
	    storage.clear();

	    try
        {
	        storage.load(getClass().getClassLoader().getResourceAsStream(Constants.PATH_DEFAULT_PROPERTIES_CREATE.toString()));

        } catch (final Exception e)
	    {
        }

        try
        {
            // We dont use the default method of properties...
            storage.load(new FileInputStream(WIde.getEnviroment().getConfigName()));

        } catch (final IOException e)
        {
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
    			final FileOutputStream out = new FileOutputStream(WIde.getEnviroment().getConfigName());
    			storage.store(out, "WIde Config");
    			out.close();

    			hasChanged = false;

    		} catch (final IOException e)
    		{
    		}
        }
	}

	public StringProperty getPropertyWithDefault(final String key, final String def)
	{
	    final StringProperty property = getProperty(key);
	    if (property.get().isEmpty())
	        property.set(def);

	    return property;
	}

	public StringProperty getProperty(final Object key)
	{
	    return getProperty(key.toString());
	}

	public StringProperty getProperty(final String key)
	{
		synchronized (properties)
		{
			StringProperty property = properties.get(key);

			if (property == null)
			{
			    final String value = storage.getProperty(key);

			    property = new SimpleStringProperty(value);
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
