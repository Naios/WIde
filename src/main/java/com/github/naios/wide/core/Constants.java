
/*
 * Copyright (c) 2013 - 2014 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.core;

public enum Constants
{
    ////////////////////////////////////////////////////////
    // Strings
    STRING_DEFAULT_CONFIG_NAME("WIde.json"),
    STRING_UNKNOWN("<unknown>"),
    STRING_TEST("test"),
    STRING_EXCEPTION("<exception>"),
    STRING_MISSIN_ENTRY("<missing entry>"),
    STRING_NULL("<null>"),

    ////////////////////////////////////////////////////////
    // Property Names
    // Git
    PROPERTY_GIT_COMMIT_HASH("git.commit.id"),
    PROPERTY_GIT_COMMIT_HASH_SHORT("git.commit.id.abbrev"),
    PROPERTY_GIT_COMMIT_TIME("git.commit.time"),
    PROPERTY_GIT_COMMIT_DESCRIBE("git.commit.id.describe"),
    PROPERTY_GIT_BUILD_TIME("git.build.time"),
    PROPERTY_GIT_BRANCH("git.branch"),
    PROPERTY_GIT_TAG("git.tags"),

    ////////////////////////////////////////////////////////
    // Paths
    PATH_REPOSITORY_INFO("properties/git.properties"),
    PATH_APPLICATION_ICON("images/icon.png");

    private final String constant;

    Constants(final String constant)
    {
        this.constant = constant;
    }

    @Override
    public String toString()
    {
        return constant;
    }
}
