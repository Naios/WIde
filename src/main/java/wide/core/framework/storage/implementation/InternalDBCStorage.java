package wide.core.framework.storage.implementation;

/**
 * Blizzards dbc file format
 * http://wowdev.org/wiki/index.php/DBC
 *
 * NOTE: This file is under heavy construction. Some parts got
 * allready rewritten but theres still something to do!
 *
 *
 * @author tharo
 */

import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.util.Vector;

public class InternalDBCStorage extends Memory
{
    // offsets
    final static int                     magic            = 0x00;
    final static int                     nRecords         = 0x04;
    final static int                     nFields          = 0x08;
    final static int                     recordSize       = 0x0C;
    final static int                     strBlockSize     = 0x10;
    final static int                     data             = 0x14;

    // Column type constants
    public final static int              COL_TYPE_NUMERIC = 0;
    public final static int              COL_TYPE_FLOAT   = 1;
    public final static int              COL_TYPE_STRING  = 2;
    public final static int              COL_TYPE_BOOLEAN = 3;
    public final static int              COL_TYPE_COLOR   = 4;

    // data storage
    private int                          strOffs, rl;
    private Memory                       dataBlock, strTable;
    private Vector<ZeroTerminatedString> vStrings;               // parsed
                                                                  // strings
    private Vector<Integer>              vColTypes;

    // -------------------------------------------------------------------
    // Init -------------------------------------------------------------

    /**
     * Use given ByteBuffer ressource to init our dbc object
     *
     * @param databuffer
     */
    public InternalDBCStorage(ByteBuffer databuffer) throws InvalidClassException
    {
        // setup data
        super(databuffer);

        // check magic ...
        if (getMagic().compareTo("WDBC") != 0)
        {
            throw new InvalidClassException("Invalid DBC data!");
        }

        // save our bytelenght for internal calculations
        rl = getRecordSize() / getNFields();

        // load our Datablock
        initData();

        // load our stringzable
        initStrings();

        // detect out column types
        vColTypes = new Vector<Integer>();
        detectTypes();
    }

    /**
     * Init out data in a seperate memory instance
     */
    private void initData()
    {
        buff.position(data);
        dataBlock = new Memory(buff);
        dataBlock.buff.limit(getRecordSize() * getNRecords());
    }

    /**
     * Initialisize out Strings as ZeroTerminatedString classes
     */
    private void initStrings()
    {
        // string offset?
        strOffs = data + getRecordSize() * getNRecords();

        // init string table
        buff.position(strOffs);
        strTable = new Memory(buff);

        // count and find all strings
        vStrings = new Vector<ZeroTerminatedString>();
        strTable.buff.position(0);
        while (strTable.buff.hasRemaining())
        {
            final ZeroTerminatedString tmp = new ZeroTerminatedString(
                    strTable.buff);
            vStrings.add(tmp);
        }
    }

    /**
     * Trys to detect the column types
     */
    private void detectTypes()
    {
        for (int col = 0; col < this.getNFields(); col++)
        {
            vColTypes.add(col, COL_TYPE_NUMERIC);

            boolean ok = true;
            int add = 0; // dont count zero fields
            int couldbe = 0; // what r we looking for?
            final Vector<Integer> lastVals = new Vector<Integer>();

            final int MAXTRYS = 10;
            final int MAXLOOPS = 20;

            // empty table?
            if (getNRecords() == 0)
                ok = false;

            for (int i = 0; i < (MAXTRYS + add) && i < getNRecords()
                    && i < MAXLOOPS; i++)
            {
                boolean found = false;
                final Object val = getData(col, i);
                final long v = ((Number) val).longValue();

                // no zero fields please
                if (v == 0)
                    add++;
                else
                {
                    // float?
                    if (v > 1000000 || v < -1000000)
                    {
                        couldbe = COL_TYPE_FLOAT;
                        found = true;
                    }
                    // string?
                    else if (couldbe != COL_TYPE_FLOAT)
                    {
                        if (getStringByOffset((int) v) != null)
                        {
                            couldbe = COL_TYPE_STRING;
                            found = true;
                            lastVals.add((int) v);
                        }
                    }

                    if (found == false)
                    {
                        ok = false;
                        break;
                    }
                }
            }

            // did we were able to detect the type?
            if (ok == true)
            {
                // try to kill out "all the same" errors
                boolean same = true;
                if (couldbe == COL_TYPE_STRING)
                {
                    int old = lastVals.firstElement();
                    for (final int val : lastVals)
                    {
                        if (val != old)
                        {
                            same = false;
                            break;
                        }
                        old = val;
                    }
                }
                else
                    same = false;

                if (same == false)
                    vColTypes.add(col, couldbe);
                else
                    vColTypes.add(col, COL_TYPE_NUMERIC);
            }
            else
                vColTypes.add(col, COL_TYPE_NUMERIC);
        }
    }

    // -------------------------------------------------------------------
    // Render Functions -------------------------------------------------

