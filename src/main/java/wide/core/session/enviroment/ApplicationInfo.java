package wide.core.session.enviroment;

import java.util.Properties;

import wide.core.Constants;

public class ApplicationInfo
{
    private String tag, hash, hash_abbrev, branch, commit_time, build_time;

    private final String path;

    public ApplicationInfo(String path)
    {
        this.path = path;
    }

    public void read()
    {
        final Properties properties = new Properties();

        try
        {
            properties.load(getClass().getClassLoader().getResourceAsStream(path));

        } catch (final Exception e)
        {
        }

        this.tag = properties.getProperty(Constants.PROPERTY_GIT_TAG.get(), Constants.STRING_UNKNOWN.get());
        this.hash = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_HASH.get(), Constants.STRING_UNKNOWN.get());
        this.hash_abbrev = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_HASH_SHORT.get(), Constants.STRING_UNKNOWN.get());
        this.branch = properties.getProperty(Constants.PROPERTY_GIT_BRANCH.get(), Constants.STRING_UNKNOWN.get());
        this.commit_time = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_TIME.get(), Constants.STRING_UNKNOWN.get());
        this.build_time = properties.getProperty(Constants.PROPERTY_GIT_BUILD_TIME.get(), Constants.STRING_UNKNOWN.get());
    }

    public String getTag()
    {
        return tag;
    }

    public String getHashShort()
    {
        return hash_abbrev;
    }

    public String getHash()
    {
        return hash;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getCommitTime()
    {
        return commit_time;
    }

    public String getBuildTime()
    {
        return build_time;
    }
}
