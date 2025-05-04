#!/usr/bin/env sh
set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo $NAMEVERSION | sed 's/[0-9.]//g')
if [ ! $NAME.puml ]; then
    exit 1
fi

cd _examples_
JAR=$(ls ../../../../plantuml/build/libs/*.jar | tail -1)
java -jar $JAR example.puml
java -jar $JAR listsprite.puml

