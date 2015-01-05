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

Shell Commands
-------------
WIde adds multiple commands to the osgi felix gogo shell, that helps you to use the framework out of the box, however this is only a small part of WIde some commands might be helpful.

- `config` Shows the config as Json.

- `databases` Shows all available databases.

- `dbc` Shows any .dbc, .db2 or .adb storage in your data dir.

- `dbcformat` Shows an estimated format for any storage (detects key, string, float and int).

- `sql` Executes a sql query on a database.

- `enums` Shows an enum constant of the given enum value.

	> `enums UnitClass 3`

   	> Value: 3 = 0x3 = CLASS_MAGE

- `flags` Shows all flags of a value (flagsplitter).

	> `flags UnitFlags 0x432`
	>
   	> Value: 1074 = 0x432
   	>
   	>   0x2      = UNIT_FLAG_NON_ATTACKABLE
   	>
   	>  0x10      = UNIT_FLAG_RENAME
   	>
   	>  0x20      = UNIT_FLAG_PREPARATION
   	>
   	> 0x400      = UNIT_FLAG_LOOTING

	*If you want more enums to be supported add it to the entities bundle and pullrequest your changes.*


Use `help ${commnand}` or just `help` to get further information.
After your work is done use `shutdown` to exit the osgi enviroment.

**For the experienced users:**

The Apache Felix GOGO shell also [supports advanced structures like lists, variables and functions](http://felix.apache.org/site/rfc-147-overview.html). This makes it possible to use advanced scripting expressions like:

    each [0x7 1738 27] { flags UnitFlags $it }

Dependencies
--------------
Dependencies are managed automatically through the maven build system.

WIde uses following dependencies and bundles at runtime:

- [Apache Aries Blueprint](http://aries.apache.org/)

- [Pax Logging API & Service](https://ops4j1.jira.com/wiki/display/paxlogging/Pax+Logging)

- [Google guava](https://github.com/google/guava/)

- [Google gson](https://code.google.com/p/google-gson/)

- [MariaDB java-client](https://mariadb.com/kb/en/mariadb/client-libraries/mariadb-java-client/)

See file doc/Dependencies.txt for a list of all dependencies and its licences.
