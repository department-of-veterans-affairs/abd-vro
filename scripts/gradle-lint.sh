#!/usr/bin/env bash

# Shows differences in gradleLint results

./gradlew :generateGradleLintReport

echo "====== Differences ======="
diff build/reports/gradleLint/abd_vro.txt gradle/gradleLint.log
