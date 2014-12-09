# WIde - MMORPG Framework IDE #
---------------------------------
[![Build Status](https://travis-ci.org/Naios/WIde.svg)](https://travis-ci.org/Naios/WIde)

WIde helps you to edit multiple MMORPG Framework entities.
Currently WIde works with TrinityCore.

WIde is mainly written in **Java** and makes heavily use of the **JavaFX** library.

License
-------------
WIde is licensed under the [**Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International**](http://creativecommons.org/licenses/by-nc-sa/4.0/) license.

The License prohibits Commecial use, this also includes projects where you get donations or money through sells **(== private servers)**!

See LICENSE file for full details.

Requirements
---------------
- JDK 8
- [Maven](http://maven.apache.org/)

Installation
--------------
WIde uses **Maven** to build executable .jar files.

1. Run `mvn clean package`

1. Runnable Jar file is located in target directory then.

Usage
-------------
WIde is able to run in **GUI** or **console** mode.

- GUI Mode:	`java -jar target/wide*`

- Console Mode: `java -jar target/wide* -ng`

Console Mode Examples

- Simple hello message: `java -jar target/wide* -e hello`

- Simple help message: `java -jar target/wide* -e "help printdbc"`

- Shows the content of TaxiNodes.db2 (or any other .dbc, .db2 or .adb storage):

    `java -jar target/wide* -e "printdbc TaxiNodes.db2"`

Dependencys
--------------
Dependencys are managed automatically through the maven build system.

WIde uses following dependencys:

- [Apache commons-io](http://commons.apache.org/proper/commons-io/)

- [Apache commons-cli](http://commons.apache.org/proper/commons-cli/)

- [Apache commons-math3](http://commons.apache.org/proper/commons-math/)

- [Apache log4j](http://logging.apache.org/log4j/2.x/)

- [Google guava](https://github.com/google/guava/)

- [Google gson](https://code.google.com/p/google-gson/)

- [MariaDB java-client](https://mariadb.com/kb/en/mariadb/client-libraries/mariadb-java-client/)

See file doc/Dependencys.txt for a list of all dependencys and its licences.
