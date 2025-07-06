#!/usr/bin/env sh
set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo "converted$NAMEVERSION" | sed 's/[0-9.]//g')
if [ ! -d _intermediate_/svg ]; then
    exit 1
fi

cd _intermediate_

mkdir -p converted
# mkdir -p individuals
# echo '@startuml' > $NAME.puml
# echo >> $NAME.puml

cd svg
CONVERTED=0
for i in *.svg; do
    ICON=$(basename $i .svg)
    ICON_UNDERSCORE=$(echo "$ICON" | tr '-' '_')
    ICON_UPPER=$(echo "$ICON_UNDERSCORE" | tr '[:lower:]' '[:upper:]')
    ICON_SKIN=$(echo "MA_$ICON_UPPER" | tr '_' ' ')

    echo -n > ../converted/$ICON.puml
    # echo '@startuml' > ../converted/$ICON.puml
    # echo >> ../converted/$ICON.puml
    echo -n 'sprite mdi-'$ICON' ' >> ../converted/$ICON.puml
    cat $i \
    | sed -e 's/ xmlns="http:\/\/www\.w3\.org\/2000\/svg"//' \
    | sed -e 's/ id=".* 24 24"></ width="48" height="48"><g transform="scale(2)"></' \
    | sed -e 's/<\/svg>/<\/g><\/svg>/' \
    >> ../converted/$ICON.puml
    echo >> ../converted/$ICON.puml

    echo >> ../converted/$ICON.puml
    echo "!define MA_$ICON_UPPER(_color)                                 SPRITE_PUT(ma_$ICON_UNDERSCORE, _color)" >> ../converted/$ICON.puml
    echo "!define MA_$ICON_UPPER(_color, _scale)                         SPRITE_PUT(ma_$ICON_UNDERSCORE, _color, _scale)" >> ../converted/$ICON.puml
    echo "!define MA_$ICON_UPPER(_color, _scale, _alias)                 SPRITE_ENT(_alias, $ICON_SKIN, ma_$ICON_UNDERSCORE, _color, _scale)" >> ../converted/$ICON.puml
    echo "!define MA_$ICON_UPPER(_color, _scale, _alias, _shape)         SPRITE_ENT(_alias, $ICON_SKIN, ma_$ICON_UNDERSCORE, _color, _scale, _shape)" >> ../converted/$ICON.puml
    echo "!define MA_$ICON_UPPER(_color, _scale, _alias, _shape, _label) SPRITE_ENT_L(_alias, $ICON_SKIN, _label, ma_$ICON_UNDERSCORE, _color, _scale, _shape)" >> ../converted/$ICON.puml
    echo "skinparam folderBackgroundColor<<$ICON_SKIN>> White" >> ../converted/$ICON.puml
    # echo '@enduml' >> ../converted/$ICON.puml

    # echo >> ../$NAME.puml
    # cat ../converted/$ICON.puml >> ../$NAME.puml
    # echo >> ../$NAME.puml

    CONVERTED=$(($CONVERTED + 1))
    if [ $((CONVERTED % 100)) -eq 0 ]; then
        printf "%05d %s\n" "$CONVERTED" "$ICON"
    fi
#     cat << EOF > ../individuals/$ICON.puml
# @startuml
# !include <$NAMEVERSION/$NAME>
# usecase a as "<\$mdi-$ICON,scale=2>"
# @enduml
# EOF
done
if [ $((CONVERTED % 100)) -ne 0 ]; then
    printf "%05d %s\n" "$CONVERTED" "$ICON"
fi
# echo '@enduml' >> ../$NAME.puml

