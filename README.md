# Typing Practice and Test

![Logo](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/TypingPracticeAndTest/ui/res/img/icon.png)

#### An app for practising, honing, and testing your typing skills.

<br>

**Note:**

* Download the [\*.deb file](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/linux/deb/typingpracticeandtest_1.0-1_amd64.deb?raw=true) to run on Linux. As I am not experienced in packaging java applications for native systems, the \*.deb file has not been tested, and may be subject to errors.

* Download the [\*.jar file](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/jar/TypingPracticeAndTest.jar?raw=true) to run on any operating System (Windows, Linux, MacOS, etc.) having one of the JREs (e.g. [OpenJDK](https://jdk.java.net/17/)) installed. The \*.jar file is guaranteed to work using a Java Runtime Environment (JRE); also referred to as Java Virtual Machine (JVM).

* To compile and build native executables, make sure to download the relevant [OpenJFX Version 17.0.1 SDK/JMod files](https://gluonhq.com/products/javafx/).

* If you're using the `package` shell script for the `jpackage` command (in order to generate the *app-image*), make sure to make the following change, following the `package` script, but preceding the `runpkg` script:

    * At the following relative path, `package/appimage/TypingPracticeAndTest/lib/app`, in the following file, `TypingPracticeAndTest.cfg`, replace `app.mainclass= TypingPracticeAndTest/App` with `app.mainclass=TypingPracticeAndTest/App`.

    * The additional space has been removed as it causes an error during run-time. Also, the generation of the space itself could be an OpenJDK bug.

* If you're compiling for packaging, that is, using the `jpackage` command, following the `javac` and `jar` commands, then make the following changes to the `Model.java` file:

   * *Un-comment* the code at line number 18 :
      `private static String APP_DIR_PATH = System.getProperty("filepath");` 

   * *Comment* out the code at line number 20 :
      `private static String APP_DIR_PATH = "TypingPracticeAndTest";` 
      
   * If you're compiling normally (for JRE), using `javac` and `java`, then reverse the above changes.

<br>

![Screenshot 1](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/0screenshots/1.png)

![Screenshot 2](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/0screenshots/2.png)

![Screenshot 2](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/0screenshots/3.png)

![Screenshot 2](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/0screenshots/4.png)

![Screenshot 2](https://github.com/melwyncarlo/Typing_Practice_and_Test/blob/main/package/0screenshots/5.png)


