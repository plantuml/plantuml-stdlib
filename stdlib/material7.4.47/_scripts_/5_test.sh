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
file *.png | awk -F', ' '/PNG image data/ {
    split($2, dims, " x ");
    w = dims[1] + 0;
    h = dims[2] + 0;
    if (w > 100 || h > 100) print $0
}'
# These files might have rendering problems.

# Also view the results with an image viewer.
# TODO What to do with extra large renderings such as arrow-clockwise, arrow-counterclockwise, power, search
# TODO What to do withh different rendering for pairs such as compass, compass-fill

