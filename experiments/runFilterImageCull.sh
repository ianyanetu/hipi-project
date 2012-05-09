#!/bin/bash
ant -f build.xml experiments/filterimgcull
hadoop jar experiments/filterimagecull.jar $1 $2 $3 $4 $5 $6 $7
