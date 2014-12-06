package com.github.naios.wide.core.session.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

import com.github.naios.wide.core.Constants;
import com.github.naios.wide.core.WIde;
import com.github.naios.wide.core.framework.game.GameBuild;
import com.github.naios.wide.core.session.hooks.Hook;
import com.github.naios.wide.core.session.hooks.HookListener;

public class Config
{
	private final Properties storage = new Properties();

	private final HashMap<String, StringProperty> properties = new HashMap<>();

	private boolean hasChanged = false;

	private boolean isLoaded = false;

	private final ObjectProperty<GameBuild> cachedBuild =
	        new SimpleObjectProperty<GameBuild>();

	public Config()
	{
	    // After Arg parser has finished, load config with args
        WIde.getHooks().addListener(new HookListener(Hook.ON_ENVIROMENT_LOADED, this)
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

        // Init GameBuilds
        getProperty(Constants.PROPERTY_ENVIROMENT_VERSION).addListener((ChangeListener<String>) (observable, oldValue, newValue) -> recalculateGameBuild());

        isLoaded = true;

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
    		    hasChanged = false;
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
			    property.addListener((ChangeListener<String>) (observable, oldValue, newValue) ->
                {
                	storage.setProperty(key, newValue);

                	if (!hasChanged)
                	    hasChanged = true;

                	// Hook.ON_CONFIG_CHANGED
                	WIde.getHooks().fire(Hook.ON_CONFIG_CHANGED);
                });

				properties.put(key, property);
			}
			return property;
		}
	}

	public ObjectProperty<GameBuild> getGameBuild()
	{
        return cachedBuild;
	}

	private void recalculateGameBuild()
	{
	    final String configVersion = getProperty(Constants.PROPERTY_ENVIROMENT_VERSION).get();
	    for (final GameBuild build : GameBuild.values())
	        if (build.getVersion().equals(configVersion))
	        {
	            cachedBuild.set(build);
	            return;
	        }

	    cachedBuild.set(null);
	}

	public boolean isLoaded()
	{
	    return isLoaded;
	}
}
