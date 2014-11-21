package wide.core.framework.storage;

import java.io.InvalidClassException;
import java.nio.ByteBuffer;

/**
 * A new created superclass for all wow file types.
 *
 * This new layer is created as counterpart for the new plugin editor in order
 * to allow changing file engines.
 *
 * It does basicaly noting else then hooking constructors and implements
 * render() that need to be run before a file can stored back to binary memory
 *
 * @author ganku
 *
 */

public class WOWFile extends Memory
{
    public WOWFile()
    {
        super();
    }

    public WOWFile(ByteBuffer dataBuffer) throws InvalidClassException
    {
        super(dataBuffer);
    }

    @Override
    public void render()
    {
    };
}