    /**
     * This function have to be called before the buffer get written back to the
     * file.
     *
     * It merges dataBlock and stringTable together and recalculates the header.
     *
     */
    @Override
    public void render()
    {
        // render strings
        if (vStrings.lastElement().getEndOffset() > strTable.buff.limit())
        {
            // speicher vergroessern
            final int oldLimit = strTable.buff.limit();
            final int add = vStrings.lastElement().getEndOffset() - oldLimit;
            strTable.expand(add);
            strTable.position(oldLimit);

            // neue Elemente anfuegen
            for (final ZeroTerminatedString str : vStrings)
            {
                if (str.getEndOffset() > oldLimit)
                {
                    str.buff.position(0);
                    strTable.put(str.buff);
                    strTable.buff.put((byte) 0);
                }
            }
        }

        // TODO: render fields

        // render offsets
        buff.putInt(strBlockSize, strTable.limit());

        // calculate new size
        final Memory tmp = new Memory(data + dataBlock.limit()
                + strTable.limit());
        // copy header
        buff.position(0);
        buff.limit(data);
        tmp.put(buff);

        // copy data field
        tmp.position(data);
        dataBlock.buff.position(0);
        tmp.put(dataBlock.buff);

        // copy strings
        strTable.buff.position(0);
        tmp.put(strTable.buff);

        // prevent
        buff = tmp.buff;

        // re init
        initData();
        initStrings();

        buff.position(0);
    }

    // -------------------------------------------------------------------
    // Put and get ------------------------------------------------------

    /**
     * Returns the number of indizies in our string table
     *
     * @return
     */
    public int getStrLenght()
    {
        return vStrings.size();
    }

    /**
     * Get the magic string of given Data
     *
     * @return magic string
     */
    public String getMagic()
    {
        final byte mag[] = new byte[4];
        buff.position(0);
        buff.get(mag, magic, 4);

        return new String(mag);
    }

    public int getNRecords()
    {
        return buff.getInt(nRecords);
    }

    public void setNRecords(int val)
    {
        buff.putInt(nRecords, val);
    }

    public int getNFields()
    {
        return buff.getInt(nFields);
    }

    public int getRecordSize()
    {
        return buff.getInt(recordSize);
    }

    public int getStringBlockSize()
    {
        return buff.getInt(strBlockSize);
    }

    /**
     * Get a 'word' with the record size lenght from given offset
     *
     * @param offset
     *            were to get word from
     */
    private Object getWord(int offset)
    {
        if (rl == 4)
            return dataBlock.buff.getInt(offset);
        else if (rl == 2)
            return dataBlock.buff.getShort(offset);
        else if (rl == 1)
            return dataBlock.buff.get(offset);
        return 0;
    }

    private void setWord(int offset, int value)
    {
        if (rl == 4)
            dataBlock.buff.putInt(offset, value);
        else if (rl == 2)
            dataBlock.buff.putShort(offset, (short) value);
        else if (rl == 1)
            dataBlock.buff.put(offset, (byte) value);
    }

    /**
     * Get the word located on this position
     *
     * @param col
     * @param row
     * @return
     */
    public Object getData(int col, int row)
    {
        final int off = getRecordSize() * row + (col * rl);
        return getWord(off);
    }

    public void setData(int col, int row, int value)
    {
        final int off = getRecordSize() * row + (col * rl);
        setWord(off, value);
    }

    /**
     * returns string entry that starts at given string-offset
     *
     * @param strPos
     *            string offset in the stringblock
     * @return
     */
    public ZeroTerminatedString getString(int strPos)
    {
        return vStrings.get(strPos);
    }

    public ZeroTerminatedString getStringByOffset(int offs)
    {
        for (final ZeroTerminatedString str : vStrings)
        {
            if (str.getInitOffset() == offs)
                return str;
        }
        // System.err.println("Unknown Offset!! : " + offs);
        return null;/**/
    }

    /**
     * Adds a new String to the End of the String Table
     *
     * @param str
     *            new String Entry
     */
    public void addString(String str)
    {
        final ZeroTerminatedString tmp = new ZeroTerminatedString(str);

        tmp.initialOffset = vStrings.lastElement().getEndOffset();
        vStrings.add(tmp);
    }

    /**
     * creates a copy of row with the new id
     *
     * @param row_id
     */
    public void copyRow(int line)
    {
        final int rz = getRecordSize();
        if (line * rz > dataBlock.buff.limit())
            return;

        // extend memory
        final int oldLimit = dataBlock.buff.limit();
        dataBlock.expand(getRecordSize());

        // copy row
        final byte data[] = new byte[rz];
        dataBlock.buff.position(line * rz);
        dataBlock.buff.get(data);

        dataBlock.position(oldLimit);
        dataBlock.buff.put(data);

        // increment n Records
        setNRecords(getNRecords() + 1);
    }

    /**
     * Removes entrys in our dbc file
     *
     * @param startLine
     *            line index where deletion have to start
     * @param lenght
     *            number of rows we wish to delete
     */
    public void deleteRows(int startLine, int lenght)
    {
        if (lenght <= 0)
            return;
        if (startLine < 0)
            return;
        if (startLine + lenght > getNRecords())
            return;

        final int rz = getRecordSize();

        // data before the deletion
        dataBlock.position(0);
        final Memory bfore = new Memory(dataBlock.buff);
        bfore.buff.limit(startLine * rz);
        bfore.position(0);

        // data behind our deletion
        dataBlock.position((startLine + lenght) * rz);
        final Memory after = new Memory(dataBlock.buff);
        after.position(0);

        // new datablock
        dataBlock = new Memory(dataBlock.limit() - (lenght * rz));

        // copy data after deletion
        dataBlock.put(bfore.buff);
        dataBlock.put(after.buff);

        // decrement n Records
        setNRecords(getNRecords() - lenght);
    }

    /**
     * Returns the quessed column type at col index
     *
     * @param index
     * @return
     */
    public int getColType(int index)
    {
        return vColTypes.get(index);
    }
}
