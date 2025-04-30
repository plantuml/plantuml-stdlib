#!/usr/bin/env sh
set -e

if [ -d svg ]; then
    exit 0
fi
VERSION=$(grep ^version README.md|awk '{print $2}')
wget https://github.com/twbs/icons/releases/download/v1.11.3/bootstrap-icons-$VERSION.zip
unzip bootstrap-icons-$VERSION.zip
rm -f bootstrap-icons-$VERSION.zip
mv bootstrap-icons-$VERSION/ svg
cd svg
rm -rf bootstrap-icons.svg font/

