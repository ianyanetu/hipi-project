#!/bin/bash
ant -f ../build.xml im2gray
hadoop jar im2gray.jar $1 $2 $3
