#!/usr/bin/env bash

set -e

cd ..
NAMEVERSION=$(pwd | sed 's!.*/!!')
NAME=$(echo "converted$NAMEVERSION" | sed 's/[0-9.]//g')
if [ ! -d _intermediate_/svg ]; then
    exit 1
fi

cd _intermediate_

mkdir -p converted

to_upper_camel_case() {
  local input="$1"
  local result=""
  
  # Split by hyphen and capitalize first letter of each part
  IFS='-' read -ra PARTS <<< "$input"
  for part in "${PARTS[@]}"; do
    # Capitalize first letter of each part
    result+="$(tr '[:lower:]' '[:upper:]' <<< ${part:0:1})${part:1}"
  done
  
  echo "$result"
}

to_lower_camel_case() {
  local input="$1"
  local result=""
  local first=true
  
  # Split by hyphen and process each part
  IFS='-' read -ra PARTS <<< "$input"
  for part in "${PARTS[@]}"; do
    # Handle the special case of replacing "left" with "Right"
    if [ "$part" = "left" ]; then
      part="Right"
    fi
    
    # Apply correct casing rules
    if [ "$first" = true ]; then
      result+="${part}"
      first=false
    else
      result+="$(tr '[:lower:]' '[:upper:]' <<< ${part:0:1})${part:1}"
    fi
  done
  
  echo "$result"
}

# generated file:
#
# sprite mdiAccountAlert <svg width="48" height="48"><g transform="scale(2)"><path d="..." /></g></svg>
#
# !if %function_exists("$MdiSprite")
# !unquoted function $MdiAccountAlertImg($color = $MDI_DEFAULT, $scale = "1")
#   !return $MdiSprite(mdiAccountAlert, $color, $scale)
# !endfunction
# 
# !unquoted function $MdiAccountAlert($alias, $label=$MDI_DEFAULT, $shape=$MDI_DEFAULT, $color=$MDI_DEFAULT, $scale=1, $stereo=$MDI_DEFAULT)
#   !return $MdiSpriteElement("mdiAccountAlert", $alias, $label, $shape, $color, $scale, $stereo)
# !endfunction
# !endif

cd svg
CONVERTED=0
for i in *.svg; do
    ICON=$(basename $i .svg)
    ICON_NAME=$(to_upper_camel_case "$ICON")
    FILE_NAME="../converted/${ICON}.puml"


    echo -n > $FILE_NAME
    echo -n 'sprite mdi'$ICON_NAME' ' >> $FILE_NAME
    cat $i \
    | sed -e 's/ xmlns="http:\/\/www\.w3\.org\/2000\/svg"//' \
    | sed -e 's/ id=".* 24 24"></ width="48" height="48"><g transform="scale(2)"></' \
    | sed -e 's/<\/svg>/<\/g><\/svg>/' \
    >> $FILE_NAME
    echo >> $FILE_NAME
    echo >> $FILE_NAME
    echo '!if %function_exists("$MdiSprite")' >> $FILE_NAME
    echo '  !unquoted function $Mdi'$ICON_NAME'Img($color = $MDI_DEFAULT, $scale = "1")' >> $FILE_NAME
    echo '    !return $MdiSprite(mdiAccountAlert, $color, $scale)' >> $FILE_NAME
    echo '  !endfunction' >> $FILE_NAME
    echo >> $FILE_NAME
    echo '  !unquoted function $Mdi'$ICON_NAME'($alias, $label=$MDI_DEFAULT, $shape=$MDI_DEFAULT, $color=$MDI_DEFAULT, $scale=1, $stereo=$MDI_DEFAULT)' >> $FILE_NAME
    echo '    !return $MdiSpriteElement("mdiAccountAlert", $alias, $label, $shape, $color, $scale, $stereo)' >> $FILE_NAME
    echo '  !endfunction' >> $FILE_NAME
    echo '!endif' >> $FILE_NAME

    CONVERTED=$(($CONVERTED + 1))
    if [ $((CONVERTED % 100)) -eq 0 ]; then
        printf "%05d %s\n" "$CONVERTED" "$ICON"
    fi
done
if [ $((CONVERTED % 100)) -ne 0 ]; then
    printf "%05d %s\n" "$CONVERTED" "$ICON"
fi

