# WIde - MMORPG Framework IDE #
---------------------------------

WIde helps you to edit multiple MMORPG Framework entities.
Currently WIde works with TrinityCore.

Overview
----------------


Requirements
---------------
- JDK 8
- Maven

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

- GUI Mode

		java -jar target/WIde_v*

- Console Mode

		java -jar target/WIde_v* -ng


Console Mode Examples

- Simple hello message
	
		java -jar target/WIde_v* -e hello

- Simple help message

    	java -jar target/WIde_v* -e help "printdbc"

- Shows the content of TaxiNodes.db2 (or any other .dbc, .db2 or .adb storage)

    	java -jar target/WIde_v* -e "printdbc TaxiNodes.db2"

