# WIde - MMORPG Framework IDE #
---------------------------------

WIde helps you to edit multiple MMORPG Framework entities.
Currently WIde works with TrinityCore.

Overview
----------------


Requirements
---------------


Installation
--------------
WIde uses Maven to build executable .jar files.

1. Install Maven (http://maven.apache.org/)

2. Clone WIde source (git clone)

3. Run "cd WIde && mvn clean package"

4. Jar file is located in WIde/target


Usage
-------------
WIde is able to run in GUI or console mode.

GUI Mode

	java -jar target/WIde_v*

Console Mode

	java -jar target/WIde_v* -ng

Console Mode (Execute a single script)

	java -jar target/WIde_v* -ng -e hello
