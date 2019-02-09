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
 Crypto sessions can be stored locally on HDD or in DB. Current example uses Redis DB as a storage. Redis DB is needed to run this example (comment out overrides for `getStorageFactory` and `getCryptoFactory` in the `Service` class in order to use local File System).

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
