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

function basictest() {
	rm -f curl.log
	print "delete user, if exists, ignore error"
	checkCurl any -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/internal/user 
	checkCurl any -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/internal/user 

	print "create user peter"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user --data '{"userID":"peter", "readKeyPassword":"rkp"}' 

	print "check user peter exists"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user/peter

	print "check user francis does not exist yet"
	checkCurl 404 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user/francis

	print "peter saves README.txt"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document --data '{
	  "documentFQN": "README.txt",
	  "documentContent": "AFFE"
	}' 

	print "peter gets README.txt of home dir"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=README.txt 

	print "peter saves deep document"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document --data '{
	  "documentFQN": "deeper/and/deeper/README.txt",
	  "documentContent": "AFFE"
	}' 

	print "peter saves another deep document"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document --data '{
	  "documentFQN": "deeper/and/deeper/README2.txt",
	  "documentContent": "AFFE1010"
	}'

	print "peter gets deep document"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME.txt"

	print "create user francis"
	checkCurl 200 -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -i ${BASE_URL}/internal/user --data '{"userID":"francis", "readKeyPassword":"passWordXyZ"}' 

	print "peter puts document to francis inbox"
	checkCurl 200 -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/inbox/in --data '{
	"receivingUser": "francis",
    "sourceFQN": "deeper/and/deeper/README.txt",
    "inboxFQN": "deeper/newName.txt",
    "moveType": "KEEP_COPY"
	}'

	print "francis lists the invox"
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/inbox/list 

	print "francis checks that newName.txt exists in the inbox"
	 tail curl.log | grep -A 1 "\"files\":" | tail -n 1 | grep newName.txt
	echo "last command was $?"

	print "francis verschiebt aus inbox"
	checkCurl 200 -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/inbox/out --data '{
    "inboxFQN": "deeper/newName.txt",
    "destFQN": "deeper/and/deeper/README.txt",
    "overwriteFlag" : "TRUE"
	}'

	print "francis liest neues document"
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME.txt


	print "peter gets deep document 1"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME.txt

	print "peter gets deep document 2"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME2.txt

	print "peter deletes deep document 2"
	checkCurl 200 -f -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME2.txt

	print "peter tries to get deep document 2"
	checkCurl 404 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME2.txt

	print "peter still gets deep document 1"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME.txt

	print "peter deletes deep folder" 
	checkCurl 200 -f -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper/

	print "peter trys to get deep document 1"
	checkCurl 404 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2FREADME.txt

	if [[ filesystem -eq 1 ]]
	then
		print "check filesystem"
		find target/filesystemstorage -type f >> curl.log
	fi

	print "peter gets README.txt of home dir"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=README.txt" 

	print "peter gets README.txt as a stream of home dir"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/octet-stream' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/documentstream?documentFQN=README.txt" 

	print "peter deletes README.txt of home dir"
	checkCurl 200 -f -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=README.txt" 

	print "peter expects 404 for  README.txt of home dir"
	checkCurl 404 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=README.txt" 

	print "peter expects 404 for README.txt as a stream of home dir"
	checkCurl 404 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/octet-stream' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/documentstream?documentFQN=README.txt" 

	print "peter creates one file, just to be not empty"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i ${BASE_URL}/document --data '{
	  "documentFQN": "deeper/and/deeper/file1.txt",
	  "documentContent": "AFFE"
	}' 

	print "peter gets this document"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2Ffile1.txt" 

	print "francis list home directory" 
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i "${BASE_URL}/document/list?documentDirectoryFQN=/&listRecursiveFlag=TRUE"

	print "francis saves deep documents"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/document --data '{
	  "documentFQN": "deeper/and/deeper/file1.txt",
	  "documentContent": "AFFE"
	}' 
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/document --data '{
	  "documentFQN": "deeper/and/deeper/file2.txt",
	  "documentContent": "AFFE"
	}' 

	print "francis list home directory" 
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i "${BASE_URL}/document/list?documentDirectoryFQN=/&listRecursiveFlag=TRUE"
  
	filesfound=$(grep "\"/" curl.out | wc -l )
	print "count of found files is $filesfound"
	if (( filesfound!=3 )) 
	then	
		echo "error expected exactly 3 files"
		exit 1
	fi

	francisHome="$(pwd)/target/newFrancis"
	if [ -d $francisHome ] 
	then
		echo "error did not expect to have directory $francisHome yet"
		echo "please execute manually: rm -rf $francisHome"
		exit 1
	fi

	print "francis registeres another dfs (temporary on the filesystem $francisHome)"
	checkCurl 200 -f -X PUT -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/internal/registerdfs --data '{
	  "filesystemRootBucketName": "'${francisHome}'"
	}'

	if [ ! -d $francisHome ] 
	then
		echo "error did expect to have directory $francisHome yet"
		exit 1
	fi

	print "francis list home directory AGAIN" 
	checkCurl 200 -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i "${BASE_URL}/document/list?documentDirectoryFQN=/&listRecursiveFlag=TRUE"
  
	filesfound=$(grep "\"/" curl.out | wc -l )
	print "count of found files is $filesfound"
	if (( filesfound!=3 )) 
	then	
		echo "error expected exactly 3 files"
		exit 1
	fi

	print "peter gets his document again, just to make sure the the movement of francis did not change peter"
	checkCurl 200 -f -X GET -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: peter' -H 'password: rkp' -i "${BASE_URL}/document?documentFQN=deeper%2Fand%2Fdeeper%2Ffile1.txt" 

	print "delete user francis to remove temporary filesystem dfs"
	checkCurl -f -X DELETE -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'userid: francis' -H 'password: passWordXyZ' -i ${BASE_URL}/internal/user 
	print "delete empty directory now"
	rmdir ${francisHome}
	
	

	print "EVERYTHING WENT FINE so FAR"
}

basictest
