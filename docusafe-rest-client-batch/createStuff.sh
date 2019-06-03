#!/bin/bash

source ./test.properties

trap error ERR

function error () {
	print "  A N    E R R O R    O C C U R R E D"
	exit 1
}
function print () {
	{
	echo "$(date) ==================================================================================="
	echo "$(date) $*"
	echo "$(date)  "
	} | tee -a curl.log
}


function checkCurl() {
	status=$1
	shift
	rm -f curl.out
	rm -f curl.error
	curl "$@" > curl.out 2>curl.error
	ret=$?
	httpStatus=ERROR
	if (( ret==0 )) 
	then
		print "curl went ok $ret"
		cat curl.out >> curl.log
		httpStatus=$(cat curl.out | head -n 1 | cut -d$' ' -f2)
	fi
	if (( ret==22 )) 
	then
		print "curl went error $ret"
		cat curl.error >> curl.log
		httpStatus=$(cat curl.error)
                httpStatus=$(echo ${httpStatus##*The requested URL returned error: })
	fi
	rm -f curl.error
	if [[ httpStatus -eq "ERROR" ]]
	then
		print "exit now due to previous error with exit code $ret"
		exit $ret
	fi

	if [[ status -eq "any" ]]
	then
		print "$httpStatus is ignored"
	else
		if (( httpStatus!=status )) 
		then
			print "expected status $status but was $httpStatus of cmd $@"
			exit 1
		else
			print "as expected status was $httpStatus of cmd $@"
		fi
	fi
}

function createStuff() {
    userid="peter-neu1"
    password="rkp"
    token="{\"userID\":\"${userid}\",\"readKeyPassword\":\"${password}\"}"
    jar=./target/dsc
    localfile="target/README.txt"
    remotefile="README.txt"
    localfile2="target/README2.txt"

    echo "token is: " $token

	rm -f curl.log
	print "create user ${userid}"
	checkCurl -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user --data $token

	print "check user ${userid} exists"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user/${userid}

    echo "somestuff" > $localfile

	print "${userid} saves $localfile -> $remotefile"
    java -DBASE_URL=${BASE_URL} -jar $jar -wb $userid $password $localfile $remotefile

	print "${userid} loads $remotefile -> $localfile2"
    java -DBASE_URL=${BASE_URL} -jar $jar -rb $userid $password  $remotefile $localfile2

	print "${userid} list home directory"
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H "userid: ${userid}" -H "password: ${password}"  -i "${BASE_URL}/document/list?documentDirectoryFQN=/&listRecursiveFlag=TRUE"

	filesfound=$(grep "\"/" curl.out | wc -l )
	print "count of found files is $filesfound"
	if (( filesfound!=1 ))
	then	
		echo "error expected exactly 1 files"
		exit 1
	fi
}

createStuff
