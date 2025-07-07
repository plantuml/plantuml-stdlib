#!/usr/bin/env sh
set -e

cd ..
VERSION=$(pwd | sed -n 's/.*\([0-9]\+\.[0-9]\+\.[0-9]\+\).*/\1/p')
BASE=https://github.com/Templarian/MaterialDesign-SVG/archive/refs/tags
if [ -d svg ]; then
    exit 0
fi

echo "wget -q $BASE/v$VERSION.zip"
wget -q $BASE/v$VERSION.zip
echo "unzip -q v$VERSION.zip"
unzip -q v$VERSION.zip
echo "rm -f v$VERSION.zip"
rm -f v$VERSION.zip

mkdir -p _intermediate_

echo "mv MaterialDesign-SVG-$VERSION meta.json ./_intermediate_/meta.json"
mv MaterialDesign-SVG-$VERSION/meta.json ./_intermediate_/meta.json
echo "mv MaterialDesign-SVG-$VERSION/svg ./_intermediate_/svg"
mv MaterialDesign-SVG-$VERSION/svg ./_intermediate_/svg

rm -rf MaterialDesign-SVG-$VERSION/
