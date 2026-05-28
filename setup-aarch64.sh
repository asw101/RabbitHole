#!/bin/bash
# setup-aarch64.sh - Setup script for building and running Alice 3 (RabbitHole) on ARM64/aarch64
# This script downloads aarch64 native libraries and configures the environment.

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$SCRIPT_DIR"

echo "=== Alice 3 (RabbitHole) ARM64/aarch64 Setup ==="
echo "Repository: $REPO_ROOT"
echo "Architecture: $(uname -m)"

if [ "$(uname -m)" != "aarch64" ]; then
  echo "WARNING: This script is intended for aarch64 systems. Current arch: $(uname -m)"
  echo "Proceeding anyway..."
fi

# 1. Download JOGL 2.5.0 aarch64 natives
echo ""
echo "--- Step 1: Downloading JOGL 2.5.0 aarch64 natives ---"
JOGL_DIR="$REPO_ROOT/platform-natives/linux-aarch64/jogl/natives/linux-aarch64"
mkdir -p "$JOGL_DIR"

TMPDIR=$(mktemp -d)
for pkg in "org/jogamp/gluegen/gluegen-rt/2.5.0/gluegen-rt-2.5.0-natives-linux-aarch64.jar" \
           "org/jogamp/jogl/jogl-all/2.5.0/jogl-all-2.5.0-natives-linux-aarch64.jar"; do
  URL="https://jogamp.org/deployment/maven/$pkg"
  JAR="$TMPDIR/$(basename $pkg)"
  echo "  Downloading: $(basename $pkg)..."
  curl -sL -o "$JAR" "$URL"
  unzip -o -j "$JAR" "*.so" -d "$JOGL_DIR" 2>/dev/null
done
rm -rf "$TMPDIR"
echo "  JOGL natives installed to: $JOGL_DIR"
ls "$JOGL_DIR"/*.so 2>/dev/null | wc -l | xargs -I{} echo "  {} native libraries extracted"

# 2. Download JavaFX aarch64 natives
echo ""
echo "--- Step 2: Downloading JavaFX 23.0.2 aarch64 natives ---"
JAVAFX_DIR="$REPO_ROOT/platform-natives/linux-aarch64/javafx"
mkdir -p "$JAVAFX_DIR"

FXVER="23.0.2"
FXTMP=$(mktemp -d)
for mod in base graphics media; do
  URL="https://repo1.maven.org/maven2/org/openjfx/javafx-${mod}/${FXVER}/javafx-${mod}-${FXVER}-linux-aarch64.jar"
  JAR="$FXTMP/javafx-${mod}-${FXVER}-linux-aarch64.jar"
  echo "  Downloading: javafx-${mod}-${FXVER}-linux-aarch64.jar..."
  curl -sL -o "$JAR" "$URL"
  unzip -o -j "$JAR" "*.so" -d "$JAVAFX_DIR" 2>/dev/null
done

# Also install into Maven local repo to replace x86_64 classifier jars
echo ""
echo "  Installing JavaFX aarch64 jars into Maven local repo (replacing linux classifier)..."
M2_FX="$HOME/.m2/repository/org/openjfx"
for mod in base graphics media; do
  JAR="$FXTMP/javafx-${mod}-${FXVER}-linux-aarch64.jar"
  TARGET="$M2_FX/javafx-${mod}/21.0.7/javafx-${mod}-21.0.7-linux.jar"
  if [ -f "$TARGET" ]; then
    cp "$TARGET" "${TARGET}.x86_64.bak"
    cp "$JAR" "$TARGET"
    echo "    Replaced: javafx-${mod}-21.0.7-linux.jar with aarch64 content"
  else
    echo "    SKIP: $TARGET not found (run mvn compile first, then re-run this script)"
  fi
done
rm -rf "$FXTMP"
echo "  JavaFX natives installed to: $JAVAFX_DIR"

# 3. Clear JavaFX native cache
echo ""
echo "--- Step 3: Clearing JavaFX native cache ---"
rm -rf "$HOME/.openjfx/cache/"
echo "  Cache cleared."

# 4. Print build and run instructions
echo ""
echo "=== Setup Complete ==="
echo ""
echo "To BUILD:"
echo "  cd $REPO_ROOT"
echo "  mvn compile install -DskipTests -Denforcer.skip -Djavafx.platform=linux"
echo ""
echo "To RUN the IDE:"
echo "  cd $REPO_ROOT/alice-ide"
echo "  JOGL_NATIVES='$JOGL_DIR'"
echo "  JAVAFX_NATIVES='$JAVAFX_DIR'"
echo '  export MAVEN_OPTS="-Djava.library.path=$JAVAFX_NATIVES:$JOGL_NATIVES"'
echo "  mvn exec:java -Dalice-ide -Denforcer.skip -Djavafx.platform=linux"
echo ""
echo "NOTE: The Sims content (nebulous) native library does not have an aarch64 build."
echo "The IDE will work but Sims-based 3D models will not be available."
