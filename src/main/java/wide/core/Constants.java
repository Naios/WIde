package wide.core;

public enum Constants
{
    ////////////////////////////////////////////////////////
    // Strings
    STRING_DEFAULT_PROPERTIES("WIde.properties"),
    STRING_UNKNOWN("<unknown>"),
    STRING_TEST("test"),
    STRING_EXCEPTION("<exception>"),

    ////////////////////////////////////////////////////////
    // Property Names
    // Database
    PROPERTY_DATABASE_USER("core.session.database.user"),
    PROPERTY_DATABASE_HOST("core.session.database.host"),
    PROPERTY_DATABASE_PORT("core.session.database.port"),
    PROPERTY_DATABASE_PASSWORD("core.session.database.password"),

    PROPERTY_DATABASE_AUTH("core.session.database.auth"),
    PROPERTY_DATABASE_CHARACTER("core.session.database.character"),
    PROPERTY_DATABASE_WORLD("core.session.database.world"),

    PROPERTY_DATABASE_AUTOLOGIN("core.session.database.autologin"),
    PROPERTY_DATABASE_SAVE_PASSWORD("core.session.database.savepassword"),

    // Git
    PROPERTY_GIT_COMMIT_HASH("git.commit.id"),
    PROPERTY_GIT_COMMIT_HASH_SHORT("git.commit.id.abbrev"),
    PROPERTY_GIT_COMMIT_TIME("git.commit.time"),
    PROPERTY_GIT_COMMIT_DESCRIBE("git.commit.id.describe"),
    PROPERTY_GIT_BUILD_TIME("git.build.time"),
    PROPERTY_GIT_BRANCH("git.branch"),
    PROPERTY_GIT_TAG("git.tags"),

    // Directories
    PROPERTY_DIR_CACHE("core.enviroment.cachedir"),
    PROPERTY_DIR_DATA("core.enviroment.dbcdir"),
    PROPERTY_DIR_DBC("core.enviroment.dbcdir"),

    ////////////////////////////////////////////////////////
    // Paths
    PATH_DEFAULT_PROPERTIES_CREATE("properties/default.properties"),
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
