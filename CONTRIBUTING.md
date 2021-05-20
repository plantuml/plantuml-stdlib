# Contributing

## Creating sprites

The entire process is described [on the official website](https://plantuml.com/sprite)

## Automating conversion from ready images

### Linux

Dependencies: `imagemagic`, `plantuml`

```bash
#!/bin/bash

IMAGES_DIR=/path/to/images
RESULT_DIR=/path/to/result

TEMP_DIR_NAME=.plantuml.sprites

mkdir $TEMP_DIR_NAME
mkdir $RESULT_DIR

for i in `find -iname '*.png' -printf "%f\n" | sed 's/\.png//g'`; do
   # Convert image to include white bg and correct size
   convert $IMAGES_DIR/$i.png \
      -background "#fff" \
      -alpha remove \
      -alpha off \
      -resize 50x50 \
      $TEMP_DIR_NAME/$i.plantuml.png 

   # Create sprites
   plantuml -encodesprite 16 $TEMP_DIR_NAME/$i.plantuml.png > $RESULT_DIR/$i.plantuml
done

rm -rf $TEMP_DIR_NAME
```
