The original *GBToolbox* was created within my
[PhD project](http://www.imim.pl/personal/adam.morawiec/A_Morawiec_Web_Page/S/K_Glowinski/Dissertation.html)
between 2011 and 2015.
Since at that time I was a novice in Java programming and I
didn't know software development processes nor best practices,
the code is **really bad** in terms of style, duplications, etc.
Still, it allowed for getting nice scientific results by me and
my colleagues.
As a New Year's resolution and in order to make the code at least a little more maintainable,
it was transformed to a Maven project early 2019.
Thanks to that, you can now use ```mvn clean package``` to compile the sources,
and you'll have the jar in the ```target``` subdirectory. (Before the first 
compilation you need to run ```mvn validate``` to install
some dependencies in your local repository.)
Then, ```java -jar target/gbtoolbox-1.0.1-SNAPSHOT.jar```
is most likely the command that you want to run.

Also, the module for importing has been tweaked
allowing for import of grain boundary data from 
the latest version of DREAM.3D (6.5), 
so *GBToolbox* is back in the game.

#### May 2022 ####
- Reading of HDF (thus, Dream3D) files has been migrated to [pure-Java jHDF library](https://github.com/jamesmudd/jhdf).
 This removes dependencies to all native libraries. The module *Draw delected GBs* 
is migrated too.
- Fixed the button for searching for QSlim binaries.


Krzysztof Głowiński