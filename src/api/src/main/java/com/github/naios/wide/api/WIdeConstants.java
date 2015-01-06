
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.api;

public enum WIdeConstants
{
    ////////////////////////////////////////////////////////
    // Version
    VERSION_WIDE_MAIN_CONFIG("1.0"),
    VERSION_WIDE_SCHEMATIC_CONFIG("1.0"),
    VERSION_WIDE_ALIAS_CONFIG("1.0"),

    ////////////////////////////////////////////////////////
    // Strings
    STRING_EMPTY(""),
    STRING_UNKNOWN("<unknown>"),
    STRING_TEST("test"),
    STRING_EXCEPTION("<exception>"),
    STRING_MISSIN_ENTRY("<missing entry>"),
    STRING_NULL("null"),

    ////////////////////////////////////////////////////////
    // Properties
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
    PATH_REPOSITORY_INFO("properties/git.properties");

    private final String constant;

    WIdeConstants(final String constant)
    {
        this.constant = constant;
    }

    @Override
    public String toString()
    {
        return constant;
    }
}
