
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core.session.enviroment;

import java.util.Properties;

import com.github.naios.wide.core.Constants;

public class ApplicationInfo
{
    private String tag, hash, hash_abbrev, branch, commit_time, build_time;

    private final String path;

    public ApplicationInfo(final String path)
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
