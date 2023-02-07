#! /bin/bash
version=$(git describe --abbrev=0 --tags)
echo "Deploying version: $version"
#todo: deployment steps here