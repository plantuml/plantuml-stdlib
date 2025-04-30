#!/usr/bin/env sh
set -e

if [ -d svg ]; then
    exit 0
fi
VERSION=$(grep ^version README.md|awk '{print $2}')
wget https://github.com/Templarian/MaterialDesign-SVG/archive/refs/tags/v$VERSION.zip
unzip v$VERSION.zip
rm -f v$VERSION.zip
mv MaterialDesign-SVG-$VERSION/svg .
rm -rf MaterialDesign-SVG-$VERSION/

