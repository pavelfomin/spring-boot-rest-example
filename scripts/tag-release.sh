#! /bin/bash
version=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
echo "Creating release tag $version"
git tag $version
git push --tags
