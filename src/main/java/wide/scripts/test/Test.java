package wide.scripts.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import wide.core.framework.extensions.scripts.Script;
import wide.core.framework.storage.DBCStorage;
import wide.core.framework.storage.DBCStructure;
import wide.core.framework.storage.implementation.InternalDBCStorage;

public class Test extends Script
{
    public Test()
    {
        super("test");
    }

    @Override
    public void run(String[] args)
    {
        
        
        
        

        
        testmy(args[0]);
        
        // testtallis(args[0]);
    }

    void testmy(String path)
    {
        try
        {
            final DBCStorage<MapStructure> dbc = new DBCStorage<MapStructure>(path)
            {
                @Override
                public DBCStructure create()
                {
                    return new MapStructure();
                }
            };

            System.out.println(dbc.toString());
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    void testtallis(String path)
    {
        try
        {
            final InternalDBCStorage dbc = new InternalDBCStorage(openBuffer(path));

            for (int y = 0; y < dbc.getNRecords(); ++y)
            {
                for (int x = 0; x < dbc.getNFields(); ++x)
                {
                    String data;

                    switch (dbc.getColType(x))
                    {
                        case InternalDBCStorage.COL_TYPE_BOOLEAN:
                            data = String.valueOf((boolean)dbc.getData(x, y));
                            break;
                        case InternalDBCStorage.COL_TYPE_COLOR:
                            data = "<color>";
                            break;
                        case InternalDBCStorage.COL_TYPE_FLOAT:
                            // data = String.valueOf(dbc.getData(x, y));
                            // break;
                        case InternalDBCStorage.COL_TYPE_NUMERIC:
                            data = String.valueOf((int)dbc.getData(x, y));
                            break;
                        case InternalDBCStorage.COL_TYPE_STRING:
                            data = dbc.getStringByOffset((int) dbc.getData(x, y)).toString();
                            break;
                        default:
                            data = dbc.getData(x, y).toString();
                            break;
                    }

                    System.out.print(data + ", ");
                }

                System.out.println();
            }

        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ByteBuffer openBuffer(String FileName)
    {
        try
        {
            // open the file an isolate the file channel
            final File f = new File(FileName);
            if (f.exists() == false)
            {
                System.err.println("File not Found: " + FileName);
                return null;
            }
            // FileInputStream fis = new FileInputStream(f);
            final RandomAccessFile fis = new RandomAccessFile(f, "rw");
            final FileChannel fc = fis.getChannel();

            // create buffer (lill endian) with our files data
            final int sz = (int) fc.size();
            final ByteBuffer bb = fc.map(FileChannel.MapMode.READ_WRITE, 0, sz);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            // re alocate data to RAM
            final ByteBuffer tmp = ByteBuffer.allocate(bb.capacity());
            tmp.order(ByteOrder.LITTLE_ENDIAN);
            bb.position(0);
            tmp.put(bb);
            tmp.position(0);

            // clean up
            fc.close();
            fis.close();

            return tmp;
        } catch (final FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
