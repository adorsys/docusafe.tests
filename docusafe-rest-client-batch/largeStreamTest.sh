BASE_URL=http://localhost:9999
BASE_URL=http://10.211.55.4:9999

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

file=./target/dsc
localfile=./target/largefile
remotefile=remotefolder/stream/largefile

rm -f $localfile
i="0"
while (( i<150 ))
do
	cat $file >> $localfile
	let i=i+1
done
size=$(ls -sk $localfile | cut -f  2 -d " ")

print "$(date) create user"
java  -DBASE_URL=${BASE_URL} -jar $file -cu

print "$(date) write file stream oriented with size $size"
java  -DBASE_URL=${BASE_URL} -jar $file -ws $localfile $remotefile

print "$(date) read file stream oriented with size $size"
java  -DBASE_URL=${BASE_URL} -jar $file -rs $remotefile $localfile.loaded
diff $localfile $localfile.loaded
rm $localfile.loaded

# print "$(date) destroy user"
# java  -DBASE_URL=${BASE_URL} -jar $file -du

print "STREAM TESTING SUCCESSFULL"
