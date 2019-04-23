# Wire™
[![Wire logo](https://github.com/wireapp/wire/blob/master/assets/header-small.png?raw=true)](https://wire.com/jobs/)

## Echo Bot
[![Build Status](https://travis-ci.org/wireapp/echo-bot.svg?branch=master)](https://travis-ci.org/wireapp/echo-bot)

This is demo project that uses: [lithium](https://github.com/wireapp/lithium). It creates a Bot that will echo everything
you send it.

# Documentation for the SDK:
[Bot API](https://github.com/wireapp/lithium/wiki)

## Build the project
 Run:
 ```
 mvn package
 ```

## Storage
 Crypto sessions can be stored locally on HDD or in DB. Current example uses Redis DB as a storage. Redis server is needed to run this example.
 In case you want to use your file system as storage set the `db` section in `echo.yaml` as:

 ```
 # To use file system as storage use these settings
 db:
   driver: fs
   url: "file:///var/echo/data"
 ```

## Run Bot Service
Runtime libraries can be built/copied from here:
https://github.com/wireapp/cryptobox4j

On Ubuntu copy:
 - libsodium.so
 - libcryptobox.so
 - libcryptobox-jni.so

to some dir and reference that dir in java run command like:
```
java -Djava.library.path=$(LD_LIBRARY_PATH) -jar echo.jar server echo.yaml
```
