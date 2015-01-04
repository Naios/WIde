Develop WIde OSGI bundles in Eclipse
========================================

**Installation:**

1. Add [e(fx)clipse](https://www.eclipse.org/efxclipse/install.html) and the [PDE](http://stackoverflow.com/questions/6177034/how-do-i-install-eclipse-pde) to your eclipse installation.

2. Run the command `mvn clean install pax:provision` once in your {src} dir.
	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/000.png)

3. Import all WIde bundles as maven project into eclipse. (Maybe there were some bundles added and it looks different at your working station).
	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/001.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/002.png)

4. Add a new OSGI target definition as descripted below. Also add the `{src}/runner/bundles` dir and the `eclipse installation` to your target definition.

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/003.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/004.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/005.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/006.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/007.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/008.png)

5. Import the provided launch configuration located in `{src}/doc/ide/eclipse/configuration/`

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/009.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/010.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/011.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/012.png)

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/013.png)

6. Add a new enviroment variable called `WIDE_WORKSPACE` to your system. This will tell the launch configuration where you want to run it. This might be your `{src}/runner` directory or your locale productive wide installation.

	![](https://raw.githubusercontent.com/Naios/WIde/osgi_integration/doc/ide/eclipse/resources/014.png)

**Troubleshooting:**

1. 
	- Q: **Eclipse throws error's when exporting bundles.**
	- A: You need to install **PDE** correctly (step 1).

2. 
	- Q: **OSGI complains about missing javafx classes.**
	- A: You need to install **e(fx)clipse** correctly (step 1) & import the provided launch config. Also make sure that `org.eclipse.fx.osgi` & `org.eclipse.fx.javafx` bundles are added at runtime.
