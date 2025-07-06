#!/usr/bin/env sh
set -e

cd ..

echo "rsync -a _intermediate_/categorized/ ./"
rsync -a _intermediate_/categorized/ ./

echo "rm -rf _intermediate_"
rm -rf _intermediate_
