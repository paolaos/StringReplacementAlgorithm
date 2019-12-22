#Coding Exercise for Accenture

###By: Paola Ortega Saborío

**Objective:** write a class using Groovy to search and replace within all files starting in a given directory.

**Estimated development time:** 13 hours

**Previous experience with Groovy:** none

####About this exercise
The solution to this problem was inspired by the "grep" terminal command in Unix systems. After taking a look at the source code in C, I realised the developers for this command used the Boyer-Moore algorithm to search for the strings. Based on this, I created a program using the same algorithm. The learning curve for the language was not steep at all, since it is very similar to Java. I must admit though that file managing and attribute instantiating was way easier and quicker using Groovy. I managed to fulfill all the requirements from the instructions that were sent to my mail. Additionally, I also added the option to run the programme synchronously or asynchronously. Of course, it is generally more efficient by running it on the latter. I also added three directories with tests for ease of testing, but feel free to try it with any other files.

####Instructions:
There are two ways of running the project: with or without logs. In any of the two cases,  open the terminal and place it at the /src directory. 

In order to run it without logs, run the following command:
<pre><code>groovy Main.groovy [target directory] "[target pattern]" "[replacement pattern]" [isAsynch?]</code></pre>

There are several things to note. 
- The _target directory_ should start with a / character, and should be a relative path starting from the src directory. 
- The _target pattern_ and the _replacement pattern_ should be placed between quotation marks. All characters are accepted in between.
- The _isAsync_ input should be 0 for running the program synchronously and 1 for running the program asynchronously.

In order to run it with logs, run the following command:
<pre><code>groovy Main.groovy [target directory] "[target pattern]" "[replacement pattern]" [isAsynch?] [logs directory]</code></pre>

The parameters are the same as the first command, except for _Logs directory_ . It should be the path where the user desires to store the logs. Just like _target directory_, this should start with a / character.

The following is an example of how the programme could be run:
<pre><code>/simple_test " the " " ze " 0 /logs</code></pre>

####Things to improve:
The programme is fine as it is. However, it would never do harm if it accepted regular expressions and have a more efficient way of replacing text once its found.
