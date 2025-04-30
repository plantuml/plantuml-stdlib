#!/usr/bin/env sh
set -e

if [ ! -d svg ]; then
    exit 1
fi

if command -v flatpak >/dev/null 2>&1; then
    if flatpak list --app | grep -q 'org\.inkscape\.Inkscape'; then
        EXE='flatpak run org.inkscape.Inkscape'
    else
        EXE='inkscape'
    fi
else
    EXE='inkscape'
fi

mkdir -p png
cd svg
for i in *.svg; do
    # Here, the minus has to be replaced here by an underscore for the encoding
    # to work properly. Otherwise the name on the first line of the PUML will be
    # truncated at the minus.
    o=../png/$(basename $(echo $i|sed -e 's/-/_/g') svg)png
    if [ ! -e $o ]; then
        echo 'Rendering PNG for '$i
        $EXE $i --export-background="#fff" --export-background-opacity=1.0 -w 50 -TCo $o 2>/dev/null
    fi
done

