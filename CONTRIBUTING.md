# Contributing

## Adding a new library

1. Fork this repository on GitHub and then git clone it to your local machine.
1. Add a new folder containing your library files on the `stdlib` folder.
1. Create this corresponding tree structure: <br>

[![Tree structure](https://img.plantuml.biz/plantuml/svg/bL71QW8n4BqB_iDKXEh1XDwtGQ4UsqElfIpHZ1X8Dc5c5hhGZszsLr6g5o-JcSoRzrucQnPBSl01UJfvcjFhJETW6sbZMB6uu7ULDx0Wp8cz9Eg0KX8ufE2Gbdg4w6fdgYwr1AKWIzqpGsCZcjbqCYTTuu-DkIZKFV1Kg5maRjhU1C_2cRnS8tXFFWlNWvUKnQV6XgMkpYcCW2_-XfOH9C6Uq0h-jjcL0wwzcO5IZXYH__7rWgkXCyR-3IyN6tTdb5AF4NvS2CisFhURj_UjYkvySNUFfv4rDcxugZy0)](https://editor.plantuml.com/uml/bL71QW8n4BqB_iDKXEh1XDwtGQ4UsqElfIpHZ1X8Dc5c5hhGZszsLr6g5o-JcSoRzrucQnPBSl01UJfvcjFhJETW6sbZMB6uu7ULDx0Wp8cz9Eg0KX8ufE2Gbdg4w6fdgYwr1AKWIzqpGsCZcjbqCYTTuu-DkIZKFV1Kg5maRjhU1C_2cRnS8tXFFWlNWvUKnQV6XgMkpYcCW2_-XfOH9C6Uq0h-jjcL0wwzcO5IZXYH__7rWgkXCyR-3IyN6tTdb5AF4NvS2CisFhURj_UjYkvySNUFfv4rDcxugZy0)

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
1. Optionally add one or more themes for your library in the `./stdlib/[your library's name]/themes` folder
   * use the naming convention `puml-theme-[theme name].puml` to allow your users to use it via
     ```plantuml
     !theme [theme name] from <[your library's name]/themes>
     ``` 
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

## Local testing of updates to the stdlib

The correct local workflow to build a `plantuml.jar` that includes stuff from `plantuml/plantuml-stdlib`:

* in local fork of `plantuml/plantuml-stdlib`
  * `gradle run`
  * copy files from `./output/` to `./src/main/resources/stdlib` of `plantuml/plantuml` fork
    * `cp -r ./output/* ../plantuml/src/main/resources/stdlib`
* in local fork of `plantuml/plantuml`
  * `gradle clean`
  * `gradle jar`
  * produces `./build/libs/plantuml-1.2025.5beta3.jar` (or a different version)
* set up PlantUML to use the produced jar (`./build/libs/plantuml-1.2025.5beta3.jar`, or other version in that folder)

**Note: When using the IntellIJ PlantUML plugin, changing the jar setting is not enough to properly reload it, we suggest
you restart IntellIJ to make sure the plugin picks up the new jar**