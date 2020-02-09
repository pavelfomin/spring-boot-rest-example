#! /bin/bash
version=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
echo "Checking that release tag $version does not exist"
git tag -l | grep $version
[ $? == 1 ] || exit 1
