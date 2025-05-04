#!/usr/bin/env sh
set -e

cd ..
VERSION=$(pwd | sed -n 's/.*\([0-9]\+\.[0-9]\+\.[0-9]\+\).*/\1/p')
BASE=https://github.com/Templarian/MaterialDesign-SVG/archive/refs/tags
if [ -d svg ]; then
    exit 0
fi

wget -q $BASE/v$VERSION.zip
unzip -q v$VERSION.zip
rm -f v$VERSION.zip
mv MaterialDesign-SVG-$VERSION/svg .
rm -rf MaterialDesign-SVG-$VERSION/

