scaland
=======

Scaland is a self-contained Scala library for Android app development.

Features to include:

* Utility functions

For more information about Scaland, please go to
  <https://github.com/whily/scaland>

Wiki pages can be found at
  <https://wiki.github.com/whily/scaland>

Development
-----------

The following tools are needed to build Scaland from source:

* JDK version 6/7 from <http://www.java.com> if Java is not available.
  Note that JDK is preinstalled on Mac OS X and available via package manager
  on many Linux systems.
* Scala (2.11.6)
* sbt (0.12.4)

To build the library, android.jar in Android SDK (the path could be
for example `$ANDROID_HOME/platforms/android-22/android.jar`)
should be copied the directory `lib/` under the source code tree.

The project follows general sbt architecture, therefore normal sbt
commands can be used to build the library: compile, doc, test,
etc. For details, please refer
<http://scala.micronauticsresearch.com/sbt/useful-sbt-commands>.

Currently the library is not published to any public repository
yet. To use this library with your project, you need to download the
source code, and run `sbt publish-local` in your command line. Then,
include following line in your sbt configuration file.

          libraryDependencies += "net.whily" %% "scaland" % "0.0.1-SNAPSHOT"
