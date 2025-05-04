#!/usr/bin/env sh
set -e

cd ..
if [ -d svg ] || [ -d icons ]; then
    exit 0
fi
VERSION=$(grep ^version README.md|awk '{print $2}')
BASE=https://github.com/twbs/icons
wget -q $BASE/releases/download/v$VERSION/bootstrap-icons-$VERSION.zip
unzip -q bootstrap-icons-$VERSION.zip
rm -f bootstrap-icons-$VERSION.zip
mv bootstrap-icons-$VERSION/ svg
rm -rf svg/bootstrap-icons.svg svg/font/
wget -q $BASE/archive/refs/tags/v$VERSION.zip
unzip -q v$VERSION.zip
rm -f v$VERSION.zip
mv icons-$VERSION/docs/content/icons .
rm -rf icons-$VERSION
cd _scripts_
./count_categories.py
