package wide.modules.gui.core.resources;

public class Resource
{
	public static String get(String name)
	{
		return Resource.class.getResource(name).toExternalForm();
	}
}
