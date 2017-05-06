#! /usr/bin/env bash

# assemble library
./gradlew :library:assembleRelease > /dev/null

# assemble fat-aar and copy dependencies
USE_METHOD_COUNT=1 ./gradlew :methodscount:assembleRelease > /dev/null

BUILD_TOOOLS_VERSION=$(ls $ANDROID_HOME/build-tools/ | sort -n -r | head -n 1)
BUILD_TOOOLS_DIR="$ANDROID_HOME/build-tools/$BUILD_TOOOLS_VERSION"
export BUILD_TOOOLS_DIR

function aar-method-counts() {
    local aar_file=$1
    local aar_basename=$(basename $aar_file '.aar')
    local aar_unzip_dest="/tmp/aar-method-counts/$aar_basename"

    rm -rf "$aar_unzip_dest"
    mkdir -p "$aar_unzip_dest"

    unzip "$aar_file" -d "$aar_unzip_dest" > /dev/null
    jar-method-counts "$aar_unzip_dest/classes.jar"
}

function aar-deps-method-counts() {
    local aar_file=$1
    local aar_basename=$(basename $aar_file '.aar')
    local aar_unzip_dest="/tmp/aar-deps-method-counts/$aar_basename"

    rm -rf "$aar_unzip_dest"
    mkdir -p "$aar_unzip_dest"

    unzip "$aar_file" -d "$aar_unzip_dest" > /dev/null

    DEPS_JARS=$(find "$aar_unzip_dest/libs" -type f -name '*.jar' | grep -v 'library-unspecified.jar')
    deps_method_counts=0

    IFS=$'\n'
    for dep_jar in $(echo "$DEPS_JARS"); do
       count=$(jar-method-counts "$dep_jar")
       deps_method_counts=$(($deps_method_counts + $count))
       # echo "$(basename $dep_jar) $deps_method_counts  $count"
    done
    unset IFS

    echo "$deps_method_counts"
}

function jar-method-counts() {
    local jar_file=$1
    local jar_basename=$(basename $jar_file '.jar')
    local jar_dir_dest="/tmp/jar-method-counts/$jar_basename"

    rm -rf "$jar_dir_dest"
    mkdir -p "$jar_dir_dest"

    "$BUILD_TOOOLS_DIR/dx"\
        --dex --no-optimize \
        --output="$jar_dir_dest/classes.dex" \
        "$jar_file"
    
    dex-method-counts "$jar_dir_dest/classes.dex" | grep 'Overall method count:' | sed -E 's/Overall method count: ([0-9]+)/\1/g'
}

function file_size() {
    echo $(
      ${DU:-du}  --apparent-size --block-size=1 "$1" 2>/dev/null ||
      ${GDU:-gdu} --apparent-size --block-size=1 "$1" 2>/dev/null ||
      ${FIND:-find} "$1" -printf "%s" 2>/dev/null ||
      ${GFIND:-gfind} "$1" -printf "%s" 2>/dev/null ||
      ${STAT:-stat} --printf="%s" "$1" 2>/dev/null ||
      ${STAT:-stat} -f%z "$1" 2>/dev/null ||
      ${WC:-wc} -c <"$1" 2>/dev/null
    ) | awk '{print $1}'
}

LIBRARY_AAR="./library/build/outputs/aar/library-release.aar"
DEPS_LIBRARY_AAR="./methodscount/build/outputs/aar/methodscount-release.aar"

#
# count library's method counts
#
lib_method_counts=$(aar-method-counts "$LIBRARY_AAR")

#
# count dependencies' method counts
#
deps_method_counts=$(aar-deps-method-counts "$DEPS_LIBRARY_AAR")

#
# library version
#
lib_version=$(cat library/library-data.properties | grep 'VERSION_NAME' | sed -E 's/VERSION_NAME=(.*)/\1/g')

#
# library (AAR) file size
#
lib_file_size=$(file_size "$LIBRARY_AAR")
lib_file_size_kb=$(( ($lib_file_size + 999) / 1000 ))

methodscount_site_url="http://www.methodscount.com/?lib=com.h6ah4i.android.widget.advrecyclerview%3Aadvrecyclerview%3A$lib_version"
methodscount_badge_url="https://img.shields.io/badge/Methods and size-core: $lib_method_counts | deps: $deps_method_counts | $lib_file_size_kb KB-e91e63.svg"
methodscount_badge_url=$(echo "$methodscount_badge_url" | sed -E 's/ /%20/g' | sed 's/|/%7C/g')

# echo "<a href=\"$methodscount_site_url\"><img src=\"$methodscount_badge_url\"/></a>"
echo "[![Method Count]($methodscount_badge_url) ]($methodscount_site_url)"

