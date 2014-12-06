package com.github.naios.wide.modules.gui.core.util;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FXMLFormFactory
{
	public static Parent create(final URL url)
	{
		try
		{
			return FXMLLoader.load(url);

		} catch (final Exception e)
		{
			e.printStackTrace();

			assert false : url + " does not exist in the package";
			return null;
		}
	}
}
