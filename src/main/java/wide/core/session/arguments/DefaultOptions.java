package wide.core.session.arguments;

import org.apache.commons.cli.Options;

public abstract class DefaultOptions extends Options
{
    public DefaultOptions()
    {
        configure();
    }

    public abstract void configure();
}
