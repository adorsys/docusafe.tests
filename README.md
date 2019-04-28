# Document Tests

## layer: docusafe-rest-server

This layer wraps the service of the docusafe framework (https://github.com/adorsys/docusafe) so that they can be accessed 
with REST calls. As this tiny server is spring based, it simply can be started with spring-boot:run. To do this
you need to provide a configuration file. To get you the most easy jump-in start, you should use 
the script 

```
. start.local.server.port.9991.filesystem.sh
```

As the name implies, the script starts a server running on port 9993. 
So after a successful launch, you should be able to see the swagger ui at  

```
http://localhost:9991/swagger-ui.html
```
  
## layer: docusafe-rest-client-batch
 
This layer expects a running server. So if you have started the server in the document-rest-server directory with the script
start.local.server.port.9991.filesystem.sh you simply can run the script 
     
```
. doRest.sh
```
     
in the directory docusafe-rest-client-batch. It simply makes some REST calls with curl to the running server. If you have run your sever
on another port, make sure you change the test.properties file.
    
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
