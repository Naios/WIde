package wide.core.framework.storage;


public class MyOwnDBCStorage extends MyOwnStorage
{
    private final static int HEADER_SIZE = 20, DBCFMTSIG = 0x43424457;

    public MyOwnDBCStorage(String path) throws Exception
    {
        super(path);

        // Store byte values
        m_rows = new byte[recordsCount][];

        for (int i = 0; i < recordsCount; i++)
        {
            m_rows[i] = new byte[recordSize];
            istream.read(m_rows[i]);
        }

    }

    @Override
    public int getHeaderSize()
    {
        return HEADER_SIZE;
    }

    @Override
    public int getDBCFmtSig()
    {
        return DBCFMTSIG;
    }
}
