#!/usr/bin/env sh
set -e

if [ ! -d png ]; then
    exit 1
fi

cd png
for i in *.png; do
    o=../$(basename $i png)puml
    if [ ! -e $o ]; then
        echo 'Encoding sprite for '$i
        plantuml -encodesprite 16 $i > $o
        cat << EOF >> $o
!define MA_ACCOUNT_MINUS(_color)                                 SPRITE_PUT(                                    ma_account_minus, _color)
!define MA_ACCOUNT_MINUS(_color, _scale)                         SPRITE_PUT(                                    ma_account_minus, _color, _scale)
!define MA_ACCOUNT_MINUS(_color, _scale, _alias)                 SPRITE_ENT(  _alias, MA ACCOUNT_MINUS,         ma_account_minus, _color, _scale)
!define MA_ACCOUNT_MINUS(_color, _scale, _alias, _shape)         SPRITE_ENT(  _alias, MA ACCOUNT_MINUS,         ma_account_minus, _color, _scale, _shape)
!define MA_ACCOUNT_MINUS(_color, _scale, _alias, _shape, _label) SPRITE_ENT_L(_alias, MA ACCOUNT_MINUS, _label, ma_account_minus, _color, _scale, _shape)
skinparam folderBackgroundColor<<MA ACCOUNT_MINUS>> White
EOF
    fi
done

