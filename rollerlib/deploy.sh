#!/bin/bash

mvn deploy:deploy-file -Durl=file:///`pwd`/repo/ -Dfile=rollerlib-1.0.jar -DgroupId=com.marco83 -DartifactId=rollerlib -Dpackaging=jar -Dversion=1.0

# -Dversion=1.0