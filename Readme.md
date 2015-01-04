# WIde - MMORPG Framework Tools #
---------------------------------
[![Build Status](https://travis-ci.org/Naios/WIde.svg?branch=master)](https://travis-ci.org/Naios/WIde) **In Development!**

WIde helps you to edit multiple MMORPG Framework entities.
Currently WIde works with TrinityCore.

WIde is written as modular **Java** **OSGI Bundles** and makes heavily use of the **JavaFX** library.

**See an example output [here](https://gist.github.com/Naios/634b0bfbc04e56165f96).**

License
-------------
WIde is licensed under the [**Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International**](http://creativecommons.org/licenses/by-nc-sa/4.0/) license.

The License prohibits Commecial use, this also includes projects where you get donations or money through sells **(== private servers)**!

See LICENSE file for full details.

Requirements
---------------
- [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](http://maven.apache.org)

Installation
--------------
WIde uses **Maven** to build OSGI bundles.

1. Run `mvn clean install` to build the bundles.

2. WIde uses pax-runner to deploy an **osgi** enviroment for testing purposes. (`mvn clean install pax:provision`)

3. WIde uses Json config files to configure the enviroment. A default config is written on first startup. You may reconfigure config values to match your enviroment.

4. If you want to develop with WIde or create new bundles for it use [this Documentation](https://github.com/Naios/WIde/blob/master/doc/ide/eclipse/How%20to%20develop%20in%20Eclipse.md) to set up your enviroment.

Usage
-------------
In the osgi felix gogo shell there are multiple useful commands available:

- `config` Shows the config as Json.

- `databases` Shows all available databases.

- `dbc` Shows any .dbc, .db2 or .adb storage in your data dir.

- `dbcformat` Shows an estimated format for any storage (detects key, string, float and int).

- `sql` Executes a sql query on a database.

Use `help ${commnand}` or just `help` to get further information.
After your work is done use `shutdown` to exit the osgi enviroment.

Dependencies
--------------
Dependencies are managed automatically through the maven build system.

WIde uses following dependencies and bundles at runtime:

- [Apache Aries Blueprint](http://aries.apache.org/)

- [Apache commons-math3](http://commons.apache.org/proper/commons-math/)

- [Apache log4j](http://logging.apache.org/log4j/2.x/)

- [Pax Logging Service](https://ops4j1.jira.com/wiki/display/paxlogging/Pax+Logging)

- [Google guava](https://github.com/google/guava/)

- [Google gson](https://code.google.com/p/google-gson/)

- [MariaDB java-client](https://mariadb.com/kb/en/mariadb/client-libraries/mariadb-java-client/)

See file doc/Dependencies.txt for a list of all dependencies and its licences.
