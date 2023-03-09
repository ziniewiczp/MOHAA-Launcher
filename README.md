Medal of Honor: Allied Assault Launcher
======

Medal of Honor: Allied Assault Launcher is a simple application which enables user to browse and connect to the Medal of Honor: Allied Assault servers. Server list is parsed from the data provided by www.mohaaservers.tk.

![MOHAA-Launcher screenshot](https://github.com/ziniewiczp/MOHAA-Launcher/blob/master/src/main/resources/images/screenshot.png)

## Usage
Medal of Honor: Allied Assault Launcher requires [Java 8 or higher](https://www.java.com/en/download/) to run. To make the installation easier, I'd suggest installing [GIT](https://git-scm.com/downloads) as well.

Once the prerequisites are in place, open a terminal and type:
```
$ git clone https://github.com/ziniewiczp/MOHAA-Launcher.git
$ cd ./MOHAA-Launcher
$ ./gradlew build
$ java -jar ./build/libs/MOHAA-Launcher.jar
```

To create an executable:
```
$ ./gradlew launch4j
```
It's going to be placed in the `./build/launch4j` directory.

## Technologies
* Java 8
* jsoup 1.15.3
* FlatLaf 3.0
* Gradle 7
