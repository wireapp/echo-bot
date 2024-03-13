This is demo project that uses: [lithium](https://github.com/wireapp/lithium). 
It creates a Bot that will echo everything you send to it.

## Documentation for the SDK:
[Bot API](https://github.com/wireapp/lithium/wiki)

## Register as Wire Bot Developer and create your bot 
https://dashboard.integrations.zinfra.io
- Once you have created a Service copy the service token and update `docker-compose.yaml` (SERVICE_TOKEN=[your token])

## Build the project and run the bot
```
docker-compose run app
```

Bot will run as a web service and it will listen on port 8080.
```
curl -i localhost:8080/status
HTTP/1.1 200 OK
Date: Fri, 03 Mar 2023 12:37:37 GMT
Vary: Accept-Encoding
Content-Length: 0
```
## Storage
 Crypto sessions can be stored locally on HDD or in DB. Current example uses Postgres DB as the storage. 
 Postgres server is needed to run this example.
 In case you want to use your file system as storage set the `database` section in `echo.yaml` as:

 ```
 # To use file system as storage use these settings
 database:
   driverClass: fs
   url: "file:///var/echo/data"
 ```

## How to run as Bot Service
Runtime libraries can be built/copied from here:
https://github.com/wireapp/cryptobox4j

On Ubuntu copy:
 - libsodium.so
 - libcryptobox.so
 - libcryptobox-jni.so

to some dir and reference that dir in java run command using `-Djava.library.path=path/to/your/libs`

- run command:
```               
export SERVICE_TOKEN=<YOUR SERVICE TOKEN>
java -jar echo.jar server echo.yaml 
```

You can also override config values from your echo.yaml file like:
```
java -jar echo.jar -Ddw.token=<your service token> server echo.yaml 
```     

Note: *Service Token* is obtained from Wire when registered as a bot provider and created new bot service