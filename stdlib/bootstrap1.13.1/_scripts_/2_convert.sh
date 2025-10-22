#!/usr/bin/env sh
set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo $NAMEVERSION | sed 's/[0-9.]//g')
if [ ! -d svg ]; then
    exit 1
fi

mkdir -p individuals
echo '@startuml' > $NAME.puml
echo >> $NAME.puml
cd svg
CONVERTED=0
for i in *.svg; do
    ICON=$(basename $i .svg)
    echo -n 'sprite bi-'$ICON' ' >> ../$NAME.puml
    cat $i \
    | sed -e 's/ xmlns="http:\/\/www\.w3\.org\/2000\/svg"//' \
    | sed -e 's/ fill=".*>/>/' \
    >> ../$NAME.puml
    echo >> ../$NAME.puml
    echo >> ../$NAME.puml
    CONVERTED=$(($CONVERTED + 1))
    if [ $((CONVERTED % 100)) -eq 0 ]; then
        printf "%05d %s\n" "$CONVERTED" "$ICON"
    fi
    cat << EOF > ../individuals/$ICON.puml
@startuml
!include <$NAMEVERSION/$NAME>
usecase a as "<\$bi-$ICON,scale=2.5>"
@enduml
EOF
done
if [ $((CONVERTED % 100)) -ne 0 ]; then
    printf "%05d %s\n" "$CONVERTED" "$ICON"
fi
echo '@enduml' >> ../$NAME.puml

