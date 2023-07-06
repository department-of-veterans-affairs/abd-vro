#!/bin/bash

# Extract names of the referenced Gradle plugin file without the "-conventions" suffix to reduce clutter
pluginNames(){
  grep -o "id.*" | cut -d' ' -f 2 | tr \' \" | sed 's/-conventions//g'
}

# Print the edges representing the dependency among Gradle plugins
pluginDependencyEdges(){
  for GFILE in {st,lo,shared,repl}*.gradle; do
    PLUGIN=$(basename "$GFILE" .gradle | sed 's/-conventions//g')
    case $PLUGIN in
      local.*) echo "\"$PLUGIN\" [style=filled,color=pink]";;
      shared.*) echo "\"$PLUGIN\" [style=filled,color=lightgreen]";;
      starter.*) echo "\"$PLUGIN\" [style=filled,color=yellow]";;
      replacement*) echo "\"$PLUGIN\" [style=filled,color=\"#db9e2c\"]";;
    esac
    for PREFIX in starter. local. shared. replacement ; do
      grep "^\s* id .$PREFIX" "$GFILE" | pluginNames | while read -r DEPENDENCY; do
        echo "\"$PLUGIN\" -> $DEPENDENCY"
      done;
    done
  done
}

# Print the edges representing the use of our Gradle plugins by our Gradle projects
pluginUsageEdges(){
  find . -type f -name build.gradle | while read -r BFILE; do
    PROJECT=$(dirname "${BFILE}")
    [ "$PROJECT" == "./gradle-plugins" ] && continue
    echo "\"$PROJECT\" [style=filled,color=lightgrey,shape=box]"
    grep '^\s* id .local.\|id .starter.\|id .shared.' "$BFILE" | pluginNames | while read -r PLUGIN; do
      echo "\"$PROJECT\" -> $PLUGIN"
    done
  done
}

cd ../src/main/groovy || exit 20
PLUGIN_DEPS=$(pluginDependencyEdges)
export PLUGIN_DEPS
cd - || exit 21
cd ../.. || exit 22
PLUGIN_USES=$(pluginUsageEdges)
export PLUGIN_USES
cd - || exit 23

cmd dependency-graph-template.html | envsubst > dependencies.html
