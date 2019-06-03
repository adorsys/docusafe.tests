#!/bin/bash

source ./test.properties

userid="968e3220-59e8-4965-aae8-406195ba44ef"
    password="765432FEDCBA9810"
    jar=./target/dsc
    remotefile="$1"
    localfile2="target/decrypted/$1"


	echo "${userid} loads $remotefile -> $localfile2"
	echo "create directory: $(dirname $localfile2)"
	mkdir -p $(dirname $localfile2)
    java -DBASE_URL=${BASE_URL} -jar $jar -rb $userid $password  $remotefile $localfile2
