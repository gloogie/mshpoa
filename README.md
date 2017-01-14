# mshpoa

This short porgram parses a file containing sensors data and does a simple statistics report.
The configuration file for this program is in the config subfolder of mshpoa-importer (mshpoa-importer.properties).
This program must be launched with Java 8.

## To build the project, run the following commands:

cd PROJECT_DIR

mvn clean install

## To execute the program, run the following commands:

cd PROJECT_DIR\target

java -jar mshpoa-importer-1.0-SNAPSHOT.jar <list of file paths to process in the arguments...>

### Example:
java -jar mshpoa-importer-1.0-SNAPSHOT.jar "..\src\test\resources\test_H_ko.txt" "..\src\test\resources\test_ok.txt"