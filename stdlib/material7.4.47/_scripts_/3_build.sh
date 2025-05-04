#!/usr/bin/env sh
set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo $NAMEVERSION | sed 's/[0-9.]//g')
if [ ! $NAME.puml ]; then
    exit 1
fi
if ! which brotli >/dev/null 2>&1; then
    echo 'The command brotli is not found. Please, install its package.'
    exit 1
fi

cd ../..
./gradlew run
perl -w script/generateOutput.pl $NAMEVERSION
perl -w script/generateOutput.pl $NAME
cp -f output/home.repx ../plantuml/src/main/resources/stdlib/
cp -f output/$NAME-*.repx ../plantuml/src/main/resources/stdlib/
cp -f output/$NAMEVERSION-*.repx ../plantuml/src/main/resources/stdlib/
cd ../plantuml
# If you need to upgrade plantuml, do in its directory
#     git restore
#     git clean -fd
#     git pull -r
# and run this script again.
./gradlew jar

