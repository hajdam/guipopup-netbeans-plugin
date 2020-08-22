GUI Popup & Inspect - NetBeans Plugin
=====================================

Registers popup menu and inspect function for all regular components in IDE.

 - Use right click or Shift-F10 to show typical popup menu on various input boxes.
 - Use Alt + Shift + Ctrl/Mac + F12 to show component inspect dialog

Homepage: https://github.com/hajdam/guipopup-netbeans-plugin  

Published as: https://plugins.netbeans.apache.org/catalogue/?id=17
Previously: http://plugins.netbeans.org/plugin/76228/  

Screenshot
----------

![Plugin Screenshot](images/screenshot.png?raw=true)

Features
--------

 - Show popup menu with clipboard cut/copy/paste actions delete, select all and some utilities actions
 - Supported components JTextComponent, JList, JTable
 - Shows inspect dialog with values readable by reflections for component currently pointed on with mouse

Compiling
---------

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle build system is used: https://gradle.org

You can either download and install gradle or use gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

Build commands: "gradle build" and "gradle nbm"

License
-------

Apache License, Version 2.0 - see LICENSE-2.0.txt
