#!/usr/bin/env bash
set -euo pipefail

mvn -DincludeSims=false -Dinstall4j.skip -pl netbeans -am package -DskipTests

nbm_count=$(find netbeans/target -maxdepth 1 -name 'netbeans-*.nbm' | wc -l | tr -d ' ')
if [ "$nbm_count" != 1 ]; then
  printf 'expected exactly one netbeans NBM, found %s
' "$nbm_count" >&2
  exit 1
fi

test -f netbeans/target/nbm/clusters/extra/modules/org-alice-netbeans.jar
test -f netbeans/target/nbm/clusters/extra/src/aliceSource.jar
test -f netbeans/target/nbm/clusters/extra/doc/aliceDocs.zip
jar tf netbeans/target/nbm/clusters/extra/modules/org-alice-netbeans.jar | grep -F org/alice/netbeans/Alice3Library.xml
jar tf netbeans/target/nbm/clusters/extra/modules/org-alice-netbeans.jar | grep -F org/alice/netbeans/layer.xml
