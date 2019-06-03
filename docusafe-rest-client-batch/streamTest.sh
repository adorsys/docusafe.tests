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
	echo "$(date) $1"
	echo "$(date)  "
	} | tee -a curl.log
}

user="stream-user"
password="pazzword"
jarfile=./target/dsc
localfile=./target/largefile
remotefile=remotefolder/stream/smallfile
rm -f $localfile
i="0"
while (( i<2 ))
do
	cat $jarfile >> $localfile
	let i=i+1
done
size=$(ls -sk $localfile | cut -f  1 -d " ")

print "$(date) create user"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -cu $user $password

# write data as bytes and stream ===================
# ==========================================================
print "$(date) write file stream oriented with size $size"
java -DBASE_URL=${BASE_URL} -jar $jarfile -wb $user $password $localfile $remotefile

print "$(date) read file stream oriented with size $size"
java -DBASE_URL=${BASE_URL} -jar $jarfile -rs $user $password $remotefile $localfile.as.stream
diff $localfile $localfile.as.stream
rm $localfile.as.stream

print "$(date) read file byte oriented with size $size"
java -DBASE_URL=${BASE_URL} -jar $jarfile -rb $user $password $remotefile $localfile.as.bytes
diff $localfile $localfile.as.bytes
rm $localfile.as.bytes

print "$(date) destroy user"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -du $user $password

print "$(date) create user"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -cu $user $password

print "$(date) write file stream oriented with size $size"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -wss $user $password $localfile $remotefile

print "$(date) read file stream oriented with size $size"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -rs $user $password $remotefile $localfile.loaded
diff $localfile $localfile.loaded
rm $localfile.loaded

print "$(date) destroy user"
java  -DBASE_URL=${BASE_URL} -jar $jarfile -du $user $password

print "STREAM TESTING SUCCESSFULL"
