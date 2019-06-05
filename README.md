# Document Tests

## layer: docusafe-rest-server

This layer wraps the service of the docusafe framework (https://github.com/adorsys/docusafe) so that they can be accessed 
with REST calls. As this tiny server is spring based, it simply can be started with spring-boot:run. To do this
you need to provide a configuration file. To get you the most easy jump-in start, you should use 
the script 

```
. start.local.server.port.9991.filesystem.sh
```

As the name implies, the script starts a server running on port 9991. 
So after a successful launch, you should be able to see the swagger ui at  

```
http://localhost:9991/swagger-ui.html
```
  
The server can be started with ceph,minio,amazon or filesystem. for that, the files in 
```
docusafe-rest-server/src/main/resources/
```
have to get the missing credentials.

## layer: docusafe-rest-client-batch
 
This layer expects a running server. So if you have started the server in the document-rest-server directory with the script
start.local.server.port.9991.filesystem.sh you simply can run the script 
     
```
./dorest.sh
```
     
in the directory docusafe-rest-client-batch. It simply makes some REST calls with curl to the running server. If you have run your sever
on another port, make sure you change the test.properties file.

Further tests are
```
./streamTest.sh
./largeSstreamTest.sh
```
    
## layer: docusafe-rest-client-gui 

This is an agular test frontend. It has  to be build up one time only with
    
```
npm install
```    

Then it can be started with
    
```
ng serve
```
    
After starting it, you can retrieve the website
     
```
http://localhost:4200
```
    
to start the tests. The default destination is going to your local port on 9993. 

* [Internals](.docs/Internals.md)

## layer: docusafe-batch-client

This is a tiny analysys tool. It extracts all data of a user and stores the decrypted content in one zip file. Please note, currently only the data are stored. The keystores are not extracted.

The tool can be started with 
```
java -jar target/java-batch-client.jar
``` 

Unfortunately the jar depends on the target/libs directory. This link is given in the manifest. The reason why the maven-assembly-plugin and the maven-one-jar plugin did not work are bouncy-castle libs, which are signed and thus require the libs directory.

A valid call for amazon would be:

```
java -DSC-NO-BUCKETPATH-ENCRYPTION -jar target/docusafe-batch-client.jar -s3url=https://s3.amazonaws.com -s3accesskey= *-s3secretkey=* -s3region=eu-central-1 -s3rootbucket=adorsys-docusafe/peter-local -user=peter-neu1 -password=rkp
```

A valid call for docusafe working with the filesystem would look like

```
java -DSC-NO-BUCKETPATH-ENCRYPTION -jar <path-to-jar>/target/docusafe-batch-client.jar -filesystem=target/test-filesystem  -user=peter-neu2 -password=rkp
```

Notice that the BucketPath has to be the same, as the servers bucket path. So if the server used a relative path, the client has to use a relative path too.
