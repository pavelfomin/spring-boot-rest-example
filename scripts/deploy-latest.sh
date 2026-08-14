#! /bin/bash
version=$(git describe --abbrev=0 --tags --first-parent)
echo "Deploying version: $version"
#todo: deployment steps here