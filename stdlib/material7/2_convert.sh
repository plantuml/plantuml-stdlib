#!/usr/bin/env sh
set -e

if [ ! -d svg ]; then
    exit 1
fi

echo '@startuml' > material.puml
echo >> material.puml
cd svg
CONVERTED=0
for i in *.svg; do
    # Replace the minus in the name for an underscore.
    NAME=$(basename $(echo $i|sed -e 's/-/_/g') .svg)
    echo $NAME
    echo -n 'sprite ma_'$NAME' ' >> ../material.puml
    cat $i \
    | sed -e 's/ xmlns="http:\/\/www\.w3\.org\/2000\/svg"//' \
    | sed -e 's/ id="mdi-[^"]*"//' \
    >> ../material.puml
    echo >> ../material.puml
    echo >> ../material.puml
    CONVERTED=$(($CONVERTED + 1))
done
echo 'Converted a total of '$CONVERTED' icons'

