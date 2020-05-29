#!/bin/bash
for filename in ./$1/*.txt; do
    echo "$(awk '{$1 = 0; print $0}' $filename)" > $filename
done
