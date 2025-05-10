#!/usr/bin/env sh
set -e

for i in *.sh; do
    echo 'Checking '$i
    checkbashisms $i
done
