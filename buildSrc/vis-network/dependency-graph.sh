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
  find . -type f -name build.gradle | while read BFILE; do
    PROJECT=$(dirname "${BFILE}")
    echo "\"$PROJECT\" [style=filled,color=lightgrey,shape=box]"
    grep 'id .local.\|id .starter.' "$BFILE" | pluginNames | while read -r PLUGIN; do
      echo "\"$PROJECT\" -> $PLUGIN"
    done
  done
}

cd ../src/main/groovy || exit 20
export PLUGIN_DEPS=$(pluginDependencyEdges)
cd - || exit 21
cd ../.. || exit 22
export PLUGIN_USES=$(pluginUsageEdges)
cd - || exit 23

cat dependency-graph-template.html | envsubst > dependencies.html
