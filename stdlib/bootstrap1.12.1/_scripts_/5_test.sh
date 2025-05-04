#!/usr/bin/env sh
set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo $NAMEVERSION | sed 's/[0-9.]//g')

if [ ! $NAME.puml ]; then
    exit 1
fi

cd individuals
JAR=$(ls ../../../../plantuml/build/libs/*.jar | tail -1)
# TODO This option is useful https://github.com/plantuml/plantuml/issues/2176
java -jar $JAR -r .
file *png | grep ' x 703'
# These files probably need to be excluded in 2_convert.sh

# Also view the results with an image viewer.
# TODO What to do with extra large renderings such as arrow-clockwise, arrow-counterclockwise, power, search
# TODO What to do withh different rendering for pairs such as compass, compass-fill

