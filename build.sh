#!/usr/bin/env bash
mvn package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/echo-bot:latest .
docker push dejankovacevic/echo-bot
