# Contributing

## Adding a new library

1. Fork this repository on GitHub and then git clone it to your local machine.
1. Add a new folder containing your library files on the `stdlib` folder.
1. Create this tree structure:
[![Tree structure](https://img.plantuml.biz/plantuml/svg/RK_B2i8m4BmN-W-n53pP_a0D5UpHYrUHaZOH0dcnkm4BVhonhL3Gqp2pmyniYoI07jeepBDT2SUY3aOuYjOSaAJHGxr2C_1cyX5O8Y1WsYDpmYfUv5a9TQ-UmeQKqd-CAwb2YlavQsuq4WRGj7NW23hGOcXy8EsTC6nHRtXd4HKZpqPGWjGh1fc0RGVnEIUrNxlJ-T9LLdw5lxlNwbOvER__1W00)](https://editor.plantuml.com/uml/RK_B2i8m4BmN-W-n53pP_a0D5UpHYrUHaZOH0dcnkm4BVhonhL3Gqp2pmyniYoI07jeepBDT2SUY3aOuYjOSaAJHGxr2C_1cyX5O8Y1WsYDpmYfUv5a9TQ-UmeQKqd-CAwb2YlavQsuq4WRGj7NW23hGOcXy8EsTC6nHRtXd4HKZpqPGWjGh1fc0RGVnEIUrNxlJ-T9LLdw5lxlNwbOvER__1W00)
1. Add a file inside your library's folder called `README.md` that contains an YAML header:
```md
---
name: <your library's name>
display_name: 
description: 
author: 
version: <your library's version>
release: 
license: 
source: <[URL to your library's separate git repo where it is maintained]>
origin: <URL to the icon/stdlib origin website>
---

Information about the `<your library's name>` Standard Library.
```
1. Make changes to the root [`README.md`](./README.md) file to add your Library, describe it and add example.
1. Commit your work and push it up to your fork repository
1. Submit a Pull Request from your fork repository back to the main repository

## Creating sprites

The entire process is described [on the official website](https://plantuml.com/sprite)

## Automating conversion from ready images

### Linux

Dependencies: `imagemagic`, `plantuml`

```sh
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
