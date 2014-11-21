package wide.core.framework.storage.implementation;

import java.nio.ByteBuffer;

/**
 * A small class for Zero terminated Strings
 *
 * @author tharo
 *
 */
public class ZeroTerminatedString extends Memory
{
    public int initialOffset = -1;

    /**
     * Creates a zero terminated T<code>String</code>.
     *
     * @param pointer
     *            A ByteBuffer where our String is.
     */
    public ZeroTerminatedString(ByteBuffer pointer)
    {
        super(pointer);

        // find str end
        byte t = 0;
        do
            t = buff.get();
        while (t != 0);

        // limit our data area and kill out last zero
        buff.limit(buff.position() - 1);

        // remember our initial offset in the buffer
        initialOffset = pointer.position();

        // push pointer
        pointer.position(pointer.position() + buff.position() + 1);
    }

    /**
     * Create a zero terminated <code>String</code> by registering new memory to
     * apply newString.
     *
     * @param newString
     *            The new <code>String</code>.
     */
    public ZeroTerminatedString(String newString)
    {
        super(newString.length());
        buff.position(0);
        buff.put(newString.getBytes());
    }

    /**
     * Sets a new string.
     *
     * @param newString
     *            The new <code>String</code>.
     */
    public void setString(String newString)
    {
        buff = doRebirth(newString.length());
        buff.position(0);
        buff.put(newString.getBytes());
    }

    /**
     * Returns this String as Java-String.
     */
    @Override
    public String toString()
    {
        final byte str[] = new byte[buff.limit()];

        buff.position(0);
        buff.get(str, 0, buff.limit());

        return new String(str);
    }

    /**
     * @return The length.
     */
    public int getLength()
    {
        return buff.limit();
    }

    /**
     * @return The initial offset.
     */
    public int getInitOffset()
    {
        return initialOffset;
    }

    /**
     * @return The offset of the end of our buffer.
     */
    public int getEndOffset()
    {
        return buff.limit() + initialOffset + 1;
    }
}
