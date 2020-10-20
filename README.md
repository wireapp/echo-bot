# Wire™
[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

## Echo Bot
[![Build Status](https://travis-ci.org/wireapp/echo-bot.svg?branch=master)](https://travis-ci.org/wireapp/echo-bot)

This is demo project that uses: [lithium](https://github.com/wireapp/lithium). It creates a Bot that will echo everything
you send it.

## Documentation for the SDK:
[Bot API](https://github.com/wireapp/lithium/wiki)

## Build the project
 Run:
 ```
 mvn package
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

Note: *Service Token* is obtained from Wire when registered as bot provider and created new bot service