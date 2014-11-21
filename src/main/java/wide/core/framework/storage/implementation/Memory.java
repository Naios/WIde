package wide.core.framework.storage.implementation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class for all memory "areas" that we could need to use. Mostly used for
 * reading files such as chunks but also for other things. <br>
 * <br>
 * <b>NOTE</b>: This class is designed to replace most of the functions of
 * <code>"starlight.taliis.core.chunks.chunk"</code> to keep that one more clean
 * for the moment.
 *
 * @author tharo
 * @see chunk
 */
public class Memory
{
    protected final static boolean DEBUG = false;

    // main storage
    public ByteBuffer              buff;

    /**
     * Constructor that slices the ByteBuffer at its current position and saves
     * the pointer.
     *
     * @param pointer
     *            The buffer.
     */
    public Memory(ByteBuffer pointer)
    {
        // take data pointer, Init byte order
        buff = pointer.slice();
        buff.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Create a new memory area with the given size in bytes and set it up.
     *
     * @param size
     *            The maximum size of the buffer.
     */
    public Memory(int size)
    {
        buff = ByteBuffer.allocate(size);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        buff.limit(size);
    }

    public Memory()
    {
    };

    /**
     * Recreate a ByteBuffer with the given size in our defaut settings
     *
     * @param size
     *            The size in bytes.
     * @return A new ByteBuffer.
     */
    protected ByteBuffer doRebirth(int size)
    {
        final ByteBuffer tmp = ByteBuffer.allocate(size);
        tmp.order(ByteOrder.LITTLE_ENDIAN);
        tmp.position(0);

        return tmp;
    }

    /**
     * Allows to expand the memory by the given size. The old size is taken from
     * <code>limit()</code>. <br>
     * <br>
     *
     * Read and write pointer will be placed at first new position.
     *
     * @param additional_space
     *            Extend <code>memory</code> by this value
     */
    public void expand(int additional_space)
    {
        final ByteBuffer tmp = ByteBuffer.allocate(buff.limit()
                + additional_space);
        tmp.order(ByteOrder.LITTLE_ENDIAN);
        tmp.position(0);
        buff.position(0);

        tmp.put(buff);

        buff = tmp;
    }

    public void render()
    {
    };

    /**
     * @return The current limit of our buffer.
     */
    public int limit()
    {
        return buff.limit();
    }

    /**
     * @param pos
     *            Set the position where our buffer is reading/writing at the
     *            moment.
     */
    public void position(int pos)
    {
        buff.position(pos);
    }

    /**
     * @return The position where our buffer is reading/writing at the moment.
     */
    public int position()
    {
        return buff.position();
    }

    /**
     * Put some data on our buffer.
     *
     * @param data
     *            Served in another buffer.
     */
    public void put(ByteBuffer data)
    {
        buff.put(data);
    }

    /**
     * @return Get the managed ByteBuffer.
     */
    public ByteBuffer get()
    {
        return buff;
    }
}
