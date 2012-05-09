#README for HIPI project

Note: This project requires that java (1.6 or later), ant (version 1.8 or later), and hadoop
(version 0.20 or later) to all be installed, and for the $JAVA_HOME, $ANT_HOME, and $HADOOP_HOME
to be set correctly.

JAVA_HOME should be set two parents higher from the output of "> which java"
For example, if "> which java" outputs "/usr/bin/java", then $JAVA_HOME should be set
to "/usr"

ANT_HOME should be set to the exact output of "> which ant"

HADOOP_HOME should be set to the parent directory of "> which hadoop"

That being said, the build.xml file found in the same directory this file was found must have
the correct values set for lines 4, 5, and 6.

In line 4, value="/usr/local/hadoop" for my $HADOOP_HOME of /usr/local/hadoop/bin
In line 5, value="1.0.1" because I was using that version of hadoop
In line 6, "-core" and "-${hadoop.version}" are in the opposite locations of each other in
my version of Hadoop than on the HIPI creators', so I had to personally modify that field

-To run the program that goes through and downloads a lists of images from a text file into a 
special compressed image (.hib file) merely type the command:

> ./downloadJob.sh

See the actual parameters I specified in both the downloadJob.sh file, and the experiments/runDownloader.sh
file

-To run the program that pulls information about a specific list of .hib files, type:

> ./dumpJob.sh

Note that this also implements the downloadJob task as a subroutine as an input to an additional MapReduce task
