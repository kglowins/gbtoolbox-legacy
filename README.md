The original *GBToolbox* was created by Krzysztof Głowiński within his
[PhD project](http://www.imim.pl/personal/adam.morawiec/A_Morawiec_Web_Page/S/K_Glowinski/Dissertation.html)
between 2011 and 2015.
Since at that time the author was a novice in Java programming and
didn't know software development processes nor best practices,
the code is **really bad** in terms of style, duplications, etc.
Still, it allowed for getting nice scientific results by me and
my colleagues.
As a New Year's resolution and in order to make the code at least a little more maintainable,
it was transformed to a Maven project early 2019.
Thanks to that, you can now use ```mvn clean package``` to compile the sources,
and you'll have the jar in the ```target``` subdirectory.
Then, ```java -jar target/gbtoolbox-1.0.1-SNAPSHOT.jar```
is most likely the command that you want to run.

In my free time (which means rarely), I have been working
on a new reincarnation of software for grain boundary analysis,
so we may be lucky enough to see it one day :)

I also took a piece of the new mystical code
and attached it to this original one using a duct tape
allowing for import of grain boundary data from 
the latest version of DREAM.3D (6.5), 
so *GBToolbox* is back in the game.