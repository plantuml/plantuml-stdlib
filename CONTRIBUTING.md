# Contributing

## Adding a new library

1. Fork this repository on github and then git clone it to your local machine.
1. Add a new folder containing your library files.
1. Add a file inside your library's folder called INFO that contains:
```
VERSION=[your library's version]
SOURCE=[URL to your library's separate git repo where it is maintained]
```
1. Make changes to the README.md file to add your Library and describe it.
1. Commit your work and push it up to your fork repository
1. Submit a Pull Request from your fork repository back to the main repository

## Creating sprites

The entire process is described [on the official website](https://plantuml.com/sprite)

## Automating conversion from ready images

### Linux

Dependencies: `imagemagic`, `plantuml`

```
#!/bin/bash

IMAGES_DIR=/path/to/images
RESULT_DIR=/path/to/results

TEMP_DIR_NAME=.plantuml.sprites

mkdir -p $TEMP_DIR_NAME
mkdir -p $RESULT_DIR

for i in `find $IMAGES_DIR -iname '*.png' -printf "%f\n" | sed 's/\.png//g'`; do
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
