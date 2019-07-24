# This is a Docusafe 2 Datasafe user migration tool

This tool will migrate Docusafe users and files into Datasafe format. Each migration run will be located under
`<PATH TO DATASAFE ROOT>` and will be versioned using UTC current time with precision to minute. For example, if
one runs migration at 2019-07-22 10:46:31 UTC, with `<PATH TO DATASAFE ROOT> = my_bucket/datasafe`, all migrated
users and files will be in `my_bucket` bucket, within `datasafe/201907221046` path inside it.

Usage:
### As JAR

Download JAR from [here](https://github.com/adorsys/docusafe.tests/releases/download/v1.1.1/docusafe2datasafe-migration-tool-pkg.jar)
```
java -jar docusafe2datasafe-migration-tool-pkg.jar \
    <PATH TO DOCUSAFE PROPERTIES FILE> \
    <PATH TO DATASAFE ROOT> \
    <USERS' GENERIC PASSWORD>\
    -skip <OPTIONAL, FILE WITH LIST OF USERS TO SKIP>\
    -only <OPTIONAL, FILE WITH LIST OF USERS TO MIGRATE (if they are found)>
```
**Positional** arguments:

1. `<PATH TO DOCUSAFE PROPERTIES FILE>` - Path to Docusafe YAML configuration file
1. `<PATH TO DATASAFE ROOT>` - Desired Datasafe migrated files root location
1. `<USERS' GENERIC PASSWORD>` - Docusafe/Datasafe user generic password
1. Optional value,  `-skip <OPTIONAL, FILE WITH LIST OF USERS TO SKIP>` - File that contains username list to skip 
(each username on new line)
1. Optional value,  `-only <OPTIONAL, FILE WITH LIST OF USERS TO MIGRATE (if they are found)>` - File that limits set
of users to migrate to those that are in file (each username on new line)

### From github using maven:
Requires JDK-8 and maven:
```
git clone https://github.com/adorsys/docusafe.tests --branch develop \
    && cd docusafe.tests/docusafe2datasafe-migration-tool \
    && mvn clean compile exec:java -Dexec.args="<PATH TO DOCUSAFE PROPERTIES FILE> <PATH TO DATASAFE ROOT> <USERS' GENERIC PASSWORD> <OPTIONAL, FILE WITH LIST OF USERS TO SKIP>  <OPTIONAL, FILE WITH LIST OF USERS TO MIGRATE (if they are found in bucket)>"
```
For options see above list.
