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

        this.tag = properties.getProperty(Constants.PROPERTY_GIT_TAG.toString(), Constants.STRING_UNKNOWN.toString());
        this.hash = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_HASH.toString(), Constants.STRING_UNKNOWN.toString());
        this.hash_abbrev = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_HASH_SHORT.toString(), Constants.STRING_UNKNOWN.toString());
        this.branch = properties.getProperty(Constants.PROPERTY_GIT_BRANCH.toString(), Constants.STRING_UNKNOWN.toString());
        this.commit_time = properties.getProperty(Constants.PROPERTY_GIT_COMMIT_TIME.toString(), Constants.STRING_UNKNOWN.toString());
        this.build_time = properties.getProperty(Constants.PROPERTY_GIT_BUILD_TIME.toString(), Constants.STRING_UNKNOWN.toString());
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
