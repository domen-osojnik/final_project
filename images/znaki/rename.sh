#!/bin/bash
# $1 = old path
# $2 = new path + name
a=1
for i in ./$1/*.* ; do
  new=$(printf "./$1/$2%02d.jpg" "$a") 
  mv -i -- "$i" "$new"
  let a=a+1
done
