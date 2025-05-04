#!/usr/bin/env sh
set -e

cd ..
if [ ! -d svg ]; then
    exit 1
fi

mkdir -p individuals
echo '@startuml' > bootstrap.puml
echo >> bootstrap.puml
cd svg
CONVERTED=0
for i in *.svg; do
    NAME=$(basename $i .svg)
    # TODO Remove the following if-then once this SVG parsing issue is fiexed:
    # https://github.com/plantuml/plantuml-stdlib/pull/133#issuecomment-2847700969
    if [ "$NAME" = "filetype-cs" ] \
    || [ "$NAME" = "filetype-css" ] \
    || [ "$NAME" = "filetype-m4p" ] \
    || [ "$NAME" = "filetype-pptx" ] \
    || [ "$NAME" = "filetype-py" ] \
    || [ "$NAME" = "filetype-sass" ] \
    || [ "$NAME" = "filetype-scss" ] \
    || [ "$NAME" = "filetype-" ] \
    || [ "$NAME" = "filetype-" ] \
    || [ "$NAME" = "filetype-" ] \
    || [ "$NAME" = "filetype-" ]; then
        continue;
    fi
    echo -n 'sprite bi-'$NAME' ' >> ../bootstrap.puml
    cat $i \
    | sed -e 's/ xmlns="http:\/\/www\.w3\.org\/2000\/svg"//' \
    | sed -e 's/ fill=".*>/>/' \
    >> ../bootstrap.puml
    echo >> ../bootstrap.puml
    echo >> ../bootstrap.puml
    CONVERTED=$(($CONVERTED + 1))
    if [ $((CONVERTED % 100)) -eq 0 ]; then
        printf "%05d %s\n" "$CONVERTED" "$NAME"
    fi
    cat << EOF > ../individuals/$NAME.puml
@startuml
!include <bootstrap1.11.3/bootstrap>
usecase a as "<\$bi-$NAME,scale=2.5>"
@enduml
EOF
done
if [ $((CONVERTED % 100)) -ne 0 ]; then
    printf "%05d %s\n" "$CONVERTED" "$NAME"
fi
echo '@enduml' >> ../bootstrap.puml
