package wide.scripts.fetch;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import wide.core.Constants;
import wide.core.WIde;
import wide.core.framework.extensions.scripts.Script;

class FileFetcher implements Runnable
{
    private final String origin, target;

    private final boolean overwrite;

    private final Runnable postaction;

    public FileFetcher(String origin, String target, boolean overwrite, Runnable postaction)
    {
        this.origin = origin;
        this.target = target;
        this.overwrite = overwrite;
        this.postaction = postaction;
    }

    @Override
    public void run()
    {
        try
        {
            if (!overwrite)
                if (new File(target).exists())
                    return;

            final URL url = new URL(origin);

            final ReadableByteChannel rbc = Channels.newChannel(url.openStream());

            final FileOutputStream fos = new FileOutputStream(target);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

            postaction.run();

        } catch (final Exception e)
        {
        }
    }
}

public class Fetch extends Script
{
    final static String WOWHEAD_URL = "http://www.wowhead.com";

    public Fetch()
    {
        super("fetch");
    }

    @Override
    public void run(String[] args)
    {
        if (args.length < 2)
        {
            System.out.println(toString() + ": Wrong Argument count!");
            return;
        }

        final ExecutorService pool = Executors.newFixedThreadPool(8);

        final String type = args[0];

        final int begin, end;

        final boolean overwrite = (args.length >= 4) && args[3].equals("o");

        try
        {
            begin = Integer.valueOf(args[1]);

            if (args.length < 3)
                end = begin;
            else
                end = Integer.valueOf(args[2]);

        } catch (final Exception e)
        {
            e.printStackTrace();
            return;
        }

        String targetdir = WIde.getEnviroment().getPath() + "/" + WIde.getConfig().getProperty(Constants.PROPERTY_DIR_CACHE).get();

        WIde.getEnviroment().createDirectory(targetdir);

        targetdir = targetdir + "/" + type;

        WIde.getEnviroment().createDirectory(targetdir);

        final AtomicInteger count = new AtomicInteger();

        for (int i = begin; i <= end; ++i)
        {
            final int id = i;

            pool.execute(new FileFetcher(WOWHEAD_URL + "/" + type + "=" + i, targetdir + "/"+ i + ".html", overwrite, new Runnable()
            {
                @Override
                public void run()
                {
                    if (WIde.getEnviroment().isTraceEnabled())
                        System.out.println(String.format("Fetched %s id: %s", type, id));

                    count.incrementAndGet();
                }
            }));
        }

        pool.shutdown();

        try
        {
            pool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (final InterruptedException e)
        {
        }

        System.out.println("Done, fetched " + count.get() + " " + type + " files.");
    }
}
